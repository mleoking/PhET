package edu.colorado.phet.website.authentication.panels;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class UpdatePasswordSuccessPanel extends PhetPanel {
    public UpdatePasswordSuccessPanel( String id, PageContext context ) {
        super( id, context );
    }

    public static String getKey() {
        return "updatePasswordSuccess";
    }

    public static String getUrl() {
        return "update-password-success";
    }
    
    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}