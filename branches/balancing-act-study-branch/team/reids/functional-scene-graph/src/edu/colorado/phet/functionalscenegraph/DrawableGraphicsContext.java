package edu.colorado.phet.functionalscenegraph;

import java.awt.Paint;
import java.awt.Shape;

/**
 * Methods for drawing onto a graphics.
 *
 * @author Sam Reid
 */
public interface DrawableGraphicsContext extends GraphicsContext {
    void draw( Shape shape );

    void drawString( String text, int x, int y );

    void fill( Shape shape );

    //TODO: does paint have equals/hashcode, etc?
    Paint getPaint();

    void setPaint( Paint paint );
}