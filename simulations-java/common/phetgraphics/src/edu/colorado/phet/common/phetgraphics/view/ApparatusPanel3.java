// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetgraphics.view;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * This class is a workaround for sims that have a problem resizing, see #2860.
 * The strategy is to sample the canvas size by using ApparatusPanel2 and printing out the reference size set in the TransformManager.
 * Then that value is provided as width x height for this constructor, and that values is used for the reference size.
 *
 * @author Sam Reid
 */
public class ApparatusPanel3 extends ApparatusPanel2 {

    //Construct an ApparatusPanel3 with the specified reference size
    public ApparatusPanel3( IClock clock, int referenceWidth, int referenceHeight ) {
        super( clock );
        setReferenceSize( referenceWidth, referenceHeight );
    }

    //Makes the initial call to setReferenceSize do nothing so that the call to explicitly set the reference size will take effect
    @Override public void setReferenceSize() {
        //Intentional no-op
    }
}
