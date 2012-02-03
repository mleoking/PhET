// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.common.view.MomentaryButtonNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of the light in the Beer's Law tab
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LightNode extends PNode {

    public LightNode( Light light ) {

        // light housing
        PImage lightHousingNode = new PImage( Images.LIGHT );
        lightHousingNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.lightHousing ) );
        addChild( lightHousingNode );

        // button, scaled to fit image
        MomentaryButtonNode buttonNode = new MomentaryButtonNode( UserComponents.lightOnOffButton, light.on );
        addChild( buttonNode );
        buttonNode.scale( 0.75 * lightHousingNode.getFullBoundsReference().getHeight() / buttonNode.getFullBoundsReference().getHeight() );
        buttonNode.setOffset( lightHousingNode.getFullBoundsReference().getMaxX() - ( buttonNode.getFullBoundsReference().getWidth() / 2 ) - 40,
                              lightHousingNode.getFullBoundsReference().getCenterY() );
    }
}
