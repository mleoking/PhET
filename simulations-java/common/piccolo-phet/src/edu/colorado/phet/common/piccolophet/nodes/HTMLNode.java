/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

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
    private static final Color DEFAULT_HTML_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private String html;
    private Font font;
    private Color htmlColor;
    private JLabel htmlLabel;//todo: consider making this static, seemed to work properly under preliminary tests
    private View htmlView;
    private final Rectangle htmlBounds; // BasicHTML$Renderer.paint requires a Rectangle

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HTMLNode() {
        this( null, DEFAULT_FONT, DEFAULT_HTML_COLOR );
    }

    public HTMLNode( String html ) {
        this( html, DEFAULT_FONT, DEFAULT_HTML_COLOR );
    }

    public HTMLNode( String html, Color htmlColor ) {
        this( html, DEFAULT_FONT, htmlColor );
    }

    public HTMLNode( String html, Font font, Color htmlColor ) {
        this.html = html;
        this.font = font;
        this.htmlColor = htmlColor;
        htmlLabel = new JLabel();
        htmlBounds = new Rectangle();
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
        return htmlColor;
    }

    /**
     * Sets the color used to render the HTML.
     * If you want to set the paint used for the node, use setPaint.
     *
     * @param color
     */
    public void setHTMLColor( Color color ) {
        htmlColor = color;
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
        htmlLabel.setText( html );
        htmlLabel.setFont( font );
        htmlLabel.setForeground( htmlColor );
        htmlLabel.setSize( htmlLabel.getPreferredSize() );
        htmlView = BasicHTML.createHTMLView( htmlLabel, html == null ? "" : html );
        htmlBounds.setRect( 0, 0, htmlView.getPreferredSpan( View.X_AXIS ), htmlView.getPreferredSpan( View.Y_AXIS ) );
        setBounds( htmlBounds );
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

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        if ( htmlLabel.getWidth() == 0 || htmlLabel.getHeight() == 0 ) {
            return;
        }
        Graphics2D g2 = paintContext.getGraphics();
        htmlView.paint( g2, htmlBounds );
    }
}
