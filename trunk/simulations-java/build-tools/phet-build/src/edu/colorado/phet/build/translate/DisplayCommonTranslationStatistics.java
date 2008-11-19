package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by: Sam
 * Dec 14, 2007 at 10:26:06 AM
 */
public class DisplayCommonTranslationStatistics {
    private File baseDir;

    public DisplayCommonTranslationStatistics( File baseDir ) {
        this.baseDir = baseDir;
    }

    public static void main( String[] args ) throws IOException {
        new DisplayCommonTranslationStatistics( new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java" ) ).displayStats();
    }

    private void displayStats() throws IOException {
        File common = new File( baseDir, "common" );
        for ( int i = 0; i < common.listFiles().length; i++ ) {
            File child = common.listFiles()[i];
            if ( child.isDirectory() && !child.getName().startsWith( "." ) ) {
                String name = child.getName();
                File p = new File( common, name + "/data/"+name+"/localization/" + name + "-strings.properties" );
                Properties prop = new Properties();
                if ( p.exists() ) {
                    prop.load( new FileInputStream( p ) );
                }
                System.out.println( name + ": " + ( p.exists() ? prop.size() : 0 ) );
            }
        }
    }
}
