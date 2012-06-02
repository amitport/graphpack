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
package graphpack.local.persistence.inmemory;
import java.util.HashMap;
import java.util.Map;

/**
 * implements a simple store that uses an {@link HashMap}
 * @author amitport
 * @param <T> the type of the object to be stored
 */
public class InMemoryObjectMap<T> {
	public Map<String,T> objectFromName;
	public InMemoryObjectMap() {
		objectFromName = new HashMap<String, T>();
	}

	/**
	 * see {@link java.util.Map#containsKey(Object)}
 	 */
	public boolean contains(String key) {
		return objectFromName.containsKey(key);
	}

	/**
	 * see {@link java.util.Map#put(Object, Object)}
	 */
	public void put(String key, T value) {
		objectFromName.put(key, value);
	}

	/**
	 * see {@link java.util.Map#get(Object)}
	 */
	public T get(String key) {
		return objectFromName.get(key);
	}

}
