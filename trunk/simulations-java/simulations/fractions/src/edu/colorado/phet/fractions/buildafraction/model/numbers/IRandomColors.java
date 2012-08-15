// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model.numbers;

import java.awt.Color;

/**
 * Way of obtaining different, randomized colors for the build a fraction: numbers targets.
 *
 * @author Sam Reid
 */
public interface IRandomColors {

    //Get the next random color
    Color next();
}