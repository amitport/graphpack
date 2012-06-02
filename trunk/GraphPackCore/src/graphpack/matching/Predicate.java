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
import java.util.Map;

/**
 * Represents a check which returns a boolean result
 * @author amitport
 */
public abstract class Predicate implements Serializable {
	private static final long serialVersionUID = -5582111230656346494L;

	/**
	 * evaluates this predicate 
	 * @param env a map of environment object
	 * @return true IFF this predicate is true
	 */
	public abstract <T extends Object> boolean evaluate(Map<String, T> env);
	
	/*** static ***/
	
	/**
	 * a predicate that negates the value of an inner predicate 
	 * @author amitport
	 */
	public static class Not extends Predicate {
		private static final long serialVersionUID = -5582111230656346494L;
		
		Predicate inner;

		public Not(Predicate inner) {
			this.inner = inner;
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
				
			return !inner.evaluate(env);
		}

		@Override
		public String toString() {
			return "!" + inner;
		}
	}


	/**
	 * a predicate that combines two inner predicates 
	 * @author amitport
	 */
	public abstract static class BinaryCombinator<T> extends Predicate {
		private static final long serialVersionUID = -5582111230656346494L;
		
		T lhs,rhs;
		String op;
		
		BinaryCombinator(T lhs, T rhs, String op){
			this.lhs = lhs;
			this.rhs = rhs;
			this.op = op;
		}
		
		@Override
		public String toString() {
			return "(" + lhs + " " + op + " " + rhs + ")";
		}
	}

	/**
	 * a predicate that is true IFF both inner predicates are true 
	 * @author amitport
	 */
	public static class And extends BinaryCombinator<Predicate> {
		private static final long serialVersionUID = -5582111230656346494L;
		
		public And(Predicate lhs, Predicate rhs) {
			super(lhs,rhs,"and");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return lhs.evaluate(env) && rhs.evaluate(env);
		}
	}

	/**
	 * a predicate that is true IFF at least one inner predicates are true 
	 * @author amitport
	 */
	public static class Or extends BinaryCombinator<Predicate> {
		private static final long serialVersionUID = -5582111230656346494L;
		
		public Or(Predicate lhs, Predicate rhs) {
			super(lhs,rhs,"or");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return lhs.evaluate(env) || rhs.evaluate(env);
		}
	}

	/**
	 * a predicate that checks two values for equality <br/>
	 * {@link Number} instances are checked using {@link Double#compare(double, double)} <br/>
	 * other objects are checked using {@link Object#equals(Object)}  
	 * @author amitport
	 */
	public static class Equals extends BinaryCombinator<Value> {
		private static final long serialVersionUID = -5582111230656346494L;

		public Equals(Value lhs, Value rhs) {
			super(lhs,rhs,"==");
		}

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			Object ol = lhs.get(env);
			Object or = rhs.get(env);
			if (ol==null) return or==null;
			if (ol instanceof Number && or instanceof Number) {
				return Double.compare(((Number) ol).doubleValue(),
									((Number) or).doubleValue()) == 0;
			} else {
				return ol.equals(or);
			}
		}
	}

	/**
	 * a predicate that compares two {@link Number} instances by using {@link Double#compare(double, double)} <br/>
	 * @author amitport
	 */
	abstract public static class Comparison extends BinaryCombinator<Value> {
		private static final long serialVersionUID = -5582111230656346494L;

		public Comparison(Value lhs, Value rhs, String op) {
			super(lhs,rhs,op);
		}

		public int compare(Object lhs, Object rhs) {
			if (lhs instanceof Number && rhs instanceof Number) {
				Double ld = ((Number) lhs).doubleValue();
				Double rd = ((Number) rhs).doubleValue();
				return Double.compare(ld, rd);
			} else {
				throw new RuntimeException("Can't Compare: " + lhs + " with " + rhs);
			}
		}
		/**
		 * main comparison function to be overridden in subclasses <br/>
		 * @param compareResult the result returned from {@link Double#compare(double, double)}
		 * @return true IFF the comparison is valid
		 */
		abstract public boolean isInCorrectOrder(int compareResult);

		@Override
		public <T extends Object> boolean evaluate(Map<String, T> env) {
			return isInCorrectOrder(compare(lhs.get(env), rhs.get(env)));
		}

		/**
		 * true IFF lhs > rhs
		 * @author amitport
		 */
		public static class GreaterThan extends Comparison {
			private static final long serialVersionUID = -5582111230656346494L;

			public GreaterThan(Value lhs, Value rhs) {
				super(lhs,rhs,">");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult > 0;
			}
		}

		/**
		 * true IFF lhs >= rhs
		 * @author amitport
		 */
		public static class GreaterOrEqualThan extends Comparison {
			private static final long serialVersionUID = -5582111230656346494L;
			
			public GreaterOrEqualThan(Value lhs, Value rhs) {
				super(lhs,rhs,">=");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult >= 0;
			}
		}

		/**
		 * true IFF lhs < rhs
		 * @author amitport
		 */
		public static class LesserThan extends Comparison {
			private static final long serialVersionUID = -5582111230656346494L;
			
			public LesserThan(Value lhs, Value rhs) {
				super(lhs,rhs,"<");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult < 0;
			}
		}

		/**
		 * true IFF lhs <= rhs
		 * @author amitport
		 */
		public static class LesserOrEqualThan extends Comparison {
			private static final long serialVersionUID = -5582111230656346494L;
			
			public LesserOrEqualThan(Value lhs, Value rhs) {
				super(lhs,rhs,"<=");
			}

			@Override
			public boolean isInCorrectOrder(int compareResult) {
				return compareResult <= 0;
			}
		}
	}
}
