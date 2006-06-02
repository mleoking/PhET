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
import java.util.Vector;
import java.util.Comparator;
import java.util.Collections;

/**
 * SimulationTable
 * <p/>
 * A JTable for displaying simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationTable extends JTable implements SimulationContainer {

    public static SimulationComparator NAME_SORT = new NameComparator();
    public static SimulationComparator MOST_RECENTLY_USED_SORT = new LastLaunchTimeComparator();

    private List sims;
//    private SimulationComparator currentComparator = MOST_RECENTLY_USED_SORT;
//    private SimulationComparator currentComparator = NAME_SORT;

    /**
     * Constructor
     *
     * @param simList
     * @param showThumbnails
     */
    public SimulationTable( List simList, boolean showThumbnails, SimulationComparator sortType ) {
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
        TableModel tableModel = new SimulationTableModel( rowData, header );
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
        nameCol.setMinWidth( 150 );
        if( showThumbnails ) {
            TableColumn thumbnailCol = getColumn( "thumbnail" );
            thumbnailCol.setMinWidth( 150 );
        }

        // Sort the table
//        sort();
    }

    public Simulation getSelection() {
        Simulation sim = null;
        int idx = getSelectedRow();
        if( idx >= 0 ) {
            String simName = (String)this.getValueAt( idx, 0 );
            sim = Simulation.getSimulationForName( simName );
        }
        return sim;
    }

    public void addSimulation( Simulation simulation ) {
        DefaultTableModel model = (DefaultTableModel)getModel();
        Object[] row = new Object[]{simulation.getName(), simulation.getThumbnail()};
        model.addRow( row );
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
    // Comparators
    //--------------------------------------------------------------------------------------------------
    public static abstract class SimulationComparator implements Comparator {
    }

    private static class NameComparator extends SimulationComparator {
        public int compare( Object o1, Object o2 ) {
            if( !( o1 instanceof Simulation && o2 instanceof Simulation ) ) {
                throw new ClassCastException();
            }
            Simulation s1 = (Simulation)o1;
            Simulation s2 = (Simulation)o2;
            return s1.getName().compareTo( s2.getName() );
        }
    }

    private static class LastLaunchTimeComparator extends SimulationComparator {
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
    // Sorting code. Adapted from The Java Developers Almanac 1.4
    //--------------------------------------------------------------------------------------------------

    public void sort() {
        // Disable autoCreateColumnsFromModel otherwise all the column customizations
        // and adjustments will be lost when the model data is sorted
        setAutoCreateColumnsFromModel( false );

        // Sort all the rows in descending order based on the
        // values in the second column of the model
        sortAllRowsBy( (DefaultTableModel)getModel(), 0, true );
    }

    // Regardless of sort order (ascending or descending), null values always appear last.
    // colIndex specifies a column in model.
    public void sortAllRowsBy( DefaultTableModel model, int colIndex, boolean ascending ) {
        Vector data = model.getDataVector();
        Collections.sort( data, new ColumnSorter( colIndex, ascending ) );
        model.fireTableStructureChanged();
    }

    // This comparator is used to sort vectors of data
    public class ColumnSorter implements Comparator {
        int colIndex;
        boolean ascending;

        ColumnSorter( int colIndex, boolean ascending ) {
            this.colIndex = colIndex;
            this.ascending = ascending;
        }

        public int compare( Object a, Object b ) {
            Vector v1 = (Vector)a;
            Vector v2 = (Vector)b;
            Object o1 = v1.get( colIndex );
            Object o2 = v2.get( colIndex );

            // Treat empty strains like nulls
            if( o1 instanceof String && ( (String)o1 ).length() == 0 ) {
                o1 = null;
            }
            if( o2 instanceof String && ( (String)o2 ).length() == 0 ) {
                o2 = null;
            }

            // Sort nulls so they appear last, regardless
            // of sort order
            if( o1 == null && o2 == null ) {
                return 0;
            }
            else if( o1 == null ) {
                return 1;
            }
            else if( o2 == null ) {
                return -1;
            }
            else if( o1 instanceof Comparable ) {
                if( ascending ) {
                    return ( (Comparable)o1 ).compareTo( o2 );
                }
                else {
                    return ( (Comparable)o2 ).compareTo( o1 );
                }
            }
            else {
                if( ascending ) {
                    return o1.toString().compareTo( o2.toString() );
                }
                else {
                    return o2.toString().compareTo( o1.toString() );
                }
            }
        }
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

    public Simulation getSimulation() {
        return getSelection();
    }

}
