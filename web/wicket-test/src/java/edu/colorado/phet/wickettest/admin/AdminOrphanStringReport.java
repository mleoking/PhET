package edu.colorado.phet.wickettest.admin;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.translation.TranslationEntityString;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class AdminOrphanStringReport extends AdminPage {
    public AdminOrphanStringReport( PageParameters parameters ) {
        super( parameters );

        final List<TranslationEntity> entities = TranslationEntity.getTranslationEntities();
        final List<TranslatedString> englishStrings = new LinkedList<TranslatedString>();
        final List<TranslatedString> orphanStrings = new LinkedList<TranslatedString>();
        final HashSet<String> includedKeys = new HashSet<String>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List strings = session.createQuery( "select ts from TranslatedString as ts, Translation as t where ts.translation = t and t.visible = true and t.locale = :locale" ).setLocale( "locale", WicketApplication.getDefaultLocale() ).list();
                for ( Object string : strings ) {
                    englishStrings.add( (TranslatedString) string );
                }
                return true;
            }
        } );

        for ( TranslationEntity entity : entities ) {
            for ( TranslationEntityString string : entity.getStrings() ) {
                includedKeys.add( string.getKey() );
            }
        }

        for ( TranslatedString string : englishStrings ) {
            if ( !includedKeys.contains( string.getKey() ) ) {
                orphanStrings.add( string );
            }
        }

        add( new ListView( "string-list", orphanStrings ) {
            protected void populateItem( ListItem item ) {
                TranslatedString string = (TranslatedString) item.getModel().getObject();

                item.add( new Label( "string-key", string.getKey() ) );
                item.add( new Label( "string-value", new ResourceModel( string.getKey() ) ) );
            }
        } );

    }
}
