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

import edu.colorado.phet.molecularreactions.view.charts.StripChartDialog;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsPieChartNode;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChartNode;
import edu.colorado.phet.molecularreactions.view.PumpGraphic;
import edu.colorado.phet.molecularreactions.view.SimpleMoleculeGraphic;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Rectangle2D;
import java.awt.event.ComponentListener;

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


    public RateExperimentsModule() {
        super( SimStrings.get( "Module.RateExperimentsModuleTitle"));

        controlPanel = new RateExperimentsMRControlPanel( this );
        setMRControlPanel( controlPanel );
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
        }
    }
}
