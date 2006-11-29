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

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglieViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.colorado.phet.hydrogenatom.wireframe.Matrix3D;
import edu.colorado.phet.hydrogenatom.wireframe.Vertex3D;
import edu.colorado.phet.hydrogenatom.wireframe.Wireframe3D;
import edu.colorado.phet.hydrogenatom.wireframe.Wireframe3DNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * DeBroglieHeight3DNode represents the deBroglie model as a 3-D standing wave,
 * where height of the wave represents amplitude.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieHeight3DNode extends AbstractDeBroglieViewStrategy {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Setting this to true cause the wireframe to rotate into place
    private static final boolean ROTATE_INTO_PLACE = true;
    
    private static final double MAX_HEIGHT = 15; // screen coordinates
    private static final double VIEW_ANGLE = 70; // degrees, rotation about the x-axis
    private static final double VIEW_ANGLE_DELTA = 5; // degrees
    
    private static final int ORBIT_POINTS = 200;
    private static final float ORBIT_STROKE_WIDTH = 1f;
    private static final Color ORBIT_FRONT_COLOR = OrbitNodeFactory.getOrbitColor();
    private static final Color ORBIT_BACK_COLOR = ORBIT_FRONT_COLOR.darker().darker();
    
    private static final int WAVE_POINTS = 200;
    private static final float WAVE_STROKE_WIDTH = 2f;
    private static final Color WAVE_FRONT_COLOR = ElectronNode.getColor();
    private static final Color WAVE_BACK_COLOR = WAVE_FRONT_COLOR.darker().darker();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _staticNode;
    private Wireframe3DNode _waveNode;
    private Matrix3D _viewMatrix; // matrix used to set view angle
    private double _viewAngle; // the current view angle
    private boolean _rotationDone; // are we done rotating the wireframe into place?
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        assert( VIEW_ANGLE >= 0 && VIEW_ANGLE <= 90 );
        _viewMatrix = new Matrix3D();
        _viewAngle = ( ROTATE_INTO_PLACE ? 0 : VIEW_ANGLE );
        _rotationDone = false;
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglieViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        
        if ( !_rotationDone ) {
            
            _viewMatrix.unit();
            _viewMatrix.xrot( _viewAngle );
            
            if ( _staticNode != null ) {
                removeChild( _staticNode );
            }
            _staticNode = createStaticNode( getAtom(), _viewMatrix );
            _staticNode.setOffset( -_staticNode.getWidth() / 2, -_staticNode.getHeight() / 2 );
            addChild( _staticNode );
            
            if ( _viewAngle == VIEW_ANGLE ) {
                _rotationDone = true;
            }
            else {
                _viewAngle += VIEW_ANGLE_DELTA;
                if ( _viewAngle > VIEW_ANGLE ) {
                    _viewAngle = VIEW_ANGLE;
                }
            }
        }
        
        if ( _waveNode != null ) {
            removeChild( _waveNode );
        }
        _waveNode = create3DWaveNode( getAtom(), _viewMatrix );
        addChild( _waveNode );
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Creates the static parts of the view and flattens everything to a single PImage node.
     */
    private static PNode createStaticNode( DeBroglieModel atom, Matrix3D viewMatrix ) {

        PNode parentNode = new PNode();
        
        // 3D orbits
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = atom.getOrbitRadius( state );
            Wireframe3DNode orbitNode = create3DOrbitNode( ORBIT_POINTS, radius, viewMatrix );
            parentNode.addChild( orbitNode );
        }
        
        // Proton
        ProtonNode protonNode = new ProtonNode();
        protonNode.setOffset( 0, 0 );
        parentNode.addChild( protonNode );
        
        // Convert all static nodes to a single image
        PImage imageNode = new PImage( parentNode.toImage() );
        
        return imageNode;
    }
    
    /*
     * Creates a 3D wave node.
     */
    private static Wireframe3DNode create3DWaveNode( DeBroglieModel atom, Matrix3D viewMatrix ) {
        
        // Compute the verticies
        Vertex3D[] verticies = getWaveVerticies( WAVE_POINTS, atom );
        
        // Create the wireframe model
        Wireframe3D wireframe = new Wireframe3D( verticies );
        for ( int i = 0; i < verticies.length - 1; i++ ) {
            wireframe.addLine( i, i + 1 );
        }
        wireframe.addLine( verticies.length - 1, 0 ); // close the path
        wireframe.done();
        wireframe.setColors( WAVE_FRONT_COLOR, WAVE_BACK_COLOR );
        wireframe.setStrokeWidth( WAVE_STROKE_WIDTH );
        
        // Transform the model
        Matrix3D matrix = wireframe.getMatrix();
        matrix.unit();
        float xt = -( wireframe.getXMin() + wireframe.getXMax() ) / 2;
        float yt = -( wireframe.getYMin() + wireframe.getYMax() ) / 2;
        float zt = -( wireframe.getZMin() + wireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( viewMatrix );
        wireframe.setTransformed( false );
        
        return new Wireframe3DNode( wireframe );
    }
    
    /*
     * Creates a 3D orbit node.
     */
    private static Wireframe3DNode create3DOrbitNode( final int numberOfVerticies, final double radius, Matrix3D viewMatrix ) {
        
        // This algorithm requires an even number of verticies.
        int n = numberOfVerticies;
        if ( n % 2 != 0 ) {
            n += 1;
        }
        
        // Compute the verticies
        Vertex3D[] verticies = getOrbitVerticies( n, radius );
        
        // Create the wireframe model
        Wireframe3D wireframe = new Wireframe3D( verticies );
        // Connect every-other pair of verticies to simulate a dashed line.
        for ( int i = 0; i < n - 1; i += 2 ) {
            wireframe.addLine( i, i + 1 );
        }
        wireframe.done();
        wireframe.setColors( ORBIT_FRONT_COLOR, ORBIT_BACK_COLOR );
        wireframe.setStrokeWidth( ORBIT_STROKE_WIDTH );
        
        // Transform the model
        Matrix3D matrix = wireframe.getMatrix();
        matrix.unit();
        float xt = -( wireframe.getXMin() + wireframe.getXMax() ) / 2;
        float yt = -( wireframe.getYMin() + wireframe.getYMax() ) / 2;
        float zt = -( wireframe.getZMin() + wireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( viewMatrix );
        wireframe.setTransformed( false );
        
        return new Wireframe3DNode( wireframe );
    }
    
    //----------------------------------------------------------------------------
    // private static
    //----------------------------------------------------------------------------
    
    /*
     * Gets the verticies that approximate the standing wave.
     * The number of verticies returned will be numberOfVerticies+1,
     * since an extra vertex is added to close the ends of the wave.
     */
    private static Vertex3D[] getWaveVerticies( int numberOfVerticies, DeBroglieModel atom ) {
        double radius = atom.getElectronOrbitRadius();
        double deltaAngle = ( 2 * Math.PI ) / numberOfVerticies;
        Vertex3D[] verticies = new Vertex3D[numberOfVerticies];
        for ( int i = 0; i < numberOfVerticies; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = MAX_HEIGHT * atom.getAmplitude( angle );
            Vertex3D v = new Vertex3D( (float) x, (float) y, (float) z );
            verticies[i] = v;
        }
        return verticies;
    }

    /*
     * Gets the verticies that approximate an orbit.
     */
    private static Vertex3D[] getOrbitVerticies( int numberOfVerticies, double radius ) {
        Vertex3D[] verticies = new Vertex3D[numberOfVerticies];
        double deltaAngle = ( 2 * Math.PI ) / numberOfVerticies;
        for ( int i = 0; i < numberOfVerticies; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = 0;
            Vertex3D v = new Vertex3D( (float) x, (float) y, (float) z );
            verticies[i] = v;
        }
        return verticies;
    }
}