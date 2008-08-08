package edu.colorado.phet.licensing;

import java.util.StringTokenizer;

/**
 * Created by: Sam
 * Aug 7, 2008 at 9:40:29 PM
 */
public class ResourceAnnotation implements ResourceAnnotationElement {
    private String name;
    private String source;
    private String author;
    private String license;
    private String notes;
    private String same;//if this resource is a copy of another, you can reference the original here

    public ResourceAnnotation() {
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor( String author ) {
        this.author = author;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense( String license ) {
        this.license = license;
    }

    public String getSame() {
        return same;
    }

    public void setSame( String same ) {
        this.same = same;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes( String notes ) {
        this.notes = notes;
    }

    public static ResourceAnnotationElement parseElement( String line ) {
        StringTokenizer st = new StringTokenizer( line, " " );
        ResourceAnnotation annotation = new ResourceAnnotation();
        annotation.name = st.nextToken();
        String attributes = line.substring( annotation.name.length() + 1 ).trim();

        annotation.source = parse( "source", attributes );
        annotation.author = parse( "author", attributes );
        annotation.license = parse( "license", attributes );
        annotation.notes = parse( "notes", attributes );
        annotation.same = parse( "same", attributes );
        return annotation;
    }

    private static String parse( String param, String attributes ) {
        int index = attributes.indexOf( param + "=" );
        if ( index < 0 ) {
            return null;
        }
        else {
            String value = attributes.substring( index );
            StringTokenizer st = new StringTokenizer( value, "=" );
            return st.nextToken();
        }
    }

    public String toText() {
        return name + " " + getString( source, "source" ) + getString( author, "author" ) + getString( license, "license" ) + getString( notes, "notes" ) + getString( same, "same" );
    }

    private String getString( String value, String key ) {
        if ( value == null ) {
            return "";
        }
        else {
            return key + "=" + value + " ";
        }
    }
}
