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

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.simlauncher.resources.SimResource;
import edu.colorado.phet.simlauncher.util.TableSorter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * SimulationTable
 * <p/>
 * A JTable for displaying simulations.
 * <p>
 * The table can have a check box in one of the columns that acts as the selector, from the user's point of view.
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

    /**
     * An enumeration class that is used to specify which columns are to be displayed in the table
     */
    public static class Column {
        private String name;
        private int width;
        private Class columnClass;

        private Column( String name, Class columnClass, int width ) {
            this.columnClass = columnClass;
            this.name = name;
            this.width = width;
        }

        String getName() {
            return name;
        }

        int getWidth() {
            return width;
        }

        public Class getColumnClass() {
            return columnClass;
        }
    }

    public static Column NAME = new Column( "Name", String.class, 170 );
    public static Column THUMBNAIL = new Column( "Thumbnail", ImageIcon.class, 150 );
    public static Column IS_INSTALLED = new Column( "Installed?", ImageIcon.class, 100 );
    public static Column IS_UP_TO_DATE = new Column( "Update Available?", ImageIcon.class, 120 );
    public static Column SELECTION_CHECKBOX = new Column( "Select", Boolean.class, 60 );


    // Icons for table entries
    private static ImageIcon isInstalledIcon;
    private static ImageIcon updateAvailableIcon;

    static {
        BufferedImage checkMarkImg = null;
        BufferedImage exclamationMarkImg = null;
        try {
            checkMarkImg = ImageLoader.loadBufferedImage( "images/check-mark-3.png" );
            exclamationMarkImg = ImageLoader.loadBufferedImage( "images/exclamation-mark-1.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        isInstalledIcon = new ImageIcon( checkMarkImg );
        updateAvailableIcon = new ImageIcon( exclamationMarkImg );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private List columns;
//    private boolean[] simSelections;
    private int checkBoxColumnIndex = -1;
    private int nameColumnIndex = -1;

    /**
     * @param sims
     * @param sortType
     * @param listSelectionModel
     * @param columns
     */
    public SimTable( List sims, SimComparator sortType, int listSelectionModel, List columns ) {

        this.columns = columns;
//        simSelections = new boolean[sims.size()];

        // Set the selection mode to be row only
        setColumnSelectionAllowed( false );
        setRowSelectionAllowed( true );
        setSelectionMode( listSelectionModel );

        // Create the row data for the table
        Object[][] rowData = createRowData( sims, sortType, columns );

        // Create the header
        Object[] header = new Object[columns.size()];
        for( int i = 0; i < columns.size(); i++ ) {
            Column column = (Column)columns.get( i );
            header[i] = column.getName();
        }

        // Create the table model
        TableSorter sorter = new TableSorter( new SimTableModel( rowData, header, columns ) );
//        this.setModel( new SimTableModel( rowData, header, columns ));
        this.setModel( sorter );
        sorter.setTableHeader( this.getTableHeader() );

        // Set the check box renderers and editors on the selection column
        for( int i = 0; i < columns.size(); i++ ) {
            Column column = (Column)columns.get( i );
            if( column == SELECTION_CHECKBOX ) {
                this.checkBoxColumnIndex = i;
                this.getColumnModel().getColumn( i ).setCellRenderer( new CheckBoxRenderer() );
                this.getColumnModel().getColumn( i ).setCellEditor( new CheckBoxEditor() );

                // Add a mouse listener that will flip the checkbox when a row is clicked on
                addMouseListener( new CheckBoxSelectionFlipper() );
            }

            if( column == NAME ) {
                nameColumnIndex = i;
            }
        }

        // Get max row height
        int hMax = getMaxRowHeight();
        if( hMax >= 1 ) {
            setRowHeight( hMax );
        }

        // Set the column widths
        this.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
        for( int i = 0; i < columns.size(); i++ ) {
            Column column = (Column)columns.get( i );
            getColumn( column.getName() ).setPreferredWidth( column.getWidth() );
            getColumn( column.getName() ).setMinWidth( column.getWidth() );
            getColumn( column.getName() ).setMaxWidth( column.getWidth() );
//            getColumn( column.getName() ).setWidth( column.getWidth() );
        }

        // If we've got check boxes, that should be the only selection mechanism
        if( columns.contains( SELECTION_CHECKBOX ) ) {
            setCellSelectionEnabled( false );
        }
    }

    /**
     * Override this method so that it returns the preferred
     * size of the JTable instead of the default fixed size
     *
     * @return the preferred size
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * Creates the array of data that populates the table model
     *
     * @param sims
     * @param sortType
     * @param columns
     * @return the array of data that populates the table model
     */
    private Object[][] createRowData( List sims, SimComparator sortType, List columns ) {
        Collections.sort( sims, sortType );
        Object[][] rowData = new Object[sims.size()][columns.size()];
        for( int i = 0; i < sims.size(); i++ ) {
            Simulation sim = (Simulation)sims.get( i );
            Object[] row = new Object[ columns.size()];
            boolean connected = PhetSiteConnection.instance().isConnected();
            for( int j = 0; j < row.length; j++ ) {
                if( columns.get( j ) == SELECTION_CHECKBOX ) {
                    row[j] = new Boolean( false );
                }
                if( columns.get( j ) == NAME ) {
                    row[j] = sim.getName();
                }
                else if( columns.get( j ) == THUMBNAIL ) {
                    row[j] = sim.getThumbnail();
                }
                else if( columns.get( j ) == IS_INSTALLED ) {
                    row[j] = sim.isInstalled() ? isInstalledIcon : null;
                }
                else if( columns.get( j ) == IS_UP_TO_DATE ) {
//                    try {
                        if( connected ) {
//                        if( SimResource.isUpdateEnabled() && connected ) {
                            row[j] = ( SimResource.isUpdateEnabled() && sim.isInstalled() && !sim.isCurrent() ) ? updateAvailableIcon : null;
                        }
//                    }
//                    catch( SimResourceException e ) {
//                        e.printStackTrace();
//                    }
                }
            }
            rowData[i] = row;
        }
        return rowData;
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
                String simName = (String)this.getValueAt( idx, nameColumnIndex );
                sim = Simulation.getSimulationForName( simName );
            }
        }
        return sim;
    }

    /**
     * Returns the currently selected simulations.
     *
     * @return the currently selected simulations
     */
    public Simulation[] getSelections() {
        List selectedSims = new ArrayList();
        int nameColumIndex = nameColumnIndex;

        // We build the list of selected simulations differently depending on whether there is a
        // column of checkboxes or not
        if( checkBoxColumnIndex > -1 ) {
            for( int row = 0; row < getRowCount(); row++ ) {
                Boolean isSelected = (Boolean)getModel().getValueAt( row, checkBoxColumnIndex );
                if( isSelected.booleanValue() ) {
                    String simName = (String)this.getValueAt( row, nameColumIndex );
                    Simulation sim = Simulation.getSimulationForName( simName );
                    selectedSims.add( sim );
                }
            }
        }
        else {
            ListSelectionModel lsm = getSelectionModel();
            for( int i = lsm.getMinSelectionIndex(); i <= lsm.getMaxSelectionIndex(); i++ ) {
                if( lsm.isSelectedIndex( i ) ) {
                    String simName = (String)this.getValueAt( i, nameColumIndex );
                    Simulation sim = Simulation.getSimulationForName( simName );
                    selectedSims.add( sim );
                }
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
        private List columns;

        public SimTableModel( Object[][] data, Object[] columnNames, List columns ) {
            super( data, columnNames );
            this.columns = columns;
        }

        public Class getColumnClass( int columnIndex ) {
            return ( (Column)columns.get( columnIndex ) ).getColumnClass();
        }

        /**
         * The only editable column is the one with the checkbox
         *
         * @param row
         * @param column
         * @return always returns false
         */
        public boolean isCellEditable( int row, int column ) {
            return ( columns.get( column ) == SELECTION_CHECKBOX );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // CellRenderer and CellEditor for the check boxes
    //--------------------------------------------------------------------------------------------------

    class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor() {
            super( new JCheckBox() );
        }

        public boolean isCellEditable( EventObject anEvent ) {
            return true;
        }

    }

    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment( JCheckBox.CENTER );
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            if( value instanceof Boolean ) {
                Boolean b = (Boolean)value;
                setSelected( b.booleanValue() );
            }
            return this;
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

    private class CheckBoxSelectionFlipper extends MouseAdapter {
        public void mouseClicked( MouseEvent e ) {
            if( getSelectedColumn() != checkBoxColumnIndex ) {
                int selectedRow = getSelectedRow();
                boolean oldValue = ( (Boolean)getValueAt( selectedRow, checkBoxColumnIndex ) ).booleanValue();
                setValueAt( new Boolean( !oldValue ), selectedRow, checkBoxColumnIndex );
            }
        }
    }
}