package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

public class PhetOutlineTextGraphic extends PhetShapeGraphic {
    private String text;
    private Font font;

    public PhetOutlineTextGraphic( Component component, Font font, String text, Color fillColor, Stroke stroke, Color strokeColor ) {
        super( component );
        this.text = text;
        this.font = font;
        setShape( createTextShape() );
        setColor( fillColor );
        setStroke( stroke );
        setBorderColor( strokeColor );
        component.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                setShape( createTextShape() );
            }
        } );
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setShape( createTextShape() );
            }
        } );
    }

    private Shape createTextShape() {
        FontRenderContext frc = createFontRenderContext();

        if( frc != null ) {
            TextLayout textLayout = new TextLayout( text, font, frc );
            return textLayout.getOutline( new AffineTransform() );
        }

        return null;
    }

    private FontRenderContext createFontRenderContext() {
        Graphics2D g2 = (Graphics2D)getComponent().getGraphics();
        if( g2 != null ) {
            FontRenderContext frc = g2.getFontRenderContext();
            if( frc != null ) {
                return frc;
            }
        }
        return new FontRenderContext( new AffineTransform(), true, false );
    }
}