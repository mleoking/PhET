// MessageDialog.as
//
// Informative dialog
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

import edu.colorado.phet.flashcommon.old.*;

class edu.colorado.phet.flashcommon.old.MessageDialog extends edu.colorado.phet.flashcommon.old.CommonDialog {

    public var okButton : JButton;
	
	public function MessageDialog( title : String, message : String, ok : Boolean ) {
        super( "message" + String( Math.round( Math.random() * 50000 ) ), title )
		
		// layout the window vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// get the string to display
		var str : String = message;
		
		var textArea = new JTextArea(str, 0, 40);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setWordWrap(true);
		textArea.setWidth(400);
		textArea.setBackground(common.backgroundColor);
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 0, 5)));
				
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		var panel : JPanel = new JPanel(new BoxLayout());
		
		if(ok) {
			okButton = new JButton(SimStrings.get("OK", "OK"));
		} else {
			okButton = new JButton(SimStrings.get("Close", "Close"));
		}
		
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, closeClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		displayMe();
	}

    public function setupTabHandler() {
        tabHandler.addAsWingButton( okButton );
    }
	
	public function closeClicked(src : JButton) {
		manualClose();
	}
}
