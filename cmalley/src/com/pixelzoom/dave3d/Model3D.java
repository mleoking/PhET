

package com.pixelzoom.dave3d;



import java.awt.Color;

import java.awt.Graphics;

import java.io.*;



/**

 * Model3D is the representation of a 3D model.

 * <p>

 * This code came from http://www.cs.cf.ac.uk/Dave/JAVA/3d/3d.html

 * Copyright Dr. A. D. Marshall, Cardiff School of Computer Science

 */

public class Model3D {



    float vert[];

    int tvert[];

    int nvert, maxvert;

    int con[];

    int ncon, maxcon;

    boolean transformed;

    Matrix3D mat;



    float xmin, xmax, ymin, ymax, zmin, zmax;



    /*

     * Create a 3D model by parsing an input stream

     */

    Model3D( InputStream is ) throws IOException {

        mat = new Matrix3D();

        mat.xrot( 20 );

        mat.yrot( 30 );

        Reader r = new BufferedReader( new InputStreamReader( is ) );

        StreamTokenizer st = new StreamTokenizer( r );

        st.eolIsSignificant( true );

        st.commentChar( '#' );

        scan: while ( true ) {

            switch ( st.nextToken() ) {

            default:

                break scan;

            case StreamTokenizer.TT_EOL:

                break;

            case StreamTokenizer.TT_WORD:

                if ( "v".equals( st.sval ) ) {

                    double x = 0, y = 0, z = 0;

                    if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                        x = st.nval;

                        if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                            y = st.nval;

                            if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                                z = st.nval;

                            }

                        }

                    }

                    addVert( (float) x, (float) y, (float) z );

                    while ( st.ttype != StreamTokenizer.TT_EOL && st.ttype != StreamTokenizer.TT_EOF ) {

                        st.nextToken();

                    }

                }

                else if ( "f".equals( st.sval ) || "fo".equals( st.sval ) || "l".equals( st.sval ) ) {

                    int start = -1;

                    int prev = -1;

                    int n = -1;

                    while ( true ) {

                        if ( st.nextToken() == StreamTokenizer.TT_NUMBER ) {

                            n = (int) st.nval;

                            if ( prev >= 0 ) {

                                add( prev - 1, n - 1 );

                            }

                            if ( start < 0 ) {

                                start = n;

                            }

                            prev = n;

                        }

                        else if ( st.ttype == '/' ) {

                            st.nextToken();

                        }

                        else {

                            break;

                        }

                    }

                    if ( start >= 0 ) {

                        add( start - 1, prev - 1 );

                    }

                    if ( st.ttype != StreamTokenizer.TT_EOL ) {

                        break scan;

                    }

                }

                else {

                    while ( st.nextToken() != StreamTokenizer.TT_EOL && st.ttype != StreamTokenizer.TT_EOF ) {

                        ;

                    }

                }

            }

        }

        is.close();

    }



    /**

     * Add a vertex to this model

     */

    int addVert( float x, float y, float z ) {

        int i = nvert;

        if ( i >= maxvert ) {

            if ( vert == null ) {

                maxvert = 100;

                vert = new float[maxvert * 3];

            }

            else {

                maxvert *= 2;

                float nv[] = new float[maxvert * 3];

                System.arraycopy( vert, 0, nv, 0, vert.length );

                vert = nv;

            }

        }

        i *= 3;

        vert[i] = x;

        vert[i + 1] = y;

        vert[i + 2] = z;

        return nvert++;

    }



    /**

     * Add a line from vertex p1 to vertex p2

     */

    void add( int p1, int p2 ) {

        int i = ncon;

        if ( p1 >= nvert || p2 >= nvert ) {

            return;

        }

        if ( i >= maxcon ) {

            if ( con == null ) {

                maxcon = 100;

                con = new int[maxcon];

            }

            else {

                maxcon *= 2;

                int nv[] = new int[maxcon];

                System.arraycopy( con, 0, nv, 0, con.length );

                con = nv;

            }

        }

        if ( p1 > p2 ) {

            int t = p1;

            p1 = p2;

            p2 = t;

        }

        con[i] = ( p1 << 16 ) | p2;

        ncon = i + 1;

    }



    /**

     * Transform all the points in this model

     */

    void transform() {

        if ( transformed || nvert <= 0 ) {

            return;

        }

        if ( tvert == null || tvert.length < nvert * 3 ) {

            tvert = new int[nvert * 3];

        }

        mat.transform( vert, tvert, nvert );

        transformed = true;

    }



    private void sort( int lo0, int hi0 ) {

        int a[] = con;

        int lo = lo0;

        int hi = hi0;

        if ( lo >= hi ) {

            return;

        }

        int mid = a[( lo + hi ) / 2];

        while ( lo < hi ) {

            while ( lo < hi && a[lo] < mid ) {

                lo++;

            }

            while ( lo < hi && a[hi] >= mid ) {

                hi--;

            }

            if ( lo < hi ) {

                int T = a[lo];

                a[lo] = a[hi];

                a[hi] = T;

            }

        }

        if ( hi < lo ) {

            int T = hi;

            hi = lo;

            lo = T;

        }

        sort( lo0, lo );

        sort( lo == lo0 ? lo + 1 : lo, hi0 );

    }



    /**

     * eliminate duplicate lines

     */

    void compress() {

        int limit = ncon;

        int c[] = con;

        sort( 0, ncon - 1 );

        int d = 0;

        int pp1 = -1;

        for ( int i = 0; i < limit; i++ ) {

            int p1 = c[i];

            if ( pp1 != p1 ) {

                c[d] = p1;

                d++;

            }

            pp1 = p1;

        }

        ncon = d;

    }



    static Color gr[];



    /**

     * Paint this model to a graphics context.  It uses the matrix associated

     * with this model to map from model space to screen space.

     * The next version of the browser should have double buffering,

     * which will make this *much* nicer

     */

    void paint( Graphics g ) {

        if ( vert == null || nvert <= 0 ) {

            return;

        }

        transform();

        if ( gr == null ) {

            gr = new Color[16];

            for ( int i = 0; i < 16; i++ ) {

                int grey = (int) ( 170 * ( 1 - Math.pow( i / 15.0, 2.3 ) ) );

                gr[i] = new Color( grey, grey, grey );

            }

        }

        int lg = 0;

        int lim = ncon;

        int c[] = con;

        int v[] = tvert;

        if ( lim <= 0 || nvert <= 0 ) {

            return;

        }

        for ( int i = 0; i < lim; i++ ) {

            int T = c[i];

            int p1 = ( ( T >> 16 ) & 0xFFFF ) * 3;

            int p2 = ( T & 0xFFFF ) * 3;

            int grey = v[p1 + 2] + v[p2 + 2];

            if ( grey < 0 ) {

                grey = 0;

            }

            if ( grey > 15 ) {

                grey = 15;

            }

            if ( grey != lg ) {

                lg = grey;

                g.setColor( gr[grey] );

            }

            g.drawLine( v[p1], v[p1 + 1], v[p2], v[p2 + 1] );

        }

    }



    /**

     * Find the bounding box of this model

     */

    void findBB() {

        if ( nvert <= 0 ) {

            return;

        }

        float v[] = vert;

        float xmin = v[0], xmax = xmin;

        float ymin = v[1], ymax = ymin;

        float zmin = v[2], zmax = zmin;

        for ( int i = nvert * 3; ( i -= 3 ) > 0; ) {

            float x = v[i];

            if ( x < xmin ) {

                xmin = x;

            }

            if ( x > xmax ) {

                xmax = x;

            }

            float y = v[i + 1];

            if ( y < ymin ) {

                ymin = y;

            }

            if ( y > ymax ) {

                ymax = y;

            }

            float z = v[i + 2];

            if ( z < zmin ) {

                zmin = z;

            }

            if ( z > zmax ) {

                zmax = z;

            }

        }

        this.xmax = xmax;

        this.xmin = xmin;

        this.ymax = ymax;

        this.ymin = ymin;

        this.zmax = zmax;

        this.zmin = zmin;

    }

}

