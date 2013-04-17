package edu.colorado.phet.licensing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by: Sam
 * Aug 7, 2008 at 9:40:14 PM
 */
public class ResourceAnnotationList {

    private final ArrayList<ResourceAnnotationElement> lines = new ArrayList<ResourceAnnotationElement>();

    //preserve blank lines and # comment lines
    public void addTextLine( ResourceAnnotationTextLine resourceAnnotationTextLine ) {
        lines.add( resourceAnnotationTextLine );
    }

    public void save( File file ) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
            bufferedWriter.write( toText() );
            bufferedWriter.close();
        }
        catch ( IOException e ) {
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
            ResourceAnnotationList list = new ResourceAnnotationList();
            BufferedReader bufferedReader = new BufferedReader( new FileReader( file ) );
            String line = bufferedReader.readLine();
            while ( line != null ) {
                ResourceAnnotationElement element = parseElement( line );
                list.lines.add( element );

                line = bufferedReader.readLine();
            }
            return list;
        }
        catch ( FileNotFoundException e ) {
            throw new RuntimeException( e );
        }
        catch ( IOException e ) {
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
        if ( args.length != 1 ) {
            System.out.println( "usage: " + ResourceAnnotationList.class.getName() + " absolute_path_to_trunk" );
            System.exit( 1 );
        }
        File trunk = new TrunkDirectory( args[0] );
        File testFile = new File( trunk, "/simulations-java/simulations/glaciers/data/glaciers/images/license.txt" ); // some test file that we know exists
        ResourceAnnotationList list = read( testFile );
        System.out.println( list.toText() );
    }

    public int getAnnotationCount() {
        int count = 0;
        for ( int i = 0; i < lines.size(); i++ ) {
            ResourceAnnotationElement resourceAnnotationElement = lines.get( i );
            if ( resourceAnnotationElement instanceof ResourceAnnotation ) {
                count++;
            }
        }
        return count;
    }

    public ResourceAnnotation getEntry( String name ) {
        ArrayList<ResourceAnnotationElement> elements = new ArrayList<ResourceAnnotationElement>();
        for ( int i = 0; i < lines.size(); i++ ) {
            ResourceAnnotationElement element = lines.get( i );
            if ( element instanceof ResourceAnnotation && ( (ResourceAnnotation) element ).getName().equals( name ) ) {
                elements.add( element );
            }
        }
        if ( elements.size() == 0 ) {
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
