package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;

import edu.colorado.phet.unfuddletool.Activity;
import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.LinkHandler;
import edu.colorado.phet.unfuddletool.TicketHandler;

public class UnfuddleToolGUI extends JFrame {

    private TicketHandler ticketHandler;

    public JEditorPane displayArea;
    public TicketList ticketList;

    public UnfuddleToolGUI() {
        setTitle( "Unfuddle Tool" );
        setSize( 900, 700 );

        JTextArea startupStatus = new JTextArea();
        startupStatus.setText( "Loading activity..." );
        add( startupStatus );

        setVisible( true );

        ticketHandler = TicketHandler.getTicketHandler();

        Activity activity = new Activity();

        startupStatus.setText( startupStatus.getText() + "\nLoading tickets..." );

        ticketList = new TicketList( this );

        remove( startupStatus );

        JScrollPane ticketScrollPane = new JScrollPane( ticketList );

        ticketScrollPane.setMinimumSize( new Dimension( 400, 0 ) );

        displayArea = new JEditorPane() {
            public void paintComponent( Graphics g ) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };

        displayArea.setEditorKit( new HTMLEditorKit() );
        displayArea.addHyperlinkListener( new LinkHandler() );
        displayArea.setEditable( false );

        displayArea.setText( "To the left are tickets sorted by when they were last modified." );

        displayArea.setBorder( new EmptyBorder( new Insets( 0, 10, 10, 10 ) ) );


        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                "font-size: " + font.getSize() + "pt; }";
        ((HTMLDocument) displayArea.getDocument()).getStyleSheet().addRule(bodyRule);



        JScrollPane areaScrollPane = new JScrollPane( displayArea );

        areaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, ticketScrollPane, areaScrollPane );

        add( splitPane );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setVisible( true );
        repaint( 0, 0, 0, 5000, 5000 );
    }

    public static void main( String[] args ) {
        Authentication.auth = args[0];

        new UnfuddleToolGUI();
    }
}
