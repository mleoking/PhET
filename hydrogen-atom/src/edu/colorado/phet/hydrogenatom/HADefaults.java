/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom;

import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.enums.GunMode;
import edu.colorado.phet.hydrogenatom.enums.LightType;

/**
 * HADefaults contains default settings for the simulation's controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HADefaults {

    /* Not intended for instantiation */
    private HADefaults() {}
    
    // Mode (Experiment/Prediction)
    public static final boolean MODE_EXPERIMENT = false;
    
    // Atomic Model
    public static final AtomicModel ATOMIC_MODEL = AtomicModel.SCHRODINGER;
    
    // DeBroglie View
    public static final DeBroglieView DEBROGLIE_VIEW = DeBroglieView.BRIGHTNESS_MAGNITUDE;
    
    // Gun
    public static final boolean GUN_ENABLED = false;
    public static final GunMode GUN_MODE = GunMode.PHOTONS;
    public static final LightType LIGHT_TYPE = LightType.MONOCHROMATIC;
    public static final double WAVELENGTH = 95; // nm
    public static final double LIGHT_INTENSITY = 1.0; // 1.0 = 100%
    public static final double ALPHA_PARTICLES_INTENSITY = LIGHT_INTENSITY;

    // Spectrometer
    public static final boolean SPECTROMETER_SELECTED = true;
    
    // Energy Diagrams
    public static final boolean ENERGY_DIAGRAM_SELECTED = true;
}
