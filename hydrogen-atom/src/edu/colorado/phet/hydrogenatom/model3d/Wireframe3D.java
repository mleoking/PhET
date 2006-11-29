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

 * 11/28/06 - add Point3D inner class

 * 11/28/06 - assume one continuos piece of wire, replace InputStream with Point3D[] in constructor

 * 11/28/06 - make addVertex and addLine public

 * 11/28/06 - fix bug in paint method, prevIndex should be initialzed to -1

 * 11/28/06 - rename some methods

 * 11/28/06 - javadoc and comments

 */



package edu.colorado.phet.hydrogenatom.model3d;



import java.awt.*;

import java.awt.geom.Line2D;



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

    // Data structures

    //----------------------------------------------------------------------------

    

    public static class Point3D {

        public double x, y, z;

        public Point3D( double x, double y, double z ) {

            this.x = x;

            this.y = y;

            this.z = z;

        }

    }

    

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

    // has the model been transformed?

    private boolean _transformed;

    // matrix associated with the model

    private Matrix3D _matrix;

    // bounds of the model

    private float _xmin, _xmax, _ymin, _ymax, _zmin, _zmax;

    // palette used to color the line segments

    private Color _palette[];

    // Is antialiasing enabled?

    private boolean _antialias;

    // Stroke used to draw the line segments

    private Stroke _stroke;

    // reusable line, for rendering

    private Line2D _line;



    //----------------------------------------------------------------------------

    // Constructors

    //----------------------------------------------------------------------------

    

    /**

     * Creates a 3D wireframe model with no verticies or lines.

     */

    public Wireframe3D() {

        _antialias = true; // enabled by default

        _stroke = DEFAULT_STROKE;

        _palette = createGrayPalette();

        _matrix = new Matrix3D();

        _line = new Line2D.Float();

    }

    

    /**

     * Creates a 3D wireframe model from a set of points.

     * Assumes that the points are one continuous piece of wire.

     */

    public Wireframe3D( Point3D[] points ) {

        this();

        for ( int i = 0; i < points.length; i++ ) {

            Point3D p = points[i];

            addVertex( (float) p.x, (float) p.y, (float) p.z );

        }

        for ( int i = 0; i < points.length - 1; i++ ) {

            addLine( i, i + 1 );

        }

        done();

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

        _palette = createColorPalette( front, back );

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

        

        int prevIndex = -1;

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

            if ( colorIndex != prevIndex ) {

                prevIndex = colorIndex;

                g2.setColor( _palette[colorIndex] );

            }

            

            _line.setLine( v[p1], v[p1 + 1], v[p2], v[p2 + 1] );

            g2.draw( _line );

        }

        

        // Restore graphics state

        g2.setColor( saveColor );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, saveAntialiasValue );

        g2.setStroke( saveStroke );

    }



    //----------------------------------------------------------------------------

    // Private

    //----------------------------------------------------------------------------

    

    /**

     * Adds a vertex.

     * @param x

     * @param y

     * @param z

     */

    public int addVertex( float x, float y, float z ) {

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



    /**

     * Adds a line between 2 verticies.

     * @param index1 index of first vertex

     * @param index2 index of second vertex

     */

    public void addLine( int index1, int index2 ) {

        int i = _ncon;

        if ( index1 >= _nvert || index2 >= _nvert ) {

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

        if ( index1 > index2 ) {

            int t = index1;

            index1 = index2;

            index2 = t;

        }

        _con[i] = ( index1 << 16 ) | index2;

        _ncon = i + 1;

    }



    /**

     * Call this after you're done adding verticies and lines.

     */

    public void done() {

        compress();

        updateBounds();

    }

    

    //----------------------------------------------------------------------------

    // Private

    //----------------------------------------------------------------------------

    

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

    

    /*

     * Finds the bounding box of this model.

     */

    private void updateBounds() {

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

        _xmax = xmax;

        _xmin = xmin;

        _ymax = ymax;

        _ymin = ymin;

        _zmax = zmax;

        _zmin = zmin;

    }

    

    //----------------------------------------------------------------------------

    // Static

    //----------------------------------------------------------------------------

    

    /*

     * Creates a grayscale palette.

     */

    private static Color[] createGrayPalette() {

        Color[] palette = new Color[16];

        for ( int i = 0; i < 16; i++ ) {

            int grey = (int) ( 170 * ( 1 - Math.pow( i / 15.0, 2.3 ) ) );

            palette[i] = new Color( grey, grey, grey );

        }

        return palette;

    }

    

    /*

     * Creates a color palette that is an interpolation of 2 colors.

     * Uses integer precision for the color components.

     * 

     * @param front color that is closest to the camera

     * @param back color that is farthest from the camera

     */

    private static Color[] createColorPalette( Color front, Color back ) {



        Color[] palette = new Color[16];

        

        // front components

        int fr = front.getRed();

        int fg = front.getGreen();

        int fb = front.getBlue();

        

        // back components

        int br = back.getRed();

        int bg = back.getGreen();

        int bb = back.getBlue();

        

        // component deltas between front and back

        float rdelta = ( fr - br ) / 16f;

        float gdelta = ( fg - bg ) / 16f;

        float bdelta = ( fb - bb ) / 16f;

        

        // interpolate color components from front to back

        for ( int i = 0; i < 16; i++ ) {

            float r = ( fr - ( i * rdelta ) ) / 255f;

            float g = ( fg - ( i * gdelta ) ) / 255f;

            float b = ( fb - ( i * bdelta ) ) / 255f;

            palette[i] = new Color( r, g, b );

        }

        

        return palette;

    }

    

    /*

     * Creates a color palette that is an interpolation of 2 colors.

     * Uses floating point precision, works better when front and back

     * colors are very similar, but is also slightly more expensive.

     * 

     * @param front color that is closest to the camera

     * @param back color that is farthest from the camera

     */

    private static Color[] createColorPaletteFloat( Color front, Color back ) {



        final int paletteSize = 16;

        

        Color[] palette = new Color[paletteSize];

        

        // front components

        float[] fc = front.getComponents( null );

        float fr = fc[0];

        float fg = fc[1];

        float fb = fc[2];

        float fa = fc[3];

        

        // back components

        float[] bc = back.getComponents( null );

        float br = bc[0];

        float bg = bc[1];

        float bb = bc[2];

        float ba = bc[3];

        

        // component deltas between front and back

        float rdelta = ( fr - br ) / paletteSize;

        float gdelta = ( fg - bg ) / paletteSize;

        float bdelta = ( fb - bb ) / paletteSize;

        float adelta = ( fa - ba ) / paletteSize;

        

        // interpolate color components from front to back

        for ( int i = 0; i < 16; i++ ) {

            float r = ( fr - ( i * rdelta ) );

            float g = ( fg - ( i * gdelta ) );

            float b = ( fb - ( i * bdelta ) );

            float a = ( fa - ( i * adelta ) );

            palette[i] = new Color( r, g, b, a );

        }

        

        return palette;

    }

}

