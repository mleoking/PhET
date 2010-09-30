package edu.colorado.phet.flashcommon {
// Shown when a more recent version of the simulation is
// detected to exist
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

import flash.events.TextEvent;

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.CenterLayout;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.geom.IntDimension;

class UpdateSimDialog extends CommonDialog {

    public var newMajorVersion: Number;
    public var newMinorVersion: Number;
    public var newSimAskLaterDays: Number;
    public var newRevision: Number;

    public var tryButton: JButton;
    public var askLaterButton: JButton;
    public var skipButton: JButton;

    private static var instance: UpdateSimDialog = null;

    public static function getInstance(): UpdateSimDialog {
        return instance;
    }

    public function UpdateSimDialog( versionMajor: Number, versionMinor: Number, versionDev: Number, versionRevision: Number, simAskLaterDays: Number ) {
        super( "updateSim", CommonStrings.get( "NewVersionAvailable", "New Version Available" ) );
        instance = this;

        // store new version information so they can be stored if the user decides to skip it
        newMajorVersion = versionMajor;
        newMinorVersion = versionMinor;
        newSimAskLaterDays = simAskLaterDays;
        newRevision = versionRevision;

        // layout things vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // construct the string of text to show
        var str: String = "";
        str += CommonStrings.get( "CurrentVersionIs", "Your current version of {0} is {1}.", ["<b>" + common.getSimTitle() + "</b>", common.getShortVersionString()] ) + "\n";
        str += CommonStrings.get( "NewerVersionIs", "A newer version {0} is available.", ["(" + common.zeroPadVersion( versionMajor, versionMinor ) + ")"] ) + "\n";

        var notUpdateStr: String = "<p align='center'><font color='#880000'>" + CommonStrings.get( "NotUpdateSim", "This simulation cannot be updated automatically." ) + "</font></p>";

        str += "\n";

        if ( (common.fromFullInstallation() && common.updateHandler.simTimestamp + 7200 < common.updateHandler.installerTimestamp) || common.getSimName() == "flash-common-strings" ) {
            // sim should be contained in the newest installation, otherwise we would not reach here
            str += notUpdateStr;
            str += "\n";

            var defaultStr: String = "To download a new installation containing the latest simulation, ";
            defaultStr += "please visit the <a href='{0}'>PhET installation page</a> for more information.";

            str += CommonStrings.get( "PhETInstallation", defaultStr, ["event:http://" + FlashCommon.getMainServer() + "/get_phet/full_install.php"] );

            str += "\n";
        }
        else {
            str += notUpdateStr;
        }

        str += "\n";
        str += CommonStrings.get( "UpdateOptionsAvailable", "Update options are available under <i>Preferences</i>." );


        var textArea: JTextArea = new JTextArea( str, 0, 30 );
        textArea.setHtmlText( str );
        textArea.addEventListener(TextEvent.LINK,function(event:TextEvent):void{common.openExternalLink(event.text)});
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.setWidth( 200 );
        textArea.setBackground( common.backgroundColor );
        // add padding around the text
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );

        window.getContentPane().append( textArea );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // panel to lay the buttons in
        var panel: JPanel = new JPanel( new FlowLayout() );

        tryButton = new JButton( CommonStrings.get( "TryNow", "Try it now" ) );
        tryButton.addEventListener( MouseEvent.CLICK, tryClicked );
        tryButton.setForeground( ASColor.BLUE );
        tryButton.setFont( new ASFont( "Tahoma", 13, true, false, false ) );
        //		tryButton.setUseHandCursor(true);
        tryButton.useHandCursor = true;
        CommonButtons.padButtonAdd( tryButton, panel );

        askLaterButton = new JButton( CommonStrings.get( "AskLater", "Ask me later" ) );
        askLaterButton.addEventListener( MouseEvent.CLICK, askLaterClicked );
        CommonButtons.padButtonAdd( askLaterButton, panel );

        skipButton = new JButton( CommonStrings.get( "PrivacySkip", "Skip this update" ) );
        skipButton.addEventListener( MouseEvent.CLICK, skipClicked );
        CommonButtons.padButtonAdd( skipButton, panel );

        var centerPanel: JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        tabHandler.addAsWingButton( tryButton );
    //        tabHandler.addAsWingButton( askLaterButton );
    //        tabHandler.addAsWingButton( skipButton );
    //    }

    public function askLaterClicked( evt: MouseEvent ): void {
        // always ask later on if time has elapsed
        common.preferences.setSkippedRevision( 0 );

        // record the time the user clicked this
        common.preferences.setSimAskLater( newSimAskLaterDays );

        manualClose()
    }

    public function skipClicked( evt: MouseEvent ): void {
        // skip this update in the future
        common.preferences.setSkippedRevision( newRevision );

        manualClose();
    }

    public function tryClicked( evt: MouseEvent ): void {
        common.openExternalLink( common.simWebsiteURL() );
    }
}
}