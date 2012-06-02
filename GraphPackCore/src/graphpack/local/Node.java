/*******************************************************************************
 * Copyright 2012 Amit Portnoy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package graphpack.local;

import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.INode;
import graphpack.NodeLocation;
import graphpack.extensions.Extensions;
import graphpack.local.persistence.IEdgeStore;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.parsing.java.IParser;
import graphpack.taskprocessing.ITask;
import graphpack.taskprocessing.ITaskManager;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import javax.annotation.Nullable;

/**
 * a local node <br/>
 * delegates persistence to injected {@link graphpack.local.persistence.IEdgeStore} <br/>
 * delegates parsing to injected {@link graphpack.parsing.java.IParser} <br/>
 * delegates task management to injected {@link graphpack.taskprocessing.ITaskManager}
 * @author amitport
 */
public class Node implements INode {
	Extensions extensions;
	public String serviceName;
	public String clientName;
	String nodeName;
	IEdgeStore edgeStore;
	IParser parser;
	NodeLocation location;
	ITaskManager taskManager;
	@Inject
	public Node(@Nullable Extensions extensions, @Assisted("serviceName") String serviceName, @Assisted("clientName") String clientName, @Assisted("nodeName") String nodeName, IEdgeStore edgeStore, IParser parser,ITaskManager taskManager){
		this.extensions = extensions;
		this.serviceName = serviceName;
		this.clientName = clientName;
		this.nodeName = nodeName;
		this.edgeStore = edgeStore;
		this.parser = parser;
		this.location = new NodeLocation(serviceName,clientName,nodeName);
		this.taskManager = taskManager;
	}
	
	@Override
	public void addTask(String taskName, Class<? extends ITask> task){
		taskManager.addTask(clientName, nodeName, taskName, this, extensions, task);
	}
	
	@Override
	public void addScheduledTask(String taskName, Class<? extends ITask> task, String cronExpression){
		taskManager.addScheduledTask(clientName, nodeName,
				taskName, this, extensions, task, cronExpression);	
	}
	
	@Override
	public void callTask(String taskName, Object... params){
		taskManager.callTask(clientName, nodeName, taskName, params);
	}
	
	@Override
	public NodeLocation location(){return location;}
	
	@Override
	public String toString() {
		return serviceName+"."+clientName+"."+nodeName;
	}

	@Override
	public List<Edge> getOutgoingEdges() {
		return edgeStore.getOutgoingEdges();
	}

	@Override
	public void addOutgoingEdge(NodeLocation target, Payload payload) {
		edgeStore.addOutgoingEdge(serviceName,clientName,nodeName,target,payload);
	}

	@Override
	public ResultSet traverse(Matcher matcher) {
		if (matcher == null) return null;
		ResultSet $ = new ResultSet();
		for (Edge e : getOutgoingEdges()) {
			//derive a new matcher
			Matcher newMatcher = matcher.cont(e.serializableEdge);
			if (newMatcher.canTake()) {
				//take valid results
				ResultSet rs = newMatcher.take();
				$ = ResultSet.union($,rs);
			}
			if (newMatcher.canCont()) {
				//continue the traverse if possible
				$ = ResultSet.union($,e.target.traverse(newMatcher));
			}
		}
		return $;
	}

	@Override
	public ResultSet traverse(String path, Object... params) {
		return traverse(parser.parsePath(path, params));
	}
}
