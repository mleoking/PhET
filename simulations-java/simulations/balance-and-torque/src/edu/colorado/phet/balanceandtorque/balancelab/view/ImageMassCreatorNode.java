// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.common.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * A node that can be used to add an image-based mass element to the model by
 * clicking on it.
 *
 * @author John Blanco
 */
public abstract class ImageMassCreatorNode extends MassCreatorNode {
    public ImageMassCreatorNode( final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
    }

    public ImageMassCreatorNode( final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas, double mass ) {
        super( model, mvt, canvas, mass );
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
