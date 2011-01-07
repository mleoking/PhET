// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

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
    public MoleculeSelectorPanel( String caption, BufferedImage image, final PhotonAbsorptionModel model, final PhotonTarget photonTarget ){
        super( caption, image );

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

    /**
     * Constructor that takes separate chemical name and chemical symbol.
     */
    public MoleculeSelectorPanel( String chemicalName, String chemicalSymbol, BufferedImage image, final PhotonAbsorptionModel model, final PhotonTarget photonTarget ) {
        this( createCaptionFromNameAndSymbol( chemicalName, chemicalSymbol ), image, model, photonTarget );
    }

    /**
     * Create a caption in the needed format given the chemical name and the
     * symbol.  The needed format consists of the chemical name, followed by
     * a line break, and then the chemical symbol.
     *
     * Example: <html>Carbon Monoxide<br>CO</html>
     *
     * @param chemicalName - Not formatted with HTML.
     * @param chemicalSymbol - Formatted with HTML.
     * @return
     */
    private static String createCaptionFromNameAndSymbol( String chemicalName, String chemicalSymbol ){
        // IMPORTANT NOTE: This function exists in order to avoid forcing the
        // translators to translate the symbol, the name, and then the
        // combined symbol and name.  Since the symbols are assumed to be
        // formatted with HTML (to enable subscripts) and the names are
        // assumed to be plain text strings.  The assertions and exception
        // throwing is designed to make sure this assumption remains valid.
        assert !chemicalName.contains( "<html>" );
        assert chemicalSymbol.contains( "<html>" );
        if (chemicalName.contains( "<html>" ) || !chemicalSymbol.contains( "<html>" )){
            throw new IllegalArgumentException();
        }
        // Create and return the combined string.
        return new String( "<html>" + chemicalName ) + new String( chemicalSymbol ).replace( "<html>", "<br>" );
    }
}