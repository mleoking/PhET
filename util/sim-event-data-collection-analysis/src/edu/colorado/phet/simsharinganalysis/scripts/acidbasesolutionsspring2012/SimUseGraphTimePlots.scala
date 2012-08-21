package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

/**
 * @author Sam Reid
 */
object SimUseGraphTimePlots {
  val minutes = 1 to 45

  def toMillis(minute: Int) = minute * 60000

  def getCountsForMinute(minute: Int, report: AcidBaseReport, rule: StateTransition => Boolean) = {
    val startMillis = toMillis(minute - 1)
    val endMillis = toMillis(minute)
    val startTime = report.log.startTime

    //TODO: Account for feature type
    //TODO: Only count first time a feature used?
    report.statesWithTransitions.filter(p =>
                                          p.entry.time - startTime >= startMillis &&
                                          p.entry.time - startTime < endMillis &&
                                          AcidBaseReport.isAcidBaseClick(report.log, p.entry) && //make sure it is classified as a click
                                          rule(p)).length
  }

  def getCounts(report: AcidBaseReport, rule: StateTransition => Boolean) = minutes.map(minute => getCountsForMinute(minute, report, rule))

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
        def countEverything(s: StateTransition) = true
        def countClicks(s: StateTransition, featureType: String) = {
          //                    val filtered = table.filter(_._1.filter(s))
          //                    assert(filtered.length == 1)
          //                    val element = filtered.head
          //                    val classification = element._2(groupIndex)
          //                    classification == featureType
          true
        }
        println(report.session + "\tTotal Clicks (E,P,N)\t" + getCounts(report, countEverything).mkString("\t"))
        println(report.session + "\tExplore Clicks (E)\t" + getCounts(report, countClicks(_, SimUseGraph.Explore)).mkString("\t"))
        println(report.session + "\tPrompted Clicks (P)\t" + getCounts(report, countClicks(_, SimUseGraph.Prompted)).mkString("\t"))
        println(report.session + "\tNew Features (E)\t" + getCounts(report, countEverything).mkString("\t"))
        println(report.session + "\tNew Features (P)\t" + getCounts(report, countEverything).mkString("\t"))
      }
    }
  }
}
