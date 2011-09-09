// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class OutlinePText extends PNode {

    private String text;
    private Font font;
    private FontRenderContext fontRenderContext;
    private PhetPPath render;

    public OutlinePText( String text, Color fillColor, Color outlineColor, Font font, FontRenderContext fontRenderContext ) {
        this.text = text;
        this.font = font;
        this.fontRenderContext = fontRenderContext;
        this.render = new PhetPPath( fillColor, new BasicStroke( 1 ), outlineColor );
        addChild( render );
        update();
    }

    void update() {
        TextLayout textLayout = new TextLayout( text, font, fontRenderContext );
        render.setPathTo( textLayout.getOutline( new AffineTransform() ) );
    }

    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    public void setTextPaint( Color color ) {
        render.setPaint( color );
    }

    public void setFillColor( Color color ) {
        render.setStrokePaint( color );
    }

    public void setText( String valueText ) {
        this.text = valueText;
        update();
    }

    /**
     * Main routine that constructs a PhET Piccolo canvas in a window.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D STAGE_SIZE = new PDimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.50 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        OutlinePText outlineTextNode = new OutlinePText( "Hey", Color.PINK, Color.BLACK, new PhetFont( 24 ), new FontRenderContext( new AffineTransform(), true, true ) );
        canvas.addWorldChild( outlineTextNode );
        outlineTextNode.setOffset( 100, 100 );

        canvas.getLayer().addChild( new PhetPPath( new Rectangle2D.Double( -5, -5, 10, 10 ), Color.PINK ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame( "RNA Polymerase Shape Testing" );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
