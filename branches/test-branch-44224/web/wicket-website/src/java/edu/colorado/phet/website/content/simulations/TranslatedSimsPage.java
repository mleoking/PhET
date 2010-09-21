package edu.colorado.phet.website.content.simulations;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.cache.SimplePanelCacheEntry;
import edu.colorado.phet.website.content.NotFoundPage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class TranslatedSimsPage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( TranslatedSimsPage.class.getName() );

    public TranslatedSimsPage( PageParameters parameters ) {
        super( parameters );

        boolean hasLocale = parameters.containsKey( "translationlocale" );

        if ( hasLocale ) {
            String localeName = parameters.getString( "translationlocale" );

            if ( localeName == null ) {
                throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
            }

            final Locale locale = LocaleUtils.stringToLocale( localeName );

            if ( locale == null ) {
                throw new RestartResponseAtInterceptPageException( NotFoundPage.class );
            }

            String localeKey = "language.names." + localeName;
            initializeLocation( getNavMenu().getLocationByKey( localeKey ) );

            String title = StringUtils.messageFormat( getPhetLocalizer().getString( "simulations.translated.language.title", this ), new Object[]{getPhetLocalizer().getString( localeKey, this )} );
            addTitle( title );

            PhetPanel panel = new SimplePanelCacheEntry( TranslationListPanel.class, null, getPageContext().getLocale(), getMyPath(), getPhetCycle() ) {
                public PhetPanel constructPanel( String id, PageContext context ) {
                    return new TranslationListPanel( id, context, locale );
                }
            }.instantiate( "panel", getPageContext(), getPhetCycle() );
            add( panel );
        }
        else {
            initializeLocation( getNavMenu().getLocationByKey( "simulations.translated" ) );

            addTitle( new ResourceModel( "simulations.translated.title" ) );

            //add( new TranslationLocaleListPanel( "panel", getPageContext() ) );
            PhetPanel panel = new SimplePanelCacheEntry( TranslationLocaleListPanel.class, null, getPageContext().getLocale(), getMyPath(), getPhetCycle() ) {
                public PhetPanel constructPanel( String id, PageContext context ) {
                    return new TranslationLocaleListPanel( id, context );
                }
            }.instantiate( "panel", getPageContext(), getPhetCycle() );
            add( panel );
        }

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^simulations/translated$", TranslatedSimsPage.class, new String[]{} );
        mapper.addMap( "^simulations/translated/([^/]+)$", TranslatedSimsPage.class, new String[]{"translationlocale"} );
    }

    public static AbstractLinker getLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "simulations/translated";
            }
        };
    }

    public static AbstractLinker getLinker( final Locale locale ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "simulations/translated/" + LocaleUtils.localeToString( locale );
            }
        };
    }

}