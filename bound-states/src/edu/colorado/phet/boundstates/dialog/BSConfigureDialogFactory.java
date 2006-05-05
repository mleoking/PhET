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
import edu.colorado.phet.boundstates.util.DoubleRange;


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
     * @return
     */
    public static JDialog createDialog( Frame owner, BSAbstractPotential potential, BSAbstractModuleSpec moduleSpec ) {
        
        JDialog dialog = null;
        
        BSWellType wellType = potential.getWellType();
        DoubleRange offsetRange = moduleSpec.getOffsetRange();
        DoubleRange depthRange = moduleSpec.getDepthRange();
        DoubleRange widthRange = moduleSpec.getWidthRange();
        DoubleRange spacingRange = moduleSpec.getSpacingRange();
        DoubleRange separationRange = moduleSpec.getSeparationRange();
        DoubleRange angularFrequencyRange = moduleSpec.getAngularFrequencyRange();
        
        if ( wellType == BSWellType.ASYMMETRIC ) {
            dialog = new BSAsymmetricDialog( owner, (BSAsymmetricWell) potential, offsetRange, depthRange, widthRange );
        }
        else if ( wellType == BSWellType.COULOMB_1D ) {
            dialog = new BSCoulomb1DDialog( owner, (BSCoulomb1DWells) potential, offsetRange, spacingRange );
        }
        else if ( wellType == BSWellType.COULOMB_3D ) {
            dialog = new BSCoulomb3DDialog( owner, (BSCoulomb3DWell) potential, offsetRange );
        }
        else if ( wellType == BSWellType.HARMONIC_OSCILLATOR ) {
            dialog = new BSHarmonicOscillatorDialog( owner, (BSHarmonicOscillatorWell) potential, offsetRange, angularFrequencyRange );
        }
        else if ( wellType == BSWellType.SQUARE ) {
            dialog = new BSSquareDialog( owner, (BSSquareWells) potential, offsetRange, depthRange, widthRange, separationRange );
        }
        else {
            throw new IllegalArgumentException( "unsupported well type: " + wellType );
        }
        
        return dialog;
    }
}
