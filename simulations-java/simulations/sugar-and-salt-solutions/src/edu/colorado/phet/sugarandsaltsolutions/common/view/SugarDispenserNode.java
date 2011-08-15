// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.*;

/**
 * Sugar dispenser which can be rotated to pour out an endless supply of sugar.
 *
 * @author Sam Reid
 */
public class SugarDispenserNode<T extends SugarAndSaltSolutionModel> extends DispenserNode<T, SugarDispenser<T>> {

    private static final BufferedImage openFull = multiScaleToHeight( SUGAR_OPEN, 250 );
    private static final BufferedImage closedFull = multiScaleToHeight( SUGAR_CLOSED, 250 );

    private static final BufferedImage openEmpty = multiScaleToHeight( SUGAR_EMPTY_OPEN, 250 );
    private static final BufferedImage closedEmpty = multiScaleToHeight( SUGAR_EMPTY_CLOSED, 250 );

    private static final BufferedImage openMicro = multiScaleToHeight( SUGAR_MICRO_OPEN, 250 );
    private static final BufferedImage closedMicro = multiScaleToHeight( SUGAR_MICRO_CLOSED, 250 );

    public SugarDispenserNode( final ModelViewTransform transform, final SugarDispenser<T> model, double beakerHeight,

                               //This flag indicates whether it is the micro or macro tab since different images are used depending on the tab
                               final boolean micro ) {
        super( transform, model, beakerHeight );

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
                boolean open = model.open.get();
                boolean allowed = model.moreAllowed.get();
                imageNode.setImage(
                        micro ? ( open ?
                                  openMicro :
                                  closedMicro )
                              : ( open && allowed ? openFull :
                                  open && !allowed ? openEmpty :
                                  !open && allowed ? closedFull :
                                  closedEmpty )
                );
            }
        }.observe( model.open, model.moreAllowed );

        //Have to update the transform once after the image size changes (since it goes from null to non-null) in the auto-callback above
        updateTransform();
    }
}