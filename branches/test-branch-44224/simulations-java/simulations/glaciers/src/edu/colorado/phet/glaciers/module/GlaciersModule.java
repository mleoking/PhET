/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.module;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.dialog.EvolutionStateDialog;
import edu.colorado.phet.glaciers.dialog.ModelConstantsDialog;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.GlaciersModel;

/**
 * GlaciersModule is the base class for all modules in the Glaciers sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GlaciersModule extends PiccoloModule {
    
    private final GlaciersModel _model;
    
    // developer controls
    private EvolutionStateDialog _evolutionStateDialog; // debug
    private boolean _evolutionStateDialogVisible; // debug
    private ModelConstantsDialog _modelConstantsDialog; // debug
    private boolean _modelConstantsDialogVisible; // debug

    public GlaciersModule( String title ) {
        super( title, new GlaciersClock() );
        
        _model = new GlaciersModel( (GlaciersClock)getClock() );
        
        // we won't be using any of the standard subpanels
        setMonitorPanel( null );
        setSimulationPanel( null );
        setClockControlPanel( null );
        setLogoPanel( null );
        setControlPanel( null );
        setHelpPanel( null );
        
        // Debug
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            
            _evolutionStateDialog = new EvolutionStateDialog( PhetApplication.getInstance().getPhetFrame(), _model.getGlacier(), getName() );
            _evolutionStateDialogVisible = false;
            
            _modelConstantsDialog = new ModelConstantsDialog( PhetApplication.getInstance().getPhetFrame(), _model.getGlacier(), getName() );
            _modelConstantsDialogVisible = false;
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    protected GlaciersModel getGlaciersModel() {
        return _model;
    }
    
    public void setEvolutionStateDialogVisible( boolean visible ) {
        _evolutionStateDialogVisible = visible;
        if ( isActive() && _evolutionStateDialog != null ) {
            _evolutionStateDialog.setVisible( visible );
        }
    }
    
    public void setModelConstantsDialogVisible( boolean visible ) {
        _modelConstantsDialogVisible = visible;
        if ( isActive() && _modelConstantsDialog != null ) {
            _modelConstantsDialog.setVisible( visible );
        }
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    public void activate() {
        super.activate();
        if ( _evolutionStateDialog != null && _evolutionStateDialogVisible ) {
            _evolutionStateDialog.setVisible( true );
        }
        if ( _modelConstantsDialog != null && _modelConstantsDialogVisible ) {
            _modelConstantsDialog.setVisible( true );
        }
    }
    
    public void deactivate() {
        if ( _evolutionStateDialog != null ) {
            _evolutionStateDialog.setVisible( false );
        }
        if ( _modelConstantsDialog != null ) {
            _modelConstantsDialog.setVisible( false );
        }
        super.deactivate();
    }
}
