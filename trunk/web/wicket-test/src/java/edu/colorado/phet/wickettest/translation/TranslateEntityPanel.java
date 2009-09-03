package edu.colorado.phet.wickettest.translation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableMultiLineLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.test.TestTranslateString;
import edu.colorado.phet.wickettest.authentication.PhetSession;
import edu.colorado.phet.wickettest.components.InvisibleComponent;
import edu.colorado.phet.wickettest.components.LocalizedLabel;
import edu.colorado.phet.wickettest.content.IndexPage;
import edu.colorado.phet.wickettest.data.TranslatedString;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.entities.TranslationEntity;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.StringUtils;

public class TranslateEntityPanel extends PhetPanel {

    private TranslationEntity entity;
    private int translationId;
    private PreviewHolder panel;
    private Map<String, IModel> stringModelMap = new HashMap<String, IModel>();

    public TranslateEntityPanel( String id, final PageContext context, final TranslationEntity entity, final int translationId, final Locale testLocale ) {
        super( id, context );
        this.entity = entity;
        this.translationId = translationId;

        final PageContext externalContext = new PageContext( "/translation/" + String.valueOf( translationId ) + "/", "", testLocale );

        setOutputMarkupId( true );

        add( new Label( "translation-id", String.valueOf( translationId ) ) );

        panel = new PreviewHolder( "panel", externalContext, entity );
        add( panel );

        ListView stringList = new ListView( "translation-string-list", entity.getStrings() ) {
            protected void populateItem( ListItem item ) {
                final TranslationEntityString tString = (TranslationEntityString) item.getModel().getObject();

                String initString = getLocalizer().getString( tString.getKey(), TranslateEntityPanel.this );
                initString = initString.replaceAll( "<br/>", "\n" );
                final Model model = new Model( initString );
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

                if ( ( (PhetSession) getSession() ).getUser().isTeamMember() ) {
                    item.add( new InvisibleComponent( "translate-auto" ) );
                    /*
                    // TODO: remove after development
                    item.add( new AjaxLink( "translate-auto" ) {
                        public void onClick( AjaxRequestTarget target ) {
                            StringUtils.setString( getHibernateSession(), tString.getKey(), TestTranslateString.translate( (String) model.getObject(), "en", "ar" ), translationId );
                            target.addComponent( TranslateEntityPanel.this );
                        }
                    } );
                    */
                }
                else {
                    item.add( new InvisibleComponent( "translate-auto" ) );
                }

                item.add( new Label( "translation-string-key", tString.getKey() ) );

                item.add( new LocalizedLabel( "translation-string-english", WicketApplication.getDefaultLocale(), new ResourceModel( tString.getKey() ) ) );

                AjaxEditableMultiLineLabel editableLabel = new AjaxEditableMultiLineLabel( "translation-string-value", model ) {
                    @Override
                    protected void onSubmit( AjaxRequestTarget target ) {
                        super.onSubmit( target );
                        StringUtils.setString( getHibernateSession(), tString.getKey(), (String) model.getObject(), translationId );
                        target.addComponent( TranslateEntityPanel.this );
                    }
                };
                if ( !isStringSet( tString.getKey() ) ) {
                    editableLabel.add( new AttributeAppender( "class", true, new Model( "not-translated" ), " " ) );
                }
                else if ( !isStringUpToDate( tString.getKey() ) ) {
                    editableLabel.add( new AttributeAppender( "class", true, new Model( "string-out-of-date" ), " " ) );
                }
                item.add( editableLabel );
            }
        };
        add( stringList );


        Link popupLink = IndexPage.createLink( "translation-popup", externalContext );
        popupLink.setPopupSettings( new PopupSettings( PopupSettings.LOCATION_BAR | PopupSettings.MENU_BAR | PopupSettings.RESIZABLE
                                                       | PopupSettings.SCROLLBARS | PopupSettings.STATUS_BAR | PopupSettings.TOOL_BAR ) );
        add( popupLink );
    }

    public boolean isStringUpToDate( final String key ) {
        boolean success = HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                /*
                Translation currentTranslation = (Translation) session.createQuery( "select t from Translation as t where t.id = :id" ).setInteger( "id", translationId ).uniqueResult();
                if ( currentTranslation.isVisible() && currentTranslation.getLocale().equals( WicketApplication.getDefaultLocale() ) ) {
                    // this is the English translation. Don't show a ton of orange, it is by definition up-to-date
                    return true;
                }
                */
                TranslatedString standard = (TranslatedString) session.createQuery( "select ts from TranslatedString as ts, Translation as t where ts.translation = t and t.visible = true and t.locale = :locale and ts.key = :key" )
                        .setLocale( "locale", WicketApplication.getDefaultLocale() )
                        .setString( "key", key )
                        .uniqueResult();
                TranslatedString current = (TranslatedString) session.createQuery( "select ts from TranslatedString as ts, Translation as t where ts.translation = t and t.id = :id and ts.key = :key" )
                        .setInteger( "id", translationId )
                        .setString( "key", key )
                        .uniqueResult();
                return current.getUpdatedAt().compareTo( standard.getUpdatedAt() ) >= 0;
            }
        } );
        return success;
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

    @Override
    public String getVariation() {
        return new Integer( translationId ).toString();
    }

}
