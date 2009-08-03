package edu.colorado.phet.wickettest.panels;

import java.util.Locale;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationLinksPanel extends PhetPanel {
    public TranslationLinksPanel( String id, final PageContext context ) {
        super( id, context );

        Locale englishLocale = LocaleUtils.stringToLocale( "en" );
        Link englishLink = IndexPage.createLink( "translation-link", context.withNewLocale( englishLocale ) );
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
                Link link = IndexPage.createLink( "translation-link", context.withNewLocale( locale ) );
                LocalizedLabel label = new LocalizedLabel( "translation-label", locale, new ResourceModel( "language.name" ) );
                link.add( label );
                if ( context.getLocale().equals( locale ) ) {
                    label.add( new AttributeAppender( "class", true, new Model( "current-locale" ), " " ) );
                }
                item.add( link );
            }
        };
        add( listView );
    }
}
