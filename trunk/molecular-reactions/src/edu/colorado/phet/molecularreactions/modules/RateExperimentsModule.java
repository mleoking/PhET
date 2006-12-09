/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;

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
        super( SimStrings.get( "Module.RateExperimentsModuleTitle" ) );

        // We want to make sure the strip chart is cleared and not running when we start up
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                setStripChartVisible( true );
                resetStripChart();
            }
        } );
        setStripChartVisible( true );
        resetStripChart();
    }

    protected void createControlPanel() {
        controlPanel = new RateExperimentsMRControlPanel( this );
        getControlPanel().addControl( controlPanel );
    }

    public void setStripChartVisible( boolean visible ) {
//        if( !visible ) {
//            System.out.println( "RateExperimentsModule.setStripChartVisible: false " );
//        }
//        else {
//            System.out.println( "RateExperimentsModule.setStripChartVisible: true " );
//        }
        super.setStripChartVisible( visible );
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
}
