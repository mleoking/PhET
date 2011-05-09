package edu.colorado.phet.website.content.contribution;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionGuidelinesPanel extends PhetPanel {
    public ContributionGuidelinesPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_MAIN ) );

    }

    public static String getKey() {
        return "teacherIdeas.guide";
    }

    public static String getUrl() {
        return "for-teachers/activity-guide";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}