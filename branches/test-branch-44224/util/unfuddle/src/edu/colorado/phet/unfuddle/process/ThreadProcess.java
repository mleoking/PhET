package edu.colorado.phet.unfuddle.process;

import java.io.IOException;

/**
 * Created by: Sam
* May 14, 2008 at 7:31:27 PM
*/
public class ThreadProcess implements MyProcess {
    private MyProcess p;
    private long timeout;

    public ThreadProcess( MyProcess p, long timeout ) {
        this.p = p;
        this.timeout = timeout;
    }

    public String invoke( final String cmd ) throws IOException, InterruptedException {
        final String[] dummy = new String[1];
        Thread t = new Thread( new Runnable() {
            public void run() {
                try {
                    String value = p.invoke( cmd );
                    dummy[0] = value;
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        } );
        t.start();
        long startTime = System.currentTimeMillis();
        while ( dummy[0] == null && ( System.currentTimeMillis() - startTime ) < timeout ) {
            try {
                Thread.sleep( 100 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        if ( dummy[0] == null ) {
            throw new InterruptedException( "Timeout" );
        }
        return dummy[0];
    }
}
