
package edu.colorado.phet.common.phetcommon.application;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.resources.PhetInstallerVersion;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;


public class VersionInfoQuery {

    private final String project;
    private final String sim;
    private final PhetVersion currentSimVersion;
    private final PhetInstallerVersion currentInstallerVersion;
    private final ArrayList listeners;

    public VersionInfoQuery( String project, String sim, PhetVersion currentSimVersion, PhetInstallerVersion currentInstallerVersion ) {
        this.project = project;
        this.sim = sim;
        this.currentSimVersion = currentSimVersion;
        this.currentInstallerVersion = currentInstallerVersion;
        listeners = new ArrayList();
    }

    public String getProject() {
        return project;
    }

    public String getSim() {
        return sim;
    }
    
    public PhetVersion getCurrentSimVersion() {
        return currentSimVersion;
    }

    public PhetInstallerVersion getCurrentInstallerVersion() {
        return currentInstallerVersion;
    }

    /**
     * Sends the query to the server.
     * Notifies listeners when the response is received.
     */
    public void send() {
        //TODO ask the server
        notifyQueryNode( new VersionInfoQueryResponse( this ) );
    }

    /**
     * Encapsulates the response to this query.
     */
    public static class VersionInfoQueryResponse {

        private final VersionInfoQuery query;

        public VersionInfoQueryResponse( VersionInfoQuery query ) {
            this.query = query;
        }

        public VersionInfoQuery getQuery() {
            return query;
        }
        
        public boolean isSimUpdateRecommended() {
            return getSimVersion().isGreaterThan( query.getCurrentSimVersion() );
        }

        public PhetVersion getSimVersion() {
            return new PhetVersion( "2", "01", "00", "999999", "123456789" );//TODO
        }

        public long getSimAskMeLaterDuration() {
            return 1;//TODO
        }
        
        public boolean isInstallerUpdateRecommended() {
            return true;//TODO
        }

        public PhetInstallerVersion getInstallerVersion() {
            return new PhetInstallerVersion( 1234567890 );//TODO
        }

        public long getInstallerAskMeLaterDuration() {
            return 30;//TODO
        }
    }

    public interface VersionInfoQueryListener {
        /**
         * The query is done and results are available.
         * @param result
         */
        public void done( VersionInfoQueryResponse result );
        /**
         * An exception occurred, and don't expect a result.
         * @param e
         */
        public void exception( Exception e );
    }
    
    public static class VersionInfoQueryAdapter implements VersionInfoQueryListener {
        public void done( VersionInfoQueryResponse result ) {}
        public void exception( Exception e ) {}
    }

    public void addListener( VersionInfoQueryListener listener ) {
        listeners.add( listener );
    }

    public void removeListener( VersionInfoQueryListener listener ) {
        listeners.remove( listener );
    }

    private void notifyQueryNode( VersionInfoQueryResponse result ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (VersionInfoQueryListener) listeners.get( i ) ).done( result );
        }
    }
}
