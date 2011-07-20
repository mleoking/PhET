// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.MinimizedConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;

/**
 * Control that allows the user to expand/collapse the concentration bar chart, also contains said bar chart.
 *
 * @author Sam Reid
 */
public class ExpandableConcentrationBarChartNode extends PNode {
    public ExpandableConcentrationBarChartNode( final Property<Boolean> showConcentrationBarChart, ObservableProperty<Double> saltConcentration, ObservableProperty<Double> sugarConcentration, SettableProperty<Boolean> showConcentrationValues, double scaleFactor ) {

        //The bar chart itself (when toggled to be visible)
        addChild( new SugarSaltBarChart( saltConcentration, sugarConcentration, showConcentrationValues, showConcentrationBarChart, scaleFactor ) );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        addChild( new MinimizedConcentrationBarChart( showConcentrationBarChart ) );
    }
}