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

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.*;
import edu.colorado.phet.boundstates.module.BSAbstractModuleSpec;


/**
 * BSConfigureDialogFactory
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConfigureDialogFactory {

    private BSConfigureDialogFactory() {}

    public static JDialog createDialog( Frame owner, BSAbstractPotential well, BSAbstractModuleSpec moduleSpec ) {
        
        JDialog dialog = null;
        
        BSWellType wellType = well.getWellType();
        BSDoubleRange offsetRange = moduleSpec.getOffsetRange();
        BSDoubleRange depthRange = moduleSpec.getDepthRange();
        BSDoubleRange widthRange = moduleSpec.getWidthRange();
        BSDoubleRange spacingRange = moduleSpec.getSpacingRange();
        BSDoubleRange separationRange = moduleSpec.getSeparationRange();
        BSDoubleRange angularFrequencyRange = moduleSpec.getAngularFrequencyRange();
        
        if ( wellType == BSWellType.ASYMMETRIC ) {
            dialog = new BSAsymmetricDialog( owner, (BSAsymmetricWell) well, offsetRange, depthRange, widthRange );
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            dialog = new BSCoulomb1DDialog( owner, (BSCoulomb1DWells) well, offsetRange, spacingRange );
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            dialog = new BSCoulomb3DDialog( owner, (BSCoulomb3DWell) well, offsetRange );
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            dialog = new BSHarmonicOscillatorDialog( owner, (BSHarmonicOscillatorWell) well, offsetRange, angularFrequencyRange );
        }
        else if ( wellType == BSWellType.SQUARE ) {
            dialog = new BSSquareDialog( owner, (BSSquareWells) well, offsetRange, depthRange, widthRange, separationRange );
        }
        else {
            throw new IllegalArgumentException( "unsupported well type: " + wellType );
        }
        
        return dialog;
    }
}
