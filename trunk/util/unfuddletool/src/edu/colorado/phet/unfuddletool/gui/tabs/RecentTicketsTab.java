package edu.colorado.phet.unfuddletool.gui.tabs;

import java.awt.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.gui.TicketDisplayPane;
import edu.colorado.phet.unfuddletool.gui.TicketTable;
import edu.colorado.phet.unfuddletool.gui.TicketTableModel;
import edu.colorado.phet.unfuddletool.handlers.TicketHandler;

public class RecentTicketsTab extends JSplitPane {

    private static TicketTableModel model;

    public TicketTable ticketTable;
    public TicketDisplayPane ticketTableDisplay;
    public TicketDisplayPane ticketTableHeader;

    public RecentTicketsTab() {
        // set up the model
        model = new TicketTableModel();
        //TicketHandler.getTicketHandler().addTicketAddListener( model );
        TicketHandler.getTicketHandler().addTicketAddListener( new TicketHandler.TicketAddListener() {
            public void onTicketAdded( Ticket ticket ) {
                Date ticketDate = ticket.lastUpdateTime();
                Date now = new Date();

                long ticketTime = ticketDate.getTime();
                long nowTime = now.getTime();

                // if ticket is from within a week, add it!
                if ( nowTime - 1000 * 60 * 60 * 24 * 7 < ticketTime ) {
                    model.onTicketAdded( ticket );
                }
            }
        } );

        ticketTable = new TicketTable( model );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );
        ticketTableDisplay = new TicketDisplayPane();
        ticketTableDisplay.setText( "Testing" );
        JScrollPane tableAreaScrollPane = new JScrollPane( ticketTableDisplay );
        tableAreaScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        ticketTableHeader = new TicketDisplayPane();
        ticketTableHeader.setText( "Ticket Header" );
        final JSplitPane rightSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT, ticketTableHeader, tableAreaScrollPane );
        rightSplitPane.setOneTouchExpandable( true );
        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        int index = ticketTable.convertRowIndexToModel( indices[0] );
                        Ticket ticket = model.getTicketAt( index );
                        ticketTableDisplay.setText( ticket.getHTMLComments() );
                        ticketTableHeader.setText( ticket.getHTMLHeader() );
                        rightSplitPane.setDividerLocation( -1 );
                    }
                }
            }
        } );

        setLeftComponent( ticketTableScrollPane );
        setRightComponent( rightSplitPane );
    }
}
