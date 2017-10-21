package com.inference.engine.models

case class Rule(causes: List[String], consecuenses: List[String]) {

  def isFulfilledIn(universe: List[String]): Boolean = {
    causes forall (universe contains)
  }
}


