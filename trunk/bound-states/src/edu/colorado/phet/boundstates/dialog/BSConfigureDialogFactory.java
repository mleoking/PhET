/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.Frame;

import javax.swing.JDialog;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;


/**
 * BSConfigureDialogFactory is a factory that creates the proper type 
 * of "Configure" dialog for a specified potential.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConfigureDialogFactory {

    /* Not intended for instantiation */
    private BSConfigureDialogFactory() {}

    /**
     * Creates a "Configure" dialog for a specified potential.
     * The moduleSpec determines the value ranges for the dialog's controls.
     * 
     * @param owner
     * @param potential
     * @param moduleSpec
     * @return a JDialog
     */
    public static JDialog createDialog( Frame owner, BSAbstractPotential potential, BSAbstractModuleSpec moduleSpec ) {
        
        JDialog dialog = null;
        
        BSWellType wellType = potential.getWellType();
        final boolean offsetControlSupported = moduleSpec.isOffsetControlSupported();
        
        if ( wellType == BSWellType.ASYMMETRIC ) {
            dialog = new BSAsymmetricDialog( owner, (BSAsymmetricPotential) potential, moduleSpec.getAsymmetricSpec() );
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            dialog = new BSCoulomb1DDialog( owner, (BSCoulomb1DPotential) potential, moduleSpec.getCoulomb1DSpec(), offsetControlSupported );
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            dialog = new BSCoulomb3DDialog( owner, (BSCoulomb3DPotential) potential, moduleSpec.getCoulomb3DSpec() );
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            dialog = new BSHarmonicOscillatorDialog( owner, (BSHarmonicOscillatorPotential) potential, moduleSpec.getHarmonicOscillatorSpec() );
        }
        else if ( wellType == BSWellType.SQUARE ) {
            dialog = new BSSquareDialog( owner, (BSSquarePotential) potential, moduleSpec.getSquareSpec(), offsetControlSupported );
        }
        else {
            throw new IllegalArgumentException( "unsupported well type: " + wellType );
        }
        
        return dialog;
    }
}
