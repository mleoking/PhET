// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.beaker;

import java.awt.Color;
import java.awt.Font;

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
        
        // add the choices
        LiquidDescriptor[] choices = LiquidDescriptor.getAllInstances();
        for ( int i = 0; i < choices.length; i++ ) {
            addItem( choices[i] );
        }
        
        // make all choices visible without scroll bars
        setMaximumRowCount( choices.length );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setChoice( LiquidDescriptor liquid ) {
        if ( !liquid.equals( getChoice() ) ) {
            setSelectedItem( liquid );
        }
    }
    
    public LiquidDescriptor getChoice() {
        return (LiquidDescriptor) getSelectedItem();
    }
}
