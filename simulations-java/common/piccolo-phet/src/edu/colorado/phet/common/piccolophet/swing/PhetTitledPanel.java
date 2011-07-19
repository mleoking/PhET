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
 * In PhET panels, it is ofter necessary to ensure that the entire title of a panel is visible.
 * This can be done with a regular component such as a JLabel, but we also prefer the visual styling of a border
 * combined with the title.  The previous implementation in phetcommon had several disadvantages because it was using
 * setPreferredSize() based on the size of children.
 * <p/>
 * In this implementation, we add a new layout component (Box.createHorizontalStrut) to ensure the layout is correct,
 * and the border is rendered with an AbstractBorder subclass implementation.
 * <p/>
 * This class also assumes as layout as per its superclass VerticalLayoutPanel, since the layout is used to ensure the title is always visible.
 * If the client changes the layout, problems could occur.
 * <p/>
 * See #2476
 *
 * @author Sam Reid
 */
public class PhetTitledPanel extends VerticalLayoutPanel {

    public PhetTitledPanel( String title ) {
        this( title, PhetTitledBorder.DEFAULT_FONT );
    }

    public PhetTitledPanel( String title, final Font font ) {
        //Set up the default behavior for a vertical panel
        setFillNone(); // don't expand children to fill the width
        setAnchor( GridBagConstraints.WEST );//and put every component on the left edge

        //Create and set the PhetTitledPanelBorder which will render the border
        final PhetTitledPanelBorder border = new PhetTitledPanelBorder( Color.black, title, font );
        setBorder( border );

        //Add a dummy component to this panel which will ensure its layout is the same minimum width as the panels title border
        add( Box.createHorizontalStrut( border.getMinimumWidth() ) );
    }

    /**
     * This border renders the title and a round rectangle border.  The bounds of the text is used to determine the layout of the parent container.
     */
    public static class PhetTitledPanelBorder extends AbstractBorder {
        protected Color lineColor;
        protected PText text;//We use piccolo to compute bounds and render text.

        public PhetTitledPanelBorder( Color color, String title, final Font font ) {
            lineColor = color;
            text = new PText( title ) {{setFont( font );}};
            text.setOffset( 8, 0 );//Move the title to the right a bit so the curve of the line border is visible
        }

        public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
            //Store original graphic state
            Graphics2D g2 = (Graphics2D) g;
            Shape origClip = g.getClip();
            Paint origPaint = g2.getPaint();
            Stroke origStroke = g2.getStroke();
            Object oldAntialiasHint = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );

            //Prepare to draw
            final double offset = text.getFullBounds().getHeight() / 2;
            RoundRectangle2D.Double shape = new RoundRectangle2D.Double( x + 2, y + offset, width - 3, height - 1 - offset, 8, 8 );
            Area clip = new Area( g.getClip() );
            clip.subtract( new Area( text.getFullBounds() ) );

            //Mutate the graphics state
            g.setColor( lineColor );
            g2.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ) );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2.setClip( clip );

            //Draw the shape
            g2.draw( shape );

            //Restore the clip for rendering the text
            g2.setClip( origClip );

            //Render the text
            text.fullPaint( new PPaintContext( g2 ) );

            //Restore the rest of the graphics state
            g2.setPaint( origPaint );
            g2.setStroke( origStroke );
            g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, oldAntialiasHint );
        }

        /**
         * Gets a border insets big enough to show the line border and title border.
         *
         * @param c component in which this border will be rendered
         * @return the insets
         */
        public Insets getBorderInsets( Component c ) {
            return new Insets( (int) Math.ceil( text.getFullBounds().getHeight() ), 5, 3, 5 );
        }

        public boolean isBorderOpaque() {
            return true;
        }

        /**
         * Determines the minimum width of this component, enough space to display the entire title without adding ellipses.
         *
         * @return the title width
         */
        public int getMinimumWidth() {
            return (int) Math.ceil( text.getFullBounds().getWidth() + 5 );
        }
    }

    /**
     * This sample main demonstrates usage of the PhetTitledPanel
     *
     * @param args not used
     */
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Test" );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final JPanel contentPane = new PhetTitledPanel( "aoensuthasnotehuBorder" );
        for ( int i = 0; i < 10; i++ ) {
            contentPane.add( new JLabel( "medium sized label " + i ) );
        }
        contentPane.add( new JButton( "A button" ) );
        frame.setContentPane( contentPane );
        frame.pack();
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
