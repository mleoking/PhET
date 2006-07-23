/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
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
 * @version $Revision$
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
