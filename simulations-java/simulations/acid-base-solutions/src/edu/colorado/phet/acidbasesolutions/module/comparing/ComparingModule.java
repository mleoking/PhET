/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.control.*;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.acidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractModule;
import edu.colorado.phet.acidbasesolutions.persistence.ComparingConfig;

/**
 * ComparingModule is the "Comparing Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModule extends ABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_COMPARING_MODULE;
    private static final ABSClock CLOCK = new ABSClock();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ComparingModel model;
    private ComparingCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ComparingModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        model = new ComparingModel( CLOCK );

        // Canvas
        canvas = new ComparingCanvas( model, this );
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
        load( ComparingDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ComparingConfig save() {

        ComparingConfig config = new ComparingConfig();

        // Module
        config.setActive( isActive() );

        // left solution controls
        SolutionControlsNode solutionControlsNodeLeft = canvas.getSolutionControlsNodeLeft();
        config.setSoluteNameLeft( solutionControlsNodeLeft.getSolute().getName() );
        config.setConcentrationLeft( solutionControlsNodeLeft.getConcentration() );
        config.setStrengthLeft( solutionControlsNodeLeft.getStrength() );
        
        // right solution controls
        SolutionControlsNode solutionControlsNodeRight = canvas.getSolutionControlsNodeRight();
        config.setSoluteNameRight( solutionControlsNodeRight.getSolute().getName() );
        config.setConcentrationRight( solutionControlsNodeRight.getConcentration() );
        config.setStrengthRight( solutionControlsNodeRight.getStrength() );
        
        // view controls
        ComparingViewControlsNode viewControlsNode = canvas.getViewControlsNode();
        config.setBeakersSelected( viewControlsNode.isBeakersSelected() );
        config.setGraphsSelected( viewControlsNode.isGraphsSelected() );
        config.setEquationsSelected( viewControlsNode.isEquationsSelected() );
        
        // beaker controls
        ComparingBeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        config.setDisassociatedComponentsRatioVisible( beakerControls.isDissociatedComponentsRatioSelected() );
        config.setHydroniumHydroxideRatioVisible( beakerControls.isHydroniumHydroxideRatioSelected() );
        config.setMoleculeCountsVisible( beakerControls.isMoleculeCountsSelected() );
        config.setBeakerLabelVisible( beakerControls.isLabelSelected() );
        
        // equations controls
        EquationScalingControl equationScalingControl = canvas.getEquationScalingControl();
        config.setEquationsScalingEnabled( equationScalingControl.isScalingEnabled() );
        
        return config;
    }

    public void load( ComparingConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        // left solution controls
        SolutionControlsNode solutionControlsNodeLeft = canvas.getSolutionControlsNodeLeft();
        solutionControlsNodeLeft.setSolute( SoluteFactory.createSolute( config.getSoluteNameLeft() ) );
        if ( solutionControlsNodeLeft.getSolute() instanceof ICustomSolute ) {
            solutionControlsNodeLeft.setConcentration( config.getConcentrationLeft() );
            solutionControlsNodeLeft.setStrength( config.getStrengthLeft() );
        }
        
        // right solution controls
        SolutionControlsNode solutionControlsNodeRight = canvas.getSolutionControlsNodeRight();
        solutionControlsNodeRight.setSolute( SoluteFactory.createSolute( config.getSoluteNameRight() ) );
        if ( solutionControlsNodeRight.getSolute() instanceof ICustomSolute ) {
            solutionControlsNodeRight.setConcentration( config.getConcentrationRight() );
            solutionControlsNodeRight.setStrength( config.getStrengthRight() );
        }
        
        // view controls
        ComparingViewControlsNode viewControlsNode = canvas.getViewControlsNode();
        viewControlsNode.setBeakersSelected( config.isBeakersSelected() );
        viewControlsNode.setGraphsSelected( config.isGraphsSelected() );
        viewControlsNode.setEquationsSelected( config.isEquationsSelected() );

        // beaker controls
        BeakerControlsNode beakerControls = canvas.getBeakerControlsNode();
        beakerControls.setDissociatedComponentsRatioSelected( config.isDisassociatedComponentsRatioVisible() );
        beakerControls.setHydroniumHydroxideRatioSelected( config.isHydroniumHydroxideRatioVisible() );
        beakerControls.setMoleculeCountsSelected( config.isMoleculeCountsVisible() );
        beakerControls.setLabelSelected( config.isBeakerLabelVisible() );
        
        // equations controls
        EquationScalingControl equationScalingControl = canvas.getEquationScalingControl();
        equationScalingControl.setScalingEnabled( config.isEquationsScalingEnabled() );
    }
}
