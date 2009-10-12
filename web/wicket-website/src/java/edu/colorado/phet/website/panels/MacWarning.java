package edu.colorado.phet.website.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.util.PageContext;

public class MacWarning extends PhetPanel {
    public MacWarning( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( "/css/warning-v1.css" ) );

        add( TroubleshootingJavaPanel.getLinker().getLink( "troubleshooting-link", context ) );
    }
}
