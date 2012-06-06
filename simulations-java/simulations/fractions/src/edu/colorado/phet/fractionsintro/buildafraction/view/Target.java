package edu.colorado.phet.fractionsintro.buildafraction.view;

import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;

/**
 * @author Sam Reid
 */
public @Data class Target {
    public final Fraction fraction;
    public final Color color;
    public final FilledPattern filledPattern;
}