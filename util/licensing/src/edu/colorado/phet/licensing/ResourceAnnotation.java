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
    private String licensefile;

    private static final String[] keys = new String[]{"source", "author", "license", "notes", "same", "licensefile"};

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

    public String getLicensefile() {
        return licensefile;
    }

    public void setLicensefile( String licensefile ) {
        this.licensefile = licensefile;
    }

    public static ResourceAnnotation parseElement( String line ) {
        ResourceAnnotation annotation = new ResourceAnnotation( AnnotationParser.parseNext( line, keys ).trim() );
        String attributes = line.substring( annotation.name.length() ).trim();

        annotation.source = AnnotationParser.getAttribute( "source", attributes, keys );
        annotation.author = AnnotationParser.getAttribute( "author", attributes, keys );
        annotation.license = AnnotationParser.getAttribute( "license", attributes, keys );
        annotation.notes = AnnotationParser.getAttribute( "notes", attributes, keys );
        annotation.same = AnnotationParser.getAttribute( "same", attributes, keys );
        annotation.licensefile = AnnotationParser.getAttribute( "licensefile", attributes, keys );
        return annotation;
    }

    public String toText() {
        return name + " " + getString( "source", source ) + getString( "author", author ) + getString( "license", license ) + getString( "notes", notes ) + getString( "same", same ) + getString( "licensefile", licensefile );
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
