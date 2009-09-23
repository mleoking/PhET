package edu.colorado.phet.reid.piccolochart

import edu.umd.cs.piccolo.PNode
import collection.mutable.ArrayBuffer
import edu.colorado.phet.piccoloscala.Predef._
import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.piccoloscala.{SCanvas, MyJFrame}
import java.lang.Math._
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Rectangle2D}
import java.awt.{Color, BasicStroke, Dimension}
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import java.awt.event.{ActionEvent, ActionListener}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import org.jfree.chart.{JFreeChart, ChartFrame, ChartFactory}
import javax.swing.{JFrame, Timer}
//This chart is a feasibility test to attempt the following features for a chart library:
//1. Easy integration with piccolo, including ease of layouts
//2. High performance, including during scrolling, panning and zooming (may not improve over a decoration-less jfreechart)
//3. Assumption of XY data series, so easier configuration and no casting to XYPlot
//4. Better MVC pattern (improvement over ControlGraph.addSeries, not over JFreeChart)
//5. Rendering improvements over JFreechart piccolo wrapper, such as chart boundaries not lining up properly, and tick labels getting cropped off
//6. Interaction like dragging the chart to pan
//7. Easier to add piccolo-ish artifacts inside the chart, such as tick labels, an arrow for an axis, game icons for moving man, etc.
//8. Eaiser to draw (or interact with) chart data as in calculus grapher.

//I noticed we already went down the path of writing our own chart library here:
//simulations-java\common\charts\src\edu\colorado\phet\common\charts\Chart.java
//This library was used in sims such as Fourier; probably would have been better to use JFreeChart there, but would have created some other difficulties 

case class DataPoint(x: Double, y: Double)
class DataSeries(name: String) {
  val data = new ArrayBuffer[DataPoint] //todo: make private and iterate with map
  val listeners = new ArrayBuffer[() => Unit]
  val pointAddedListeners = new ArrayBuffer[(Double,Double)=>Unit]

  def addPoint(x: Double, y: Double) = {
    data += new DataPoint(x, y)
//    println("added data: "+data.mkString(", "))
    for (l <- listeners) l()
    for (p <- pointAddedListeners) p(x,y)
  }
}

class PiccoloChart(modelBounds: Rectangle2D,
                   chartWidth: Double, //doesn't account for ticks and labels, just the chart area itself
                   chartHeight: Double)
        extends PNode {
  private val seriesList = new ArrayBuffer[DataSeries]
  val background = new PhetPPath(new Rectangle2D.Double(0, 0, chartWidth, chartHeight), Color.yellow, new BasicStroke(1), Color.darkGray)
  addChild(background)

  trait SeriesRenderer {
  }
  val transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, chartWidth, chartHeight))
  class LineRenderer(series: DataSeries) extends PhetPPath(new BasicStroke(1), Color.black) {
    def updatePath() = {

      val polyline = for (point <- series.data) yield transform.modelToViewDouble(point.x, point.y)
      if (polyline.length > 0)
        setPathToPolyline(polyline.toArray)
    }
//    series.listeners += (() => updatePath())
    moveTo(0,0)
    series.pointAddedListeners += ((x:Double,y:Double)=>{
      val pt = transform.modelToViewDouble(x,y)
      lineTo(pt.getX.toFloat,pt.getY.toFloat)
    })
    updatePath()
  }
  def addSeries(series: DataSeries) = {
    seriesList += series
    addChild(new LineRenderer(series))
  }
}
object TestPiccoloChart {
  def main(args: Array[String]) {
    runInSwingThread {
      new MyJFrame {
        val serieses = (for (i <- 0 until 10) yield new DataSeries("series "+i)).toList
//        val sineWave = new DataSeries("sine wave")
        contentPane = new SCanvas {
          val chart = this add new PiccoloChart(new Rectangle2D.Double(0, -1, PI, 2), 600, 400) {
          }

          for (s <- serieses) chart.addSeries(s)
//          chart.addSeries(sineWave)
          this add new PText("a ptext")
        }
        setSize(new Dimension(800, 600))
        setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
        setVisible(true)
        var xvalue = 0.0
        val timer = new Timer(30, new ActionListener {
          def actionPerformed(e: ActionEvent) = {
            for (i <- 0 until serieses.length) serieses(i).addPoint(xvalue,sin(xvalue)+i/10.0)
//            sineWave.addPoint(xvalue, sin(xvalue))
            xvalue = xvalue + 0.01
          }
        })
        timer.start()
      }
    }
  }
}

//This feasibility test indicates comparable performance to the PiccoloChart above, when all decorations are disabled.
object TestJFC {
  def main(args: Array[String]) {
    runInSwingThread {
      val dataset = new XYSeriesCollection
//      val series: XYSeries = new XYSeries("series 1")
      val serieses = (for (i <- 0 until 10) yield new XYSeries("series "+i)).toList
      for (s <- serieses) dataset.addSeries(s)
//      dataset.addSeries(series)
      val chart: JFreeChart = ChartFactory.createXYLineChart("xy", "x", "y", dataset, PlotOrientation.VERTICAL, false, false, false)
      chart.getXYPlot.getDomainAxis.setAutoRange(false)
      chart.getXYPlot.getRangeAxis.setAutoRange(false)
      chart.getXYPlot.getDomainAxis.setRange(0, PI)
      chart.getXYPlot.getRangeAxis.setRange(-1, 1)
      chart.getXYPlot.setDomainGridlinesVisible(false)//this gives the biggest performance increase over default
      chart.getXYPlot.setRangeGridlinesVisible(false)
      chart.getXYPlot.getRangeAxis.setTickLabelsVisible(false)
      chart.getXYPlot.getRangeAxis.setTickMarksVisible(false)
      chart.getXYPlot.getDomainAxis.setTickLabelsVisible(false)
      chart.getXYPlot.getDomainAxis.setTickMarksVisible(false)
      val frame = new ChartFrame("test frame", chart)
      frame.setSize(800, 600)
      frame.setVisible(true)
      frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)

      var xvalue = 0.0
      val timer = new Timer(30, new ActionListener {
        def actionPerformed(e: ActionEvent) = {
//          series.add(xvalue, sin(xvalue))
          for (i <- 0 until serieses.length) serieses(i).add(xvalue,sin(xvalue)+i/10.0)
          xvalue = xvalue + 0.01
        }
      })
      timer.start()
    }
  }
}