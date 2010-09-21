package edu.colorado.phet.website.admin;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.cache.InstallerCache;
import edu.colorado.phet.website.templates.PhetPage;

/**
 * Called after an installer build with the latest timestamp
 */
public class AdminNewInstallerPage extends PhetPage {

    private static final Logger logger = Logger.getLogger( AdminNewInstallerPage.class.getName() );

    private static final Object lock = new Object();


    public AdminNewInstallerPage( PageParameters parameters ) {
        super( parameters );

        synchronized( lock ) {

            if ( getPhetCycle().isLocalRequest() ) {
                String timestampString = parameters.getString( "timestamp" );
                long timestamp = Long.valueOf( timestampString );
                InstallerCache.updateTimestamp( timestamp );

                addTitle( "updated timestamp to " + timestamp );
            }

        }

    }

}