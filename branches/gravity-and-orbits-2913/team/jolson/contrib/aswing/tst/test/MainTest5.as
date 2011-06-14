/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.JButton;
import org.aswing.JPieChart;
import org.aswing.JToggleButton;
import org.aswing.MCPanel;

import test.ColorIcon;
/**
* The main testing of AsWing.
*
* @author iiley
*/
class test.MainTest5 extends MCPanel
{
	private var button2 : AbstractButton;
	private var button1 : AbstractButton;
	private var centerButton : AbstractButton;
	private var pieTest : JPieChart;
	public function MainTest5 ()
	{
		super (_root, 400, 400);
		setLayout (new BorderLayout ());
		button2 = new JToggleButton ("JToggleButton Switch TopButton enable");
		button1 = new JButton ("JButton Switch CenterButton enable");
		var dataSet : Array = [20, 34, 345, 342, 122, 453, 12];
		var colorSet : Array = [0xff0000, 0x00ff00, 0x0000ff];
		pieTest = new JPieChart ("JPieChart", dataSet);
		button1.setEnabled (false);
		append (button1, BorderLayout.NORTH);
		append (button2, BorderLayout.SOUTH);
		append (new JButton (null, new ColorIcon (null, ASColor.RED, 40, 100)) , BorderLayout.WEST);
		append (new JButton (null, new ColorIcon (null, ASColor.BLUE, 40, 100)) , BorderLayout.EAST);
		append (button2, BorderLayout.SOUTH);
		var blackIcon : Icon = new ColorIcon (null, ASColor.BLACK, 40, 100);
		var greenIcon : Icon = new ColorIcon (null, ASColor.GREEN, 40, 100);
		var alphaIcon : Icon = new ColorIcon (null, new ASColor (0x30cc20, 50) , 40, 100);
		centerButton = new JToggleButton ();
		centerButton.setIcon (alphaIcon);
		centerButton.setRollOverIcon (greenIcon);
		centerButton.setSelectedIcon (blackIcon);
		//append(centerButton, BorderLayout.CENTER);
		append (pieTest, BorderLayout.CENTER);
		// Disable a component to receive focus
		setFocusable (false);
		//centerButton.setFocusable(false);
		//Create custom tabbing for each component
		//comment the bottom 2 lines to see different tabbing order
		/*
		Disable / Enable FocusManager
		
		*/
		//FocusManager.getCurrentManager().enabled(false);
		//FocusManager.getCurrentManager().enabled(true);
		//If for some weird reason the user decides to give focus management
		// to another key
		//FocusManager.getCurrentManager().setKeyType(Key.DOWN);
		initHandlers ();
	}
	private function initHandlers () : Void
	{
		button1.addEventListener (Component.ON_RELEASE, _switchCenterButtonEnable, this);
		button2.addEventListener (Component.ON_RELEASE, _switchTopButtonEnable, this);
		pieTest.addEventListener (Component.ON_PRESS, pieClicked, this);
	}
	private function pieClicked (source:JPieChart, data:Object) : Void
	{
		_root.DEBUG.text += "PIE CLICKED " + data + "\n";
	}
	private function _switchCenterButtonEnable () : Void
	{
		centerButton.setEnabled ( ! centerButton.isEnabled ());
	}
	private function _switchTopButtonEnable () : Void
	{
		button1.setEnabled ( ! button1.isEnabled ());
		if (button1.isEnabled ())
		{
			var val : Array = [23, 45, 89];
			pieTest.setDataSet (val);
		}
		else
		{
			var val : Array = [223, 189, 34, 89, 456, 233, 42];
			pieTest.setDataSet (val);
		}
	}
	public static function main () : Void
	{
		_root.createTextField ("DEBUG", 10000, 500, 300, 300, 300);
		var db : TextField = _root.DEBUG;
		db.multiline = true;
		var format : TextFormat = new TextFormat ();
		format.align = "right"; 
		db.setTextFormat (format);
		db.text = "TESTING";
		trace("init Flashout");
		try
		{
			trace ("try MainTest2");
			var p : MainTest5 = new MainTest5 ();
			trace ("done MainTest2");
		}catch (e)
		{
			trace ("error : " + e);
		}
	}
}
