package edu.colorado.phet.flashcommon {

import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.system.Capabilities;

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

public class AboutDialog extends CommonDialog {

    public var agreementButton: JButton;
    public var creditsButton: JButton;
    public var okButton: JButton;

    private static var instance: AboutDialog = null;

    public static function getInstance(): AboutDialog {
        return instance;
    }

    public function AboutDialog() {
        super( "about", CommonStrings.get( "AboutSim", "About {0}", [ FlashCommon.getInstance().getSimTitle() ] ) );
        instance = this;

        // layout things vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // construct the string of text to show
        var str: String = "";
        str += "<b>PhET Interactive Simulations</b>\n";
        str += "Copyright \u00A9 2004-2011 University of Colorado.\n";
        str += "Some rights reserved.\n";
        str += "Visit <a href='event:http://" + FlashCommon.getMainServer() + "'>http://" + FlashCommon.getMainServer() + "</a>\n\n";

        str += "<b><font size='16'>" + common.getSimTitle() + "</font></b>\n";
        str += CommonStrings.get( "Version", "Version" ) + ": " + common.getFullVersionString() + "\n";
        const timestampString: String = common.getVersionTimestampString();
        var dateString: String = FlashCommon.NULLVAL;
        if ( timestampString != FlashCommon.NULLVAL ) {
            dateString = FlashCommon.dateString( FlashCommon.dateOfSeconds( parseInt( timestampString ) ) );
        }
        str += CommonStrings.get( "BuildDate", "Build Date" ) + ": " + dateString + "\n";
        if ( common.getDistributionTag() != null || common.getSimName() == "flash-common-strings" ) {
            str += CommonStrings.get( "Distribution", "Distribution" ) + ": " + String( common.getDistributionTag() ) + "\n";
        }
        str += "\n";
        str += CommonStrings.get( "FlashVersion", "Flash Version" ) + ": " + Capabilities.version + "\n";
        str += CommonStrings.get( "OSVersion", "OS Version" ) + ": " + Capabilities.os + "\n";

        // create CSS to make links blue
        //var css : TextField.StyleSheet = new TextField.StyleSheet();
        //css.parseCSS( FlashCommon.DISPLAY_CSS );

        var textArea: JTextArea = new JTextArea();
        textArea.setHtmlText( str );
        textArea.addEventListener(TextEvent.LINK,function(event:TextEvent):void{common.openExternalLink(event.text)});
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );
        textArea.setBackground( common.backgroundColor );
        textArea.setWidth( 250 );

        window.getContentPane().append( textArea );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // panel to lay the buttons in
        var panel: JPanel = new JPanel( new FlowLayout() );

        // button that will open the agreements dialog
        str = CommonStrings.get( "SoftwareAgreement", "Software Agreement" );
        // temporary fix for improper rendering of Arabic strings
        if ( common.getLocale() == "ar" ) {
            str = "..." + str;
        }
        else {
            str += "...";
        }
        agreementButton = new JButton( str );
        agreementButton.addEventListener( MouseEvent.CLICK, agreementClicked );
        CommonButtons.padButtonAdd( agreementButton, panel );

        // button that will open the credits dialog
        str = CommonStrings.get( "Credits", "Credits" );
        // temporary fix for improper rendering of Arabic strings
        if ( common.getLocale() == "ar" ) {
            str = "..." + str;
        }
        else {
            str += "...";
        }
        creditsButton = new JButton( str );
        creditsButton.addEventListener( MouseEvent.CLICK, creditsClicked );
        CommonButtons.padButtonAdd( creditsButton, panel );

        // button will close the about dialog
        okButton = new JButton( CommonStrings.get( "OK", "OK" ) );
        okButton.addEventListener( MouseEvent.CLICK, okClicked );
        CommonButtons.padButtonAdd( okButton, panel );

        var centerPanel: JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        displayMe();
    }

    //    override public function setupTabHandler():void {
    //        //        tabHandler.addAsWingButton( agreementButton );
    //        //		tabHandler.addAsWingButton( creditsButton );
    //        //		tabHandler.addAsWingButton( okButton );
    //    }

    public function agreementClicked( evt: MouseEvent ): void {
        CommonDialog.openAgreementDialog();
    }

    public function okClicked( evt: MouseEvent ): void {
        manualClose();
    }

    public function creditsClicked( evt: MouseEvent ): void {
        CommonDialog.openCreditsDialog();
    }
}
}