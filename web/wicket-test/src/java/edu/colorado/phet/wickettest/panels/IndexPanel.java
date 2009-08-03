package edu.colorado.phet.wickettest.panels;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;

import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.translation.TranslationTestPage;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetLink;
import edu.colorado.phet.wickettest.util.StaticImage;

public class IndexPanel extends PhetPanel {
    public IndexPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "ksu-logo", "/images/ksu-logo.gif", null ) );
        add( new StaticImage( "jila-logo", "/images/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/hewlett-logo-small.jpg", null ) );

        PhetLink imageLink = SimulationDisplay.createLink( "image-link", context.getLocale() );
        add( imageLink );
        imageLink.add( new StaticImage( "index-animated-screenshot", "/images/mass-spring-lab-animated-screenshot.gif", null ) );

        add( SimulationDisplay.createLink( "play-sims-link", context.getLocale() ) );

        add( new BookmarkablePageLink( "test-translation", TranslationTestPage.class ) );

//        add( new LocalizedLabel( "test-en", LocaleUtils.stringToLocale( "en" ), new ResourceModel( "language.name" ) ) );
//        add( new LocalizedLabel( "test-ar", LocaleUtils.stringToLocale( "ar" ), new ResourceModel( "language.name" ) ) );

        add( new TranslationLinksPanel( "translation-links", context ) );
    }

}
