package edu.colorado.phet.website.panels;

import edu.colorado.phet.website.util.PageContext;

public class PanelHolder extends PhetPanel {

    public PanelHolder( String id, PageContext context ) {
        super( id, context );
        setOutputMarkupId( true );
    }

    public String getWicketId() {
        return "holder-sub-panel";
    }

}
