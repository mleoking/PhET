package edu.colorado.phet.wickettest.panels;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.components.StaticImage;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.content.WorkshopsPanel;
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

        add( WorkshopsPanel.getLinker().getLink( "workshops-link", context ) );

        add( SimulationDisplay.createLink( "play-sims-link", context ) );

        add( new BookmarkablePageLink( "test-translation", TranslationMainPage.class ) );

        add( new TranslationLinksPanel( "translation-links", context ) );

        add( HeaderContributor.forCss( "/css/home-v1.css" ) );


        add( SimulationDisplay.createLink( "on-line-link", context ) );

        add( new StaticImage( "mini-screenshot", "/images/geometric-optics-screenshot.png", null ) );
    }

}
