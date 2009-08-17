package edu.colorado.phet.wickettest.translation;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.authentication.AuthenticatedPage;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.data.PhetUser;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationMainPage extends AuthenticatedPage {

    private LocaleModel selectedLocaleModel;

    public TranslationMainPage( PageParameters parameters ) {
        super( parameters );

        Form createTranslationForm = new CreateTranslationForm();

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

                if ( !translation.isAuthorizedUser( getUser() ) ) {
                    // don't show translations that the user doesn't have access to
                    continue;
                }

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

        ListView tlist = new TranslationListView( translations, sizes );

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

    private class CreateTranslationForm extends Form {
        public CreateTranslationForm() {
            super( "create-translation-form" );
        }

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
                    translation.setVisible( false );

                    PhetUser user = (PhetUser) session.load( PhetUser.class, TranslationMainPage.this.getUser().getId() );
                    translation.addUser( user );

                    session.save( translation );
                    session.save( user );

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
    }

    private class TranslationListView extends ListView {
        private final List<Translation> translations;
        private final Map<Translation, Integer> sizes;

        public TranslationListView( List<Translation> translations, Map<Translation, Integer> sizes ) {
            super( "translation-list", translations );
            this.translations = translations;
            this.sizes = sizes;
        }

        protected void populateItem( ListItem item ) {
            final Translation translation = (Translation) item.getModel().getObject();
            item.add( new Label( "id", String.valueOf( translation.getId() ) ) );
            item.add( new Label( "locale", translation.getLocale().getDisplayName() + " (" + LocaleUtils.localeToString( translation.getLocale() ) + ")" ) );
            item.add( new Label( "num-strings", String.valueOf( sizes.get( translation ) ) ) );
            Label visibleLabel = new Label( "visible-label", String.valueOf( translation.isVisible() ) );
            if ( translation.isVisible() ) {
                visibleLabel.add( new AttributeAppender( "class", true, new Model( "translation-visible" ), " " ) );
            }
            item.add( visibleLabel );


            if ( getUser().isTeamMember() ) {
                item.add( new Link( "visible-toggle" ) {
                    public void onClick() {
                        Session session = getHibernateSession();
                        Transaction tx = null;
                        try {
                            tx = session.beginTransaction();

                            Translation tr = (Translation) session.load( Translation.class, translation.getId() );
                            List otherTranslations = session.createQuery( "select t from Translation as t where t.visible = true and t.locale = :locale" ).setLocale( "locale", translation.getLocale() ).list();

                            if ( !tr.isVisible() && !otherTranslations.isEmpty() ) {
                                throw new RuntimeException( "There is already a visible translation of that locale" );
                            }

                            tr.setVisible( !tr.isVisible() );
                            session.update( tr );

                            tx.commit();

                            if ( translation.isVisible() ) {
                                ( (WicketApplication) getApplication() ).addTranslation( tr );
                            }
                            else {
                                ( (WicketApplication) getApplication() ).removeTranslation( tr );
                            }
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
            else {
                // TODO: find compact way of adding an invisible item?
                Label invisibleLabel = new Label( "visible-toggle", "unseen" );
                invisibleLabel.setVisible( false );
                item.add( invisibleLabel );
            }

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
    }
}
