/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.BorderLayout;
import org.aswing.Component;
import org.aswing.Icon;
import org.aswing.JButton;
import org.aswing.JToggleButton;
import org.aswing.MCPanel;

import test.ColorIcon;

/**
 * The main testing of AsWing.
 * 
 * @author iiley
 */
class test.MainTest2 extends MCPanel{

	private var button2:AbstractButton;
	private var button1:AbstractButton;
	private var centerButton:AbstractButton;
	
	public function MainTest2(){
		super(_root, 400, 400);
		setLayout(new BorderLayout());
		button2 = new JToggleButton("JToggleButton Switch TopButton enable");
		button1 = new JButton("JButton Switch CenterButton enable");
		button1.setEnabled(false);
		append(button1, BorderLayout.NORTH);
		append(button2, BorderLayout.SOUTH);
		append(new JButton(null, new ColorIcon(null, ASColor.RED, 40, 100)), BorderLayout.WEST);
		append(new JButton(null, new ColorIcon(null, ASColor.BLUE, 40, 100)), BorderLayout.EAST);
		append(button2, BorderLayout.SOUTH);
		var blackIcon:Icon = new ColorIcon(null, ASColor.BLACK, 40, 100);
		var greenIcon:Icon = new ColorIcon(null, ASColor.GREEN, 40, 100);
		var alphaIcon:Icon = new ColorIcon(null, new ASColor(0x30cc20, 50), 40, 100);
		centerButton = new JToggleButton();
		centerButton.setIcon(alphaIcon);
		centerButton.setRollOverIcon(greenIcon);
		centerButton.setSelectedIcon(blackIcon);
		append(centerButton, BorderLayout.CENTER);
		initHandlers();
	}
	
	private function initHandlers():Void{
		button1.addEventListener(Component.ON_RELEASE, _switchCenterButtonEnable, this);
		button2.addEventListener(Component.ON_RELEASE, _switchTopButtonEnable, this);
	}
	
	private function _switchCenterButtonEnable():Void{
		centerButton.setEnabled(!centerButton.isEnabled());
	}
	private function _switchTopButtonEnable():Void{
		button1.setEnabled(!button1.isEnabled());
	}	
	
	public static function main():Void{

		//Flashout.log("init Flashout");
		try{
			trace("try MainTest2");
			var p:MainTest2 = new MainTest2();
			trace("done MainTest2");
		}catch(e){
			trace("error : " + e);
		}
	}
	
}
