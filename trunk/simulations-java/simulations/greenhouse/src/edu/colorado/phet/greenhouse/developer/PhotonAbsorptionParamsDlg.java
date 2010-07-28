/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.developer;

import java.awt.Frame;
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
    public PhotonAbsorptionParamsDlg (Frame frame, final PhotonAbsorptionModel model){
        super(frame);

        setTitle("Params");
        setLayout(new GridLayout( 3, 2));

        // Create and add the slider for controlling the photon emission rate.
        add(new JLabel( "Single Target Photon Emission Frequency", JLabel.CENTER));
        final LinearValueControl singleTargetEmissionRateSlider = new LinearValueControl( 0, 5, "Frequency:", "#.#", "Photons/sec" );
        singleTargetEmissionRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setPhotonEmissionPeriodSingleTarget( 1 / singleTargetEmissionRateSlider.getValue() * 1000 );
            }
        });
        singleTargetEmissionRateSlider.setValue( 1 / model.getPhotonEmissionPeriodSingleTarget() * 1000 );
        add( singleTargetEmissionRateSlider  );

        // Create and add the slider for controlling the photon emission rate.
        add(new JLabel( "Multi-Target Photon Emission Frequency", JLabel.CENTER));
        final LinearValueControl multiTargetEmissionRateSlider = new LinearValueControl( 0, 5, "Frequency:", "#.#", "Photons/sec" );
        multiTargetEmissionRateSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setPhotonEmissionPeriodMultipleTarget( 1 / multiTargetEmissionRateSlider.getValue() * 1000 );
            }
        });
        multiTargetEmissionRateSlider.setValue( 1 / model.getPhotonEmissionPeriodMultipleTarget() * 1000 );
        add( multiTargetEmissionRateSlider  );

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
        
        // Size the dialog to just fit all the controls.
        pack();
    }
}
