/* Copyright 2005-2010, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * HTMLNode is a Piccolo node for rendering HTML text.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 * @version $Revision$
 */
public class HTMLNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Font DEFAULT_FONT = new PhetFont( Font.BOLD, 12 );
    private static final Color DEFAULT_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private String html;
    private Font font;
    private Color color;
    private final JLabel label;
    private View view;
    private final Rectangle bounds; // BasicHTML$Renderer.paint requires a Rectangle

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HTMLNode() {
        this( null, DEFAULT_COLOR, DEFAULT_FONT );
    }

    public HTMLNode( String html ) {
        this( html, DEFAULT_COLOR, DEFAULT_FONT );
    }

    public HTMLNode( String html, Color color ) {
        this( html, color, DEFAULT_FONT );
    }

    public HTMLNode( String html, Color color, Font font ) {
        this.html = html;
        this.color = color;
        this.font = font;
        label = new JLabel();
        bounds = new Rectangle();
        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the HTML string.
     *
     * @return HTML string
     */
    public String getHTML() {
        return html;
    }

    /**
     * Sets the HMTL string.
     *
     * @param html
     */
    public void setHTML( String html ) {
        if ( ( this.html != null && html == null ) || ( this.html == null && html != null ) || ( !this.html.equals( html ) ) ) {
            this.html = html;
            update();
        }
    }

    /**
     * Gets the font.
     *
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Sets the font.
     *
     * @param font
     */
    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    /**
     * Gets the color used to render the HTML.
     * If you want to get the paint used for the node, use getPaint.
     *
     * @return the color used to render the HTML.
     */
    public Color getHTMLColor() {
        return color;
    }

    /**
     * Sets the color used to render the HTML.
     * If you want to set the paint used for the node, use setPaint.
     *
     * @param color
     */
    public void setHTMLColor( Color color ) {
        this.color = color;
        update();
    }

    //----------------------------------------------------------------------------
    // Update handler
    //----------------------------------------------------------------------------

    /*
     * Updates everything that is involved in rendering the HTML string.
     * This method is called when one the HTML-related properties is modified.
     */
    private void update() {
        label.setText( html );
        label.setFont( font );
        label.setForeground( color );
        label.setSize( label.getPreferredSize() );
        view = BasicHTML.createHTMLView( label, html == null ? "" : html );
        bounds.setRect( 0, 0, view.getPreferredSpan( View.X_AXIS ), view.getPreferredSpan( View.Y_AXIS ) );
        setBounds( bounds );
        repaint();
    }

    //----------------------------------------------------------------------------
    // PNode overrides
    //----------------------------------------------------------------------------

    /*
     * Paints the node.
     * The HTML string is painted last, so it appears on top of any child nodes.
     * 
     * @param paintContext
     */
    @Override
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        if ( label.getWidth() == 0 || label.getHeight() == 0 ) {
            return;
        }
        Graphics2D g2 = paintContext.getGraphics();
        view.paint( g2, bounds );
    }
}
