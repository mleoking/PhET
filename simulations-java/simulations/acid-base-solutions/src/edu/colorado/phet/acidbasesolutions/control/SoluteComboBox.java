
package edu.colorado.phet.acidbasesolutions.control;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;

import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;


public class SoluteComboBox extends JComboBox {
    
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
        
        // items
        Solute[] solutes = SoluteFactory.getSolutes();
        for ( int i = 0; i < solutes.length; i++ ) {
            addItem( new Choice( solutes[i] ) );
        }
        
        // make all items visible (no vertical scroll bar)
        setMaximumRowCount( getItemCount() );
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
    
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        final SoluteComboBox comboBox = new SoluteComboBox();
        comboBox.addItemListener( new ItemListener() {
            public void itemStateChanged( ItemEvent e ) {
                if ( e.getStateChange() == ItemEvent.SELECTED ) {
                System.out.println( "selection=" + comboBox.getSoluteName() );
                }
            }
        });
        frame.setContentPane( comboBox );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
