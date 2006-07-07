/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * CheckBoxTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckBoxTest extends JFrame {
    public CheckBoxTest() throws HeadlessException {

        setSize( 300, 300 );
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        Class columnClasses[] = {Boolean.class, String.class};
        JTable jt = new JTable();
        DefaultTableModel model = (DefaultTableModel)jt.getModel();
        model.addColumn( "A", new Object[]{new Boolean( true ), new Boolean( false )} );
        model.addColumn( "B", new Object[]{new Boolean( true ), new Boolean( false )} );
        jt.getColumnModel().getColumn( 0 ).setCellRenderer( new CheckBoxRenderer() );
        jt.getColumnModel().getColumn( 0 ).setCellEditor( new CheckBoxEditor() );

        getContentPane().add( jt );
    }

    class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor() {
            super( new JCheckBox() );
        }
    }

    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            super( "FOO" );
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            System.out.println( "value = " + value );
            if( value instanceof Boolean ) {
                Boolean b = (Boolean)value;
                setSelected( b.booleanValue() );
            }
            return this;
        }
    }


    public static void main( String[] args ) {
        CheckBoxTest cbt = new CheckBoxTest();
        cbt.setVisible( true );
    }


}
