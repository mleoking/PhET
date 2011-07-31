// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.ConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.model.property.Property.property;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SUGAR;
import static java.awt.Color.white;

/**
 * This bar chart shows the concentrations for both salt and sugar (if any)
 *
 * @author Sam Reid
 */
public class SugarSaltBarChart extends ConcentrationBarChart {
    public SugarSaltBarChart( ObservableProperty<Double> saltConcentration, ObservableProperty<Double> sugarConcentration, final SettableProperty<Boolean> showValues, final SettableProperty<Boolean> visible, double scaleFactor ) {
        super( showValues, visible, 0 );

        //Convert from model units (Mols) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 160 * 1E-4 * scaleFactor;

        //Add a Salt concentration bar
        addChild( new Bar( property( white ), SALT, new None<PNode>(), saltConcentration, showValues, verticalAxisScale, false ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 - WIDTH, abscissaY );
        }} );

        //Add a Sugar concentration bar
        addChild( new Bar( property( white ), SUGAR, new None<PNode>(), sugarConcentration, showValues, verticalAxisScale, false ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 + WIDTH + 25, abscissaY );
        }} );
    }
}