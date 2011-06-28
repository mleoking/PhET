// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class defines the box from which the user can drag weights that can
 * then be set on the balance.
 *
 * @author John Blanco
 */
public class WeightBoxNode extends PNode {

    private static final PDimension SIZE = new PDimension( 220, 300 );

    private final ModelViewTransform mvt;

    /**
     * Constructor.
     */
    public WeightBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        this.mvt = mvt;
        PNode backgroundBox = new PhetPPath( new RoundRectangle2D.Double( 0, 0, SIZE.getWidth(), SIZE.getHeight(), 20, 20 ),
                                             Color.WHITE,
                                             new BasicStroke( 3 ),
                                             Color.BLACK );
        addChild( backgroundBox );

        // Add the brick stacks to the weight box.
        BrickStackInWeightBoxNode oneBrickStack = new BrickStackInWeightBoxNode( 1, model, mvt, canvas ) {{
            setOffset( SIZE.width * 0.25, SIZE.height / 2 );
        }};
        backgroundBox.addChild( oneBrickStack );
        BrickStackInWeightBoxNode twoBrickStack = new BrickStackInWeightBoxNode( 2, model, mvt, canvas ) {{
            setOffset( SIZE.width * .75, SIZE.height / 2 );
        }};
        backgroundBox.addChild( twoBrickStack );
        BrickStackInWeightBoxNode threeBrickStack = new BrickStackInWeightBoxNode( 3, model, mvt, canvas ) {{
            setOffset( SIZE.width * 0.25, SIZE.height * 0.75 );
        }};
        backgroundBox.addChild( threeBrickStack );
        BrickStackInWeightBoxNode fourBrickStack = new BrickStackInWeightBoxNode( 4, model, mvt, canvas ) {{
            setOffset( SIZE.width * 0.75, SIZE.height * 0.75 );
        }};
        backgroundBox.addChild( fourBrickStack );
    }
}
