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
import edu.colorado.phet.hydrogenatom.view.ModelViewTransform;
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
    private static final double FINAL_VIEW_ANGLE = 70; // degrees, rotation about the x-axis
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
    private double _currentViewAngle; // the current view angle
    private boolean _finalViewAngleDrawn; // are we done rotating the wireframe into place?
    private Vertex3D[] _orbitVerticies; // reusable verticies for orbits
    private Vertex3D[] _waveVerticies; // reusable verticies for wave
    private Wireframe3D _waveWireframe; // reusable wireframe for wave
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        
        assert( FINAL_VIEW_ANGLE >= 0 && FINAL_VIEW_ANGLE <= 90 ); // for view angle animation
        assert( ORBIT_VERTICIES % 2 == 0 ); // for faking dashed line style in 3D
        
        _currentViewAngle = ( ROTATE_INTO_PLACE ? 0 : FINAL_VIEW_ANGLE );
        _finalViewAngleDrawn = false;
        _viewMatrix = new Matrix3D();
        _viewMatrix.xrot( _currentViewAngle );
        
        _orbitVerticies = new Vertex3D[ ORBIT_VERTICIES ];
        for ( int i = 0; i < _orbitVerticies.length; i++ ) {
            _orbitVerticies[i] = new Vertex3D();
        }
        
        _waveVerticies = new Vertex3D[ WAVE_VERTICIES ];
        for ( int i = 0; i < _waveVerticies.length; i++ ) {
            _waveVerticies[i] = new Vertex3D();
        }
        
        _waveWireframe = new Wireframe3D();
        _waveWireframe.setColors( WAVE_FRONT_COLOR, WAVE_BACK_COLOR );
        _waveWireframe.setStrokeWidth( WAVE_STROKE_WIDTH );
        
        _staticNode = new PNode();
        addChild( _staticNode );
        
        _waveNode = new Wireframe3DNode( _waveWireframe );
        addChild( _waveNode );
        
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglieViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        updateWaveNode();
        if ( !_finalViewAngleDrawn ) {
            updateStaticNode();
            updateViewMatrix();
        }
    }
    
    //----------------------------------------------------------------------------
    // Private updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the static parts of the view and flattens them to a PImage node.
     */
    private void updateStaticNode() {

        _staticNode.removeAllChildren();
        
        PNode parentNode = new PNode();
        {
            // 3D orbits
            int groundState = getAtom().getGroundState();
            int numberOfStates = getAtom().getNumberOfStates();
            for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
                double radius = ModelViewTransform.transform( getAtom().getOrbitRadius( state ) );
                Wireframe3DNode orbitNode = create3DOrbitNode( radius, _viewMatrix, _orbitVerticies );
                parentNode.addChild( orbitNode );
            }

            // Proton
            ProtonNode protonNode = new ProtonNode();
            protonNode.setOffset( 0, 0 );
            parentNode.addChild( protonNode );
        }
        
        // Convert all static nodes to a single image
        PImage imageNode = new PImage( parentNode.toImage() );
        imageNode.setOffset( -imageNode.getWidth() / 2, -imageNode.getHeight() / 2 );
        
        _staticNode.addChild( imageNode );
    }
    
    /*
     * Updates the 3D wave node.
     */
    private void updateWaveNode() {
        
        // Compute the verticies
        getWaveVerticies( getAtom(), _waveVerticies );
        
        // Create the wireframe model
        _waveWireframe.reset();
        _waveWireframe.addVerticies( _waveVerticies );
        for ( int i = 0; i < _waveVerticies.length - 1; i++ ) {
            _waveWireframe.addLine( i, i + 1 );
        }
        _waveWireframe.addLine( _waveVerticies.length - 1, 0 ); // close the path
        _waveWireframe.done();
        
        // Transform the model
        Matrix3D matrix = _waveWireframe.getMatrix();
        matrix.unit();
        float xt = -( _waveWireframe.getXMin() + _waveWireframe.getXMax() ) / 2;
        float yt = -( _waveWireframe.getYMin() + _waveWireframe.getYMax() ) / 2;
        float zt = -( _waveWireframe.getZMin() + _waveWireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( _viewMatrix );
        
        // Add the model to the node
        _waveNode.setWireframe( _waveWireframe );
    }
    
    /*
     * Updates the view matrix, accounting for possible 
     * animation of the view rotating into place.
     */
    private void updateViewMatrix() {
        if ( _currentViewAngle == FINAL_VIEW_ANGLE ) {
            _finalViewAngleDrawn = true;
        }
        else {
            _currentViewAngle += VIEW_ANGLE_DELTA;
            if ( _currentViewAngle > FINAL_VIEW_ANGLE ) {
                _currentViewAngle = FINAL_VIEW_ANGLE;
            }
            _viewMatrix.unit();
            _viewMatrix.xrot( _currentViewAngle );
        }
    }
    
    //----------------------------------------------------------------------------
    // Node creation
    //----------------------------------------------------------------------------
    
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
        double radius = ModelViewTransform.transform( atom.getElectronOrbitRadius() );;
        
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