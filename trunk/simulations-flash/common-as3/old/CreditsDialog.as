// CreditsDialog.as
//
// Shows credits information in a window
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.BoxLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.Viewportable;

import edu.colorado.phet.flashcommon.old.*;

class edu.colorado.phet.flashcommon.old.CreditsDialog extends edu.colorado.phet.flashcommon.old.CommonDialog {

    public var closeButton : JButton;
    public var textArea : JTextArea;
    public var textScroll : JScrollPane;
	
	public function CreditsDialog() {
        super( "credits", _level0.SimStrings.get("Credits", "Credits" ) );
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = "";
		var defaultString : String = "";
		str += "<font size='17'>" + SimStrings.get("PhetDevelopmentTeam", "PhET Development Team ") + "</font>\n";
		str += common.getCreditsText();
		str += "\n\n";
		
		str += "<font size='17'>" + SimStrings.get("LicenseLibraries", "Used Library Licensing Information:") + "</font>\n";
		var aswingText = "For AsWing A2:\n2005-2006(c) AsWing.org.\nAll rights reserved.\n\nRedistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:\n\n1) Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n\n2) Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.\n\n3) Neither the name AsWing.org nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.\n\n\nTHIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND ANY\nEXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF\nMERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL\nTHE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,\nSPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT\nOF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)\nHOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR\nTORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\nSOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
		str += aswingText;
		
		textArea = new JTextArea(str, 0, 40);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setWordWrap(true);
		textArea.setWidth(400);
		textArea.setBackground(common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
				
		textScroll = new JScrollPane(textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED);
		textScroll.setPreferredSize(400, 300);
		textScroll.setBorder(new EmptyBorder(new LineBorder(null, ASColor.GRAY, 1, 0), new Insets(5, 5, 5, 5)));
		window.getContentPane().append(textScroll);
		
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// add the OK button
		var panel : JPanel = new JPanel(new BoxLayout());
		closeButton = new JButton(SimStrings.get("Close", "Close"));
		closeButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, closeClicked));
		CommonButtons.padButtonAdd(closeButton, panel);
		window.getContentPane().append(panel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		displayMe();
	}

    public function setupTabHandler() {
        var areaEntry = new TabEntry( textArea.trigger_mc, TabHandler.HIGHLIGHT_LOCAL, textScroll.getVerticalScrollBar().target_mc );
        tabHandler.addEntry( areaEntry );

        // Page Down
        tabHandler.registerKey( textArea.trigger_mc, 34, function() { _level0.creditsDialog.scroll( 10 ); } )

        // Page Up
        tabHandler.registerKey( textArea.trigger_mc, 33, function() { _level0.creditsDialog.scroll( -10 ); } )

        // up key
        tabHandler.registerKey( textArea.trigger_mc, 38, function() { _level0.creditsDialog.scroll( -2 ); } )

        // down key
        tabHandler.registerKey( textArea.trigger_mc, 40, function() { _level0.creditsDialog.scroll( 2 ); } )


        tabHandler.addAsWingButton( closeButton );
    }

    public function scroll( amount : Number ) {
        var view : Viewportable = textScroll.getViewport();
        view.setViewPosition( view.getViewPosition().move( 0, amount ) );
    }
	
	public function closeClicked(src : JButton) {
		manualClose();
	}
}
