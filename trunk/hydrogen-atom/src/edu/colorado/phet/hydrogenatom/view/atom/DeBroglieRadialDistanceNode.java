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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglie2DViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DeBroglieRadialDistanceNode represents the deBroglie model
 * as a standing wave whose amplitude is proportional to the
 * radial distance from the electron's orbit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieRadialDistanceNode extends AbstractDeBroglie2DViewStrategy {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // multiply the orbit radius by this number to determine how much to add to radius when amplitude=1 
    public static double RADIAL_OFFSET_FACTOR = 0.15;
    
    // number of line segments used to approximate the ring
    private static final int NUMBER_OF_SEGMENTS = 200;
    
    private static final Color COLOR = ElectronNode.getColor();
    private static final Stroke STROKE = new BasicStroke( 2f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PPath _ringNode;
    private GeneralPath _ringPath;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieRadialDistanceNode( DeBroglieModel atom ) {
        super( atom );
        _ringPath = new GeneralPath();
        _ringNode = new PPath();
        _ringNode.setStrokePaint( COLOR );
        _ringNode.setStroke( STROKE );
        addChild( _ringNode );
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglieViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * In this case, the view is a standing wave whose amplitude
     * is proportional to the radial distance from the electron's orbit.
     */
    public void update() {

        _ringPath.reset();
        
        DeBroglieModel atom = getAtom();
        double radius = atom.getElectronOrbitRadius();

        for ( int i = 0; i < NUMBER_OF_SEGMENTS; i++ ) {

            double angle = ( 2 * Math.PI ) * ( (double) i / NUMBER_OF_SEGMENTS );
            double amplitude = atom.getAmplitude( angle );

            double maxRadialOffset = RADIAL_OFFSET_FACTOR * radius;
            double radialOffset = maxRadialOffset * amplitude;
            float x = (float) ( ( radius + radialOffset ) * Math.cos( angle ) );
            float y = (float) ( ( radius + radialOffset ) * Math.sin( angle ) );
            if ( i == 0 ) {
                _ringPath.moveTo( x, y );
            }
            else {
                _ringPath.lineTo( x, y );
            }
        }
        _ringPath.closePath();
        _ringNode.setPathTo( _ringPath );
    }
}