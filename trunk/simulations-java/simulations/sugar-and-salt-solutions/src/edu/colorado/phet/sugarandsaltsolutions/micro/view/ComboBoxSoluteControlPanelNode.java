// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * This control panel lets the user choose between solute dispenser types (i.e. sugar vs salt)
 *
 * @author Sam Reid
 */
public class ComboBoxSoluteControlPanelNode extends SoluteControlPanelNode {
    public ComboBoxSoluteControlPanelNode( final Property<DispenserType> dispenserType, PSwingCanvas canvas ) {
        super( new SoluteComboBox( canvas ) );
    }

    static class SoluteComboBox extends PNode {
        SoluteComboBox( PSwingCanvas canvas ) {
            addChild( new ComboBoxNode<String>( "Sodium Chloride", "Sucrose", "Sodium Nitrate", "Calcium Chloride", "Ethanol" ) );
        }
    }
}