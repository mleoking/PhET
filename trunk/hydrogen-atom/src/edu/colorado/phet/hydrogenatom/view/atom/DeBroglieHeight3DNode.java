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
    
    private static final int ORBIT_VERTICIES = 200;
    private static final float ORBIT_STROKE_WIDTH = 1f;
    private static final Color ORBIT_FRONT_COLOR = OrbitNodeFactory.getOrbitColor();
    private static final Color ORBIT_BACK_COLOR = ORBIT_FRONT_COLOR.darker().darker();
    
    private static final int WAVE_VERTICIES = 200;
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
    private Vertex3D[] _orbitVerticies; // reusable verticies for orbits
    private Vertex3D[] _waveVerticies; // reusable verticies for wave
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        
        assert( VIEW_ANGLE >= 0 && VIEW_ANGLE <= 90 ); // for view angle animation
        assert( ORBIT_VERTICIES % 2 == 0 ); // for faking dashed line style in 3D
        
        _viewMatrix = new Matrix3D();
        _viewAngle = ( ROTATE_INTO_PLACE ? 0 : VIEW_ANGLE );
        _rotationDone = false;
        
        _orbitVerticies = new Vertex3D[ ORBIT_VERTICIES ];
        for ( int i = 0; i < _orbitVerticies.length; i++ ) {
            _orbitVerticies[i] = new Vertex3D();
        }
        
        _waveVerticies = new Vertex3D[ WAVE_VERTICIES ];
        for ( int i = 0; i < _waveVerticies.length; i++ ) {
            _waveVerticies[i] = new Vertex3D();
        }
        
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
            _staticNode = createStaticNode( getAtom(), _viewMatrix, _orbitVerticies );
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
        _waveNode = create3DWaveNode( getAtom(), _viewMatrix, _waveVerticies );
        addChild( _waveNode );
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
    /*
     * Creates the static parts of the view and flattens everything to a single PImage node.
     */
    private static PNode createStaticNode( DeBroglieModel atom, Matrix3D viewMatrix, Vertex3D[] verticies ) {

        PNode parentNode = new PNode();
        
        // 3D orbits
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = atom.getOrbitRadius( state );
            Wireframe3DNode orbitNode = create3DOrbitNode( radius, viewMatrix, verticies );
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
    private static Wireframe3DNode create3DWaveNode( DeBroglieModel atom, Matrix3D viewMatrix, Vertex3D[] verticies ) {
        
        // Compute the verticies
        getWaveVerticies( atom, verticies );
        
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
    private static Wireframe3DNode create3DOrbitNode( final double radius, Matrix3D viewMatrix, Vertex3D[] verticies ) {
        
        // Compute the verticies
        getOrbitVerticies( radius, verticies );
        
        // Create the wireframe model
        Wireframe3D wireframe = new Wireframe3D( verticies );
        // Connect every-other pair of verticies to simulate a dashed line.
        for ( int i = 0; i < verticies.length - 1; i += 2 ) {
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
    // Verticies computation
    //----------------------------------------------------------------------------
    
    /*
     * Gets the verticies that approximate the standing wave.
     */
    private static Vertex3D[] getWaveVerticies( DeBroglieModel atom, Vertex3D[] verticies ) {
        
        int numberOfVerticies = verticies.length;
        double deltaAngle = ( 2 * Math.PI ) / numberOfVerticies;
        double radius = atom.getElectronOrbitRadius();
        
        for ( int i = 0; i < numberOfVerticies; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = MAX_HEIGHT * atom.getAmplitude( angle );
            verticies[i].setLocation( (float) x, (float) y, (float) z );
        }
        
        return verticies;
    }

    /*
     * Gets the verticies that approximate an orbit.
     */
    private static Vertex3D[] getOrbitVerticies( double radius, Vertex3D[] verticies ) {
        
        int numberOfVerticies = verticies.length;
        double deltaAngle = ( 2 * Math.PI ) / numberOfVerticies;
        
        for ( int i = 0; i < numberOfVerticies; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = 0;
            verticies[i].setLocation( (float) x, (float) y, (float) z );
        }
        
        return verticies;
    }
}