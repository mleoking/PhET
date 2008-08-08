package edu.colorado.phet.licensing;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by: Sam
 * Aug 7, 2008 at 9:40:14 PM
 */
public class ResourceAnnotationList {
    private ArrayList lines = new ArrayList();

    public ResourceAnnotationList() {
    }

    public void addResourceAnnotation( ResourceAnnotation resourceAnnotation ) {
        lines.add( resourceAnnotation );
    }

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
            return ResourceAnnotation.parseElement( line );
        }
    }

    public static void main( String[] args ) {
        File file = new File( "C:\\reid-not-backed-up\\phet\\svn\\trunk2\\util\\phet-media-license\\data\\license.txt" );
        ResourceAnnotationList list = read( file );
        System.out.println( list.toText() );
    }
}
