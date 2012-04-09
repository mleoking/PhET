// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.HeaterCoolerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Burner;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents the burner in the view.
 *
 * @author John Blanco
 */
public class BurnerNode extends PNode {

    private static final double perspectiveAngle = Math.PI / 4;
    private static final double edgeLength = 20; // In screen units, which are close to pixels.

    public BurnerNode( final Burner burner, final ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToViewRectangle( burner.getOutlineRect() ), new BasicStroke( 1 ), Color.RED ) );

        // Add the heater-cooler node to the center bottom of the model
        // representation.
        // TODO: i18n
        PNode heaterCoolerNode = new HeaterCoolerNode( burner.heatCoolLevel, "Heat", "Cool" ) {{
            setScale( mvt.modelToViewDeltaX( burner.getOutlineRect().getWidth() ) * 0.5 / getFullBoundsReference().width );
            Rectangle2D burnerViewRect = mvt.modelToView( burner.getOutlineRect() ).getBounds2D();
            setOffset( burnerViewRect.getX() + burnerViewRect.getWidth() / 2 - getFullBoundsReference().width / 2,
                       burnerViewRect.getMaxY() - getFullBoundsReference().height );
        }};
        addChild( heaterCoolerNode );


        // Create the faux-3D frame for the burner.
        Rectangle2D viewRect = mvt.modelToViewRectangle( burner.getOutlineRect() );
        ImmutableVector2D offsetVector = new ImmutableVector2D( 0, -edgeLength / 2 ).getRotatedInstance( perspectiveAngle );
        AffineTransform backTransform = AffineTransform.getTranslateInstance( offsetVector.getX(), offsetVector.getY() );
        Rectangle2D backFrame = backTransform.createTransformedShape( viewRect ).getBounds2D();
        addChild( new PhetPPath( backFrame, new BasicStroke( 1 ), Color.BLACK ) );
        AffineTransform frontTransform = AffineTransform.getTranslateInstance( -offsetVector.getX(), -offsetVector.getY() );
        Rectangle2D frontFrame = frontTransform.createTransformedShape( viewRect ).getBounds2D();
        addChild( new PhetPPath( frontFrame, new BasicStroke( 1 ), Color.BLACK ) );


    }
}
