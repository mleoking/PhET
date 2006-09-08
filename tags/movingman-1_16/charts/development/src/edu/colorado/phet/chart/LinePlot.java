/**
 * Class: LinePlot
 * Package: edu.colorado.phet.chart
 * Author: Another Guy
 * Date: Sep 15, 2004
 */
package edu.colorado.phet.chart;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class LinePlot extends DataSetGraphic {
    private GeneralPath generalPath;
    private PhetShapeGraphic phetShapeGraphic;

    public LinePlot( Component component, Chart chart, DataSet dataSet ) {
        this( component, chart, dataSet, new BasicStroke( 1 ), Color.black );
    }

    public LinePlot( Component component, final Chart chart, DataSet dataSet, Stroke stroke, Paint paint ) {
        super( component, chart, dataSet );
        phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, stroke, paint );
        addGraphic( phetShapeGraphic );
        setClip( chart.getChartBounds() );
        chart.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                setClip( chart.getChartBounds() );
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
            }
        } );
    }

    public void pointAdded( Point2D point ) {
        if( point == null ) {
            throw new RuntimeException( "Null point" );
        }
        Point viewLocation = getChart().transform( point );
        if( generalPath == null ) {
            generalPath = new GeneralPath();
            generalPath.moveTo( viewLocation.x, viewLocation.y );
            phetShapeGraphic.setShape( generalPath );
        }
        else {
            //Determine the exact region for repaint.
            generalPath.lineTo( (float)viewLocation.getX(), (float)viewLocation.getY() );
            phetShapeGraphic.setBoundsDirty();
            phetShapeGraphic.autorepaint();
        }
    }

    public void cleared() {
        phetShapeGraphic.setShape( null );
        generalPath = null;
    }

    public void transformChanged() {
        generalPath = null;
        phetShapeGraphic.setShape( null );
        addAllPoints();
    }

}