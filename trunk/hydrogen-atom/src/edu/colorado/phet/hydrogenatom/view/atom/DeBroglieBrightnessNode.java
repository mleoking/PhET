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
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

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
    
    private static final double RING_WIDTH = 5;
    private static final double RING_STEP_SIZE = 3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _ringNode;
    private ArrayList _ringGeometry; // array of PPath
    private int _previousState;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieBrightnessNode( DeBroglieModel atom ) {
        super( atom );
        _ringNode = new PNode();
        _ringGeometry = new ArrayList();
        _previousState = DeBroglieModel.getGroundState() - 1; // force initialization
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

        int steps = getRingSteps();
        int state = getAtom().getElectronState();
        
        if ( state != _previousState ) {
            _previousState = state;
            updateRingGeometry( steps );
        }

        for ( int i = 0; i < steps; i++ ) {

            double angle = ( 2 * Math.PI ) * ( (double) i / steps );
            double amplitude = getAtom().getAmplitude( angle );
            Color color = amplitudeToColor( amplitude );

            PPath pathNode = (PPath)_ringGeometry.get( i );
            pathNode.setPaint( color );
        }
    }
    
    private int getRingSteps() {
        DeBroglieModel atom = getAtom();
        double radius = atom.getElectronOrbitRadius();
        double circumference = Math.PI * ( 2 * radius );
        int steps = (int) ( circumference / RING_STEP_SIZE ) + 1;
        return steps;
    }
    
    private void updateRingGeometry( int steps ) {
        
        _ringNode.removeAllChildren();
        _ringGeometry.clear();

        double radius = getAtom().getElectronOrbitRadius();
        for ( int i = 0; i < steps; i++ ) {

            double angle = ( 2 * Math.PI ) * ( (double) i / steps );

            double r1 = radius - ( RING_WIDTH / 2 );
            double r2 = radius + ( RING_WIDTH / 2 );
            double cos1 = Math.cos( angle );
            double sin1 = Math.sin( angle );
            double a2 = angle + ( 2 * Math.PI / steps ) + 0.001;
            double cos2 = Math.cos( a2 );
            double sin2 = Math.sin( a2 );
            double x1 = r1 * cos1;
            double y1 = r1 * sin1;
            double x2 = r2 * cos1;
            double y2 = r2 * sin1;
            double x3 = r2 * cos2;
            double y3 = r2 * sin2;
            double x4 = r1 * cos2;
            double y4 = r1 * sin2;
            
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) x1, (float) y1 );
            path.lineTo( (float) x2, (float) y2 );
            path.lineTo( (float) x3, (float) y3 );
            path.lineTo( (float) x4, (float) y4 );
            path.closePath();

            PPath pathNode = new PPath( path );
            pathNode.setStroke( null );
            _ringNode.addChild( pathNode );
            _ringGeometry.add( pathNode );
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
