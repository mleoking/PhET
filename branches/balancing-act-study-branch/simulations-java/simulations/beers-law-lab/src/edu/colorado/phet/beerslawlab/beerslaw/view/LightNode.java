// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.common.BLLResources.Images;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PiccoloPhetResources;
import edu.colorado.phet.common.piccolophet.nodes.ToggleButtonNode;
import edu.colorado.phet.common.piccolophet.simsharing.NonInteractiveEventHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of the light in the Beer's Law tab.
 * Origin is at the right center, where the light comes out of the "housing".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LightNode extends PNode {

    public LightNode( Light light, ModelViewTransform mvt ) {

        // light housing
        PImage lightHousingNode = new PImage( Images.LIGHT );
        lightHousingNode.addInputEventListener( new NonInteractiveEventHandler( UserComponents.lightHousing ) );
        addChild( lightHousingNode );
        lightHousingNode.setOffset( -lightHousingNode.getFullBoundsReference().getWidth(),
                                    -lightHousingNode.getFullBoundsReference().getHeight() / 2 );

        // button, scaled to fit image
        ToggleButtonNode buttonNode = new ToggleButtonNode( UserComponents.lightButton, light.on,
                                                            PiccoloPhetResources.getImage( "button_pressed.png" ),
                                                            PiccoloPhetResources.getImage( "button_unpressed.png" ) );
        addChild( buttonNode );
        buttonNode.scale( 0.65 * lightHousingNode.getFullBoundsReference().getHeight() / buttonNode.getFullBoundsReference().getHeight() );
        buttonNode.setOffset( lightHousingNode.getFullBoundsReference().getMaxX() - buttonNode.getFullBoundsReference().getWidth() - 40,
                              lightHousingNode.getFullBoundsReference().getCenterY() - ( buttonNode.getFullBoundsReference().getHeight() / 2 ) );

        setOffset( mvt.modelToView( light.location.toPoint2D() ) );
    }
}
