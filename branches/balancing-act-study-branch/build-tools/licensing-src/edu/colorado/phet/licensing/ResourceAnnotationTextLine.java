package edu.colorado.phet.licensing;

/**
 * Created by: Sam
 * Aug 7, 2008 at 9:47:09 PM
 */
public class ResourceAnnotationTextLine implements ResourceAnnotationElement {
    private String line;

    public ResourceAnnotationTextLine( String line ) {
        this.line = line;
    }

    public String toText() {
        return line;
    }
}
