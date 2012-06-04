package edu.colorado.phet.forcesandmotionbasics;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class DefaultForcesAndMotionBasicsCanvas extends AbstractForcesAndMotionBasicsCanvas {
    public DefaultForcesAndMotionBasicsCanvas() {
        final ModelViewTransform transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() * 0.8 ), 100 );
        addChild( new OutsideBackgroundNode( transform, 10, 1 ) );
        addChild( new PImage( Images.MYSTERY_BOX ) {{
            final Point2D point = transform.modelToView( 0, 0 );
            setOffset( point );
            translate( -getFullBounds().getWidth() / 2, -getFullBounds().getHeight() );
        }} );
        addChild( new PImage( Images.CLOUD1 ) );
    }
}