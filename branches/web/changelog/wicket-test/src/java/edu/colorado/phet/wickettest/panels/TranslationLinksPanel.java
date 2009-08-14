package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationLinksPanel extends PhetPanel {
    public TranslationLinksPanel( String id, final PageContext context ) {
        super( id, context );

        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        PageContext englishContext = context.withNewLocale( englishLocale );
        Link englishLink = new PhetLink( "translation-link", englishContext.getPrefix() + englishContext.getPath() );
        LocalizedLabel englishLabel = new LocalizedLabel( "translation-label", englishLocale, new ResourceModel( "language.name" ) );
        englishLink.add( englishLabel );
        if ( context.getLocale().equals( englishLocale ) ) {
            englishLabel.add( new AttributeAppender( "class", true, new Model( "current-locale" ), " " ) );
        }
        add( englishLink );

        ListView listView = new ListView( "translation-links", WicketApplication.getTranslations() ) {
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
