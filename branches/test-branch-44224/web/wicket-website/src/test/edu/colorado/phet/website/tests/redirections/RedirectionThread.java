package edu.colorado.phet.website.tests.redirections;

import java.util.Map;
import java.util.StringTokenizer;

public class RedirectionThread extends Thread {
    private final Map<Request, Hits> map;
    private final StringDispenser dispenser;

    private boolean complete = false;

    public static final Object maplock = new Object();

    public RedirectionThread( Map<Request, Hits> map, StringDispenser dispenser ) {
        this.map = map;
        this.dispenser = dispenser;
        dispenser.increment();
    }

    @Override
    public void run() {
        String line;
        while ( ( line = dispenser.getStringIfExists() ) != null ) {
            StringTokenizer tokenizer = new StringTokenizer( line, " " );
            if ( tokenizer.countTokens() < 4 ) {
                continue;
            }
            String method = tokenizer.nextToken();
            if ( !method.equals( "GET" ) ) {
                continue;
            }
            String addr = tokenizer.nextToken();
            try {
                int status = Integer.valueOf( tokenizer.nextToken() );
                String redir = tokenizer.nextToken();

                Request request = new Request( addr, status );

                boolean processed = false;

                synchronized( maplock ) {
                    if ( map.containsKey( request ) ) {
                        map.get( request ).increment();
                        processed = true;
                    }
                }

                if ( !processed ) {
                    Hit hit = RedirectionTester.getHit( addr );
                    synchronized( maplock ) {
                        // it might now have the request?
                        if ( map.containsKey( request ) ) {
                            map.get( request ).increment();
                        }
                        else {
                            map.put( request, new Hits( hit ) );
                            //System.err.println( addr + " (" + request.toString() + ":" + ( hit.isOk() ? "OK" : "ERR" ) + ") " + hit );
                        }
                    }
                }
            }
            catch( NumberFormatException e ) {

            }
        }
        complete = true;
        dispenser.decrement();
    }

    public boolean isComplete() {
        synchronized( maplock ) {
            return complete;
        }
    }
}
