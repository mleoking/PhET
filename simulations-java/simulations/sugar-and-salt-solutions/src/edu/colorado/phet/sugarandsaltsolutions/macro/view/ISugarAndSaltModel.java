// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;

/**
 * @author Sam Reid
 */
public interface ISugarAndSaltModel {
    DoubleProperty getSaltMoles();

    DoubleProperty getSugarMoles();

    void removeSalt();

    void removeSugar();
}
