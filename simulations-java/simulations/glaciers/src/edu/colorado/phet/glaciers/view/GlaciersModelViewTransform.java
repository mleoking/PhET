/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import edu.colorado.phet.glaciers.GlaciersConstants;


public class GlaciersModelViewTransform extends ModelViewTransform {

    public GlaciersModelViewTransform() {
        super( GlaciersConstants.MVT_X_SCALE, GlaciersConstants.MVT_Y_SCALE, 
                GlaciersConstants.MVT_X_OFFSET, GlaciersConstants.MVT_Y_OFFSET, 
                GlaciersConstants.MVT_FLIP_SIGN_X, GlaciersConstants.MVT_FLIP_SIGN_Y );
    }
}
