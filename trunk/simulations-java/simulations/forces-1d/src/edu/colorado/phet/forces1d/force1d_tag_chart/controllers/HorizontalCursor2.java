/*PhET, 2004.*/
package edu.colorado.phet.forces1d.force1d_tag_chart.controllers;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.forces1d.force1d_tag_chart.Chart;

/**
 * User: Sam Reid
 * Date: Jul 1, 2003
 * Time: 9:02:00 AM
 */
public class HorizontalCursor2 extends CompositePhetGraphic {

    private ArrayList listeners = new ArrayList();
    private double minX = Double.NEGATIVE_INFINITY;
    private double maxX = Double.POSITIVE_INFINITY;

    private Chart chart;
    private double modelX = 0;
    private int width;
    private Stroke stroke = new BasicStroke( 1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 2, new float[]{6, 4}, 0 );
    private Rectangle shape = null;
    private PhetShapeGraphic shapeGraphic;

    public HorizontalCursor2( Component component, final Chart chart, Color fillColor, Color outlineColor, int width ) {
        super( component );

        this.chart = chart;
        this.width = width;
        shape = new Rectangle();
        setCursorHand();
        addMouseInputListener( new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                double newX = chart.getModelViewTransform().viewToModelX( e.getX() );
                newX = Math.max( newX, chart.getRange().getMinX() );
                newX = Math.min( newX, chart.getRange().getMaxX() );
                newX = Math.max( newX, minX );
                newX = Math.min( newX, maxX );
                if ( newX != modelX ) {
                    setModelX( newX );
                    for ( int i = 0; i < listeners.size(); i++ ) {
                        Listener listener = (Listener) listeners.get( i );
                        listener.modelValueChanged( newX );
                    }
                }
            }
        } );
        chart.addListener( new Chart.Listener() {
            public void transformChanged( Chart chart ) {
                update();
            }
        } );
        shapeGraphic = new PhetShapeGraphic( component, null, fillColor, stroke, outlineColor );
        addGraphic( shapeGraphic );
        update();
    }

    public void setX( double x ) {
        setModelX( x );
    }

    public double getMaxX() {
        return maxX;
    }

    public interface Listener {
        public void modelValueChanged( double modelX );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setMinX( double minX ) {
        this.minX = minX;
    }

    public void setMaxX( double maxX ) {
        this.maxX = maxX;
    }

    public void update() {
        int xCenter = chart.getModelViewTransform().modelToViewX( modelX );
        int x = xCenter - width / 2;
        int y = chart.getViewBounds().y;
        int height = chart.getViewBounds().height;
        shape.setBounds( x, y, width, height );
        shapeGraphic.setShape( shape );
    }

    public void setModelX( double modelX ) {
        this.modelX = modelX;
        update();
    }

}
