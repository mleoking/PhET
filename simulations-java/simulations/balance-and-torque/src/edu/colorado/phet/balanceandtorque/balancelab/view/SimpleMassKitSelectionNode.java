// Copyright 2002-2013, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.GridLayout;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources;
import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.massKitSelector;

/**
 * A simplified version of the MassKitSelectionNode that only allows the user
 * to choose between a very limited set of masses.  This was created explicitly
 * for the Stanford study.
 *
 * @author John Blanco
 */
public class SimpleMassKitSelectionNode extends KitSelectionNode<PNode> {

    static class TitleNode extends PText {
        TitleNode( String text ) {
            super( text );
            setFont( new PhetFont( 16 ) );
        }
    }

    public SimpleMassKitSelectionNode( final Property<Integer> selectedKit, final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( massKitSelector,
               selectedKit,
//               new Kit<PNode>( new TitleNode( BalanceAndTorqueResources.Strings.BRICKS ),
//                               new HBox(
//                                       new BrickStackCreatorNode( 1, model, mvt, canvas ),
//                                       new BrickStackCreatorNode( 2, model, mvt, canvas )
//                               )
//
//               ),

               new Kit<PNode>( new TitleNode( BalanceAndTorqueResources.Strings.BRICKS ),
                               new SwingLayoutNode( new GridLayout( 1, 2, 30, 20 ) ) {{
                                   addChild( new BrickStackCreatorNode( 1, model, mvt, canvas ) );
                                   addChild( new BrickStackCreatorNode( 2, model, mvt, canvas ) );
                               }}
               )
        );
    }

    public void reset() {
        selectedKit.reset();
    }
}
