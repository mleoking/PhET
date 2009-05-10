package edu.colorado.phet.unfuddletool.gui;

import java.util.Comparator;

import javax.swing.*;

import org.jdesktop.swingx.JXTable;

import edu.colorado.phet.unfuddletool.data.DateReverseComparator;
import edu.colorado.phet.unfuddletool.data.Ticket;
import edu.colorado.phet.unfuddletool.gui.cell.DateCellRenderer;
import edu.colorado.phet.unfuddletool.gui.cell.TicketNumberCellRenderer;
import edu.colorado.phet.unfuddletool.gui.cell.TicketPriorityCellRenderer;
import edu.colorado.phet.unfuddletool.gui.cell.TicketSummaryCellRenderer;

public class TicketTable extends JXTable implements TicketTableModel.TicketTableDisplay {

    public TicketTableModel model;
    public ListSelectionModel ticketSelectionModel;

    public TicketTable( TicketTableModel model ) {
        super( model );

        this.model = model;
        model.addDisplay( this );

        ticketSelectionModel = new DefaultListSelectionModel();
        ticketSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        setSelectionModel( ticketSelectionModel );

        getColumnModel().getColumn( TicketTableModel.INDEX_LAST_MODIFIED ).setCellRenderer( new DateCellRenderer() );
        getColumnModel().getColumn( TicketTableModel.INDEX_NUMBER ).setCellRenderer( new TicketNumberCellRenderer() );
        getColumnModel().getColumn( TicketTableModel.INDEX_SUMMARY ).setCellRenderer( new TicketSummaryCellRenderer() );
        getColumnModel().getColumn( TicketTableModel.INDEX_PRIORITY ).setCellRenderer( new TicketPriorityCellRenderer() );

        getColumnModel().getColumn( TicketTableModel.INDEX_LAST_MODIFIED ).setPreferredWidth( 120 );
        getColumnModel().getColumn( TicketTableModel.INDEX_NUMBER ).setPreferredWidth( 60 );
        getColumnModel().getColumn( TicketTableModel.INDEX_COMPONENT ).setPreferredWidth( 130 );
        getColumnModel().getColumn( TicketTableModel.INDEX_SUMMARY ).setPreferredWidth( 290 );

        setColumnControlVisible( true );

        getColumnExt( TicketTableModel.ID_STATUS ).setVisible( false );
        getColumnExt( TicketTableModel.ID_ASSIGNEE ).setVisible( false );
        getColumnExt( TicketTableModel.ID_REPORTER ).setVisible( false );
        getColumnExt( TicketTableModel.ID_PRIORITY ).setVisible( false );
        getColumnExt( TicketTableModel.ID_MILESTONE ).setVisible( false );

        getColumnExt( TicketTableModel.ID_LAST_MODIFIED ).setComparator( new DateReverseComparator() );
        getColumnExt( TicketTableModel.ID_PRIORITY ).setComparator( new Comparator<Integer>() {
            public int compare( Integer a, Integer b ) {
                return -a.compareTo( b );
            }
        } );
        getColumnExt( TicketTableModel.ID_SUMMARY ).setComparator( new Comparator<Ticket>() {
            public int compare( Ticket a, Ticket b ) {
                return -( Integer.valueOf( a.getPriority() ) ).compareTo( Integer.valueOf( b.getPriority() ) );
            }
        } );

        addHighlighter( new TicketHighlighter() );

    }

    public int getSelectedIndex() {
        int[] indices = getSelectedRows();
        if ( indices.length == 1 ) {
            return convertRowIndexToModel( indices[0] );
        }

        return -1;
    }

    public void setSelectedIndex( int index ) {
        int newIndex = convertRowIndexToView( index );
        ticketSelectionModel.setSelectionInterval( newIndex, newIndex );
    }
}
