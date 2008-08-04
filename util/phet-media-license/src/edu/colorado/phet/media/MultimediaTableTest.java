package edu.colorado.phet.media;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * User: Sam Reid
 * Date: Aug 16, 2006
 * Time: 9:50:34 PM
 * Copyright (c) Aug 16, 2006 by Sam Reid
 */

public class MultimediaTableTest extends JTable {
    private MultimediaTableTest.MultimediaTableModel tableModel;
    int columns = 4;
    int rows = 3;

    public MultimediaTableTest() {
        tableModel = new MultimediaTableTest.MultimediaTableModel();
        setModel( tableModel );
        getColumnModel().getColumn( 1 ).setCellRenderer( new MultimediaTableTest.MyRenderer() );
        getColumnModel().getColumn( 1 ).setPreferredWidth( 100 );
        setRowHeight( 100 );

        JTableHeader header = getTableHeader();
    }

    class MultimediaTableModel extends AbstractTableModel {

        public int getColumnCount() {
            return columns;
        }

        public int getRowCount() {
            return rows;
        }

        public Object getValueAt( int rowIndex, int columnIndex ) {
            return "r=" + rowIndex + ", col=" + columnIndex;
        }
    }

    public class MyRenderer extends DefaultTableCellRenderer {

        /*
        * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
        */
        public Component getTableCellRendererComponent( JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus,
                                                        int row, int column ) {
            ImageIcon icon = null;
            try {
                icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/Phet-logo-32x32.gif" ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            setText( (String) value );
            setIcon( icon );
            return this;
        }
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        JPanel contentPanel = new JPanel( new BorderLayout() );
        MultimediaTableTest table = new MultimediaTableTest();

        // Add header in NORTH slot
        contentPanel.add( table.getTableHeader(), BorderLayout.NORTH );

        // Add table itself to CENTER slot
        contentPanel.add( table, BorderLayout.CENTER );
        frame.setContentPane( contentPanel );
        frame.setSize( 600, 600 );
        frame.setVisible( true );
    }
}
