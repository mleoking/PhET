
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.umd.cs.piccolox.pswing.PComboBox;

/**
 * Combo box for selecting a solute.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteComboBox extends PComboBox {
    
    private static final int MIN_ITEM_HEIGHT = 35; //TODO discover this
    
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
        
        // items
        Solute[] solutes = SoluteFactory.getSolutes();
        for ( int i = 0; i < solutes.length; i++ ) {
            Choice choice = new Choice( solutes[i] );
            addItem( choice );
        }
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
        
//        setRenderer( new CustomRenderer( MIN_ITEM_HEIGHT ) );
    }
    
    /**
     * Sets the selection based on the Solute's name.
     * @param solute
     */
    public void setSolute( Solute solute ) {
        Object item = null;
        int count = getItemCount();
        for ( int i = 0; i < count; i++ ) {
            Choice choice = (Choice) getItemAt( i );
            if ( choice.getName().equals( solute.getName() ) ){
                item = choice;
                break;
            }
        }
        if ( item == null ) {
            throw new IllegalArgumentException( "solute is not in the list: " + solute.toString() );
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
    
    private static class CustomRenderer extends JLabel implements ListCellRenderer {

        private final int height;
        
        public CustomRenderer( int minHeight ) {
            super();
            setOpaque( true );
            setHorizontalAlignment( LEFT );
            setVerticalAlignment( CENTER );
            this.height = minHeight;
        }

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {

            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
            }
            else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }

            setText( value.toString() );
            setFont( list.getFont() );
            setBorder( new EmptyBorder( 2, 2, 2, 2 ) );
            setPreferredSize( new Dimension( getPreferredSize().width, Math.max( height, getPreferredSize().height ) ) );
            return this;

        }
    }
}
