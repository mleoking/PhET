package edu.colorado.phet.website.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

public class AdminStringsPage extends AdminPage {

    private static final Logger logger = Logger.getLogger( AdminStringsPage.class.getName() );

    public AdminStringsPage( PageParameters parameters ) {
        super( parameters );

        final List<TranslatedString> strings = new LinkedList<TranslatedString>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select ts from TranslatedString as ts where ts.translation.visible = true and ts.translation.locale = :en" )
                        .setLocale( "en", PhetWicketApplication.getDefaultLocale() ).list();
                for ( Object o : list ) {
                    strings.add( (TranslatedString) o );
                }
                return true;
            }
        } );

        Collections.sort( strings, new Comparator<TranslatedString>() {
            public int compare( TranslatedString a, TranslatedString b ) {
                return a.getKey().compareTo( b.getKey() );
            }
        } );

        add( new ListView( "string", strings ) {
            protected void populateItem( ListItem item ) {
                TranslatedString string = (TranslatedString) item.getModel().getObject();
                item.add( new Label( "key", string.getKey() ) );
                item.add( new Label( "value", string.getValue() ) );
            }
        } );


    }

}