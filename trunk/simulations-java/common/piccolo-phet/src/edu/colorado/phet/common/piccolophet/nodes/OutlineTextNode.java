// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Locale;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

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
public class OutlineTextNode extends PNode {

    // Font render context used for outline text.  Honestly, I (jblanco) don't
    // know much about font render contexts, but I found a Piccolo node called
    // PStyledText that did this, so I tried it, and it seems to work okay.
    // It has been tested on Windows 7 and Mac and works well.
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
    public OutlineTextNode( String text, Font font, Color fillColor, Color outlineColor, double outlineStrokeWidth ) {
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

    /**
     * Test harness.
     *
     * @param args
     */
    public static void main( String[] args ) {

        final Dimension stageSize = new Dimension( 1024, 768 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( stageSize );
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        final double xMargin = 5;
        final double yMargin = 10;
        final double ySpacing = 5;
        final Color fillColor = Color.YELLOW;
        final Color strokeColor = Color.BLACK;
        final double strokeWidth = 1;
        final int smallestFontSize = 12; // display integer multiples of this font size

        // English, all letters (upper and lowercase) and numbers, in various font sizes
        PNode lastNode = null;
        for ( int i = 0; i < 5; i++ ) {

            PhetFont font = new PhetFont( Font.PLAIN, ( i + 1 ) * smallestFontSize );

            OutlineTextNode uppercaseNode = new OutlineTextNode( "ABCDEFGHIJKLMNOPQRSTUVWXYZ", font, fillColor, strokeColor, strokeWidth );
            uppercaseNode.setOffset( xMargin, ( lastNode == null ) ? yMargin : lastNode.getFullBoundsReference().getMaxY() + ySpacing );
            canvas.addWorldChild( uppercaseNode );

            OutlineTextNode lowercaseNode = new OutlineTextNode( "abcdefghijklmnopqrstuvwxyz", font, fillColor, strokeColor, strokeWidth );
            lowercaseNode.setOffset( xMargin, uppercaseNode.getFullBoundsReference().getMaxY() + ySpacing );
            canvas.addWorldChild( lowercaseNode );

            OutlineTextNode numbersNode = new OutlineTextNode( "0123456789", font, fillColor, strokeColor, strokeWidth );
            numbersNode.setOffset( xMargin, lowercaseNode.getFullBoundsReference().getMaxY() + ySpacing );
            canvas.addWorldChild( numbersNode );

            lastNode = numbersNode;
        }

        // Arabic
        OutlineTextNode outlineTextNode5 = new OutlineTextNode( "\uFE9D\u06B0\u06AA\uFEB5", PhetFont.getPreferredFont( new Locale( "ar" ), Font.PLAIN, 64 ), fillColor, strokeColor, strokeWidth );
        outlineTextNode5.setOffset( xMargin, lastNode.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode5 );

        // Chinese
        OutlineTextNode outlineTextNode6 = new OutlineTextNode( "\u4e2d\u56fd\u8bdd\u4e0d", PhetFont.getPreferredFont( new Locale( "zh" ), Font.PLAIN, 64 ), fillColor, strokeColor, strokeWidth );
        outlineTextNode6.setOffset( xMargin, outlineTextNode5.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode6 );

        // Boiler plate Piccolo app stuff.
        JFrame frame = new JFrame( "OutlineTextNode.main" );
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }
}
