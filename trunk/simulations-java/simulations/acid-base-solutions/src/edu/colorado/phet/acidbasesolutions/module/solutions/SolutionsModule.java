/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.control.BeakerControls;
import edu.colorado.phet.acidbasesolutions.control.MiscControls;
import edu.colorado.phet.acidbasesolutions.control.SolutionControlsNode;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
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

        // solution controls
        SolutionControlsNode solutionControlsNode = canvas.getSolutionControlsNode();
        solutionControlsNode.setSolute( SolutionsDefaults.SOLUTE );
        if ( solutionControlsNode.getSolute() instanceof ICustomSolute ) {
            solutionControlsNode.setConcentration( SolutionsDefaults.CONCENTRATION );
            solutionControlsNode.setStrength( SolutionsDefaults.STRENGTH );
        }

        // beaker controls
        BeakerControls beakerControls = canvas.getBeakerControls();
        beakerControls.setDissociatedComponentsRatioSelected( SolutionsDefaults.DISASSOCIATED_COMPONENTS_RATIO_VISIBLE );
        beakerControls.setHydroniumHydroxideRatioSelected( SolutionsDefaults.HYDRONIUM_HYDROXIDE_RATIO_VISIBLE );
        beakerControls.setMoleculeCountsSelected( SolutionsDefaults.MOLECULE_COUNTS_VISIBLE );
        beakerControls.setBeakerLabelSelected( SolutionsDefaults.BEAKER_LABEL_VISIBLE );

        // misc controls
        MiscControls miscControls = canvas.getMiscControls();
        miscControls.setConcentrationGraphSelected( SolutionsDefaults.CONCENTRATION_GRAPH_VISIBLE );
        miscControls.setSymbolLegendSelected( SolutionsDefaults.SYMBOL_LEGEND_VISIBLE );
        miscControls.setEquilibriumExpressionsSelected( SolutionsDefaults.EQUILIBRIUM_EXPRESSIONS_VISIBLE );
        miscControls.setReactionRatesSelected( SolutionsDefaults.REACTION_EQUATIONS_VISIBLE );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public SolutionsConfig save() {

        SolutionsConfig config = new SolutionsConfig();

        // Module
        config.setActive( isActive() );

        //XXX call config setters
        
        return config;
    }

    public void load( SolutionsConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        //XXX call config getters
    }
}
