// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Control that allows the user to expand/collapse the concentration bar chart, also contains said bar chart.
 *
 * @author Sam Reid
 */
public class ExpandableConcentrationBarChartNode extends PNode {
    public ExpandableConcentrationBarChartNode( final Property<Boolean> showConcentrationBarChart, ObservableProperty<Double> saltConcentration, ObservableProperty<Double> sugarConcentration, SettableProperty<Boolean> showConcentrationValues ) {

        //Button that maximizes the bar chart
        PImage maximizeButton = new PImage( PhetCommonResources.getMaximizeButtonImage() ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    showConcentrationBarChart.set( true );
                }
            } );
        }};

        //Layout maximize button next to "concentration" label
        HBox contentPane = new HBox(
                new PText( "Concentration" ) {{
                    setFont( MacroCanvas.TITLE_FONT );
                }},
                maximizeButton
        );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        ControlPanelNode showBarChartPanel = new ControlPanelNode( contentPane, WATER_COLOR, new BasicStroke( 1 ), Color.black, 3, 0, false ) {{
            showConcentrationBarChart.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean chartVisible ) {
                    setVisible( !chartVisible );
                }
            } );
        }};
        addChild( showBarChartPanel );

        //The bar chart itself (when toggled to be visible)
        ConcentrationBarChart concentrationBarChart = new ConcentrationBarChart( saltConcentration, sugarConcentration, showConcentrationValues, showConcentrationBarChart );
        addChild( concentrationBarChart );

        //Right align the expander button with the chart so the +/- buttons will be in the same location
        showBarChartPanel.setOffset( concentrationBarChart.getFullBounds().getMaxX() - showBarChartPanel.getFullBounds().getWidth(), 0 );
    }
}
