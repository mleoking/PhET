package edu.colorado.phet.wickettest.translation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SponsorsPanel;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class TranslateEntityPanel extends PhetPanel {

    private TranslationEntity entity;
    private int translationId;
    private PanelHolder panel;
    private Component subPanel;
    private Map<String, IModel> stringModelMap = new HashMap<String, IModel>();

    public TranslateEntityPanel( String id, final PageContext context, final TranslationEntity entity, final int translationId, final Locale testLocale ) {
        super( id, context );
        this.entity = entity;
        this.translationId = translationId;

        setOutputMarkupId( true );

        add( new Label( "translation-id", String.valueOf( translationId ) ) );

        panel = new PanelHolder( "panel", context.withNewLocale( testLocale ) );
        subPanel = new SponsorsPanel( panel.getWicketId(), context.withNewLocale( testLocale ) );
        if ( entity.hasPreviews() ) {
            subPanel = entity.getPreviews().get( 0 ).getNewPanel( panel.getWicketId(), context.withNewLocale( testLocale ), (PhetRequestCycle) getRequestCycle() );
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
                subPanel = entity.getPreviews().get( 0 ).getNewPanel( panel.getWicketId(), context.withNewLocale( testLocale ), (PhetRequestCycle) getRequestCycle() );
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

                if ( tString.getNotes() == null ) {
                    Label label = new Label( "translation-string-notes", "UNSEEN" );
                    label.setVisible( false );
                    item.add( label );
                }
                else {
                    Label label = new Label( "translation-string-notes", tString.getNotes() );
                    item.add( label );
                }

                item.add( new Label( "translation-string-key", tString.getKey() ) );
                AjaxEditableMultiLineLabel editableLabel = new AjaxEditableMultiLineLabel( "translation-string-value", model ) {
                    @Override
                    protected void onSubmit( AjaxRequestTarget target ) {
                        super.onSubmit( target );
                        setString( tString.getKey(), (String) model.getObject() );
                        target.addComponent( TranslateEntityPanel.this );
                    }
                };
                if ( !isStringSet( tString.getKey() ) ) {
                    editableLabel.add( new AttributeAppender( "class", true, new Model( "not-translated" ), " " ) );
                }
                item.add( editableLabel );
            }
        };
        add( stringList );
    }

    public boolean isStringSet( String key ) {
        Session session = getHibernateSession();
        Transaction tx = null;
        List results = null;
        try {
            tx = session.beginTransaction();

            Query query = session.createQuery( "select ts from TranslatedString as ts, Translation as t where (t.id = :id AND ts.translation = t AND ts.key = :key)" );
            query.setInteger( "id", translationId );
            query.setString( "key", key );
            results = query.list();

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

        if ( results == null || results.isEmpty() ) {
            return false;
        }
        return true;
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
