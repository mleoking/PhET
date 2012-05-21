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

        final Dimension stageSize = new Dimension( 850, 700 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( stageSize );
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        final double xMargin = 5;
        final double ySpacing = 5;

        // English, all alphabetical and numeric chars in various font sizes
        OutlineTextNode outlineTextNode0 = new OutlineTextNode( "The quick brown fox jumps over the lazy dog 0123456789", new PhetFont( 12 ), Color.ORANGE, Color.BLACK, 1 );
        outlineTextNode0.setOffset( xMargin, 25 );
        canvas.addWorldChild( outlineTextNode0 );
        OutlineTextNode outlineTextNode1 = new OutlineTextNode( "The quick brown fox jumps over the lazy dog 0123456789", new PhetFont( 24 ), Color.PINK, Color.BLACK, 1 );
        outlineTextNode1.setOffset( xMargin, outlineTextNode0.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode1 );
        OutlineTextNode outlineTextNode2a = new OutlineTextNode( "The quick brown fox jumps over", new PhetFont( Font.BOLD, 36 ), Color.YELLOW, Color.BLACK, 1 );
        outlineTextNode2a.setOffset( xMargin, outlineTextNode1.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode2a );
         OutlineTextNode outlineTextNode2b = new OutlineTextNode( "the lazy dog 0123456789", new PhetFont( Font.BOLD, 36 ), Color.YELLOW, Color.BLACK, 1 );
        outlineTextNode2b.setOffset( xMargin, outlineTextNode2a.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode2b );
        OutlineTextNode outlineTextNode3a = new OutlineTextNode( "The quick brown fox jumps over", new PhetFont( Font.BOLD, 48 ), Color.GREEN, Color.BLACK, 1 );
        outlineTextNode3a.setOffset( xMargin, outlineTextNode2b.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode3a );
        OutlineTextNode outlineTextNode3b = new OutlineTextNode( "the lazy dog 0123456789", new PhetFont( Font.BOLD, 48 ), Color.GREEN, Color.BLACK, 1 );
        outlineTextNode3b.setOffset( xMargin, outlineTextNode3a.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode3b );
        OutlineTextNode outlineTextNode4a = new OutlineTextNode( "The quick brown fox", new PhetFont( Font.BOLD, 72 ), Color.MAGENTA, Color.BLACK, 2 );
        outlineTextNode4a.setOffset( xMargin, outlineTextNode3b.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode4a );
        OutlineTextNode outlineTextNode4b = new OutlineTextNode( "jumps over the lazy", new PhetFont( Font.BOLD, 72 ), Color.MAGENTA, Color.BLACK, 2 );
        outlineTextNode4b.setOffset( xMargin, outlineTextNode4a.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode4b );
        OutlineTextNode outlineTextNode4c = new OutlineTextNode( "dog 0123456789", new PhetFont( Font.BOLD, 72 ), Color.MAGENTA, Color.BLACK, 2 );
        outlineTextNode4c.setOffset( xMargin, outlineTextNode4b.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode4c );

        // Arabic
        OutlineTextNode outlineTextNode5 = new OutlineTextNode( "\uFE9D\u06B0\u06AA\uFEB5", PhetFont.getPreferredFont( new Locale( "ar" ), Font.BOLD, 64 ), Color.ORANGE, Color.BLACK, 2 );
        outlineTextNode5.setOffset( xMargin, outlineTextNode4c.getFullBoundsReference().getMaxY() + ySpacing );
        canvas.addWorldChild( outlineTextNode5 );

        // Chinese
        OutlineTextNode outlineTextNode6 = new OutlineTextNode( "\u4e2d\u56fd\u8bdd\u4e0d", PhetFont.getPreferredFont( new Locale( "zh" ), Font.BOLD, 64 ), Color.CYAN, Color.BLACK, 2 );
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
