package edu.colorado.phet.buildtools.html5;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Sam
 * Date: 4/23/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class XMLVMListSources {
    public static void main( String[] args ) {
        File f = new File( "C:\\Users\\Sam\\Desktop\\xmlvmoutFAMB" );
        for ( File file : f.listFiles() ) {
            System.out.println( "<script type=\"text/javascript\" src=\"" + file.getName() + "\"></script>" );
        }
    }
}
