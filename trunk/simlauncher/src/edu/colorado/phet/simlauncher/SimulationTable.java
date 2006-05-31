/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

/**
 * SimulationTable
 * <p>
 * A JTable for displaying simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationTable extends JTable implements SimulationContainer {
    private Simulation[] sims;

    public SimulationTable( List simList, boolean showThumbnails ) {
        super();

        // Set the selection mode to be row only
        setColumnSelectionAllowed( false );
        setRowSelectionAllowed( true );

        // Populate the table
        sims = (Simulation[])simList.toArray( new Simulation[ simList.size()] );
        Object[][] rowData = new Object[sims.length][3];

        for( int i = 0; i < sims.length; i++ ) {
            Simulation sim = sims[i];
            Object[] row = new Object[]{sim.getName(), sim.getThumbnail()/*, sim.description*/};
            rowData[i] = row;
        }

        Object[] header = null;
        if( showThumbnails ) {
            header = new Object[]{"name", "thumbnail"};
        }
        else {
            header = new Object[]{"name"};
        }
        TableModel tableModel = new SimulationTableModel( rowData, header );
        this.setModel( tableModel );

        // So no header gets displayed
        this.setTableHeader( null );

        // Get max row height
        int hMax = getMaxRowHeight();
        if( hMax >= 1 ) {
            setRowHeight( hMax );
        }

        TableColumn nameCol = getColumn( "name" );
        nameCol.setMinWidth( 150 );
        if( showThumbnails ) {
            TableColumn thumbnailCol = getColumn( "thumbnail" );
            thumbnailCol.setMinWidth( 150 );
        }
//        TableColumn descriptionCol = getColumn( "description");
//        descriptionCol.setMinWidth( 400 );

//        TableColumn col = getColumnModel().getColumn(2);
//        col.setCellRenderer(new MyTableCellRenderer());

    }

    public Simulation getSelection() {
        Simulation sim = null;
        int idx = getSelectedRow();
        if( idx >= 0 ) {
         sim = sims[idx];
        }
        return sim;
    }

    private int getMaxRowHeight() {
        int h = 0;
        int hMax = 0;
        int columnCount = getColumnCount();
        for( int i = 0; i < columnCount; i++ ) {
            TableColumn col = getColumnModel().getColumn( i );
            h = getMaxRowHeightForColumn( col );
            hMax = Math.max( h, hMax );
        }
        return hMax;
    }

    private int getMaxRowHeightForColumn( TableColumn col ) {
        int h = 0;
        int hMax = 0;
        int c = col.getModelIndex();
        for( int r = 0; r < getRowCount(); r++ ) {
            TableCellRenderer renderer = getCellRenderer( r, c );
            Component comp = renderer.getTableCellRendererComponent( this,
                                                                     this.getValueAt( r, c ),
                                                                     false,
                                                                     false,
                                                                     r,
                                                                     c );
            h = comp.getMaximumSize().height;
            hMax = Math.max( h, hMax );
        }
        return hMax;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private static class SimulationTableModel extends DefaultTableModel {
        public SimulationTableModel( Object[][] data, Object[] columnNames ) {
            super( data, columnNames );
        }

        public Class getColumnClass( int columnIndex ) {
            if( columnIndex == 1 ) {
                return ImageIcon.class;
            }
            if( columnIndex == 2 ) {
                return MyTableCellRenderer.class;
            }
            else {
                return super.getColumnClass( columnIndex );
            }
        }

        /**
         * No cells are editable
         * @param row
         * @param column
         * @return
         */
        public boolean isCellEditable( int row, int column ) {
            return false;
        }
    }


    public class MyTableCellRenderer extends JLabel implements TableCellRenderer {
        // This method is called each time a cell in a column
        // using this renderer needs to be rendered.
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {
            // 'value' is value contained in the cell located at
            // (rowIndex, vColIndex)

            if( isSelected ) {
                // cell (and perhaps other cells) are selected
            }

            if( hasFocus ) {
                // this cell is the anchor and the table has the focus
            }

            // Configure the component with the specified value
            setText( value.toString() );

            // Set tool tip if desired
            setToolTipText( (String)value );

            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        public void validate() {
        }

        public void revalidate() {
        }

        protected void firePropertyChange( String propertyName, Object oldValue, Object newValue ) {
        }

        public void firePropertyChange( String propertyName, boolean oldValue, boolean newValue ) {
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return getSelection();
    }

}
