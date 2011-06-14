/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.plaf.basic {
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.MouseEvent;
import flash.geom.Matrix;

import org.aswing.*;
import org.aswing.border.BevelBorder;
import org.aswing.border.EmptyBorder;
import org.aswing.colorchooser.JColorMixer;
import org.aswing.colorchooser.PreviewColorIcon;
import org.aswing.colorchooser.VerticalLayout;
import org.aswing.event.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.BaseComponentUI; 

/**
 * @private
 */
public class BasicColorMixerUI extends BaseComponentUI {
	
	private var colorMixer:JColorMixer;
	private var mixerPanel:JPanel;
	private var HSMC:AWSprite;
	private var HSPosMC:AWSprite;
	private var LMC:AWSprite;
	private var LPosMC:AWSprite;
	private var previewColorLabel:JLabel;
	private var previewColorIcon:PreviewColorIcon;
	private var AAdjuster:JAdjuster;
	private var RAdjuster:JAdjuster;
	private var GAdjuster:JAdjuster;
	private var BAdjuster:JAdjuster;
	private var HAdjuster:JAdjuster;
	private var SAdjuster:JAdjuster;
	private var LAdjuster:JAdjuster;
	private var hexText:JTextField;
			
	public function BasicColorMixerUI(){
		super();		
	}

    protected function getPropertyPrefix():String {
        return "ColorMixer.";
    }
	
    override public function installUI(c:Component):void{
		colorMixer = JColorMixer(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
		colorMixer = JColorMixer(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():void{
		var pp:String = getPropertyPrefix();
        LookAndFeel.installColorsAndFont(colorMixer, pp);
		LookAndFeel.installBasicProperties(colorMixer, pp);
        LookAndFeel.installBorderAndBFDecorators(colorMixer, pp);
	}
    private function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(colorMixer);
    }	
    
	private function installComponents():void{
		mixerPanel = createMixerPanel();
		previewColorLabel = createPreviewColorLabel();
		previewColorIcon = createPreviewColorIcon();
		previewColorLabel.setIcon(previewColorIcon);
		hexText = createHexTextField();
		createAdjusters();
		layoutComponents();
		createHSAndL();
		updateSectionVisibles();
    }
	private function uninstallComponents():void{
		colorMixer.removeAll();
    }
        
	private function installListeners():void{
		colorMixer.addEventListener(InteractiveEvent.STATE_CHANGED, __colorSelectionChanged);
		
		AAdjuster.addActionListener(__AAdjusterValueAction);
		AAdjuster.addStateListener(__AAdjusterValueChanged);
		RAdjuster.addActionListener(__RGBAdjusterValueAction);
		RAdjuster.addStateListener(__RGBAdjusterValueChanged);
		GAdjuster.addActionListener(__RGBAdjusterValueAction);
		GAdjuster.addStateListener(__RGBAdjusterValueChanged);
		BAdjuster.addActionListener(__RGBAdjusterValueAction);
		BAdjuster.addStateListener(__RGBAdjusterValueChanged);
		
		HAdjuster.addActionListener(__HLSAdjusterValueAction);
		HAdjuster.addStateListener(__HLSAdjusterValueChanged);
		LAdjuster.addActionListener(__HLSAdjusterValueAction);
		LAdjuster.addStateListener(__HLSAdjusterValueChanged);
		SAdjuster.addActionListener(__HLSAdjusterValueAction);
		SAdjuster.addStateListener(__HLSAdjusterValueChanged);

		hexText.addActionListener(__hexTextAction);
		hexText.getTextField().addEventListener(Event.CHANGE, __hexTextChanged);
		hexText.getTextField().addEventListener(FocusEvent.FOCUS_OUT, __hexTextAction);
	}
    private function uninstallListeners():void{
    	colorMixer.removeEventListener(InteractiveEvent.STATE_CHANGED, __colorSelectionChanged);
    }
    
    /**
     * Override this method to change different layout
     */
    private function layoutComponents():void{
    	colorMixer.setLayout(new BorderLayout(0, 4));
    	
    	var top:Container = SoftBox.createHorizontalBox(4, SoftBoxLayout.CENTER);
    	top.append(mixerPanel);
    	top.append(previewColorLabel);
    	top.setUIElement(true);
    	colorMixer.append(top, BorderLayout.NORTH);
    	
    	var bottom:Container = SoftBox.createHorizontalBox(4, SoftBoxLayout.CENTER);
    	var p:Container = new JPanel(new VerticalLayout(VerticalLayout.RIGHT, 4));
    	p.append(createLabelToComponet(getALabel(), AAdjuster));
    	var cube:Component = new JPanel();
    	cube.setPreferredSize(p.getComponent(0).getPreferredSize());
    	p.append(cube);
    	p.append(createLabelToComponet(getHexLabel(), hexText));
    	bottom.append(p);
    	
    	p = new JPanel(new VerticalLayout(VerticalLayout.RIGHT, 4));
    	p.append(createLabelToComponet(getRLabel(), RAdjuster));
    	p.append(createLabelToComponet(getGLabel(), GAdjuster));
    	p.append(createLabelToComponet(getBLabel(), BAdjuster));
    	bottom.append(p);
    	
    	p = new JPanel(new VerticalLayout(VerticalLayout.RIGHT, 4));
    	p.append(createLabelToComponet(getHLabel(), HAdjuster));
    	p.append(createLabelToComponet(getSLabel(), SAdjuster));
    	p.append(createLabelToComponet(getLLabel(), LAdjuster));
    	bottom.append(p);
    	
    	bottom.setUIElement(true);
    	colorMixer.append(bottom, BorderLayout.SOUTH);
    }
    
    private function createLabelToComponet(label:String, component:Component):Container{
    	var p:JPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	p.append(new JLabel(label));
    	p.append(component);

    	component.addEventListener(AWEvent.HIDDEN, function():void{
    		p.setVisible(false);
    	});
    	component.addEventListener(AWEvent.SHOWN, function():void{
    		p.setVisible(true);
    	});
    	
    	return p;
    }
    //----------------------------------------------------------------------
    
    
    private function getMixerPaneSize():IntDimension{
		var crm:Number = getColorRectMargin()*2;
		var hss:IntDimension = getHSSize();
		var ls:IntDimension = getLSize();
		hss.change(crm, crm);
		ls.change(crm, crm);
		var size:IntDimension = new IntDimension(hss.width + getHS2LGap() + ls.width, 
							Math.max(hss.height, ls.height));
		size.change(getMCsMarginSize(), getMCsMarginSize());
		return size;
	}
	
	private function getMCsMarginSize():int{
		return 4;
	}
	
	private function getColorRectMargin():int{
		return 1;
	}
    
    private function getHSSize():IntDimension{
    	return new IntDimension(120, 100);
    }
    
    private function getHS2LGap():int{
    	return 8;
    }
    
    private function getLSize():IntDimension{
    	return new IntDimension(40, 100);
    }
	
	private function getLStripWidth():int{
		return 20; //half of getLSize().width
	}
	
    private function getSelectedColor():ASColor{
    	var c:ASColor = colorMixer.getSelectedColor();
    	if(c == null) return ASColor.BLACK;
    	return c;
    }
    
    private function setSelectedColor(c:ASColor):void{
    	color_at_views = c;
    	colorMixer.setSelectedColor(c);
    }
    
    private function updateMixerAllItems():void{
    	updateMixerAllItemsWithColor(getSelectedColor());
	}	
	
	private function getHFromPos(p:IntPoint):Number{
		return (p.x - getHSColorsStartX()) / getHSSize().width;
	}
	private function getSFromPos(p:IntPoint):Number{
		return 1 - ((p.y - getHSColorsStartY()) / getHSSize().height);
	}
	private function getLFromPos(p:IntPoint):Number{
		return 1 - ((p.y - getLColorsStartY()) / getLSize().height);
	}
	
	private function getHAdjusterValueFromH(h:Number):Number{
		return h * (HAdjuster.getMaximum()- HAdjuster.getMinimum());
	}
	private function getSAdjusterValueFromS(s:Number):Number{
		return s * (SAdjuster.getMaximum()- SAdjuster.getMinimum());
	}
	private function getLAdjusterValueFromL(l:Number):Number{
		return l * (LAdjuster.getMaximum()- LAdjuster.getMinimum());
	}
	
	private function getHFromHAdjuster():Number{
		return HAdjuster.getValue() / (HAdjuster.getMaximum()- HAdjuster.getMinimum());
	}
	private var HAdjusterUpdating:Boolean;
	private function updateHAdjusterWithH(h:Number):void{
		HAdjusterUpdating = true;
		HAdjuster.setValue(getHAdjusterValueFromH(h));
		HAdjusterUpdating = false;
	}
	private function getSFromSAdjuster():Number{
		return SAdjuster.getValue() / (SAdjuster.getMaximum()- SAdjuster.getMinimum());
	}
	private var SAdjusterUpdating:Boolean;
	private function updateSAdjusterWithS(s:Number):void{
		SAdjusterUpdating = true;
		SAdjuster.setValue(getSAdjusterValueFromS(s));
		SAdjusterUpdating = false;
	}
	private function getLFromLAdjuster():Number{
		return LAdjuster.getValue() / (LAdjuster.getMaximum()- LAdjuster.getMinimum());
	}
	private var LAdjusterUpdating:Boolean;
	private function updateLAdjusterWithL(l:Number):void{
		LAdjusterUpdating = true;
		LAdjuster.setValue(getLAdjusterValueFromL(l));
		LAdjusterUpdating = false;
	}
	
	private var RAdjusterUpdating:Boolean;
	private function updateRAdjusterWithL(v:Number):void{
		RAdjusterUpdating = true;
		RAdjuster.setValue(v);
		RAdjusterUpdating = false;
	}
	private var GAdjusterUpdating:Boolean;
	private function updateGAdjusterWithG(v:Number):void{
		GAdjusterUpdating = true;
		GAdjuster.setValue(v);
		GAdjusterUpdating = false;
	}
	private var BAdjusterUpdating:Boolean;
	private function updateBAdjusterWithB(v:Number):void{
		BAdjusterUpdating = true;
		BAdjuster.setValue(v);
		BAdjusterUpdating = false;
	}
	
	private function getAFromAAdjuster():Number{
		return AAdjuster.getValue()/100;
	}
	private var AAdjusterUpdating:Boolean;
	private function updateAAdjusterWithA(v:Number):void{
		AAdjusterUpdating = true;
		AAdjuster.setValue(v*100);
		AAdjusterUpdating = false;
	}
	
	private function getColorFromRGBAAdjusters():ASColor{
		var rr:Number = RAdjuster.getValue();
		var gg:Number = GAdjuster.getValue();
		var bb:Number = BAdjuster.getValue();
		return ASColor.getASColor(rr, gg, bb, getAFromAAdjuster());
	}
	private function getColorFromHLSAAdjusters():ASColor{
		var hh:Number = getHFromHAdjuster();
		var ll:Number = getLFromLAdjuster();
		var ss:Number = getSFromSAdjuster();
		return HLSA2ASColor(hh, ll, ss, getAFromAAdjuster());
	}
	//-----------------------------------------------------------------------
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		updateSectionVisibles();
		updateMixerAllItems();
	}
	private function updateSectionVisibles():void{
		hexText.setVisible(colorMixer.isHexSectionVisible());
		AAdjuster.setVisible(colorMixer.isAlphaSectionVisible());
	}
	//------------------------------------------------------------------------
	private function __AAdjusterValueChanged(e:Event):void{
		if(!AAdjusterUpdating){
			updatePreviewColorWithHSL();
		}
	}
	private function __AAdjusterValueAction(e:Event):void{
		colorMixer.setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __RGBAdjusterValueChanged(e:Event):void{
		if(RAdjusterUpdating || GAdjusterUpdating || BAdjusterUpdating){
			return;
		}
		var color:ASColor = getColorFromRGBAAdjusters();
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		var ll:Number = hls.l;
		H_at_HSMC = hh;
		S_at_HSMC = ss;
		L_at_LMC  = ll;
		color_at_views = color;
		updateHSLPosFromHLS(H_at_HSMC, L_at_LMC, S_at_HSMC);
		updateHexTextWithColor(color);
		updateHLSAdjustersWithHLS(hh, ll, ss);
		updatePreviewWithColor(color);
	}
	private function __RGBAdjusterValueAction(e:Event):void{
		colorMixer.setSelectedColor(getColorFromRGBAAdjusters());
	}
	private function __HLSAdjusterValueChanged(e:Event):void{
		if(HAdjusterUpdating || LAdjusterUpdating || SAdjusterUpdating){
			return;
		}
		H_at_HSMC = getHFromHAdjuster();
		L_at_LMC  = getLFromLAdjuster();
		S_at_HSMC = getSFromSAdjuster();
		var color:ASColor = HLSA2ASColor(H_at_HSMC, L_at_LMC, S_at_HSMC, getAFromAAdjuster());
		color_at_views = color;
		updateHSLPosFromHLS(H_at_HSMC, L_at_LMC, S_at_HSMC);
		updateHexTextWithColor(color);
		updateRGBAdjustersWithColor(color);
		updatePreviewWithColor(color);
	}
	private function __HLSAdjusterValueAction(e:Event):void{
		colorMixer.setSelectedColor(getColorFromHLSAAdjusters());
	}
	private function __hexTextChanged(e:Event):void{
		if(hexTextUpdating){
			return;
		}
		var color:ASColor = getColorFromHexTextAndAdjuster();
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		var ll:Number = hls.l;
		H_at_HSMC = hh;
		S_at_HSMC = ss;
		L_at_LMC  = ll;
		color_at_views = color;
		updateHSLPosFromHLS(H_at_HSMC, L_at_LMC, S_at_HSMC);
		updateHLSAdjustersWithHLS(hh, ll, ss);
		updateRGBAdjustersWithColor(color);
		updatePreviewWithColor(color);
	}
	private function __hexTextAction(e:Event):void{
		colorMixer.setSelectedColor(getColorFromHexTextAndAdjuster());
	}
	
	private var H_at_HSMC:Number;
	private var S_at_HSMC:Number;
	private var L_at_LMC:Number;
	private var color_at_views:ASColor;
	
	private function __colorSelectionChanged(e:Event):void{
		var color:ASColor = getSelectedColor();
		previewColorIcon.setColor(color);
		previewColorLabel.repaint();
		if(!color.equals(color_at_views)){
			updateMixerAllItemsWithColor(color);
			color_at_views = color;
		}
	}	
		
	private function createHSAndL():void{
		HSMC = new AWSprite();
		HSPosMC = new AWSprite();
		LMC = new AWSprite();
		LPosMC = new AWSprite();
		
		mixerPanel.addChild(HSMC);
		mixerPanel.addChild(HSPosMC);
		mixerPanel.addChild(LMC);
		mixerPanel.addChild(LPosMC);

		paintHSMC();
		paintHSPosMC();
		paintLMC();
		paintLPosMC();
		
		HSMC.addEventListener(MouseEvent.MOUSE_DOWN, __HSMCPress);
		HSMC.addEventListener(ReleaseEvent.RELEASE, __HSMCRelease);
		HSMC.addEventListener(ReleaseEvent.RELEASE_OUT_SIDE, __HSMCRelease);
		LMC.addEventListener(MouseEvent.MOUSE_DOWN, __LMCPress);
		LMC.addEventListener(ReleaseEvent.RELEASE, __LMCRelease);
		LMC.addEventListener(ReleaseEvent.RELEASE_OUT_SIDE, __LMCRelease);
	}
	private function __HSMCPress(e:Event):void{
		HSMC.addEventListener(MouseEvent.MOUSE_MOVE, __HSMCDragging);
		__HSMCDragging(null);
	}
	private function __HSMCRelease(e:Event):void{
		HSMC.removeEventListener(MouseEvent.MOUSE_MOVE, __HSMCDragging);
		countHSWithMousePosOnHSMCAndUpdateViews();
		setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __HSMCDragging(e:Event):void{
		countHSWithMousePosOnHSMCAndUpdateViews();
	}
	
	private function __LMCPress(e:Event):void{
		LMC.addEventListener(MouseEvent.MOUSE_MOVE, __LMCDragging);
		__LMCDragging(null);
	}
	private function __LMCRelease(e:Event):void{
		LMC.removeEventListener(MouseEvent.MOUSE_MOVE, __LMCDragging);		
		countLWithMousePosOnLMCAndUpdateViews();
		setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __LMCDragging(e:Event):void{
		countLWithMousePosOnLMCAndUpdateViews();
	}
	
	private function countHSWithMousePosOnHSMCAndUpdateViews():void{
		var p:IntPoint = mixerPanel.getMousePosition();
		var hs:Object = getHSWithPosOnHSMC(p);
		HSPosMC.x = p.x;
		HSPosMC.y = p.y;
		var h:Number = hs.h;
		var s:Number = hs.s;
		H_at_HSMC = h;
		S_at_HSMC = s;
		updateOthersWhenHSMCAdjusting();
	}
	private function getHSWithPosOnHSMC(p:IntPoint):Object{
		var hsSize:IntDimension = getHSSize();
		var minX:Number = getHSColorsStartX();
		var maxX:Number = minX + hsSize.width;
		var minY:Number = getHSColorsStartY();
		var maxY:Number = minY + hsSize.height;
		p.x = Math.max(minX, Math.min(maxX, p.x));
		p.y = Math.max(minY, Math.min(maxY, p.y));
		var h:Number = getHFromPos(p);
		var s:Number = getSFromPos(p);
		return {h:h, s:s};
	}
	
	private function countLWithMousePosOnLMCAndUpdateViews():void{
		var p:IntPoint = mixerPanel.getMousePosition();
		var ll:Number = getLWithPosOnLMC(p);
		LPosMC.y = p.y;
		L_at_LMC = ll;
		updateOthersWhenLMCAdjusting();
	}
	private function getLWithPosOnLMC(p:IntPoint):Number{
		var lSize:IntDimension = getLSize();
		var minY:Number = getLColorsStartY();
		var maxY:Number = minY + lSize.height;
		p.y = Math.max(minY, Math.min(maxY, p.y));
		return getLFromPos(p);
	}
	
	private function getColorFromMCViewsAndAAdjuster():ASColor{
		return HLSA2ASColor(H_at_HSMC, L_at_LMC, S_at_HSMC, getAFromAAdjuster());
	}
	
	private function updatePreviewColorWithHSL():void{
		updatePreviewWithColor(getColorFromMCViewsAndAAdjuster());
	}
	
	private function updatePreviewWithColor(color:ASColor):void{
		previewColorIcon.setCurrentColor(color);
		previewColorLabel.repaint();
    	colorMixer.getModel().fireColorAdjusting(color);
	}
	
	private function updateOthersWhenHSMCAdjusting():void{
		paintLMCWithHS(H_at_HSMC, S_at_HSMC);
		var color:ASColor = getColorFromMCViewsAndAAdjuster();
		updateHexTextWithColor(color);
		updateHLSAdjustersWithHLS(H_at_HSMC, L_at_LMC, S_at_HSMC);
		updateRGBAdjustersWithColor(color);
		updatePreviewWithColor(color);
	}
	
	private function updateOthersWhenLMCAdjusting():void{
		var color:ASColor = getColorFromMCViewsAndAAdjuster();
		updateHexTextWithColor(color);
		LAdjuster.setValue(getLAdjusterValueFromL(L_at_LMC));
		updateRGBAdjustersWithColor(color);
		updatePreviewWithColor(color);
	}
	    
	//*******************************************************************************
	//       Override these methods to easiy implement different look
	//******************************************************************************
	
	private function getALabel():String{
		return "Alpha:";
	}
	private function getRLabel():String{
		return "R:";
	}
	private function getGLabel():String{
		return "G:";
	}
	private function getBLabel():String{
		return "B:";
	}
	private function getHLabel():String{
		return "H:";
	}
	private function getSLabel():String{
		return "S:";
	}
	private function getLLabel():String{
		return "L:";
	}
	private function getHexLabel():String{
		return "#";
	}
	
	private function updateMixerAllItemsWithColor(color:ASColor):void{
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		var ll:Number = hls.l;
		H_at_HSMC = hh;
		S_at_HSMC = ss;
		L_at_LMC  = ll;
		
		updateHSLPosFromHLS(hh, ll, ss);
		
		previewColorIcon.setColor(color);
		previewColorLabel.repaint();
		
		updateRGBAdjustersWithColor(color);
		updateHLSAdjustersWithHLS(hh, ll, ss);
		updateAlphaAdjusterWithColor(color);
		updateHexTextWithColor(color);
	}
	
	private function updateHSLPosFromHLS(hh:Number, ll:Number, ss:Number):void{
		var hsSize:IntDimension = getHSSize();
		HSPosMC.x = hh*hsSize.width + getHSColorsStartX();
		HSPosMC.y = (1-ss)*hsSize.height + getHSColorsStartY();
		paintLMCWithHS(hh, ss);
		LPosMC.y = getLColorsStartY() + (1-ll)*getLSize().height;
	}
		
	private function updateRGBAdjustersWithColor(color:ASColor):void{

		updateRAdjusterWithL(color.getRed());
		updateGAdjusterWithG(color.getGreen());
		updateBAdjusterWithB(color.getBlue());
	}
	
	private function updateHLSAdjustersWithHLS(h:Number, l:Number, s:Number):void{
		updateHAdjusterWithH(h);
		updateLAdjusterWithL(l);
		updateSAdjusterWithS(s);
	}
	
	private function updateAlphaAdjusterWithColor(color:ASColor):void{
		updateAAdjusterWithA(color.getAlpha());
	}

    private var hexTextColor:ASColor;
    private var hexTextUpdating:Boolean;
    private function updateHexTextWithColor(color:ASColor):void{
    	if(!color.equals(hexTextColor)){
	    	hexTextColor = color;
	    	var hex:String;
	    	if(color == null){
	    		hex = "000000";
	    	}else{
	    		hex = color.getRGB().toString(16);
	    	}
	    	for(var i:Number=6-hex.length; i>0; i--){
	    		hex = "0" + hex;
	    	}
	    	hex = hex.toUpperCase();
	    	hexTextUpdating = true;
	    	hexText.setText(hex);
	    	hexTextUpdating = false;
    	}
    }
	
    private function getColorFromHexTextAndAdjuster():ASColor{
    	var text:String = hexText.getText();
    	var rgb:Number = parseInt("0x" + text);
    	return new ASColor(rgb, getAFromAAdjuster());
    }
	
	private function getHSColorsStartX():Number{
		return getMCsMarginSize() + getColorRectMargin();
	}
	private function getHSColorsStartY():Number{
		return getMCsMarginSize() + getColorRectMargin();
	}
	private function getLColorsStartY():Number{
		return getMCsMarginSize() + getColorRectMargin();
	}
	//private function getLColorsStartX():Number{
	//	return getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 + getHS2LGap();
	//}	
	
	private function paintHSMC():void{
		HSMC.graphics.clear();
		var g:Graphics2D = new Graphics2D(HSMC.graphics);
		var offset:Number = getMCsMarginSize();
		var thickness:Number = getColorRectMargin();
		var hsSize:IntDimension = getHSSize();
		var H:Number = hsSize.width;
		var S:Number = hsSize.height;
		
		var w:Number = H+thickness*2;
		var h:Number = S+thickness*2;
		g.drawLine(new Pen(ASColor.GRAY, thickness), 
					offset+thickness/2, offset+thickness/2, 
					offset+w-thickness, 
					offset+thickness/2);
		g.drawLine(new Pen(ASColor.GRAY, 1), 
					offset+thickness/2, offset+thickness/2, 
					offset+thickness/2, 
					offset+h-thickness);
		
		offset += thickness;

		var colors:Array = [0, 0x808080];
		var alphas:Array = [1, 1];
		var ratios:Array = [0, 255];
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(1, S, (90/180)*Math.PI, offset, 0); 
		var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
		for(var x:Number=0; x<H; x++){
			var pc:ASColor = HLSA2ASColor(x/H, 0.5, 1, 100);
			colors[0] = pc.getRGB();
			matrix.tx = x+offset;
			g.fillRectangle(brush, x+offset, offset, 1, S);
		}
	}
	
	private function paintHSPosMC():void{
		HSPosMC.graphics.clear();
		var g:Graphics2D = new Graphics2D(HSPosMC.graphics);
		g.drawLine(new Pen(ASColor.BLACK, 2), -6, 0, -3, 0);
		g.drawLine(new Pen(ASColor.BLACK, 2), 6, 0, 3, 0);
		g.drawLine(new Pen(ASColor.BLACK, 2), 0, -6, 0, -3);
		g.drawLine(new Pen(ASColor.BLACK, 2), 0, 6, 0, 3);
	}
	private function paintLMCWithHS(hh:Number, ss:Number):void{
		LMC.graphics.clear();
		var g:Graphics2D = new Graphics2D(LMC.graphics);
		var x:Number = getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 + getHS2LGap();
		var y:Number = getMCsMarginSize();
		
		var thickness:Number = getColorRectMargin();
		var lSize:IntDimension = getLSize();
		var w:Number = getLStripWidth() + thickness*2;
		var h:Number = lSize.height + thickness*2;
		
		g.drawLine(new Pen(ASColor.GRAY, thickness), 
					x+thickness/2, y+thickness/2, 
					x+w-thickness, 
					y+thickness/2);
		g.drawLine(new Pen(ASColor.GRAY, 1), 
					x+thickness/2, y+thickness/2, 
					x+thickness/2, 
					y+h-thickness);
		x += thickness;
		y += thickness;
		w = getLStripWidth();
		h = lSize.height;

		var colors:Array = [0xFFFFFF, HLSA2ASColor(hh, 0.5, ss, 100).getRGB(), 0];
		var alphas:Array = [1, 1, 1];
		var ratios:Array = [0, 127.5, 255];
		var matrix:Matrix = new Matrix();
		matrix.createGradientBox(w, h, (90/180)*Math.PI, x, y); 
		var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR, colors, alphas, ratios, matrix);
		g.fillRectangle(brush, x, y, w, h);		
	}
	private function paintLMCWithColor(color:ASColor):void{
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		paintLMCWithHS(hh, ss);
	}
	private function paintLMC():void{
		paintLMCWithColor(getSelectedColor());
	}
	private function paintLPosMC():void{
		LPosMC.graphics.clear();
		var g:Graphics2D = new Graphics2D(LPosMC.graphics);
		g.fillPolygon(new SolidBrush(ASColor.BLACK), [
		new IntPoint(0, 0), new IntPoint(4, -4), new IntPoint(4, 4)]);
		LPosMC.x = getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 
					+ getHS2LGap() + getLStripWidth() + 1;
	}
	
	private function createMixerPanel():JPanel{
		var p:JPanel = new JPanel();
		p.setBorder(null); //esure there is no border
		p.setPreferredSize(getMixerPaneSize());
		return p;
	}
	
	private function createPreviewColorIcon():PreviewColorIcon{
		return new PreviewColorIcon(46, 100, PreviewColorIcon.VERTICAL);
	}
	
	private function createPreviewColorLabel():JLabel{
		var label:JLabel = new JLabel();
		var margin:Number = getMCsMarginSize();
		var bb:BevelBorder = new BevelBorder(null, BevelBorder.LOWERED);
		bb.setThickness(1);
		label.setBorder(new EmptyBorder(bb, new Insets(margin, 0, margin, 0))); 
		return label;
	}
	
	private function createHexTextField():JTextField{
		return new JTextField("000000", 6);
	}
	
	private function createAdjusters():void{
		AAdjuster = createAAdjuster();
		RAdjuster = createRAdjuster();
		GAdjuster = createGAdjuster();
		BAdjuster = createBAdjuster();
		HAdjuster = createHAdjuster();
		SAdjuster = createSAdjuster();
		LAdjuster = createLAdjuster();
	}
	
	private function createAAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);		
		adjuster.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "%";
			});
		adjuster.setValues(100, 0, 0, 100);
		return adjuster;
	}
	private function createRAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(255, 0, 0, 255);
		return adjuster;
	}
	private function createGAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(255, 0, 0, 255);
		return adjuster;
	}
	private function createBAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(255, 0, 0, 255);
		return adjuster;
	}
	private function createHAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(0, 0, 0, 360);
		adjuster.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "°";
			});
		return adjuster;
	}
	private function createSAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(0, 0, 0, 100);
		adjuster.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "%";
			});
		return adjuster;
	}
	private function createLAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValues(0, 0, 0, 100);
		adjuster.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "%";
			});
		return adjuster;
	}
	
	//----------------Tool functions--------------------
	
	private static function getHLS(color:ASColor):Object{
		var rr:Number = color.getRed()/255;
		var gg:Number = color.getGreen()/255;
		var bb:Number = color.getBlue()/255;
		var hls:Object = rgb2Hls(rr, gg, bb);
		return hls;
	}
	
	/**
	 * H, L, S -> [0, 1], alpha -> [0, 100]
	 */
	private static function HLSA2ASColor(H:Number, L:Number, S:Number, alpha:Number):ASColor{
		var p1:Number, p2:Number, r:Number, g:Number, b:Number;
		p1 = p2 = 0;
		H = H*360;
		if(L<0.5){
			p2=L*(1+S);
		}else{
			p2=L + S - L*S;
		}
		p1=2*L-p2;
		if(S==0){
			r=L;
			g=L;
			b=L;
		}else{
			r = hlsValue(p1, p2, H+120);
			g = hlsValue(p1, p2, H);
			b = hlsValue(p1, p2, H-120);
		}
		r *= 255;
		g *= 255;
		b *= 255;
		return ASColor.getASColor(r, g, b, alpha);
	}
	
	private static function hlsValue(p1:Number, p2:Number, h:Number):Number{
	   if (h > 360) h = h - 360;
	   if (h < 0)   h = h + 360;
	   if (h < 60 ) return p1 + (p2-p1)*h/60;
	   if (h < 180) return p2;
	   if (h < 240) return p1 + (p2-p1)*(240-h)/60;
	   return p1;
	}
	
	private static function rgb2Hls(rr:Number, gg:Number, bb:Number):Object{
	   // Static method to compute HLS from RGB. The r,g,b triplet is between
	   // [0,1], h, l, s are [0,1].
	
		var rnorm:Number, gnorm:Number, bnorm:Number;
		var minval:Number, maxval:Number, msum:Number, mdiff:Number;
		var r:Number, g:Number, b:Number;
	   	var hue:Number, light:Number, satur:Number;
	   	
		r = g = b = 0;
		if (rr > 0) r = rr; if (r > 1) r = 1;
		if (gg > 0) g = gg; if (g > 1) g = 1;
		if (bb > 0) b = bb; if (b > 1) b = 1;
		
		minval = r;
		if (g < minval) minval = g;
		if (b < minval) minval = b;
		maxval = r;
		if (g > maxval) maxval = g;
		if (b > maxval) maxval = b;
		
		rnorm = gnorm = bnorm = 0;
		mdiff = maxval - minval;
		msum  = maxval + minval;
		light = 0.5 * msum;
		if (maxval != minval) {
			rnorm = (maxval - r)/mdiff;
			gnorm = (maxval - g)/mdiff;
			bnorm = (maxval - b)/mdiff;
		} else {
			satur = hue = 0;
			return {h:hue, l:light, s:satur};
		}
		
		if (light < 0.5)
		  satur = mdiff/msum;
		else
		  satur = mdiff/(2.0 - msum);
		
		if (r == maxval)
		  hue = 60.0 * (6.0 + bnorm - gnorm);
		else if (g == maxval)
		  hue = 60.0 * (2.0 + rnorm - bnorm);
		else
		  hue = 60.0 * (4.0 + gnorm - rnorm);
		
		if (hue > 360)
			hue = hue - 360;
		hue/=360;
		return {h:hue, l:light, s:satur};
	}	
}
}