package controllers

import javax.inject._

import models.InferenceEngine
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, environment: Environment) extends AbstractController(cc) {

  val ruleEngine = new InferenceEngine(gerResourceFilePath("/toothDiagnosisRules.rls"))

  println("Base test")

  val baseEngine = new InferenceEngine(gerResourceFilePath("/baseRules.rls"))

  forwardTest(ruleEngine, List("a","b","c","e"))
  backwardTest(ruleEngine, List("a","b","c","e"),"z")
  backwardTest(ruleEngine, List("f", "b"),"d")

  println("\nAnimal rules test\n")

  val animalEngine = new InferenceEngine(gerResourceFilePath("/animalRules.rls"))

  forwardTest(animalEngine, List("croaks","eatsFlies"))
  backwardTest(animalEngine, List("croaks","eatsFlies"),"green")
  backwardTest(animalEngine, List("croaks","eatsFlies"),"yellow")

  def gerResourceFilePath(file: String): String = {
    environment.resource(file).get.getPath.replaceAll("%20", " ")
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

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>

    Ok(views.html.index())
  }
}
