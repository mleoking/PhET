package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

/**
 * @author Sam Reid
 */
object SimUseGraphTimePlots {

  //Range of minutes to look at, inclusive
  val minutes = 1 to 45

  //Convert minutes to milliseconds
  def minutesToMilliseconds(minute: Int) = minute * 60000

  //Count the events that happen within the specified minute
  def getCountsForMinute(minute: Int, report: AcidBaseReport, rule: StateTransition => Boolean) = {
    val startTime = minutesToMilliseconds(minute - 1)
    val endTime = minutesToMilliseconds(minute)
    val logStartTime = report.log.startTime

    report.statesWithTransitions.filter(p =>
                                          p.entry.time - logStartTime >= startTime &&
                                          p.entry.time - logStartTime < endTime &&
                                          AcidBaseReport.isAcidBaseClick(report.log, p.entry) && //only consider events classified as a click, such as dragging the slider left
                                          rule(p)).length
  }

  //Get a list of counts for each minute specified above
  def getCounts(report: AcidBaseReport, rule: StateTransition => Boolean) = minutes.map(minute => getCountsForMinute(minute, report, rule))

  //Rule that allows anny state transition to be counted
  def countEverything(s: StateTransition) = true

  //Rule that only counts clicks that are classified as "Explore" or "Prompted" according to the featureType argument
  def countClicks(s: StateTransition, featureType: String, groupIndex: Int, report: AcidBaseReport) = {
    //only want to count this transition if it is in the same category as the specified feature type (for this group).
    //it should only match one thing in the table
    val matched = SimUseGraph.table.filter(_._1.filter(s))
    matched.length match {
      case 0 => false //didn't match anything in the table, so doesn't count as explore
      case 1 => matched.head._2.apply(groupIndex) == featureType
    }
  }

  //Find the first occurrence of the feature (if any), but only considering the events counted as clicks
  def firstOccurrenceOfFeature(s: StateTransition, f: Feature, report: AcidBaseReport) = {
    report.statesWithTransitions.filter(p => AcidBaseReport.isAcidBaseClick(report.log, p.entry)).find(f.filter)
  }

  //Check whether this is the first time a certain feature was used, considering only the events that count as clicks
  def isNovel(s: StateTransition, report: AcidBaseReport) = {
    val tableEntry = SimUseGraph.table.filter(_._1.filter(s)).head
    val firstOccurrence = firstOccurrenceOfFeature(s, tableEntry._1, report)
    if ( firstOccurrence.isDefined ) s == firstOccurrence.get else false
  }

  //Classifier that returns true for a StateTransition if it is the first time the feature is used
  def countNewClicks(s: StateTransition, featureType: String, groupIndex: Int, report: AcidBaseReport) = if ( countClicks(s, featureType, groupIndex, report) ) isNovel(s, report) else false

  def main(args: Array[String]) {
    val groups = SimUseGraphSupport.groups
    for ( group <- groups ) {
      println("longest use in group: " + group.reports.map(_.log.minutesUsed).max)
    }
    println("Session\tType\t" + minutes.mkString("\t"))
    for ( groupIndex <- 0 to 2 ) {
      println()
      println("A" + ( groupIndex + 1 ) + " Sessions")
      println()
      for ( report <- groups(groupIndex).reports ) {
        println(report.session + "\tTotal Clicks (E,P,N)\t" + getCounts(report, countEverything).mkString("\t"))
        println(report.session + "\tExplore Clicks (E)\t" + getCounts(report, countClicks(_, SimUseGraph.Explore, groupIndex, report)).mkString("\t"))
        println(report.session + "\tPrompted Clicks (P)\t" + getCounts(report, countClicks(_, SimUseGraph.Prompted, groupIndex, report)).mkString("\t"))
        println(report.session + "\tNew Features (E)\t" + getCounts(report, countNewClicks(_, SimUseGraph.Explore, groupIndex, report)).mkString("\t"))
        println(report.session + "\tNew Features (P)\t" + getCounts(report, countNewClicks(_, SimUseGraph.Prompted, groupIndex, report)).mkString("\t"))
      }
    }
  }
}