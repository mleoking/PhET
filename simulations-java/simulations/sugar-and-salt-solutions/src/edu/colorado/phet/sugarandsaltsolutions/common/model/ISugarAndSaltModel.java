// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * TODO: remove this interface, clients should just use MacroModel
 *
 * @author Sam Reid
 */
public interface ISugarAndSaltModel {
    //True if there is any salt that can be removed from the model.  This includes salt falling from the shaker or dissolved in solution
    ObservableProperty<Boolean> isAnySaltToRemove();

    //@see isAnySaltToRemove
    ObservableProperty<Boolean> isAnySugarToRemove();

    void removeSalt();

    void removeSugar();
}