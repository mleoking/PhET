/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * PersistentGeneralPath
 * <p/>
 * A wrapper class for GeneralPath that can be persisted using XMLEncoder/XMLDecoder. Note that the
 * class implements Shape. It cannot extend GeneralPath because that is a final class.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentGeneralPath implements Shape, Persistent {
    private GeneralPath path;

    public PersistentGeneralPath() {
         path = new GeneralPath();
    }

    public PersistentGeneralPath( GeneralPath path ) {
        this.path = path;
    }

    ///////////////////////////////////////////
    // Persistence
    //
    public StateDescriptor getState() {
        return new GeneralPathDescriptor(path);
    }

    public void setState( StateDescriptor stateDescriptor ) {
        path = new GeneralPath();
        stateDescriptor.setState( this );
    }

    ///////////////////////////////////////////
    // Wrapper methods
    //
    public boolean contains( double x, double y ) {
        return path.contains( x, y );
    }

    public boolean contains( double x, double y, double w, double h ) {
        return path.contains( x, y, w, h );
    }

    public boolean intersects( double x, double y, double w, double h ) {
        return path.intersects( x, y, w, h );
    }

    public Rectangle getBounds() {
        return path.getBounds();
    }

    public boolean contains( Point2D p ) {
        return path.contains( p );
    }

    public Rectangle2D getBounds2D() {
        return path.getBounds2D();
    }

    public boolean contains( Rectangle2D r ) {
        return path.contains( r );
    }

    public boolean intersects( Rectangle2D r ) {
        return path.intersects( r );
    }

    public PathIterator getPathIterator( AffineTransform at ) {
        return path.getPathIterator( at );
    }

    public PathIterator getPathIterator( AffineTransform at, double flatness ) {
        return path.getPathIterator( at, flatness );
    }

    public void append( PathIterator pi, boolean connect ) {
        path.append( pi, connect );
    }

    public void append( Shape s, boolean connect ) {
        path.append( s, connect );
    }

    public void closePath() {
        path.closePath();
    }

    public Shape createTransformedShape( AffineTransform at ) {
        return path.createTransformedShape( at );
    }

    public void curveTo( float x1, float y1, float x2, float y2, float x3, float y3 ) {
        path.curveTo( x1, y1, x2, y2, x3, y3 );
    }

    public Point2D getCurrentPoint() {
        return path.getCurrentPoint();
    }

    public int getWindingRule() {
        return path.getWindingRule();
    }

    public void lineTo( float x, float y ) {
        path.lineTo( x, y );
    }

    public void moveTo( float x, float y ) {
        path.moveTo( x, y );
    }

    public void quadTo( float x1, float y1, float x2, float y2 ) {
        path.quadTo( x1, y1, x2, y2 );
    }

    public void reset() {
        path.reset();
    }

    public void setWindingRule( int rule ) {
        path.setWindingRule( rule );
    }

    public void transform( AffineTransform at ) {
        path.transform( at );
    }


    /////////////////////////////////
    // Inner classes
    //

    /**
     * A Jaba Bean conformant class that contains the information needed
     * to persist and restor a GeneralPath.
     */
    public static class GeneralPathDescriptor implements StateDescriptor {
        int windingRule;
        ArrayList segments = new ArrayList();
        int[] segTypes;
        float[][] segCoords;
        // Number of segments in the path
        int segCnt = 0;
        // Pointer to the current segment
        int segPtr = 0;

        public GeneralPathDescriptor() {
        }

        public GeneralPathDescriptor( GeneralPath path ) {
            // Winding rule
            this.windingRule = path.getWindingRule();

            // Determine how many segments there are in the path
            PathIterator pi = path.getPathIterator( new AffineTransform() );
            while( !pi.isDone() ) {
                pi.next();
                segCnt++;
            }
            // Create an internal representation of the segment types and their
            // coordinates
            segTypes = new int[segCnt];
            segCoords = new float[segCnt][];
            PathIterator pi2 = path.getPathIterator( new AffineTransform() );
            int i = 0;
            while( !pi2.isDone() ) {
                float[] c = new float[6];
                segTypes[i] = pi2.currentSegment( c );
                segCoords[i] = c;
                pi2.next();
                i++;
            }
        }

        ////////////////////////////////////////////
        // Path generation
        //

        public void setState( Persistent persistentObj ) {
            PersistentGeneralPath path = (PersistentGeneralPath)persistentObj;
            path.setWindingRule( windingRule );
            for( int i = 0; i < segTypes.length; i++ ) {
                int segType = segTypes[i];
                float[] coords = segCoords[i];
                switch( segType ) {
                    case PathIterator.SEG_CLOSE:
                        path.closePath();
                        break;
                    case PathIterator.SEG_CUBICTO:
                        path.curveTo( coords[0], coords[1], coords[2], coords[3], coords[4], coords[5] );
                        break;
                    case PathIterator.SEG_LINETO:
                        path.lineTo( coords[0], coords[1] );
                        break;
                    case PathIterator.SEG_MOVETO:
                        path.moveTo( coords[0], coords[1] );
                        break;
                    case PathIterator.SEG_QUADTO:
                        path.quadTo( coords[0], coords[1], coords[2], coords[3] );
                        break;
                }
            }
        }

        ///////////////////////////////////////////
        // Setters and getters
        //
        public int[] getSegTypes() {
            return segTypes;
        }

        public void setSegTypes( int[] segTypes ) {
            this.segTypes = segTypes;
        }

        public float[][] getSegCoords() {
            return segCoords;
        }

        public void setSegCoords( float[][] segCoords ) {
            this.segCoords = segCoords;
        }
    }
}
