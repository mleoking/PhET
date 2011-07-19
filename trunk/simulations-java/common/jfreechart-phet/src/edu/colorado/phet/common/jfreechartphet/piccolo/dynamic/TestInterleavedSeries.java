// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.jfreechartphet.piccolo.dynamic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/**
 * Demonstration of usage and behavior of JFreeChartNode with multiple series.
 *
 * @author Sam Reid
 */
public class TestInterleavedSeries extends TestDynamicJFreeChartNode {

    public TestInterleavedSeries() {

        getDynamicJFreeChartNode().addSeries( "Series 1", Color.green );
        getDynamicJFreeChartNode().addSeries( "Series 2", Color.red );
        getDynamicJFreeChartNode().addSeries( "Series 3", Color.black );
        PhetPPath phetPPath = new PhetPPath( new Rectangle( 0, 0, 100, 100 ), new BasicStroke( 2 ), Color.green );
        phetPPath.addInputEventListener( new PDragEventHandler() );
        phetPPath.addInputEventListener( new CursorHandler() );
        getPhetPCanvas().addScreenChild( phetPPath );
    }

    protected void updateGraph() {
        double t = ( System.currentTimeMillis() - super.getInitialTime() ) / 1000.0;
        double frequency = 1.0 / 10.0;
        double sin = Math.sin( t * 2 * Math.PI * frequency );
        Point2D.Double pt = new Point2D.Double( t / 100.0, sin );

        getDynamicJFreeChartNode().addValue( 0, pt.getX(), pt.getY() );
        getDynamicJFreeChartNode().addValue( 1, pt.getX(), pt.getY() * 0.9 );
        getDynamicJFreeChartNode().addValue( 2, pt.getX(), pt.getY() * 0.8 );
        getDynamicJFreeChartNode().addValue( 3, pt.getX(), pt.getY() * 1.5 );
    }

    public static void main( String[] args ) {
        new TestInterleavedSeries().start();
    }
}
