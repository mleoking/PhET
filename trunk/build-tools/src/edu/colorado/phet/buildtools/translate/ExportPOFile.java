package edu.colorado.phet.buildtools.translate;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by: Sam
 * Feb 4, 2008 at 7:48:56 AM
 */
public class ExportPOFile {
    public static void main( String[] args ) throws IOException {
        ExportPOFile.export( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\cck\\data\\cck\\localization\\cck-strings.properties" ),
                             new File( "C:\\Users\\Sam\\Desktop\\cck.po" ) );
    }

    private static void export( File propertyFile, File poFile ) throws IOException {
        Properties properties = new Properties();
        properties.load( new FileInputStream( propertyFile ) );

        poFile.getParentFile().mkdirs();
        poFile.createNewFile();
        Set keySet = properties.keySet();
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( poFile ) );
        for ( Iterator iterator = keySet.iterator(); iterator.hasNext(); ) {
            String key = (String) iterator.next();
            String value = properties.getProperty( key );
            value = value.replaceAll( "\n", "\\n" );
            String entry = getEntry( key, value );
            bufferedWriter.write( entry );
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    private static String getEntry( String key, String value ) {
        return "\n# " + value + "\nmsgid " + key + "\nmsgstr " + value;
    }
}
