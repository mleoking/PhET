// UpdateInstallationDetailsDialog.as
//
// Show details about updating an installation
//
// Author: Jonathan Olson

import org.aswing.ASWingUtils;
import org.aswing.CenterLayout;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JFrame;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;

import edu.colorado.phet.flashcommon.old.*;

class edu.colorado.phet.flashcommon.old.UpdateInstallationDetailsDialog extends edu.colorado.phet.flashcommon.old.CommonDialog {

	public var textArea : JTextArea;
    public var okButton : JButton;
	
	public function UpdateInstallationDetailsDialog() {
        super( "updateInstallationDetails", _level0.SimStrings.get( "NewVersionAvailable", "New Version Available") );

		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		
		str += SimStrings.get("InstallerInfo1", "Keeping your PhET Offline Website Installation up-to-date ensures that you have access to the newest PhET simulations and supplemental materials.") + "\n\n";
		str += SimStrings.get("InstallerInfo2", "If you choose to get the newest version, a web browser will open to the PhET website, where you can download the PhET Offline Website Installer.");
		
		var textArea = new JTextArea(str, 0, 0);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setMultiline(true);
		textArea.setBackground(common.backgroundColor);
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setWidth(250);
		textArea.setWordWrap(true);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());
		
		okButton = new JButton(SimStrings.get("Close", "Close"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, closeClicked));
		CommonButtons.padButtonAdd(okButton, panel);
		
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);
		
		displayMe();
	}

    public function setupTabHandler() {
        tabHandler.addAsWingButton( okButton );
    }
	
	public function closeClicked(src : JButton) {
		manualClose();
	}
	
}
