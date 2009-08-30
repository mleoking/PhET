package edu.colorado.phet.wickettest.content;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.components.StaticImage;
import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.TranslationLinksPanel;
import edu.colorado.phet.wickettest.translation.TranslationMainPage;
import edu.colorado.phet.wickettest.util.PageContext;

public class IndexPanel extends PhetPanel {
    public IndexPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "ksu-logo", "/images/ECSME-combined-logo-small.jpg", null ) );
        add( new StaticImage( "jila-logo", "/images/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/hewlett-logo-small.jpg", null ) );

        PhetLink imageLink = SimulationDisplay.createLink( "image-link", context );
        add( imageLink );
        imageLink.add( new StaticImage( "index-animated-screenshot", "/images/mass-spring-lab-animated-screenshot.gif", null ) );

        // TODO: refactor out context.getPrefix(), etc
        add( new LocalizedText( "index-main-text", "home.subheader", new Object[]{ "href=\"" + context.getPrefix() + ResearchPanel.getUrl() + "\"" } ) );

        add( SimulationDisplay.createLink( "play-sims-link", context ) );

        add( RunOurSimulationsPanel.getLinker().getLink( "run-our-sims-link", context ) );
        add( SimulationDisplay.createLink( "on-line-link", context ) );
        add( FullInstallPanel.getLinker().getLink( "full-install-link", context ) );
        add( OneAtATimePanel.getLinker().getLink( "one-at-a-time-link", context ) );

        add( WorkshopsPanel.getLinker().getLink( "workshops-link", context ) );

        add( ContributePanel.getLinker().getLink( "contribute-link", context ) );
        add( ContributePanel.getLinker().getLink( "support-phet-link", context ) );

        add( SimulationDisplay.createLink( "browse-sims-link", context ) );

        add( SimulationDisplay.createLink( "below-simulations-link", context ) );

        add( new BookmarkablePageLink( "test-translation", TranslationMainPage.class ) );

        add( new TranslationLinksPanel( "translation-links", context ) );

        add( HeaderContributor.forCss( "/css/home-v1.css" ) );

        Link miniLink = SimulationDisplay.createLink( "mini-screenshot-link", context );
        add( miniLink );
        miniLink.add( new StaticImage( "mini-screenshot", "/images/geometric-optics-screenshot.png", null ) );
    }

}
