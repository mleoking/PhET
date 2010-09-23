package edu.colorado.phet.flashcommon {
// Shows license information in a window
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

import org.aswing.ASColor;
import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.event.FrameEvent;
import org.aswing.geom.IntDimension;

public class AgreementDialog extends CommonDialog {

    public var closeButton:JButton;
    public var agreementScroll:JScrollPane;
    public var textArea:JTextArea;

    private static var instance:AgreementDialog = null;

    public static function getInstance():AgreementDialog {
        return instance;
    }

    public function AgreementDialog() {
        super( "agreement", CommonStrings.get( "PhetSoftwareAgreement", "PhET Software Agreement" ) );
        instance = this;

        // layout the window vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // get the string to display
        var str:String = "";
        var defaultString:String = "";
        //str += "<font size='17'>" + CommonStrings.get("PhetSoftwareAgreement", "PhET Software Agreement") + "</font>\n";
        str += common.getAgreementText();
        str += "\n\n";

        textArea = new JTextArea( str, 0, 40 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.setWidth( 400 );
        textArea.setBackground( common.backgroundColor );
        // add padding around the text
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 0, 5 ) ) );

        agreementScroll = new JScrollPane( textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED );
        agreementScroll.setPreferredSize( new IntDimension( 400, 300 ) );
        agreementScroll.setBorder( new EmptyBorder( new LineBorder( null, ASColor.GRAY, 1, 0 ), new Insets( 5, 5, 5, 5 ) ) );
        window.getContentPane().append( agreementScroll );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // add the OK button
        var panel:JPanel = new JPanel( new BoxLayout() );
        closeButton = new JButton( CommonStrings.get( "Close", "Close" ) );
        closeButton.addEventListener( MouseEvent.CLICK, closeClicked );
        CommonButtons.padButtonAdd( closeButton, panel );

        //window.getContentPane().append(panel);
        var centerPanel:JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        var areaEntry = new TabEntry( textArea.trigger_mc, TabHandler.HIGHLIGHT_LOCAL, agreementScroll.getVerticalScrollBar().target_mc );
    //        tabHandler.addEntry( areaEntry );
    //
    //        // Page Down
    //        tabHandler.registerKey( textArea.trigger_mc, 34, function() { _level0.agreementDialog.scroll( 10 ); } )
    //
    //        // Page Up
    //        tabHandler.registerKey( textArea.trigger_mc, 33, function() { _level0.agreementDialog.scroll( -10 ); } )
    //
    //        // up key
    //        tabHandler.registerKey( textArea.trigger_mc, 38, function() { _level0.agreementDialog.scroll( -2 ); } )
    //
    //        // down key
    //        tabHandler.registerKey( textArea.trigger_mc, 40, function() { _level0.agreementDialog.scroll( 2 ); } )
    //
    //
    //        tabHandler.addAsWingButton( closeButton );
    //    }

    //    public function scroll( amount : Number ) {
    //        var view : Viewportable = agreementScroll.getViewport();
    //        view.setViewPosition( view.getViewPosition().move( 0, amount ) );
    //    }

    override public function closeClicked( evt:FrameEvent ):void {
        // make the window invisible
        manualClose();
    }
}
}