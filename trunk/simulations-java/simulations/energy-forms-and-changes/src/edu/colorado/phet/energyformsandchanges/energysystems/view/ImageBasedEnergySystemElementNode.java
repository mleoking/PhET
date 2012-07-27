// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemElement;
import edu.colorado.phet.energyformsandchanges.energysystems.model.ModelElementImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for PNodes that represent energy system elements in the view
 * that use images as part of their representation.
 *
 * @author John Blanco
 */
public abstract class ImageBasedEnergySystemElementNode extends PNode {

    private final ModelViewTransform mvt;

    public ImageBasedEnergySystemElementNode( EnergySystemElement element, final ModelViewTransform mvt ) {
        this.mvt = mvt;

        // Update the overall offset based on the model position.
        element.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }

    protected PImage addImageNode( ModelElementImage modelElementImage ) {
        PImage imageNode = new PImage( modelElementImage.getImage() );
        double widthInView = mvt.modelToViewDeltaX( modelElementImage.getWidth() );
        imageNode.setScale( widthInView / imageNode.getFullBoundsReference().width );
        Point2D centerToCenterOffsetInView = mvt.modelToViewDelta( modelElementImage.getCenterToCenterOffset() ).toPoint2D();
        imageNode.centerFullBoundsOnPoint( centerToCenterOffsetInView.getX(), centerToCenterOffsetInView.getY() );
        addChild( imageNode );
        return imageNode;
    }
}
