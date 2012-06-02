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
package graphpack;


import graphpack.Edge.Payload;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.taskprocessing.ITask;

//import java.io.Serializable;
import java.util.List;

/**
 * a generic reference to a node - it can be either local or remote
 * @author amitport
 */
public interface INode {

	/**
	 * @return a list of all outgoing edges
	 */
	List<Edge> getOutgoingEdges();

	/**
	 * adds an outgoing edges
	 * @param target the location of the edge's target
	 * @param payload the edge's payload
	 */
	void addOutgoingEdge(NodeLocation target, Payload payload);
	
	/**
	 * parses PackCypher path expression with parameters
	 * and delegates to {@link #traverse(Matcher)} 
	 * @param path PackCypher expression
	 * @param params parameters for the PackCypher expression
	 * @return the results of the traverse
	 */
	ResultSet traverse(String path, Object... params);

	/**
	 * traverses the graph 
	 * @param matcher specifies the traverse
	 * @return the results of the traverse
	 */
	ResultSet traverse(Matcher matcher);
	
	/**
	 * @return the location of this node
	 */
	NodeLocation location();
	
	/* task management */
	
	/**
	 * add a new task
	 * @param taskName the name of the new task
	 * @param task reference to the code that describes the task
	 */
	void addTask(String taskName, Class<? extends ITask> task);
	
	/**
	 * add a new scheduled tasks
	 * @param taskName the name of the new task
	 * @param task reference to the code that describes the task
	 * @param cronExpression an expression that described the task schedule, see <a href="http://en.wikipedia.org/wiki/Cron#CRON_expression">http://en.wikipedia.org/wiki/Cron#CRON_expression</a>
	 */
	void addScheduledTask(String taskName, Class<? extends ITask> task, String cronExpression);
	
	/**
	 * call a task
	 * @param taskName the task to call
	 * @param params any parameters that should be passed to the task
	 */
	void callTask(String taskName, Object... params);
}
