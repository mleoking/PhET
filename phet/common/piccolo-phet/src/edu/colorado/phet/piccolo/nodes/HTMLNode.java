/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.nodes;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;

/**
 * HTMLNode is a Piccolo node for rendering HTML text.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HTMLNode extends PNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Font DEFAULT_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color DEFAULT_HTML_COLOR = Color.BLACK;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private String html;
    private Font font;
    private Color htmlColor;
    private JLabel htmlLabel;
    private View htmlView;
    private Rectangle htmlBounds;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public HTMLNode() {
        this( "", DEFAULT_FONT, DEFAULT_HTML_COLOR );
    }

    public HTMLNode( String html ) {
        this( html, DEFAULT_FONT, DEFAULT_HTML_COLOR );
    }

    public HTMLNode( String html, Font font, Color htmlColor ) {
        this.html = html;
        this.font = font;
        this.htmlColor = htmlColor;
        htmlLabel = new JLabel();
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
        if( !this.html.equals( html ) ) {
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
        htmlView = BasicHTML.createHTMLView( htmlLabel, html );
        htmlBounds = new Rectangle( htmlLabel.getPreferredSize() );
        setBounds( htmlLabel.getBounds() );
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
        if( htmlLabel.getWidth() == 0 || htmlLabel.getHeight() == 0 ) {
            return;
        }
        Graphics2D g = paintContext.getGraphics();
        htmlView.paint( g, htmlBounds );
    }
}
