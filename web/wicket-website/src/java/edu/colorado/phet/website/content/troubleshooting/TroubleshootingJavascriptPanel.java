package edu.colorado.phet.website.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLabel;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TroubleshootingJavascriptPanel extends PhetPanel {
    public TroubleshootingJavascriptPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.TROUBLESHOOTING ) );

        add( new LocalizedText( "troubleshooting-javascript-notJava", "troubleshooting.javascript.notJava", new Object[]{
                TroubleshootingJavaPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-javascript-notify", "troubleshooting.javascript.notify", new Object[]{
                Linkers.PHET_HELP_LINK
        } ) );

        add( new RawLabel( "troubleshooting-javascript-q1-yes",
                           "\n/* <![CDATA[ */\n" +
                           "document.getElementById( \"javascript-ok\" ).style.display = \"block\";\n" +
                           "document.getElementById( \"javascript-ok\" ).innerHTML = \"" + HtmlUtils.encode( getLocalizer().getString( "troubleshooting.javascript.q1.yes", this ) ).replace( "\"", "&quot;" ) + "\";\n"
                           + "\n/* ]]> */\n" ) );

        add( new LocalizedText( "troubleshooting-javascript-q2-answer", "troubleshooting.javascript.q2.answer" ) );

        add( new LocalizedText( "troubleshooting-javascript-q3-answer", "troubleshooting.javascript.q3.answer", new Object[]{
                "href=\"#q1\""
        } ) );

        add( new LocalizedText( "troubleshooting-javascript-q4-answer", "troubleshooting.javascript.q4.answer", new Object[]{
                "href=\"#q1\""
        } ) );

        add( new LocalizedText( "troubleshooting-javascript-q5-answer", "troubleshooting.javascript.q5.answer" ) );

    }

    public static String getKey() {
        return "troubleshooting.javascript";
    }

    public static String getUrl() {
        return "troubleshooting/javascript";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, TroubleshootingJavascriptPanel.class ) ) {
                    return "http://phet.colorado.edu/tech_support/support-javascript.php";
                }
                else {
                    return super.getRawUrl( context, cycle );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}