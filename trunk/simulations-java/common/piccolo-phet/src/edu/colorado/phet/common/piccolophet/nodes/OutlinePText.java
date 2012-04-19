// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.util.Locale;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * PNode that represents outlined text.  This is often useful for making text
 * more visible when shown against a complex or gradient background.
 * <p/>
 * There has been some debate amongst the PhET developers as to whether or not
 * it is wise to use this, since it may not look that great for other
 * languages, so it is probably best to use it sparingly.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class OutlinePText extends PNode {

    // Font render context used for outline text.  Honestly, I (jblanco) don't
    // know much about font render contexts, but I found a Piccolo node called
    // PStyledText that did this, so I tried it, and it seems to work okay.
    // It has been testing on Windows 7 and Mac and works well.
    private static FontRenderContext SWING_FRC = new FontRenderContext( null, true, false );
    private PPath textPPath;
    private Font font;

    /**
     * Constructor.
     *
     * @param text
     * @param font
     * @param fillColor
     * @param outlineColor
     * @param outlineStrokeWidth
     */
    public OutlinePText( String text, Font font, Color fillColor, Color outlineColor, double outlineStrokeWidth ) {
        this.font = font;
        textPPath = new PhetPPath( fillColor, new BasicStroke( (float) outlineStrokeWidth ), outlineColor );
        TextLayout textLayout = new TextLayout( text, font, SWING_FRC );
        textPPath.setPathTo( textLayout.getOutline( new AffineTransform() ) );
        addChild( new ZeroOffsetNode( textPPath ) ); // Make sure that this node's origin is in the upper left corner.
        textPPath.setPickable( false ); // #3313 temporary workaround; this class should be pickable, but not individual components
    }

    public void setText( String text ) {
        TextLayout textLayout = new TextLayout( text, font, SWING_FRC );
        textPPath.setPathTo( textLayout.getOutline( new AffineTransform() ) );
    }

    public void setFillColor( Color color ) {
        textPPath.setPaint( color );
    }

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        Dimension2D STAGE_SIZE = new PDimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, STAGE_SIZE ) );

        // Add the text.
        OutlinePText outlineTextNode1 = new OutlinePText( "36 point plain", new PhetFont( 36 ), Color.PINK, Color.BLACK, 1 );
        outlineTextNode1.setOffset( 50, 50 );
        canvas.addWorldChild( outlineTextNode1 );
        OutlinePText outlineTextNode2 = new OutlinePText( "48 point bold", new PhetFont( 48, true ), Color.YELLOW, Color.BLACK, 1 );
        outlineTextNode2.setOffset( 50, 100 );
        canvas.addWorldChild( outlineTextNode2 );
        OutlinePText outlineTextNode3 = new OutlinePText( "24 point bold", new PhetFont( 24, true ), Color.GREEN, Color.BLACK, 1 );
        outlineTextNode3.setOffset( 50, 150 );
        canvas.addWorldChild( outlineTextNode3 );
        OutlinePText outlineTextNode4 = new OutlinePText( "72 point bold", new PhetFont( 72, true ), Color.MAGENTA, Color.BLACK, 2 );
        outlineTextNode4.setOffset( 50, 250 );
        canvas.addWorldChild( outlineTextNode4 );
        OutlinePText outlineTextNode5 = new OutlinePText( "\uFE9D\u06B0\u06AA\uFEB5", PhetFont.getPreferredFont( new Locale( "ar" ), Font.BOLD, 64 ), Color.ORANGE, Color.BLACK, 2 );
        outlineTextNode5.setOffset( 50, outlineTextNode4.getFullBoundsReference().getMaxY() );
        canvas.addWorldChild( outlineTextNode5 );
        OutlinePText outlineTextNode6 = new OutlinePText( "\u4e2d\u56fd\u8bdd\u4e0d", PhetFont.getPreferredFont( new Locale( "zh" ), Font.BOLD, 64 ), Color.CYAN, Color.BLACK, 2 );
        outlineTextNode6.setOffset( 50, outlineTextNode5.getFullBoundsReference().getMaxY() + 20 );
        canvas.addWorldChild( outlineTextNode6 );

        outlineTextNode1.setText( "New thing." );

        // Boiler plate Piccolo app stuff.
        JFrame frame = new JFrame( "Outline Text Test" );
        frame.setContentPane( canvas );
        frame.setSize( (int) STAGE_SIZE.getWidth(), (int) STAGE_SIZE.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
