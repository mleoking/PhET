/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.MiscControlsNode;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
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
    private boolean reactionEquationDialogWasOpen, equilibriumExpressionsDialogWasOpen, symbolLegendDialogWasOpen;
    private boolean kExpressionDialogWasOpen;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SolutionsModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        model = new SolutionsModel( CLOCK );

        // Canvas
        canvas = new SolutionsCanvas( model, this );
        setSimulationPanel( canvas );

        // No control Panel
        setControlPanel( null );
        
        //  No clock controls
        setClockControlPanel( null );

        // Help
        if ( hasHelp() ) {
            // add help items here
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
        super.reset();
        load( SolutionsDefaults.getInstance().getConfig() );
    }

    public void activate() {
        super.activate();
        
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        MiscControlsNode miscControlsNode = canvas.getMiscControlsNode();
        
        // restore state of dialogs
        solutionControlsNode.setKExpressionDialogOpen( kExpressionDialogWasOpen );
        miscControlsNode.setReactionEquationsSelected( reactionEquationDialogWasOpen );
        miscControlsNode.setEquilibriumExpressionsSelected( equilibriumExpressionsDialogWasOpen );
        miscControlsNode.setSymbolLegendSelected( symbolLegendDialogWasOpen );
    }
    
    public void deactivate() {
        
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        MiscControlsNode miscControlsNode = canvas.getMiscControlsNode();
        
        // save state of open dialogs
        kExpressionDialogWasOpen = solutionControlsNode.isKExpressionDialogOpen();
        reactionEquationDialogWasOpen = miscControlsNode.isReactionEquationsSelected();
        equilibriumExpressionsDialogWasOpen = miscControlsNode.isEquilibriumExpressionsSelected();
        symbolLegendDialogWasOpen = miscControlsNode.isSymbolLegendSelected();
        
        // hide any open dialogs
        solutionControlsNode.setKExpressionDialogOpen( false );
        miscControlsNode.setReactionEquationsSelected( false );
        miscControlsNode.setEquilibriumExpressionsSelected( false );
        miscControlsNode.setSymbolLegendSelected( false );
        
        // deactivate
        super.deactivate();
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
        config.setKExpressionVisible( solutionControlsNode.isKExpressionDialogOpen() );
        
        SolutionsBeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        config.setSoluteComponentsRatioVisible( beakerControls.isSoluteComponentsRatioSelected() );
        config.setHydroniumHydroxideRatioVisible( beakerControls.isHydroniumHydroxideRatioSelected() );
        config.setMoleculeCountsVisible( beakerControls.isMoleculeCountsSelected() );
        config.setBeakerLabelVisible( beakerControls.isLabelSelected() );
        
        MiscControlsNode miscControls = canvas.getMiscControlsNode();
        config.setConcentrationGraphVisible( miscControls.isConcentrationGraphSelected() );
        config.setSymbolLegendVisible( miscControls.isSymbolLegendSelected() );
        config.setEquilibriumExpressionsVisible( miscControls.isEquilibriumExpressionsSelected() );
        config.setReactionEquationsVisible( miscControls.isReactionEquationsSelected() );
        config.setEquilibriumExpressionsScalingEnabled(  miscControls.isEquilibriumExpressionsScalingEnabled() );
        config.setReactionEquationsScalingEnabled(  miscControls.isReactionEquationsScalingEnabled() );
        
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
        solutionControlsNode.setKExpressionDialogOpen( config.isKExpressionVisible() );

        // beaker controls
        BeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        beakerControls.setSoluteComponentsRatioSelected( config.isSoluteComponentsRatioVisible() );
        beakerControls.setHydroniumHydroxideRatioSelected( config.isHydroniumHydroxideRatioVisible() );
        beakerControls.setMoleculeCountsSelected( config.isMoleculeCountsVisible() );
        beakerControls.setLabelSelected( config.isBeakerLabelVisible() );

        // misc controls
        MiscControlsNode miscControls = canvas.getMiscControlsNode();
        miscControls.setConcentrationGraphSelected( config.isConcentrationGraphVisible() );
        miscControls.setSymbolLegendSelected( config.isSymbolLegendVisible() );
        miscControls.setEquilibriumExpressionsSelected( config.isEquilibriumExpressionsVisible() );
        miscControls.setReactionEquationsSelected( config.isReactionEquationsVisible() );
        miscControls.setEquilibriumExpressionsScalingEnabled( config.isEquilibriumExpressionsScalingEnabled() );
        miscControls.setReactionEquationsScalingEnabled( config.isReactionEquationsScalingEnabled() );
    }
}
