package edu.colorado.phet.unfuddletool.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.unfuddletool.Activity;
import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.LinkHandler;
import edu.colorado.phet.unfuddletool.data.Ticket;

public class UnfuddleToolGUI extends JFrame {
    public JEditorPane ticketListDisplay;
    public JEditorPane ticketTableDisplay;
    public TicketList ticketList;
    public TicketTableModel ticketTableModel;
    public TicketTable ticketTable;
    private JEditorPane ticketTableHeader;

    public UnfuddleToolGUI() {
        setTitle( "Unfuddle Tool" );
        setSize( 900, 700 );

        ticketList = new TicketList( this );

        JScrollPane ticketListScrollPane = new JScrollPane( ticketList );

        ticketListScrollPane.setMinimumSize( new Dimension( 400, 0 ) );

        ticketListDisplay = createDisplayPane();
        ticketListDisplay.setText( "To the left are tickets sorted by when they were last modified." );
        setPaneStyle( ticketListDisplay );
        JScrollPane listAreaScrollPane = new JScrollPane( ticketListDisplay );
        listAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        JSplitPane listSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, ticketListScrollPane, listAreaScrollPane );

        // NEW
        ticketTableModel = new TicketTableModel();
        ticketTable = new TicketTable( ticketTableModel );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );
        ticketTableDisplay = createDisplayPane();
        ticketTableDisplay.setText( "Testing" );
        setPaneStyle( ticketTableDisplay );
        JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        ticketTableHeader = createDisplayPane();
        ticketTableHeader.setText( "Ticket Header" );
        setPaneStyle( ticketTableHeader );
        final JSplitPane rightSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, ticketTableHeader, tableAreaScrollPane );
        rightSplitPane.setOneTouchExpandable( true );
        JSplitPane tableSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, ticketTableScrollPane, rightSplitPane );
        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        //System.out.println( "Selected " + indices[0] );
                        Ticket ticket = ticketTableModel.getTicketAt( indices[0] );
                        ticketTableDisplay.setText( ticket.getHTMLComments() );
                        ticketTableHeader.setText( ticket.getHTMLHeader() );
                        rightSplitPane.setDividerLocation( -1 );
                    }
                }
            }
        } );
        // END NEW

        JTabbedPane tabber = new JTabbedPane();
        tabber.addTab( "Ticket List", listSplitPane );
        tabber.addTab( "Ticket Table", tableSplitPane );
        add( tabber );

        //add( splitPane );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        setJMenuBar( createMenu() );

        setVisible( true );
        repaint( 0, 0, 0, 5000, 5000 );

    }

    private JEditorPane createDisplayPane() {
        JEditorPane pane = new JEditorPane() {
            public void paintComponent( Graphics g ) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
                                      RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };

        pane.setEditorKit( new HTMLEditorKit() );
        pane.addHyperlinkListener( new LinkHandler() );
        pane.setEditable( false );
        pane.setBorder( new EmptyBorder( new Insets( 0, 10, 10, 10 ) ) );

        return pane;
    }

    private void setPaneStyle( JEditorPane pane ) {
        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont( "Label.font" );
        String bodyRule = "body { font-family: " + font.getFamily() + "; " +
                          "font-size: " + font.getSize() + "pt; }";
        ( (HTMLDocument) pane.getDocument() ).getStyleSheet().addRule( bodyRule );
    }

    public JMenuBar createMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu developmentMenu = new JMenu( "Development" );

        JMenuItem updateItem = new JMenuItem( "Update" );

        updateItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                Activity.requestRecentActivity( 3 );
            }
        } );

        developmentMenu.add( updateItem );

        bar.add( developmentMenu );

        return bar;
    }

    public static Color priorityColor( int priority ) {
        switch( priority ) {
            case 1:
                return new Color( 0xAA, 0xAA, 0xFF );
            case 2:
                return new Color( 0xDD, 0xDD, 0xFF );
            case 3:
                return new Color( 0xFF, 0xFF, 0xFF );
            case 4:
                return new Color( 0xFF, 0xDD, 0xDD );
            case 5:
                return new Color( 0xFF, 0xAA, 0xAA );
        }

        return new Color( 0xFF, 0xFF, 0xFF );
    }

    public static void main( String[] args ) {
        Authentication.auth = args[0];

        new UnfuddleToolGUI();
    }
}
