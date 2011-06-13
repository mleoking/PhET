// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * @author Sam Reid
 */
public interface ISugarAndSaltModel {
    ObservableProperty<Boolean> isAnySaltInSolution();

    ObservableProperty<Boolean> isAnySugarInSolution();

    void removeSalt();

    void removeSugar();
}
