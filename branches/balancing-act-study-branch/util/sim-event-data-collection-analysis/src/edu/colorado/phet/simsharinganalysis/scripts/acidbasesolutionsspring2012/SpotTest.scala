package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.phet
import java.io.File

/**
 * @author Sam Reid
 */
object SpotTest {
  val session = "bgbfcm7dgdfhutv1cpdteq7agt"

  def main(args: Array[String]) {
    val file = """C:\Users\Sam\Desktop\phet\studies\abs-study-data\kl-ten-recitation\s180_th3p_a1_c9_2012-01-26_15-40-40_rtfuekn4jr9p17739samt3k243_bgbfcm7dgdfhutv1cpdteq7agt.txt"""
    val report = new AcidBaseReport(phet.load(new File(file) :: Nil).head)
    //    report.statesWithTransitions.filter(p => p.start.selectedTab==1 && p.start.tab1.acid==false).map(_.entry).foreach(println)
    //    report.statesWithTransitions.foreach(println)
    //    val problemItem = report.statesWithTransitions.filter(_.entry.time == 1327617847750L)
    //    println(problemItem)
    //
    //    val s = problemItem.head
    //    val selected = !s.start.tab1.acid && s.start.selectedTab == 1 && used(control)
    //    println("selected = " + selected)
  }
}
