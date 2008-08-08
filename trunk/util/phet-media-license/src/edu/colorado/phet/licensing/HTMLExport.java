package edu.colorado.phet.licensing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.colorado.phet.licensing.media.ImageEntry;
import edu.colorado.phet.licensing.media.SimAssociations;
import edu.colorado.phet.licensing.media.AnnotatedRepository;

/**
 * Author: Sam Reid
 * Jun 15, 2007, 9:57:47 AM
 */
public class HTMLExport {
    private boolean showPhetImages = false;
    private boolean useSimList = false;

    public HTMLExport( boolean showPhetImages ) {
        this.showPhetImages = showPhetImages;
    }

    public void setShowPhetImages( boolean showPhetImages ) {
        this.showPhetImages = showPhetImages;
    }

    public void export( File file, ImageEntry[] imageEntries ) throws IOException {
//        updateSimAssociations( imageEntries );//do it once because it's expensive
        BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file ) );
        String title = "PhET Images";
        String content = "";
        for ( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];
            if ( shouldInclude( imageEntry ) ) {
                String imageLink = "<img src=\"annotated-data/" + imageEntry.getImageName() + "\" width=\"50\" height=\"50\">";
                content += "<a href=\"#" + imageEntry.getImageName() + "\">" + imageLink + "</a>";
            }
        }
        content += "<hr>";
        for ( int i = 0; i < imageEntries.length; i++ ) {
            ImageEntry imageEntry = imageEntries[i];

            if ( shouldInclude( imageEntry ) ) {
                String simList = useSimList ? getSimList( imageEntry ) : "";
//                String simList = i<50? getSimList( imageEntry ) : "";
                System.out.println( "simList = " + simList );
                content += "<a name=\"" + imageEntry.getImageName() + "\">" + imageEntry.getImageName() + "</a>" +
                           "<br>" +
                           "<img src=\"annotated-data/" + imageEntry.getImageName() + "\">" +
                           "<br>" +
                           "source=" + getSourceText( imageEntry ) + "<br>" +
                           "notes=" + imageEntry.getNotes() + "<br>" +
                           simList +
                           "<br><br><br><hr>";
            }
        }
        bufferedWriter.write( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" +
                              "    \"http://www.w3.org/TR/html4/loose.dtd\">\n" +
                              "<html>\n" +
                              "<head>\n" +
                              "  <title>" + title + "</title>\n" +
                              "</head>\n" +
                              "<body>\n" +
                              "\n" +
                              content +
                              "\n" +
                              "</body>\n" +
                              "</html>" );

        bufferedWriter.close();
    }

    private String getSimList( ImageEntry imageEntry ) {
        SimAssociations simAssociations = new SimAssociations( new ImageEntry[0] );
        File[] f = simAssociations.getAssociations( imageEntry );

        String html = "usages:</br><ul>";
        for ( int i = 0; i < f.length; i++ ) {
            File file = f[i];
            html += "<li>" + toName( file ) + "</li>";
        }
        html += "</ul>";
        return html;
    }

    private String toName( File file ) {
        String abs = file.getAbsolutePath();
        int prefix = abs.lastIndexOf( "simulations-java" );
        return abs.substring( prefix + "simulations-java".length() + 1 );
    }

    private boolean shouldInclude( ImageEntry imageEntry ) {
        return !imageEntry.isDone();
//        return showPhetImages || imageEntry.isNonPhet();
    }

    private String getSourceText( ImageEntry imageEntry ) {
        if ( !imageEntry.isNonPhet() ) {
            return "phet";
        }
        else {
            return imageEntry.getSource();
        }
    }

    public static void main( String[] args ) throws IOException {
        ImageEntry[] entries = AnnotatedRepository.loadAnnotatedEntries();

        new HTMLExport( false ).export( new File( "phet-media.html" ), entries );
    }
}