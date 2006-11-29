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
import java.awt.Stroke;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.model3d.Matrix3D;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3D;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3DNode;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3D.Point3D;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglieViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
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
    
    private static final double MAX_HEIGHT = 15; // screen coordinates
    private static final double VIEW_ANGLE = 70; // degrees
    
    private static final int ORBIT_POINTS = 200;
    private static final Stroke ORBIT_STROKE = new BasicStroke( 1f );
    private static final Color ORBIT_FRONT_COLOR = OrbitNodeFactory.getOrbitColor();
    private static final Color ORBIT_BACK_COLOR = ORBIT_FRONT_COLOR.darker().darker();
    
    private static final int WAVE_POINTS = 200;
    private static final Stroke WAVE_STROKE = new BasicStroke( 2f );
    private static final Color WAVE_FRONT_COLOR = ElectronNode.getColor();
    private static final Color WAVE_BACK_COLOR = WAVE_FRONT_COLOR.darker().darker();
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Wireframe3DNode _waveNode;
    private Matrix3D _viewMatrix; // matrix used to set view angle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        _viewMatrix = new Matrix3D();
        _viewMatrix.xrot( VIEW_ANGLE );
        PNode staticNode = createStaticNode( atom, _viewMatrix );
        staticNode.setOffset( -staticNode.getWidth()/2, -staticNode.getHeight()/2 );
        addChild( staticNode );
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglieViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( _waveNode != null ) {
            removeChild( _waveNode );
        }
        _waveNode = create3DWaveNode( getAtom(), _viewMatrix );
        addChild( _waveNode );
    }
    
    //----------------------------------------------------------------------------
    // private static
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
        
        Point3D[] points = getWavePoints( WAVE_POINTS, atom );
        Wireframe3D wireframe = new Wireframe3D( points );
        wireframe.setColors( WAVE_FRONT_COLOR, WAVE_BACK_COLOR );
        wireframe.setStroke( WAVE_STROKE );
        
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
    private static Wireframe3DNode create3DOrbitNode( int numberOfPoints, double radius, Matrix3D viewMatrix ) {
        
        Point3D[] points = getOrbitPoints( numberOfPoints, radius );
        Wireframe3D wireframe = new Wireframe3D( points );
        wireframe.setColors( ORBIT_FRONT_COLOR, ORBIT_BACK_COLOR );
        wireframe.setStroke( ORBIT_STROKE );
        
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
     * Gets the points that approximate the standing wave.
     */
    private static Point3D[] getWavePoints( int numberOfPoints, DeBroglieModel atom ) {
        double radius = atom.getElectronOrbitRadius();
        double deltaAngle = ( 2 * Math.PI ) / numberOfPoints;
        Point3D[] points = new Point3D[numberOfPoints + 1];
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = MAX_HEIGHT * atom.getAmplitude( angle );
            Point3D pt = new Point3D( x, y, z );
            points[i] = pt;
        }
        points[points.length - 1] = points[0]; // close the path
        return points;
    }

    /*
     * Gets the points that approximate an orbit.
     */
    private static Point3D[] getOrbitPoints( int numberOfPoints, double radius ) {
        Point3D[] points = new Point3D[numberOfPoints + 1];
        double deltaAngle = ( 2 * Math.PI ) / numberOfPoints;
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double angle = i * deltaAngle;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = 0;
            Point3D pt = new Point3D( x, y, z );
            points[i] = pt;
        }
        points[points.length - 1] = points[0]; // close the path
        return points;
    }
}