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
package graphpack.extensions;

import graphpack.INode;

import java.util.HashMap;
import java.util.Map;

/**
 * a collection of extension objects that may be passed to running tasks
 * @author amitport
 */
public class Extensions {
	private final Map<String,Extension> extensionsMap;
	public Extensions(Map<String,Extension> extensionsMap){
		this.extensionsMap = extensionsMap;
	}

	
	/**
	 * get an extended versions of a given node
	 * @param node the node to extend
	 * @return a map from extension name to extended node
	 */
	public Map<String,Object> getExtendedNodeMap(INode node){
		Map<String,Object> ExtendedNodeMap = new HashMap<String,Object>();
		for (Map.Entry<String, Extension> e : extensionsMap.entrySet()) {
			ExtendedNodeMap.put(e.getKey(), e.getValue().extendNode(node));
			
		}
		return ExtendedNodeMap;
	}
}
