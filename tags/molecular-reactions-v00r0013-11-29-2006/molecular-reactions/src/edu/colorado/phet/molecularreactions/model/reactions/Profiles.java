/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.reactions;

import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.MRConfig;

/**
 * Profiles
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Profiles {

    public static final EnergyProfile DEFAULT = MRConfig.DEFAULT_ENERGY_PROFILE;

    public static final EnergyProfile R1 = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );

    public static final EnergyProfile R2 = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .4,
                                                                100 );

    public static final EnergyProfile R3 = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .7,
                                                                MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                100 );
    
    public static final EnergyProfile DYO = new EnergyProfile( MRConfig.DEFAULT_ENERGY_PROFILE );
}
