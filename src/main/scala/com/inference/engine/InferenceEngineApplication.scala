package com.inference.engine

import com.inference.engine.models.InferenceEngine

object InferenceEngineApplication {
  def main(args: Array[String]): Unit = {
    println("Base test")

    val baseEngine = new InferenceEngine(gerResourceFilePath("/baseRules.rls"))

    forwardTest(baseEngine, List("a","b","c","e"))
    backwardTest(baseEngine, List("a","b","c","e"),"z")
    backwardTest(baseEngine, List("f", "b"),"d")

    println("\nAnimal rules test\n")

    val animalEngine = new InferenceEngine(gerResourceFilePath("/animalRules.rls"))

    forwardTest(animalEngine, List("croaks","eatsFlies"))
    backwardTest(animalEngine, List("croaks","eatsFlies"),"green")
    backwardTest(animalEngine, List("croaks","eatsFlies"),"yellow")
  }

  def gerResourceFilePath(file: String): String = {
    getClass.getResource(file).getPath.replaceAll("%20", " ")
  }

  def forwardTest(engine: InferenceEngine, universe: List[String]): Unit = {
    println("")
    println("")
    println("  ******* Forward Test ******")
    println("")
    println("New universe: " + engine.infer(universe))
  }

  def backwardTest(engine: InferenceEngine, universe: List[String], statement: String): Unit = {
    println("")
    println("")
    println("  ******* Backward Test ******")
    println("")
    val originalUniverse = universe
    println("Universe: " + universe)

    if(engine.prove(statement, universe)) {
      println(s"${statement} proved given ${originalUniverse}")
    } else {
      println(s"can't be proved ${statement} given ${originalUniverse}")
    }
  }
}
