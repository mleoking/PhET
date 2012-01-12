// UpdateInstallationDialog.as
//
// Shown when a more recent version of the installation is
// detected to exist
//
// Author: Jonathan Olson

import org.aswing.ASColor;
import org.aswing.ASFont;
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

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.UpdateInstallationDialog extends edu.colorado.phet.flashcommon.CommonDialog {
	
	public var newInstallerTimestamp : Number;
	public var newInstallerAskLaterDays : Number;
    public var yesButton : JButton;
    public var askLaterButton : JButton;
    public var tellMoreButton : JButton;
	
	public function UpdateInstallationDialog( installerTimestamp : Number, installerAskLaterDays : Number ) {
        super( "updateInstallation", _level0.common.strings.get( "NewVersionAvailable", "New Version Available" ) );

		// store new version information so they can be stored if the user decides to skip it
		newInstallerTimestamp = installerTimestamp;
		newInstallerAskLaterDays = installerAskLaterDays;
		
		// layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		
		// construct the string of text to show
		var str : String = "";
		
		str += common.strings.get("InstallationDated", "Your PhET Offline Website Installation is dated {0}.", [FlashCommon.dateString(FlashCommon.dateOfSeconds(common.getInstallerCreationTimestamp()))]) + "\n";
		str += common.strings.get("InstallationNewer", "A newer version is available, dated {0}.", [FlashCommon.dateString(FlashCommon.dateOfSeconds(newInstallerTimestamp))]) + "\n";
		str += "\n";
		str += common.strings.get("InstallerAsk", "Would you like to get the latest PhET Offline Website Installer?");
		
		var textArea = new JTextArea(str, 0, 0);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setWordWrap(true);
		textArea.setMultiline(true);
		
		textArea.setBackground(common.backgroundColor);
		
		// add padding around the text
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setWidth(350);
		
		window.getContentPane().append(textArea);
		
		window.getContentPane().append(new JSpacer(5, 5));
		
		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());
		
		yesButton = new JButton(common.strings.get("Yes!", "Yes!"));
		yesButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, yesClicked));
		yesButton.setForeground(ASColor.BLUE);
		yesButton.setFont(new ASFont(ASFont.DEFAULT_NAME, ASFont.DEFAULT_SIZE + 2, true, false, false));
		yesButton.setUseHandCursor(true);
		CommonButtons.padButtonAdd(yesButton, panel);
		
		askLaterButton = new JButton(common.strings.get("AskLater", "Ask me later"));
		askLaterButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, askLaterClicked));
		CommonButtons.padButtonAdd(askLaterButton, panel);
		
		tellMoreButton = new JButton(common.strings.get("TellMore", "Tell me more..."));
		tellMoreButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, tellMoreClicked));
		CommonButtons.padButtonAdd(tellMoreButton, panel);
		
		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);

		displayMe();
	}

    public function setupTabHandler() {
        tabHandler.addAsWingButton( yesButton );
        tabHandler.addAsWingButton( askLaterButton );
        tabHandler.addAsWingButton( tellMoreButton );
    }
	
	public function onClose() {
		super.onClose();
        
		common.updateHandler.handleResponse();
	}
	
	public function yesClicked(src : JButton) {
		common.openExternalLink( "http://" + FlashCommon.getMainServer() + "/get_phet/full_install.php" );
	}
	
	public function askLaterClicked(src : JButton) {
		manualClose();
		
		common.preferences.setInstallationAskLater(newInstallerAskLaterDays);
		common.updateHandler.handleResponse();
	}
	
	public function tellMoreClicked(src : JButton) {
		CommonDialog.openUpdateInstallationDetailsDialog();
	}
}
