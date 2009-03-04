package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.Font;
import java.util.Locale;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;


/**
 * A collection of utilities related to HTML.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HTMLUtils {
    
    private static final Font DEFAULT_FONT = new PhetFont();
    private static final String DEFAULT_CSS = "<head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>";
    
    /* not intended for instantiation */
    private HTMLUtils() {}
    
    /**
     * Creates an \<a\> tag with an href attribute.
     * The link is displayed.
     * @param url the URL to be loaded when the hyperlinked object is activated.
     * @return
     */
    public static String getHref( String url ) {
        return getHref( url, url );
    }
    
    /**
     * Creates an \<a\> tag with an href attribute.
     * @param url the URL to be loaded when the hyperlinked object is activated.
     * @param text the text to be displayed
     * @return
     */
    public static String getHref( String url, String text ) {
        return "<a href=" + url + ">" + text + "</a>";
    }
    
    /**
     * Creates an \<a\> tag that contains an href link to the PhET homepage.
     * The link is displayed.
     * @return
     */
    public static String getPhetHomeHref() {
        return getPhetHomeHref( PhetCommonConstants.PHET_HOME_URL );
    }
    
    /**
     * Creates an \<a\> tag that contains an href link to the PhET homepage.
     * @param text the text to be displayed
     * @return
     */
    public static String getPhetHomeHref( String text ) {
        return getHref( PhetCommonConstants.PHET_HOME_URL, text );
    }
    
    /**
     * Creates an \<a\> tag that contains a mailto link to the PhET help email address.
     * The email address is displayed.
     * @return
     */
    public static String getPhetMailtoHref() {
        return getPhetMailtoHref( PhetCommonConstants.PHET_EMAIL );
    }
    
    /**
     * Creates an \<a\> tag that contains a mailto link to the PhET help email address.
     * @param text the text to be displayed
     * @return
     */
    public static String getPhetMailtoHref( String text ) {
        return getHref( "mailto:" + PhetCommonConstants.PHET_EMAIL, text );
    }
    
    /**
     * Gets the URL for a simulation's web page on the PhET site.
     * @param project
     * @param sim
     * @param ampersand
     * @return
     */
    public static String getSimWebsiteURL( String project, String sim, String ampersand ) {
        return PhetCommonConstants.SIM_WEBSITE_REDIRECT_URL + "?" + 
            "request_version=" + PhetCommonConstants.SIM_WEBSITE_REDIRECT_VERSION + ampersand + 
            "project=" + project + ampersand + 
            "sim=" + sim;
    }


    /**
     * Gets the URL for a simulation's JAR file on the PhET site.
     *
     * @param project
     * @param sim
     * @param locale
     * @param ampersand
     * @return
     */
    public static String getSimJarURL( String project, String sim, Locale locale, String ampersand ) {
        String url = PhetCommonConstants.SIM_JAR_REDIRECT_URL + "?" + 
            "request_version=" + PhetCommonConstants.SIM_JAR_REDIRECT_VERSION + ampersand + 
            "project=" + project + ampersand + 
            "sim=" + sim + ampersand + 
            "language=" + locale.getLanguage();
        if ( !locale.getCountry().equals( "" ) ) {
            // add optional country code
            url += ampersand + "country=" + locale.getCountry();
        }
        return url;
    }

    /**
     * Returns the URL for the <project>_all.jar from the PhET site.
     * @param project
     * @param ampersand
     * @return
     */
    public static String getProjectJarURL( String project, String ampersand ) {
        return PhetCommonConstants.SIM_JAR_REDIRECT_URL + "?" +
            "request_version=" + PhetCommonConstants.SIM_JAR_REDIRECT_VERSION + ampersand + 
            "project=" + project;
    }
        
    public static String createStyledHTMLFromFragment( String htmlFragment ) {
        return createStyledHTMLFromFragment( htmlFragment, DEFAULT_FONT, DEFAULT_CSS );
    }
    
    public static String createStyledHTMLFromFragment( String htmlFragment, Font font ) {
        return createStyledHTMLFromFragment( htmlFragment, font, DEFAULT_CSS );
    }
    
    /**
     * Creates an HTML fragment that contains a CSS that sets font properties.
     * @param htmlFragment
     * @param font
     * @param css
     * @return
     */
    public static String createStyledHTMLFromFragment( String htmlFragment, Font font, String css ) {
        String html = "<html>" + css + htmlFragment + "</html>";
        return setFontInStyledHTML( html, font );
    }
    
    /**
     * Fills in font information in any HTML that contains CSS placeholders ala DEFAULT_CSS.
     * @param html
     * @param font
     * @return
     */
    public static String setFontInStyledHTML( String html, Font font ) {
        html = html.replaceAll( "@FONT_SIZE@", font.getSize() + "pt" );
        html = html.replaceAll( "@FONT_FAMILY@", font.getFamily() );
        return html;
    }
    
    /**
     * An HTML editor pane.
     * This is the base class for other types of HTML editor panes.
     * You need to add your own hyperlink behavior via addHyperlinkListener.
     */
    public static class HTMLEditorPane extends JEditorPane {
        public HTMLEditorPane( String html ) {
            setEditorKit( new HTMLEditorKit() );
            setText( html );
            setEditable( false );
        }
    }
    
    /**
     * An HTML editor pane that opens a web browser for hyperlinks.
     */
    public static class InteractiveHTMLPane extends HTMLEditorPane {
        public InteractiveHTMLPane( String html ) {
            super( html );
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        PhetServiceManager.showWebPage( e.getURL() );
                    }
                }
            } );
        }
    }
    
    /**
     * Combines an array of HTML strings, HTML fragments 
     * and plain text strings into a single HTML string.
     * 
     * @param strings
     * @return
     */
    public static String toHTMLString( String[] strings ) {
        StringBuffer buffer = new StringBuffer();
        buffer.append( "<html>" );
        for ( int i = 0; i < strings.length; i++ ) {
            String string = strings[i];
            string = string.replaceAll( "<html>", "" );
            string = string.replaceAll( "</html>", "" );
            buffer.append( string );
        }
        buffer.append( "</html>" );
        return buffer.toString();
    }
    
    /**
     * Converts a string to an HTML string.
     * If you have a plain-text string, this is a convenience method to add the \<html\> tag.
     * Sometimes we want to combine HTML strings, and we end up with too many \<html\> tags.
     * This method ensures that we only have \<html\> and \</html\> at the beginning and 
     * end of the string.
     * <p>
     * Example: "hello world" becomes "\<html\>hello world\</html\>".
     * <p>
     * Example "\<html\>foo\</html\> \<html\>bar\</html\>" becomes "\<html\>foo bar\</html\>".
     * 
     * @param string
     * @return
     */
    public static String toHTMLString( String string ) {
        String[] s = { string };
        return toHTMLString( s );
    }
}
