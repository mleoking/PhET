package edu.colorado.phet.build.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOutputReader extends Thread {
    private InputStream inputStream;
    private String data="";

    public ProcessOutputReader( InputStream inputStream ) {
        this.inputStream = inputStream;
    }

    public void run() {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream ) );
        try {
            String line = bufferedReader.readLine();
            while ( line != null ) {
                if (data.length()!=0){
                    data=data+"\n";
                }
                data+=line;
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
}