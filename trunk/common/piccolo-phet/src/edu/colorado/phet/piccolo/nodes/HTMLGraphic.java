package edu.colorado.phet.piccolo.nodes;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import java.awt.*;

/**
 * Class for rendering HTML Text.
 * 
 * @deprecated use HTMLNode
 */
public class HTMLGraphic extends PNode {

    private String html;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 12 );
    private Color color = Color.black;
    private JLabel label;
    private View htmlView;
    private Rectangle alloc;

    public HTMLGraphic( String html ) {
        this.html = html;
        label = new JLabel( html );
        update();
    }

    public HTMLGraphic( String html, Font font, Color color ) {
        this.html = html;
        label = new JLabel( html );
        setFont( font );
        setColor( color );
        update();
    }

    public Font getFont() {
        return font;
    }

    public void setFont( Font font ) {
        this.font = font;
        update();
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
        update();
    }

    public String getHtml() {
        return html;
    }

    public void setHtml( String html ) {
        this.html = html;
        update();
    }

    private void update() {
        label.setText( html );
        label.setFont( font );
        label.setForeground( color );
        label.setSize( label.getPreferredSize() );
        htmlView = BasicHTML.createHTMLView( label, html );
        alloc = new Rectangle( label.getPreferredSize() );
        setBounds( label.getBounds() );
        repaint();
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        if( label.getWidth() == 0 || label.getHeight() == 0 ) {
            return;
        }
        Graphics2D g = paintContext.getGraphics();
        htmlView.paint( g, alloc );
    }

}
