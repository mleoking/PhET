// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Images.GRAY_ARROW;

/**
 * Button for moving backwards through the kits
 *
 * @author Sam Reid
 */
public class BackButton extends ArrowButton {
    public BackButton() {
        super( flipX( GRAY_ARROW ) );
    }
}