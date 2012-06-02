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
import graphpack.Edge.Payload;
import graphpack.NodeLocation;
import graphpack.SerializableEdge;
import graphpack.matching.Matcher;
import graphpack.matching.ResultSet;
import graphpack.taskprocessing.ITask;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRemotableNode extends Remote {
	List<SerializableEdge> getOutgoingEdges(ClientLocation sender) throws RemoteException;

	void addOutgoingEdge(ClientLocation sender,
			NodeLocation target, Payload payload) throws RemoteException;

	ResultSet traverse(ClientLocation sender, Matcher matcher) throws RemoteException;

	void addTask(ClientLocation sender, String taskName,Class<? extends ITask> task) throws RemoteException;
	void addScheduledTask(ClientLocation sender, String taskName,
			Class<? extends ITask> task, String cronExpression) throws RemoteException;;
	
	void callTask(ClientLocation sender, String taskName, Object... params) throws RemoteException;


}
