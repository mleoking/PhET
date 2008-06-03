/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.experiments;

import java.awt.Frame;

import javax.swing.JPanel;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.defaults.ExperimentsDefaults;
import edu.colorado.phet.glaciers.persistence.ExperimentsConfig;

/**
 * AdvancedModule is the "Advanced" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExperimentsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExperimentsModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_EXPERIMENTS, ExperimentsDefaults.CLOCK );
        setLogoPanel( null );
        setSimulationPanel( new JPanel() );
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void resetAll() {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExperimentsConfig save() {
        ExperimentsConfig config = new ExperimentsConfig();
        //XXX
        return config;
    }

    public void load( ExperimentsConfig config ) {
        //XXX
    }

}
