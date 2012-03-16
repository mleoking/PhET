// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.DispenserRadioButtonSet;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SelectableSoluteItem;
import edu.colorado.phet.sugarandsaltsolutions.common.view.WhiteControlPanelNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.GLUCOSE;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode.createTitle;

/**
 * Control node that allows the user to choose from different kits, which each have different combinations of solutes
 *
 * @author Sam Reid
 */
public class MicroKitControlNode extends PNode {
    public final KitSelectionNode<DispenserRadioButtonSet> kitSelectionNode;

    public MicroKitControlNode( final Property<Integer> selectedKit, final Property<DispenserType> dispenserType,

                                //A button that shows the periodic table when pressed, shown inside the kit selection node since the selected item controls what is highlighted in the periodic table
                                TextButtonNode periodicTableButton,

                                boolean onlyOneKit ) {

        //Show the radio buttons on two lines to show scientific name and molecular formula to save horizontal space
        //For onlyOneKit, only show sugar and salt in the micro tab.  Used for wet lab in fall 2011 and can probably be deleted afterwards.
        kitSelectionNode = onlyOneKit ? new KitSelectionNode<DispenserRadioButtonSet>(
                selectedKit,
                createTitle(),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new SelectableSoluteItem( SODIUM_CHLORIDE_NA_CL, SALT ), new SelectableSoluteItem( SUCROSE_C_12_H_22_O_11, SUGAR ) ) ) )
                                      : new KitSelectionNode<DispenserRadioButtonSet>(
                selectedKit,
                createTitle(),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new SelectableSoluteItem( SODIUM_CHLORIDE_NA_CL, SALT ), new SelectableSoluteItem( SUCROSE_C_12_H_22_O_11, SUGAR ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new SelectableSoluteItem( SODIUM_CHLORIDE_NA_CL, SALT ), new SelectableSoluteItem( CALCIUM_CHLORIDE_CA_CL_2, CALCIUM_CHLORIDE ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new SelectableSoluteItem( SODIUM_CHLORIDE_NA_CL, SALT ), new SelectableSoluteItem( SODIUM_NITRATE_NA_NO_3, SODIUM_NITRATE ) ) ),
                new Kit<DispenserRadioButtonSet>( new DispenserRadioButtonSet( dispenserType, new SelectableSoluteItem( SUCROSE_C_12_H_22_O_11, SUGAR ), new SelectableSoluteItem( GLUCOSE_C_6_H_12_O_6, GLUCOSE ) ) ) );


        //Show the selection dialog above the periodic table button
        addChild( new WhiteControlPanelNode( new VBox( kitSelectionNode, periodicTableButton ) ) );

        //When switching to a new kit, switch to a dispenser that is in the set (if not already selecting it).  If switching from a set that contains NaCl to a new set that also contains NaCl, then keep the selection
        selectedKit.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer index ) {
                kitSelectionNode.getKit( index ).content.setSelected();
            }
        } );
    }
}