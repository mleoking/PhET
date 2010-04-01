package edu.colorado.phet.website.content.contribution;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionGuidelinesPanel extends PhetPanel {
    public ContributionGuidelinesPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/contribution-main-v1.css" ) );

        // TODO: localize

    }

    public static String getKey() {
        return "teacherIdeas.guide";
    }

    public static String getUrl() {
        return "contributions/guide";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}