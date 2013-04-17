// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.comparing;

import java.awt.Frame;

import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.advancedacidbasesolutions.AdvancedAcidBaseSolutionsApplication;
import edu.colorado.phet.advancedacidbasesolutions.control.*;
import edu.colorado.phet.advancedacidbasesolutions.model.AABSClock;
import edu.colorado.phet.advancedacidbasesolutions.model.SoluteFactory;
import edu.colorado.phet.advancedacidbasesolutions.model.Solute.ICustomSolute;
import edu.colorado.phet.advancedacidbasesolutions.module.AABSAbstractModule;
import edu.colorado.phet.advancedacidbasesolutions.persistence.ComparingConfig;

/**
 * ComparingModule is the "Comparing Solutions" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingModule extends AABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = AABSStrings.TITLE_COMPARING_MODULE;
    private static final AABSClock CLOCK = new AABSClock();

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
        config.setSoluteComponentsRatioVisible( beakerControls.isSoluteComponentsRatioSelected() );
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
            AdvancedAcidBaseSolutionsApplication.getInstance().setActiveModule( this );
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
        beakerControls.setSoluteComponentsRatioSelected( config.isSoluteComponentsRatioVisible() );
        beakerControls.setHydroniumHydroxideRatioSelected( config.isHydroniumHydroxideRatioVisible() );
        beakerControls.setMoleculeCountsSelected( config.isMoleculeCountsVisible() );
        beakerControls.setLabelSelected( config.isBeakerLabelVisible() );
        
        // equations controls
        EquationScalingControl equationScalingControl = canvas.getEquationScalingControl();
        equationScalingControl.setScalingEnabled( config.isEquationsScalingEnabled() );
    }
}
