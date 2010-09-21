package edu.colorado.phet.website.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.util.PageContext;

/**
 * The alternate content shown within the Rotator for offline installers or if the user does not have Flash.
 */
public class RotatorFallbackPanel extends PhetPanel {

    public RotatorFallbackPanel( String id, PageContext context ) {
        super( id, context );

        Link fallbackLink = SimulationPage.getLinker( "mass-spring-lab", "mass-spring-lab" ).getLink( "fallback-link", context, getPhetCycle() );
        add( fallbackLink );
        if ( getPhetCycle().isOfflineInstaller() ) {
            fallbackLink.add( new AttributeModifier( "class", true, new Model<String>( "installer" ) ) );
        }
    }

}