/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.developer;

import javax.swing.JFrame;
import javax.swing.JSlider;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;


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
    public PhotonAbsorptionParamsDlg (){

        setTitle("Params");
        
        JSlider testSlider = new JSlider();
        add(testSlider);
        
        // Set this to hide itself if the user clicks the close button.
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // Center this in the parent frame.
        setLocationRelativeTo( null );
        
        // Size the dialog to just fit all the controls.
        pack();
    }
}
