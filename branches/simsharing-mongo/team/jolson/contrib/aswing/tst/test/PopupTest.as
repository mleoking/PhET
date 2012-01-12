import org.aswing.JButton;
import org.aswing.JPanel;
import org.aswing.JPopup;
import org.aswing.SoftBoxLayout;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * @author iiley
 */
class test.PopupTest {
	
	private static var popup:JPopup;
	
	public static function main():Void{
		popup = new JPopup();
		var panel:JPanel = new JPanel(new SoftBoxLayout(SoftBoxLayout.Y_AXIS));
		panel.append(new JButton("Popup"));
		popup.append(panel);
		popup.pack();
		popup.setVisible(true);
	}
	
}