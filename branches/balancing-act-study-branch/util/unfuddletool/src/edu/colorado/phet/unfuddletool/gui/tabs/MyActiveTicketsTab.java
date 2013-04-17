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
import edu.colorado.phet.unfuddletool.util.SimpleTicketReport;
import edu.colorado.phet.unfuddletool.util.TicketLoader;

public class MyActiveTicketsTab extends JSplitPane {

    private static TicketTableModel model;

    public TicketTable ticketTable;
    public TicketDisplayPane ticketDisplayPane;

    public MyActiveTicketsTab() {
        // set up the model
        model = new TicketTableModel();
        //TicketHandler.getTicketHandler().addTicketAddListener( model );

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

        JPanel leftPanel = new JPanel( new BorderLayout() );
        JButton generateReportButton = new JButton( "Generate Report" );
        leftPanel.add( generateReportButton, BorderLayout.NORTH );
        leftPanel.add( ticketTableScrollPane, BorderLayout.CENTER );

        setLeftComponent( leftPanel );
        setRightComponent( ticketDisplayPane );


        generateReportButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.clear();
                List<Integer> ticketList = SimpleTicketReport.getMyActiveTicketIDs();
                System.out.println( "Report found " + ticketList.size() + " tickets" );
                //model.addTicketIDList( ticketList );
                new TicketLoader( ticketList, model ).start();
            }
        } );

    }
}