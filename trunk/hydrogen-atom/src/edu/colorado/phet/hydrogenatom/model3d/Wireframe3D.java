/*

 * Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.

 * 

 * Redistribution and use in source and binary forms, with or without

 * modification, are permitted provided that the following conditions

 * are met:

 * 

 * -Redistributions of source code must retain the above copyright

 *  notice, this list of conditions and the following disclaimer.

 * 

 * -Redistribution in binary form must reproduct the above copyright

 *  notice, this list of conditions and the following disclaimer in

 *  the documentation and/or other materials provided with the distribution.

 * 

 * Neither the name of Sun Microsystems, Inc. or the names of contributors

 * may be used to endorse or promote products derived from this software

 * without specific prior written permission.

 * 

 * This software is provided "AS IS," without a warranty of any kind. ALL

 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING

 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE

 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT

 * BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT

 * OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS

 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST

 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,

 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY

 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN

 * IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

 * 

 * You acknowledge that Software is not designed, licensed or intended for

 * use in the design, construction, operation or maintenance of any nuclear

 * facility.

 */



/*

 * CHANGE LIST:

 * 

 * 11/27/06 - obtained code from http://www.cs.cf.ac.uk/Dave/JAVA/3d/3d.html

 * 11/27/06 - discovered code was distributed by Sun with JDK 1.4.2

 * 11/28/06 - added missing copyright header

 * 11/28/06 - formatted code to PhET standards

 * 11/28/06 - organized imports to PhET standards

 * 11/28/06 - made all member data private, added mutators and accessors where needed

 * 11/28/06 - added missing public modifiers to methods

 * 11/28/06 - added some comments

 * 11/28/06 - changed class name to Wireframe3D, since that's what it does

 * 11/28/06 - add ability to set an optional color, used instead of grayscale palette

 * 11/28/06 - add ability to specify the Stroke used to draw the lines in the wireframe

 * 11/28/06 - add ability to specify whether lines are antialiased

 * 11/28/06 - automatically call compress and findBB after parsing input stream

 * 11/28/06 - move parsing of input stream into its own method, called from constructor

 * 11/28/06 - remove bogus rotations applied to matrix in constructor, start with unit matrix

 * 11/28/06 - reorganize code and add section headers

 * 11/28/06 - add '_' prefix to all instance data, makes it easier for me to grok

 * 11/28/06 - improve precision of drawing by using Line2D.Float

 */



package edu.colorado.phet.hydrogenatom.model3d;



import java.awt.*;

import java.awt.geom.Line2D;

import java.io.*;



/**

 * Wireframe3D draws a wireframe 3D model.

 * <p>

 * This code was distributed with JDK 1.4.2 as class Model3D, 

 * in the Wireframe example applet.

 */

public class Wireframe3D {



    //----------------------------------------------------------------------------

    // Class data

    //----------------------------------------------------------------------------

    

    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );

    

    //----------------------------------------------------------------------------

    // Instance data

    //----------------------------------------------------------------------------

    

    private float _vert[];

    private int _tvert[];

    private int _nvert;

    private int _maxvert;

    private int _con[];

    private int _ncon;

    private int _maxcon;

    private boolean _transformed;

    private Matrix3D _matrix;

    private float _xmin, _xmax, _ymin, _ymax, _zmin, _zmax;

    private Color _palette[]; // color palette

    private boolean _antialias;

    private Stroke _stroke;



    //----------------------------------------------------------------------------

    // Constructors

    //----------------------------------------------------------------------------

    

    /*

     * Creates a 3D wireframe model by parsing an input stream.

     */

    public Wireframe3D( InputStream is ) throws IOException {

        _antialias = true; // enabled by default

        _stroke = DEFAULT_STROKE;

        initPaletteGray();

        _matrix = new Matrix3D();

        parseStream( is );

        compress();

        findBB();

    }

    

    //----------------------------------------------------------------------------

    // Mutators and accessors

    //----------------------------------------------------------------------------

    

    public Matrix3D getMatrix() {

        return _matrix;

    }

    

    public void setTransformed( boolean transformed ) {

        _transformed = transformed;

    }

    

    public void setColors( Color front, Color back ) {

        initPalette( front, back );

    }

    

    public void setStroke( Stroke stroke ) {

        _stroke = stroke;

    }

    

    public void setAntialias( boolean antialias ) {

        _antialias = antialias;

    }

    

    public boolean getAntialias() {

        return _antialias;

    }

    

    public float getXMax() {

        return _xmax;

    }

    

    public float getXMin() {

        return _xmin;

    }

    

    public float getYMax() {

        return _ymax;

    }

    

    public float getYMin() {

        return _ymin;

    }

    

    public float getZMax() {

        return _zmax;

    }

    

    public float getZMin() {

        return _zmin;

    }

    

    //----------------------------------------------------------------------------

    // Rendering

    //----------------------------------------------------------------------------

    

    /**

     * Paints this model to a graphics context.  It uses the matrix associated

     * with this model to map from model space to screen space.

     * 

     * @param g2

     */

    public void paint( Graphics2D g2 ) {



        if ( _vert == null || _nvert <= 0 ) {

            return;

        }

        

        transform();

        

        int lg = 0;

        int lim = _ncon;

        int c[] = _con;

        int v[] = _tvert;

        if ( lim <= 0 || _nvert <= 0 ) {

            return;

        }

        

        // Save graphics state

        Color saveColor = g2.getColor();

        Object saveAntialiasValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

        Stroke saveStroke = g2.getStroke();

        

        if ( _antialias ) {

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        }

        g2.setStroke( _stroke );

        

        // Draw wireframe

        Line2D line = new Line2D.Float();

        for ( int i = 0; i < lim; i++ ) {

            int T = c[i];

            int p1 = ( ( T >> 16 ) & 0xFFFF ) * 3;

            int p2 = ( T & 0xFFFF ) * 3;



            // choose a color from the palette based on depth

            int colorIndex = v[p1 + 2] + v[p2 + 2];

            if ( colorIndex < 0 ) {

                colorIndex = 0;

            }

            else if ( colorIndex > 15 ) {

                colorIndex = 15;

            }

            if ( colorIndex != lg ) {

                lg = colorIndex;

                g2.setColor( _palette[colorIndex] );

            }

            line.setLine( v[p1], v[p1 + 1], v[p2], v[p2 + 1] );

            g2.draw( line );

        }



        

        // Restore graphics state

        g2.setColor( saveColor );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, saveAntialiasValue );

        g2.setStroke( saveStroke );

    }



    //----------------------------------------------------------------------------

    // Private

    //----------------------------------------------------------------------------

    

    /*

     * Reads and parses an input stream that contains the description

     * of the wireframe model.

     */

    private void parseStream( InputStream is ) throws IOException {

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

                                addLine( prev - 1, n - 1 );

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

                        addLine( start - 1, prev - 1 );

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

    

    /*

     * Initializes the palette to a range of grays.

     */

    private void initPaletteGray() {

        _palette = new Color[16];

        for ( int i = 0; i < 16; i++ ) {

            int grey = (int) ( 170 * ( 1 - Math.pow( i / 15.0, 2.3 ) ) );

            _palette[i] = new Color( grey, grey, grey );

        }

    }

    

    /*

     * Initializes the palette to a range of colors.

     */

    private void initPalette( Color front, Color back ) {



        int fr = front.getRed();

        int fg = front.getGreen();

        int fb = front.getBlue();

        

        int br = back.getRed();

        int bg = back.getGreen();

        int bb = back.getBlue();

        

        float rdelta = ( fr - br ) / 16f;

        float gdelta = ( fg - bg ) / 16f;

        float bdelta = ( fb - bb ) / 16f;

        

        for ( int i = 0; i < 16; i++ ) {

            float r = ( fr - ( i * rdelta ) ) / 255f;

            float g = ( fg - ( i * gdelta ) ) / 255f;

            float b = ( fb - ( i * bdelta ) ) / 255f;

            _palette[i] = new Color( r, g, b );

        }

    }

    

    /*

     * Adds a vertex to this model.

     */

    private int addVert( float x, float y, float z ) {

        int i = _nvert;

        if ( i >= _maxvert ) {

            if ( _vert == null ) {

                _maxvert = 100;

                _vert = new float[_maxvert * 3];

            }

            else {

                _maxvert *= 2;

                float nv[] = new float[_maxvert * 3];

                System.arraycopy( _vert, 0, nv, 0, _vert.length );

                _vert = nv;

            }

        }

        i *= 3;

        _vert[i] = x;

        _vert[i + 1] = y;

        _vert[i + 2] = z;

        return _nvert++;

    }



    /*

     * Adds a line from vertex p1 to vertex p2.

     */

    private void addLine( int p1, int p2 ) {

        int i = _ncon;

        if ( p1 >= _nvert || p2 >= _nvert ) {

            return;

        }

        if ( i >= _maxcon ) {

            if ( _con == null ) {

                _maxcon = 100;

                _con = new int[_maxcon];

            }

            else {

                _maxcon *= 2;

                int nv[] = new int[_maxcon];

                System.arraycopy( _con, 0, nv, 0, _con.length );

                _con = nv;

            }

        }

        if ( p1 > p2 ) {

            int t = p1;

            p1 = p2;

            p2 = t;

        }

        _con[i] = ( p1 << 16 ) | p2;

        _ncon = i + 1;

    }



    /*

     * Transforms all the points in this model.

     */

    private void transform() {

        if ( _transformed || _nvert <= 0 ) {

            return;

        }

        if ( _tvert == null || _tvert.length < _nvert * 3 ) {

            _tvert = new int[_nvert * 3];

        }

        _matrix.transform( _vert, _tvert, _nvert );

        _transformed = true;

    }



    /*

     * ?

     */

    private void sort( int lo0, int hi0 ) {

        int a[] = _con;

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



    /*

     * Eliminates duplicate lines.

     */

    private void compress() {

        int limit = _ncon;

        int c[] = _con;

        sort( 0, _ncon - 1 );

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

        _ncon = d;

    }

    

    /**

     * Finds the bounding box of this model.

     */

    private void findBB() {

        if ( _nvert <= 0 ) {

            return;

        }

        float v[] = _vert;

        float xmin = v[0], xmax = xmin;

        float ymin = v[1], ymax = ymin;

        float zmin = v[2], zmax = zmin;

        for ( int i = _nvert * 3; ( i -= 3 ) > 0; ) {

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

        this._xmax = xmax;

        this._xmin = xmin;

        this._ymax = ymax;

        this._ymin = ymin;

        this._zmax = zmax;

        this._zmin = zmin;

    }

}

