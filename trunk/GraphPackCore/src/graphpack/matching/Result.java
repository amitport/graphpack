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
package graphpack.matching;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * a single matched result
 * @author amitport
 */
public class Result implements Serializable {
	private static final long serialVersionUID = 3087489222402385206L;

	final public Map<String,Object> map = new HashMap<String,Object>();;
	
	public Result(){}

	/**
	 * copy constructor
	 * @param res a result to copy
	 */
	public Result(Result res) {
		map.putAll(res.map);
	}

	/**
	 * return a matched value
	 * @param key the name of the matched value
	 * @return the value of the matched value
	 */
	public Object get(String key){
		return map.get(key);
	}
	
	/**
	 * match a value
	 * @param key the name of the matched value
	 * @param value the value of the matched value
	 */
	public void put(String key, Object value){
		map.put(key, value);
	}
	
	/**
	 * remove a matched value
	 * @param key the name of the value to be removed
	 */
	public void remove(String key) {
		map.remove(key);
	}
	
	/**
	 * get an unmodifiable copy of the inner map used by this result
	 * @return unmodifiable map
	 */
	public Map<String,Object> getMap(){
		return Collections.unmodifiableMap(map);
	}
	
	@Override
	public String toString(){
		return map.toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
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
		Result other = (Result) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map)){
			return false;
		}
		return true;
	}
	
	/*** static ***/
	
	/**
	 * Merges two results
	 * @return a new merged result or {@code null} if the results have conflicts
	 */
	public static Result merge(Result res1, Result res2){
		Result $ = new Result(res1);
		for (Entry<String,Object> res2Entry : res2.map.entrySet()){
			String res2Key = res2Entry.getKey();
			Object res2Val = res2Entry.getValue();
			if (!res1.map.containsKey(res2Key)){ 	
				//$ don't have this key yet -> just add it
				$.map.put(res2Key, res2Val);     			//->just add it
			}else {
				//$ already have this key
				if (res2Val != UNASSIGNED_VALUE){
					//and res2 have it assigned
					Object res1Val = res1.map.get(res2Key);
					if (res1Val != UNASSIGNED_VALUE){ 			
						//and $ also have it assigned
						if (!res1Val.equals(res2Val)){
							//but not with the same value !! -> fail
							return null;
						} //else both res have the same value -> do nothing
					} else { 
						//res2Key was not assigned in $ -> assign it now
						$.map.put(res2Key, res2Val);
					}
				} //res2Val is unassigned -> keep whatever we have from res1
			}
		}
		return $;
	}
	
	/* unassigned values (complications are because of serializations) */
	public final static Unassigned UNASSIGNED_VALUE = new Unassigned();
	final static SerializedUnassigned SERIALIZED_UNASSIGNED_VALUE = new SerializedUnassigned();
	
	/**
	 * an unassigned value (the value of a name that has not yet been matched) 
	 * @author amitport
	 */
	static final class Unassigned implements Serializable {
		private Unassigned(){}
		public Object writeReplace() throws ObjectStreamException 
			{return SERIALIZED_UNASSIGNED_VALUE;}
		
		@Override
		public String toString() {
			return "<Unassigned>";
		}
	}
	
	/**
	 * a serialized version of {@link Unassigned}
	 * @author amitport
	 */
	static final class SerializedUnassigned implements Serializable {
		private static final long serialVersionUID = -3488584018216259087L;

		private SerializedUnassigned(){}
		
		Object readResolve() throws ObjectStreamException
			{return UNASSIGNED_VALUE;}
	}

}
