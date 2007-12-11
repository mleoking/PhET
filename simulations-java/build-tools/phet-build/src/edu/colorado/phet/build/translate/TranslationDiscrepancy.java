package edu.colorado.phet.build.translate;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

import com.jcraft.jsch.JSchException;

public class TranslationDiscrepancy {
    private final Set extraLocal;
    private final Set extraRemote;
    private final PhetProject phetProject;
    private final String flavor;

    TranslationDiscrepancy( Set extraLocal, Set extraRemote, PhetProject phetProject, String flavor ) {
        this.extraLocal = extraLocal;
        this.extraRemote = extraRemote;
        this.phetProject = phetProject;
        this.flavor = flavor;
    }

    public Set getExtraLocal() {
        return extraLocal;
    }

    public Set getExtraRemote() {
        return extraRemote;
    }

    public PhetProject getPhetProject() {
        return phetProject;
    }

    public String getFlavor() {
        return flavor;
    }

    public String toString() {
        return "need to be removed from remote jar: " + extraRemote + ", " + "need to be added to remote jar: " + extraLocal + " ";
    }

    public void resolve() {
        try {
            File resolveJAR = new File( FileUtils.getTmpDir(), flavor + "_resolved" + System.currentTimeMillis() + ".jar" );
            resolve( resolveJAR );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void resolve( File resolveJAR ) throws IOException {
        if ( !extraLocal.isEmpty() || !extraRemote.isEmpty() ) {
            File jarFile = downloadJAR();
            System.out.println( "Downloaded Jar file: " + jarFile.getAbsolutePath() );
            synchronizeStrings( jarFile, resolveJAR );

//        try {
//            uploadJAR( resolveJAR );
//        }
//        catch( JSchException e ) {
//            e.printStackTrace();
//        }
        }
        else {
            System.out.println( "no resolution needed for: " + phetProject.getName() + ": " + flavor );
        }
    }

    private void uploadJAR( File resolveJAR ) throws JSchException, IOException {
        ScpTo.uploadFile( resolveJAR, "reids", "tigercat.colorado.edu", "/home/tigercat/phet/reids/" + resolveJAR.getName() );
    }

    private void synchronizeStrings( File jarFile, File resolveJAR ) throws IOException {
        File tempUnzipDir = new File( FileUtils.getTmpDir(), flavor + "-dir" );
        System.out.println( "tempUnzipDir.getAbsolutePath() = " + tempUnzipDir.getAbsolutePath() );
        tempUnzipDir.mkdirs();

        final Pattern excludePattern = Pattern.compile( quote( phetProject.getName() ) + "[\\\\/]localization[\\\\/]" + quote( phetProject.getName() ) + ".*\\.properties" );
        FileUtils.unzip( jarFile, tempUnzipDir, new FileFilter() {
            public boolean accept( File file ) {
                return !excludePattern.matcher( file.getAbsolutePath() ).find();
            }
        } );

        File localizationDir = new File( tempUnzipDir, phetProject.getName() + File.separator + "localization" );

        localizationDir.mkdir();

        for ( Iterator iterator = extraLocal.iterator(); iterator.hasNext(); ) {
            Locale locale = (Locale) iterator.next();

            File source = phetProject.getTranslationFile( locale );
            FileUtils.copyTo( source, new File( localizationDir, source.getName() ) );
        }
        FileUtils.jar( tempUnzipDir, resolveJAR );
    }

    private File downloadJAR() throws IOException {
        String deployUrl = phetProject.getDeployedFlavorJarURL( flavor );

        File jarFile = File.createTempFile( flavor, ".jar" );

        FileDownload.download( deployUrl, jarFile );
        return jarFile;
    }

    //http://www.exampledepot.com/egs/java.util.regex/Escape.html
    public static String quote( String name ) {
        if ( name.toLowerCase().indexOf( "\\e" ) > 0 ) {
            throw new RuntimeException( "Quote method will fail" );
        }
        return "\\Q" + name + "\\E";
    }

}
