// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel {
    public final double width = 1.04;//visible width in meters
    public final double height = 0.7;//visible height in meters

    //Center the beaker's base at x=0 and have it go halfway up the screen
    public final double beakerWidth = width * 0.6;
    public final double beakerX = -beakerWidth / 2;
    public final double beakerHeight = height * 0.5;

    public final Beaker beaker = new Beaker( beakerX, 0, beakerWidth, beakerHeight );//The beaker into which you can add water, salt and sugar.
}
