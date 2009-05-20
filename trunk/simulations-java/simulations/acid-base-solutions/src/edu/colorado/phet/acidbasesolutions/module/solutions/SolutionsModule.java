/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.MiscControlsNode;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractModule;
import edu.colorado.phet.acidbasesolutions.persistence.SolutionsConfig;

/**
 * SolutionsModule is the "Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsModule extends ABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_SOLUTIONS_MODULE;
    private static final ABSClock CLOCK = new ABSClock();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SolutionsModel model;
    private SolutionsCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SolutionsModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        AqueousSolution solution = new AqueousSolution();
        model = new SolutionsModel( CLOCK, solution );

        // Canvas
        canvas = new SolutionsCanvas( model, this );
        setSimulationPanel( canvas );

        // No control Panel
        setControlPanel( null );
        
        //  No clock controls
        setClockControlPanel( null );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
        load( SolutionsDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public SolutionsConfig save() {

        SolutionsConfig config = new SolutionsConfig();

        // Module
        config.setActive( isActive() );

        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        config.setSoluteName( solutionControlsNode.getSolute().getName() );
        config.setConcentration( solutionControlsNode.getConcentration() );
        config.setStrength( solutionControlsNode.getStrength() );
        
        BeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        config.setDisassociatedComponentsRatioVisible( beakerControls.isDissociatedComponentsRatioSelected() );
        config.setHydroniumHydroxideRatioVisible( beakerControls.isHydroniumHydroxideRatioSelected() );
        config.setMoleculeCountsVisible( beakerControls.isMoleculeCountsSelected() );
        config.setBeakerLabelVisible( beakerControls.isBeakerLabelSelected() );
        
        MiscControlsNode miscControls = canvas.getMiscControlsNode();
        config.setConcentrationGraphVisible( miscControls.isConcentrationGraphSelected() );
        config.setSymbolLegendVisible( miscControls.isSymbolLegendSelected() );
        config.setEquilibriumExpressionsVisible( miscControls.isEquilibriumExpressionsSelected() );
        config.setReactionEquationsVisible( miscControls.isReactionEquationsSelected() );
        
        return config;
    }

    public void load( SolutionsConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        // solution controls
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        solutionControlsNode.setSolute( SoluteFactory.createSolute( config.getSoluteName() ) );
        if ( solutionControlsNode.getSolute() instanceof ICustomSolute ) {
            solutionControlsNode.setConcentration( config.getConcentration() );
            solutionControlsNode.setStrength( config.getStrength() );
        }

        // beaker controls
        BeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        beakerControls.setDissociatedComponentsRatioSelected( config.isDisassociatedComponentsRatioVisible() );
        beakerControls.setHydroniumHydroxideRatioSelected( config.isHydroniumHydroxideRatioVisible() );
        beakerControls.setMoleculeCountsSelected( config.isMoleculeCountsVisible() );
        beakerControls.setBeakerLabelSelected( config.isBeakerLabelVisible() );

        // misc controls
        MiscControlsNode miscControls = canvas.getMiscControlsNode();
        miscControls.setConcentrationGraphSelected( config.isConcentrationGraphVisible() );
        miscControls.setSymbolLegendSelected( config.isSymbolLegendVisible() );
        miscControls.setEquilibriumExpressionsSelected( config.isEquilibriumExpressionsVisible() );
        miscControls.setReactionEquationsSelected( config.isReactionEquationsVisible() );
    }
}
