// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.java2d;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.platetectonics.model.BlockCrustPlateModel;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

/**
 * @author Sam Reid
 */
public class TestDensityRenderer extends PNode {
    public TestDensityRenderer( PlateModel plateModel ) {
        Rectangle2D.Double modelRect = new Rectangle2D.Double( -10000, -10000, 20000, 20000 );
        Rectangle2D.Double viewRect = new Rectangle2D.Double( 0, 0, 800, 800 );
        ModelViewTransform transform = ModelViewTransform.createRectangleInvertedYMapping( modelRect, viewRect );
        double cellWidth = 250;
        double cellHeight = 250;
        for ( double x = modelRect.getMinX(); x <= modelRect.getMaxX(); x += cellWidth ) {
            for ( double y = modelRect.getMinY(); y <= modelRect.getMaxY(); y += cellHeight ) {
                if ( y <= plateModel.getElevation( x, 0 ) ) {
                    double density = plateModel.getDensity( x, y );
                    double temperature = plateModel.getTemperature( x, y );
                    final Shape viewShape = transform.modelToView( new Rectangle2D.Double( x, y, cellWidth, cellHeight ) );
                    PhetPPath cell = new PhetPPath( RectangleUtils.expand( viewShape.getBounds2D(), 2, 2 ), getColor( density, temperature ) );
                    addChild( cell );
                }
            }
        }
    }

    private Paint getColor( double density, double temperature ) {
        //When surface density and temperature, use clay color
        Color clay = new Color( 255, 222, 156 );

        //When it gets hotter, turn down the G & B channels to make redder

//        System.out.println( "density = " + density );
        double minDensityToShow = 3000;
        double maxDensityToShow = 3100;
        double x = new Function.LinearFunction( minDensityToShow, maxDensityToShow, 255, 100 ).evaluate( density );
        int d = (int) MathUtil.clamp( 0, x, 255 );

//        System.out.println( "temperature = " + temperature );

        int tempChannel = (int) new Function.LinearFunction( 280, 300, d, 255 ).evaluate( temperature );
        return new Color( tempChannel, d, d );
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new JFrame( "Test density renderer" ) {{
                    setContentPane( new PhetPCanvas() {{
                        setBackground( Color.blue );
                        addScreenChild( new TestDensityRenderer( new BlockCrustPlateModel() ) );
                        setPanEventHandler( new PPanEventHandler() );
                        setZoomEventHandler( new PZoomEventHandler() );
                    }} );
                    setSize( 1024, 768 );
                }}.setVisible( true );
            }
        } );
    }
}