package edu.colorado.phet.flashcommon {
// Shows the session-start statistics message, with other text
//
// Author: Jonathan Olson

import flash.events.MouseEvent;

import org.aswing.ASColor;
import org.aswing.BoxLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.geom.IntDimension;

class StatisticsDetailsDialog extends CommonDialog {

    public var okButton:JButton;
    public var textArea:JTextArea;
    public var detailsScroll:JScrollPane;

    private static var instance:StatisticsDetailsDialog = null;

    public static function getInstance():StatisticsDetailsDialog {
        return instance;
    }

    public function StatisticsDetailsDialog() {
        super( "statisticsDetails", CommonStrings.get( "PrivacyDetails", "Privacy Details" ) );
        instance = this;

        // layout the window vertically
        window.getContentPane().setLayout( new SoftBoxLayout( SoftBoxLayout.Y_AXIS ) );

        // get the string to display
        var str:String = "";
        str += CommonStrings.get( "SentInformation", "The information shown below will be sent to PhET when the simulation starts." ) + "\n\n";
        str += "<font size=\"12\">"
        // insert what would be sent as the message. we need to unescape strings so they will be
        // correctly viewable
        str += common.statistics.sessionStartMessage( true );
        str += "</font>"

        textArea = new JTextArea( str, 0, 40 );
        textArea.setHtmlText( str );
        textArea.setEditable( false );
        textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
        textArea.setWordWrap( true );
        textArea.setWidth( 300 );
        textArea.setBackground( common.backgroundColor );

        // add padding around the text
        textArea.setBorder( new EmptyBorder( null, new Insets( 5, 5, 5, 5 ) ) );

        detailsScroll = new JScrollPane( textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED );
        detailsScroll.setPreferredSize( new IntDimension( 400, 300 ) );
        detailsScroll.setBorder( new EmptyBorder( new LineBorder( null, ASColor.GRAY, 1, 0 ), new Insets( 5, 5, 5, 5 ) ) );
        window.getContentPane().append( detailsScroll );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        // add the OK button
        var panel:JPanel = new JPanel( new BoxLayout() );
        okButton = new JButton( CommonStrings.get( "OK", "OK" ) );
        okButton.addEventListener( MouseEvent.CLICK, okClicked );
        CommonButtons.padButtonAdd( okButton, panel );
        window.getContentPane().append( panel );

        window.getContentPane().append( new JSpacer( new IntDimension( 5, 5 ) ) );

        displayMe();
    }

    //    public function setupTabHandler() {
    //        var areaEntry = new TabEntry( textArea.trigger_mc, TabHandler.HIGHLIGHT_LOCAL, detailsScroll.getVerticalScrollBar().target_mc );
    //        tabHandler.addEntry( areaEntry );
    //
    //        // Page Down
    //        tabHandler.registerKey( textArea.trigger_mc, 34, function() { _level0.statisticsDetailsDialog.scroll( 10 ); } )
    //
    //        // Page Up
    //        tabHandler.registerKey( textArea.trigger_mc, 33, function() { _level0.statisticsDetailsDialog.scroll( -10 ); } )
    //
    //        // up key
    //        tabHandler.registerKey( textArea.trigger_mc, 38, function() { _level0.statisticsDetailsDialog.scroll( -2 ); } )
    //
    //        // down key
    //        tabHandler.registerKey( textArea.trigger_mc, 40, function() { _level0.statisticsDetailsDialog.scroll( 2 ); } )
    //
    //        tabHandler.addAsWingButton( okButton );
    //    }
    //
    //    public function scroll( amount : Number ) {
    //        var view : Viewportable = detailsScroll.getViewport();
    //        view.setViewPosition( view.getViewPosition().move( 0, amount ) );
    //    }

    public function okClicked( evt:MouseEvent ):void {
        manualClose();
    }
}
}