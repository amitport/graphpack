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


import java.io.Serializable;


/**
 * Uniquely identifies an {@link INode}
 * @author amitport
 */
public final class NodeLocation implements Serializable {
	private static final long serialVersionUID = 5874957587568274428L;
	final String serviceName; //should be unique across the network
	final String clientName; //should unique for every service
	final String nodeName; //should unique for every client of every service	
	public NodeLocation(String serviceName, String clientName, String nodeName) {		
		this.serviceName = serviceName;
		this.clientName = clientName;
		this.nodeName = nodeName;
	}
	
	public String getServiceName() {return serviceName;}
	public String getClientName() {return clientName;}
	public String getNodeName() {return nodeName;}

	@Override
	public String toString() {
		return serviceName+"."+clientName+"."+nodeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientName == null) ? 0 : clientName.hashCode());
		result = prime * result
				+ ((nodeName == null) ? 0 : nodeName.hashCode());
		result = prime * result
				+ ((serviceName == null) ? 0 : serviceName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeLocation other = (NodeLocation) obj;
		if (clientName == null) {
			if (other.clientName != null)
				return false;
		} else if (!clientName.equals(other.clientName))
			return false;
		if (nodeName == null) {
			if (other.nodeName != null)
				return false;
		} else if (!nodeName.equals(other.nodeName))
			return false;
		if (serviceName == null) {
			if (other.serviceName != null)
				return false;
		} else if (!serviceName.equals(other.serviceName))
			return false;
		return true;
	};
}
