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
import edu.colorado.phet.hydrogenatom.model3d.PNode3D;
import edu.colorado.phet.hydrogenatom.model3d.Wireframe3D;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieNode.AbstractDeBroglieViewStrategy;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.ProtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;


public class DeBroglieHeight3DNode extends AbstractDeBroglieViewStrategy {
    
    private static final double MAX_HEIGHT = 15; // screen coordinates
    private static final double VIEW_ANGLE = 70; // degrees
    
    private static final int ORBIT_POINTS = 200;
    private static final Stroke ORBIT_STROKE = new BasicStroke( 1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3,3}, 0 );
    private static final Color ORBIT_FRONT_COLOR = Color.WHITE;
    private static final Color ORBIT_BACK_COLOR = ORBIT_FRONT_COLOR.darker().darker();
    
    private static final int WAVE_POINTS = 200;
    private static final Stroke WAVE_STROKE = new BasicStroke( 2f );
    private static final Color WAVE_FRONT_COLOR = ElectronNode.getColor();
    private static final Color WAVE_BACK_COLOR = WAVE_FRONT_COLOR.darker().darker();
    
    private PNode3D _waveNode;
    private Matrix3D _viewAngleMatrix; // matrix used to set view angle
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        _viewAngleMatrix = new Matrix3D();
        _viewAngleMatrix.xrot( VIEW_ANGLE );
        initStaticNodes();
        update();
    }
    
    /*
     * Creates nodes for the proton and orbits.
     */
    private void initStaticNodes() {

        PNode parentNode = new PNode();
        
        // 3D orbits
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = getAtom().getOrbitRadius( state );
            PNode3D orbitNode = createOrbitNode3D( radius, ORBIT_POINTS );
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
    
    public void update() {

        Point3D[] points = getPoints( WAVE_POINTS );
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
        _waveNode = new PNode3D( wireframe );
        addChild( _waveNode );
    }
    
    /*
     * Gets the points that represent the current state of the atom.
     */
    private Point3D[] getPoints( int numberOfPoints ) {
        
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
     * Converts an array of 3D points to an input stream.
     * The Model3D constructor is expecting to read an input stream,
     * most likely from a file that contains info about the model.
     * The input stream format is reportedly the same as the Wavefront OBJ file format.
     * Tokens found in Dave's sample data files:
     * v = vertex
     * l = line
     * f = face
     * fo = ?
     * g = ?
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
    
    private static class Point3D {
        double x, y, z;
        public Point3D( double x, double y, double z ) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    private PNode3D createOrbitNode3D( double radius, int numberOfPoints ) {
        
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
        Wireframe3D orbit3d = null;
        try {
            orbit3d = new Wireframe3D( stream );
        }
        catch ( IOException ioe ) {
            throw new RuntimeException( ioe );
        }
        
        orbit3d.setColors( ORBIT_FRONT_COLOR, ORBIT_BACK_COLOR );
        orbit3d.setStroke( ORBIT_STROKE );
        
        Matrix3D matrix = orbit3d.getMatrix();
        matrix.unit();
        float xt = -( orbit3d.getXMin() + orbit3d.getXMax() ) / 2;
        float yt = -( orbit3d.getYMin() + orbit3d.getYMax() ) / 2;
        float zt = -( orbit3d.getZMin() + orbit3d.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( _viewAngleMatrix );
        orbit3d.setTransformed( false );
        
        return new PNode3D( orbit3d );
    }
}