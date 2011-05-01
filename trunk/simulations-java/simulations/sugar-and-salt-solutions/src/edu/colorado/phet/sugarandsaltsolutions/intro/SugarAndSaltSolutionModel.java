// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel {
    public final double width = 0.7;//visible width in meters
    public final double height = 1.04;//visible height in meters

    public final Beaker beaker = new Beaker( width / 2, height / 2 );//The beaker into which you can add water, salt and sugar.
}
