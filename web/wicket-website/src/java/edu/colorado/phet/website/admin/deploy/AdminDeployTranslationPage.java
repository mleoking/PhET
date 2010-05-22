package edu.colorado.phet.website.admin.deploy;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.buildtools.BuildLocalProperties;
import edu.colorado.phet.buildtools.JARGenerator;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.WebsiteProperties;
import edu.colorado.phet.website.admin.AdminPage;
import edu.colorado.phet.website.data.Project;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * Called during the deployment process from within the server, meant to synchronize a particular project
 */
public class AdminDeployTranslationPage extends AdminPage {

    private static Logger logger = Logger.getLogger( AdminDeployTranslationPage.class.getName() );

    private static final Object lock = new Object();


    public AdminDeployTranslationPage( PageParameters parameters ) {
        super( parameters );

        synchronized( lock ) {
            add( new Label( "dir", parameters.getString( "dir" ) ) );

        }
    }

    public static RawLinkable getLinker( final String dir ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "admin/deploy-translation?dir=" + dir;
            }
        };
    }

}