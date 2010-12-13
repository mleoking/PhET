package edu.colorado.phet.website.content;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ClassroomUsePanel extends PhetPanel {
    public ClassroomUsePanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "for-teachers.classroom-use";
    }

    public static String getUrl() {
        return "for-teachers/classroom-use";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}