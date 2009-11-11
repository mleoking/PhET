package edu.colorado.phet.website.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TroubleshootingJavascriptPanel extends PhetPanel {
    public TroubleshootingJavascriptPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );

        add( new LocalizedText( "troubleshooting-javascript-notJava", "troubleshooting.javascript.notJava", new Object[]{
                TroubleshootingJavaPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-javascript-notify", "troubleshooting.javascript.notify", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\"><span class=\"red\">phethelp@colorado.edu</span></a>"
        } ) );

        Label javascriptYesLabel = new Label( "troubleshooting-javascript-q1-yes",
                                              "\n/* <![CDATA[ */\n" +
                                              //"document.write(\"" + getLocalizer().getString( "troubleshooting.javascript.q1.yes", this ) + "\");"
                                              "document.getElementById( \"javascript-ok\" ).style.display = \"block\";\n" +
                                              "document.getElementById( \"javascript-ok\" ).innerHTML = \"" + getLocalizer().getString( "troubleshooting.javascript.q1.yes", this ) + "\";\n"
                                              + "\n/* ]]> */\n" );
        javascriptYesLabel.setEscapeModelStrings( false );
        add( javascriptYesLabel );

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