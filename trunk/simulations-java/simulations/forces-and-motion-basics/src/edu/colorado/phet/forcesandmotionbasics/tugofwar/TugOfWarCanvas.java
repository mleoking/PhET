package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createIdentity;

/**
 * @author Sam Reid
 */
public class TugOfWarCanvas extends AbstractForcesAndMotionBasicsCanvas {
    public TugOfWarCanvas() {

        //use view coordinates since nothing compex happening in model coordinates.

        //for a canvas height of 710, the ground is at 452 down from the top
        final int width = 10000;

        //Reverse bottom and top because using view coordinates
        addChild( new SkyNode( createIdentity(), new Rectangle2D.Double( -width / 2, -width / 2 + 452, width, width / 2 ), 452, SkyNode.DEFAULT_TOP_COLOR, SkyNode.DEFAULT_BOTTOM_COLOR ) );

        final PImage grassNode = new PImage( Images.GRASS );
        grassNode.setOffset( -2, 452 - 2 );
        addChild( grassNode );
    }
}