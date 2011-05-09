package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.translation.TranslationListPanel;

public class AdminTranslationsPage extends AdminPage {
    public AdminTranslationsPage( PageParameters parameters ) {
        super( parameters );

        add( new TranslationListPanel( "translations", getPageContext() ) );
    }

}