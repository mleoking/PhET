package edu.colorado.phet.build.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class ProcessOutputReader extends Thread {
    private InputStream inputStream;
    private String data = "";

    public ProcessOutputReader( InputStream inputStream ) {
        this.inputStream = inputStream;
    }

    public void run() {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        try {
            String line = bufferedReader.readLine();
            while ( line != null ) {
                if ( data.length() != 0 ) {
                    data = data + "\n";
                }
                data += line;
                line = bufferedReader.readLine();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String getOutput() {
        return data;
    }

    public static class ProcessExecResult {
        private String[] args;
        private int exitCode;
        private String out;
        private String err;

        public ProcessExecResult( String[] args, int exitCode, String out, String err ) {
            this.args = args;
            this.exitCode = exitCode;
            this.out = out;
            this.err = err;
        }

        public String[] getArgs() {
            return args;
        }

        public String toString() {
            return "args=" + Arrays.asList( args ) + ", exitCode=" + exitCode + ", out=" + out + ", err=" + err;
        }

        public boolean getTerminatedNormally() {
            return exitCode == 0;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getOut() {
            return out;
        }

        public String getErr() {
            return err;
        }
    }

    public static ProcessExecResult exec( String[] args ) {
        try {
            Process p = Runtime.getRuntime().exec( args );
            try {
                ProcessOutputReader processOutputReader = new ProcessOutputReader( p.getInputStream() );
                processOutputReader.start();

                ProcessOutputReader processErr = new ProcessOutputReader( p.getErrorStream() );
                processErr.start();

                int code = p.waitFor();

                return new ProcessExecResult( args, code, processOutputReader.getOutput(), processErr.getOutput() );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }

        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }

    }
}