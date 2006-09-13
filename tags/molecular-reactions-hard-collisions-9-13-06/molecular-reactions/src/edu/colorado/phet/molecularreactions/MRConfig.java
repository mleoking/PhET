package edu.colorado.phet.molecularreactions;/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

/**
 * edu.colorado.phet.molecularreactions.MRConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRConfig {

    // Version
    public static final String VERSION = "0.00.01";

    // Prefix of the strings bundles
    public static final String LOCALIZATION_BUNDLE = "localization/MRStrings";

    // Debug flag
    public static boolean DEBUG = true;

    // Model constants
    public static double MAX_REACTION_THRESHOLD = 0.5E3;
    public static double DEFAULT_REACTION_THRESHOLD = MAX_REACTION_THRESHOLD * .7;
}
