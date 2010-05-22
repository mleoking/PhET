package edu.colorado.phet.website.translation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class TranslationMainPage extends TranslationPage {

    private PhetLocales phetLocales;

    private static final Logger logger = Logger.getLogger( TranslationMainPage.class.getName() );

    public TranslationMainPage( PageParameters parameters ) {
        super( parameters );

        if ( !PhetSession.get().isSignedIn() ) {
            throw new RestartResponseAtInterceptPageException( SignInPage.class );
        }

        phetLocales = ( (PhetWicketApplication) getApplication() ).getSupportedLocales();

        Form createTranslationForm = new CreateTranslationForm();

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

                if ( !translation.isAuthorizedUser( PhetSession.get().getUser() ) ) {
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
            logger.warn( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

        ListView tlist = new TranslationListView( translations, sizes );

        add( tlist );
    }

    private class CreateTranslationForm extends Form {

        private LocaleDropDownChoice localeChoice;

        public CreateTranslationForm() {
            super( "create-translation-form" );

            localeChoice = new LocaleDropDownChoice( "locales", getPageContext() );
            add( localeChoice );
        }

        @Override
        protected void onSubmit() {
            logger.debug( localeChoice.getLocale() );

            if ( localeChoice.getLocale() != null ) {

                PageParameters params = new PageParameters();

                Session session = getHibernateSession();
                Translation translation = null;
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    translation = new Translation();
                    translation.setLocale( localeChoice.getLocale() );
                    translation.setVisible( false );

                    PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );
                    translation.addUser( user );

                    session.save( translation );
                    session.save( user );

                    tx.commit();
                }
                catch( RuntimeException e ) {
                    logger.warn( "Exception: " + e );
                    if ( tx != null && tx.isActive() ) {
                        try {
                            tx.rollback();
                        }
                        catch( HibernateException e1 ) {
                            logger.error( "ERROR: Error rolling back transaction", e1 );
                        }
                        throw e;
                    }
                }

                params.put( "translationId", translation.getId() );
                params.put( "translationLocale", LocaleUtils.localeToString( localeChoice.getLocale() ) );

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
            item.add( new Label( "locale", phetLocales.getName( translation.getLocale() ) + " (" + LocaleUtils.localeToString( translation.getLocale() ) + ")" ) );
            item.add( new Label( "num-strings", String.valueOf( sizes.get( translation ) ) ) );
            Label visibleLabel = new Label( "visible-label", String.valueOf( translation.isVisible() ) );
            if ( translation.isVisible() ) {
                visibleLabel.add( new AttributeAppender( "class", true, new Model( "translation-visible" ), " " ) );
            }
            item.add( visibleLabel );


            if ( PhetSession.get().getUser().isTeamMember() && !( translation.isVisible() && translation.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) ) ) {
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
                            translation.setVisible( !translation.isVisible() );
                            session.update( tr );

                            tx.commit();

                            if ( translation.isVisible() ) {
                                ( (PhetWicketApplication) getApplication() ).addTranslation( tr );
                            }
                            else {
                                ( (PhetWicketApplication) getApplication() ).removeTranslation( tr );
                            }
                        }
                        catch( RuntimeException e ) {
                            logger.warn( "Exception: " + e );
                            if ( tx != null && tx.isActive() ) {
                                try {
                                    tx.rollback();
                                }
                                catch( HibernateException e1 ) {
                                    logger.error( "ERROR: Error rolling back transaction", e1 );
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

            if ( PhetSession.get().getUser().isTeamMember() && !( translation.isVisible() && translation.getLocale().equals( PhetWicketApplication.getDefaultLocale() ) ) ) {
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
                            for ( Object o : translation.getAuthorizedUsers() ) {
                                PhetUser user = (PhetUser) o;
                                user.getTranslations().remove( translation );
                                session.update( user );
                            }
                            session.delete( translation );

                            tx.commit();
                        }
                        catch( RuntimeException e ) {
                            logger.warn( "Exception: " + e );
                            if ( tx != null && tx.isActive() ) {
                                try {
                                    tx.rollback();
                                }
                                catch( HibernateException e1 ) {
                                    logger.error( "ERROR: Error rolling back transaction", e1 );
                                }
                                throw e;
                            }
                        }
                    }
                } );
            }
            else {
                item.add( new InvisibleComponent( "delete" ) );
            }
        }
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "for-translators/website", TranslationMainPage.class, new String[]{} );
    }

    public static AbstractLinker getLinker() {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "for-translators/website";
            }
        };
    }

}
