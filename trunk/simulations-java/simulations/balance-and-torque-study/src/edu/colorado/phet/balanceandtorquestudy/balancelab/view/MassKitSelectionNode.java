// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.balancelab.view;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorquestudy.common.model.BalanceModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.massKitSelector;

/**
 * A node which allows the user to scroll through the various mass kits and
 * select individual masses for putting on the balance.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MassKitSelectionNode extends KitSelectionNode<PNode> {

    static class TitleNode extends PText {
        TitleNode( String text ) {
            super( text );
            setFont( new PhetFont( 16 ) );
        }
    }

    public MassKitSelectionNode( final Property<Integer> selectedKit, final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( massKitSelector,
               selectedKit,
               new Kit<PNode>( new TitleNode( BalanceAndTorqueResources.Strings.BRICKS ),
                               new VBox( 0,
                                         new HBox( 20,
                                                   new BrickStackCreatorNode( 1, model, mvt, canvas ),
                                                   new BrickStackCreatorNode( 2, model, mvt, canvas )
                                         ),
                                         new HBox( 20,
                                                   new BrickStackCreatorNode( 3, model, mvt, canvas ),
                                                   new BrickStackCreatorNode( 4, model, mvt, canvas )
                                         ),
                                         new HBox( 20,
                                                   new BrickStackCreatorNode( 5, model, mvt, canvas ),
                                                   new BrickStackCreatorNode( 6, model, mvt, canvas )
                                         )
                               )
               )

               // People and mystery masses removed for the Stanford study.

//               new Kit<PNode>( new TitleNode( MessageFormat.format( BalanceAndTorqueResources.Strings.PATTERN_0_VALUE_1_UNITS, BalanceAndTorqueResources.Strings.PEOPLE, "1" ) ),
//                               new HBox(
//                                       new BoyCreatorNode( model, mvt, canvas ),
//                                       new AdultMaleHumanCreatorNode( model, mvt, canvas )
//                               )
//               ),
//               new Kit<PNode>( new TitleNode( MessageFormat.format( BalanceAndTorqueResources.Strings.PATTERN_0_VALUE_1_UNITS, BalanceAndTorqueResources.Strings.PEOPLE, "2" ) ),
//                               new HBox(
//                                       new YoungGirlCreatorNode( model, mvt, canvas ),
//                                       new AdultFemaleHumanCreatorNode( model, mvt, canvas )
//                               )
//               )

//               new Kit<PNode>( new TitleNode( MessageFormat.format( BalanceAndTorqueResources.Strings.PATTERN_0_VALUE_1_UNITS, BalanceAndTorqueResources.Strings.MYSTERY_OBJECTS, "1" ) ),
//                               new VBox(
//                                       new HBox( 20, new MysteryMassCreatorNode( 0, model, mvt, canvas ),
//                                                 new MysteryMassCreatorNode( 1, model, mvt, canvas ) ),
//                                       new HBox( 20, new MysteryMassCreatorNode( 2, model, mvt, canvas ),
//                                                 new MysteryMassCreatorNode( 3, model, mvt, canvas ) )
//                               )
//               )
//               new Kit<PNode>( new TitleNode( MessageFormat.format( BalanceAndTorqueResources.Strings.PATTERN_0_VALUE_1_UNITS, BalanceAndTorqueResources.Strings.MYSTERY_OBJECTS, "2" ) ),
//                               new VBox(
//                                       new HBox( 20, new MysteryMassCreatorNode( 4, model, mvt, canvas ),
//                                                 new MysteryMassCreatorNode( 5, model, mvt, canvas ) ),
//                                       new HBox( 20, new MysteryMassCreatorNode( 6, model, mvt, canvas ),
//                                                 new MysteryMassCreatorNode( 7, model, mvt, canvas ) )
//                               )
//               )
        );
    }

    public void reset() {
        selectedKit.reset();
    }
}
