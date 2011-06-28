// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * This class defines the box from which the user can drag weights that can
 * then be set on the balance.
 *
 * @author John Blanco
 */
public class WeightBoxNode extends ControlPanelNode {

    private static final PDimension SIZE = new PDimension( 220, 300 );
    private static final PhetFont BUTTON_FONT = new PhetFont( 16 );
    private static final Color BUTTON_COLOR = new Color( 50, 205, 50 );
    private final ModelViewTransform mvt;

    /**
     * Constructor.
     */
    public WeightBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( new VBox(
                new PText( "Weights" ) {{
                    setFont( new PhetFont( 20 ) );
                }},
                new HBox(
                        new TextButtonNode( "Previous", BUTTON_FONT, BUTTON_COLOR ),
                        new TextButtonNode( "Next", BUTTON_FONT, BUTTON_COLOR )
                ),
                new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
                    // Add the brick stacks to the weight box.
                    BrickStackInWeightBoxNode oneBrickStack = new BrickStackInWeightBoxNode( 1, model, mvt, canvas ) {{
                        setOffset( SIZE.width * 0.25, SIZE.height / 2 );
                    }};
                    addChild( oneBrickStack );
                    BrickStackInWeightBoxNode twoBrickStack = new BrickStackInWeightBoxNode( 2, model, mvt, canvas ) {{
                        setOffset( SIZE.width * .75, SIZE.height / 2 );
                    }};
                    addChild( twoBrickStack );
                    BrickStackInWeightBoxNode threeBrickStack = new BrickStackInWeightBoxNode( 3, model, mvt, canvas ) {{
                        setOffset( SIZE.width * 0.25, SIZE.height * 0.75 );
                    }};
                    addChild( threeBrickStack );
                    BrickStackInWeightBoxNode fourBrickStack = new BrickStackInWeightBoxNode( 4, model, mvt, canvas ) {{
                        setOffset( SIZE.width * 0.75, SIZE.height * 0.75 );
                    }};
                    addChild( fourBrickStack );
                }}
        ) );
        this.mvt = mvt;
    }
}