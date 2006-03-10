/* Copyright 2005, University of Colorado */

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

import edu.colorado.phet.boundstates.model.*;


/**
 * BSConfigureDialogFactory
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConfigureDialogFactory {

    private BSConfigureDialogFactory() {}

    public static JDialog createDialog( Frame owner, BSAbstractPotential well ) {
        JDialog dialog = null;
        if ( well instanceof BSCoulombWells ) {
            dialog = new BSCoulombDialog( owner, (BSCoulombWells) well );
        }
        else if ( well instanceof BSHarmonicOscillatorWell ) {
            dialog = new BSHarmonicOscillatorDialog( owner, (BSHarmonicOscillatorWell) well );
        }
        else if ( well instanceof BSSquareWells ) {
            dialog = new BSSquareDialog( owner, (BSSquareWells) well );
        }
        else if ( well instanceof BSAsymmetricWell ) {
            dialog = new BSAsymmetricDialog( owner, (BSAsymmetricWell) well );
        }
        else {
            throw new IllegalArgumentException( "unsupported well type: " + well.getWellType() );
        }
        return dialog;
    }
}
