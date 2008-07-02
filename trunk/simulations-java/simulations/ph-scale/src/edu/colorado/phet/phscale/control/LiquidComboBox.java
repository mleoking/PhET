/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.LiquidDescriptor;
import edu.umd.cs.piccolox.pswing.PComboBox;

/**
 * LiquidComboBox is the combo box for selecting a liquid type.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidComboBox extends PComboBox {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LiquidComboBox() {
        super();
        setFont( FONT );
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
        setBackground( Color.WHITE );
        
        // sort the choices by ascending pH value
        LiquidDescriptor[] choices = LiquidDescriptor.getAllInstances();
        Comparator comparator = new Comparator() {
            public int compare( Object o1, Object o2 ) {
                int rval = 0;
                final double pH1 = ((LiquidDescriptor)o1).getPH();
                final double pH2 = ((LiquidDescriptor)o2).getPH();
                if ( pH1 == pH2 ) {
                    rval = 0;
                }
                else if ( pH1 < pH2 ) {
                    rval = -1;
                }
                else {
                    rval = 1;
                }
                return rval;
            }
        };
        Arrays.sort( choices, comparator );
        
        // add the choices in descending pH value
        for ( int i = choices.length - 1; i >= 0; i-- ) {
            addItem( choices[i] );
        }
        
        // make all choices visible without scroll bars
        setMaximumRowCount( choices.length );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setChoice( LiquidDescriptor liquid ) {
        setSelectedItem( liquid );
    }
    
    public LiquidDescriptor getChoice() {
        LiquidDescriptor choice = null;
        Object selectedItem = getSelectedItem();
        if ( selectedItem instanceof LiquidDescriptor ) {
            choice = (LiquidDescriptor) selectedItem;
        }
        return choice;
    }
}
