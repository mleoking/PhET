package edu.colorado.phet.licensing;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by: Sam
 * Aug 7, 2008 at 9:40:14 PM
 */
public class ResourceAnnotationList {
    
    private final ArrayList<ResourceAnnotationElement> lines = new ArrayList<ResourceAnnotationElement>();
    private final String description;

    public ResourceAnnotationList( String description ) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    public void addResourceAnnotation( ResourceAnnotation resourceAnnotation ) {
        lines.add( resourceAnnotation );
    }

    //preserve blank lines and # comment lines
    public void addTextLine( ResourceAnnotationTextLine resourceAnnotationTextLine ) {
        lines.add( resourceAnnotationTextLine );
    }

    public void addTextLine( String text ) {
        addTextLine( new ResourceAnnotationTextLine( text ) );
    }

    public void save( File file ) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
            bufferedWriter.write( toText() );
            bufferedWriter.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String toText() {
        String text = "";
        for ( int i = 0; i < lines.size(); i++ ) {
            ResourceAnnotationElement resourceAnnotationTextLine = (ResourceAnnotationElement) lines.get( i );
            text += ( resourceAnnotationTextLine.toText() );
            text += ( "\n" );
        }

        return text;
    }

    public static ResourceAnnotationList read( File file ) {
        try {
            ResourceAnnotationList list = new ResourceAnnotationList( "File: " + file.getAbsolutePath() );
            BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                ResourceAnnotationElement element = parseElement( line );
                list.lines.add( element );

                line = bufferedReader.readLine();
            }
            return list;
        }
        catch( FileNotFoundException e ) {
            throw new RuntimeException( e );
        }
        catch( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private static ResourceAnnotationElement parseElement( String line ) {
        if ( line.trim().length() == 0 || line.trim().startsWith( "#" ) ) {
            return new ResourceAnnotationTextLine( line );
        }
        else {
            return new ResourceAnnotation( line );
        }
    }

    // this is a unit test
    public static void main( String[] args ) {
        File trunk = Config.getTrunkPath(args);
        File testFile = new File( trunk, "/simulations-java/simulations/glaciers/data/glaciers/images/license.txt" ); // some test file that we know exists
        ResourceAnnotationList list = read( testFile );
        System.out.println( list.toText() );
    }

    public int getAnnotationCount() {
        int count = 0;
        for ( int i = 0; i < lines.size(); i++ ) {
            ResourceAnnotationElement resourceAnnotationElement = (ResourceAnnotationElement) lines.get( i );
            if ( resourceAnnotationElement instanceof ResourceAnnotation ) {
                count++;
            }
        }
        return count;
    }

    public ResourceAnnotation getEntry( String name ) {
        ArrayList<ResourceAnnotationElement> elements = new ArrayList<ResourceAnnotationElement>();
        for ( int i = 0; i < lines.size(); i++ ) {
            ResourceAnnotationElement element = (ResourceAnnotationElement) lines.get( i );
            if ( element instanceof ResourceAnnotation && ( (ResourceAnnotation) element ).getName().equals( name ) ) {
                elements.add( element );
            }
        }
        if ( elements.size() == 0 ) {
//            throw new RuntimeException( "Element not found for name=" + name + ", resourceList=" + toText() );
//            new RuntimeException( "Element not found for name=" + name +" in "+description).printStackTrace(  );
            ResourceAnnotation annotation = new ResourceAnnotation( name );
            annotation.setSource( "NOT_ANNOTATED" );
            return annotation;
        }
        else if ( elements.size() > 1 ) {
            System.out.println( "Multiple elements found: " + elements );
        }
        return (ResourceAnnotation) elements.get( 0 );
    }
}
