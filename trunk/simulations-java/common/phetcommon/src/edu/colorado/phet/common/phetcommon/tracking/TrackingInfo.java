package edu.colorado.phet.common.phetcommon.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class TrackingInfo {
    private TrackingEntry[] entries;

    public TrackingInfo( PhetApplicationConfig config ) {
        entries = new TrackingEntry[]{
                //Sim info first
                new TrackingEntry( "version", config.getVersion().toString() ),
                new TrackingEntry( "project", config.getProjectName() ),
                new TrackingEntry( "sim", config.getFlavor() ),
                new TrackingEntry( "sim-locale", PhetResources.readLocale().toString() ),

                //Then general to specific information about machine config
                new TrackingEntry.SystemProperty( "os.name" ),
                new TrackingEntry.SystemProperty( "os.version" ),
                new TrackingEntry.SystemProperty( "os.arch" ),

                new TrackingEntry.SystemProperty( "javawebstart.version" ),
                new TrackingEntry.SystemProperty( "java.version" ),
                new TrackingEntry.SystemProperty( "java.vendor" ),

                new TrackingEntry.SystemProperty( "user.country" ),
                new TrackingEntry.SystemProperty( "user.timezone" ),
                new TrackingEntry( "default-locale", Locale.getDefault().toString() ),

                //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
                new TrackingEntry( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
    }

    public String toPHP() {
        String php = "";
        for ( int i = 0; i < entries.length; i++ ) {
            if ( php.length() > 0 ) {
                php += "&";
            }
            php += entries[i].toPHP();
        }
        return php;
    }

}
