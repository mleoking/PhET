
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolox.pswing.PComboBox;

/**
 * Combo box for selecting a solute.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteComboBox extends PComboBox {
    
    private static final Font FONT = new PhetFont( 16 );
    
    /*
     * Choices in the combo box.
     */
    private static class Choice {

        private String name;
        private String symbol;

        public Choice( Solute solute ) {
            this.name = solute.getName();
            this.symbol = solute.getSymbol();
        }

        public String getName() {
            return name;
        }

        /**
         * toString determines how the choices look in the combo box.
         */
        public String toString() {
            String s = name;
            if ( symbol != null && symbol.length() > 0 ) {
                s += " (" + symbol + ")";
            }
            return HTMLUtils.toHTMLString( s );
        }
    }

    public SoluteComboBox() {
        super();
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) ); // hack for PComboBox
        setBackground( Color.WHITE ); // hack for PComboBox
        setFont( FONT );
        
        // items
        int maxWidth = 0;
        int maxHeight = 0;
        Solute[] solutes = SoluteFactory.getSolutes();
        for ( int i = 0; i < solutes.length; i++ ) {
            
            // add item to the menu
            Solute solute = solutes[i];
            Choice choice = new Choice( solute );
            addItem( choice );
            
            // calculate max item dimensions
            JLabel label = new JLabel( choice.toString() );
            label.setFont( FONT );
            maxWidth = Math.max( maxWidth, label.getPreferredSize().width );
            maxHeight = Math.max( maxHeight, label.getPreferredSize().height );
        }
        
//        final ListCellRenderer lcr = getRenderer();
//        setRenderer( new ListCellRenderer() {
//
//            public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
//                if ( value instanceof JSeparator ) {
//                    return (JSeparator) value;
//                }
//                else {
//                    return (JLabel) lcr.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
//                }
//            }
//        } );
//
//        DefaultComboBoxModel model = new DefaultComboBoxModel() {
//
//            public void setSelectedItem( Object o ) {
//                if ( o instanceof JSeparator )
//                    return;
//                super.setSelectedItem( o );
//            }
//        };
//        setModel( model );
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
        
        setPreferredSize( new Dimension( maxWidth + 60, maxHeight + 5 ) );
        setRenderer( new CustomRenderer( getPreferredSize().height ) );
    }
    
    /**
     * Sets the selection based on the Solute's name.
     * @param solute
     */
    public void setSelectionByName( String soluteName ) {
        Object item = null;
        int count = getItemCount();
        for ( int i = 0; i < count; i++ ) {
            Object o = getItemAt( i );
            if ( o instanceof Choice ) {
                Choice choice = (Choice) o;
                if ( choice.getName().equals( soluteName ) ) {
                    item = choice;
                    break;
                }
            }
        }
        if ( item == null ) {
            throw new IllegalArgumentException( "solute is not in the list: " + soluteName );
        }
        else {
            setSelectedItem( item );
        }
    }
    
    /**
     * Gets the name of the selected Solute.
     * @return
     */
    public String getSoluteName() {
        return ( (Choice) getSelectedItem() ).getName();
    }
    
    
    private static class CustomRenderer implements ListCellRenderer {

        private final int height;
        
        public CustomRenderer( int minHeight ) {
            super();
            this.height = minHeight;
        }

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component component = null;
            if ( value instanceof JSeparator ) {
                component = (JSeparator) value; 
            }
            else {
                JLabel label = new JLabel( value.toString() );
                label.setFont( list.getFont() );
                label.setOpaque( true );
                label.setHorizontalAlignment( SwingConstants.LEFT );
                label.setVerticalAlignment( SwingConstants.TOP );
                if ( isSelected ) {
                    label.setBackground( list.getSelectionBackground() );
                    label.setForeground( list.getSelectionForeground() );
                }
                else {
                    label.setBackground( list.getBackground() );
                    label.setForeground( list.getForeground() );
                }
                label.setBorder( new EmptyBorder( 2, 4, 2, 6 ) ); // top, left, bottom, right
                label.setPreferredSize( new Dimension( label.getPreferredSize().width, Math.max( height, label.getPreferredSize().height ) ) );
                component = label;
            }
            return component;
        }
    }
}
