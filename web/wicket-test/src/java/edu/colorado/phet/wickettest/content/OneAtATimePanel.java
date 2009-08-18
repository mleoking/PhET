package edu.colorado.phet.wickettest.content;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;

public class OneAtATimePanel extends PhetPanel {
    public OneAtATimePanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "get-phet.one-at-a-time";
    }

    public static String getUrl() {
        return "get-phet/one-at-a-time";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}