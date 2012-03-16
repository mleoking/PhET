/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASWingUtils;
import org.aswing.border.BevelBorder;
import org.aswing.colorchooser.ColorRectIcon;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.FlowLayout;
import org.aswing.geom.Point;
import org.aswing.JButton;
import org.aswing.JColorChooser;
import org.aswing.JFrame;
import org.aswing.JWindow;
import org.aswing.MouseManager;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class test.pallette.PalletteDemo extends JWindow {
	
	private var palletteButton:JButton;
	private var palletteIcon:ColorRectIcon;
	private var colorSwatchesWindow:JWindow;
	private var colorSwatches:JColorSwatches;
	private var colorMixerButton:JButton;
	
	private var chooserDialog:JFrame;
	private var colorChooser:JColorChooser;
	
	public function PalletteDemo(){
		super();
		palletteIcon = new ColorRectIcon(20, 20);
		palletteButton = new JButton(palletteIcon);
		
		getContentPane().setLayout(new FlowLayout());
		getContentPane().append(palletteButton);
		palletteButton.addActionListener(__openColorSwaches, this);
		
		colorMixerButton = new JButton(" M ");
		colorSwatches = new JColorSwatches();
		colorSwatches.setAlphaSectionVisible(false);
		colorSwatches.addComponentColorSectionBar(colorMixerButton);
		colorSwatchesWindow = new JWindow(this, false);
		colorSwatchesWindow.setBorder(new BevelBorder(null, BevelBorder.RAISED));
		colorSwatchesWindow.setContentPane(colorSwatches);
		colorSwatchesWindow.pack();
		
		colorChooser = new JColorChooser();
		colorChooser.addColorAdjustingListener(__colorAdjusting, this);
		//colorChooser.setAlphaSectionVisible(false);
		chooserDialog = JColorChooser.createDialog(colorChooser, this, "Chooser a color to test", 
			false, Delegate.create(this, __colorSelectedInDialog),  Delegate.create(this, __colorSeletionCanceldInDialog));
		//center it
		var location:Point = ASWingUtils.getScreenCenterPosition();
		location.x -= chooserDialog.getWidth()/2;
		location.y -= chooserDialog.getHeight()/2;
		location.x = Math.round(location.x);
		location.y = Math.round(location.y);
		chooserDialog.setLocation(location);
		
		colorMixerButton.addActionListener(__openColorMixer, this);
		
		colorSwatches.getModel().addChangeListener(__colorSelectionChanged, this);
		colorSwatches.setSelectedColor(null);
		colorSwatches.setNoColorSectionVisible(true);
		MouseManager.addEventListener(MouseManager.ON_MOUSE_DOWN, __onMouseDown, this);
	}
	
	private function __onMouseDown():Void{
		if(colorSwatchesWindow.isVisible()){
			if(!colorSwatchesWindow.hitTestMouse()){
				colorSwatchesWindow.hide();
			}
		}
	}
	
	private function __openColorSwaches():Void{
		var pos:Point = getMousePosition();
		pos.y += 10;
		colorSwatchesWindow.setLocation(pos);
		colorSwatchesWindow.show();
	}
	
	private function __openColorMixer():Void{
		colorSwatchesWindow.hide();
		var time:Number = getTimer();
		colorChooser.setSelectedColor(colorSwatches.getSelectedColor());
		//colorChooser.getTabbedPane().setSelectedIndex(0);
		trace("init time : " + (getTimer() - time));
		time = getTimer();
		chooserDialog.show();
		trace("show time : " + (getTimer() - time));
	}
	
	private function __colorSelectionChanged():Void{
		colorSwatchesWindow.hide();
		palletteIcon.setColor(colorSwatches.getSelectedColor());
		palletteButton.repaint();
	}
	
	private function __colorAdjusting(source, color:ASColor):Void{
		palletteIcon.setColor(color);
		palletteButton.repaint();
	}
	
	private function __colorSelectedInDialog(color:ASColor):Void{
		colorSwatches.setSelectedColor(color);
	}
	
	private function __colorSeletionCanceldInDialog():Void{
		palletteIcon.setColor(colorSwatches.getSelectedColor());
		palletteButton.repaint();
	}
	
	public static function main():Void {
		Stage.scaleMode = "noScale";
		try{
			var fj:PalletteDemo = new PalletteDemo();
			fj.setBounds(0, 0, 550, 400);
			fj.setVisible(true);
		}catch(e:Error){
			trace("Error : " + e);
		}
	}
}