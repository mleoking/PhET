package edu.colorado.phet.licensing;

import java.io.File;

/**
 * Created by: Sam
 * Aug 8, 2008 at 1:36:53 PM
 */
public class AnnotatedFile {
    private File file;
    private ResourceAnnotation resourceAnnotation;

    public AnnotatedFile( File file, ResourceAnnotation resourceAnnotation ) {
        this.file = file;
        this.resourceAnnotation = resourceAnnotation;
    }

    public File getFile() {
        return file;
    }

    public String toString() {
        return resourceAnnotation.toText();
    }

    public ResourceAnnotation getResourceAnnotation() {
        return resourceAnnotation;
    }
}
