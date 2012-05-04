package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import java.io.File
import scala._
import edu.colorado.phet.simsharinganalysis.{phet, Log}

/**
 * Generates reports for the games only, for groups A1-A2
 * @author Sam Reid
 */

class GameReport(result: GameResult) {
  lazy val undefinedString = if ( result.finished ) "undefined (finished)" else "undefined (aborted)"

  override def toString = result.level + "\t" + result.hiding + "\t" + ( 0 to 5 ).map(i => if ( result.checks.isDefinedAt(i) ) result.checks(i) else undefinedString).mkString("\t")
}

class IndividualReport(log: Log) {
  val report = RPALAnalysis toReport log
  val gameReports = report.gameResults.map(r => new GameReport(r))

  override def toString = log.session + "\t" + gameReports.mkString("\t")
}

class GameSummaryReport(name: String, logs: List[Log]) {
  lazy val individualReports = logs.map(log => new IndividualReport(log))

  override def toString =
    "name\n" + individualReports.mkString("\n")
}

object RPALAnalysisGameSummary {

  def main(args: Array[String]) {
    val a1Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A1 logs")
    println(new GameSummaryReport("A1", a1Logs))
  }
}