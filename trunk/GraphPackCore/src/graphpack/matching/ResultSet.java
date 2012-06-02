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

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

/**
 * a multi-set of matched results (the same result can be repeated - in the future this behavior may be customized)
 * @author amitport
 */
public class ResultSet implements Serializable {
	private static final long serialVersionUID = -6513155130201864844L;
	
	final private Multiset<Result> results;
	
	public ResultSet(){
		results = HashMultiset.create();
	}
	
	private ResultSet(Multiset<Result> results){
		this.results = results;
	}
	
	/**
	 * check if there are any results
	 * @return true IFF there are no results
	 */
	public boolean isEmpty() {
		return results.isEmpty();
	}
	
	/**
	 * adds a single result
	 * @param r the result to add
	 */
	public void add(Result r) {
		results.add(r);
	}
	
	/**
	 * check if this set contains a specific result
	 * @param r the result to look for
	 * @return true IFF the result is found
	 */
	public boolean contains(Result r) {
		return results.contains(r);
	}
	
	/**
	 * return the a set of distinct results
	 * @return distinct results
	 */
	public Set<Result> elementSet() {
		return results.elementSet();
	}
	
	@Override
	public String toString(){
		return results.toString();
	}
	/*** static ***/

	/**
	 * Product joins two result sets, entities with the same name are merged
	 * if either one is unassigned (if only one of them is assigned we take it's value)
	 * or if they point to the same value (otherwise we disregard the result)
	 * @return the merged ResultSet
	 */
	public static ResultSet product(ResultSet rs1, ResultSet rs2){
		ResultSet $ = new ResultSet();
		for (Result res1 : rs1.results){
			for (Result res2 : rs2.results){
				Result r = Result.merge(res1, res2);
				if (r != null) $.results.add(r);
			}
		}
		return $;		
	}

	/**
	 * Union of all the results in both input result sets
	 * @return the unified result set
	 */
	public static ResultSet union(ResultSet rs1, ResultSet rs2){
		ResultSet $ = new ResultSet();
		$.results.addAll(rs1.results);
		$.results.addAll(rs2.results);
		return $;
	}
	
	/**
	 * create empty result set (&#8709)
	 * @return the empty result set
	 */
	public static ResultSet Empty() {
		return new ResultSet(new ImmutableMultiset.Builder<Result>().build());
	}
	
	/**
	 * create an epsilon result set ({&#949;}), i.e., a set with single empty result <br/>
	 * note that unlike an empty set the epsilon set is considered a successful result
	 * @return an epsilon set
	 */
	public static ResultSet Epsilon() {
		ResultSet $ = new ResultSet();
		$.add(new Result());
		return $;
	}
}
