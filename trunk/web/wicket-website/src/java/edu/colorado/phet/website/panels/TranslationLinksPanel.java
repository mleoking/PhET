package edu.colorado.phet.website.panels;

import java.util.Locale;

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
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TranslationLinksPanel extends PhetPanel {
    public TranslationLinksPanel( String id, final PageContext context ) {
        super( id, context );

        // TODO: missing query strings

        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        PageContext englishContext = context.withNewLocale( englishLocale );
        String linkTo = englishContext.getPrefix() + englishContext.getPath();
        if ( linkTo.equals( "/en/" ) ) {
            linkTo = "/";
        }
        if ( DistributionHandler.redirectEnglishLinkToPhetMain( (PhetRequestCycle) getRequestCycle() ) ) {
            linkTo = "http://phet.colorado.edu";
        }
        Link englishLink = new PhetLink( "translation-link", linkTo );
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
                Link link = new PhetLink( "translation-link", newContext.getPrefix() + newContext.getPath() );
                LocalizedLabel label = new LocalizedLabel( "translation-label", locale, new ResourceModel( "language.name" ) );
                link.add( label );
                if ( context.getLocale().equals( locale ) ) {
                    label.add( new AttributeAppender( "class", true, new Model( "current-locale" ), " " ) );
                }
                item.add( link );
            }
        };
        add( listView );

        add( HeaderContributor.forCss( "/css/translation-links-v1.css" ) );
    }
}
