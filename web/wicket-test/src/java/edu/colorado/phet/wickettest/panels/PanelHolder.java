package edu.colorado.phet.wickettest.panels;

import edu.colorado.phet.wickettest.util.PageContext;

public class PanelHolder extends PhetPanel {

    public PanelHolder( String id, PageContext context ) {
        super( id, context );
        setOutputMarkupId( true );
    }

    public String getWicketId() {
        return "holder-sub-panel";
    }

}
