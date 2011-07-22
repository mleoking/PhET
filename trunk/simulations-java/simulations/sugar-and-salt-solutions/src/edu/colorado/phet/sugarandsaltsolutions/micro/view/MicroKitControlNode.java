// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.view.WhiteControlPanelNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode.createTitle;

/**
 * Control node that allows the user to choose from different kits, which each have different combinations of solutes
 *
 * @author Sam Reid
 */
public class MicroKitControlNode extends PNode {
    public final KitSelectionNode<DispenserRadioButtonSet> kitSelectionNode;
    public final WhiteControlPanelNode soluteControlPanelNode;

    public MicroKitControlNode( final Property<Integer> selectedKit, final Property<DispenserType> dispenserType ) {

        //Show the radio buttons on two lines to show scientific name and molecular formula to save horizontal space
        String sodiumChlorideString = "<html>Sodium Chloride<br>NaCl</html>";
        String calciumChlorideString = "<html>Calcium Chloride<br>CaCl<sub>2</sub></html>";
        String sodiumNitrateString = "<html>Sodium Nitrate<br>NaNO<sub>3</sub></html>";
        String sucroseString = "<html>Sucrose<br>C<sub>12</sub>H<sub>22</sub>O<sub>11</sub></html>";
        String ethanolString = "<html>Ethanol<br>C<sub>2</sub>H<sub>5</sub>OH</html>";
        kitSelectionNode = new KitSelectionNode<DispenserRadioButtonSet>( selectedKit,
                                                                          new DispenserRadioButtonSet( dispenserType, new Item( Strings.SALT, SALT ), new Item( Strings.SUGAR, SUGAR ) ),
                                                                          new DispenserRadioButtonSet( dispenserType, new Item( sodiumChlorideString, SALT ), new Item( calciumChlorideString, CALCIUM_CHLORIDE ) ),
                                                                          new DispenserRadioButtonSet( dispenserType, new Item( sodiumChlorideString, SALT ), new Item( sodiumNitrateString, SODIUM_NITRATE ) ),
                                                                          new DispenserRadioButtonSet( dispenserType, new Item( sucroseString, SUGAR ), new Item( ethanolString, ETHANOL ) )
        );
        soluteControlPanelNode = new WhiteControlPanelNode( new VBox(

                //Use a negative spacing to bring the arrow buttons up next to the title
                -ArrowButton.ARROW_HEIGHT,
                createTitle(),
                kitSelectionNode
        ) );
        addChild( soluteControlPanelNode );

        //When switching to a new kit, switch to a dispenser that is in the set (if not already selecting it).  If switching from a set that contains NaCl to a new set that also contains NaCl, then keep the selection
        selectedKit.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer index ) {
                kitSelectionNode.getKit( index ).setSelected();
            }
        } );
    }
}
