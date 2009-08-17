package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.AuthorizationException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.translation.entities.CommonEntity;

public class TranslationEditPage extends TranslationPage {
    private int translationId;
    private PanelHolder panelHolder;
    private TranslateEntityPanel subPanel;
    private Locale testLocale;
    private String selectedEntityName = null;

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
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
            throw e;
        }

        panelHolder = new PanelHolder( "translation-panel", getPageContext() );
        add( panelHolder );
        CommonEntity commonEntity = new CommonEntity();
        selectedEntityName = commonEntity.getDisplayName();
        subPanel = new TranslateEntityPanel( panelHolder.getWicketId(), getPageContext(), commonEntity, translationId, testLocale );
        panelHolder.add( subPanel );

        add( new TranslationEntityListPanel( "entity-list-panel", getPageContext(), this ) );
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

}
