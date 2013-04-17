package edu.colorado.phet.unfuddletool.gui;

import javax.swing.*;

import org.jdesktop.swingx.JXTable;

import edu.colorado.phet.unfuddletool.data.DateReverseComparator;
import edu.colorado.phet.unfuddletool.gui.cell.DateCellRenderer;

public class EventTable extends JXTable {
    public EventTableModel model;
    public ListSelectionModel eventSelectionModel;

    public EventTable( EventTableModel model ) {
        super( model );

        this.model = model;

        eventSelectionModel = new DefaultListSelectionModel();
        eventSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        setSelectionModel( eventSelectionModel );

        getColumnModel().getColumn( EventTableModel.INDEX_TIME ).setCellRenderer( new DateCellRenderer() );

        getColumnModel().getColumn( EventTableModel.INDEX_TIME ).setPreferredWidth( 120 );
        getColumnModel().getColumn( EventTableModel.INDEX_EVENT ).setPreferredWidth( 80 );
        getColumnModel().getColumn( EventTableModel.INDEX_SUMMARY ).setPreferredWidth( 200 );
        getColumnModel().getColumn( EventTableModel.INDEX_DESCRIPTION ).setPreferredWidth( 350 );
        getColumnModel().getColumn( EventTableModel.INDEX_PERSON ).setPreferredWidth( 150 );

        setColumnControlVisible( true );

        getColumnExt( EventTableModel.INDEX_TIME ).setComparator( new DateReverseComparator() );

    }
}
