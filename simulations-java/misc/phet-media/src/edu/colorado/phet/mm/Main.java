package edu.colorado.phet.mm;

import java.io.IOException;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 12:49:30 AM
 */
public class Main {
    public static void main( String[] args ) {
        MultimediaApplication multimediaApplication = new MultimediaApplication();
        try {
            multimediaApplication.setImageEntries( ConvertAnnotatedRepository.loadAnnotatedEntries() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        multimediaApplication.start();
    }
}
