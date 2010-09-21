package edu.colorado.phet.website.tests.redirections;

import java.io.*;

public class StringDispenser {
    private final BufferedReader reader;
    private int counter = 0;

    public StringDispenser( File file ) throws FileNotFoundException {
        reader = new BufferedReader( new FileReader( file ) );
    }

    public synchronized String getStringIfExists() {
        try {
            return reader.readLine();
        }
        catch( IOException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized void increment() {
        counter++;
    }

    public synchronized void decrement() {
        counter--;
    }

    public synchronized int getCounter() {
        return counter;
    }
}
