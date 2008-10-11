package edu.colorado.phet.common.phetcommon.updates;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jnlp.UnavailableServiceException;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

public class OpenWebPageToNewVersion {
    public static void openWebPageToNewVersion( String project, String sim ) {
        try {
            URL url = new URL( HTMLUtils.getSimURL( project, sim ) );
            PhetServiceManager.getBasicService().showDocument( url );
        }
        catch( UnavailableServiceException e1 ) {
            e1.printStackTrace();
        }
        catch( MalformedURLException e1 ) {
            e1.printStackTrace();
        }
    }
}
