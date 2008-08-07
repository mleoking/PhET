package edu.colorado.phet.licensing.media;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

/**
 * User: Sam Reid
 * Date: Aug 16, 2006
 * Time: 9:50:34 PM
 * Copyright (c) Aug 16, 2006 by Sam Reid
 */

public class MultimediaTable extends JTable {
    private MultimediaTableModel tableModel;

    public MultimediaTable() {
        tableModel = new MultimediaTableModel();
        tableModel.addColumn( "Simulation" );
        tableModel.addColumn( "Name" );
        tableModel.addColumn( "Image" );
        tableModel.addColumn( "Width" );
        tableModel.addColumn( "Height" );
        tableModel.addColumn( "Non-Phet" );
        tableModel.addColumn( "Source" );
        tableModel.addColumn( "Done" );
        tableModel.addColumn( "Notes" );

        TableSorter sorter = new TableSorter( tableModel ) {
            protected Comparator getComparator( int column ) {
                if ( column == 3 || column == 4 ) {
                    return TableSorter.COMPARABLE_COMAPRATOR;
                }
                return super.getComparator( column );
            }
        };
        sorter.setColumnComparator( Integer.class, new Comparator() {
            public int compare( Object o1, Object o2 ) {
                Integer a = (Integer) o1;
                Integer b = (Integer) o2;
                return a.compareTo( b );
            }
        } );

        sorter.setTableHeader( getTableHeader() ); //ADDED THIS
        setModel( sorter );

        getColumnModel().getColumn( 2 ).setCellRenderer( new ImageRenderer() );
        getColumnModel().getColumn( 5 ).setCellRenderer( new CheckBoxRenderer() );
        this.getColumnModel().getColumn( 5 ).setCellEditor( new CheckBoxEditor() );

        getColumnModel().getColumn( 7 ).setCellRenderer( new CheckBoxRenderer() );
        getColumnModel().getColumn( 7 ).setCellEditor( new CheckBoxEditor() );

        getColumnModel().getColumn( 2 ).setPreferredWidth( 100 );
        setRowHeight( 100 );

        tableModel.addTableModelListener( new MyTableModelListener( this ) );
    }

    class MyTableModelListener implements TableModelListener {
        JTable table;

        // It is necessary to keep the table since it is not possible
        // to determine the table from the event's source
        MyTableModelListener( JTable table ) {
            this.table = table;
        }

        public void tableChanged( TableModelEvent e ) {
            int firstRow = e.getFirstRow();
            int lastRow = e.getLastRow();
            int mColIndex = e.getColumn();

            switch( e.getType() ) {
                case TableModelEvent.INSERT:
                    // The inserted rows are in the range [firstRow, lastRow]
                    for ( int r = firstRow; r <= lastRow; r++ ) {
                        // Row r was inserted
                    }
                    break;
                case TableModelEvent.UPDATE:
                    if ( firstRow == TableModelEvent.HEADER_ROW ) {
                        if ( mColIndex == TableModelEvent.ALL_COLUMNS ) {
                            // A column was added
                        }
                        else {
                            // Column mColIndex in header changed
                        }
                    }
                    else {
                        // The rows in the range [firstRow, lastRow] changed
//                        System.out.println( "MultimediaTable$MyTableModelListener.tableChanged" );

                        for ( int r = firstRow; r <= lastRow; r++ ) {
                            // Row r was changed
                            ImageEntry entry = (ImageEntry) list.get( r );

                            entry.setNonPhet( ( (Boolean) tableModel.getValueAt( r, 5 ) ).booleanValue() );
                            entry.setSource( (String) tableModel.getValueAt( r, 6 ) );
                            entry.setDone( ( (Boolean) tableModel.getValueAt( r, 7 ) ).booleanValue() );
                            entry.setNotes( (String) tableModel.getValueAt( r, 8 ) );
                            if ( mColIndex == TableModelEvent.ALL_COLUMNS ) {
                                // All columns in the range of rows have changed
                            }
                            else {
                                // Column mColIndex changed
                            }
                        }
                    }
                    break;
                case TableModelEvent.DELETE:
                    // The rows in the range [firstRow, lastRow] changed
                    for ( int r = firstRow; r <= lastRow; r++ ) {
                        // Row r was deleted
                    }
                    break;
            }
        }
    }

    class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor() {
            super( new JCheckBox() );
        }

        public boolean isCellEditable( EventObject anEvent ) {
            return true;
        }
    }

    ArrayList list = new ArrayList();

    public void addEntry( final ImageEntry entry ) {
        Object[] rowData = new Object[]{entry.getSimulationName(), entry.getImageName(), entry.toImage(),
                new Integer( entry.toImage().getWidth() ), new Integer( entry.toImage().getHeight() ), new Boolean( entry.isNonPhet() ),
                entry.getSource(), new Boolean( entry.isDone() ), entry.getNotes()
        };
        tableModel.addRow( rowData );
        list.add( entry );
        entry.addListener( new ImageEntry.Listener() {
            public void nonPhetChanged() {
                int row = list.indexOf( entry );
                tableModel.setValueAt( new Boolean( entry.isNonPhet() ), row, 5 );
            }

            public void sourceChanged() {
                int row = list.indexOf( entry );
                tableModel.setValueAt( entry.getSource(), row, 6 );
            }

            public void notesChanged() {
                int row = list.indexOf( entry );
                tableModel.setValueAt( entry.getNotes(), row, 8 );
            }

            public void doneChanged() {
                int row = list.indexOf( entry );
                tableModel.setValueAt( new Boolean( entry.isDone() ), row, 7 );
            }

        } );
    }

    class MultimediaTableModel extends DefaultTableModel {
    }

    public class ImageRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column ) {
            ImageIcon icon = null;
            BufferedImage image = (BufferedImage) value;
            if ( image.getWidth() > 100 || image.getHeight() > 100 ) {
                if ( image.getWidth() > image.getHeight() ) {
                    image = BufferedImageUtils.rescaleXMaintainAspectRatio( image, 100 );
                }
                else {
                    image = BufferedImageUtils.rescaleYMaintainAspectRatio( image, 100 );
                }
            }
            icon = new ImageIcon( image );
            setIcon( icon );
            return this;
        }
    }

    public class CheckBoxRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column ) {
            return new JCheckBox( "", ( (Boolean) value ).booleanValue() );
        }
    }
}
