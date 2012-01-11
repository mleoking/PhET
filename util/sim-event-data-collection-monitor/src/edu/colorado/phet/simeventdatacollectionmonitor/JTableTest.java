// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class JTableTest {

    public static final Object[][] data = new Object[][] { { 1, 2 }, { 3, 4 } };
    public static final Object[] columnNames = new Object[] { "a", "b" };
    public static final DefaultTableModel tableModel = new DefaultTableModel( data, columnNames );
    public static final JTable table = new JTable( tableModel );

    public static void main( String[] args ) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                new JFrame() {{
                    setContentPane( new JScrollPane( table ) );

                    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                    pack();
                    setLocationRelativeTo( null );
                }}.setVisible( true );
            }
        } );
    }
}