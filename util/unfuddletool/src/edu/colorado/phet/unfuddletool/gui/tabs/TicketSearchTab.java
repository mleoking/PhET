package edu.colorado.phet.unfuddletool.gui.tabs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.gui.TicketDisplayPane;
import edu.colorado.phet.unfuddletool.gui.TicketTable;
import edu.colorado.phet.unfuddletool.gui.TicketTableModel;
import edu.colorado.phet.unfuddletool.util.TicketLoader;
import edu.colorado.phet.unfuddletool.util.TicketSearch;

public class TicketSearchTab extends JSplitPane {

    private static TicketTableModel model;

    public TicketTable ticketTable;
    public TicketDisplayPane ticketTableDisplay;
    public TicketDisplayPane ticketTableHeader;

    public TicketSearchTab() {
        // set up the model
        model = new TicketTableModel();
        //TicketHandler.getTicketHandler().addTicketAddListener( model );

        ticketTable = new TicketTable( model );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );
        ticketTableDisplay = new TicketDisplayPane();
        ticketTableDisplay.setText( "Testing" );
        final JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        //tableAreaScrollPane.setMinimumSize( new Dimension( 0, 200 ) );
        ticketTableHeader = new TicketDisplayPane();
        ticketTableHeader.setText( "Ticket Header" );
        final JScrollPane tableHeaderScrollPane = new JScrollPane( ticketTableHeader );
        tableHeaderScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        //tableHeaderScrollPane.setMinimumSize( new Dimension( 0, 200 ) );
        final JSplitPane rightSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, tableHeaderScrollPane, tableAreaScrollPane );
        rightSplitPane.setOneTouchExpandable( true );
        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        int index = ticketTable.convertRowIndexToModel( indices[0] );
                        Ticket ticket = model.getTicketAt( index );

                        String htmlComments = ticket.getHTMLComments();
                        String htmlHeader = ticket.getHTMLHeader();

                        ticketTableDisplay.setText( htmlComments );
                        ticketTableHeader.setText( htmlHeader );

                        // size everything
                        int sizeAvailable = rightSplitPane.getHeight();
                        int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                        int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                        rightSplitPane.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, rightSplitPane.getDividerSize() ) );

                        // size it again (for if scrollbars were added after sizing)
                        sizeAvailable = rightSplitPane.getHeight();
                        sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                        sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                        rightSplitPane.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, rightSplitPane.getDividerSize() ) );

                        // make sure the header is set to the top
                        ticketTableHeader.setSelectionStart( 0 );
                        ticketTableHeader.setSelectionEnd( 0 );

                        ticketTableDisplay.scrollRectToVisible( new Rectangle( 0, ticketTableDisplay.getBounds( null ).height, 1, 1 ) );

                        //ticketTableDisplay.setSelectionStart( htmlComments.length() );
                        //ticketTableDisplay.setSelectionEnd( htmlComments.length() );

                        //tableAreaScrollPane.getVerticalScrollBar().setValue( tableAreaScrollPane.getVerticalScrollBar().getMaximum() );

                        //ticketTableDisplay.revalidate();
                        //ticketTableHeader.revalidate();

                        // try:
                        // revalidate()
                        // repaint() // immediately maybe?
                        // revalidate?

                        /* WAS WORKING WITH FLICKER
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {

                                System.out.println( "during runnable status: " );
                                TicketSearchTab.this.printStatus();

                                int sizeAvailable = rightSplitPane.getHeight();
                                int sizeTop = (int) ticketTableHeader.getPreferredScrollableViewportSize().getHeight();
                                int sizeBottom = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                                rightSplitPane.setDividerLocation( getTicketDividerLocation( sizeAvailable, sizeTop, sizeBottom, rightSplitPane.getDividerSize() ) );

                                ticketTableHeader.setSelectionStart( 0 );
                                ticketTableHeader.setSelectionEnd( 0 );
                            }
                        } );
                        */

                        /* UNSURE IF THIS IS HELPFUL?

                        tableHeaderScrollPane.validate();
                        tableAreaScrollPane.validate();

                        tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
                        */

                        /* WAS WORKING WITH FLICKER
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                //tableHeaderScrollPane.getVerticalScrollBar().setValue( 0 );
                                tableAreaScrollPane.getVerticalScrollBar().setValue( tableAreaScrollPane.getVerticalScrollBar().getMaximum() );
                            }
                        } );
                        */

                        //tableHeaderScrollPane.getViewport().setViewPosition( new Point( 0, 0 ) );

                        //int prefDisplayHeight = (int) ticketTableDisplay.getPreferredScrollableViewportSize().getHeight();
                        //tableAreaScrollPane.getViewport().setViewPosition( new Point( 0, (int) (prefDisplayHeight - tableAreaScrollPane.getViewport().getViewSize().getHeight()) ) );

                        //rightSplitPane.setDividerLocation( -1 );
                    }
                }
            }
        } );


        JButton searchButton = new JButton( "Search" );
        final JTextField searchField = new JTextField( "" );
        searchField.setEditable( true );

        JPanel topLeftPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.BOTH;
        topLeftPanel.add( searchField, c );
        c.gridx = 1;
        c.weightx = 0.0;
        topLeftPanel.add( searchButton, c );

        JPanel leftPanel = new JPanel( new BorderLayout() );
        leftPanel.add( topLeftPanel, BorderLayout.NORTH );
        leftPanel.add( ticketTableScrollPane, BorderLayout.CENTER );

        setLeftComponent( leftPanel );
        setRightComponent( rightSplitPane );


        searchButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.clear();
                List<Integer> ticketList = TicketSearch.getTicketSearchIDs( searchField.getText() );
                System.out.println( "Report found " + ticketList.size() + " tickets" );
                //model.addTicketIDList( ticketList );
                new TicketLoader( ticketList, model ).start();
            }
        } );

    }

    public static int getTicketDividerLocation( int sizeAvailable, int sizeTop, int sizeBottom, int dividerSize ) {
        int extraPadding = 5;

        int ret;

        if ( sizeAvailable >= sizeTop + extraPadding * 2 + sizeBottom + dividerSize ) {
            // if we have room for everything
            ret = sizeTop + extraPadding;
        }
        else {
            int minTop = 400;
            int minBottom = 200;
            int maxTop = sizeAvailable - minBottom - extraPadding - dividerSize;

            if ( sizeBottom < minBottom ) {
                ret = sizeAvailable - sizeBottom - extraPadding - dividerSize;
            }
            else if ( sizeTop < minTop ) {
                ret = sizeTop + extraPadding;
            }
            else {
                ret = minTop + extraPadding;
            }
        }

        return ret;

    }
}