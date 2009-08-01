package edu.colorado.phet.wickettest.translation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SponsorsPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TranslateEntityPanel extends PhetPanel {

    private TranslationEntity entity;
    private int translationId;
    private PanelHolder panel;
    private Component subPanel;
    private Map<String, IModel> stringModelMap = new HashMap<String, IModel>();

    public TranslateEntityPanel( String id, final PageContext context, final TranslationEntity entity ) {
        super( id, context );
        this.entity = entity;

        setOutputMarkupId( true );

        final Locale testLocale = LocaleUtils.stringToLocale( "zh_CN" );
        Session session = getHibernateSession();
        Translation translation = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            translation = new Translation();
            translation.setLocale( testLocale );

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

        if ( translation != null ) {
            translationId = translation.getId();
            add( new Label( "translation-id", String.valueOf( translationId ) ) );
        }

        panel = new PanelHolder( "panel", new PageContext( context, testLocale ) );
        subPanel = new SponsorsPanel( panel.getWicketId(), new PageContext( context, testLocale ) );
        if ( entity.hasPreviews() ) {
            subPanel = entity.getPreviews().get( 0 ).getNewPanel( panel.getWicketId(), new PageContext( context, testLocale ), (PhetRequestCycle) getRequestCycle() );
        }
        else {
            subPanel = new Label( panel.getWicketId(), "(Preview is not available)" );
        }
        panel.add( subPanel );
        add( panel );

        Form form = new Form( "test-form" );
        form.add( new AjaxButton( "test-button" ) {
            protected void onSubmit( AjaxRequestTarget target, Form form ) {
                panel.remove( subPanel );
                subPanel = entity.getPreviews().get( 0 ).getNewPanel( panel.getWicketId(), new PageContext( context, testLocale ), (PhetRequestCycle) getRequestCycle() );
                panel.add( subPanel );
                target.addComponent( panel );
            }
        } );
        add( form );

        ListView stringList = new ListView( "translation-string-list", entity.getStrings() ) {
            protected void populateItem( ListItem item ) {
                final TranslationEntityString tString = (TranslationEntityString) item.getModel().getObject();

                final Model model = new Model( getLocalizer().getString( tString.getKey(), TranslateEntityPanel.this ) );
                stringModelMap.put( tString.getKey(), model );

                item.add( new Label( "translation-string-key", tString.getKey() ) );
                item.add( new AjaxEditableMultiLineLabel( "translation-string-value", model ) {
                    @Override
                    protected void onSubmit( AjaxRequestTarget target ) {
                        super.onSubmit( target );
                        setString( tString.getKey(), (String) model.getObject() );
                        target.addComponent( TranslateEntityPanel.this );
                    }
                } );
            }
        };
        add( stringList );
    }

    public void setString( String key, String value ) {
        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Translation translation = (Translation) session.load( Translation.class, translationId );
            TranslatedString tString = null;
            for ( Object o : translation.getTranslatedStrings() ) {
                TranslatedString ts = (TranslatedString) o;
                if ( ts.getKey().equals( key ) ) {
                    tString = ts;
                    break;
                }
            }
            if ( tString == null ) {
                tString = new TranslatedString();
                tString.setKey( key );
                tString.setValue( value );
                tString.setTranslation( translation );
                translation.getTranslatedStrings().add( tString );
                session.save( tString );
            }
            else {
                tString.setValue( value );
                session.update( tString );
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
    }

    @Override
    public String getVariation() {
        return new Integer( translationId ).toString();
    }

}
