/*
 Copyright aswing.org, see the LICENCE.txt.
*/

package org.aswing.plaf.basic { 

import flash.events.*;

import org.aswing.*;
import org.aswing.border.BevelBorder;
import org.aswing.border.EmptyBorder;
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.colorchooser.JColorMixer;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.colorchooser.PreviewColorIcon;
import org.aswing.event.InteractiveEvent;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.BaseComponentUI;

/**
 * @author iiley
 * @private
 */
public class BasicColorChooserUI extends BaseComponentUI {
	
	private var chooser:JColorChooser;
	private var previewColorLabel:JLabel;
	private var previewColorIcon:PreviewColorIcon;
	
	public function BasicColorChooserUI(){
		super();		
	}

    protected function getPropertyPrefix():String {
        return "ColorChooser.";
    }

    override public function installUI(c:Component):void{
		chooser = JColorChooser(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
		chooser = JColorChooser(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():void{
		var pp:String = getPropertyPrefix();
        LookAndFeel.installColorsAndFont(chooser, pp);
		LookAndFeel.installBasicProperties(chooser, pp);
        LookAndFeel.installBorderAndBFDecorators(chooser, pp);
	}
    private function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(chooser);
    }	
    
	private function installComponents():void{
		addChooserPanels();
		previewColorLabel = createPreviewColorLabel();
		previewColorLabel.setUIElement(true);
		previewColorIcon = createPreviewColorIcon();
		previewColorLabel.setIcon(previewColorIcon);
		previewColorIcon.setColor(chooser.getSelectedColor());
		
		layoutComponents();
		updateSectionVisibles();
    }
	private function uninstallComponents():void{
		chooser.removeAllChooserPanels();
    }
        
	private function installListeners():void{
		chooser.addEventListener(InteractiveEvent.STATE_CHANGED, __selectedColorChanged);
	}
    private function uninstallListeners():void{
    	chooser.removeEventListener(InteractiveEvent.STATE_CHANGED, __selectedColorChanged);
    }
    
    //------------------------------------------------------------------------------
    
	private function __selectedColorChanged(e:Event):void{
		previewColorIcon.setPreviousColor(previewColorIcon.getCurrentColor());
		previewColorIcon.setCurrentColor(chooser.getSelectedColor());
		previewColorLabel.repaint();
	}

	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		previewColorIcon.setColor(chooser.getSelectedColor());
		previewColorLabel.repaint();
		updateSectionVisibles();
	}
	private function updateSectionVisibles():void{
		for(var i:Number=0; i<chooser.getChooserPanelCount(); i++){
			var pane:AbstractColorChooserPanel = chooser.getChooserPanelAt(i);
			pane.setAlphaSectionVisible(chooser.isAlphaSectionVisible());
			pane.setHexSectionVisible(chooser.isHexSectionVisible());
			pane.setNoColorSectionVisible(chooser.isNoColorSectionVisible());
		}
	}
	//*******************************************************************************
	//       Override these methods to easiy implement different look
	//*******************************************************************************
	
	private function layoutComponents():void{
		chooser.setLayout(new BorderLayout(6, 6));	
		chooser.append(chooser.getTabbedPane(), BorderLayout.CENTER);
		var bb:BevelBorder = new BevelBorder(new EmptyBorder(null, new Insets(0, 0, 2, 0)), BevelBorder.LOWERED);
		chooser.getTabbedPane().setBorder(bb);
		
		var rightPane:Container = SoftBox.createVerticalBox(6, SoftBoxLayout.TOP);
		chooser.getCancelButton().setMargin(new Insets(0, 5, 0, 5));
		rightPane.append(chooser.getOkButton());
		rightPane.append(chooser.getCancelButton());
		rightPane.append(new JLabel("Old"));
		rightPane.append(AsWingUtils.createPaneToHold(previewColorLabel, new CenterLayout()));
		rightPane.append(new JLabel("Current"));
		chooser.append(rightPane, BorderLayout.WEST);
	}
	
    private function addChooserPanels():void{
    	var colorS:JColorSwatches = new JColorSwatches();
    	var colorM:JColorMixer = new JColorMixer();
    	colorS.setUIElement(true);
    	colorM.setUIElement(true);
    	chooser.addChooserPanel("Color Swatches", colorS);
    	chooser.addChooserPanel("Color Mixer", colorM);
    }
    
	private function createPreviewColorIcon():PreviewColorIcon{
		return new PreviewColorIcon(60, 60, PreviewColorIcon.VERTICAL);
	}
	
	private function createPreviewColorLabel():JLabel{
		var label:JLabel = new JLabel();
		var bb:BevelBorder = new BevelBorder(null, BevelBorder.LOWERED);
		bb.setThickness(1);
		label.setBorder(bb); 
		return label;
	}
}
}