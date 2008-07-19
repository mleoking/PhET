/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;


public class GlaciersModelViewTransform extends ModelViewTransform {

    // ModelViewTransform (MVT) parameters
    private static final double MVT_X_SCALE = 0.062; // scale x by this amount when going from model to view
    private static final double MVT_Y_SCALE = 0.1; // scale y by this amount when going from model to view
    private static final double MVT_X_OFFSET = 0; // translate x by this amount when going from model to view
    private static final double MVT_Y_OFFSET = 0; // translate y by this amount when going from model to view
    private static final boolean MVT_FLIP_SIGN_X = false;
    private static final boolean MVT_FLIP_SIGN_Y = true;
    
    public GlaciersModelViewTransform() {
        super( MVT_X_SCALE, MVT_Y_SCALE, MVT_X_OFFSET, MVT_Y_OFFSET, MVT_FLIP_SIGN_X, MVT_FLIP_SIGN_Y );
    }
}
