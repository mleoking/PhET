package edu.colorado.phet.flashcommon {
// Handles creating and displaying the privacy dialog
//
// Author: Jonathan Olson

import flash.display.Sprite;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.text.StyleSheet;

import org.aswing.BoxLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.geom.IntDimension;

public class PrivacyDialog extends JFrame {

    public var backgroundMC:Sprite;

    public var textArea:JTextArea;
    public var canceled:Boolean;

    public var common:FlashCommon;

    // shorthand for debugging function
    public function debug( str:String ):void {
        FlashCommon.getInstance().debug( str );
    }

    public function PrivacyDialog() {
        // shortcut to FlashCommon, but now with type-checking!
        common = FlashCommon.getInstance();

        // create the background
        backgroundMC = new Sprite();
        backgroundMC.graphics.beginFill( common.getBGColor(), 0.5 );
        // larger dimensions in case people resize afterwards
        backgroundMC.graphics.drawRect( -5000, -5000, 10000, 10000 );
        backgroundMC.graphics.endFill();
        common.root.addChild( backgroundMC );
        debug( "Adding backgroundMC" );

        super( root, CommonStrings.get( "SoftwareAgreement", "Software Agreement" ), true );


        // make this accessible by the asfunction callback in the text
        //		_level0.privacyDialog = this;

        canceled = false;


        // make it catch all mouse clicks, but not show the hand pointer
        backgroundMC.useHandCursor = false;
        backgroundMC.addEventListener( MouseEvent.CLICK, function( evt:MouseEvent ):void {} );

        // we don't want this window closable
        setClosable( false );

        setResizable( false );

        // make sure we can access it from anywhere
        //		_level0.privacyWindow = window;

        // set the background to default
        setBackground( common.backgroundColor );

        // layout things vertically
        getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // construct the string of text to show
        var str:String = "";
        var defaultString:String = "";
        defaultString += "In all PhET simulations, we collect a minimal amount of anonymous <a href='{0}'>information</a> ";
        defaultString += "each time the simulation starts (e.g., simulation version, operating system). ";
        defaultString += "You can disable the sending of this information at any time via the Preferences button."
        str += CommonStrings.get( "PrivacyMessage1", defaultString, ["event:infoClicked"] );
        str += "\n\n";
        defaultString = "By clicking \"Agree and Continue\", you agree to PhET's Software Agreement, ";
        defaultString = "including our privacy policy, for this and every PhET simulation you run. ";
        defaultString = "(For details, <a href='{0}'>click here</a>).";
        str += CommonStrings.get( "PrivacyMessage2", defaultString, ["event:detailsClicked"] );
        //str += "\n";

        // create CSS to make links blue
        var css:StyleSheet = new StyleSheet();
        css.parseCSS( "a:link{color:#0000FF;font-weight:bold;}" +
                      "a:visited{color:#0000FF;font-weight:bold;}" +
                      "a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
                      "a:active{color:#0000FF;font-weight:bold;}" );

        textArea = new JTextArea( str, 0, 30 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( css );
        textArea.setWordWrap( true );
        textArea.setWidth( 300 );
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 0, 5 ) ) );
        textArea.setBackground( common.backgroundColor );

        textArea.addEventListener( TextEvent.LINK, function( evt:TextEvent ):void {
            switch( evt.text ) {
                case "infoClicked":
                    infoClicked();
                    break;
                case "detailsClicked":
                    detailsClicked();
                    break;
            }
        } );

        getContentPane().append( textArea );

        getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // panel to lay the buttons in
        var panel:JPanel = new JPanel( new BoxLayout() );

        // button that will allow us to continue
        var continueButton:JButton = new JButton( CommonStrings.get( "AcceptContinue", "Accept and Continue" ) );
        continueButton.addEventListener( MouseEvent.CLICK, continueClicked );
        CommonButtons.padButtonAdd( continueButton, panel );

        getContentPane().append( panel );

        getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        pack();
        // center the window
        setLocationXY( (common.root.stage.stageWidth - getWidth()) / 2, (common.root.stage.stageHeight - getHeight()) / 2 );
        show();

        //        Key.addListener( this );
    }

    public function continueClicked( evt:MouseEvent ):void {
        // set policy as accepted
        common.preferences.agreeToPrivacy();

        // hide this window
        this.setVisible( false );

        backgroundMC.graphics.clear();
        common.root.removeChild( backgroundMC );

        // continue with common code initialization
        common.postAgreement();
    }

    public function infoClicked():void {
        CommonDialog.openStatisticsDetailsDialog();
    }

    public function detailsClicked():void {
        CommonDialog.openAgreementDialog();
    }

    //    public function onKeyDown() {
    //		if( Key.getCode() == Key.SPACE || Key.getCode() == Key.ENTER ) {
    //			continueClicked( null );
    //            Key.removeListener( this );
    //		}
    //	}
}

}