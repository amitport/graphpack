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

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * edge with an interactable reference to its target node
 * @author amitport
 */
final public class Edge {	
	final public INode target;
	final public SerializableEdge serializableEdge;
	
	public Edge(NodeLocation source, INode target, long num, Payload payload) {
		this.target = target;
		this.serializableEdge = new SerializableEdge(source, target.location(), num, payload);
	}
	
	@Override
	public int hashCode() {
		return serializableEdge.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		return (serializableEdge.equals(other.serializableEdge));
	}
	
	@Override
	public String toString() {
		return serializableEdge.toString();
	}
	
	/*** static ***/
	
	/**
	 * contains data that is associated with an edge
	 * @author amitport
	 */
	public static interface Payload extends Serializable{
		public final static EmptyPayload EMPTY = new EmptyPayload();
		
		//complications below are needed in order to pass the static final EMPTY payload remotely
		final static SerializedEmptyPayload SERIALIZED_EMPTY = new SerializedEmptyPayload();
		/**
		 * a payload that without any fields
		 * @author amitport
		 */
		final public static class EmptyPayload implements Payload {		
			private EmptyPayload(){}
			public Object writeReplace() throws ObjectStreamException 
			{return SERIALIZED_EMPTY;}
		
			@Override
			public String toString() {return "<emptyPayload>";}
		}
		/**
		 * a serialized version of {@link EmptyPayload}
		 * @author amitport
		 */
		final static class SerializedEmptyPayload implements Serializable {
			private static final long serialVersionUID = -6611689051234748601L;

			private SerializedEmptyPayload(){}
			
			Object readResolve() throws ObjectStreamException
				{return EMPTY;}
		}
		//ENDOF complications
	}
}
