package edu.colorado.phet.website.content;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.content.contribution.ContributionBrowsePage;
import edu.colorado.phet.website.content.contribution.ContributionCreatePage;
import edu.colorado.phet.website.content.contribution.ContributionGuidelinesPanel;
import edu.colorado.phet.website.content.simulations.CategoryPage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TeacherIdeasPanel extends PhetPanel {
    public TeacherIdeasPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "welcome", "teacherIdeas.welcome", new Object[]{
                CategoryPage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "start", "teacherIdeas.start", new Object[]{
                ContributionBrowsePage.getLinker().getHref( context, getPhetCycle() ),
                CategoryPage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "contribute", "teacherIdeas.contribute", new Object[]{
                ContributionCreatePage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "guidelines", "teacherIdeas.guidelines", new Object[]{
                ContributionGuidelinesPanel.getLinker().getHref( context, getPhetCycle() ),
                Linkers.CONTRIBUTION_GUIDELINES_PDF.getHref( context, getPhetCycle() )
        } ) );

    }

    public static String getKey() {
        return "teacherIdeas";
    }

    public static String getUrl() {
        return "for-teachers";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}