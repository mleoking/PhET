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

 * 11/29/06 - give more descriptive names to all member data

 * 11/29/06 - change precision of matrix transform from int to float

 */



package edu.colorado.phet.hydrogenatom.wireframe;



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

    

    private static final float DEFAULT_STROKE_WIDTH = 1;

    

    //----------------------------------------------------------------------------

    // Instance data

    //----------------------------------------------------------------------------

    

    // the model's transformation matrix

    private Matrix3D _matrix;

    // storage for verticies, may be larger than actual number of verticies

    private float _verticies[];

    // actual number of verticies

    private int _numberOfVerticies;

    // storage for lines, may be larger than actual number of lines

    private int _lines[];

    // actual number of lines

    private int _numberOfLines;

    // storage for transformed verticies, may be larger than actual number of verticies

    private float _transformedVerticies[];

    // bounds of the model

    private float _xmin, _xmax, _ymin, _ymax, _zmin, _zmax;

    // has the model been transformed?

    private boolean _transformed;

    // palette used to color the line segments

    private Color _palette[];

    // Is antialiasing enabled?

    private boolean _antialias;

    // Width of Stroke used to draw the line segments

    private float _strokeWidth;

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

        // Defaults for all instance data

        _matrix = new Matrix3D();

        _verticies = new float[300];

        _numberOfVerticies = 0;

        _lines = new int[100];

        _numberOfLines = 0;

        _transformedVerticies = new float[_verticies.length];

        _xmin = _xmax = _ymin = _ymax = _zmin = _zmax = 0;

        _transformed = false;

        _palette = createGrayPalette();

        _antialias = true; // enabled by default

        _strokeWidth = DEFAULT_STROKE_WIDTH;

        _stroke = createStroke( _strokeWidth );

        _line = new Line2D.Float();

    }

    

    /**

     * Creates a 3D wireframe model with a set of verticies

     * but no lines connecting the vertices.

     * Call setLine to specify how the verticies are connected.

     */

    public Wireframe3D( Vertex3D[] verticies ) {

        this();

        for ( int i = 0; i < verticies.length; i++ ) {

            addVertex( verticies[i] );

        }

    }

    

    /**

     * Creates a 3D wireframe model with a set of verticies and lines.

     * @param verticies

     * @param lines indicies into vertices, consecutive entries define a line

     */

    public Wireframe3D( Vertex3D[] verticies, int[] lines ) {

        this( verticies );

        for ( int i = 0; i < lines.length - 1; i += 2 ) {

            int i1 = lines[i];

            int i2 = lines[i + 1];

            if ( i1 < 0 || i1 > verticies.length - 1 ) {

                throw new IllegalArgumentException( "line index out of range: " + i1 );

            }

            if ( i2 < 0 || i2 > verticies.length - 1 ) {

                throw new IllegalArgumentException( "line index out of range: " + i2 );

            }

            addLine( i1, i2 );

        }

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

        printColorPalette( _palette );//XXX

    }

    

    public void setStrokeWidth( float strokeWidth ) {

        if ( strokeWidth != _strokeWidth ) {

            _strokeWidth = strokeWidth;

            _stroke = createStroke( strokeWidth );

        }

        updateBounds();

    }

    

    public void setAntialias( boolean antialias ) {

        _antialias = antialias;

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



        if ( _numberOfVerticies == 0 || _numberOfLines == 0 ) {

            return;

        }

        

        transform();



        // Save graphics state

        Color saveColor = g2.getColor();

        Object saveAntialiasValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

        Stroke saveStroke = g2.getStroke();

        

        if ( _antialias ) {

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        }

        g2.setStroke( _stroke );

        

        // Draw wireframe

        int prevColorIndex = -1;

        for ( int i = 0; i < _numberOfLines; i++ ) {

            

            int T = _lines[i];

            int p1 = ( ( T >> 16 ) & 0xFFFF ) * 3;

            int p2 = ( T & 0xFFFF ) * 3;



            // choose a color from the palette based on depth

            int colorIndex = (int)( _transformedVerticies[p1 + 2] + _transformedVerticies[p2 + 2] );

            if ( colorIndex < 0 ) {

                colorIndex = 0;

            }

            else if ( colorIndex > 15 ) {

                colorIndex = 15;

            }

            

            // if the color index has changed, set the graphics context

            if ( colorIndex != prevColorIndex ) {

                prevColorIndex = colorIndex;

                g2.setColor( _palette[colorIndex] );

            }

            

            // draw the line

            _line.setLine( _transformedVerticies[p1], _transformedVerticies[p1 + 1], _transformedVerticies[p2], _transformedVerticies[p2 + 1] );

            g2.draw( _line );

        }

        

        // Restore graphics state

        g2.setColor( saveColor );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, saveAntialiasValue );

        g2.setStroke( saveStroke );

    }



    //----------------------------------------------------------------------------

    // Vertex and line management

    //----------------------------------------------------------------------------

    

    /**

     * Adds multiple verticies.

     * @param verticies

     * @return the total number of verticies

     */

    public int addVerticies( Vertex3D[] verticies ) {

        int numberOfVerticies = 0;

        for ( int i = 0; i < verticies.length; i++ ) {

            numberOfVerticies = addVertex( verticies[i] );

        }

        return numberOfVerticies;

    }

    

    /**

     * Adds a vertex.

     * @param v

     * @return the total number of verticies

     */

    public int addVertex( Vertex3D v ) {

        return addVertex( v.getX(), v.getY(), v.getZ() );

    }

    

    /**

     * Adds a vertex.

     * @param x

     * @param y

     * @param z

     * @return the total number of verticies

     */

    public int addVertex( float x, float y, float z ) {

        

        // If we've run out of space, resize the verticies array.

        if ( _numberOfVerticies >= _verticies.length / 3 ) {

            float newVerticies[] = new float[_verticies.length * 2];

            System.arraycopy( _verticies, 0, newVerticies, 0, _verticies.length );

            _verticies = newVerticies;

        }

        

        // Add the vertex

        int i = _numberOfVerticies * 3;

        _verticies[i] = x;

        _verticies[i + 1] = y;

        _verticies[i + 2] = z;

        _numberOfVerticies++;

        

        _transformed = false;

        

        return _numberOfVerticies;

    }



    /**

     * Adds a line between 2 verticies.

     * @param index1 index of first vertex

     * @param index2 index of second vertex

     * @return the total number of lines

     */

    public int addLine( int index1, int index2 ) {

        // Validate indicies

        if ( index1 >= _numberOfVerticies ) {

            throw new IllegalArgumentException( "line index1 out of range: " + index1 );

        }

        if ( index2 >= _numberOfVerticies ) {

            throw new IllegalArgumentException( "line index2 out of range: " + index2 );

        }

        

        // If we've run out of space, resize the lines array.

        if ( _numberOfLines >= _lines.length ) {

                int newLines[] = new int[_lines.length * 2];

                System.arraycopy( _lines, 0, newLines, 0, _lines.length );

                _lines = newLines;

        }

        

        // Add the line

        if ( index1 > index2 ) {

            int tmp = index1;

            index1 = index2;

            index2 = tmp;

        }

        _lines[_numberOfLines] = ( index1 << 16 ) | index2;

        _numberOfLines++;

        

        return _numberOfLines;

    }



    /**

     * Resets the wireframe to have no verticies or lines.

     */

    public void reset() {

        _numberOfVerticies = 0;

        _numberOfLines = 0;

        _transformed = false;

    }

    

    /**

     * Call this after you're done adding verticies and lines.

     */

    public void done() {

        updateBounds();

    }



    /**

     * Call this if you suspect that your dataset might contain duplicate lines.

     */

    public void compress() {

        sort( 0, _numberOfLines - 1 );

        int newNumberOfLines = 0;

        int index = 0;

        int prevIndex = -1;

        for ( int i = 0; i < _numberOfLines; i++ ) {

            index = _lines[i];

            if ( prevIndex != index ) {

                _lines[newNumberOfLines] = index;

                newNumberOfLines++;

            }

            prevIndex = index;

        }

        _numberOfLines = newNumberOfLines;

    }

    

    //----------------------------------------------------------------------------

    // Private

    //----------------------------------------------------------------------------

    

    /*

     * Transforms all the points in this model.

     */

    private void transform() {

        if ( _transformed || _numberOfVerticies <= 0 ) {

            return;

        }

        // Resize the destination array if it's too small

        if ( _transformedVerticies.length < _numberOfVerticies * 3 ) {

            _transformedVerticies = new float[_numberOfVerticies * 3];

        }

        _matrix.transform( _verticies, _transformedVerticies, _numberOfVerticies );

        _transformed = true;

    }



    /*

     * ?

     */

    private void sort( final int lo0, final int hi0 ) {

        int lo = lo0;

        int hi = hi0;

        int tmp = 0;

        if ( lo >= hi ) {

            return;

        }

        int mid = _lines[( lo + hi ) / 2];

        while ( lo < hi ) {

            while ( lo < hi && _lines[lo] < mid ) {

                lo++;

            }

            while ( lo < hi && _lines[hi] >= mid ) {

                hi--;

            }

            if ( lo < hi ) {

                tmp = _lines[lo];

                _lines[lo] = _lines[hi];

                _lines[hi] = tmp;

            }

        }

        if ( hi < lo ) {

            tmp = hi;

            hi = lo;

            lo = tmp;

        }

        sort( lo0, lo );

        sort( lo == lo0 ? lo + 1 : lo, hi0 );

    }

    

    /*

     * Finds the bounding box of this model.

     */

    private void updateBounds() {

        if ( _numberOfVerticies <= 0 ) {

            return;

        }

        float v[] = _verticies;

        float xmin = v[0], xmax = xmin;

        float ymin = v[1], ymax = ymin;

        float zmin = v[2], zmax = zmin;

        for ( int i = _numberOfVerticies * 3; ( i -= 3 ) > 0; ) {

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

        _xmax = xmax + _strokeWidth;

        _xmin = xmin - _strokeWidth;

        _ymax = ymax + _strokeWidth;

        _ymin = ymin - _strokeWidth;

        _zmax = zmax + _strokeWidth;

        _zmin = zmin - _strokeWidth;

    }

    

    //----------------------------------------------------------------------------

    // Static

    //----------------------------------------------------------------------------

    

    /*

     * Encapsulates all Stroke creation.

     */

    private static Stroke createStroke( float width ) {

        return new BasicStroke( width );

    }

    

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

     * Prints a color palette to System.out.

     */

    private static void printColorPalette( Color[] palette ) {

        System.out.println( "Palette size=" + palette.length );

        for ( int i = 0; i < palette.length; i++ ) {

            Color c = palette[i];

            System.out.println( i + " [" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "]" );

        }

    }

}

