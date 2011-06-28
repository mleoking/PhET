// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PComboBox;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * This control panel lets the user choose between solute dispenser types (i.e. sugar vs salt)
 *
 * @author Sam Reid
 */
public class SoluteControlPanelNode extends AbstractSoluteControlPanelNode {
    public SoluteControlPanelNode( final Property<DispenserType> dispenserType, PSwingCanvas canvas ) {
        super( new SoluteComboBox( canvas )
               /*
               new PSwing( new VerticalLayoutPanel() {{
                   add( new PropertyRadioButton<DispenserType>( SugarAndSaltSolutionsResources.Strings.SALT, dispenserType, SALT ) {{setFont( CONTROL_FONT );}} );
                   add( new PropertyRadioButton<DispenserType>( SugarAndSaltSolutionsResources.Strings.SUGAR, dispenserType, SUGAR ) {{setFont( CONTROL_FONT );}} );
               }} ) )
               */
        );
    }

    static class SoluteComboBox extends PNode {
        SoluteComboBox( PSwingCanvas canvas ) {
            PComboBox comboBox = new PComboBox( new String[] { "Sodium Chloride", "Sucrose", "Sodium Nitrate", "Calcium Chloride", "Ethanol" } );
            PSwing comboBoxPSwing = new PSwing( comboBox );
            comboBox.setEnvironment( comboBoxPSwing, canvas );
            addChild( new PSwing( comboBox ) );
        }
    }
}