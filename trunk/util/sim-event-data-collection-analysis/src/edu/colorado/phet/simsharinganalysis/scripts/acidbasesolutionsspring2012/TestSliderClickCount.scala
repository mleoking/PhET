package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.phet
import java.io.File

/**
 * @author Sam Reid
 */
object TestSliderClickCount {
  def main(args: Array[String]) {
    val logFile = phet.load(new File("C:\\Users\\Sam\\Desktop\\phet\\studies\\abs-study-data\\kl-eleven-recitation\\s190_f1p_a2_c3_2012-01-27_13-03-41_44sdmfktubaecsr1q4kqn7o67c_rh03d6mnmbtn0j8aed0r733oj3.txt") :: Nil).head
    println(logFile)
    val report = new AcidBaseReport(logFile)
    println(report.clicks.length)

    //    val groups = SimUseGraphSupport.groups
    //    for (group <- groups){
    //      println("started new group")
    //      for (report <- group.reports){
    //        val entryPairs = report.log.entries.zip(report.log.entries.tail)
    //        for (pair <- entryPairs){
    //          if (pair._1.action == pair._2.action  && pair._1.action=="startDrag"){
    //            println("startDrag happened on adjacent lines in this file: "+report.log.file)
    //          }
    //        }
    //      }
    //    }


  }
}
