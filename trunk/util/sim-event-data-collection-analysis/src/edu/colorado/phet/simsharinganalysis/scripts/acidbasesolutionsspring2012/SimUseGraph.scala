package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012


/**
 * @author Sam Reid
 */
object SimUseGraph {
  val NoCredit = "No credit"
  val Explore = "Explore"
  val Prompted = "Prompted"
  val NNN = List(NoCredit, NoCredit, NoCredit)
  val EPP = List(Explore, Prompted, Prompted)
  val EEE = List(Explore, Explore, Explore)
  val EEP = List(Explore, Explore, Prompted)

  def table = {
    Map(
      List("introductionTab") -> NNN,
      List("strongAcidRadioButton") -> EPP,
      List("weakAcidRadioButton") -> EPP,
      List("strongBaseRadioButton") -> EEE,
      List("weakBaseRadioButton") -> EEE,
      List("waterRadioButton") -> EEE,
      List("magnifyingGlassRadioButton") -> EEP,
      List("showSolventCheckBox") -> EEE,
      List("concentrationGraphRadioButton") -> EEP,
      List("liquidRadioButton") -> EEE,
      List("phMeterRadioButton", "phMeterRadioButtonIcon") -> NNN,
      List("phPaperRadioButton", "phPaperRadioButtonIcon") -> NNN,
      List("conductivityTesterRadioButton", "conductivityTesterRadioButtonIcon") -> NNN,
      List("dunkedPhMeter") -> EEP,
      List("dunkedPhPaper") -> EEE,
      List("completedCircuit") -> EEE,
      List("customSolutionTab") -> NNN,
      List("acidRadioButton") -> EPP,
      List("acid.concentrationControl") -> EPP,
      List("acid.strongRadioButton") -> EPP,
      List("acid.weakRadioButton") -> EPP,
      List("acid.weakStrengthControl") -> EPP,
      List("baseRadioButton") -> EEE,
      List("base.concentrationControl") -> EEE,
      List("base.strongRadioButton") -> EEE,
      List("base.weakRadioButton") -> EEE,
      List("base.weakStrengthControl") -> EEE
    )
  }

  def main(args: Array[String]) {
    println(table)
  }
}
