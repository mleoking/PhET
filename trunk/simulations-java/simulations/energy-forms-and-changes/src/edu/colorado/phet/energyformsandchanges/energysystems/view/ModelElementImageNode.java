// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.ModelElementImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Class that represents an image-based model element, or a piece thereof, in
 * the view.
 *
 * @author John Blanco
 */
public class ModelElementImageNode extends PNode {
    public ModelElementImageNode( final ModelElementImage modelElementImage, final ModelViewTransform mvt ) {
        addChild( new PImage( modelElementImage.getImage() ) {{
            // TODO: Image used as their default size, i.e. no scaling.  Will this work long term?
            Point2D centerToCenterOffsetInView = mvt.modelToViewDelta( modelElementImage.getCenterToCenterOffset() ).toPoint2D();
            centerFullBoundsOnPoint( centerToCenterOffsetInView.getX(), centerToCenterOffsetInView.getY() );
        }} );
    }
}
