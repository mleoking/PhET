/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.util;

import java.awt.*;

import edu.colorado.phet.forces1d.common_force1d.view.ApparatusPanel2;
import edu.colorado.phet.forces1d.common_force1d.view.phetgraphics.PhetGraphic;

/**
 * A PhetGraphic that draws a grid of regularly spaced lines. Intended for use during program
 * development to aid in design and layout.
 */
public class LineGrid extends PhetGraphic implements ApparatusPanel2.ChangeListener {
    private static Stroke defaultStroke = new BasicStroke( 1f );
    private int dx;
    private int dy;
    private Dimension canvasSize;
    private Paint color;
    private Stroke stroke;
    private double alpha = 1;
    private ApparatusPanel2 appPanel;

    public LineGrid( ApparatusPanel2 component, int dx, int dy, Color color ) {
        this( component, dx, dy, color, defaultStroke );
    }

    public LineGrid( ApparatusPanel2 component, int dx, int dy, Color color, Stroke stroke ) {
        super( component );
        appPanel = component;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
        this.stroke = stroke;

        setCanvasSize( component.getCanvasSize() );
        setIgnoreMouse( true );
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setPaint( Paint paint ) {
        color = paint;
    }

    public void setAlpha( double alpha ) {
        this.alpha = alpha;
    }

    public void setCanvasSize( Dimension size ) {
        canvasSize = size;
    }

    protected Rectangle determineBounds() {
        setCanvasSize( appPanel.getCanvasSize() );
        return new Rectangle( canvasSize );
    }

    public void paint( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );

        g2.setPaint( color );
        g2.setStroke( stroke );
        GraphicsUtil.setAlpha( g2, alpha );
        for ( int x = dx; x < canvasSize.getWidth(); x += dx ) {
            g2.drawLine( x, 0, x, (int) canvasSize.getHeight() );
            for ( int y = dy; y < canvasSize.getHeight(); y += dy ) {
                g2.drawLine( 0, y, (int) canvasSize.getWidth(), y );
            }
        }
        gs.restoreGraphics();
    }

    public void canvasSizeChanged( ApparatusPanel2.ChangeEvent event ) {
        setCanvasSize( event.getCanvasSize() );
    }
}