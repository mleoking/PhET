package edu.colorado.phet.wickettest.content.about;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.content.ContributePanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

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