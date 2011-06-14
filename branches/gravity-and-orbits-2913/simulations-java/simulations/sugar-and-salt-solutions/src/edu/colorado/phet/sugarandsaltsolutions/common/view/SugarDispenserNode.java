// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * Sugar dispenser which can be rotated to pour out an endless supply of sugar.
 *
 * @author Sam Reid
 */
public class SugarDispenserNode extends DispenserNode<SugarDispenser> {

    private static final BufferedImage openFull = multiScaleToHeight( RESOURCES.getImage( "sugar_open.png" ), 250 );
    private static final BufferedImage closedFull = multiScaleToHeight( RESOURCES.getImage( "sugar_closed.png" ), 250 );

    private static final BufferedImage openEmpty = multiScaleToHeight( RESOURCES.getImage( "sugar_empty_open.png" ), 250 );
    private static final BufferedImage closedEmpty = multiScaleToHeight( RESOURCES.getImage( "sugar_empty_closed.png" ), 250 );

    public SugarDispenserNode( final ModelViewTransform transform, final SugarDispenser model ) {
        super( transform, model );

        //Hide the sugar dispenser if it is not enabled (selected by the user)
        model.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                setVisible( enabled );
            }
        } );

        //Choose the image based on the angle.  If it is tipped sideways the opening should flip open.
        //Also update the image when the the dispenser opens/closes and empties/fills.
        new RichSimpleObserver() {
            @Override public void update() {
                imageNode.setImage(
                        model.open.get() && model.moreAllowed.get() ? openFull :
                        model.open.get() && !model.moreAllowed.get() ? openEmpty :
                        !model.open.get() && model.moreAllowed.get() ? closedFull :
                        closedEmpty
                );
            }
        }.observe( model.open, model.moreAllowed );

        //Have to update the transform once after the image size changes (since it goes from null to non-null) in the auto-callback above
        updateTransform();
    }
}