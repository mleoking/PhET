package edu.colorado.phet.website.panels;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.data.*;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.data.util.IChangeListener;
import edu.colorado.phet.website.util.PageContext;

public class SponsorsPanel extends PhetPanel {
    public SponsorsPanel( String id, final PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.SPONSORS ) );

        add( AboutSponsorsPanel.getLinker().getLink( "sponsors-link", context, getPhetCycle() ) );

        // TODO: localize alt attributes
        add( new StaticImage( "hewlett-logo", Images.LOGO_HEWLETT, "The Hewlett Logo" ) );
        add( new StaticImage( "nsf-logo", Images.LOGO_NSF, "The NSF Logo" ) );
        add( new StaticImage( "ecsme-logo", Images.LOGO_ECSME, "The King Saud (ESCME) Logo" ) );

        addDependency( new EventDependency() {

            private IChangeListener stringListener;

            @Override
            protected void addListeners() {
                stringListener = createTranslationChangeInvalidator( context.getLocale() );
                HibernateEventListener.addListener( TranslatedString.class, stringListener );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( TranslatedString.class, stringListener );
            }
        } );
    }
}
