// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.photonabsorption.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel.PhotonTarget;

/**
 * Class that defines a panel that allows the user to select a specific
 * target molecule in the photon absorption model.  This class takes care
 * of wiring up the button to the model.  This particular version takes a
 * string for the caption and another string for the "tool tip", i.e. some
 * text that appears when the user hovers the mouse over the button.
 *
 * @author John Blanco
 */
public class MoleculeSelectorPanelWithToolTip extends SelectionPanelWithImage {

    /**
     * Constructor.
     */
    public MoleculeSelectorPanelWithToolTip( String buttonCaption, String toolTipText, BufferedImage image,
            final PhotonAbsorptionModel model, final PhotonTarget photonTarget ) {

        super( buttonCaption, toolTipText, image );

        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        getRadioButton().addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                     model.setPhotonTarget( photonTarget );
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