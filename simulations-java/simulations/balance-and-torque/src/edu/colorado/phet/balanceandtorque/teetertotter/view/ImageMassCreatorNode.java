// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author John Blanco
 */
public abstract class ImageMassCreatorNode extends ModelElementCreatorNode {
    public ImageMassCreatorNode( final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }


    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        ImageMass imageMassModelElement = createImageMassInstance();
        imageMassModelElement.setPosition( position );
        imageMassModelElement.setAnimationDestination( position );
        imageMassModelElement.userControlled.set( true );
        model.addMass( imageMassModelElement );
        return imageMassModelElement;
    }

    /**
     * Create an instance of the image mass that corresponds to this creator
     * node.  Overridden by subclasses to create the appropriate type.
     *
     * @return
     */
    protected abstract ImageMass createImageMassInstance();
}
