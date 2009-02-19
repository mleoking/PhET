
package edu.colorado.phet.common.phetcommon.application;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;


public class PhetInfoQuery {

    private final String project;
    private final String sim;
    private final PhetVersion currentSimVersion;
    private final long currentInstallerTimestamp;
    private final ArrayList listeners;

    public PhetInfoQuery( String project, String sim, PhetVersion currentSimVersion, long currentInstallerTimestamp ) {
        this.project = project;
        this.sim = sim;
        this.currentSimVersion = currentSimVersion;
        this.currentInstallerTimestamp = currentInstallerTimestamp;
        listeners = new ArrayList();
    }

    public void start() {
        //TODO ask the server
        notifyQueryNode( new PhetInfoQueryResult( this ) );
    }

    public static class PhetInfoQueryResult {

        private final PhetInfoQuery query;

        public PhetInfoQueryResult( PhetInfoQuery query ) {
            this.query = query;
        }

        public PhetInfoQuery getQuery() {
            return query;
        }
        
        public boolean isInstallerUpdateRecommended() {
            return true;
        }

        public long getInstallerTimestamp() {
            return 1234567890;
        }

        public long getInstallerAskMeLaterDuration() {
            return 30;
        }
        
        public boolean isSimUpdateRecommended() {
            return getSimVersion().isGreaterThan( query.getCurrentSimVersion() );
        }

        public PhetVersion getSimVersion() {
            return new PhetVersion( "2", "01", "00", "999999", "123456789" );
        }

        public long getSimAskMeLaterDuration() {
            return 1;
        }
    }

    //TODO add an adapter
    public interface Listener {
        public void queryDone( PhetInfoQueryResult result );
        public void exception( Exception e );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyQueryNode( PhetInfoQueryResult result ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).queryDone( result );
        }
    }

    public String getProject() {
        return project;
    }


    public String getSim() {
        return sim;
    }


    public long getCurrentInstallerTimestamp() {
        return currentInstallerTimestamp;
    }

    
    public PhetVersion getCurrentSimVersion() {
        return currentSimVersion;
    }
}
