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
    public TicketDisplayPane ticketDisplayPane;

    public RecentTicketsTab() {
        // set up the model
        model = new TicketTableModel();

        TicketHandler.getTicketHandler().addTicketAddListener( new TicketHandler.TicketAddListener() {
            public void onTicketAdded( Ticket ticket ) {
                Date ticketDate = ticket.lastUpdateTime();
                Date now = new Date();

                long ticketTime = ticketDate.getTime();
                long nowTime = now.getTime();

                // if ticket is from within a week, add it!
                if ( nowTime - 1000 * 60 * 60 * 24 * 7 < ticketTime ) {
                    model.onTicketAdded( ticket );
                } else {
                    ticket.addListener( new Ticket.TicketListener() {
                        public void onTicketUpdate( Ticket ticket ) {
                            long nowTime = ( new Date() ).getTime();
                            long ticketTime = ticket.lastUpdateTime().getTime();
                            if ( nowTime - 1000 * 60 * 60 * 24 * 7 < ticketTime ) {
                                if( !model.hasTicket( ticket ) ) {
                                    model.onTicketAdded( ticket );
                                    ticket.removeListener( this );
                                }
                            }
                        }
                    } );
                }
            }
        } );

        ticketTable = new TicketTable( model );
        JScrollPane ticketTableScrollPane = new JScrollPane( ticketTable );
        ticketTable.setFillsViewportHeight( true );
        ticketTableScrollPane.setMinimumSize( new Dimension( 600, 0 ) );

        ticketDisplayPane = new TicketDisplayPane();

        ticketTable.ticketSelectionModel.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent event ) {
                if ( !event.getValueIsAdjusting() ) {
                    int[] indices = ticketTable.getSelectedRows();
                    if ( indices.length == 1 ) {
                        int index = ticketTable.convertRowIndexToModel( indices[0] );
                        Ticket ticket = model.getTicketAt( index );

                        ticketDisplayPane.showTicket( ticket );
                    }
                }
            }
        } );

        setLeftComponent( ticketTableScrollPane );
        setRightComponent( ticketDisplayPane );
    }
}
