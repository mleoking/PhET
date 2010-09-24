package edu.colorado.phet.flashcommon {
// Shown when a more recent version of the installation is
// detected to exist
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

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

class UpdateInstallationDialog extends CommonDialog {

    public var newInstallerTimestamp: Number;
    public var newInstallerAskLaterDays: Number;
    public var yesButton: JButton;
    public var askLaterButton: JButton;
    public var tellMoreButton: JButton;

    private static var instance: UpdateInstallationDialog = null;

    public static function getInstance(): UpdateInstallationDialog {
        return instance;
    }

    public function UpdateInstallationDialog( installerTimestamp: Number, installerAskLaterDays: Number ) {
        super( "updateInstallation", CommonStrings.get( "NewVersionAvailable", "New Version Available" ) );
        instance = this;

        // store new version information so they can be stored if the user decides to skip it
        newInstallerTimestamp = installerTimestamp;
        newInstallerAskLaterDays = installerAskLaterDays;

        // layout things vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // construct the string of text to show
        var str: String = "";

        str += CommonStrings.get( "InstallationDated", "Your PhET Offline Website Installation is dated {0}.", [FlashCommon.dateString( FlashCommon.dateOfSeconds( common.getInstallerCreationTimestamp() ) )] ) + "\n";
        str += CommonStrings.get( "InstallationNewer", "A newer version is available, dated {0}.", [FlashCommon.dateString( FlashCommon.dateOfSeconds( newInstallerTimestamp ) )] ) + "\n";
        str += "\n";
        str += CommonStrings.get( "InstallerAsk", "Would you like to get the latest PhET Offline Website Installer?" );

        var textArea = new JTextArea( str, 0, 0 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.getTextField().multiline = true;

        textArea.setBackground( common.backgroundColor );

        // add padding around the text
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );
        textArea.setWidth( 350 );

        window.getContentPane().append( textArea );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // panel to lay the buttons in
        var panel: JPanel = new JPanel( new FlowLayout() );

        yesButton = new JButton( CommonStrings.get( "Yes!", "Yes!" ) );
        yesButton.addEventListener( MouseEvent.CLICK, yesClicked );
        yesButton.setForeground( ASColor.BLUE );
        yesButton.setFont( new ASFont( "Tahoma", 13, true, false, false ) );
        yesButton.useHandCursor = true;
        CommonButtons.padButtonAdd( yesButton, panel );

        askLaterButton = new JButton( CommonStrings.get( "AskLater", "Ask me later" ) );
        askLaterButton.addEventListener( MouseEvent.CLICK, askLaterClicked );
        CommonButtons.padButtonAdd( askLaterButton, panel );

        tellMoreButton = new JButton( CommonStrings.get( "TellMore", "Tell me more..." ) );
        tellMoreButton.addEventListener( MouseEvent.CLICK, tellMoreClicked );
        CommonButtons.padButtonAdd( tellMoreButton, panel );

        var centerPanel: JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        tabHandler.addAsWingButton( yesButton );
    //        tabHandler.addAsWingButton( askLaterButton );
    //        tabHandler.addAsWingButton( tellMoreButton );
    //    }

    override public function onClose(): void {
        super.onClose();

        common.updateHandler.handleResponse();
    }

    public function yesClicked( evt: MouseEvent ): void {
        common.openExternalLink( "http://" + FlashCommon.getMainServer() + "/get_phet/full_install.php" );
    }

    public function askLaterClicked( evt: MouseEvent ): void {
        manualClose();

        common.preferences.setInstallationAskLater( newInstallerAskLaterDays );
        common.updateHandler.handleResponse();
    }

    public function tellMoreClicked( evt: MouseEvent ): void {
        CommonDialog.openUpdateInstallationDetailsDialog();
    }
}
}