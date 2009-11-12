package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * Model for the "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModel {
    
    public GameModel() {
    }
    
    public void reset() {
    }
    
    public IntegerRange getCoefficientRange() {
        return GameDefaults.COEFFICIENT_RANGE;
    }
    
    public IntegerRange getQuantityRange() {
        return GameDefaults.QUANTITY_RANGE;
    }

}
