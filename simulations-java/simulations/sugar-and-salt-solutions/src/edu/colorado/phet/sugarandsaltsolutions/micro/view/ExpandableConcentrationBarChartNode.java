// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.MinimizedConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.SodiumChlorideConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;

/**
 * Control that allows the user to expand/collapse the concentration bar chart, also contains said bar chart.
 *
 * @author Sam Reid
 */
public class ExpandableConcentrationBarChartNode extends PNode {
    public ExpandableConcentrationBarChartNode( final Property<Boolean> showConcentrationBarChart,
                                                ObservableProperty<Double> sodiumConcentration,
                                                ObservableProperty<Double> calciumConcentration,
                                                SettableProperty<Boolean> showConcentrationValues,
                                                double scaleFactor ) {

        //The bar chart itself (when toggled to be visible)
        addChild( new SodiumChlorideConcentrationBarChart( sodiumConcentration, calciumConcentration, showConcentrationValues, showConcentrationBarChart, scaleFactor ) );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        addChild( new MinimizedConcentrationBarChart( showConcentrationBarChart ) );
    }
}