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
package graphpack.parsing.scala


import scala.util.parsing.combinator.JavaTokenParsers
import graphpack. matching.Value
import graphpack.matching.Value._
import graphpack.matching.Predicate
import graphpack.matching.Predicate._
import graphpack.matching.Predicate.Comparison._
import graphpack.matching.Matcher
import graphpack.matching.Matcher.{Cons,Repeat,Edge}
import java.util.LinkedList
import graphpack.parsing.java.IReservedIdentifiers
import scala.collection.mutable.Buffer
import graphpack.matching.Value.Entity

/**
 * the PackCypher parser
 */
class Parser extends JavaTokenParsers with graphpack.parsing.java.IParser {
self =>
	val reservedIdentifiers = new IReservedIdentifiers {
	    def getCURRENT_SRC_NAME() = "_src"
	    def getCURRENT_EDG_NAME() = "_edg"
	    def getCURRENT_TRG_NAME() = "_trg"
	      
	    def asList = List(getCURRENT_SRC_NAME(),getCURRENT_EDG_NAME(),getCURRENT_TRG_NAME())
	}
	
    def parsePath(str: String,pvals:Object*) = parseAll(pathWithRepeat(pvals),str) match {
    	case Success(result,_) => result
    	case NoSuccess(result,rest) => throw new IllegalArgumentException(result +" "+ rest.pos)
	}
  
	def parsePredicate(str: String,pvals:Object*):Predicate = parseAll(predicate(pvals),str) match {
	    	case Success(result,_) => result
	    	case NoSuccess(result,_) => throw new IllegalArgumentException(result)
	 }
	
     def pathWithRepeat(pvals:Seq[Object]):Parser[Matcher] = rep1(optionalRepeat(step(pvals)) | optionalRepeat(parens(pathWithRepeat(pvals)))) ^^	{
										    (x)=>
										        Cons.makeList(
										          new LinkedList[Matcher](java.util.Arrays.asList(x.toArray:_*)))
										         
										  }

   def optionalRepeat(m:Parser[Matcher]):Parser[Matcher] = m ~ opt("*"~opt(wholeNumber~"..")~wholeNumber) ^^ {
									     case p ~ rep => rep match {
										  	      case Some("*"~min1~max) => {
										  	        var minI = (min1 match {case Some(min2~"..")=> min2;case None=> max;}).toInt 
										  	        val maxI = max.toInt
										  	        if (minI<0 || maxI < 1)throw new IllegalArgumentException("repeated steps must have min>=0 and max>=1")
										  	        new Repeat(p,minI,maxI)
										  	      }
										  	      case None => p
										  	    }
   										}

   def step(pvals:Seq[Object]):Parser[Matcher] = "-" ~ opt("[" ~> edgeNameAndType <~ "]") ~ "->" ~ opt(matchedIdentifier) ~ opt("?"~>"(" ~> predicate(pvals) <~")") ^^ {
	  	case "-" ~ et ~ "->" ~ target ~ pred =>
	  	  {
	  	    et match {
	  	      case None => new Edge(reservedIdentifiers,null,null,target.orNull,pred.orNull)
	  	      case Some(x) => new Edge(reservedIdentifiers,x._1.orNull,x._2.orNull,target.orNull,pred.orNull)
	  	    }
	  	  }
	  }

  
  def edgeNameAndType:Parser[(Option[String],Option[String])] = opt(matchedIdentifier) ~ opt(":" ~ ident) ^^{
    case None ~ None => throw new IllegalArgumentException("when using [] you must specify either name or type")
    case e ~ x => (e,x match {case Some(":" ~ t) => Some(t)
      						  case None => None})
  }

  def matchedIdentifier:Parser[String] = ident ^^ {
  	case str => if (reservedIdentifiers.asList.exists(_.equals(str))){
      throw new IllegalArgumentException("\""+ str +"\" is reserved in this context",null)
    } else {
      str
    }
  }

  def edgeInfo(pvals:Seq[Object]): Parser[(String,Option[Predicate])] = ident ~ opt("?" ~> predicate(pvals)) ^^
		  				{case e ~ pred => (e,pred)};
  
  
  def node: Parser[Option[String]] = ("_" | ident) ^^ {case "_" => None;case i => Some(i)}

  /* predicate */
  def predicate(pvals:Seq[Object]): Parser[Predicate] = (orderedComparison(pvals) | not(pvals) | notEquals(pvals) | equals(pvals) | parens(predicate(pvals))) * (
    ignoreCase("and") ^^^ { (a: Predicate, b: Predicate) => new And(a, b)  } |
    ignoreCase("or") ^^^  { (a: Predicate, b: Predicate) => new Or(a, b) }
    )
  
  def equals(pvals:Seq[Object]): Parser[Predicate] = value(pvals) ~ "==" ~ value(pvals) ^^ {
    case l ~ "==" ~ r => new Equals(l, r)
  }
  
  def notEquals(pvals:Seq[Object]): Parser[Predicate] = value(pvals) ~ "!=" ~ value(pvals) ^^ {
    case l ~ "!=" ~ r => new Not(new Equals(l, r))
  }
  
  def orderedComparison(pvals:Seq[Object]): Parser[Predicate] = (lessThanOrEqual(pvals) | greaterThanOrEqual(pvals) | lessThan(pvals) | greaterThan(pvals))

  def lessThan(pvals:Seq[Object]): Parser[Predicate] = orderedValue(pvals) ~ "<" ~ orderedValue(pvals) ^^ {
    case l ~ "<" ~ r => new LesserThan(l, r)
  }

  def greaterThan(pvals:Seq[Object]): Parser[Predicate] = orderedValue(pvals) ~ ">" ~ orderedValue(pvals) ^^ {
    case l ~ ">" ~ r => new GreaterThan(l, r)
  }

  def lessThanOrEqual(pvals:Seq[Object]): Parser[Predicate] = orderedValue(pvals) ~ "<=" ~ orderedValue(pvals) ^^ {
    case l ~ "<=" ~ r => new LesserOrEqualThan(l, r)
  }

  def greaterThanOrEqual(pvals:Seq[Object]): Parser[Predicate] = orderedValue(pvals) ~ ">=" ~ orderedValue(pvals) ^^ {
    case l ~ ">=" ~ r => new GreaterOrEqualThan(l, r)
  }
  
  def not(pvals:Seq[Object]): Parser[Predicate] = ignoreCase("not") ~ "(" ~ predicate(pvals) ~ ")" ^^ {
    case not ~ "(" ~ inner ~ ")" => new Not(inner)
  }

  /* value */
  def value(pvals:Seq[Object]): Parser[Value] = ( property(pvals) | parameterLiteral(pvals) | nullLiteral | booleanLiteral | doubleLiteral | entity | apostropheStringLiteral   )
  
  def entity: Parser[Value] = ident ^^ (x =>  new Entity(x))
    
  def parameterLiteral(pvals:Seq[Object]): Parser[Value] = parameter(pvals) ^^
  						(x =>  new Literal(x) )
  						 
  def parameter(pvals:Seq[Object]): Parser[Object] = "{" ~> wholeNumber <~ "}" ^^
  						(x => pvals(x.toInt) )

  /* ordered value */
  def orderedValue(pvals:Seq[Object]): Parser[Value] = (doubleLiteral | property(pvals) | parameterLiteral(pvals) )
	
  /* boolean */
  def booleanLiteral: Parser[Value] = (trueX | falseX)
  def trueX: Parser[Value] = ignoreCase("true") ^^ (x => new Literal(true))
  def falseX: Parser[Value] = ignoreCase("false") ^^ (x => new Literal(false))
  
  /* null */
  def nullLiteral: Parser[Value] = "null" ^^ (x => new Literal(null))
  
  /* property */ 
  def property(pvals:Seq[Object]): Parser[Value] = (parameter(pvals) | ident) ~ propChain ^^ {
    case e ~ p => {
      new Property(e, p)
    }
  }
  
  def propChain: Parser[Array[String]] = rep1("." ~ ident) ^^ {
    (x) => {
     var res:Buffer[String] = Buffer[String]()
      for ("."~p <-x){
       res.append(p)
     }
     res.toArray
    }
  } 
  
  /* string */
  def apostropheStringLiteral: Parser[Value] = ("\'" + """([^'\p{Cntrl}\\]|\\[\\/bfnrt]|\\u[a-fA-F0-9]{4})*""" + "\'").r ^^ (x => new Literal(x.substring(1, x.length - 1)))
 
  /* double */
  def doubleLiteral: Parser[Value] = (floatingPointNumber | decimalNumber | wholeNumber) ^^ (x => new Literal(x.toDouble))
  
  /* helpers */
  def ignoreCase(str: String): Parser[String] = ("""(?i)\Q""" + str + """\E""").r ^^ (x => x.toLowerCase)
  def parens[U](inner: Parser[U]) = "(" ~> inner <~ ")"
}

