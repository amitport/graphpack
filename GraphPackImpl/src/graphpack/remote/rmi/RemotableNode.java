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
package graphpack.remote.rmi;

import graphpack.ClientLocation;
import graphpack.Edge;
import graphpack.Edge.Payload;
import graphpack.NodeLocation;
import graphpack.SerializableEdge;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.remote.Gateway;
import graphpack.taskprocessing.ITask;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;


public class RemotableNode implements IRemotableNode {
	Gateway.Node gateway;
	public RemotableNode(Gateway.Node gateway){
		this.gateway = gateway;
		try {
			UnicastRemoteObject.exportObject(this, 0);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void unexport() {
		try {
			UnicastRemoteObject.unexportObject(this, false);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<SerializableEdge> getOutgoingEdges(ClientLocation sender) throws RemoteException {
		List<SerializableEdge> $ = new ArrayList<SerializableEdge>();
		for (Edge e : gateway.getOutgoingEdges(sender)){
			$.add(e.serializableEdge);
		}
		return $;
	}

	@Override
	public void addOutgoingEdge(ClientLocation sender,
			NodeLocation target, Payload payload) throws RemoteException {
		gateway.addOutgoingEdge(sender,target,payload);
	}

	@Override
	public ResultSet traverse(ClientLocation sender, Matcher matcher) {
		return gateway.traverse(sender,matcher);
	}

	@Override
	public void addTask(ClientLocation sender, String taskName,
			Class<? extends ITask> task) throws RemoteException {
		gateway.addTask(sender, taskName, task);
	}

	@Override
	public void addScheduledTask(ClientLocation sender, String taskName,
			Class<? extends ITask> task, String cronExpression)
					throws RemoteException {
		gateway.addScheduledTask(sender, taskName, task, cronExpression);
	}
	
	@Override
	public void callTask(ClientLocation sender, String taskName,
			Object... params) throws RemoteException {
		gateway.callTask(sender, taskName, params);
	}

}
