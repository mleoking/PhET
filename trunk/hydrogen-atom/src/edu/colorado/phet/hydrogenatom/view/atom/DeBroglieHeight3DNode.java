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


public class DeBroglieHeight3DNode extends AbstractDeBroglieViewStrategy {
    
    private static final int WAVE_POINTS = 200;
    private static final int WAVE_FREQUENCY = 6;
    private static final Stroke WAVE_STROKE = new BasicStroke( 2f );
    private static final Color WAVE_COLOR = ElectronNode.getColor();
    
    private PNode3D _waveNode;
    private int count = 0;//XXX get rid of this, get values from model
    private Matrix3D _amat;
    
    public DeBroglieHeight3DNode( DeBroglieModel atom ) {
        super( atom );
        _amat = new Matrix3D();
        _amat.xrot( 60 );
        initStaticNodes();
        update();
    }
    
    /*
     * Creates nodes for the proton and orbits.
     */
    private void initStaticNodes() {

        // Orbits (pseudo-3D perspective)
        int groundState = DeBroglieModel.getGroundState();
        int numberOfStates = DeBroglieModel.getNumberOfStates();
        for ( int state = groundState; state < ( groundState + numberOfStates ); state++ ) {
            double radius = getAtom().getOrbitRadius( state );
            PNode orbitNode = OrbitNodeFactory.createPerspectiveOrbitNode( radius, 2 );
            addChild( orbitNode );
        }
        
        // Proton
        ProtonNode protonNode = new ProtonNode();
        protonNode.setOffset( 0, 0 );
        addChild( protonNode );
    }
    
    public void update() {
        
        double amplitude = 6 * Math.sin( count / 10.0 );
        double phase = count / 100.0;
        double radius = getAtom().getElectronOrbitRadius();

        Point3D[] points = getPoints( WAVE_POINTS, radius, WAVE_FREQUENCY, amplitude, phase );
        InputStream stream = pointsToStream( points );
        Wireframe3D wireframe = null;
        try {
            wireframe = new Wireframe3D( stream );
        }
        catch ( IOException ioe ) {
            throw new RuntimeException( ioe );
        }
        
        wireframe.compress();
        wireframe.findBB();
        
        wireframe.setColor( WAVE_COLOR );
        
        Matrix3D matrix = wireframe.getMatrix();
        matrix.unit();
        float xt = -( wireframe.getXMin() + wireframe.getXMax() ) / 2;
        float yt = -( wireframe.getYMin() + wireframe.getYMax() ) / 2;
        float zt = -( wireframe.getZMin() + wireframe.getZMax() ) / 2;
        matrix.translate( xt, yt, zt );
        matrix.mult( _amat );
        wireframe.setTransformed( false );
        
        if ( _waveNode != null ) {
            removeChild( _waveNode );
        }
        _waveNode = new PNode3D( wireframe, WAVE_STROKE );
        addChild( _waveNode );
        
        count++;
    }
    
    /*
     * Gets the points that approximate a standing wave.
     */
    private static Point3D[] getPoints( int numberOfPoints, double radius, double zFreq, double zAmp, double zPhase ) {
        Point3D[] points = new Point3D[numberOfPoints];
        for ( int i = 0; i < numberOfPoints; i++ ) {
            double frac = i / ( (double) numberOfPoints - 1 );
            double angle = Math.PI * 2 * frac;
            double x = radius * Math.cos( angle );
            double y = radius * Math.sin( angle );
            double z = zAmp * Math.sin( angle * zFreq + zPhase );
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
}