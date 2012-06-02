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
package graphpack.taskprocessing;

import graphpack.INode;
import graphpack.extensions.Extensions;

/**
 * an interface for the principle class that is responsible for task management
 * @author amitport
 */
public interface ITaskManager {
	/**
	 * install a new task
	 * @param clientName the name of the client that own the task
	 * @param nodeName the name of the associated node
	 * @param taskName the name of the task
	 * @param node the actual node
	 * @param extensions possible extensions 
	 * @param task the task
	 */
	void addTask(String clientName, String nodeName, String taskName, INode node, Extensions extensions, Class<? extends ITask> task);
	
	/**
	 * add a scheduled task
	 * @param clientName the name of the client that own the task
	 * @param nodeName the name of the associated node
	 * @param taskName the name of the task
	 * @param node the actual node
	 * @param extensions possible extensions 
	 * @param task the task
	 * @param cronExpression an expression that described the task schedule, see <a href="http://en.wikipedia.org/wiki/Cron#CRON_expression">http://en.wikipedia.org/wiki/Cron#CRON_expression</a>
	 */
	void addScheduledTask(String clientName, String nodeName, String taskName, INode node, Extensions extensions, Class<? extends ITask> task, String cronExpression);
	
	/**
	 * execute an installed task
	 * @param clientName the name of the client that own the task
	 * @param nodeName the name of the associated node
	 * @param taskName the name of the task
	 * @param params parameters for the called task
	 */
	void callTask(String clientName, String nodeName, String taskName, Object... params);
}
