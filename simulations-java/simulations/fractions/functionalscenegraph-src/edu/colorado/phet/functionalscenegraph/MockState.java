package edu.colorado.phet.functionalscenegraph;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Terminals need to know certain environment aspects (as is known for graphics2d), they are passed in the MockState.
 *
 * @author Sam Reid
 */
public class MockState implements GraphicsContext {
    //TODO: this could be different that Graphics2D font, should be rectified
    private Font font;

    //TODO: mutable, should be wrapped
    private AffineTransform transform = new AffineTransform();

    public MockState() {
        this( new PhetFont() );
    }

    public MockState( final Font font ) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public FontRenderContext getFontRenderContext() {
        return new FontRenderContext( new AffineTransform(), true, true );
    }

    @Override public AffineTransform getTransform() {
        return transform;
    }

    //Maybe should be preconcat
    @Override public void transform( final AffineTransform transform ) {
        transform.concatenate( transform );
    }

    @Override public void setTransform( final AffineTransform orig ) {
        this.transform = orig;
    }

    public void setFont( final Font font ) {
        this.font = font;
    }
}