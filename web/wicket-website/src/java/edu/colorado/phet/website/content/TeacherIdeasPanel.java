package edu.colorado.phet.website.content;

import edu.colorado.phet.website.components.LocalizedText;
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
        }));

        // TODO: localize, fix links

//        add( SimulationDisplay.createLink( "online-link", context ) );
//        add( FullInstallPanel.getLinker().getLink( "install-link", context, getPhetCycle() ) );
//        add( OneAtATimePanel.getLinker().getLink( "offline-link", context, getPhetCycle() ) );
//
//        add( new LocalizedText( "get-phet-install-header", "get-phet.install.header" ) );
//        add( new LocalizedText( "get-phet-offline-header", "get-phet.offline.header" ) );
//
//        add( new LocalizedText( "get-phet-install-howToGet", "get-phet.install.howToGet", new Object[]{
//                FullInstallPanel.getLinker().getHref( context, getPhetCycle() )
//        } ) );
//
//        add( new LocalizedText( "get-phet-offline-howToGet", "get-phet.offline.howToGet", new Object[]{
//                OneAtATimePanel.getLinker().getHref( context, getPhetCycle() )
//        } ) );

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