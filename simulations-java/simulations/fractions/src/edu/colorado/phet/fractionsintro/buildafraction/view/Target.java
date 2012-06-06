package edu.colorado.phet.fractionsintro.buildafraction.view;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.awt.Color;

import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
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
    public static Target target( int numerator, int denominator, Color color, F<Fraction, FilledPattern> pattern ) {
        return new Target( new Fraction( numerator, denominator ), color, BuildAFractionModel.composite( pattern ).f( new Fraction( numerator, denominator ) ) );
    }
}