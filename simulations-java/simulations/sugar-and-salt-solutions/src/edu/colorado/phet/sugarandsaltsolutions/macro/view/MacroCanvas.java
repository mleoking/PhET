// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.sugarandsaltsolutions.common.SugarAndSaltSolutionsColorScheme;
import edu.colorado.phet.sugarandsaltsolutions.common.view.*;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Canvas for the introductory (macro) tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroCanvas extends SugarAndSaltSolutionsCanvas {
    public MacroCanvas( final MacroModel model, SugarAndSaltSolutionsColorScheme config ) {
        super( model, config );

        //Show the full water node at the correct color, then overlay a partially transparent one on top, so that some objects (such as the conductivity tester) will look submerged
        addChild( new SolutionNode( transform, model.solution, WATER_COLOR ) );

        //Node that shows things that get submerged such as the conductivity tester
        addChild( conductivityToolboxLayer );
        addChild( submergedInWaterNode );

        //Overlay node that renders as partially transparent in front of submerged objects, such as the conductivity tester.
        //When changing the transparency here make sure it looks good for precipitate as well as submerged probes
        addChild( new SolutionNode( transform, model.solution, new Color( WATER_COLOR.getRed(), WATER_COLOR.getGreen(), WATER_COLOR.getBlue(), 128 ) ) {{

            //Make it so the mouse events pass through the front water layer so it is still possible to pick and move the conductivity tester probes
            setPickable( false );
            setChildrenPickable( false );
        }} );

        //Readout the volume of the water in Liters, only visible if the user opted to show values (in the concentration bar chart)
        addChild( new VolumeIndicatorNode( transform, model.solution, model.showConcentrationValues, model.solidVolume, model.anySolutes ) );

        //Add a control that allows the user to remove solutes
        final PNode removeSolutesButton = new SeparateRemoveSaltSugarButtons( model );

        //Button should be inside the beaker at the bottom right so it doesn't collide with the leftmost tick marks
        removeSolutesButton.setOffset( transform.modelToViewX( model.beaker.getMaxX() ) - INSET - removeSolutesButton.getFullBounds().getWidth(),
                                       transform.modelToViewY( model.beaker.getY() ) - removeSolutesButton.getFullBounds().getHeight() - INSET );
        addChild( removeSolutesButton );

        ExpandableConcentrationBarChartNode concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.saltConcentration, model.sugarConcentration, model.showConcentrationValues ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Toolbox from which the conductivity tester can be dragged
        conductivityToolboxLayer.addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }} );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );

        //Add an evaporation rate slider below the beaker
        addChild( new EvaporationSlider2( model.evaporationRate ) {{
            Point2D point = MacroCanvas.this.transform.modelToView( 0, -model.beaker.getWallWidth() / 2 );
            setOffset( point.getX() - getFullBounds().getWidth() / 2, point.getY() + INSET );
        }} );

        //Show the precipitate as the sum of salt and sugar
        submergedInWaterNode.addChild( new PrecipitateNode( transform, model.salt.solidVolume.plus( model.sugar.solidVolume ), model.beaker ) );
    }
}