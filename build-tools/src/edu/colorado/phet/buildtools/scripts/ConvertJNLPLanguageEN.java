package edu.colorado.phet.buildtools.scripts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Created by: Sam
 * Feb 28, 2008 at 2:42:34 PM
 */
public class ConvertJNLPLanguageEN {
    private File src;
    private File dst;

    public ConvertJNLPLanguageEN( File src, File dst ) {
        this.src = src;
        this.dst = dst;
        this.dst.mkdirs();
    }

    public static void main( String[] args ) throws IOException {
        new ConvertJNLPLanguageEN( new File( "C:\\Users\\Sam\\Desktop\\sims-2" ), new File( "C:\\Users\\Sam\\Desktop\\sims-out-" + System.currentTimeMillis() ) ).start();
    }

    private void start() throws IOException {
        File[] f = src.listFiles();
        for ( int i = 0; i < f.length; i++ ) {
            File simDir = f[i];
            if ( simDir.isDirectory() ) {
                File newDir = new File( dst, simDir.getName() );
                newDir.mkdirs();
                File[] simFiles = simDir.listFiles( new FilenameFilter() {
                    public boolean accept( File dir, String name ) {
                        return name.endsWith( ".jnlp" ) && name.indexOf( "_" ) < 0;
                    }
                } );
                for ( int j = 0; j < simFiles.length; j++ ) {
                    File simFile = simFiles[j];
                    System.out.println( "simFile.getAbsolutePath() = " + simFile.getAbsolutePath() );
                    String text = FileUtils.loadFileAsString( simFile, "utf-16" );
                    final String matchString = "    <resources>\n" +
                                               "\n" +
                                               "        <j2se version=\"1.4+\"/>\n";
                    String replacement = "    <resources>\n" +
                                         "\n" +
                                         "        <j2se version=\"1.4+\"/>\n" +
                                         "        <property name=\"javaws.phet.locale\" value=\"en\" />\n" + //XXX #1057, backward compatibility, delete after IOM
                                         "        <property name=\"javaws.user.language\" value=\"en\" />\n";
                    int index = text.indexOf( matchString );
                    if ( index <= 0 ) {
                        System.out.println( "Malformed JNLP" );
                    }
                    else {
                        text = text.substring( 0, index ) + replacement + text.substring( index + matchString.length() );
                        System.out.println( "Text replacement:\n" + text );
                        File destFile = new File( newDir, simFile.getName() );
                        destFile.createNewFile();
                        FileUtils.writeString( destFile, text, "utf-16" );
                    }
                }
            }
        }
    }
}
