package edu.colorado.phet.unfuddletool.gui;

import javax.swing.*;

public class TicketTable extends JTable implements TicketTableModel.TicketTableDisplay {

    public TicketTableModel model;
    public ListSelectionModel ticketSelectionModel;

    public TicketTable( TicketTableModel model ) {
        super( model );

        this.model = model;

        model.addDisplay( this );

        //setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        ticketSelectionModel = new DefaultListSelectionModel();

        ticketSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        setSelectionModel( ticketSelectionModel );

        getColumnModel().getColumn( TicketTableModel.INDEX_SUMMARY ).setCellRenderer( new TicketSummaryCellRenderer() );

        //setDefaultRenderer( Ticket.class, new TicketSummaryCellRenderer() );

        getColumnModel().getColumn( TicketTableModel.INDEX_LAST_MODIFIED ).setPreferredWidth( 120 );
        getColumnModel().getColumn( TicketTableModel.INDEX_NUMBER ).setPreferredWidth( 60 );
        getColumnModel().getColumn( TicketTableModel.INDEX_COMPONENT ).setPreferredWidth( 130 );
        getColumnModel().getColumn( TicketTableModel.INDEX_SUMMARY ).setPreferredWidth( 290 );

    }

    public int getSelectedIndex() {
        int[] indices = getSelectedRows();
        if ( indices.length == 1 ) {
            return indices[0];
        }

        return -1;
    }

    public void setSelectedIndex( int index ) {
        ticketSelectionModel.setSelectionInterval( index, index );
    }
}
