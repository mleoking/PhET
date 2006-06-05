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

import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * SimulationTable
 * <p/>
 * A JTable for displaying simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimTable extends JTable implements SimContainer {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static SimComparator NAME_SORT = new NameComparator();
    public static SimComparator MOST_RECENTLY_USED_SORT = new LastLaunchTimeComparator();


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private List sims;

    /**
     * Constructor
     *
     * @param simList
     * @param showThumbnails
     */
    public SimTable( List simList, boolean showThumbnails, SimComparator sortType ) {
        super();

        // Set the selection mode to be row only
        setColumnSelectionAllowed( false );
        setRowSelectionAllowed( true );

        // Create the row data for the table
        sims = simList;
        Collections.sort( sims, sortType );
        Object[][] rowData = new Object[sims.size()][3];
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation sim = (Simulation)sims.get( i );
//            System.out.println( "sim.getThumbnail() = " + sim.getThumbnail().getIconHeight() );
            Object[] row = new Object[]{sim.getName(), sim.getThumbnail()};
            rowData[i] = row;
        }

        // Create the header
        Object[] header = null;
        if( showThumbnails ) {
            header = new Object[]{"name", "thumbnail"};
        }
        else {
            header = new Object[]{"name"};
        }

        // Create the table model
        TableModel tableModel = new SimTableModel( rowData, header );
        this.setModel( tableModel );

        // So no header gets displayed
        this.setTableHeader( null );

        // Get max row height
        int hMax = getMaxRowHeight();
        if( hMax >= 1 ) {
            setRowHeight( hMax );
        }

        // Name the columns
        TableColumn nameCol = getColumn( "name" );
        nameCol.setMinWidth( 200 );
        nameCol.setMaxWidth( 200 );
        if( showThumbnails ) {
            TableColumn thumbnailCol = getColumn( "thumbnail" );
            thumbnailCol.setMinWidth( 150 );
        }
    }

    /**
     * Returns the currently selected simulation
     * @return The currently selected simulation, or null if none is selected
     */
    public Simulation getSelection() {
        Simulation sim = null;
        if( isVisible() ) {
            int idx = getSelectedRow();
            if( idx >= 0 ) {
                String simName = (String)this.getValueAt( idx, 0 );
                sim = Simulation.getSimulationForName( simName );
            }
        }
        return sim;
    }

    /**
     * Returns the height of the tallest row needed for any simulation
     * @return The height of the tallest row
     */
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

    /**
     * Returns the height of the tallest cell needed for all the entries in a specified column
     * @param col
     * @return The height of the tallest cell needed for all the entries in a specified column
     */
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
    // Comparators. Used to sort the rows in the table
    //--------------------------------------------------------------------------------------------------
    public static abstract class SimComparator implements Comparator {

        // Overide equals() to simply give class equality
        public boolean equals( Object obj ) {
            return this.getClass() == obj.getClass();
        }

        // Hash code uses class name, so instances will hash together
        public int hashCode() {
            int code = 0;
            for( int i = 0; i < this.getClass().getName().charAt( i ); i++ ) {
                code += this.getClass().getName().charAt( i );
            }
            return code;
        }
    }

    public static class NameComparator extends SimComparator {
        public int compare( Object o1, Object o2 ) {
            if( !( o1 instanceof Simulation && o2 instanceof Simulation ) ) {
                throw new ClassCastException();
            }
            Simulation s1 = (Simulation)o1;
            Simulation s2 = (Simulation)o2;
            return s1.getName().compareTo( s2.getName() );
        }
    }

    public static class LastLaunchTimeComparator extends SimComparator {
        public int compare( Object o1, Object o2 ) {
            if( !( o1 instanceof Simulation && o2 instanceof Simulation ) ) {
                throw new ClassCastException();
            }
            Simulation s1 = (Simulation)o1;
            Simulation s2 = (Simulation)o2;
            Long t1 = new Long( s1.getLastLaunchTimestamp() );
            Long t2 = new Long( s2.getLastLaunchTimestamp() );
            return t2.compareTo( t1 );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Table model
    //--------------------------------------------------------------------------------------------------

    private static class SimTableModel extends DefaultTableModel {
        public SimTableModel( Object[][] data, Object[] columnNames ) {
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
         *
         * @param row
         * @param column
         * @return always returns false
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

    /**
     * Returns the currently selected simulation
     * @return the currently selected simulation
     */
    public Simulation getSimulation() {
        return getSelection();
    }



    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    public static class ChangeEvent extends EventObject {
        public ChangeEvent( SimTable source ) {
            super( source );
        }

        public SimTable getOptions() {
            return (SimTable)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void simulationTableChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
