// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.HAModelViewTransform;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglie2DViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * DeBroglieBrightnessNode represents the deBroglie model
 * as a standing wave. The amplitude (-1...+1) of the standing
 * wave is represented by the brightness of color in a ring that 
 * is positioned at the electron's orbit. The ring is approximated
 * using a set of polygons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieBrightnessNode extends AbstractDeBroglie2DViewStrategy {
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    // radial width of the ring representation,
    // public because it's used by collision detection code in the model
    public static final double RING_WIDTH = 5;
  
    // color used when amplitude = +1
    public static Color PLUS_COLOR = ElectronNode.getColor();
    // color used when amplitude = -1
    public static Color MINUS_COLOR = Color.BLACK;
    // color used when amplitude = 0
    public static Color ZERO_COLOR = ColorUtils.interpolateRBGA( MINUS_COLOR, PLUS_COLOR, 0.5 );
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    // distance along the ring's circumference that each polygon occupies
    private static final double POLYGON_SIZE = 3; // in view distance units
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _ringNode; // parent node for all geometry that approximates the ring
    private ArrayList _polygons; // array of PPath, polygons used to approximate the ring
    private int _previousState; // previous state of the atom's electron
    private GeneralPath[] _pathShapes; // reusable shapes used to construct the ring
    private PPath[] _pathNodes; // reusable nodes used to construct the ring
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieBrightnessNode( DeBroglieModel atom ) {
        super( atom );
        
        _ringNode = new PNode();
        _polygons = new ArrayList();
        _previousState = DeBroglieModel.getGroundState() - 1; // force initialization
        addChild( _ringNode );
        
        // Create the maximum number of objects we'll need to draw the larges orbit
        int maxState = DeBroglieModel.getGroundState() + DeBroglieModel.getNumberOfStates() - 1;
        double maxRadius = HAModelViewTransform.transform( DeBroglieModel.getOrbitRadius( maxState ) );
        int maxPolygons = calculateNumberOfPolygons( maxRadius );
        createReusablePolygons( maxPolygons );
        
        update();
    }
    
    /*
     * Creates reusable objects that are used to construct the ring.
     */
    private void createReusablePolygons( int numberOfPolygons ) {
        _pathShapes = new GeneralPath[ numberOfPolygons ];
        _pathNodes = new PPath[ numberOfPolygons ];
        for ( int i = 0; i < numberOfPolygons; i++ ) {
            _pathShapes[i] = new GeneralPath();
            _pathNodes[i] = new PPath();
            _pathNodes[i].setStroke( null );
        }
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

        int state = getAtom().getElectronState();
        
        // Update the ring's geometry when the electron's state changes.
        if ( state != _previousState ) {
            _previousState = state;
            double radius = HAModelViewTransform.transform( getAtom().getElectronOrbitRadius() );
            int numberOfPolygons = calculateNumberOfPolygons( radius );
            updateRingGeometry( numberOfPolygons );
        }

        // Use amplitude to set the color of each polygon in the ring.
        updateRingColors();
    }
    
    /*
     * Calculates the number of polygons required to approximate the ring.
     * This is a function of the ring's radius.
     */
    private static int calculateNumberOfPolygons( double radius ) {
        double circumference = Math.PI * ( 2 * radius );
        int numberOfPolygons = (int) ( circumference / POLYGON_SIZE ) + 1;
        return numberOfPolygons;
    }
    
    /*
     * Updates the ring's geometry, approximated using polygons
     * @param numberOfPolygons
     */
    private void updateRingGeometry( int numberOfPolygons ) {
        
        _ringNode.removeAllChildren();
        _polygons.clear();

        double radius = HAModelViewTransform.transform( getAtom().getElectronOrbitRadius() );
        
        if ( numberOfPolygons > _pathNodes.length ) {
            System.err.println( "WARNING: DebroglieBrightnessNode.updateRingGeometry needed to allocate more objects to create ring geometry" );
            createReusablePolygons( numberOfPolygons );
        }
        
        for ( int i = 0; i < numberOfPolygons; i++ ) {

            double a1 = ( 2 * Math.PI ) * ( (double) i / numberOfPolygons );
            double a2 = a1 + ( 2 * Math.PI / numberOfPolygons ) + 0.001; // overlap!
            double r1 = radius - ( RING_WIDTH / 2 );
            double r2 = radius + ( RING_WIDTH / 2 );
            double cos1 = Math.cos( a1 );
            double sin1 = Math.sin( a1 );
            double cos2 = Math.cos( a2 );
            double sin2 = Math.sin( a2 );
            
            // Points that define the polygon
            double x1 = r1 * cos1;
            double y1 = r1 * sin1;
            double x2 = r2 * cos1;
            double y2 = r2 * sin1;
            double x3 = r2 * cos2;
            double y3 = r2 * sin2;
            double x4 = r1 * cos2;
            double y4 = r1 * sin2;
            
            // Shape for the polygon
            GeneralPath path = _pathShapes[i];
            path.reset();
            path.moveTo( (float) x1, (float) y1 );
            path.lineTo( (float) x2, (float) y2 );
            path.lineTo( (float) x3, (float) y3 );
            path.lineTo( (float) x4, (float) y4 );
            path.closePath();

            // Draw the polygon using a node
            PPath pathNode = _pathNodes[i];
            pathNode.setPathTo( path );
            pathNode.setPaint( Color.RED ); // so we can see if any polgons don't get paint set properly
            
            _ringNode.addChild( pathNode );
            _polygons.add( pathNode );
        }
    }
    
    /*
     * Updates the colors of the ring's polygons to match 
     * the amplitude of the standing wave.
     */
    private void updateRingColors() {
        // Use amplitude to set the color of each polygon in the ring.
        int numberOfPolygons = _polygons.size();
        for ( int i = 0; i < numberOfPolygons; i++ ) {

            double angle = ( 2 * Math.PI ) * ( (double) i / numberOfPolygons );
            double amplitude = getAtom().getAmplitude( angle );
            Color color = amplitudeToColor( amplitude );

            PPath pathNode = (PPath)_polygons.get( i );
            pathNode.setPaint( color );
        }
    }
    
    /**
     * Maps the specified amplitude to a color.
     * May be overridden by subclasses to provide different representations.
     * 
     * @param amplitude
     * @return Color
     */
    protected Color amplitudeToColor( double amplitude ) {
        assert( amplitude >= -1 && amplitude <= 1 );
        Color color = null;
        if ( amplitude > 0 ) {
            color = ColorUtils.interpolateRBGA( ZERO_COLOR, PLUS_COLOR, amplitude );
        }
        else {
            color = ColorUtils.interpolateRBGA( ZERO_COLOR, MINUS_COLOR, -amplitude );
        }
        return color;
    }
}
