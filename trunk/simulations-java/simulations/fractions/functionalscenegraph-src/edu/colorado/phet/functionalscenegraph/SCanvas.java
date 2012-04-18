package edu.colorado.phet.functionalscenegraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import static java.awt.geom.AffineTransform.getScaleInstance;
import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * @author Sam Reid
 */
public class SCanvas extends JComponent {
    public final SEffect child;

    //Make sure mock and Graphics2D use same font, so bounds will be right
    public static final Font DEFAULT_FONT = new PhetFont();

    public SCanvas( final SEffect child ) {
        this.child = child;
    }

    @Override protected void paintComponent( final Graphics g ) {
        super.paintComponent( g );
        Font orig = g.getFont();
        g.setFont( DEFAULT_FONT );
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        this.child.render( new Graphics2DContext( g2 ) );
        g.setFont( orig );
    }

    public static void main( String[] args ) {
        new JFrame( "Test" ) {{
            final WithTransform text = new WithTransform( getTranslateInstance( 100, 100 ), new WithPaint( Color.red, new WithFont( new PhetFont( 24, true ), new DrawText( "hello" ) ) ) );
            ImmutableRectangle2D bounds = text.getBounds( new MockState( DEFAULT_FONT ) );
            System.out.println( "bounds = " + bounds );

            DrawShape shape = new DrawShape( bounds.toRectangle2D() );
            WithPaint fillShape = new WithPaint( Color.white, new FillShape( bounds.toRectangle2D() ) );

            setContentPane( new SCanvas( new WithTransform( getScaleInstance( 2, 2 ), new SList( fillShape, shape, text ) ) ) {{
                setPreferredSize( new Dimension( 800, 600 ) );
            }} );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }}.setVisible( true );
    }
}