package edu.colorado.phet.common.phetcommon.updates;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.UnavailableServiceException;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

public class OpenWebPageToNewVersion {
    public static void openWebPageToNewVersion() {
        try {
            //todo: look up the correct URL for the simulation, should probably be a redirect from sim.flavor to Human Readable
            PhetServiceManager.getBasicService().showDocument( new URL( "http://phet.colorado.edu" ) );
        }
        catch( UnavailableServiceException e1 ) {
            e1.printStackTrace();
        }
        catch( MalformedURLException e1 ) {
            e1.printStackTrace();
        }
    }
}
