// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.ComboBoxNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.*;

/**
 * This control panel lets the user choose between solute dispenser types (i.e. sugar vs salt)
 *
 * @author Sam Reid
 */
public class ComboBoxSoluteControlPanelNode extends SoluteControlPanelNode {
    public ComboBoxSoluteControlPanelNode( final Property<DispenserType> dispenserType ) {
        super( new SoluteComboBox( dispenserType ) );
    }

    static class SoluteComboBox extends PNode {
        SoluteComboBox( final Property<DispenserType> dispenserType ) {
            addChild( new ComboBoxNode<DispenserType>( SALT, SUGAR, SODIUM_NITRATE, CALCIUM_CHLORIDE, ETHANOL ) {{
                //When the model changes, update the view
                selectedItem.addObserver( new VoidFunction1<DispenserType>() {
                    public void apply( DispenserType dt ) {
                        dispenserType.set( dt );
                    }
                } );

                //When the view changes, update the model
                dispenserType.addObserver( new VoidFunction1<DispenserType>() {
                    public void apply( DispenserType dt ) {
                        selectedItem.set( dt );
                    }
                } );
            }} );
        }
    }
}