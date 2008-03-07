/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.BoundGraphic;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * 
 * AlphaRadiationChart - This class displays the chart at the bottom of the
 * Alpha Radiation tab in this sim.  The chart shows the interaction between
 * the alpha particles and the energy barrier.
 *
 * @author John Blanco
 */
public class AlphaRadiationChart extends PNode {

    // Chart border.
    private PPath _borderNode;

    public AlphaRadiationChart(PCanvas parent) {
        
        Rectangle parentBounds = parent.getBounds();

        // Set the bounds for this node.
        setBounds(10, parentBounds.getHeight() * 0.75f, parentBounds.getWidth() - 20, parentBounds.getHeight() * 0.25f);
        
        // Put a boundary around this node.
        BoundGraphic boundGraphic = new BoundGraphic( this, 5, 5 );
        boundGraphic.setStroke( new BasicStroke() );
        boundGraphic.setWidth( 5 );
        boundGraphic.setStrokePaint( Color.black );
        boundGraphic.setPaint( Color.white );
        addChild( boundGraphic );
        
    }

}