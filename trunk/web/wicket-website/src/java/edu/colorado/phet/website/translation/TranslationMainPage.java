package edu.colorado.phet.website.translation;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.authentication.SignInPage;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.TranslatedString;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
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

        add( new CreateTranslationForm( "create-new-translation-form" ) );

        TranslationListPanel translationList = new TranslationListPanel( "translation-list-panel", getPageContext() );
        
        add( translationList );

        add( new CopyTranslationForm( "create-version-translation-form", translationList.getTranslations() ) );

    }

    private class CopyTranslationForm extends Form {

        private Translation translation;

        public CopyTranslationForm( String id, final List<Translation> translations ) {
            super( id );

            add( new DropDownChoice<Translation>( "translations", new PropertyModel<Translation>( this, "translation" ), translations ) );
        }

        @Override
        protected void onSubmit() {

            if ( translation == null ) {
                return;
            }

            final Translation ret[] = new Translation[1];

            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Translation newTranslation = new Translation();
                    newTranslation.setLocale( translation.getLocale() );
                    newTranslation.setVisible( false );

                    PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );
                    newTranslation.addUser( user );

                    session.save( newTranslation );
                    session.save( user );
                    ret[0] = newTranslation;

                    Translation oldTranslation = (Translation) session.load( Translation.class, translation.getId() );

                    for ( Object o : oldTranslation.getTranslatedStrings() ) {
                        TranslatedString oldString = (TranslatedString) o;

                        TranslatedString newString = new TranslatedString();
                        newString.setCreatedAt( oldString.getCreatedAt() );
                        newString.setUpdatedAt( oldString.getUpdatedAt() );
                        newString.setKey( oldString.getKey() );
                        newString.setValue( oldString.getValue() );
                        newTranslation.addString( newString );

                        session.save( newString );
                    }

                    return true;
                }
            } );

            if ( success ) {

                logger.info( "Created translation: " + ret[0] + " based on " + translation );

                PageParameters params = new PageParameters();
                params.put( "translationId", ret[0].getId() );
                params.put( "translationLocale", LocaleUtils.localeToString( ret[0].getLocale() ) );

                setResponsePage( TranslationEditPage.class, params );
            }
        }
    }

    private class CreateTranslationForm extends Form {

        private LocaleDropDownChoice localeChoice;

        public CreateTranslationForm( String id ) {
            super( id );

            localeChoice = new LocaleDropDownChoice( "locales", getPageContext() );
            add( localeChoice );
        }

        @Override
        protected void onSubmit() {
            if ( localeChoice.getLocale() == null ) {
                return;
            }

            final Translation ret[] = new Translation[1];

            boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    Translation translation = new Translation();
                    translation.setLocale( localeChoice.getLocale() );
                    translation.setVisible( false );

                    PhetUser user = (PhetUser) session.load( PhetUser.class, PhetSession.get().getUser().getId() );
                    translation.addUser( user );
                    ret[0] = translation;

                    session.save( translation );
                    session.save( user );
                    return true;
                }
            } );

            logger.info( "Created translation: " + ret[0] );

            if ( success ) {
                PageParameters params = new PageParameters();
                params.put( "translationId", ret[0].getId() );
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
