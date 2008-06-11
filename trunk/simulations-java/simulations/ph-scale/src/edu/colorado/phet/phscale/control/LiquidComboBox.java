/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;

import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.model.LiquidType;
import edu.umd.cs.piccolox.pswing.PComboBox;


public class LiquidComboBox extends PComboBox {
    
    public static final Font FONT = PHScaleConstants.CONTROL_FONT;
    
    public LiquidComboBox() {
        super();
        setFont( FONT );
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
        setBackground( Color.WHITE );
       
        addItem( LiquidType.LEMON_JUICE );
        addItem( LiquidType.MILK );
        addItem( LiquidType.COLA );
        addItem( LiquidType.BEER );
    }
    
    public void setLiquidType( LiquidType liquidType ) {
        setSelectedItem( liquidType );
    }
    
    public LiquidType getLiquidType() {
        return (LiquidType) getSelectedItem();
    }
}
