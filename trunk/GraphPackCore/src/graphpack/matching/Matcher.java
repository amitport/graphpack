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

import graphpack.parsing.java.IReservedIdentifiers;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * this is the principle class that is responsible for the pattern matching
 * @author amitport
 */
public abstract class Matcher implements Serializable {
	private static final long serialVersionUID = -2128454884790538032L;

	/** @return true IFF this pattern already matched some valid results */
	abstract public boolean canTake();
	/** @return results matched so far */
	abstract public ResultSet take();
	
	/** @return true IFF there are some possible edges that can be accepted by this pattern */
	abstract public boolean canCont();
	/** 
	 * matches an edge to the beginning of this pattern
	 * @return a derived matcher, i.e., a matcher that is responsible on matching the rest of the pattern
	 */
	abstract public Matcher cont(graphpack.SerializableEdge e);
	
	/** @return a new Matcher which results can merge with the input results */
	abstract public Matcher conformTo(ResultSet rs);
	
	/*** static ***/
	
	/**
	 * empty (can be viewed as null) matcher, always returns no result 
	 * @author amitport
	 */
	public static class FinishedWithNoResults extends Matcher{
		private static final long serialVersionUID = 7141719605961205939L;
		
		@Override
		public boolean canTake(){return false;}
		@Override
		public boolean canCont(){return false;}
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {return this;}
		@Override
		public Matcher conformTo(ResultSet rs) {return this;}
		@Override
		public ResultSet take() {return ResultSet.Empty();}
		@Override
		public String toString() {return "<empty>";}
	}

	/**
	 * epsilon (can be viewed as an empty string "") matcher,
	 * may return some results (previously accumulated) but expects no further inputs 
	 * @author amitport
	 */
	public static class FinishedWithSomeResults extends Matcher{
		private static final long serialVersionUID = 8963226114219021863L;
		
		final ResultSet rs;
		public FinishedWithSomeResults(ResultSet rs){
			this.rs = rs;
		}
		@Override
		public boolean canTake(){return true;}
		@Override
		public boolean canCont(){return false;}
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {return new FinishedWithNoResults();}
		@Override
		public Matcher conformTo(ResultSet rs) {
			ResultSet newRs = ResultSet.product(this.rs, rs);
			return ((newRs.isEmpty())?new FinishedWithNoResults():new FinishedWithSomeResults(newRs));
		}
		@Override
		public ResultSet take() {return rs;}
		@Override
		public String toString() {
			return "<epsilon>";}
	}
	
	/**
	 * matches a single edge
	 * @author amitport
	 */
	public static class Edge extends Matcher {
		private static final long serialVersionUID = 2894625041465800190L;
		
		final ResultSet rs;
		final String edgeName, edgeType, targetName;
		final Predicate pred;
		final IReservedIdentifiers parser;
		
		public Edge(IReservedIdentifiers parser,String edgeName, String edgeType, String targetName, Predicate pred){
			this(parser,null,edgeName,edgeType,targetName,pred);
		}
		public Edge(IReservedIdentifiers parser,ResultSet rs, String edgeName, String edgeType, String targetName, Predicate pred){
			if (rs != null){
				this.rs = rs;
			}else{
				this.rs = new ResultSet();				
				Result r = new Result();
				if (edgeName != null) r.put(edgeName, Result.UNASSIGNED_VALUE);
				if (targetName != null) r.put(targetName, Result.UNASSIGNED_VALUE);
				this.rs.add(r);//initially we have a single result which may have unassigned edgeName and targetName				
			}

			this.edgeName = edgeName;
			this.edgeType = edgeType;//TODO
			this.targetName = targetName;
			this.pred = pred;
			this.parser = parser;
		}
		@Override
		public boolean canTake() {return false;/*since we haven't matched the edge yet*/}
		@Override
		public boolean canCont() {return true;/*since we need to match one edge*/}
		
		/** 
		 * @return true IFF {@code o1} doesn't conflict with another value with key {@code name} at {@code res}.
		 * if needed {@code newRes} is updated with the new value {@code o1} */
		private boolean handleConflict(Result res, String name, Object o1){
			if (name != null){
				Object o2 = res.get(name);//we know o2 is not null because we've put it in the constructor
				if (o2 == Result.UNASSIGNED_VALUE){
					res.put(name, o1);
					return true;
				} else if (!o1.equals(o2)){
					return false;
				} else {
					return true;
				}
			} else {
				return true;				
			}	
		}
		
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {
			if (!canCont()) return new FinishedWithNoResults();
			
			ResultSet $ = new ResultSet();
			for (Result res : rs.elementSet()){
				Result newRes = new Result(res);
				newRes.put(parser.getCURRENT_SRC_NAME(), e.source);
				newRes.put(parser.getCURRENT_EDG_NAME(), e);
				newRes.put(parser.getCURRENT_TRG_NAME(), e.target);
				if (   handleConflict(newRes,edgeName,e)
					&& handleConflict(newRes,targetName,e.target)
					&& (pred==null || pred.evaluate(newRes.getMap()))){
					newRes.remove(parser.getCURRENT_SRC_NAME());
					newRes.remove(parser.getCURRENT_EDG_NAME());
					newRes.remove(parser.getCURRENT_TRG_NAME());
					$.add(newRes);
				}			
			}
			return ($.isEmpty())?new FinishedWithNoResults():new FinishedWithSomeResults($);
		}
		@Override
		public Matcher conformTo(ResultSet rs){
			ResultSet newRs = ResultSet.product(this.rs,rs);
			return ((this.rs.isEmpty())?new FinishedWithNoResults():
				new Edge(parser,newRs,edgeName,edgeType,targetName,pred));
		}
		@Override
		public ResultSet take() {return rs;}
		@Override
		public String toString() {
			return "-" + ((edgeName!=null||edgeType!=null)?"["+
					((edgeName!=null)?edgeName:"")+":"
					+((edgeType!=null)?edgeType:"")+ "]":"") + "->" 
					+ ((targetName!=null)?targetName:"")
					+ ((pred!=null)?"?"+pred:"");
		}
	}
	
	/**
	 * matches two alternatives at the same time
	 * @author amitport
	 */
	public static class Or extends Matcher {
		private static final long serialVersionUID = -7248241815875103990L;
		
		final Matcher r,s;
		public Or(Matcher r,Matcher s){this.r = r;this.s = s;}
		@Override
		public boolean canTake() {return r.canTake()||s.canTake();}
		@Override
		public boolean canCont() {return r.canCont()||s.canCont();}
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {
			if (!canCont()) return new FinishedWithNoResults();
			if (!r.canCont()) return s.cont(e);
			if (!s.canCont()) return r.cont(e);
			return new Or(r.cont(e),s.cont(e));
		}
		@Override
		public Matcher conformTo(ResultSet rs){
			return new Or(r.conformTo(rs),s.conformTo(rs));
		}
		@Override
		public ResultSet take() {
			if (!s.canTake()) return r.take();
			if (!r.canTake()) return s.take();
			return ResultSet.union(r.take(),s.take());
		}
		@Override
		public String toString() {
			return "("+r+" or "+s+")";
		}	
	}
	
	/**
	 * used to pair two consecutive matchers together, this is vital for making sequences
	 * @author amitport
	 */
	public static class Cons extends Matcher {
		private static final long serialVersionUID = 5972474348841909144L;
		
		Matcher r,s;
		public Cons(Matcher r,Matcher s){this.r = r;this.s = s;}
		
		public static Cons makeList(LinkedList<Matcher> ms){
			if (ms.isEmpty())
				throw new IllegalArgumentException("can't create an empty path");
			Matcher $ = ms.removeFirst();
			if (ms.isEmpty())
				return new Cons($,new FinishedWithSomeResults(ResultSet.Epsilon()));
			return new Cons($,makeList(ms));
		}
		
		@Override
		public boolean canTake() {return r.canTake()&&s.canTake();}
		@Override
		public boolean canCont() {return ((r.canTake()||r.canCont())//the first matcher must be able to continue or to finish (otherwise this pair will never get matched)
											&&(s.canTake()||s.canCont())//same for the second matcher
											&&(r.canCont()||s.canCont()));}//at least one of the matchers must be able to continue
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {
			if (!canCont()) return new FinishedWithNoResults();
			if (!s.canCont()) return r.conformTo(s.take()).cont(e);
			if (!r.canCont()) return s.conformTo(r.take()).cont(e);
			
			if (r.canTake()){//the first had some valid results
				if (r.canCont()){//but it can also continue
					Matcher nextR = r.cont(e);
					Matcher nextCons;
					if (!nextR.canCont()){
						nextCons = s.conformTo(nextR.take());
					}else {
						nextCons = new Cons(nextR,s.conformTo(nextR.take()));
					}
					if (s.canCont())//the second can also continue -> we've got a path split
						return new Or(nextCons,s.cont(e));//either the first will takes this match or the second
					return nextCons; //the second can't continue -> the first takes this match
				} else {
					return s.cont(e); //r took what he can -> moving to s
				}
			} else  {
				Matcher nextR = r.cont(e); // r can we continue since we've checked canCont in the start of the function
				if (!nextR.canCont()){
					if (!nextR.canTake()) return new FinishedWithNoResults();
					return s.conformTo(nextR.take());
				}
				Matcher nextCons = new Cons(nextR,s.conformTo(nextR.take()));
				return nextCons;//continue with r
			}
		}
		@Override
		public Matcher conformTo(ResultSet rs){
			return new Cons(r.conformTo(rs),s.conformTo(rs));	
		}
		@Override
		public ResultSet take() {
			return ResultSet.product(r.take(),s.take());
		}
		@Override
		public String toString() {
			return "("+r.toString()+", "+s.toString()+")";
		}
	}
	
	/**
	 * regular star operator
	 * @author amitport
	 */
	public static class Repeat extends Matcher {
		private static final long serialVersionUID = -5258929810407712331L;
		
		final Matcher r;
		final int min,max;//max >=1
		public Repeat(Matcher r,int min,int max){this.r = r;this.min = min;this.max = max;}
		@Override
		public boolean canTake() {return min<=0;/*we've repeat the matcher enough times*/}
		@Override
		public boolean canCont() {
			return r.canCont()&&max>=1;/*the matcher can continue and we've haven't passed our max repeat limit*/ 
		}
		@Override
		public Matcher cont(graphpack.SerializableEdge e) {
			if (!r.canCont()) return new FinishedWithNoResults();
			if (max==1/*this is our last repeat*/) return r.cont(e);
			Matcher nextR = r.cont(e);
			return new Cons(nextR,new Repeat(r,min-1,max-1).conformTo(nextR.take()));/*let r match his stuff first than see if repeat is needed*/
		}
		@Override
		public Matcher conformTo(ResultSet rs){
			return new Repeat(r.conformTo(rs),min,max);
		}
		@Override
		public ResultSet take() {return r.take();}
		@Override
		public String toString() {
			return "("+r.toString()+")*"+min+".."+max;
		}
	}
}
