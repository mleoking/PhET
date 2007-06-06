package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

import org.jfree.chart.JFreeChart;

/**
 * This class extends the functionality of JFreeChartNode by providing different strategies for rendering the data.
 * It is assumed that the chart's plot is XYPlot, and some functionality is lost in rendering, since we
 * have our own rendering strategies here.  Also, the supplied chart's data is not rendered; only the data
 * set explicitly with addValue() to this DynamicJFreeChartNode.
 * <p/>
 * Data is added to the chart through addValue() methods, not through the underlying XYSeriesCollection dataset.
 * <p/>
 * The 3 rendering styles are:
 * 1. JFreeChart renderer: This uses the JFreeChart subsystem to do all the data rendering.
 * This looks beautiful and comes built-in to jfreechart but has performance problems during dynamic data display, since the entire region
 * is repainted whenever a single point changes.
 * <p/>
 * 2. Piccolo renderer: This uses a PPath to render the path as a PNode child of the JFreeChartNode
 * This looks fine and reduces the clip region necessary for painting.  When combined with a buffered jfreechartnode, this can improve computation substantially.
 * This renderer is combined with a PClip to ensure no data is drawn outside the chart's data area (which could otherwise change the fullbounds of the jfreechartnode..
 * <p/>
 * 3. Buffered renderer: This draws directly to the buffer in the JFreeChartNode, and only repaints the changed region of the screen.
 * <p/>
 * 4. Buffered Immediate: This draws directly to the buffer,
 * and immediately repaints the dirty region so that multiple regions don't accumulate in the RepaintManager.
 *
 * @author Sam Reid
 * @version $Revision: 15634 $
 */
public class DynamicJFreeChartNode2 extends JFreeChartNode {

    public DynamicJFreeChartNode2( PhetPCanvas phetPCanvas, JFreeChart chart ) {
        super( chart );
    }

    public void setBuffered( boolean buffered ) {
        super.setBuffered( buffered );
        clearBufferAndRepaint();
    }

}
