// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.AbstractBorder;

import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Feasibility test for PhetTitlePanel, we will probably use something more like PhetTitledPanel3 since this implementation
 * restricts the layout to be that of a VerticalLayoutPanel and will silently fail if the layout is changed.
 *
 * @author Sam Reid
 */
public class PhetTitledPanel2 extends VerticalLayoutPanel {

    public PhetTitledPanel2( String title ) {
        this( title, PhetTitledBorder.DEFAULT_FONT );
    }

    public PhetTitledPanel2( String title, final Font font ) {
        setFillNone();
        setAnchor( GridBagConstraints.WEST );
        final PhetTitledPanelBorder border = new PhetTitledPanelBorder( Color.blue, title, font );
        setBorder( border );
        add( Box.createHorizontalStrut( border.getMinimumWidth() ) );
    }

    /**
     * This sample main demonstrates usage of the PhetTitledPanel
     *
     * @param args not used
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JPanel contentPane = new PhetTitledPanel2( "aoensuthasnotehuBorder" );
        for ( int i = 0; i < 10; i++ ) {
            contentPane.add( new JLabel( "medium sized label " + i + "aonetuhasnoetuh" ) );
        }
        contentPane.add( new JButton( "A button" ) );
        frame.setContentPane( contentPane );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }

    public static class PhetTitledPanelBorder extends AbstractBorder {
        protected Color lineColor;
        private final Font font;
        protected PText text;

        public PhetTitledPanelBorder( Color color, String title, final Font font ) {
            lineColor = color;
            this.font = font;
            text = new PText( title ) {{setFont( font );}};
            text.setOffset( 8, 0 );
        }

        public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
            Graphics2D g2 = (Graphics2D) g;
            Shape origClip = g.getClip();
            Paint origPaint = g2.getPaint();
            Stroke origStroke = g2.getStroke();
            Object oldAntialiasHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

            final double offset = text.getFullBounds().getHeight() / 2;
            RoundRectangle2D.Double shape = new RoundRectangle2D.Double( x + 2, y + offset, width - 3, height - 1 - offset, 8, 8 );
            Area clip = new Area( g.getClip() );
            clip.subtract( new Area( text.getFullBounds() ) );

            g.setColor( lineColor );
            g2.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setClip( clip );

            g2.draw( shape );

            g2.setStroke( origStroke );
            g2.setClip( origClip );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldAntialiasHint );

            g2.setColor( Color.black );
            g2.setFont( font );
            text.fullPaint( new PPaintContext( g2 ) );

            g2.setPaint( origPaint );
        }

        public Insets getBorderInsets( Component c ) {
            return new Insets( (int) Math.ceil( text.getFullBounds().getHeight() ), 5, 3, 5 );
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public int getMinimumWidth() {
            return (int) Math.ceil( text.getFullBounds().getWidth() );
        }
    }
}
