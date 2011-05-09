package edu.colorado.phet.website.admin;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.panels.contribution.ContributionManagePanel;

public class AdminActivitiesPage extends AdminPage {
    public AdminActivitiesPage( PageParameters parameters ) {
        super( parameters );

        add( new ContributionManagePanel( "activities", getPageContext() ) );
    }

}