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

object GroupComparisonTool extends App {

  //Recursively list all files under a directory, see http://stackoverflow.com/questions/4629984/scala-cleanest-way-to-recursively-parse-files-checking-for-multiple-strings
  def filesAt(f: File): List[File] = if ( f.isDirectory ) f.listFiles.flatMap(filesAt).toList else f :: Nil

  process(new File("C:\\Users\\Sam\\Desktop\\abs-study-data"))

  def process(folder: File) {
    val group1 = filesAt(folder).filter(_.getName.indexOf("_a1_") >= 0) -> "a1"
    val group2 = filesAt(folder).filter(_.getName.indexOf("_a2_") >= 0) -> "a2"
    val group3 = filesAt(folder).filter(_.getName.indexOf("_a3_") >= 0) -> "a3"

    println("group 1\n" + group1._1.mkString("\n"))
    println("group 2\n" + group2._1.mkString("\n"))
    println("group 3\n" + group3._1.mkString("\n"))

    val groups = for ( g <- group1 :: group2 :: group3 :: Nil; logs = phet.load(g._1) ) yield {
      GroupResult(logs.map(AcidBaseSolutionSpring2012AnalysisReport.toReport(_)).toList, g._2)
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

    plot("count", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addBooleanToPlot(group, _.selectedBase, "selected base")
        addBooleanToPlot(group, _.showedSolvent, "showed solvent")
        addBooleanToPlot(group, _.dunkedPHMeter, "dunked pH meter")
        addBooleanToPlot(group, _.dunkedPHPaper, "dunked pH paper")
        addBooleanToPlot(group, _.completedCircuit, "completed circuit")
      }
    })

    plot("time", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(group, _.timeSimOpenMin, "time open (min)")
      }
    })

    plot("number transitions", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(group, _.numTabTransitions, "tab")
        addToPlot(group, _.numSolutionTransitions, "solution")
        addToPlot(group, _.numViewTransitions, "view")
        addToPlot(group, _.numTestTransitions, "test")
      }
    })
  }

  def plot(range: String, groups: List[GroupResult], dataSet: MyStatisticalDataSet) {

    //Create a statistical bar chart of the provided data
    val categoryAxis = new CategoryAxis("Type") {
      setCategoryMargin(0.4)
      setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(java.lang.Math.PI / 2.0));
    }
    val plot = new CategoryPlot(dataSet, categoryAxis, new NumberAxis(range), new StatisticalBarRenderer)
    val title = "Title"
    new ChartFrame(title, new JFreeChart(title, plot)) {
      setSize(900, 600)
      SwingUtils.centerWindowOnScreen(this)
    }.setVisible(true)
  }
}

class MyStatisticalDataSet extends DefaultStatisticalCategoryDataset {
  def addBooleanToPlot(group: GroupResult, f: SessionResult => Boolean, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f).map(x => if ( x ) 1.0 else 0.0)
    add(phet average values, phet standardDeviation values, group.name, label)
  }

  def addToPlot(group: GroupResult, f: SessionResult => Double, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f)
    add(phet average values, phet standardDeviation values, group.name, label)
  }
}

case class GroupResult(sessionResults: List[SessionResult], name: String) {
  def indicator(b: Boolean) = if ( b ) 1 else 0

  def averageTimeOpen = phet average sessionResults.map(_.timeSimOpenMin)

  def averageNumberOfClicks = phet averageInt sessionResults.map(_.numberOfClicks)

  def averageNumberSelectedBase = phet averageInt sessionResults.map(_.selectedBase).map(indicator)

  def averageNumberShowedSolvent = phet averageInt sessionResults.map(_.showedSolvent).map(indicator)

  def averageNumberDunkedPHMeter = phet averageInt sessionResults.map(_.dunkedPHMeter).map(indicator)

  def averageNumberDunkedPHPaper = phet averageInt sessionResults.map(_.dunkedPHPaper).map(indicator)

  def averageNumberCompletedCircuit = phet averageInt sessionResults.map(_.completedCircuit).map(indicator)

  def averageNumberTabTransitions = phet averageInt sessionResults.map(_.numTabTransitions)

  def averageNumberSolutionTransitions = phet averageInt sessionResults.map(_.numSolutionTransitions)

  def averageNumberViewTransitions = phet averageInt sessionResults.map(_.numViewTransitions)

  def averageNumberTestTransitions = phet averageInt sessionResults.map(_.numTestTransitions)
}

//Results from parsing and analyzing, so they can be averaged, composited, etc.
case class SessionResult(session: String,
                         timeSimOpenMin: Double,
                         firstClickToLastClick: Double,
                         numberOfClicks: Int,
                         clicksPerMinute: Map[Long, Int],
                         interactiveComponentsResult: InteractionResult,
                         selectedBase: Boolean,
                         showedSolvent: Boolean,
                         dunkedPHMeter: Boolean,
                         dunkedPHPaper: Boolean,
                         completedCircuit: Boolean,
                         timeOnSolutionsMin: Map[String, String],
                         timeOnViewsMin: Map[String, String],
                         timeOnTestsMin: Map[String, String],
                         numTabTransitions: Int,
                         numSolutionTransitions: Int,
                         numViewTransitions: Int,
                         numTestTransitions: Int,
                         nonInteractiveComponentsResult: InteractionResult)

//Case classes max out at 22 parameters, so combine in composites
case class InteractionResult(numberEvents: Int, componentsUsed: List[String], componentsNotUsed: List[String])