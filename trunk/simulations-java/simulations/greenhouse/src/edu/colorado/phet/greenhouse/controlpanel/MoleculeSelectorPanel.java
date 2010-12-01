package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;

/**
 * Class that defines a panel that allows the user to select a specific
 * target molecule in the photon absorption model.  This class takes care
 * of wiring up the button to the model.
 *
 * @author John Blanco
 */
class MoleculeSelectorPanel extends SelectionPanelWithImage {

    /**
     * Constructor.
     */
    public MoleculeSelectorPanel( String text, BufferedImage image, final PhotonAbsorptionModel model, final PhotonTarget photonTarget ) {
        super( text, image );

        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        getRadioButton().addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
//                    if ( getRadioButton().isSelected() ) {
                        model.setPhotonTarget( photonTarget );
//                    }
                }
            } );

        // Listen to the model so that the button state can be updated when
        // the model setting changes.
        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void photonTargetChanged() {
                // The logic in these statements is a little hard to follow,
                // but the basic idea is that if the state of the model
                // doesn't match that of the button, update the button,
                // otherwise leave the button alone.  This prevents a bunch
                // of useless notifications from going to the model.
                if ( ( model.getPhotonTarget() == photonTarget ) != getRadioButton().isSelected() ) {
                    getRadioButton().setSelected( model.getPhotonTarget() == photonTarget );
                }
            }
        } );
    }
}