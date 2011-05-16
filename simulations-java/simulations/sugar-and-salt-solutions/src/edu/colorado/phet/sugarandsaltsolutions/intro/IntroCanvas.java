// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.intro;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsConfig;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConductivityTesterToolboxNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends SugarAndSaltSolutionsCanvas {
    public IntroCanvas( final IntroModel model, SugarAndSaltSolutionsConfig config ) {
        super( model, model.anySolutes, config );

        //Button that maximizes the bar chart
        PImage maximizeButton = new PImage( PhetCommonResources.getMaximizeButtonImage() ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    model.showConcentrationBarChart.set( true );
                }
            } );
        }};
        //Layout maximize button next to "concentration" label
        HBox contentPane = new HBox(
                new PText( "Concentration" ) {{
                    setFont( CONTROL_FONT );
                }},
                maximizeButton
        );

        //Panel that says "concentration" and has a "+" button to expand the concentration bar chart
        ControlPanelNode showBarChartPanel = new ControlPanelNode( contentPane, WATER_COLOR, new BasicStroke( 1 ), Color.black ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
            model.showConcentrationBarChart.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean chartVisible ) {
                    setVisible( !chartVisible );
                }
            } );
        }};
        behindShakerNode.addChild( showBarChartPanel );

        //The bar chart itself (when toggled to be visible)
        ConcentrationBarChart concentrationBarChart = new ConcentrationBarChart( model.saltConcentration, model.sugarConcentration, model.showConcentrationValues, model.showConcentrationBarChart ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Toolbox from which the conductivity tester can be dragged
        addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }} );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );
    }
}
