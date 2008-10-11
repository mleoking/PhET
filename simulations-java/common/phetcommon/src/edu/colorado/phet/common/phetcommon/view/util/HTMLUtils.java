package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.Font;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
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
     * Gets the URL for simulations web page on the PhET site.
     * @param project
     * @param sim
     * @return
     */
    public static String getSimURL( String project, String sim ) {
        return PhetCommonConstants.PHET_HOME_URL + "/simulations/sim-redirect.php?project=" + project + "&sim=" + sim;
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
     * An editor pane that contains interactive HTML links.
     */
    public static class InteractiveHTMLPane extends JEditorPane {
        public InteractiveHTMLPane( String html ) {
            setEditorKit( new HTMLEditorKit() );
            setText( html );
            setEditable( false );
            setBackground( new JPanel().getBackground() );
            addHyperlinkListener( new HyperlinkListener() {
                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                        PhetServiceManager.showWebPage( e.getURL() );
                    }
                }
            } );
        }
    }
}
