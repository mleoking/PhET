// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.axis.{NumberAxis, CategoryLabelPositions, CategoryAxis}
import org.jfree.chart.{JFreeChart, ChartFrame}
import org.jfree.chart.renderer.category.StatisticalBarRenderer
import java.io.File
import edu.colorado.phet.simsharinganalysis.phet
import edu.colorado.phet.simsharinganalysis.util.MathUtil
import AcidBaseSolutionSpring2012AnalysisReport.toReport

object GroupComparisonTool extends App {

  //Recursively list all files under a directory, see http://stackoverflow.com/questions/4629984/scala-cleanest-way-to-recursively-parse-files-checking-for-multiple-strings
  def filesAt(f: File): List[File] = if ( f.isDirectory ) f.listFiles.flatMap(filesAt).toList else f :: Nil

  process(new File("C:\\Users\\Sam\\Desktop\\abs-study-data"))

  def process(folder: File) {
    val group1 = filesAt(folder).filter(_.getName.indexOf("_a1_") >= 0) -> "a1"
    val group2 = filesAt(folder).filter(_.getName.indexOf("_a2_") >= 0) -> "a2"
    val group3 = filesAt(folder).filter(_.getName.indexOf("_a3_") >= 0) -> "a3"

    //    val group1 = filesAt(folder).filter(_.getName.indexOf("_a1_") >= 0).slice(0, 3) -> "a1"
    //    val group2 = filesAt(folder).filter(_.getName.indexOf("_a2_") >= 0).slice(0, 3) -> "a2"
    //    val group3 = filesAt(folder).filter(_.getName.indexOf("_a3_") >= 0).slice(0, 3) -> "a3"

    println("group 1\n" + group1._1.mkString("\n"))
    println("group 2\n" + group2._1.mkString("\n"))
    println("group 3\n" + group3._1.mkString("\n"))

    val groups = for ( g <- group1 :: group2 :: group3 :: Nil; logs = phet.load(g._1) ) yield {
      GroupResult(logs.map(toReport).toList, g._2)
    }
    for ( group <- groups ) {
      println("Processing group: " + group)
      println("averageTimeOpen = " + group.averageTimeOpen)
      println("averageNumberOfClicks = " + group.averageNumberOfClicks)
      println("averageNumberSelectedBase = " + group.averageNumberSelectedBase)
      println("averageNumberShowedSolvent = " + group.averageNumberShowedSolvent)
      println("averageNumberDunkedPHMeter = " + group.averageNumberDunkedPHMeter)
      println("averageNumberDunkedPHPaper = " + group.averageNumberDunkedPHPaper)
      println("averageNumberCompletedCircuit = " + group.averageNumberCompletedCircuit)
      println("averageNumberTabTransitions = " + group.averageNumberTabTransitions)
      println("averageNumberSolutionTransitions = " + group.averageNumberSolutionTransitions)
      println("averageNumberViewTransitions = " + group.averageNumberViewTransitions)
      println("averageNumberTestTransitions = " + group.averageNumberTestTransitions)
      group
    }

    plot("Number of events", "count", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addBooleanToPlot(group, _.selectedBase, "selected base")
        addBooleanToPlot(group, _.showedSolvent, "showed solvent")
        addBooleanToPlot(group, _.dunkedPHMeter, "dunked pH meter")
        addBooleanToPlot(group, _.dunkedPHPaper, "dunked pH paper")
        addBooleanToPlot(group, _.completedCircuit, "completed circuit")
      }
    })

    plot("Time", "time (minutes)", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(group, _.timeSimOpenMin, "time open")
        addToPlot(group, _.firstClickToLastClick, "first to last click")

        //Time on solutions
        addToPlot(group, s => s.solutionTable.toMap.getOrElse(Globals.weakAcid, 0L) / 60.0 / 1000.0 + s.solutionTable.toMap.getOrElse(Globals.strongAcid, 0L) / 60.0 / 1000.0, "solution: acid")
        addToPlot(group, s => s.solutionTable.toMap.getOrElse(Globals.weakBase, 0L) / 60.0 / 1000.0 + s.solutionTable.toMap.getOrElse(Globals.strongBase, 0L) / 60.0 / 1000.0, "solution: base")
        addToPlot(group, s => s.solutionTable.toMap.getOrElse(Globals.water, 0L) / 60.0 / 1000.0, "solution: water")

        //Time on views
        addToPlot(group, _.viewTable.toMap.getOrElse(Globals.molecules, 0L) / 60.0 / 1000.0, "view: molecules")
        addToPlot(group, _.viewTable.toMap.getOrElse(Globals.barGraph, 0L) / 60.0 / 1000.0, "view: bar graph")
        addToPlot(group, _.viewTable.toMap.getOrElse(Globals.liquid, 0L) / 60.0 / 1000.0, "view: liquid")
      }
    })

    plot("Number of transitions", "number transitions", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(group, _.numTabTransitions, "tab")
        addToPlot(group, _.numSolutionTransitions, "solution")
        addToPlot(group, _.numViewTransitions, "view")
        addToPlot(group, _.numTestTransitions, "test")
      }
    })

    plotHistogram("Clicks vs Time", groups, _.clicksPerMinute)
    plotHistogram("Clicks vs Time (based on first click)", groups, _.clicksPerMinuteBasedOnFirstClick)
  }

  //    Plot the histogram of clicks for each condition? So for
  //    instance, the first bin (0-1 min) would include the average number of
  //    clicks for a1, a2, and a3. And so forth.
  def plotHistogram(title: String, groups: List[GroupResult], histogram: AcidBaseSolutionSpring2012AnalysisReport => Map[Long, Int]) {
    def maxMinute(g: GroupResult): Int = g.sessionResults.map(histogram(_).keys.max).max.toInt
    val maxTime = groups.map(maxMinute(_)).max
    println("max time = " + maxTime)
    plot(title, "number of clicks", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        for ( i <- 0 until maxTime.toInt ) {
          addToPlot(group, histogram(_).getOrElse(i.toLong, 0).toDouble, "clicks in minute " + i)
        }
      }
    })
  }

  def plot(title: String, range: String, groups: List[GroupResult], dataSet: MyStatisticalDataSet) {

    //Create a statistical bar chart of the provided data
    val categoryAxis = new CategoryAxis("Type") {
      //      setCategoryMargin(0.4)
      setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(java.lang.Math.PI / 2.0));
    }
    val plot = new CategoryPlot(dataSet, categoryAxis, new NumberAxis(range), new StatisticalBarRenderer)
    new ChartFrame(title, new JFreeChart(title, plot)) {
      setSize(900, 600)
      SwingUtils.centerWindowOnScreen(this)
    }.setVisible(true)
  }
}

class MyStatisticalDataSet extends DefaultStatisticalCategoryDataset {
  def addBooleanToPlot(group: GroupResult, f: AcidBaseSolutionSpring2012AnalysisReport => Boolean, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f).map(x => if ( x ) {
      1.0
    }
    else {
      0.0
    })
    add(MathUtil average values, MathUtil standardDeviation values, group.name, label)
  }

  def addToPlot(group: GroupResult, f: AcidBaseSolutionSpring2012AnalysisReport => Double, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f)
    add(MathUtil average values, MathUtil standardDeviation values, group.name, label)
  }
}

case class GroupResult(sessionResults: List[AcidBaseSolutionSpring2012AnalysisReport], name: String) {
  def indicator(b: Boolean) = if ( b ) 1 else 0

  def averageTimeOpen = MathUtil average sessionResults.map(_.timeSimOpenMin)

  def averageNumberOfClicks = MathUtil averageInt sessionResults.map(_.numberOfClicks)

  def averageNumberSelectedBase = MathUtil averageInt sessionResults.map(_.selectedBase).map(indicator)

  def averageNumberShowedSolvent = MathUtil averageInt sessionResults.map(_.showedSolvent).map(indicator)

  def averageNumberDunkedPHMeter = MathUtil averageInt sessionResults.map(_.dunkedPHMeter).map(indicator)

  def averageNumberDunkedPHPaper = MathUtil averageInt sessionResults.map(_.dunkedPHPaper).map(indicator)

  def averageNumberCompletedCircuit = MathUtil averageInt sessionResults.map(_.completedCircuit).map(indicator)

  def averageNumberTabTransitions = MathUtil averageInt sessionResults.map(_.numTabTransitions)

  def averageNumberSolutionTransitions = MathUtil averageInt sessionResults.map(_.numSolutionTransitions)

  def averageNumberViewTransitions = MathUtil averageInt sessionResults.map(_.numViewTransitions)

  def averageNumberTestTransitions = MathUtil averageInt sessionResults.map(_.numTestTransitions)
}