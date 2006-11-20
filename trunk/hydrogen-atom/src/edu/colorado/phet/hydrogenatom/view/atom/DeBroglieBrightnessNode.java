/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.atom;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglie2DViewStrategy;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DeBroglieBrightnessNode represents the deBroglie model
 * as a standing wave. The amplitude (-1...+1) of the standing
 * wave is represented by the brightness of color in a ring that 
 * is positioned at the electron's orbit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class DeBroglieBrightnessNode extends AbstractDeBroglie2DViewStrategy {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double RING_WIDTH = 10;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _ringNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieBrightnessNode( DeBroglieModel atom ) {
        super( atom );
        _ringNode = new PNode();
        addChild( _ringNode );
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglie2DViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * In this case, the view is a ring, located at the electron's orbit.
     * The ring is colored by mapping amplitudes of the standing wave
     * at points on the ring to colors.
     */
    public void update() {

        _ringNode.removeAllChildren();

        DeBroglieModel atom = getAtom();
        int steps = 36;
        double radius = atom.getElectronOrbitRadius();

        for ( int i = 0; i < steps; i++ ) {

            double angle = ( 2 * Math.PI ) * ( (double) i / steps );
            double amplitude = atom.getAmplitude( angle );
            Color color = amplitudeToColor( amplitude );

            double diameter = 15;
            Shape shape = new Ellipse2D.Double( -diameter/2, -diameter/2, diameter, diameter );

            PPath path = new PPath( shape );
            path.setPaint( color );
            path.setStroke( null );
            _ringNode.addChild( path );

            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            path.setOffset( x, y );
        }
    }
    
    /**
     * Maps the magnitude of the specified amplitude to a color.
     * May be overridden by subclasses to provide different representations.
     * 
     * @param amplitude
     * @return Color
     */
    protected Color amplitudeToColor( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= 1 );
        return Color.RED;//XXX
    }
}
