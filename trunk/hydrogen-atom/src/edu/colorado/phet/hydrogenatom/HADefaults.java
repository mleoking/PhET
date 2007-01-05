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
    
    // Use this flag to quickly switch between test values and production values.
    private static final boolean TEST = false;

    /* Not intended for instantiation */
    private HADefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = ( TEST ? true : true );
    public static final int CLOCK_INDEX = ( TEST ? 2 : 2 );
    
    // Mode (Experiment/Prediction)
    public static final boolean MODE_EXPERIMENT = ( TEST ? false : true );
    
    // Atomic Model
    public static final AtomicModel ATOMIC_MODEL = ( TEST ? AtomicModel.SCHRODINGER : AtomicModel.BILLIARD_BALL );
    
    // DeBroglie View
    public static final DeBroglieView DEBROGLIE_VIEW = ( TEST ? DeBroglieView.BRIGHTNESS_MAGNITUDE : DeBroglieView.BRIGHTNESS_MAGNITUDE );
    
    // Gun
    public static final boolean GUN_ENABLED = ( TEST ? false : false );
    public static final GunMode GUN_MODE = ( TEST ? GunMode.PHOTONS : GunMode.PHOTONS );
    public static final LightType LIGHT_TYPE = ( TEST ? LightType.MONOCHROMATIC : LightType.WHITE );
    public static final double WAVELENGTH = ( TEST ? 95 : 490 );
    public static final double LIGHT_INTENSITY = ( TEST ? 1.0 : 1.0 );
    public static final double ALPHA_PARTICLES_INTENSITY = ( TEST ? 1.0 : 1.0 );
    public static final boolean SHOW_ALPHA_PARTICLE_TRACES = ( TEST ? false : false );

    // Spectrometer
    public static final boolean SPECTROMETER_SELECTED = ( TEST ? true : false );
    public static final boolean SPECTROMETER_RUNNING = ( TEST ? true : true );
    
    // Energy Diagrams
    public static final boolean ENERGY_DIAGRAM_SELECTED = ( TEST ? true : false );
}
