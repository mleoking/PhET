import org.aswing.EmptyLayout;
import org.aswing.JButton;
import org.aswing.JWindow;
/*
 Copyright aswing.org, see the LICENCE.txt.
*/

/**
 * @author iiley
 */
class test.ButtonTest {
	
	public static function main():Void{
		var window:JWindow = new JWindow();
		var button:JButton = new JButton("Popygup 1");
		button.setSize(button.getPreferredSize());
		window.getContentPane().setLayout(new EmptyLayout());
		window.getContentPane().append(button);
		window.setBounds(0, 0, 300, 300);
		window.show();
	}
	
}