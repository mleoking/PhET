package edu.colorado.phet.mm;

import java.util.ArrayList;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 12:15:36 AM
 */
public class Main {
    public static void main( String[] args ) {
        ArrayList list = ImageFinder.getAnnotatedImageEntries();
        MultimediaApplication multimediaApplication = new MultimediaApplication();
        multimediaApplication.setImageEntries( (ImageEntry[])list.toArray( new ImageEntry[0] ) );
        multimediaApplication.start();
    }
}
