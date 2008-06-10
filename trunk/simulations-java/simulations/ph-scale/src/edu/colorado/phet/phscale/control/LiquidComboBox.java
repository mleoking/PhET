/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.control;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.phscale.model.LiquidType;
import edu.umd.cs.piccolox.pswing.PComboBox;


public class LiquidComboBox extends PComboBox {
    
    public static final Font FONT = new PhetFont( 18 );
    
    public LiquidComboBox() {
        super();
        setFont( FONT );
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ) );
        
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
