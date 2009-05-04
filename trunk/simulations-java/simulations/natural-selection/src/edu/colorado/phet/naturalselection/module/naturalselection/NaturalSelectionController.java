/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.naturalselection.control.NaturalSelectionControlPanel;

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
        model.addListener( canvas.bunnies );

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

        controlPanel.generationCanvas.statsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                module.showGenerationChart();
            }
        } );
    }

}
