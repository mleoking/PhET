/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

/**
 * Model of a weak acid solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class WeakAcid extends Solution {
    
    public WeakAcid() {
        super( ProtoConstants.WEAK_ACID_CONCENTRATION_RANGE.getDefault(), ProtoConstants.WEAK_ACID_STRENGTH_RANGE.getDefault() );
    }
    
    public WeakAcid( double concentration, double strength ) {
        super( concentration, strength );
    }
}
