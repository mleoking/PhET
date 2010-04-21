package edu.colorado.phet.website.content;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.contribution.ContributionBrowsePage;
import edu.colorado.phet.website.content.contribution.ContributionCreatePage;
import edu.colorado.phet.website.content.contribution.ContributionGuidelinesPanel;
import edu.colorado.phet.website.content.simulations.SimulationDisplay;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TeacherIdeasPanel extends PhetPanel {
    public TeacherIdeasPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "welcome", "teacherIdeas.welcome", new Object[]{
                SimulationDisplay.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "start", "teacherIdeas.start", new Object[]{
                ContributionBrowsePage.getLinker().getHref( context, getPhetCycle() ),
                SimulationDisplay.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "contribute", "teacherIdeas.contribute", new Object[]{
                ContributionCreatePage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "guidelines", "teacherIdeas.guidelines", new Object[]{
                ContributionGuidelinesPanel.getLinker().getHref( context, getPhetCycle() ),
                "href=\"/publications/activities-guide/contribution-guidelines.pdf\""
        } ) );

    }

    public static String getKey() {
        return "teacherIdeas";
    }

    public static String getUrl() {
        return "contributions";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}