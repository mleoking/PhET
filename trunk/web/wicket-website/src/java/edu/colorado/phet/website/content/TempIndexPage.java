package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.templates.PhetPage;

public class TempIndexPage extends PhetPage {
    public TempIndexPage( PageParameters parameters ) {
        super( parameters );

        addTitle( new ResourceModel( "home.title" ) );

        add( new IndexPanel( "index-panel", getPageContext() ) );

    }
}