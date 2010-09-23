package edu.colorado.phet.flashcommon {
// Informative dialog
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.event.FrameEvent;
import org.aswing.geom.IntDimension;

class MessageDialog extends CommonDialog {

    public var okButton:JButton;

    public function MessageDialog( title:String, message:String, ok:Boolean ) {
        super( "message" + String( Math.round( Math.random() * 50000 ) ), title )

        // layout the window vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // get the string to display
        var str:String = message;

        var textArea:JTextArea = new JTextArea( str, 0, 40 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.setWidth( 400 );
        textArea.setBackground( common.backgroundColor );
        // add padding around the text
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 0, 5 ) ) );

        window.getContentPane().append( textArea );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        var panel:JPanel = new JPanel( new BoxLayout() );

        if ( ok ) {
            okButton = new JButton( CommonStrings.get( "OK", "OK" ) );
        }
        else {
            okButton = new JButton( CommonStrings.get( "Close", "Close" ) );
        }

        okButton.addEventListener( MouseEvent.CLICK, closeClicked );
        CommonButtons.padButtonAdd( okButton, panel );

        var centerPanel:JPanel = new JPanel( new CenterLayout() ); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
        centerPanel.append( panel );
        window.getContentPane().append( centerPanel );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        tabHandler.addAsWingButton( okButton );
    //    }

    override public function closeClicked( evt:FrameEvent ):void {
        manualClose();
    }
}
}