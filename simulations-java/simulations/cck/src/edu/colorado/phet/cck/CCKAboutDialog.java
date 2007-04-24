package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;

/**
 * User: Sam Reid
 * Date: Oct 9, 2006
 * Time: 2:01:23 PM
 *
 */

public class CCKAboutDialog extends PhetAboutDialog {
    public CCKAboutDialog( CCKApplication cckApplication ) {
        super( cckApplication );
    }

//    protected String getLicenseText() {
//        String s = super.getLicenseText();
//        s += System.getProperty( "line.separator" );
//        s += System.getProperty( "line.separator" );
//        s += readText( "cck-projects.txt" );
//        return s;
//    }
}
