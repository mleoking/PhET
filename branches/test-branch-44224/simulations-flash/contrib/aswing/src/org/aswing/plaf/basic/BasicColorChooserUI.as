/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASWingUtils;
import org.aswing.border.BevelBorder;
import org.aswing.border.EmptyBorder;
import org.aswing.BorderLayout;
import org.aswing.CenterLayout;
import org.aswing.colorchooser.AbstractColorChooserPanel;
import org.aswing.colorchooser.JColorMixer;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.colorchooser.PreviewColorIcon;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.Insets;
import org.aswing.JColorChooser;
import org.aswing.JLabel;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ColorChooserUI;
import org.aswing.SoftBox;
import org.aswing.SoftBoxLayout;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicColorChooserUI extends ColorChooserUI {
	
	private var chooser:JColorChooser;
	private var previewColorLabel:JLabel;
	private var previewColorIcon:PreviewColorIcon;
	private var modelListener:Object;
	
	public function BasicColorChooserUI(){
	}

    public function installUI(c:Component):Void{
		chooser = JColorChooser(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		chooser = JColorChooser(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		var pp:String = "ColorChooser.";
        LookAndFeel.installColorsAndFont(chooser, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBasicProperties(chooser, pp);
        LookAndFeel.installBorder(chooser, pp + "border");
	}
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(chooser);
    }	
    
	private function installComponents():Void{
		addChooserPanels();
		previewColorLabel = createPreviewColorLabel();
		previewColorIcon = createPreviewColorIcon();
		previewColorLabel.setIcon(previewColorIcon);
		previewColorIcon.setColor(chooser.getSelectedColor());
		
		layoutComponents();
		updateSectionVisibles();
    }
	private function uninstallComponents():Void{
		chooser.removeAllChooserPanels();
    }
        
	private function installListeners():Void{
		modelListener = chooser.addChangeListener(__selectedColorChanged, this);
	}
    private function uninstallListeners():Void{
    }
    
    //------------------------------------------------------------------------------
    
	private function __selectedColorChanged():Void{
		previewColorIcon.setPreviousColor(previewColorIcon.getCurrentColor());
		previewColorIcon.setCurrentColor(chooser.getSelectedColor());
		previewColorLabel.repaint();
	}
	private function create(c:Component):Void{
		super.create(c);
		updateSectionVisibles();
	}
	private function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		previewColorIcon.setColor(chooser.getSelectedColor());
		previewColorLabel.repaint();
		updateSectionVisibles();
	}
	private function updateSectionVisibles():Void{
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
	
	private function layoutComponents():Void{
		chooser.setLayout(new BorderLayout(6, 6));
		chooser.append(chooser.getTabbedPane(), BorderLayout.CENTER);
		var bb:BevelBorder = new BevelBorder(new EmptyBorder(null, new Insets(0, 0, 2, 0)), BevelBorder.LOWERED);
		chooser.getTabbedPane().setBorder(bb);
		
		var rightPane:Container = SoftBox.createVerticalBox(6, SoftBoxLayout.TOP);
		chooser.getCancelButton().setMargin(new Insets(0, 5, 0, 5));
		rightPane.append(chooser.getOkButton());
		rightPane.append(chooser.getCancelButton());
		rightPane.append(new JLabel("Old"));
		rightPane.append(ASWingUtils.createPaneToHold(previewColorLabel, new CenterLayout()));
		rightPane.append(new JLabel("Current"));
		chooser.append(rightPane, BorderLayout.EAST);
	}
	
    private function addChooserPanels():Void{
    	chooser.addChooserPanel("Color Swatches", new JColorSwatches());
    	chooser.addChooserPanel("Color Mixer", new JColorMixer());
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