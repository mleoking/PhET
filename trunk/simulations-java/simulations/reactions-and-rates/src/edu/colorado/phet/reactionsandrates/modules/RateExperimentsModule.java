/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.modules;

import edu.colorado.phet.reactionsandrates.MRConfig;

/**
 * RateExperimentsModule
 * <p/>
 * This module has controls for running experiments
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RateExperimentsModule extends ComplexModule {
    private RateExperimentsMRControlPanel controlPanel;

    /**
     *
     */
    public RateExperimentsModule() {
        super( MRConfig.RESOURCES.getLocalizedString( "module.rate-experiments" ) );
    }

    protected MRControlPanel createControlPanel() {
        controlPanel = new RateExperimentsMRControlPanel( this );
        return controlPanel;
    }

    /**
     * Tells the module if an experiment is running or not. This allows the module to enable
     * or disable appropriate controls.
     *
     * @param isRunning
     */
    public void setExperimentRunning( boolean isRunning ) {
        if( controlPanel != null ) {
            controlPanel.setExperimentRunning( isRunning );
            setStripChartRecording( isRunning );
        }
    }

    public void resetStripChart() {
        super.resetStripChart();
        setFirstTimeStripChartVisible( true );
    }

    public void reset() {
        super.reset();
        controlPanel.reset();
    }

    public void emptyBox() {
        getMRModel().removeAllMolecules();
    }
    
    public void clearExperiment() {
        super.clearExperiment();
        controlPanel.clearExperiment();
    }
}
