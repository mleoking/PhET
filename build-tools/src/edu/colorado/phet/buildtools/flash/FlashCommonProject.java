package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.swing.*;

import edu.colorado.phet.buildtools.java.JavaBuildCommand;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.SVNDependencyProject;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 12, 2009
 * Time: 3:10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class FlashCommonProject extends SVNDependencyProject {
    public FlashCommonProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public static void generateFlashSoftwareAgreement( File trunk ) {
        File softwareAgreementFile = new File( trunk, JavaBuildCommand.SOFTWARE_AGREEMENT_PATH + "/software-agreement.htm" );

        if ( !softwareAgreementFile.exists() ) {
            JOptionPane.showMessageDialog( null, "Could not find software-agreement.htm", "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }

        try {
            String text = FileUtils.loadFileAsString( softwareAgreementFile );

            text = text.replaceAll( "\n", "" );
            text = text.replaceAll( "\r", "" );

            text = text.replaceAll( "'", "\\\\'" );

            Date now = new Date();

            String aString = "// SoftwareAgreement.as\n//\n// Contains the text of the software agreement\n";

            //aString += "// Generated from PBG at " + now.toString() + "\n\n";

            aString += "\nclass edu.colorado.phet.flashcommon.SoftwareAgreement {\n\tpublic static var agreementText : String = '";
            aString += text;
            aString += "';\n}\n";

            File actionScriptSoftwareAgreementFile = new File( trunk, "simulations-flash/common/src/edu/colorado/phet/flashcommon/SoftwareAgreement.as" );

            FileUtils.writeString( actionScriptSoftwareAgreementFile, aString );

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
