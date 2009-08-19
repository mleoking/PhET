package edu.colorado.phet.wickettest.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class TroubleshootingJavascriptPanel extends PhetPanel {
    public TroubleshootingJavascriptPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/troubleshooting-v1.css" ) );

    }

    public static String getKey() {
        return "troubleshooting.javascript";
    }

    public static String getUrl() {
        return "troubleshooting/javascript";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}