package edu.colorado.phet.flashcommon {
// Show details about updating an installation
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

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

class UpdateInstallationDetailsDialog extends CommonDialog {

    public var textArea:JTextArea;
    public var okButton:JButton;

    private static var instance:UpdateInstallationDetailsDialog = null;

    public static function getInstance():UpdateInstallationDetailsDialog {
        return instance;
    }

    public function UpdateInstallationDetailsDialog() {
        super( "updateInstallationDetails", CommonStrings.get( "NewVersionAvailable", "New Version Available" ) );
        instance = this;

        // layout things vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // construct the string of text to show
        var str:String = "";

        str += CommonStrings.get( "InstallerInfo1", "Keeping your PhET Offline Website Installation up-to-date ensures that you have access to the newest PhET simulations and supplemental materials." ) + "\n\n";
        str += CommonStrings.get( "InstallerInfo2", "If you choose to get the newest version, a web browser will open to the PhET website, where you can download the PhET Offline Website Installer." );

        var textArea:JTextArea = new JTextArea( str, 0, 0 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.getTextField().multiline = true;
        textArea.setBackground( common.backgroundColor );
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );
        textArea.setWidth( 250 );
        textArea.setWordWrap( true );

        window.getContentPane().append( textArea );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // panel to lay the buttons in
        var panel:JPanel = new JPanel( new FlowLayout() );

        okButton = new JButton( CommonStrings.get( "Close", "Close" ) );
        okButton.addEventListener( MouseEvent.CLICK, closeClicked );
        CommonButtons.padButtonAdd( okButton, panel );

        var centerPanel:JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        tabHandler.addAsWingButton( okButton );
    //    }

    override public function closeClicked( evt:MouseEvent ):void {
        manualClose();
    }

}
}