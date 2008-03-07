/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.BoundGraphic;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
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


    // Chart border.
    private PPath _borderNode;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AlphaRadiationChart(double width, double height) {

        _borderNode = new PPath( ); // origin at center
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( BACKGROUND_COLOR );
        addChild( _borderNode );

        updateBounds(width, height );
    }

    private void updateBounds(double width,double height) {
        _borderNode.setPathTo( new RoundRectangle2D.Double( 0,height/2,width-40,height/4-20,20,20 ) );
    }

    public void componentResized( double width, double height ) {
        updateBounds( width, height );
    }
}