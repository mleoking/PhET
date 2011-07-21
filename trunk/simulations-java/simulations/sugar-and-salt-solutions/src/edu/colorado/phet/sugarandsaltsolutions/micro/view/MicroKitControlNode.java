// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.*;

/**
 * Control node that allows the user to choose from different kits, which each have different combinations of solutes
 *
 * @author Sam Reid
 */
public class MicroKitControlNode extends PNode {
    public final KitSelectionNode kitSelectionNode;

    public MicroKitControlNode( final Property<Integer> selectedKit, final Property<DispenserType> dispenserType ) {
        addChild( kitSelectionNode = new KitSelectionNode( selectedKit,
                                                           new DispenserRadioButtonSet( dispenserType, new Item( Strings.SALT, SALT ), new Item( Strings.SUGAR, SUGAR ) ),
                                                           new DispenserRadioButtonSet( dispenserType, new Item( Strings.SALT, SALT ), new Item( Strings.CALCIUM_CHLORIDE, CALCIUM_CHLORIDE ) ),
                                                           new DispenserRadioButtonSet( dispenserType, new Item( Strings.SALT, SALT ), new Item( Strings.SODIUM_NITRATE, SODIUM_NITRATE ) ),
                                                           new DispenserRadioButtonSet( dispenserType, new Item( Strings.SUCROSE, SUGAR ), new Item( Strings.ETHANOL, ETHANOL ) )
        ) );
    }
}
