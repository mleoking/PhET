/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src-test/TestOptionsSaveRestore.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.2 $
 * Date modified : $Date: 2006/07/16 18:08:59 $
 */

import edu.colorado.phet.simlauncher.Options;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * TestOptionsSaveRestore
 *
 * @author Ron LeMaster
 * @version $Revision: 1.2 $
 */
public class TestOptionsSaveRestore {
    public static void main( String[] args ) {
        Options options = Options.instance();
        options.setShowInstalledThumbnails( false );
        options.setShowCatalogThumbnails( true );
        options.save();

        // Deserialize an object
        try {
            XMLDecoder decoder = new XMLDecoder( new BufferedInputStream(
                    new FileInputStream( "outfilename.xml" ) ) );

            // MyClass is declared in e7 Serializing a Bean to XML
            Options o = (Options)decoder.readObject();
            decoder.close();

            // Use the object
            System.out.println( "o = " + o.isShowInstalledThumbnails() );
        }
        catch( FileNotFoundException e ) {
        }
    }
}
