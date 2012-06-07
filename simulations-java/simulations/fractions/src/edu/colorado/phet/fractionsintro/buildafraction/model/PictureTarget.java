package edu.colorado.phet.fractionsintro.buildafraction.model;

import lombok.Data;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * Target the user tries to create when creating pictures to match a given numeric representation.
 * Not sure this warrants a class, but created for symmetry with NumberTarget and in case more information is added.
 *
 * @author Sam Reid
 */
public @Data class PictureTarget {
    public final Fraction fraction;
}