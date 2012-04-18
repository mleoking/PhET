package edu.colorado.phet.functionalscenegraph;

import fj.Effect;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Methods for graphics state, for getting bounds and/or rendering.
 *
 * @author Sam Reid
 */
public interface GraphicsContext {
    void setFont( Font font );

    Font getFont();

    FontRenderContext getFontRenderContext();

    AffineTransform getTransform();

    void transform( AffineTransform transform );

    void setTransform( AffineTransform orig );

    void setDragHandler( Effect<Vector2D> effect );
}