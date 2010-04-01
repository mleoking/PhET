package edu.colorado.phet.website.content;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.content.contribution.ContributePanel;
import edu.colorado.phet.website.content.contribution.ContributionBrowsePage;
import edu.colorado.phet.website.content.simulations.SimulationDisplay;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.getphet.OneAtATimePanel;
import edu.colorado.phet.website.content.getphet.RunOurSimulationsPanel;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.TranslationLinksPanel;
import edu.colorado.phet.website.translation.TranslationMainPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * The panel which represents the main content portion of the home (index) page
 */
public class IndexPanel extends PhetPanel {
    public IndexPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "ksu-logo", "/images/sponsors/ECSME-combined-logo-small.jpg", null ) );
        //add( new StaticImage( "jila-logo", "/images/sponsors/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/sponsors/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/sponsors/hewlett-logo-small.jpg", null ) );

        PhetLink imageLink = SimulationDisplay.createLink( "image-link", context );
        add( imageLink );
        imageLink.add( new StaticImage( "index-animated-screenshot", "/images/mass-spring-lab-animated-screenshot.gif", null ) );

        add( new LocalizedText( "index-main-text", "home.subheader", new Object[]{"href=\"" + ResearchPanel.getLinker().getRawUrl( context, getPhetCycle() ) + "\""} ) );

        add( SimulationDisplay.createLink( "play-sims-link", context ) );

        add( RunOurSimulationsPanel.getLinker().getLink( "run-our-sims-link", context, getPhetCycle() ) );
        add( SimulationDisplay.createLink( "on-line-link", context ) );
        add( FullInstallPanel.getLinker().getLink( "full-install-link", context, getPhetCycle() ) );
        add( OneAtATimePanel.getLinker().getLink( "one-at-a-time-link", context, getPhetCycle() ) );

        add( WorkshopsPanel.getLinker().getLink( "workshops-link", context, getPhetCycle() ) );

        add( ContributePanel.getLinker().getLink( "contribute-link", context, getPhetCycle() ) );
        add( ContributePanel.getLinker().getLink( "support-phet-link", context, getPhetCycle() ) );

        add( SimulationDisplay.createLink( "browse-sims-link", context ) );

        add( SimulationDisplay.createLink( "below-simulations-link", context ) );

        if ( context.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) && DistributionHandler.displayTranslationEditLink( (PhetRequestCycle) getRequestCycle() ) ) {
            add( new BookmarkablePageLink( "test-translation", TranslationMainPage.class ) );
        }
        else {
            add( new InvisibleComponent( "test-translation" ) );
        }

        if ( DistributionHandler.displayTranslationLinksPanel( (PhetRequestCycle) getRequestCycle() ) ) {
            add( new TranslationLinksPanel( "translation-links", context ) );
        }
        else {
            add( new InvisibleComponent( "translation-links" ) );
        }

        if ( DistributionHandler.redirectActivities( (PhetRequestCycle) getRequestCycle() ) ) {
            add( new PhetLink( "activities-link", "http://phet.colorado.edu/teacher_ideas/index.php" ) );
            add( new PhetLink( "browse-activities-link", "http://phet.colorado.edu/teacher_ideas/browse.php" ) );
            add( new PhetLink( "submit-activity-link", "http://phet.colorado.edu/teacher_ideas/index.php" ) );
        }
        else {
            // TODO: what to do when we use a distribution that doesn't want this link?
            add( TeacherIdeasPanel.getLinker().getLink( "activities-link", context, getPhetCycle() ) );
            add( ContributionBrowsePage.getLinker().getLink( "browse-activities-link", context, getPhetCycle() ) );
            add( TeacherIdeasPanel.getLinker().getLink( "submit-activity-link", context, getPhetCycle() ) );
        }

        add( HeaderContributor.forCss( "/css/home-v1.css" ) );

        Link miniLink = SimulationDisplay.createLink( "mini-screenshot-link", context );
        add( miniLink );
        miniLink.add( new StaticImage( "mini-screenshot", "/images/geometric-optics-screenshot.png", null ) );
    }

}
