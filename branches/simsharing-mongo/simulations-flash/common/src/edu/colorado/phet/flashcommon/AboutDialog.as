import org.aswing.CenterLayout;
import org.aswing.FlowLayout;
import org.aswing.Insets;
import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JSpacer;
import org.aswing.JTextArea;
import org.aswing.SoftBoxLayout
import org.aswing.util.Delegate;
import org.aswing.border.EmptyBorder;

import edu.colorado.phet.flashcommon.*;

class edu.colorado.phet.flashcommon.AboutDialog extends edu.colorado.phet.flashcommon.CommonDialog {

    public var agreementButton : JButton;
    public var creditsButton : JButton;
    public var okButton : JButton;

    public function AboutDialog() {
        super( "about", _level0.common.strings.get( "AboutSim", "About {0}", [ _level0.common.getSimTitle() ] ) );

        // layout things vertically
		window.getContentPane().setLayout(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));

		// construct the string of text to show
		var str : String = "";
		str += "<b>PhET Interactive Simulations</b>\n";
		str += "Copyright \u00A9 2004-2011 University of Colorado.\n";
		str += "Some rights reserved.\n";
		str += "Visit <a href='asfunction:_level0.common.openExternalLink,http://" + FlashCommon.getMainServer() + "'>http://" + FlashCommon.getMainServer() + "</a>\n\n";

		str += "<b><font size='16'>" + common.getSimTitle() + "</font></b>\n";
		str += common.strings.get("Version", "Version") + ": " + common.getFullVersionString() + "\n";
		str += common.strings.get("BuildDate", "Build Date") + ": " + FlashCommon.dateString(FlashCommon.dateOfSeconds(common.getVersionTimestamp())) + "\n";
		if(common.getDistributionTag() != null || common.getSimName() == "flash-common-strings") {
			str += common.strings.get("Distribution", "Distribution") + ": " + String(common.getDistributionTag()) + "\n";
		}
		str += "\n";
		str += common.strings.get("FlashVersion", "Flash Version") + ": " + System.capabilities.version + "\n";
		str += common.strings.get("OSVersion", "OS Version") + ": " + System.capabilities.os + "\n";

		// create CSS to make links blue
		//var css : TextField.StyleSheet = new TextField.StyleSheet();
		//css.parseCSS( FlashCommon.DISPLAY_CSS );

		var textArea = new JTextArea(str);
		textArea.setHtml(true);
		textArea.setEditable(false);
		textArea.setCSS( FlashCommon.LINK_STYLE_SHEET );
		textArea.setBorder(new EmptyBorder(null, new Insets(5, 5, 5, 5)));
		textArea.setBackground(common.backgroundColor);
		textArea.setWidth(250);

		window.getContentPane().append(textArea);

		window.getContentPane().append(new JSpacer(5, 5));

		// panel to lay the buttons in
		var panel : JPanel = new JPanel(new FlowLayout());

		// button that will open the agreements dialog
		var str : String = common.strings.get("SoftwareAgreement", "Software Agreement");
		// temporary fix for improper rendering of Arabic strings
		if( common.getLocale() == "ar" ) {
			str = "..." + str;
		} else {
			str += "...";
		}
		agreementButton = new JButton(str);
		agreementButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, agreementClicked));
		CommonButtons.padButtonAdd(agreementButton, panel);

		// button that will open the credits dialog
		str = common.strings.get("Credits", "Credits");
		// temporary fix for improper rendering of Arabic strings
		if( common.getLocale() == "ar" ) {
			str = "..." + str;
		} else {
			str += "...";
		}
		creditsButton = new JButton(str);
		creditsButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, creditsClicked));
		CommonButtons.padButtonAdd(creditsButton, panel);

		// button will close the about dialog
		okButton = new JButton(common.strings.get("OK", "OK"));
		okButton.addEventListener(JButton.ON_RELEASE, Delegate.create(this, okClicked));
		CommonButtons.padButtonAdd(okButton, panel);

		var centerPanel : JPanel = new JPanel(new CenterLayout()); //SoftBoxLayout.X_AXIS, 0, SoftBoxLayout.CENTER
		centerPanel.append(panel);
		window.getContentPane().append(centerPanel);

        displayMe();
    }

    public function setupTabHandler() {
        tabHandler.addAsWingButton( agreementButton );
		tabHandler.addAsWingButton( creditsButton );
		tabHandler.addAsWingButton( okButton );
    }

    public function agreementClicked(src : JButton) {
		CommonDialog.openAgreementDialog();
	}

	public function okClicked(src : JButton) {
		manualClose();
	}

	public function creditsClicked(src : JButton) {
		CommonDialog.openCreditsDialog();
	}
}
