// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.kit.KitSelectionNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
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

    public MassKitSelectionNode( final Property<Integer> selectedKit, final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( selectedKit,
               new VBox( new TitleNode( "Bricks" ),
                         new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
                             addChild( new BrickStackInMassBoxNode( 1, model, mvt, canvas ) );
                             addChild( new BrickStackInMassBoxNode( 2, model, mvt, canvas ) );
                             addChild( new BrickStackInMassBoxNode( 3, model, mvt, canvas ) );
                             addChild( new BrickStackInMassBoxNode( 4, model, mvt, canvas ) );
                         }}
               ),
               new VBox( new TitleNode( "People" ),
                         new SwingLayoutNode( new FlowLayout() ) {{
                             AdolescentHumanInMassBoxNode adolescentHumanInMassBoxNode = new AdolescentHumanInMassBoxNode( model, mvt, canvas );
                             addChild( adolescentHumanInMassBoxNode );
                             AdultMaleHumanInMassBoxNode adultMaleHumanInMassBoxNode = new AdultMaleHumanInMassBoxNode( model, mvt, canvas );
                             addChild( adultMaleHumanInMassBoxNode );
                         }}
               ),
               new VBox( new TitleNode( "Mystery Objects" ),
                         new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
                             addChild( new MysteryObjectInMassBoxNode( 0, model, mvt, canvas ) );
                             addChild( new MysteryObjectInMassBoxNode( 1, model, mvt, canvas ) );
                             addChild( new MysteryObjectInMassBoxNode( 2, model, mvt, canvas ) );
                             addChild( new MysteryObjectInMassBoxNode( 3, model, mvt, canvas ) );
                         }}
               )
        );
    }
}
