// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.WhiteControlPanelNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.CALCIUM_CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.GLUCOSE;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SODIUM_NITRATE;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode.createTitle;

/**
 * Control node that allows the user to choose from different kits, which each have different combinations of solutes
 *
 * @author Sam Reid
 */
public class MicroKitControlNode extends PNode {
    public final KitSelectionNode<DispenserRadioButtonSet> kitSelectionNode;

    public MicroKitControlNode( final Property<Integer> selectedKit, final Property<DispenserType> dispenserType ) {

        //Show the radio buttons on two lines to show scientific name and molecular formula to save horizontal space
        kitSelectionNode = new KitSelectionNode<DispenserRadioButtonSet>(
                selectedKit,
                createTitle(),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new Item( SODIUM_CHLORIDE_NA_CL, SALT ), new Item( SUCROSE_C_12_H_22_O_11, SUGAR ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new Item( SODIUM_CHLORIDE_NA_CL, SALT ), new Item( CALCIUM_CHLORIDE_CA_CL_2, CALCIUM_CHLORIDE ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new Item( SODIUM_CHLORIDE_NA_CL, SALT ), new Item( SODIUM_NITRATE_NA_NO_3, SODIUM_NITRATE ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new Item( SUCROSE_C_12_H_22_O_11, SUGAR ), new Item( GLUCOSE_C_6_H_12_O_11, GLUCOSE ) ) ) );
        addChild( new WhiteControlPanelNode( kitSelectionNode ) );

        //When switching to a new kit, switch to a dispenser that is in the set (if not already selecting it).  If switching from a set that contains NaCl to a new set that also contains NaCl, then keep the selection
        selectedKit.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer index ) {
                kitSelectionNode.getKit( index ).content.setSelected();
            }
        } );
    }
}