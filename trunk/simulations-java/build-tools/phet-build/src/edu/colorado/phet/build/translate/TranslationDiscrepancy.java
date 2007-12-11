package edu.colorado.phet.build.translate;

import edu.colorado.phet.build.FileUtils;
import edu.colorado.phet.build.PhetProject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class TranslationDiscrepancy {
    private final Set extraLocal;
    private final Set extraRemote;
    private final PhetProject phetProject;
    private final String flavor;

    TranslationDiscrepancy(Set extraLocal, Set extraRemote, PhetProject phetProject, String flavor) {
        this.extraLocal  = extraLocal;
        this.extraRemote = extraRemote;
        this.phetProject = phetProject;
        this.flavor      = flavor;
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

    public void resolve(File resolveJAR) throws IOException {
//        final Pattern excludePattern = Pattern.compile(Pattern.quote(phetProject.getName()) + "[\\\\/]localization[\\\\/]" + Pattern.quote(phetProject.getName()) + ".*\\.properties");
        final Pattern excludePattern = Pattern.compile(quote(phetProject.getName()) + "[\\\\/]localization[\\\\/]" + quote(phetProject.getName()) + ".*\\.properties");

        String deployUrl = phetProject.getDeployedFlavorJarURL(flavor);

        File jarFile = File.createTempFile(flavor, ".jar");

        FileDownload.download(deployUrl, jarFile);

        File tempUnzipDir = File.createTempFile(flavor + "-dir", "");

        FileUtils.unzip(jarFile, tempUnzipDir, new FileFilter() {
            public boolean accept(File file) {
                if (excludePattern.matcher(file.getAbsolutePath()).find()) {
                    return false;
                }

                return true;
            }
        });

        File localizationDir = new File(tempUnzipDir, phetProject.getName() + File.separator + "localization");

        localizationDir.mkdir();

        for (Iterator iterator = extraLocal.iterator(); iterator.hasNext();) {
            Locale locale = (Locale)iterator.next();

            File source = phetProject.getTranslationFile(locale);
            FileUtils.copyTo(source, new File(localizationDir, source.getName()));
        }
        FileUtils.zip( localizationDir,resolveJAR);
    }

    //http://www.exampledepot.com/egs/java.util.regex/Escape.html
    public static String quote( String name ) {
        if (name.toLowerCase().indexOf( "\\e")>0){
            throw new RuntimeException( "Quote method will fail");
        }
        return "\\Q"+name+"\\E";
    }
}
