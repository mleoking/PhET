package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.unfuddletool.handlers.LinkHandler;

public class HTMLDisplayPane extends JEditorPane {

    public HTMLDisplayPane() {
        setEditorKit( new HTMLEditorKit() );
        addHyperlinkListener( new LinkHandler() );
        setEditable( false );
        setBorder( new EmptyBorder( new Insets( 0, 10, 10, 10 ) ) );
        setPaneStyle( this );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                              RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
    }

    private void setPaneStyle( JEditorPane pane ) {
        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont( "Label.font" );
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                          "font-size: " + font.getSize() + "pt; }";
        ( (HTMLDocument) pane.getDocument() ).getStyleSheet().addRule( bodyRule );
    }
}
