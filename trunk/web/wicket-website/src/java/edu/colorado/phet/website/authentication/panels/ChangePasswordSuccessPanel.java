package edu.colorado.phet.website.authentication.panels;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ChangePasswordSuccessPanel extends PhetPanel {
    public ChangePasswordSuccessPanel( String id, PageContext context ) {
        super( id, context );
    }

    public static String getKey() {
        return "changePasswordSuccess";
    }

    public static String getUrl() {
        return "change-password-success";
    }
    
    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}