// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.common.view.MinimizedConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getMaximizeButtonImage;

/**
 * Control that allows the user to expand/collapse the concentration bar chart, also contains said bar chart.
 *
 * @author Sam Reid
 */
public class ExpandableConcentrationBarChartNode extends PNode {
    public ExpandableConcentrationBarChartNode( final Property<Boolean> showConcentrationBarChart, ObservableProperty<Double> saltConcentration, ObservableProperty<Double> sugarConcentration, SettableProperty<Boolean> showConcentrationValues, double scaleFactor ) {

        //Layout maximize button next to "concentration" label
        HBox contentPane = new HBox(
                //Title for the control
                new PText( SugarAndSaltSolutionsResources.CONCENTRATION ) {{
                    setFont( MacroCanvas.TITLE_FONT );
                }},
                //Button that maximizes the bar chart
                new PImage( getMaximizeButtonImage() ) {{
                    addInputEventListener( new CursorHandler() );
                    addInputEventListener( new PBasicInputEventHandler() {
                        @Override public void mousePressed( PInputEvent event ) {
                            showConcentrationBarChart.set( true );
                        }
                    } );
                }}
        );

        //The bar chart itself (when toggled to be visible)
        addChild( new ConcentrationBarChart( saltConcentration, sugarConcentration, showConcentrationValues, showConcentrationBarChart, scaleFactor ) );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        addChild( new MinimizedConcentrationBarChart( showConcentrationBarChart ) );

//        addChild( new ControlPanelNode( contentPane, WATER_COLOR, new BasicStroke( 1 ), Color.black, 3, 0, false ) {
//            {
//                showConcentrationBarChart.addObserver( new VoidFunction1<Boolean>() {
//                    public void apply( Boolean chartVisible ) {
//                        setVisible( !chartVisible );
//                    }
//                } );
//                //Right align the expander button with the chart so the +/- buttons will be in the same location
//                setOffset( concentrationBarChart.getFullBounds().getMaxX() - getFullBounds().getWidth(), 0 );
//            }
//
//            @Override protected PBounds getControlPanelBounds( PNode content ) {
//                return new PBounds( super.getControlPanelBounds( content ).getX(), super.getControlPanelBounds( content ).getY(),
//                                    concentrationBarChart.getFullBounds().getWidth(), concentrationBarChart.getFullBounds().getHeight() );
//            }
//        } );
    }
}