package edu.colorado.phet.website.content;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.content.about.*;
import edu.colorado.phet.website.content.contribution.ContributionBrowsePage;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.content.getphet.OneAtATimePanel;
import edu.colorado.phet.website.content.getphet.RunOurSimulationsPanel;
import edu.colorado.phet.website.content.simulations.CategoryPage;
import edu.colorado.phet.website.content.workshops.WorkshopsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.RotatorFallbackPanel;
import edu.colorado.phet.website.panels.RotatorPanel;
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

        // TODO: link these image URLs from the constants area
        add( new StaticImage( "ksu-logo", "/images/support/ECSME-combined-logo-small.jpg", null ) );
        //add( new StaticImage( "jila-logo", "/images/support/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/support/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/support/hewlett-logo-small.jpg", null ) );

        /* ADD THE FOLLOWING IN FOR THE SOCIAL MEDIA BUTTONS
        <!--<div style="text-align: center; font-size: 11px; vertical-align:middle; position: relative; padding-bottom: 10px;">-->
            <!--<a href="http://www.facebook.com/sharer.php?u=http%3A%2F%2Fphet.colorado.edu&t=PhET%3A%20Free%20online%20physics%2C%20chemistry%2C%20biology%2C%20earth%20science%20and%20math%20simulations">-->
            <!--Like <img wicket:id="facebook-like"-->
            <!--src="../../../../../../../root/images/icons/social/facebook-like.gif" alt="Facebook icon"-->
            <!--style="vertical-align: middle; border: none;"/>-->
            <!--</a> &nbsp;|&nbsp;-->
            <!--<a href="https://www.facebook.com/pages/PhET-Interactive-Simulations/87753699983">-->
            <!--Join us on <img wicket:id="facebook-image"-->
            <!--src="../../../../../../../root/images/icons/social/facebook.png" alt="Facebook icon"-->
            <!--style="vertical-align: middle; border: none;"/>-->
            <!--</a> &nbsp;|&nbsp;-->
            <!--<a href="http://twitter.com/PhETSims">-->
            <!--Follow us on <img wicket:id="twitter-image"-->
            <!--src="../../../../../../../root/images/icons/social/twitter.png" alt="Twitter icon"-->
            <!--style="vertical-align: middle; border: none;"/>-->
            <!--</a> &nbsp;|&nbsp;-->
            <!--<a href="http://phet.colorado.edu/blog/">-->
            <!--Read our blog-->
            <!--</a>-->
            <!--</div>-->
         */
//        add( new StaticImage( "facebook-like", "/images/icons/social/facebook-like.gif", null));
//        add( new StaticImage( "facebook-image", "/images/icons/social/facebook.png", null));
//        add( new StaticImage( "twitter-image", "/images/icons/social/twitter.png", null));

        add( new LocalizedText( "index-main-text", "home.subheader", new Object[]{
                ResearchPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( CategoryPage.getLinker().getLink( "play-sims-link", context, getPhetCycle() ) );

        add( RunOurSimulationsPanel.getLinker().getLink( "run-our-sims-link", context, getPhetCycle() ) );
        add( CategoryPage.getLinker().getLink( "on-line-link", context, getPhetCycle() ) );
        add( FullInstallPanel.getLinker().getLink( "full-install-link", context, getPhetCycle() ) );
        add( OneAtATimePanel.getLinker().getLink( "one-at-a-time-link", context, getPhetCycle() ) );

        add( WorkshopsPanel.getLinker().getLink( "workshops-link", context, getPhetCycle() ) );

        add( TeacherIdeasPanel.getLinker().getLink( "contribute-link", context, getPhetCycle() ) );
        add( DonatePanel.getLinker().getLink( "support-phet-link", context, getPhetCycle() ) );
        add( TranslationUtilityPanel.getLinker().getLink( "translate-sims-link", context, getPhetCycle() ) );

        add( AboutMainPanel.getLinker().getLink( "about-general", context, getPhetCycle() ) );
        add( AboutMainPanel.getLinker().getLink( "about-phet", context, getPhetCycle() ) );
        add( AboutNewsPanel.getLinker().getLink( "about-news", context, getPhetCycle() ) );
        add( AboutContactPanel.getLinker().getLink( "about-contact", context, getPhetCycle() ) );
        add( AboutSponsorsPanel.getLinker().getLink( "about-sponsors", context, getPhetCycle() ) );

        //add( CategoryPage.createLink( "browse-sims-link", context ) );

        //add( CategoryPage.createLink( "below-simulations-link", context ) );

        //add( AboutMainPanel.getLinker().getLink( "home-about-phet-link", context, getPhetCycle() ) );

        if ( context.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) && DistributionHandler.displayTranslationEditLink( (PhetRequestCycle) getRequestCycle() ) ) {
            add( TranslationMainPage.getLinker().getLink( "test-translation", context, getPhetCycle() ) );
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
            add( new RawLink( "activities-link", "http://phet.colorado.edu/teacher_ideas/index.php" ) );
            add( new RawLink( "browse-activities-link", "http://phet.colorado.edu/teacher_ideas/browse.php" ) );
            add( new RawLink( "submit-activity-link", "http://phet.colorado.edu/teacher_ideas/index.php" ) );
        }
        else {
            add( TeacherIdeasPanel.getLinker().getLink( "activities-link", context, getPhetCycle() ) );
            if ( getPhetCycle().isOfflineInstaller() ) {
                add( new InvisibleComponent( "browse-activities-link" ) );
            }
            else {
                add( ContributionBrowsePage.getLinker().getLink( "browse-activities-link", context, getPhetCycle() ) );
            }
            add( TeacherIdeasPanel.getLinker().getLink( "submit-activity-link", context, getPhetCycle() ) );
        }

        add( HeaderContributor.forCss( CSS.HOME ) );

        //Link miniLink = CategoryPage.createLink( "mini-screenshot-link", context );
        //add( miniLink );
        //miniLink.add( new StaticImage( "mini-screenshot", "/images/geometric-optics-screenshot.png", null ) );

        if ( DistributionHandler.showRotatorFallback( getPhetCycle() ) ) {
            add( new RotatorFallbackPanel( "rotator-panel", context ) );
        }
        else {
            add( new RotatorPanel( "rotator-panel", context ) );
        }

        add( AboutLicensingPanel.getLinker().getLink( "some-rights-link", context, getPhetCycle() ) );
    }

}
