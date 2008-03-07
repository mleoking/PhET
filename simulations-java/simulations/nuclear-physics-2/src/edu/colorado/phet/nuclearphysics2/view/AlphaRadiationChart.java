/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;

import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;


/**
 *
 * AlphaRadiationChart - This class displays the chart at the bottom of the
 * Alpha Radiation tab in this sim.  The chart shows the interaction between
 * the alpha particles and the energy barrier.
 *
 * @author John Blanco
 */
public class AlphaRadiationChart extends PComposite {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Constants for controlling look and feel.
    private static final Color BORDER_COLOR = Color.gray;
    private static final float BORDER_STROKE_WIDTH = 10f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );
    private static final Color BACKGROUND_COLOR = Color.white;
    private static final double SCREEN_FRACTION_Y = 0.4d;

    // Chart border.
    private PPath _borderNode;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationChart(double width, double height) {

        _borderNode = new PPath( );
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );

        updateBounds(width, height );
    }

    private void updateBounds(double width,double height) {
        _borderNode.setPathTo( new RoundRectangle2D.Double( 
                BORDER_STROKE_WIDTH,
                height-(height*SCREEN_FRACTION_Y),
                width-(BORDER_STROKE_WIDTH*2),
                height*SCREEN_FRACTION_Y-BORDER_STROKE_WIDTH,
                20,
                20 ) );
    }

    public void componentResized( double width, double height ) {
        updateBounds( width, height );
    }
}