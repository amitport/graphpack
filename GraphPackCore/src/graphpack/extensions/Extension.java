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

/**
 * this interface should be sub-classed by extension providers <br/>
 * it allows for an extension to provide an extended version of a currently processed node <br/> 
 * this extension should be security aware (otherwise it could just be a static method)
 * @author amitport
 */
public interface Extension {
	
	/**
	 * provide an extended verion of a given node
	 * @param node the nofe
	 * @return an extended node as defined by the extension
	 */
	Object extendNode(INode node);
}
