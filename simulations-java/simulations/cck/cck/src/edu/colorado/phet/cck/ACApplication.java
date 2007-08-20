package edu.colorado.phet.cck;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Author: Sam Reid
 * Apr 12, 2007, 11:48:01 PM
 */
public class ACApplication {

    public static void main( String[] args ) throws InterruptedException, InvocationTargetException {
        ArrayList array = new ArrayList( Arrays.asList( args ) );
        array.add( CCKApplication.AC_OPTION );
        CCKApplication.main( (String[])array.toArray( new String[0] ) );
    }
}
