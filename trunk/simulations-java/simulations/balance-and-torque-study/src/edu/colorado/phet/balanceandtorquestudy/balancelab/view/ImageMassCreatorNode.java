// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorquestudy.common.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * A node that can be used to add an image-based mass element to the model by
 * clicking on it.
 *
 * @author John Blanco
 */
public abstract class ImageMassCreatorNode extends MassCreatorNode {
    protected final ImageMass prototypeImageMass;

    public ImageMassCreatorNode( final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas, ImageMass prototypeImageMass, boolean showMassLabel ) {
        super( model, mvt, canvas, prototypeImageMass.getMass(), showMassLabel );
        this.prototypeImageMass = prototypeImageMass;
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        Mass imageMassModelElement = createImageMassInstance();
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
    protected Mass createImageMassInstance() {
        return prototypeImageMass.createCopy();
    }
}
