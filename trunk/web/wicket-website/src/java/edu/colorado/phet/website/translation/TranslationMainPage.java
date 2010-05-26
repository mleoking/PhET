package edu.colorado.phet.website.translation;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.Form;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
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

        add( new TranslationListPanel( "translation-list-panel", getPageContext() ) );

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
