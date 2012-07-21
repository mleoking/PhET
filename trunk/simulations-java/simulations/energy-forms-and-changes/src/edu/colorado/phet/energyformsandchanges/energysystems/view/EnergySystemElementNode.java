// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemElement;
import edu.colorado.phet.energyformsandchanges.energysystems.model.ModelElementImage;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for PNodes that represent energy system elements in the view.
 * At this level of the class hierarchy, images are added.  Other functionality
 * that is more specific to the particular energy system should be added in
 * subclasses.
 *
 * @author John Blanco
 */
public class EnergySystemElementNode extends PNode {

    // Debug flag, adds small node that depicts center point of this node.
    private static final boolean SHOW_CENTER = false;

    public EnergySystemElementNode( EnergySystemElement energySystemElement, final ModelViewTransform mvt ) {
        for ( ModelElementImage imageInfo : energySystemElement.getImageList() ) {
            PImage imageNode = new PImage( imageInfo.getImage() );
            double widthInView = mvt.modelToViewDeltaX( imageInfo.getWidth() );
            imageNode.setScale( widthInView / imageNode.getFullBoundsReference().width );
            Point2D centerToCenterOffsetInView = mvt.modelToViewDelta( imageInfo.getCenterToCenterOffset() ).toPoint2D();
            imageNode.centerFullBoundsOnPoint( centerToCenterOffsetInView.getX(), centerToCenterOffsetInView.getY() );
            addChild( imageNode );
        }

        // Monitor the model element position and update this node's position accordingly.
        energySystemElement.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );

        // Center point node, used for debugging.
        if ( SHOW_CENTER ) {
            addChild( new PhetPPath( new Ellipse2D.Double( -4, -4, 8, 8 ), Color.PINK, new BasicStroke( 1 ), Color.BLACK ) );
        }
    }
}
