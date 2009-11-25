package edu.colorado.phet.licensing;

import edu.colorado.phet.common.phetcommon.util.AnnotationParser;

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

    /*
     * If you're thinking that you've found documentation on the format of resource files, guess again.
     * - There is no documentation on the semantics of these fields.
     * - This "keys" array isn't used anywhere, so this may not be the actual list of fields that's supported.
     * - These String literals are copied throughout this file (and others?) instead of using symbolic constants.
     */
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
        try{
        AnnotationParser.Annotation a = AnnotationParser.parse( line.trim() );

        ResourceAnnotation annotation = new ResourceAnnotation( a.getId() );

        annotation.source = a.get( "source" );
        annotation.author = a.get( "author" );
        annotation.license = a.get( "license" );
        annotation.notes = a.get( "notes" );
        annotation.same = a.get( "same" );
        annotation.licensefile = a.get( "licensefile" );
        return annotation;
        }catch(Exception e){
            System.out.println( "Error on line="+line );
            e.printStackTrace(  );
            AnnotationParser.Annotation a = AnnotationParser.parse( line.trim() );
            throw new RuntimeException( e);
        }

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
