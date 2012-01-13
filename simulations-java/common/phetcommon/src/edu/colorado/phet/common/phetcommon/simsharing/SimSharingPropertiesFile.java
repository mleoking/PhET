// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.io.File;

import edu.colorado.phet.common.phetcommon.application.PhetPersistenceDir;
import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Properties file for sim-sharing ($HOME/.phet/sim-sharing.properties).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingPropertiesFile extends AbstractPropertiesFile {

    private static final String KEY_MACHINE_COOKIE = "machine.cookie";

    public SimSharingPropertiesFile() {
        super( new File( new PhetPersistenceDir(), "sim-sharing.properties" ) );
    }

    public String getMachineCookie() {
        String machineCookie = getProperty( KEY_MACHINE_COOKIE );
        if ( machineCookie == null ) {
            machineCookie = SimSharingManager.generateStrongId();
            setMachineCookie( machineCookie );
        }
        return machineCookie;
    }

    public void setMachineCookie( String machineCookie ) {
        setProperty( KEY_MACHINE_COOKIE, machineCookie );
    }
}
