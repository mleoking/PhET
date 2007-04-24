/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.view.GPLAboutPanel;
import edu.colorado.phet.common.phetcommon.view.LogoPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import org.jfree.base.Library;
import org.jfree.ui.about.Contributor;
import org.jfree.ui.about.ProjectInfo;
import org.jfree.ui.about.AboutFrame;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 5, 2006
 * Time: 1:43:10 PM
 *
 */

public class TravoltageAboutDialog extends AboutFrame {
    public TravoltageAboutDialog( TravoltageApplication travoltageApplication ) {
        // travoltageApplication.getPhetFrame(),
        super( "Travoltage", createProjectInfo( travoltageApplication ) );
    }

    private static ProjectInfo createProjectInfo( TravoltageApplication travoltageApplication ) {
        final String projectName = travoltageApplication.getTitle();
        String version = travoltageApplication.getVersion();
        String info = travoltageApplication.getDescription();
        Image logo = null;
        try {
            logo = ImageLoader.loadBufferedImage( LogoPanel.IMAGE_PHET_LOGO );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        String copyright = "Copyright 2006, University of Colorado";
        String licenseName = "GNU Public License (GPL)";
        String licenseText = GPLAboutPanel.getGPLText();

        ArrayList contributorsList = new ArrayList();
        contributorsList.add( new Contributor( "Sam Reid", "" ) );

        final ProjectInfo projectInfo = new ProjectInfo( projectName, version, info, logo, copyright, licenseName, licenseText );
        projectInfo.setContributors( contributorsList );
        projectInfo.addLibrary( new Library( "Jade", "0.6.1", "GPL", "http://rsheh.web.cse.unsw.edu.au/homepage/index.php?id=34" ) );
        projectInfo.addLibrary( new Library( "JCommon", "1.0.5", "LGPL", "http://www.jfree.org/jcommon/index.php" ) );

        return projectInfo;
    }
}
