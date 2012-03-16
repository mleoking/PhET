// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;
import edu.colorado.phet.naturalselection.dialog.PedigreeChartCanvas;
import edu.colorado.phet.naturalselection.model.*;
import edu.colorado.phet.naturalselection.view.LandscapeNode;
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

        controlPanel.getClimatePanel().getArcticButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_ARCTIC );
            }
        } );

        controlPanel.getClimatePanel().getEquatorButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setClimate( NaturalSelectionModel.CLIMATE_EQUATOR );
            }
        } );

        controlPanel.getSelectionPanel().getNoneButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_NONE );
            }
        } );

        controlPanel.getSelectionPanel().getFoodButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_FOOD );
            }
        } );

        controlPanel.getSelectionPanel().getWolvesButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                model.setSelectionFactor( NaturalSelectionModel.SELECTION_WOLVES );
            }
        } );

//        controlPanel.getResetAllButton().addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent actionEvent ) {
//                module.reset();
//            }
//        } );
        controlPanel.getResetAllButton().addResettable( module );

        // when a bunny is selected, inform the pedigree chart to display that bunny
        canvas.getLandscapeNode().addListener( new LandscapeNode.Listener() {
            public void onBunnySelected( Bunny bunny ) {
                controlPanel.getPedigreeChart().displayBunny( bunny );
            }
        } );

        // notify the main canvas of mutatable change events, so we can display the "mutation pending" notification
        GeneListener geneListener = new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {
            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {
            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
                canvas.handleMutationChange( gene, mutatable );
            }
        };
        ColorGene.getInstance().addListener( geneListener );
        TailGene.getInstance().addListener( geneListener );
        TeethGene.getInstance().addListener( geneListener );

        // detect when all of the bunnies die, and show the game over dialog
        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_GAME_OVER ) {
                    module.showGameOver();
                }
            }
        } );

        // detect when too many bunnies are alive, and show the bunnies take over dialog
        model.addListener( new NaturalSelectionModel.Listener() {
            public void onEvent( NaturalSelectionModel.Event event ) {
                if ( event.getType() == NaturalSelectionModel.Event.TYPE_BUNNIES_TAKE_OVER ) {
                    module.showBunniesTakeOver();
                }
            }
        } );

        // a slightly roundabout way of checking the bunny population limits when ANY bunny is created or dies
        ColorGene.getInstance().addListener( new GeneListener() {
            public void onChangeDominantAllele( Gene gene, boolean primary ) {
            }

            public void onChangeDistribution( Gene gene, int primary, int secondary ) {
                if ( primary + secondary == 0 ) {
                    model.endGame();
                }
            }

            public void onChangeMutatable( Gene gene, boolean mutatable ) {
            }
        } );

        //----------------------------------------------------------------------------
        // When a mutation button is pressed, make sure to enable the corresponding part of the gene panel
        //----------------------------------------------------------------------------

        controlPanel.getMutationPanel().getColorButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setColorEnabled( true );
            }
        } );

        controlPanel.getMutationPanel().getTailButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setTailEnabled( true );
            }
        } );

        controlPanel.getMutationPanel().getTeethButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getGenePanel().setTeethEnabled( true );
            }
        } );

        //----------------------------------------------------------------------------
        // Interactions between the switcher and detachable panels
        //----------------------------------------------------------------------------

        // on switch from pedigree to statistics panel
        controlPanel.getSwitcherPanel().getStatisticsRadioButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getDetachPanel().showStaticChild();

                // remove the selection from the current bunny if it is selected
                if ( Bunny.getSelectedBunny() != null ) {
                    Bunny.getSelectedBunny().setSelected( false );
                }
            }
        } );

        // on switch from statistics to pedigree chart
        controlPanel.getSwitcherPanel().getPedigreeRadioButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                controlPanel.getDetachPanel().showDetachableChild();

                // if necessary, cause the last selected bunny to be reselected
                PedigreeChartCanvas pedigreeChart = controlPanel.getPedigreeChart();
                Bunny lastBunny = pedigreeChart.getLastDisplayedBunny();
                if ( lastBunny != null ) {
                    if ( Bunny.getSelectedBunny() == null && lastBunny.isAlive() ) {
                        pedigreeChart.getLastDisplayedBunny().setSelected( true );
                    }
                }
            }
        } );

        // allow detach, reattach and closing events to be passed to the switcher panel
        controlPanel.getDetachPanel().addListener( controlPanel.getSwitcherPanel() );


        // attach generation progress to the progress bar
        model.getClock().addTimeListener( new NaturalSelectionClock.Listener() {
            public void onTick( ClockEvent event ) {
                controlPanel.getGenerationProgressPanel().setGenerationProgressPercent( model.getGenerationProgressPercent() );
            }
        } );

        // when time changes, inform the bunny statistics panel
        model.getClock().addTimeListener( new NaturalSelectionClock.Listener() {
            public void onTick( ClockEvent event ) {
                controlPanel.getBunnyStatsPanel().getBunnyStatsCanvas().onTick( model.getPopulation() );
            }
        } );

    }

}
