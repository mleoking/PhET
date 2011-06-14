// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.wireframe;



import java.awt.*;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.EventListenerList;



/**

 * Wireframe3D draws a wireframe 3D model.

 * <p>

 * This code was distributed with JDK 1.4.2 as class Model3D, 

 * in the Wireframe example applet.

 */

public class Wireframe3D {



    //----------------------------------------------------------------------------

    // Debug

    //----------------------------------------------------------------------------

    

    private static final boolean PRINT_COLOR_PALETTE = false;

    

    //----------------------------------------------------------------------------

    // Public class data

    //----------------------------------------------------------------------------

    

    public static final String PROPERTY_BOUNDS = "bounds";

    public static final String PROPERTY_STROKE_WIDTH = "strokeWidth";

    

    //----------------------------------------------------------------------------

    // Private class data

    //----------------------------------------------------------------------------

    

    private static final float DEFAULT_STROKE_WIDTH = 1;

    

    private static final int COLOR_PALETTE_SIZE = 16;

    

    //----------------------------------------------------------------------------

    // Instance data

    //----------------------------------------------------------------------------

    

    // matrix that was last used to transform the model

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

    // coordinates that define the untransformed bounding box of the model

    private float _xmin, _xmax, _ymin, _ymax, _zmin, _zmax;

    // coordinates that define the transformed bounding box of the model

    private float _txmin, _txmax, _tymin, _tymax, _tzmin, _tzmax;

    // Has the model been transformed?

    private boolean _transformed;

    // are the untransformed bounds dirty?

    private boolean _untransformedBoundsDirty;

    // palette used to color the line segments

    private Color _palette[];

    // Is antialiasing enabled?

    private boolean _antialiased;

    // Width of Stroke used to draw the line segments

    private float _strokeWidth;

    // Stroke used to draw the line segments

    private Stroke _stroke;

    // reusable line, for rendering

    private Line2D _line;

    // listeners for changes to the model

    private EventListenerList _listenerList;



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

        _txmin = _txmax = _tymin = _tymax = _tzmin = _tzmax = 0;

        _transformed = false;

        _untransformedBoundsDirty = true;

        _palette = createColorPalette( Color.BLACK, Color.GRAY );

        _antialiased = true; // enabled by default

        _strokeWidth = DEFAULT_STROKE_WIDTH;

        _stroke = createStroke( _strokeWidth );

        _line = new Line2D.Float();

        _listenerList = new EventListenerList();

    }

    

    /**

     * Creates a 3D wireframe model with a set of verticies

     * but no lines connecting the vertices.

     * Call setLine to specify how the verticies are connected.

     * 

     * @param verticies

     */

    public Wireframe3D( Vertex3D[] verticies ) {

        this();

        for ( int i = 0; i < verticies.length; i++ ) {

            addVertex( verticies[i] );

        }

    }

    

    /**

     * Creates a 3D wireframe model with a set of verticies and lines.

     * 

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



    /**

     * Sets the matrix used to transform the model.

     * 

     * @param matrix

     */

    public void setMatrix( Matrix3D matrix ) {

        // Don't check ==, Matrix3D is mutable!

        _matrix = matrix;

        _transformed = false;

        transform();

    }

    

    /**

     * Gets the matrix used to transform the model.

     * 

     * @return Matrix3D

     */

    public Matrix3D getMatrix() {

        return _matrix;

    }

    

    /**

     * Sets the colors used to draw the lines segments in the wireframe.

     * Calling this method builds a color palette.

     * 

     * @param front

     * @param back

     */

    public void setColors( Color front, Color back ) {

        _palette = createColorPalette( front, back );

        if ( PRINT_COLOR_PALETTE ) {

            printColorPalette( _palette );

        }

    }

    

    /**

     * Sets the width of the stroke used to draw the lines in the wireframe.

     * 

     * @param strokeWidth

     */

    public void setStrokeWidth( float strokeWidth ) {

        if ( strokeWidth != _strokeWidth ) {

            _strokeWidth = strokeWidth;

            _stroke = createStroke( strokeWidth );

            notifyStrokeWidthChange(); // stroke width affects the screen bounds

        }

    }

    

    /**

     * Gets the width of the stroke used to draw the lines in the wireframe.

     * 

     * @return float

     */

    public float getStrokeWidth() {

        return _strokeWidth;

    }

    

    /**

     * Enables or disables antialiasing.

     * 

     * @param antialias

     */

    public void setAntialiased( boolean antialiased ) {

        _antialiased = antialiased;

    }

    

    /**

     * Is antialiasing enabled?

     * 

     * @return true or false

     */

    public boolean isAntialiased() {

        return _antialiased;

    }

    

    /*

     * Coordinates of the untransformed bounding box.

     */

    

    public float getXMax() {

        updateUntransformedBounds();

        return _xmax;

    }

    

    public float getXMin() {

        updateUntransformedBounds();

        return _xmin;

    }

    

    public float getYMax() {

        updateUntransformedBounds();

        return _ymax;

    }

    

    public float getYMin() {

        updateUntransformedBounds();

        return _ymin;

    }

    

    public float getZMax() {

        updateUntransformedBounds();

        return _zmax;

    }

    

    public float getZMin() {

        updateUntransformedBounds();

        return _zmin;

    }

    

    /*

     * Coordinates of the transformed bounding box.

     */

    

    public float getTXMax() {

        transform();

        return _txmax;

    }

    

    public float getTXMin() {

        transform();

        return _txmin;

    }

    

    public float getTYMax() {

        transform();

        return _tymax;

    }

    

    public float getTYMin() {

        transform();

        return _tymin;

    }

    

    public float getTZMax() {

        transform();

        return _tzmax;

    }

    

    public float getTZMin() {

        transform();

        return _tzmin;

    }



    //----------------------------------------------------------------------------

    // Rendering

    //----------------------------------------------------------------------------

    

    /**

     * Paints this model to a graphics context.

     * It uses the model's matrix to map from model space to screen space.

     * 

     * @param g2

     */

    public void paint( Graphics2D g2 ) {



        if ( _numberOfVerticies == 0 || _numberOfLines == 0 ) {

            return;

        }

        

        if ( !_transformed ) {

            transform();

        }

        

        // Save graphics state

        Color saveColor = g2.getColor();

        Object saveAntialiasValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

        Stroke saveStroke = g2.getStroke();

        

        if ( _antialiased ) {

            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        }

        g2.setStroke( _stroke );

        

        // Draw wireframe

        int prevColorIndex = -1;

        for ( int i = 0; i < _numberOfLines; i++ ) {

            

            int T = _lines[i];

            int p1 = ( ( T >> 16 ) & 0xFFFF ) * 3;

            int p2 = ( T & 0xFFFF ) * 3;



            // choose a color from the palette based on the line's average z-depth

            float zAvg = _transformedVerticies[p1 + 2] + ( ( _transformedVerticies[p1 + 2] - _transformedVerticies[p2 + 2] ) / 2 );

            int colorIndex = (int)( ( _palette.length - 1 ) * ( ( zAvg - _tzmin ) / ( _tzmax - _tzmin ) ) );

            assert( colorIndex >= 0 && colorIndex < _palette.length );

            

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

     * 

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

     * 

     * @param v

     * @return the total number of verticies

     */

    public int addVertex( Vertex3D v ) {

        return addVertex( v.getX(), v.getY(), v.getZ() );

    }

    

    /**

     * Adds a vertex.

     * 

     * @param x

     * @param y

     * @param z

     * @return the total number of verticies

     */

    public int addVertex( float x, float y, float z ) {

        

        // If we've run out of space, double the size of the verticies array.

        int capacity = _verticies.length / 3;

        if ( _numberOfVerticies >= capacity ) {

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

        

        _untransformedBoundsDirty = true;

        

        return _numberOfVerticies;

    }



    /**

     * Adds a line between 2 verticies.

     * 

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

        

        // If we've run out of space, double the size of the lines array.

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

        _untransformedBoundsDirty = true;

        _transformed = false;

    }



    /**

     * Eliminates duplicate lines.

     * Call this if you suspect that your model contains duplicate lines.

     */

    public void compress() {

        

        // Sort the lines

        quickSort( _lines, 0, _numberOfLines - 1 );

        

        // Remove duplicates

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

    // Transform

    //----------------------------------------------------------------------------

    

    /**

     * Transforms all the verticies in this model.

     * 

     * @param matrix

     */

    private void transform() {

        

        if ( _transformed || _numberOfVerticies == 0 ) {

            return;

        }

        

        // Resize the destination array if it's too small

        if ( _transformedVerticies.length < _verticies.length ) {

            _transformedVerticies = new float[_verticies.length];

        }

        

        // Transform the verticies

        _matrix.transform( _verticies, _transformedVerticies, _numberOfVerticies );

        

        // Update the bounds

        updateTransformedBounds();

        

        _transformed = true;



        notifyBoundsChange();

    }



    //----------------------------------------------------------------------------

    // Bounds

    //----------------------------------------------------------------------------

    

    /*

     * Computes the untransformed bounds of the model.

     */

    private void updateUntransformedBounds() {

        

        if ( !_untransformedBoundsDirty || _numberOfVerticies == 0 ) {

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

            else if ( x > xmax ) {

                xmax = x;

            }

            

            float y = v[i + 1];

            if ( y < ymin ) {

                ymin = y;

            }

            else if ( y > ymax ) {

                ymax = y;

            }

            

            float z = v[i + 2];

            if ( z < zmin ) {

                zmin = z;

            }

            else if ( z > zmax ) {

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

    

    /*

     * Computes the transformed bounds of the model.

     */

    private void updateTransformedBounds() {

        

        if ( _transformed || _numberOfVerticies == 0 ) {

            return;

        }

        

        float v[] = _transformedVerticies;

        float xmin = v[0], xmax = xmin;

        float ymin = v[1], ymax = ymin;

        float zmin = v[2], zmax = zmin;

        

        for ( int i = _numberOfVerticies * 3; ( i -= 3 ) > 0; ) {

            

            float x = v[i];

            if ( x < xmin ) {

                xmin = x;

            }

            else if ( x > xmax ) {

                xmax = x;

            }

            

            float y = v[i + 1];

            if ( y < ymin ) {

                ymin = y;

            }

            else if ( y > ymax ) {

                ymax = y;

            }

            

            float z = v[i + 2];

            if ( z < zmin ) {

                zmin = z;

            }

            else if ( z > zmax ) {

                zmax = z;

            }

        }

        _txmax = xmax;

        _txmin = xmin;

        _tymax = ymax;

        _tymin = ymin;

        _tzmax = zmax;

        _tzmin = zmin;

    }

    

    //----------------------------------------------------------------------------

    // Sorting

    //----------------------------------------------------------------------------

    

    /* 

     * Quick Sort implementation.

     * 

     * @param a

     * @param left

     * @param right

     */

    private static void quickSort( int a[], int left, int right ) {

        

        int leftIndex = left;

        int rightIndex = right;

        int partionElement;

        

        if ( right > left ) {



            /* Arbitrarily establishing partition element as the midpoint of

             * the array.

             */

            partionElement = a[( left + right ) / 2];



            // loop through the array until indices cross

            while ( leftIndex <= rightIndex ) {

                /* find the first element that is greater than or equal to

                 * the partionElement starting from the leftIndex.

                 */

                while ( ( leftIndex < right ) && ( a[leftIndex] < partionElement ) ) {

                    ++leftIndex;

                }



                /* find an element that is smaller than or equal to

                 * the partionElement starting from the rightIndex.

                 */

                while ( ( rightIndex > left ) && ( a[rightIndex] > partionElement ) ) {

                    --rightIndex;

                }



                // if the indexes have not crossed, swap

                if ( leftIndex <= rightIndex ) {

                    swap( a, leftIndex, rightIndex );

                    ++leftIndex;

                    --rightIndex;

                }

            }



            /* If the right index has not reached the left side of array

             * must now sort the left partition.

             */

            if ( left < rightIndex ) {

                quickSort( a, left, rightIndex );

            }



            /* If the left index has not reached the right side of array

             * must now sort the right partition.

             */

            if ( leftIndex < right ) {

                quickSort( a, leftIndex, right );

            }

        }

    }

    

    /*

     * Swaps two positions in an array.

     * 

     * @param a

     * @param i

     * @param j

     */

    private static void swap( int a[], int i, int j ) {

        int T;

        T = a[i];

        a[i] = a[j];

        a[j] = T;

    }

    

    //----------------------------------------------------------------------------

    // Strokes

    //----------------------------------------------------------------------------

    

    /*

     * Encapsulates all Stroke creation.

     * 

     * @param width

     */

    private static Stroke createStroke( float width ) {

        return new BasicStroke( width );

    }

    

    //----------------------------------------------------------------------------

    // Color palette

    //----------------------------------------------------------------------------

    

    /*

     * Creates a color palette that is an interpolation of 2 colors.

     * Uses integer precision for the color components.

     * 

     * @param front color that is closest to the camera

     * @param back color that is farthest from the camera

     */

    private static Color[] createColorPalette( Color front, Color back ) {



        Color[] palette = new Color[ COLOR_PALETTE_SIZE ];

        

        // front components

        int fr = front.getRed();

        int fg = front.getGreen();

        int fb = front.getBlue();

        

        // back components

        int br = back.getRed();

        int bg = back.getGreen();

        int bb = back.getBlue();

        

        // component deltas between front and back

        final float segments = (float) ( COLOR_PALETTE_SIZE - 1 );

        float rdelta = ( fr - br ) / segments;

        float gdelta = ( fg - bg ) / segments;

        float bdelta = ( fb - bb ) / segments;

        

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

     * 

     * @param palette

     */

    private static void printColorPalette( Color[] palette ) {

        System.out.println( "Palette size=" + palette.length );

        for ( int i = 0; i < palette.length; i++ ) {

            Color c = palette[i];

            System.out.println( i + " [" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + "]" );

        }

    }

    

    //----------------------------------------------------------------------------

    // Event handling

    //----------------------------------------------------------------------------



    /**

     * Adds a PropertyChangeListener.

     *

     * @param listener the listener

     */

    public void addPropertyChangeListener( PropertyChangeListener listener ) {

        _listenerList.add( PropertyChangeListener.class, listener );

    }



    /**

     * Removes a PropertyChangeListener.

     *

     * @param listener the listener

     */

    public void removePropertyChangeListener( PropertyChangeListener listener ) {

        _listenerList.remove( PropertyChangeListener.class, listener );

    }



    /**

     * Fires a PropertyChangeEvent.

     *

     * @param event the event

     */

    private void firePropertyChangeEvent( PropertyChangeEvent event ) {

        Object[] listeners = _listenerList.getListenerList();

        for( int i = 0; i < listeners.length; i += 2 ) {

            if( listeners[i] == PropertyChangeListener.class ) {

                ( (PropertyChangeListener)listeners[i + 1] ).propertyChange( event );

            }

        }

    }

    

    /*

     * Fires an event indicating that the model's bounds have changed.

     */

    private void notifyBoundsChange() {

        PropertyChangeEvent event = new PropertyChangeEvent( this, PROPERTY_BOUNDS, null, null );

        firePropertyChangeEvent( event );

    }

    

    /*

     * Fires an event indicating that the model's stroke width has changed.

     */

    private void notifyStrokeWidthChange() {

        PropertyChangeEvent event = new PropertyChangeEvent( this, PROPERTY_STROKE_WIDTH, null, null );

        firePropertyChangeEvent( event );

    }

}

