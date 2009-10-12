package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.ContributePanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutContactPanel extends PhetPanel {
    public AboutContactPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-contact-licensingText", "about.contact.licensingText", new Object[]{
                AboutLicensingPanel.getLinker().getHref( context )
        } ) );

        add( new LocalizedText( "about-contact-correspondence", "about.contact.correspondence", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\">phethelp@colorado.edu</a>",
                ContributePanel.getLinker().getHref( context )
        } ) );
    }

    public static String getKey() {
        return "about.contact";
    }

    public static String getUrl() {
        return "about/contact";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}