package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.data.List;
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
    public final List<FilledPattern> filledPattern;

    //Convenience for single pattern
    public static Target newTarget( Fraction fraction, Color color, FilledPattern pattern ) { return new Target( fraction, color, List.single( pattern ) ); }
}