// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;

/**
 * Bar chart that shows Na+ and Cl- concentrations for table salt.
 *
 * @author Sam Reid
 */
public class SodiumChlorideConcentrationBarChart extends ConcentrationBarChart {

    public SodiumChlorideConcentrationBarChart( BarItem sodium,
                                                BarItem chloride,
                                                SettableProperty<Boolean> showValues,
                                                Property<Boolean> visible ) {
        super( showValues, visible );

        //Convert from model units (mol/L) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 4;

        //Add a Sodium concentration bar
        addChild( new Bar( sodium.color, sodium.caption, sodium.icon, sodium.concentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 - WIDTH, abscissaY );
        }} );

        //Add a Chloride concentration bar
        addChild( new Bar( chloride.color, chloride.caption, chloride.icon, chloride.concentration, showValues, verticalAxisScale ) {{
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 + WIDTH + 25, abscissaY );
        }} );
    }
}