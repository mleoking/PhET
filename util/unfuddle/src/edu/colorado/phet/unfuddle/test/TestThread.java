package edu.colorado.phet.unfuddle.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by: Sam
 * May 14, 2008 at 6:55:03 PM
 */
public class TestThread {
    public static void main( String[] args ) {
        String cmd = "java -classpath C:\\reid\\phet\\svn\\trunk\\util\\unfuddle\\classes edu.colorado.phet.unfuddle.test.RunLongTime 10000";
        testProcess( new BasicProcess(), cmd );
        testProcess( new ThreadProcess( new BasicProcess(), 5000 ), cmd );
    }

    private static void testProcess( MyProcess myProcess, String command ) {
        try {
            String out = myProcess.invoke( command );
            System.out.println( "out=" + out );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public static interface MyProcess {
        String invoke( String cmd ) throws IOException, InterruptedException;
    }

    public static class ThreadProcess implements MyProcess {
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

    public static class BasicProcess implements MyProcess {
        public String invoke( String cmd ) throws IOException {
            Process p = Runtime.getRuntime().exec( cmd );
            StringBuffer s = new StringBuffer();
            InputStream in = p.getInputStream();
            int c;
            while ( ( c = in.read() ) != -1 ) {//blocks until data is available
                s.append( (char) c );
            }
            in.close();
            return s.toString();
        }
    }

}