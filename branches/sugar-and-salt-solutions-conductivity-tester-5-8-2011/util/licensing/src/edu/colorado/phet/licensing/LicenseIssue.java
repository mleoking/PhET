package edu.colorado.phet.licensing;

/**
 * Marker interface for all license issues.
 */
public interface LicenseIssue {

    /**
     * License issue related to data files.
     */
    public class DataFileLicenseIssue implements LicenseIssue {

        private AnnotatedFile annotatedFile;

        public DataFileLicenseIssue( AnnotatedFile annotatedFile ) {
            super();
            this.annotatedFile = annotatedFile;
        }

        public String toString() {
            return annotatedFile.getResourceAnnotation().getName() + " " + annotatedFile.getResourceAnnotation().toText();
        }
    }
}
