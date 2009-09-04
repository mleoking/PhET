package edu.colorado.phet.wickettest.content.about;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.Linkable;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.content.ContributePanel;

public class AboutContactPanel extends PhetPanel {
    public AboutContactPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-contact-licensingText", "about.contact.licensingText", new Object[]{
                "href=\"" + context.getPrefix() + AboutLicensingPanel.getUrl() + "\""
        }));

        add( new LocalizedText( "about-contact-correspondence", "about.contact.correspondence", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>",
                "href=\"" + context.getPrefix() + ContributePanel.getUrl() + "\""
        }));
    }

    public static String getKey() {
        return "about.contact";
    }

    public static String getUrl() {
        return "about/contact";
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, context.getPrefix() + getUrl() );
            }
        };
    }
}