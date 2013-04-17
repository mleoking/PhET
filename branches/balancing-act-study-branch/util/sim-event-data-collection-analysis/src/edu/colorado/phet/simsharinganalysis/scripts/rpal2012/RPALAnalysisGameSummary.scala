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

  override def toString = {
    result.level + "\t" + result.hiding + "\t" + ( 0 until 5 ).map(i => if ( result.points.isDefinedAt(i) ) result.points(i) else undefinedString).mkString("\t")
  }
}

class IndividualReport(log: Log) {
  val report = RPALAnalysis toReport log
  val gameReports = report.gameResults.map(r => new GameReport(r))

  override def toString = log.file.getName + "\t" + gameReports.mkString("\t")
}

class GameSummaryReport(name: String, logs: List[Log]) {
  lazy val individualReports = logs.map(log => new IndividualReport(log))

  def columns(gameIndex: Int) = "Game " + gameIndex + ": Level\t" +
                                "Game " + gameIndex + ": Hidden\t" +
                                "Game " + gameIndex + ": Question 1\t" +
                                "Game " + gameIndex + ": Question 2\t" +
                                "Game " + gameIndex + ": Question 3\t" +
                                "Game " + gameIndex + ": Question 4\t" +
                                "Game " + gameIndex + ": Question 5"

  override def toString = "File\t" + ( 1 to 7 ).map(i => columns(i)).mkString("\t") + "\n" + individualReports.mkString("\n")
}

object RPALAnalysisGameSummary {

  def main(args: Array[String]) {
    val a1Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A1 logs")
    a1Logs.foreach(log => {
      val report = new RPALAnalysis.Report(log)
      val gr = report.gameResults
      gr.foreach(g => {
        println(g.points.mkString(","))
      })
    })
    println(new GameSummaryReport("A1", a1Logs))
  }
}