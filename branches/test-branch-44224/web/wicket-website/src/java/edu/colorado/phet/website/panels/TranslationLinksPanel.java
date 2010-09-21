package edu.colorado.phet.website.panels;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.components.LocalizedLabel;
import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Shows a list of the available website translations with the currently viewed one grayed. Clicking the links to
 * other translations will take one to the exact same page, but for the other translation.
 */
public class TranslationLinksPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( TranslationLinksPanel.class.getName() );

    public TranslationLinksPanel( String id, final PageContext context ) {
        super( id, context );

        final String queryString = getPhetCycle().getQueryString() == null ? "" : "?" + getPhetCycle().getQueryString();

        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        PageContext englishContext = context.withNewLocale( englishLocale );
        String linkTo = englishContext.getPrefix() + englishContext.getPath();
        if ( linkTo.equals( "/en/" ) ) {
            linkTo = "/";
        }
        if ( DistributionHandler.redirectEnglishLinkToPhetMain( (PhetRequestCycle) getRequestCycle() ) ) {
            linkTo = "http://phet.colorado.edu";
        }
        linkTo += queryString;
        Link englishLink = new RawLink( "translation-link", linkTo );
        LocalizedLabel englishLabel = new LocalizedLabel( "translation-label", englishLocale, new ResourceModel( "language.name" ) );
        englishLink.add( englishLabel );
        if ( context.getLocale().equals( englishLocale ) ) {
            englishLabel.add( new AttributeAppender( "class", true, new Model( "current-locale" ), " " ) );
        }
        add( englishLink );

        ListView listView = new ListView( "translation-links", ( (PhetWicketApplication) getApplication() ).getTranslationLocaleStrings() ) {
            protected void populateItem( ListItem item ) {
                String localeString = (String) item.getModel().getObject();
                Locale locale = LocaleUtils.stringToLocale( localeString );
                PageContext newContext = context.withNewLocale( locale );
                String path = newContext.getPrefix() + newContext.getPath() + queryString;
                Link link = new RawLink( "translation-link", path );
                LocalizedLabel label = new LocalizedLabel( "translation-label", locale, new ResourceModel( "language.name" ) );
                link.add( label );
                if ( context.getLocale().equals( locale ) ) {
                    label.add( new AttributeAppender( "class", true, new Model( "current-locale" ), " " ) );
                }
                item.add( link );
            }
        };
        add( listView );

        add( HeaderContributor.forCss( CSS.TRANSLATION_LINKS ) );
    }
}
