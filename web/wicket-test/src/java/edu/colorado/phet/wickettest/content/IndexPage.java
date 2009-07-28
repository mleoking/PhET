package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.translation.TranslationTestPage;
import edu.colorado.phet.wickettest.util.*;

public class IndexPage extends PhetPage {
    public IndexPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( "Wicket test index page" );

        add( new StaticImage( "ksu-logo", "/images/ksu-logo.gif", null ) );
        add( new StaticImage( "jila-logo", "/images/jila_logo_small.gif", null ) );
        add( new StaticImage( "nsf-logo", "/images/nsf-logo-small.gif", null ) );
        add( new StaticImage( "hewlett-logo", "/images/hewlett-logo-small.jpg", null ) );

        PhetLink imageLink = SimulationDisplay.createLink( "image-link", LocaleUtils.stringToLocale( "en" ) );
        add( imageLink );
        imageLink.add( new StaticImage( "index-animated-screenshot", "/images/mass-spring-lab-animated-screenshot.gif", null ) );

        add( SimulationDisplay.createLink( "play-sims-link", LocaleUtils.stringToLocale( "en" ) ) );
        add( SimulationDisplay.createLink( "es-simulations", LocaleUtils.stringToLocale( "es" ) ) );
        add( SimulationDisplay.createLink( "el-simulations", LocaleUtils.stringToLocale( "el" ) ) );
        add( SimulationDisplay.createLink( "ar-simulations", LocaleUtils.stringToLocale( "ar" ) ) );

        add( new BookmarkablePageLink( "test-translation", TranslationTestPage.class ) );
    }

    public static PhetLink createLink( String id ) {
        return new PhetLink( id, "/" );
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return new PhetLink( id, "/" );
            }
        };
    }
}