package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.translation.entities.CommonEntity;
import edu.colorado.phet.wickettest.util.PhetPage;

public class TranslationEditPage extends PhetPage {
    private int translationId;
    private PanelHolder panelHolder;
    private TranslateEntityPanel subPanel;
    private Locale testLocale;
    private String selectedEntityName = null;

    public TranslationEditPage( PageParameters parameters ) {
        super( parameters, true );

        testLocale = LocaleUtils.stringToLocale( "zh_CN" );
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
        }

        addTitle( "Translation test page" );

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
