package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012.SimUseGraphSupport.Group


/**
 * @author Sam Reid
 */
object SimUseGraph {

  //http://stackoverflow.com/questions/10373715/scala-ignore-case-class-field-for-equals-hascode
  case class Feature(name: String)(val filter: AcidBaseReport => Boolean)

  val NoCredit = "No credit"
  val Explore = "Explore"
  val Prompted = "Prompted"
  val NNN = List(NoCredit, NoCredit, NoCredit)
  val EPP = List(Explore, Prompted, Prompted)
  val EEE = List(Explore, Explore, Explore)
  val EEP = List(Explore, Explore, Prompted)

  def table = {
    List(
      Feature("introductionTab")(_.used("introductionTab")) -> NNN,
      Feature("strongAcidRadioButton")(_.used("strongAcidRadioButton")) -> EPP,
      Feature("weakAcidRadioButton")(_.used("weakAcidRadioButton")) -> EPP,
      Feature("strongBaseRadioButton")(_.used("strongBaseRadioButton")) -> EEE,
      Feature("weakBaseRadioButton")(_.used("weakBaseRadioButton")) -> EEE,
      Feature("waterRadioButton")(_.used("waterRadioButton")) -> EEE,
      Feature("magnifyingGlassRadioButton")(_.used("magnifyingGlassRadioButton")) -> EEP,
      Feature("showSolventCheckBox")(_.used("showSolventCheckBox")) -> EEE,
      Feature("concentrationGraphRadioButton")(_.used("concentrationGraphRadioButton")) -> EEP,
      Feature("liquidRadioButton")(_.used("liquidRadioButton")) -> EEE,
      Feature("phMeterRadioButton, phMeterIcon")(_.used("phMeterRadioButton", "phMeterIcon")) -> NNN,
      Feature("phPaperRadioButton, phPaperIcon")(_.used("phPaperRadioButton", "phPaperIcon")) -> NNN,
      Feature("conductivityTesterRadioButton, conductivityTesterIcon")(_.used("conductivityTesterRadioButton", "conductivityTesterIcon")) -> NNN,
      Feature("dunkedPhMeter")(_.dunkedPHMeter) -> EEP,
      Feature("dunkedPhPaper")(_.dunkedPHPaper) -> EEE,
      Feature("completedCircuit")(_.completedCircuit) -> EEE,
      Feature("customSolutionTab")(_.used("customSolutionTab")) -> NNN,

      Feature("acidRadioButton")(_.used("acidRadioButton")) -> EPP,
      Feature("acid.concentrationControl")(r => r.usedAcidControlOn2ndTab("concentrationControl")) -> EPP,
      Feature("acid.strongRadioButton")(r => r.usedAcidControlOn2ndTab("strongRadioButton")) -> EPP,
      Feature("acid.weakRadioButton")(r => r.usedAcidControlOn2ndTab("weakRadioButton")) -> EPP,
      Feature("acid.weakStrengthControl")(r => r.usedAcidControlOn2ndTab("weakStrengthControl")) -> EPP,

      Feature("baseRadioButton")(_.used("baseRadioButton")) -> EEE,
      Feature("base.concentrationControl")(r => r.usedBaseControlOn2ndTab("concentrationControl")) -> EEE,
      Feature("base.strongRadioButton")(r => r.usedBaseControlOn2ndTab("strongRadioButton")) -> EEE,
      Feature("base.weakRadioButton")(r => r.usedBaseControlOn2ndTab("weakRadioButton")) -> EEE,
      Feature("base.weakStrengthControl")(r => r.usedBaseControlOn2ndTab("weakStrengthControl")) -> EEE
    )
  }

  def getFractionInGroup(feature: Feature, group: Group) = {
    val size = group.size
    val number = group.reports.filter(report => feature.filter(report)).length
    number.toDouble / size.toDouble
  }

  def main(args: Array[String]) {
    println(table)

    val groups = SimUseGraphSupport.groups
    println("loaded " + groups.length + " groups")

    for ( feature <- table.map(e => e._1) ) {
      println(feature)
      for ( group <- groups ) {
        val fraction = getFractionInGroup(feature, group)
        println(group.name + ": " + fraction)
      }
    }

    println("Feature\tA1\tA2\tA3\t\tA1-E\tA1-P\tA2-E\tA2-P\tA3-E\tA3-P\t\tA1-E\tA1-P\tA2-E\tA2-P\tA3-E\tA3-P")
    for ( entry <- table ) {
      val feature = entry._1
      val classifications = entry._2

      def indicator(column: String) = {
        val myType = column.last + ""
        val index = getGroupIndex(column)
        val classification = classifications(index)
        if ( myType.charAt(0) == classification.charAt(0) ) "1\t" else "\t"
      }

      def data(column: String) = {
        val index: Int = getGroupIndex(column)
        val amount = getFractionInGroup(feature, groups(index)) + ""
        if ( indicator(column) == "1\t" ) amount + "\t" else "\t"
      }

      println(feature.name + "\t" + classifications(0) + "\t" + classifications(1) + "\t" + classifications(2) + "\t\t" +
              indicator("A1-E") + indicator("A1-P") + indicator("A2-E") + indicator("A2-P") + indicator("A3-E") + indicator("A3-P") + "\t" +
              data("A1-E") + data("A1-P") + data("A2-E") + data("A2-P") + data("A3-E") + data("A3-P")
      )
    }
  }

  def getGroupIndex(column: String): Int = column.substring(0, 2) match {
    case "A1" => 0
    case "A2" => 1
    case "A3" => 2
  }
}
