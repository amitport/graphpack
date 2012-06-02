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

import graphpack.Edge.Payload;

/**
 * a representation of an edge that can be directly serialized
 * (it doesn't contain external references)
 * @author amitport
 */
public class SerializableEdge implements Serializable {
	private static final long serialVersionUID = -876475734147966205L;
	
	final public NodeLocation source;
	final public NodeLocation target;
	final public Payload payload;
	final public long num;
	
	public SerializableEdge(NodeLocation source, NodeLocation target, long num, Payload payload) {
		this.source = source;
		this.target = target;
		this.num = num;
		this.payload = payload;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (num ^ (num >>> 32));
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
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
		SerializableEdge other = (SerializableEdge) obj;
		if (num != other.num)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return ((source == null)?"null":source.nodeName) +
				"-"+num+"->" +
				((target == null)?"null":target);
	}
}
