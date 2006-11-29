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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import edu.colorado.phet.hydrogenatom.model.DeBroglieModel;
import edu.colorado.phet.hydrogenatom.model3d.Matrix3D;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3DNode;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3D;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglieViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * DeBroglieHeight3DNode
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
    private static final Stroke ORBIT_STROKE = OrbitNodeFactory.getOrbitStroke();
    private static final Color ORBIT_FRONT_COLOR = OrbitNodeFactory.getOrbitColor();
    private static final Color ORBIT_BACK_COLOR = ORBIT_FRONT_COLOR.darker().darker();
    
    private static final int WAVE_POINTS = 200;
    private static final Stroke WAVE_STROKE = new BasicStroke( 2f );
    private static final Color WAVE_FRONT_COLOR = ElectronNode.getColor();
    private static final Color WAVE_BACK_COLOR = WAVE_FRONT_COLOR.darker().darker();
    
    //----------------------------------------------------------------------------
    // Data structures
    //----------------------------------------------------------------------------
    
    private static class Point3D {
        double x, y, z;
        public Point3D( double x, double y, double z ) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Wireframe3DNode _waveNode;
    private Matrix3D _viewAngleMatrix; // matrix used to set view angle
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        _viewAngleMatrix = new Matrix3D();
        _viewAngleMatrix.xrot( VIEW_ANGLE );
        initStaticNodes();
        update();
    }
    
    //----------------------------------------------------------------------------
    // AbstractDeBroglieViewStrategy implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {

        Point3D[] points = getWavePoints( WAVE_POINTS );
        InputStream stream = pointsToStream( points );
        Wireframe3D wireframe = null;
        try {
            wireframe = new Wireframe3D( stream );
        }
        catch ( IOException ioe ) {
            throw new RuntimeException( ioe );
        }
        
        wireframe.setColors( WAVE_FRONT_COLOR, WAVE_BACK_COLOR );
        wireframe.setStroke( WAVE_STROKE );
        wireframe.setAntialias( true );
        
        Matrix3D matrix = wireframe.getMatrix();
        matrix.unit();
        float xt = -( wireframe.getXMin() + wireframe.getXMax() ) / 2;
        float yt = -( wireframe.getYMin() + wireframe.getYMax() ) / 2;
        float zt = -( wireframe.getZMin() + wireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( _viewAngleMatrix );
        wireframe.setTransformed( false );
        
        if ( _waveNode != null ) {
            removeChild( _waveNode );
        }
        _waveNode = new Wireframe3DNode( wireframe );
        addChild( _waveNode );
    }
    
    //----------------------------------------------------------------------------
    // private
    //----------------------------------------------------------------------------
    
    /*
     * Creates the static parts of the view and flattens everything to a single PImage node.
     */
    private void initStaticNodes() {

        PNode parentNode = new PNode();
        
        // 3D orbits
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = getAtom().getOrbitRadius( state );
            Wireframe3DNode orbitNode = create3DOrbitNode( radius, ORBIT_POINTS );
            parentNode.addChild( orbitNode );
        }
        
        // Proton
        ProtonNode protonNode = new ProtonNode();
        protonNode.setOffset( 0, 0 );
        parentNode.addChild( protonNode );
        
        // Convert all static nodes to a single image
        PImage imageNode = new PImage( parentNode.toImage() );
        imageNode.setOffset( -imageNode.getWidth()/2, -imageNode.getHeight()/2 );
        addChild( imageNode );
    }
    
    /*
     * Gets the points that represent the current state of the atom.
     */
    private Point3D[] getWavePoints( int numberOfPoints ) {
        
        double radius = getAtom().getElectronOrbitRadius();
        Point3D[] points = new Point3D[numberOfPoints];
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double frac = i / ( (double) numberOfPoints - 1 );
            double angle = Math.PI * 2 * frac;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = MAX_HEIGHT * getAtom().getAmplitude( angle );
            Point3D pt = new Point3D( x, y, z );
            points[i] = pt;
        }
        return points;
    }
    
    /*
     * Creates a 3D orbit node.
     */
    private Wireframe3DNode create3DOrbitNode( double radius, int numberOfPoints ) {
        
        Point3D[] points = new Point3D[numberOfPoints];
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double frac = i / ( (double) numberOfPoints - 1 );
            double angle = Math.PI * 2 * frac;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = 0;
            Point3D pt = new Point3D( x, y, z );
            points[i] = pt;
        }
        
        InputStream stream = pointsToStream( points );
        Wireframe3D wireframe = null;
        try {
            wireframe = new Wireframe3D( stream );
        }
        catch ( IOException ioe ) {
            throw new RuntimeException( ioe );
        }
        
        wireframe.setColors( ORBIT_FRONT_COLOR, ORBIT_BACK_COLOR );
        wireframe.setStroke( ORBIT_STROKE );
        wireframe.setAntialias( true );
        
        Matrix3D matrix = wireframe.getMatrix();
        matrix.unit();
        float xt = -( wireframe.getXMin() + wireframe.getXMax() ) / 2;
        float yt = -( wireframe.getYMin() + wireframe.getYMax() ) / 2;
        float zt = -( wireframe.getZMin() + wireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( _viewAngleMatrix );
        wireframe.setTransformed( false );
        
        return new Wireframe3DNode( wireframe );
    }
    
    /*
     * Converts an array of 3D points to an input stream.
     * 
     * The Wireframe3D constructor is expecting to read an input stream,
     * most likely from a file that contains info about the model.
     * The input stream format is reportedly similar to the Wavefront OBJ 
     * file format, but that has not been confirmed.
     * 
     * Tokens appearing in Wireframe3D's parser include:
     * v = vertex
     * l = line
     * f = face
     * fo = ?
     * # = comment
     */
    private static InputStream pointsToStream( Point3D[] points ) {
        StringBuffer buf = new StringBuffer();
        DecimalFormat formatter = new DecimalFormat( "0.0000" );
        for ( int i = 0; i < points.length; i++ ) {
            Point3D p = points[i];
            buf.append( "v " );
            buf.append( formatter.format( p.x ) );
            buf.append( " " );
            buf.append( formatter.format( p.y ) );
            buf.append( " " );
            buf.append( formatter.format( p.z ) );
            buf.append( System.getProperty( "line.separator" ) );
        }
        for ( int i = 1; i <= points.length; i++ ) {
            buf.append( "l " );
            buf.append( i );
            buf.append( " " );
            buf.append( i + 1 );
            buf.append( System.getProperty( "line.separator" ) );
        }
        buf.append( "l " );
        buf.append( points.length - 1 );
        buf.append( " 1" );
        InputStream is = new ByteArrayInputStream( buf.toString().getBytes() );
        return is;
    }
}