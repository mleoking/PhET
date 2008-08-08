package edu.colorado.phet.licensing;

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

    private static final String[] keys = new String[]{"source", "author", "license", "notes", "same"};

    public ResourceAnnotation( String name ) {
        this.name = name;
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
        ResourceAnnotation annotation = new ResourceAnnotation( parseNext( line ).trim() );
        String attributes = line.substring( annotation.name.length() ).trim();

        annotation.source = getAttribute( "source", attributes );
        annotation.author = getAttribute( "author", attributes );
        annotation.license = getAttribute( "license", attributes );
        annotation.notes = getAttribute( "notes", attributes );
        annotation.same = getAttribute( "same", attributes );
        return annotation;
    }

    private static String getAttribute( String param, String attributes ) {
//        attributes += " suffix=dummyvalue";//dummy key value pair to simplify parsing
        String key = param + "=";
        int index = attributes.indexOf( key );
        if ( index < 0 ) {
            return null;
        }
        else {
            String remainder = attributes.substring( index + key.length() ).trim();
            String substring = parseNext( remainder );
            return substring.trim();
        }
    }

    private static String parseNext( String remainder ) {
        int next = Integer.MAX_VALUE;
        for ( int i = 0; i < keys.length; i++ ) {
            int nextIndex = remainder.indexOf( keys[i] + "=" );
            if ( nextIndex >= 0 && nextIndex < next ) {
                next = nextIndex;
            }
        }
        if ( next == Integer.MAX_VALUE ) {//was the last key-value pair
            next = remainder.length();
        }
        return remainder.substring( 0, next );
    }

    public String toText() {
        return name + " " + getString( "source", source ) + getString( "author", author ) + getString( "license", license ) + getString( "notes", notes ) + getString( "same", same );
    }

    private String getString( String key, String value ) {
        if ( value == null ) {
            return "";
        }
        else {
            return key + "=" + value + " ";
        }
    }

    public String getName() {
        return name;
    }
}
