// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.MinimizedConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.umd.cs.piccolo.PNode;

/**
 * Control that allows the user to expand/collapse the concentration bar chart, also contains said bar chart.
 *
 * @author Sam Reid
 */
public class ExpandableConcentrationBarChartNode extends PNode {

    //The node for the expanded bar chart
    protected MicroConcentrationBarChart microConcentrationBarChart;

    public ExpandableConcentrationBarChartNode( final Property<Boolean> showConcentrationBarChart,
                                                SettableProperty<Boolean> showConcentrationValues,
                                                BarItem... bars ) {

        //The bar chart itself (when toggled to be visible)
        addChild( microConcentrationBarChart = new MicroConcentrationBarChart( showConcentrationBarChart, showConcentrationValues, bars ) );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        addChild( new MinimizedConcentrationBarChart( showConcentrationBarChart ) );
    }

    //Clear the previous bars and display the specified bars
    public void setBars( BarItem... bars ) {
        microConcentrationBarChart.setBars( bars );
    }
}