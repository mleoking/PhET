/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsControlPanel extends ControlPanel {

    public SolubleSaltsControlPanel( SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        // Sliders for affinity adjustment
        final ModelSlider vesselIonStickSlider = new ModelSlider( "Ion stick likelihood",
                                                                  "",
                                                                  0,
                                                                  1,
                                                                  0,
                                                                  new DecimalFormat( "0.00" ) );
        vesselIonStickSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setIonStickAffinity( new RandomAffinity( vesselIonStickSlider.getValue() ) );
            }
        } );
        final ModelSlider vesselIonReleaseSlider = new ModelSlider( "Ion release likelihood",
                                                                    "",
                                                                    0,
                                                                    1,
                                                                    0,
                                                                    new DecimalFormat( "0.000" ) );
        vesselIonReleaseSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setIonReleaseAffinity( new RandomAffinity( vesselIonReleaseSlider.getValue() ) );
            }
        } );
        addControl( vesselIonStickSlider );
        addControl( vesselIonReleaseSlider );
    }


}
