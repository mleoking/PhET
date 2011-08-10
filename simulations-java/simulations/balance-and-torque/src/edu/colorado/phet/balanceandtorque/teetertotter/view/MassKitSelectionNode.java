// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.GridLayout;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.Kit;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * @author Sam Reid
 */
public class MassKitSelectionNode extends KitSelectionNode<PNode> {

    static class TitleNode extends PText {
        TitleNode( String text ) {
            super( text );
            setFont( new PhetFont( 16 ) );
        }
    }

    public MassKitSelectionNode( final Property<Integer> selectedKit, final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( selectedKit,
               new Kit<PNode>( new TitleNode( "Bricks" ),
                               new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
                                   addChild( new BrickStackCreatorNode( 1, model, mvt, canvas ) );
                                   addChild( new BrickStackCreatorNode( 2, model, mvt, canvas ) );
                                   addChild( new BrickStackCreatorNode( 3, model, mvt, canvas ) );
                                   addChild( new BrickStackCreatorNode( 4, model, mvt, canvas ) );
                               }}
               ),
               new Kit<PNode>( new TitleNode( "People" ),
                               new HBox(
                                       new AdolescentHumanCreatorNode( model, mvt, canvas ),
                                       new AdultMaleHumanCreatorNode( model, mvt, canvas )
                               )
               ),
               new Kit<PNode>( new TitleNode( "Mystery Objects" ),
                               new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
                                   addChild( new MysteryObjectCreatorNode( 0, model, mvt, canvas ) );
                                   addChild( new MysteryObjectCreatorNode( 1, model, mvt, canvas ) );
                                   addChild( new MysteryObjectCreatorNode( 2, model, mvt, canvas ) );
                                   addChild( new MysteryObjectCreatorNode( 3, model, mvt, canvas ) );
                               }}
               )
        );
    }
}
