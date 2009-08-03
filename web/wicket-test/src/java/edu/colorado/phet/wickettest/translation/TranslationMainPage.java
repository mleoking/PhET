package edu.colorado.phet.wickettest.translation;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetPage;

public class TranslationMainPage extends PhetPage {

    private LocaleModel selectedLocaleModel;

    public TranslationMainPage( PageParameters parameters ) {
        super( parameters, true );

        Form createTranslationForm = new Form( "create-translation-form" ) {
            @Override
            protected void onSubmit() {
                System.out.println( selectedLocaleModel );

                if ( selectedLocaleModel.locale != null ) {

                    PageParameters params = new PageParameters();

                    Session session = getHibernateSession();
                    Translation translation = null;
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();

                        translation = new Translation();
                        translation.setLocale( selectedLocaleModel.locale );

                        session.save( translation );

                        tx.commit();
                    }
                    catch( RuntimeException e ) {
                        System.out.println( "Exception: " + e );
                        if ( tx != null && tx.isActive() ) {
                            try {
                                tx.rollback();
                            }
                            catch( HibernateException e1 ) {
                                System.out.println( "ERROR: Error rolling back transaction" );
                            }
                            throw e;
                        }
                    }

                    params.put( "translationId", translation.getId() );
                    params.put( "translationLocale", LocaleUtils.localeToString( selectedLocaleModel.locale ) );

                    setResponsePage( TranslationEditPage.class, params );
                }
            }
        };

        Locale[] localeArray = Locale.getAvailableLocales();
        List<LocaleModel> models = new LinkedList<LocaleModel>();
        for ( Locale locale : localeArray ) {
            models.add( new LocaleModel( locale ) );
        }

        Collections.sort( models, new Comparator<LocaleModel>() {
            public int compare( LocaleModel a, LocaleModel b ) {
                return a.getObject().toString().compareTo( b.getObject().toString() );
            }
        } );

        selectedLocaleModel = new LocaleModel( LocaleUtils.stringToLocale( "en" ) );

        DropDownChoice localeChoice = new DropDownChoice( "locales", selectedLocaleModel, models );
        createTranslationForm.add( localeChoice );

        add( createTranslationForm );

        final List<Translation> translations = new LinkedList<Translation>();
        final Map<Translation, Integer> sizes = new HashMap<Translation, Integer>();

        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List trans = session.createQuery( "select t from Translation as t order by t.id" ).list();

            for ( Object tran : trans ) {
                Translation translation = (Translation) tran;

                // count the number of strings
                sizes.put( translation, ( (Long) session.createQuery( "select count(*) from TranslatedString as ts where ts.translation = :translation" ).setEntity( "translation", translation ).iterate().next() ).intValue() );

                translations.add( translation );
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        ListView tlist = new ListView( "translation-list", translations ) {
            protected void populateItem( ListItem item ) {
                final Translation translation = (Translation) item.getModel().getObject();
                item.add( new Label( "id", String.valueOf( translation.getId() ) ) );
                item.add( new Label( "locale", translation.getLocale().getDisplayName() + " (" + LocaleUtils.localeToString( translation.getLocale() ) + ")" ) );
                item.add( new Label( "num-strings", String.valueOf( sizes.get( translation ) ) ) );
                Link popupLink = IndexPage.createLink( "preview", new PageContext( "/translation/" + String.valueOf( translation.getId() ) + "/", "", translation.getLocale() ) );
                popupLink.setPopupSettings( new PopupSettings( PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | PopupSettings.RESIZABLE
                                                               | PopupSettings.SCROLLBARS | PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR ) );
                item.add( popupLink );
                item.add( new Link( "edit" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "translationId", translation.getId() );
                        params.put( "translationLocale", LocaleUtils.localeToString( translation.getLocale() ) );
                        setResponsePage( TranslationEditPage.class, params );
                    }
                } );
                item.add( new Link( "delete" ) {
                    public void onClick() {
                        Session session = TranslationMainPage.this.getHibernateSession();
                        Transaction tx = null;
                        try {
                            tx = session.beginTransaction();

                            session.load( translation, translation.getId() );

                            translations.remove( translation );
                            for ( Object o : translation.getTranslatedStrings() ) {
                                session.delete( o );
                            }
                            session.delete( translation );

                            tx.commit();
                        }
                        catch( RuntimeException e ) {
                            System.out.println( "Exception: " + e );
                            if ( tx != null && tx.isActive() ) {
                                try {
                                    tx.rollback();
                                }
                                catch( HibernateException e1 ) {
                                    System.out.println( "ERROR: Error rolling back transaction" );
                                }
                                throw e;
                            }
                        }
                    }
                } );
            }
        };

        add( tlist );

        addTitle( "Translation test page" );
    }

    private static class LocaleModel implements IModel {
        private Locale locale;

        private LocaleModel( Locale locale ) {
            this.locale = locale;
        }

        public Object getObject() {
            return locale;
        }

        public void setObject( Object object ) {
            if ( object instanceof Locale ) {
                locale = (Locale) object;
            }
            else if ( object instanceof LocaleModel ) {
                locale = (Locale) ( (LocaleModel) object ).getObject();
            }
            else {
                throw new RuntimeException( "Bad LocaleModel!" );
            }
        }

        public void detach() {

        }

        @Override
        public String toString() {
            return locale.getDisplayName() + "  ( " + LocaleUtils.localeToString( locale ) + " )";
        }
    }
}
