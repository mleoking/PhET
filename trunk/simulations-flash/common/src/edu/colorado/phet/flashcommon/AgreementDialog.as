// AgreementDialog.as
//
// Shows license information in a window
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.BoxLayout;
import org.aswing.CenterLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JScrollPane;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.AgreementDialog extends edu.colorado.phet.flashcommon.CommonDialog {

    public var closeButton : JButton;
	
	public function AgreementDialog() {
        super( "agreement", _level0.common.strings.get( "PhetSoftwareAgreement", "PhET Software Agreement" ) );
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = "";
		var defaultString : String = "";
		//str += "<font size='17'>" + common.strings.get("PhetSoftwareAgreement", "PhET Software Agreement") + "</font>\n";
		str += common.getAgreementText();
		str += "\n\n";
		
		var textArea = new JTextArea(str, 0, 40);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setWordWrap(true);
		textArea.setWidth(400);
		textArea.setBackground(common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
				
		var agreementScroll = new JScrollPane(textArea, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED);
		agreementScroll.setPreferredSize(400, 300);
		agreementScroll.setBorder(new EmptyBorder(new LineBorder(null, ASColor.GRAY, 1, 0), new Insets(5, 5, 5, 5)));
		window.getContentPane().append(agreementScroll);		
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// add the OK button
		var panel : JPanel = new JPanel(new BoxLayout());
		closeButton = new JButton(common.strings.get("Close", "Close"));
		closeButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, closeClicked));
		CommonButtons.padButtonAdd(closeButton, panel);
		
		//window.getContentPane().append(panel);
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		displayMe();
	}

    public function setupTabHandler() {
        tabHandler.addAsWingButton( closeButton );
    }
	
	public function closeClicked( src : JButton ) {
		// make the window invisible
		manualClose();
	}
}
