package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.buildtools.BuildToolsPaths;
import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.buildtools.util.SVNDependencyProject;

/**
 * Not meant for building purposes, just currently for:
 * (a) representing the dependency on flash-common that all flash simulations have
 * (b) generating actionscript that contains the software agreement. (This is done because it is too large to pass in
 * through FlashVars (and it would have to be passed twice, AND we gain the SWF compression on it)
 */
public class FlashCommonProject extends SVNDependencyProject {
    public FlashCommonProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }

    public static void generateFlashSoftwareAgreement( File trunk ) {
        File softwareAgreementFile = new File( trunk, BuildToolsPaths.SOFTWARE_AGREEMENT_HTML );

        if ( !softwareAgreementFile.exists() ) {
            JOptionPane.showMessageDialog( null, "Could not find software-agreement.htm", "Error", JOptionPane.ERROR_MESSAGE );
            return;
        }

        try {
            String text = FileUtils.loadFileAsString( softwareAgreementFile );

            // strip newlines out of the HTML, since Flash's HTML fields incorrectly add them in as equivalent to <br>s
            text = text.replaceAll( "\n", "" );
            text = text.replaceAll( "\r", "" );

            // escape the single quotes
            text = text.replaceAll( "'", "\\\\'" );

            String aString = "// SoftwareAgreement.as\n//\n// Contains the text of the software agreement\n";

            // these lines screw with version control. if SoftwareAgreement.as is ever taken out of version control,
            // it may be useful to add this back in
            //Date now = new Date();
            //aString += "// Generated from PBG at " + now.toString() + "\n\n";

            aString += "\nclass edu.colorado.phet.flashcommon.SoftwareAgreement {\n\tpublic static var agreementText : String = '";
            aString += text;
            aString += "';\n}\n";

            File actionScriptSoftwareAgreementFile = new File( trunk, BuildToolsPaths.FLASH_SOFTWARE_AGREEMENT_ACTIONSCRIPT );

            FileUtils.writeString( actionScriptSoftwareAgreementFile, aString );

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public boolean isTestable() {
        return false;
    }

    @Override
    public String getType() {
        return "flash-common";
    }
}
