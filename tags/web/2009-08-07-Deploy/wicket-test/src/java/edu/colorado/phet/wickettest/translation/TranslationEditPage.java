package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
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

        testLocale = LocaleUtils.stringToLocale( parameters.getString( "translationLocale" ) );
        translationId = parameters.getInt( "translationId" );

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
