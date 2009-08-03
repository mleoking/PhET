package edu.colorado.phet.wickettest.translation;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Translation;
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
