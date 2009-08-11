/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.LegendControlNode;
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
    private boolean symbolLegendDialogWasOpen;
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
        LegendControlNode miscControlsNode = canvas.getLegendControlNode();
        
        // restore state of dialogs
        solutionControlsNode.setKExpressionDialogOpen( kExpressionDialogWasOpen );
        miscControlsNode.setSelected( symbolLegendDialogWasOpen );
    }
    
    public void deactivate() {
        
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        LegendControlNode miscControlsNode = canvas.getLegendControlNode();
        
        // save state of open dialogs
        kExpressionDialogWasOpen = solutionControlsNode.isKExpressionDialogOpen();
        symbolLegendDialogWasOpen = miscControlsNode.isSelected();
        
        // hide any open dialogs
        solutionControlsNode.setKExpressionDialogOpen( false );
        miscControlsNode.setSelected( false );
        
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

        // solution controls
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        config.setSoluteName( solutionControlsNode.getSolute().getName() );
        config.setConcentration( solutionControlsNode.getConcentration() );
        config.setStrength( solutionControlsNode.getStrength() );
        config.setKExpressionVisible( solutionControlsNode.isKExpressionDialogOpen() );
        config.setKExpressionScalingEnabled( solutionControlsNode.isKExpressionScalingEnabled() );
        
        // beaker controls
        SolutionsBeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        config.setSoluteComponentsRatioVisible( beakerControls.isSoluteComponentsRatioSelected() );
        config.setHydroniumHydroxideRatioVisible( beakerControls.isHydroniumHydroxideRatioSelected() );
        config.setMoleculeCountsVisible( beakerControls.isMoleculeCountsSelected() );
        config.setBeakerLabelVisible( beakerControls.isLabelSelected() );
        
        // mutually exclusive views
        config.setGraphVisible( canvas.isGraphVisible() );
        config.setReactionEquationsVisible( canvas.isReactionEquationsVisible() );
        config.setReactionEquationsScalingEnabled( canvas.isReactionEquationsScalingEnabled() );
        config.setEquilibriumExpressionsVisible( canvas.isEquilibriumExpressionsVisible( ) );
        config.setEquilibriumExpressionsScalingEnabled( canvas.isEquilibriumExpressionsScalingEnabled() );
        
        // legend
        LegendControlNode legendControl = canvas.getLegendControlNode();
        config.setSymbolLegendVisible( legendControl.isSelected() );
        
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
        solutionControlsNode.setKExpressionScalingEnabled( config.isKExpressionScalingEnabled() );
        
        // beaker controls
        BeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        beakerControls.setSoluteComponentsRatioSelected( config.isSoluteComponentsRatioVisible() );
        beakerControls.setHydroniumHydroxideRatioSelected( config.isHydroniumHydroxideRatioVisible() );
        beakerControls.setMoleculeCountsSelected( config.isMoleculeCountsVisible() );
        beakerControls.setLabelSelected( config.isBeakerLabelVisible() );

        // mutually exclusive views
        canvas.setGraphVisible( config.isGraphVisible() );
        canvas.setReactionEquationsVisible( config.isReactionEquationsVisible() );
        canvas.setReactionEquationsScalingEnabled( config.isReactionEquationsScalingEnabled() );
        canvas.setEquilibriumExpressionsVisible( config.isEquilibriumExpressionsVisible() );
        canvas.setEquilibriumExpressionsScalingEnabled( config.isEquilibriumExpressionsScalingEnabled() );
        
        // legend
        LegendControlNode legendControl = canvas.getLegendControlNode();
        legendControl.setSelected( config.isSymbolLegendVisible() );
    }
}
