package edu.colorado.phet.website.translation;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.AuthorizationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.panels.PanelHolder;
import edu.colorado.phet.website.translation.entities.TranslationEntity;

public class TranslationEditPage extends TranslationPage {
    private int translationId;
    private PanelHolder panelHolder;
    private TranslateEntityPanel subPanel;
    private TranslationEntityListPanel entityListPanel;
    private Locale testLocale;
    private String selectedEntityName = null;

    private static final Logger logger = Logger.getLogger( TranslationEditPage.class.getName() );

    public TranslationEditPage( PageParameters parameters ) {
        super( parameters );

        testLocale = LocaleUtils.stringToLocale( parameters.getString( "translationLocale" ) );
        translationId = parameters.getInt( "translationId" );

        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Translation translation = (Translation) session.load( Translation.class, translationId );

            if ( !translation.isAuthorizedUser( getUser() ) ) {
                throw new AuthorizationException( "You are not authorized to edit this translation" ) {
                };
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            logger.warn( e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
            throw e;
        }

        panelHolder = new PanelHolder( "translation-panel", getPageContext() );
        add( panelHolder );
        TranslationEntity firstEntity = TranslationEntity.getTranslationEntities().get( 0 );
        selectedEntityName = firstEntity.getDisplayName();
        subPanel = new TranslateEntityPanel( panelHolder.getWicketId(), getPageContext(), this, firstEntity, translationId, testLocale );
        panelHolder.add( subPanel );

        entityListPanel = new TranslationEntityListPanel( "entity-list-panel", getPageContext(), this );
        add( entityListPanel );

        add( new TranslationUserPanel( "user-panel", getPageContext(), translationId ) );

    }

    public int getTranslationId() {
        return translationId;
    }

    public PanelHolder getPanelHolder() {
        return panelHolder;
    }

    public TranslateEntityPanel getSubPanel() {
        return subPanel;
    }

    public Locale getTestLocale() {
        return testLocale;
    }

    public String getSelectedEntityName() {
        return selectedEntityName;
    }

    public void setSelectedEntityName( String selectedEntityName ) {
        this.selectedEntityName = selectedEntityName;
    }

    public TranslationEntityListPanel getEntityListPanel() {
        return entityListPanel;
    }
}
