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
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.simlauncher.resources.SimResourceException;
import edu.colorado.phet.simlauncher.util.TableSorter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.io.IOException;

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

    public static class Column {
        private String name;
        private Column( String name ) {
            this.name = name;
        }
    }

    public static Column NAME = new Column( "Name");
    public static Column THUMBNAIL = new Column( "Thumbnail" );
    public static Column IS_INSTALLED = new Column( "Installed?");
    public static Column IS_UP_TO_DATE = new Column( "Update Available?" );


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
    public SimTable( List simList, boolean showThumbnails, SimComparator sortType, int listSelectionModel ) {
        super();

        // Set the selection mode to be row only
        setColumnSelectionAllowed( false );
        setRowSelectionAllowed( true );

        setSelectionMode( listSelectionModel );

        // Create the row data for the table
        sims = simList;
        Collections.sort( sims, sortType );
        Object[][] rowData = new Object[sims.size()][3];
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation sim = (Simulation)sims.get( i );

            Object isCurrent = "";
            Object isInstalled = "";
            BufferedImage checkMarkImg = null;
            BufferedImage exclamationMarkImg = null;
            try {
                checkMarkImg = ImageLoader.loadBufferedImage( "images/check-mark-3.png" );
                exclamationMarkImg = ImageLoader.loadBufferedImage( "images/exclamation-mark-1.png" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            ImageIcon isInstalledIcon = new ImageIcon( checkMarkImg );
            ImageIcon updateAvailableIcon = new ImageIcon( exclamationMarkImg );
            try {
                isCurrent = sim.isCurrent() ? "" : updateAvailableIcon;
//                isCurrent = sim.isCurrent() ? "up to date" : "update available";
                isInstalled = sim.isInstalled() ? isInstalledIcon : "";
//                isInstalled = sim.isInstalled() ? "yes" : "no";
            }
            catch( SimResourceException e ) {
                e.printStackTrace();
            }
            Object[] row = new Object[]{sim.getName(), sim.getThumbnail(), isInstalled, isCurrent};
            rowData[i] = row;
        }

        // Create the header
        Object[] header = null;
        if( showThumbnails ) {
            header = new Object[]{"Name", "thumbnail", "Installed?", "Update Available?"};
        }
        else {
            header = new Object[]{"Name"};
        }

        // Create the table model
        TableSorter sorter = new TableSorter( new SimTableModel( rowData, header ));
        TableModel tableModel = new SimTableModel( rowData, header );
//        TableModel tableModel = new SimTableModel( rowData, header );
        this.setModel( sorter );
//        this.setModel( tableModel );
        sorter.setTableHeader(this.getTableHeader());

        // So no header gets displayed
//        this.setTableHeader( null );

        // Get max row height
        int hMax = getMaxRowHeight();
        if( hMax >= 1 ) {
            setRowHeight( hMax );
        }

        // Name the columns
        TableColumn nameCol = getColumn( "Name" );
//        nameCol.setMinWidth( 200 );
//        nameCol.setMaxWidth( 200 );
        nameCol.setWidth( 200 );
        if( showThumbnails ) {
            TableColumn thumbnailCol = getColumn( "thumbnail" );
            thumbnailCol.setMinWidth( 150 );
        }
    }

    /**
     * Returns the currently selected simulation
     *
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

    public Simulation[] getSelections() {
        ListSelectionModel lsm = getSelectionModel();
        List selectedSims = new ArrayList();
        for( int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++ ) {
            if( lsm.isSelectedIndex( i ) ) {
                String simName = (String)this.getValueAt( i, 0 );
                Simulation sim = Simulation.getSimulationForName( simName );
                selectedSims.add( sim );
            }
        }
        return (Simulation[])selectedSims.toArray( new Simulation[selectedSims.size()] );
    }

    /**
     * Returns the height of the tallest row needed for any simulation
     *
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
     *
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
                return ImageIcon.class;
//                return MyTableCellRenderer.class;
            }
            if( columnIndex == 3 ) {
                return ImageIcon.class;
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

    //--------------------------------------------------------------------------------------------------
    // Custom CellRenderers
    //--------------------------------------------------------------------------------------------------

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
    // Implementation of SimContainer
    //--------------------------------------------------------------------------------------------------

    /**
     * Returns the currently selected simulation
     *
     * @return the currently selected simulation
     */
    public Simulation getSimulation() {
        return getSelection();
    }

    public Simulation[] getSimulations() {
        return getSelections();
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