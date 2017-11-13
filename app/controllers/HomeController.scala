package controllers

import javax.inject._

import models._
import play.api._
import play.api.libs.json.{JsError, Json, Reads}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, environment: Environment)
  extends AbstractController(cc) {

  implicit val colorationReads = Json.reads[Coloration]
  implicit val kindOfPainReads = Json.reads[KindOfPain]
  implicit val coldStimulusReads = Json.reads[ColdStimulus]
  implicit val heatStimulusReads = Json.reads[HeatStimulus]
  implicit val electricalStimulationReads = Json.reads[ElectricalStimulation]
  implicit val percussionStimulationReads = Json.reads[PercussionStimulation]
  implicit val pulpStateReads = Json.reads[PulpState]
  implicit val patientAgeReads = Json.reads[PatientAge]
  implicit val toothTestReads = Json.reads[ToothTest]

  implicit val diagnosisWrites = Json.writes[Diagnosis]

  val ruleEngine = new InferenceEngine(gerResourceFilePath("/toothDiagnosisRules.rls"))

  /*
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
  */

  def gerResourceFilePath(file: String): String = {
    environment.resource(file).get.getPath.replaceAll("%20", " ")
  }

  def forwardTest() = Action { implicit request: Request[AnyContent] =>
    val toothTest = request.body.asJson.get.as[ToothTest]
    val universe = toothTest.toUniverse
    val newUniverse = ruleEngine.infer(universe)

    println(newUniverse.diff(universe))

    val diagnosis = Diagnosis(newUniverse.diff(universe)(0).replace("Diagnosis.", ""))

    Ok(Json.toJson(Diagnosis.translate(diagnosis)))
  }

  def backwardTest(universe: List[String], statement: String): Unit = {
    println("")
    println("")
    println("  ******* Backward Test ******")
    println("")
    val originalUniverse = universe
    println("Universe: " + universe)

    if(ruleEngine.prove(statement, universe)) {
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
