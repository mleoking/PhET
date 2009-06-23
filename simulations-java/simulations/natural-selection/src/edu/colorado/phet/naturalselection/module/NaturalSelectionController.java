/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.view.NaturalSelectionCanvas;

/**
 * Wires up parts of the control panel, model and views
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionController {

    /**
     * Constructor
     *
     * @param model        The model
     * @param canvas       The main simulation canvas
     * @param controlPanel The control panel
     * @param module       The module itself
     */
    public NaturalSelectionController( final NaturalSelectionModel model, final NaturalSelectionCanvas canvas, final NaturalSelectionControlPanel controlPanel, final NaturalSelectionModule module ) {

        // if a new bunny is created in the model, we should create a sprite for it. (Also bushes and trees change
        // depending on environment and selection factors)
        model.addListener( canvas.landscapeNode );

        // if the environment changes, we need to modify the background
        model.addListener( canvas.backgroundNode );

        //----------------------------------------------------------------------------
        // Control panel buttons
        //----------------------------------------------------------------------------

        controlPanel.climatePanel.arcticButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_ARCTIC );
            }
        } );

        controlPanel.climatePanel.equatorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_EQUATOR );
            }
        } );

        controlPanel.noneButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_NONE );
            }
        } );

        controlPanel.foodButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_FOOD );
            }
        } );

        controlPanel.wolvesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_WOLVES );
            }
        } );

        controlPanel.resetAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.reset();
            }
        } );

        controlPanel.showGenerationChartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.showGenerationChart();
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( ColorGene.getInstance(), mutatable );
            }
        } );

        TailGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( TailGene.getInstance(), mutatable );
            }
        } );

        TeethGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {

            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( TeethGene.getInstance(), mutatable );
            }
        } );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_GAME_OVER ) {
                    module.showGameOver();
                }
            }
        } );

        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_BUNNIES_TAKE_OVER ) {
                    module.showBunniesTakeOver();
                }
            }
        } );

        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {

            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {
                if ( primary + secondary == 0 ) {
                    model.endGame();
                }
                else if ( primary + secondary > NaturalSelectionModel.MAX_POPULATION ) {
                    model.bunniesTakeOver();
                }
            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {

            }
        } );

        controlPanel.showGenerationChartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.showGenerationChart();
            }
        } );
    }

}
