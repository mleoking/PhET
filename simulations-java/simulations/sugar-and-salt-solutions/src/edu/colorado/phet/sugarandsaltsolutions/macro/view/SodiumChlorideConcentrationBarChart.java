// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM;
import static java.awt.Color.white;

/**
 * Bar chart that shows Na+ and Cl- concentrations for table salt.
 *
 * @author Sam Reid
 */
public class SodiumChlorideConcentrationBarChart extends ConcentrationBarChart {
    public SodiumChlorideConcentrationBarChart( ObservableProperty<Double> sodiumConcentration, ObservableProperty<Double> chlorideConcentration, SettableProperty<Boolean> showValues, Property<Boolean> visible, double scaleFactor ) {
        super( showValues, visible );

        //Convert from model units (Mols) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 4;

        //Add a Salt concentration bar
        addChild( new Bar( white, SODIUM, sodiumConcentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 - WIDTH, abscissaY );
        }} );

        //Add a Sugar concentration bar
        addChild( new Bar( white, CHLORIDE, chlorideConcentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 + WIDTH + 25, abscissaY );
        }} );
    }
}