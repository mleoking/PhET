// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.MomentaryButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of the light in the Beer's Law tab
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LightNode extends PNode {

    public LightNode( Light light ) {

        PImage imageNode = new PImage( Images.LIGHT );
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( UserComponents.lightOnOffButton, light.on );

        // scale button to fit image
        buttonNode.scale( 0.75 * imageNode.getFullBoundsReference().getHeight() / buttonNode.getFullBoundsReference().getHeight() );

        addChild( imageNode );
        addChild( buttonNode );

        // layout
        buttonNode.setOffset( imageNode.getFullBoundsReference().getMaxX() - ( buttonNode.getFullBoundsReference().getWidth() / 2 ) - 40,
                              imageNode.getFullBoundsReference().getCenterY() );
    }
}
