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
package graphpack.parsing.java;

import graphpack.matching.Matcher;
import graphpack.matching.Predicate;

/**
 * an interface for the PachCypher parser (which is written in {@code scala})
 * @author amitport
 */
public interface IParser {
	/**
	 * parses a path
	 * @param str the PackCypher expression
	 * @param params parameters for the expression
	 * @return a matcher that corresponds to the expression
	 */
	Matcher parsePath(String str, Object... params);
	
	/**
	 * parses just a predicate 
	 * @param str a predicate of a PackCypher expression
	 * @param params  parameters for the expression
	 * @return a predicate object that corresponds to the expression
	 */
	Predicate parsePredicate(String str, Object... params);
}
