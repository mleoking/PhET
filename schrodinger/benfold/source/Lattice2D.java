import java.awt.*;


/**
 * Class to assist in the display of a 2D lattice.
 */
class Lattice2D {
    /**
     * Creates a new lattice
     *
     * @param    vectors    A two-element array containing the lattice vectors
     */
    public Lattice2D( Vector2D[] vectors ) {
        if( vectors.length != 2 ) {
            throw new RuntimeException( "Lattice2D requires two Vector2Ds" );
        }
        this.vectors = vectors;
        xscl = yscl = 1;
        vectorCols = new Color[]{Color.red, Color.blue};
        basisCol = Color.black;
        basisSize = 15;
        basis = new PolygonBasis( 6, basisSize );
    }


    /**
     * Creates a new lattice
     *
     * @param    v1    The first lattice vector
     * @param    v2    The second lattice vector
     */
    public Lattice2D( Vector2D v1, Vector2D v2 ) {
        this( new Vector2D[]{v1, v2} );
    }


    /**
     * Creates a new lattice
     *
     * @param    x1    The <i>x</i> component of the first lattice vector
     * @param    y1    The <i>y</i> component of the first lattice vector
     * @param    x2    The <i>x</i> component of the second lattice vector
     * @param    y2    The <i>y</i> component of the second lattice vector
     */
    public Lattice2D( double x1, double y1, double x2, double y2 ) {
        this( new Vector2D( x1, y1 ), new Vector2D( x2, y2 ) );
    }


    /**
     * Creates a new lattice with orthogonal lattice vectors.
     */
    public Lattice2D() {
        this( 45, 0, 0, -45 );
    }


    /**
     * Converts an <i>x</i> coordinate from lattice space to screen space
     *
     * @param    x    The lattice coordinate
     * @param    w    The width of the display
     */
    public int toScreenX( double x, int w ) {
        return (int)( w / 2 + x * xscl );
    }


    /**
     * Converts a <i>y</i> coordinate from lattice space to screen space
     *
     * @param    y    The lattice coordinate
     * @param    h    The height of the display
     */
    public int toScreenY( double y, int h ) {
        return (int)( h / 2 + y * yscl );
    }


    /**
     * Converts an <i>x</i> coordinate from screen space to lattice space
     *
     * @param    x    The screen coordinate
     * @param    w    The width of the display
     */
    public double fromScreenX( int x, int w ) {
        return ( x - w / 2 ) / xscl;
    }


    /**
     * Converts a <i>y</i> coordinate from screen space to lattice space
     *
     * @param    y    The screen coordinate
     * @param    h    The height of the display
     */
    public double fromScreenY( int y, int h ) {
        return ( y - h / 2 ) / yscl;
    }


    /**
     * Converts a pair of coordinates from screen space to a lattice space
     * vector.
     *
     * @param    x    The screen <i>x</i> coordinate
     * @param    y    The screen <i>y</i> coordinate
     * @param    w    The width of the display
     * @param    h    The height of the display
     */
    public Vector2D fromScreen( int x, int y, int w, int h ) {
        return new Vector2D( fromScreenX( x, w ), fromScreenY( y, h ) );
    }


    /**
     * Displays this lattice, inlcuding the two vectors.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the area on which to paint
     * @param    h    The height (in pixels) of the area on which to paint
     */
    public void paint( Graphics g, int w, int h ) {
        paintLattice( g, w, h );

        paintVector( g, w, h, 0 );
        paintVector( g, w, h, 1 );
    }


    /**
     * Paints a vector.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the area on which to paint
     * @param    h    The height (in pixels) of the area on which to paint
     * @param    vector    The vector to paint
     */
    protected void paintVector( Graphics g, int w, int h, int vector ) {
        Vector2D v = vectors[vector];
        int x = toScreenX( v.getX(), w );
        int y = toScreenY( v.getY(), h );

        g.setColor( vectorCols[vector] );
        g.drawLine( toScreenX( 0, w ), toScreenY( 0, h ), x, y );
        g.fillRect( x - MARK_RADIUS, y - MARK_RADIUS, 2 * MARK_RADIUS, 2 * MARK_RADIUS );
    }


    /**
     * Returns the vector that is closest (of the two lattice vectors) to the
     * specified screen coordinates.
     *
     * @param    x    The screen <i>x</i> coordinate
     * @param    y    The screen <i>y</i> coordinate
     * @param    w    The width of the display
     * @param    h    The height of the display
     */
    public Vector2D chooseVector( int x, int y, int w, int h ) {
        Vector2D v = fromScreen( x, y, w, h );
        double best = Double.MAX_VALUE;
        int bestIndex = -1;
        double dist;
        for( int i = 0; i < 2; i++ ) {
            if( ( dist = distanceFrom( v, i ) ) < best ) {
                bestIndex = i;
                best = dist;
            }
        }
        return ( best < THRESHOLD ) ? vectors[bestIndex] : null;
    }


    /**
     * Returns the distance between the specifed point and one of the two
     * lattice vectors.
     *
     * @param    point    The point
     * @param    vector    The index (0 or 1) of a lattice vector
     */
    protected double distanceFrom( Vector2D point, int vector ) {
        return vectors[vector].subtract( point ).modulusSquared();
    }


    /**
     * Paints just the lattice (without the vectors).
     * <p/>
     * The basic algorithm is to display a &quot;grid&quot; of bases, each
     * at a position <i>a</i><b>v<sub>1</sub></b> + <i>b</i><b>v<sub>2</sub></b>,
     * where <i>a</i>, <i>b</i> are integers in the interval [-30,30].
     * <p/>
     * However, with near-dependent lattice vectors, this is often
     * insufficient; a large number of bases will fall outside the view
     * area, and the view area will not be entirely filled.
     * <p/>
     * The solution is to make use of the fact that <b>u</b>, <b>v</b>
     * describe the same vector space as <b>u</b>, <b>v</b> -
     * <i>n</i><b>u</b>, where <i>n</i> is any integer.  The two lattice
     * vectors will be transformed in this way, subtracting the smaller from
     * the larger, until the modulae are minimised, at which point the
     * vectors will be the closest-to-orthogonal vectors which still describe
     * the same vector space.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    w    The width (in pixels) of the area on which to paint
     * @param    h    The height (in pixels) of the area on which to paint
     */
    protected void paintLattice( Graphics g, int w, int h ) {
        g.setColor( basisCol );

        Vector2D v1 = vectors[0], v2 = vectors[1], spine, v;

        //	Fudge vectors to make diagram easier to draw
        //	v1,v2 form the same set of points as v2-k*v1, v1

        if( v2.modulus() < v1.modulus() ) {
            v = v2;
            v2 = v1;
            v1 = v;
        }

        boolean changed = true;
        double oldModulus = v2.modulus();
        while( changed ) {
            changed = false;
            double d;
            if( v2.modulus() < v1.modulus() ) {
                v = v2;
                v2 = v1;
                v1 = v;
            }

            oldModulus = v2.modulus();

            //	v1 is now the smaller of the two;

            d = v2.subtract( v1 ).modulus();
            if( d < oldModulus ) {
                v2 = v2.subtract( v1 );
                oldModulus = d;
                changed = true;
            }

            d = v2.add( v1 ).modulus();
            if( d < oldModulus ) {
                v2 = v2.add( v1 );
                oldModulus = d;
                changed = true;
            }
        }


        int x, y;

        int minX = 0 - basisSize;
        int maxX = w + basisSize;
        int minY = 0 - basisSize;
        int maxY = h + basisSize;

        /*	Transform view rectangle to bounds in lattice vector space
        int minAlpha=0,maxAlpha=0,minBeta=0,maxBeta=0;
        double alpha = vectors[0].x - vectors[0].y*vectors[1].x/vectors[1].y;
        double beta = vectors[1].x - vectors[1].y*vectors[0].x/vectors[0].y;
        Vector2D[] pts = new Vector2D[] {new Vector2D(0,0), new Vector2D(w,0), new Vector2D(0,h), new Vector2D(w,h)};
        for(int i=0; i<pts.length; i++)
        {
            double alpha =
        */

        for( int i = -30; i <= 30; i++ ) {
            spine = v1.multiply( i );
/*			x = toScreenX(v1.getX(),w);
			y = toScreenY(v1.getY(),h);

			if(x<minX || x>maxX || y<minY || y>maxY)
				break;*/

            for( int j = -30; j <= 30; j++ ) {
                v = v2.multiply( j );
                v = v.add( spine );
                x = toScreenX( v.getX(), w );
                y = toScreenY( v.getY(), h );

                if( x > minX && x < maxX && y > minY && y < maxY ) {
                    basis.paint( g, x, y );
                    /*	basis.paint(g,toScreenX(-v.getX(),w), toScreenY(-v.getY(),h));
                         v = v1.subtract(v2);
                         basis.paint(g,toScreenX(v.getX(),w), toScreenY(v.getY(),h));
                         basis.paint(g,toScreenX(-v.getX(),w), toScreenY(-v.getY(),h));*/
                }
            }
        }
    }


    /**
     * Sets the selected basis
     */
    public void setBasis( Basis b ) {
        basis = b;
    }

/*	protected void paintSquare(Graphics g, int cx, int cy)
    {
        int r = basisSize;
        g.drawRect(cx-r, cy-r, 2*r, 2*r);
    }*/

    /*protected void paintHexagon(Graphics g, int cx, int cy)
     {
         //System.err.println("painting basis ("+cx+","+cy+")");
         //	n-gon

         int r = basisSize;
         int x=cx+r,y=cy;
         int n = 6;

         for(int i=1; i<=n; i++)
             g.drawLine(x, y, x=cx+(int)(r*Math.cos(2*i*Math.PI/n)), y=cy+(int)(r*Math.sin(2*i*Math.PI/n)));
     } */


    /**
     * Class representing a basis.  In the context of this model,
     * a basis is just an image to replicate across the lattice; this is
     * reflected in the fact that this interface has only one method,
     * {@link #paint(Graphics,int,int) paint()}.
     */
    public interface Basis {
        /**
         * Requests that the basis paint itself
         *
         * @param g  The Graphics surface on which to paint
         * @param cx The <i>x</i> coordinate (in pixels) at which the
         *           basis should be painted
         * @param cy The <i>y</i> coordinate (in pixels) at which the
         *           basis should be painted
         */
        public void paint( Graphics g, int cx, int cy );
    }


    /**
     * Polygon-based implementation of a basis
     */
    public class PolygonBasis implements Basis {
        /**
         * Creates a new polygon basis.  Points are distributed as <i>n</i>th
         * (complex) roots of unity, then scaled and rotated as appropriate.
         *
         * @param    nPoints    The number of points
         * @param    r        The distance of each point from the centre
         * @param    rotation    A rotating factor
         */
        public PolygonBasis( int nPoints, int r, double rotation ) {
            x = new int[nPoints];
            y = new int[nPoints];

            for( int i = 0; i < nPoints; i++ ) {
                x[i] = (int)( r * Math.cos( rotation + 2 * i * Math.PI / nPoints ) );
                y[i] = (int)( r * Math.sin( rotation + 2 * i * Math.PI / nPoints ) );
            }
        }


        /**
         * Creates a new polygon basis.  Points are distributed as <i>n</i>th
         * (complex) roots of unity, then scaled as appropriate.
         *
         * @param    nPoints    The number of points
         * @param    r        The distance of each point from the centre
         */
        public PolygonBasis( int nPoints, int r ) {
            this( nPoints, r, 0 );
        }

        public void paint( Graphics g, int cx, int cy ) {
            int n = x.length;
            g.translate( cx, cy );
            for( int i = 0; i < n; i++ ) {
                g.drawLine( x[i % n], y[i % n], x[( i + 1 ) % n], y[( i + 1 ) % n] );
            }
            g.translate( -cx, -cy );
        }


        int[] x, y;
    }


    /**
     * An implementation of Basis which displays as a circle.
     */
    public class CircularBasis implements Basis {
        /**
         * Creates a new basis with the specified radius
         */
        public CircularBasis( int r ) {
            this.r = r;
        }

        public void paint( Graphics g, int cx, int cy ) {
            cx -= r;
            cy -= r;
            g.translate( cx, cy );
            g.drawOval( 0, 0, 2 * r, 2 * r );
            g.translate( -cx, -cy );
        }


        /**
         * The radius
         */
        protected int r;
    }


    /**
     * The selected basis
     */
    protected Basis basis;

    /**
     * A two-element array containing colours (that's right, with a 'u') for the vectors
     */
    protected Color[] vectorCols;

    /**
     * The colour to use when drawing the bases
     */
    protected Color basisCol;

    /**
     * The ratio between <i>x</i> coordinates in the model and those in
     * screen space.
     */
    protected double xscl;

    /**
     * The ratio between <i>x</i> coordinates in the model and those in
     * screen space.
     */
    protected double yscl;

    /**
     * A two-element array containing the lattice vectors
     */
    public Vector2D[] vectors;

    /**
     * Constant scaling factor for the size of the bases.
     */
    protected int basisSize;

    /**
     * Size of the draggable blobs on the ends of the vectors
     */
    public static final int MARK_RADIUS = 2;

    /**    Square of the radius of the circle of points around the end of a
     vector such that a click within that circle is considered close enough
		to be interpreted as a click on said vector.
	*/
	public static final double THRESHOLD = 36;
}