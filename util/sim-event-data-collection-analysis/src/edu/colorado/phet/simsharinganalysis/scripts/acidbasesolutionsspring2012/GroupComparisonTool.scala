// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import javax.swing.JFrame.EXIT_ON_CLOSE
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import swing._
import edu.colorado.phet.simsharinganalysis.phet
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset
import org.jfree.chart.plot.CategoryPlot
import org.jfree.chart.axis.{NumberAxis, CategoryLabelPositions, CategoryAxis}
import org.jfree.chart.{JFreeChart, ChartFrame}
import org.jfree.chart.renderer.category.StatisticalBarRenderer
import java.io.File
import javax.swing.JFileChooser
import collection.mutable.ArrayBuffer

/**
 * Utility for showing different groups and plotting data
 * @author Sam Reid
 */
object GroupComparisonTool extends SimpleSwingApplication {
  private val list = new ArrayBuffer[File]
  private var lastDir: File = null
  lazy val top = new Frame {
    title = "Group comparison"
    val f = this
    peer setDefaultCloseOperation EXIT_ON_CLOSE
    contents = new BorderPanel {
      val panel = this
      val boxPanel = new BoxPanel(Orientation.Vertical) {
        minimumSize = new Dimension(800, 600)
        preferredSize = new Dimension(800, 600)
      }
      add(boxPanel, BorderPanel.Position.Center)
      add(new FlowPanel {
        contents += Button("Add Data Collection") {
                                                    val chooser = new JFileChooser(lastDir) {
                                                      setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
                                                    }
                                                    val result = chooser.showOpenDialog(f.peer)
                                                    result match {
                                                      case JFileChooser.APPROVE_OPTION => {
                                                        boxPanel.contents += new Label(chooser.getSelectedFile.getAbsolutePath)
                                                        list += chooser.getSelectedFile
                                                        lastDir = chooser.getSelectedFile.getParentFile
                                                        f.repaint()
                                                        panel.peer.paintImmediately(0, 0, panel.peer.getWidth, panel.peer.getHeight)
                                                        f.bounds = new Rectangle(f.peer.getX, f.peer.getY, f.peer.getWidth - 1, f.peer.getHeight)
                                                        f.bounds = new Rectangle(f.peer.getX, f.peer.getY, f.peer.getWidth + 1, f.peer.getHeight)
                                                      }
                                                      case _ => {}
                                                    }
                                                  }

        contents += Button("Analyze All") {
                                            CompareGroups.process(list.toList)
                                          }
      }, BorderPanel.Position.South)
      pack()
    }
    SwingUtils centerWindowOnScreen peer
  }
}

object CompareGroups extends App {

  process(List(new File("C:\\Users\\Sam\\Desktop\\kl-one-recitation"), new File("C:\\Users\\Sam\\Desktop\\kl-two-recitation")))

  def process(folders: List[File]) {

    val groups = for ( g <- folders ) yield {
      val logs = phet.load(g).sortBy(_.startTime)
      GroupResult(logs.map(AcidBaseSolutionSpring2012AnalysisReport.toReport(_)).toList)
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
        addBooleanToPlot(groups, group, _.selectedBase, "selected base")
        addBooleanToPlot(groups, group, _.showedSolvent, "showed solvent")
        addBooleanToPlot(groups, group, _.dunkedPHMeter, "dunked pH meter")
        addBooleanToPlot(groups, group, _.dunkedPHPaper, "dunked pH paper")
        addBooleanToPlot(groups, group, _.completedCircuit, "completed circuit")
      }
    })

    plot("time", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(groups, group, _.timeSimOpenMin, "time open (min)")
      }
    })

    plot("number transitions", groups, new MyStatisticalDataSet {
      for ( group <- groups ) {
        addToPlot(groups, group, _.numTabTransitions, "tab")
        addToPlot(groups, group, _.numSolutionTransitions, "solution")
        addToPlot(groups, group, _.numViewTransitions, "view")
        addToPlot(groups, group, _.numTestTransitions, "test")
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
  def addBooleanToPlot(groups: List[GroupResult], group: GroupResult, f: SessionResult => Boolean, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f).map(x => if ( x ) 1.0 else 0.0)
    add(phet average values, phet standardDeviation values, "group " + groups.indexWhere(_ eq group), label)
  }

  def addToPlot(groups: List[GroupResult], group: GroupResult, f: SessionResult => Double, label: String) {
    val values: Seq[Double] = group.sessionResults.map(f)
    add(phet average values, phet standardDeviation values, "group " + groups.indexWhere(_ eq group), label)
  }
}

case class GroupResult(sessionResults: List[SessionResult]) {
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