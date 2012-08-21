package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

/**
 * @author Sam Reid
 */
object SimUseGraphTimePlots {
  val minutes = 1 to 45

  def toMillis(minute: Int) = minute * 60000

  def getCountsForMinute(minute: Int, report: AcidBaseReport, featureType: String) = {
    val startMillis = toMillis(minute - 1)
    val endMillis = toMillis(minute)
    val startTime = report.log.startTime

    //TODO: Account for feature type
    //TODO: Only count first time a feature used?
    report.statesWithTransitions.filter(p =>
                                          p.entry.time - startTime >= startMillis &&
                                          p.entry.time - startTime < endMillis).length
  }

  def getCounts(report: AcidBaseReport, featureType: String) = minutes.map(minute => getCountsForMinute(minute, report, featureType))

  def main(args: Array[String]) {
    val groups = SimUseGraphSupport.groups
    for ( group <- groups ) {
      println("longest use in group: " + group.reports.map(_.log.minutesUsed).max)
    }
    val table = SimUseGraph.table
    //for each group

    //the 0/1 indicator table for each session
    println("Session\tType\t" + minutes.mkString("\t"))
    for ( groupIndex <- 0 :: 1 :: 2 :: Nil ) {
      println()
      println("A" + ( groupIndex + 1 ) + " Sessions")
      println()
      for ( report <- groups(groupIndex).reports ) {
        println(report.session + "\t" + SimUseGraph.Explore + "\t" + getCounts(report, SimUseGraph.Explore).mkString("\t"))
        //        println(report.session + "\t" + SimUseGraph.Prompted + "\t" + getCounts(report, SimUseGraph.Prompted).mkString("\t"))
        //        println(report.session + "\t" + SimUseGraph.NoCredit + "\t" + getCounts(report, SimUseGraph.NoCredit).mkString("\t"))
      }
    }
  }
}
