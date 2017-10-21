package com.inference.engine.models

import scala.collection.mutable

class InferenceEngine(rulesFilePath: String) {
  private var rules: Map[String, List[Rule]] = Map()
  private var rulesIndex: Map[String, List[Rule]] = Map()

  case class ProveResult(proved: Boolean, resultingUniverse: List[String])

  parseRules()

  private def parseRules(): Unit = {
    import scala.io.Source

    for (line <- Source.fromFile(rulesFilePath).getLines()) {
      val lineTrimed = line.trim

      if(!lineTrimed.isEmpty) {
        val List(causesInString, consecuensesInString) = lineTrimed.split("IF")(1).split("THEN").toList

        val causes = causesInString.split(",").filter(x => !x.isEmpty).map(x => x.replace(" ", "")).toList
        val consecuenses = consecuensesInString.split(",").filter(x => !x.isEmpty).map(x => x.replace(" ", "")).toList
        val rule = Rule(causes, consecuenses)

        causes.foreach { cause =>
          rules = rules ++ Map(cause -> (rule::rules.getOrElse(cause, List())))
        }

        consecuenses.foreach { consecuence =>
          rulesIndex = rulesIndex ++ Map(consecuence -> (rule::rulesIndex.getOrElse(consecuence, List())))
        }
      }
    }
  }

  def updateUniverse(universe: List[String], statement: String): List[String] = {
    if(!universe.contains(statement)) {
      println("Universe updated with: " + statement)
      statement :: universe
    } else {
      universe
    }
  }

  def infer(universe: List[String]): List[String] = {
    val queue: mutable.Queue[String] = mutable.Queue()
    var myUniverse = universe

    myUniverse.foreach(x => queue.enqueue(x))

    while (queue.length > 0) {
      val statement = queue.dequeue()

      rules.getOrElse(statement, List()).foreach { rule =>
        if(rule.isFulfilledIn(myUniverse)) {

          rule.consecuenses.foreach { new_statement =>
            if(!myUniverse.contains(new_statement)) {
              println("Infered from " + rule.causes + " -> " + new_statement)
              myUniverse = updateUniverse(myUniverse, new_statement)
              queue.enqueue(new_statement)
            }
          }
        }
      }
    }

    myUniverse
  }

  def prove(statement: String, universe: List[String]): Boolean = {
    if(universe.contains(statement)) {
      return true
    }

    val rules = rulesIndex.getOrElse(statement, List())

    rules.map { rule =>
      if(rule.isFulfilledIn(universe)) {
        true
      } else {
        rule.causes.map(cause => prove(cause, universe)).reduce((x, y) => x && y)
      }
    }.contains(true)
  }
}
