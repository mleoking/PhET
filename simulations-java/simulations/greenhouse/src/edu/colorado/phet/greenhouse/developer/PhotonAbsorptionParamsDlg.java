/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.developer;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.SingleMoleculePhotonAbsorptionProbability;


/**
 * Developer control dialog for altering some parameters that affect the behavior
 * of the Photon Absorption tab.
 * 
 * @author John Blanco
 */
public class PhotonAbsorptionParamsDlg extends PaintImmediateDialog {

    /**
     * Constructor.
     */
    public PhotonAbsorptionParamsDlg (final PhotonAbsorptionModel model){

        setTitle("Params");
        setLayout(new GridLayout( 2, 2));

        // Create and add the slider for controlling the photon emission rate.
        add(new JLabel( "Photon Emission Frequency", JLabel.CENTER));
        final LinearValueControl emissionRateSlider = new LinearValueControl( 0, 5, "Frequency:", "#.#", "Photons/sec" );
        emissionRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setPhotonEmissionPeriod( 1 / emissionRateSlider.getValue() * 1000 );
            }
        });
        emissionRateSlider.setValue( 1 / model.getPhotonEmissionPeriod() * 1000 );
        add( emissionRateSlider  );

        // Create and add the slider for controlling absorption probability.
        add(new JLabel( "Absorption Probability", JLabel.CENTER));
        final LinearValueControl abosrptionProbabilitySlider = new LinearValueControl( 0, 1, "Probability", "#.#", null );
        abosrptionProbabilitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                SingleMoleculePhotonAbsorptionProbability.getInstance().setAbsorptionsProbability( abosrptionProbabilitySlider.getValue() );
            }
        });
        abosrptionProbabilitySlider.setValue( SingleMoleculePhotonAbsorptionProbability.getInstance().getAbsorptionsProbability() );
        add( abosrptionProbabilitySlider  );

        // Set this to hide itself if the user clicks the close button.
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // Center this in the parent frame.
        setLocationRelativeTo( null );
        
        // Should always be on top when visible.
        setAlwaysOnTop( true );
        
        // Size the dialog to just fit all the controls.
        pack();
    }
}
