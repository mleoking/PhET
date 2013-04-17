package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.java_websocket.util.Base64;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * @author Sam Reid
 */
public class ConvertImageToBase64 {
    public static void main( String[] args ) throws IOException {

        File source = new File( "C:\\workingcopy\\phet\\svn-1.7\\trunk\\simulations-html5\\simulations\\energy-skate-park-easeljs\\src\\resources\\" );
        File destination = new File( source.getParent(), "resourcesbase64" );
        destination.mkdirs();
        for ( File s : source.listFiles( new FilenameFilter() {
            @Override public boolean accept( final File dir, final String name ) {
                return !name.toLowerCase().equals( ".svn" ) && ( name.toLowerCase().endsWith( ".png" ) || name.toLowerCase().endsWith( ".jpg" ) );
            }
        } ) ) {
            String name = s.getName();
            String suffix = name.substring( name.indexOf( '.' ) + 1 );
            String prefix = name.substring( 0, name.indexOf( '.' ) );
            String encoded = Base64.encodeFromFile( s.getAbsolutePath() );
            System.out.println( encoded );

            String contents = "var " + prefix + "Image = new Image();\n" +
                              prefix + "Image.src=\"data:image/" + suffix + ";base64," + encoded + "\";";

            File outputFile = new File( destination, prefix + "-image.js" );
            FileUtils.writeString( outputFile, contents );
        }

    }
}