// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Shaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.*;

/**
 * Sugar dispenser which can be rotated to pour out sugar, used in macro and micro tab.
 *
 * @author Sam Reid
 */
public class SaltShakerNode<T extends SugarAndSaltSolutionModel> extends DispenserNode<T, Shaker<T>> {
    public SaltShakerNode( final ModelViewTransform transform, final Shaker<T> model, double beakerHeight,

                           //This flag indicates whether it is the micro or macro tab since different images are used depending on the tab
                           boolean micro ) {
        super( transform, model, beakerHeight );

        //Create images to use in each scenario
        final BufferedImage fullImage = multiScaleToHeight( micro ? SALT_MICRO : SALT_1, 200 );
        final BufferedImage emptyImage = multiScaleToHeight( micro ? SALT_MICRO : SALT_EMPTY, 200 );

        //Hide the sugar dispenser if it is not enabled (selected by the user)
        model.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                setVisible( enabled );
            }
        } );

        //Switch between the empty and full images based on whether the user is allowed to add more salt
        model.moreAllowed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean moreAllowed ) {
                imageNode.setImage( moreAllowed ? fullImage : emptyImage );
            }
        } );

        //Have to update the transform once after the image size changes (since it goes from null to non-null) in the auto-callback above
        updateTransform();
    }
}