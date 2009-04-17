/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.border.BevelBorder;
import org.aswing.border.EmptyBorder;
import org.aswing.BorderLayout;
import org.aswing.colorchooser.JColorMixer;
import org.aswing.colorchooser.PreviewColorIcon;
import org.aswing.colorchooser.VerticalLayout;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.FlowLayout;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.GradientBrush;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.Insets;
import org.aswing.overflow.JAdjuster;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JTextField;
import org.aswing.LookAndFeel;
import org.aswing.plaf.ColorMixerUI;
import org.aswing.SoftBox;
import org.aswing.SoftBoxLayout;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicColorMixerUI extends ColorMixerUI {
	
	private var colorMixer:JColorMixer;
	private var mixerPanel:JPanel;
	private var HSMC:MovieClip;
	private var HSPosMC:MovieClip;
	private var LMC:MovieClip;
	private var LPosMC:MovieClip;
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
	
	private var colorMixerListener:Object;
		
	public function BasicColorMixerUI(){
	}

    public function installUI(c:Component):Void{
		colorMixer = JColorMixer(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		colorMixer = JColorMixer(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		var pp:String = "ColorMixer.";
        LookAndFeel.installColorsAndFont(colorMixer, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBasicProperties(colorMixer, pp);
        LookAndFeel.installBorder(colorMixer, pp + "border");
	}
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(colorMixer);
    }	
    
	private function installComponents():Void{
		mixerPanel = createMixerPanel();
		previewColorLabel = createPreviewColorLabel();
		previewColorIcon = createPreviewColorIcon();
		previewColorLabel.setIcon(previewColorIcon);
		hexText = createHexTextField();
		createAdjusters();
		layoutComponents();
		updateSectionVisibles();
    }
	private function uninstallComponents():Void{
		mixerPanel.removeAll();
    }
        
	private function installListeners():Void{
		colorMixerListener = new Object();
		colorMixerListener[JColorMixer.ON_STATE_CHANGED] = Delegate.create(this, __colorSelectionChanged);
		colorMixer.addEventListener(colorMixerListener);
		
		AAdjuster.addChangeListener(__AAdjusterValueChanged, this);
		AAdjuster.addActionListener(__AAdjusterValueAction, this);
		
		var rgbListener:Object = new Object();
		rgbListener[JAdjuster.ON_STATE_CHANGED] = Delegate.create(this, __RGBAdjusterValueChanged);
		rgbListener[JAdjuster.ON_ACT] = Delegate.create(this, __RGBAdjusterValueAction);
		RAdjuster.addEventListener(rgbListener);
		GAdjuster.addEventListener(rgbListener);
		BAdjuster.addEventListener(rgbListener);
		
		var hlsListener:Object = new Object();
		hlsListener[JAdjuster.ON_STATE_CHANGED] = Delegate.create(this, __HLSAdjusterValueChanged);
		hlsListener[JAdjuster.ON_ACT] = Delegate.create(this, __HLSAdjusterValueAction);
		HAdjuster.addEventListener(hlsListener);
		LAdjuster.addEventListener(hlsListener);
		SAdjuster.addEventListener(hlsListener);
		
		hexText.addChangeListener(__hexTextChanged, this);
		hexText.addActionListener(__hexTextAction, this);
		hexText.addEventListener(JTextField.ON_FOCUS_LOST, __hexTextAction, this);
	}
    private function uninstallListeners():Void{
    	colorMixer.removeEventListener(colorMixerListener);
    }
    
    /**
     * Override this method to change different layout
     */
    private function layoutComponents():Void{
    	colorMixer.setLayout(new BorderLayout(0, 4));
    	
    	var top:Container = SoftBox.createHorizontalBox(4, SoftBoxLayout.CENTER);
    	top.append(mixerPanel);
    	top.append(previewColorLabel);
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
    	
    	colorMixer.append(bottom, BorderLayout.SOUTH);
    }
    
    private function createLabelToComponet(label:String, component:Component):Container{
    	var p:JPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    	p.append(new JLabel(label));
    	p.append(component);
    	component.addEventListener(Component.ON_HIDDEN, function(){
    		p.setVisible(false);
    	});
    	component.addEventListener(Component.ON_SHOWN, function(){
    		p.setVisible(true);
    	});
    	
    	return p;
    }
    //----------------------------------------------------------------------
    
    
    private function getMixerPaneSize():Dimension{
		var crm:Number = getColorRectMargin()*2;
		var hss:Dimension = getHSSize();
		var ls:Dimension = getLSize();
		hss.change(crm, crm);
		ls.change(crm, crm);
		var size:Dimension = new Dimension(hss.width + getHS2LGap() + ls.width, 
							Math.max(hss.height, ls.height));
		size.change(getMCsMarginSize(), getMCsMarginSize());
		return size;
	}
	
	private function getMCsMarginSize():Number{
		return 4;
	}
	
	private function getColorRectMargin():Number{
		return 1;
	}
    
    private function getHSSize():Dimension{
    	return new Dimension(120, 100);
    }
    
    private function getHS2LGap():Number{
    	return 8;
    }
    
    private function getLSize():Dimension{
    	return new Dimension(40, 100);
    }
	
	private function getLStripWidth():Number{
		return 20; //half of getLSize().width
	}
	
    private function getSelectedColor():ASColor{
    	var c:ASColor = colorMixer.getSelectedColor();
    	if(c == null) return ASColor.BLACK;
    	return c;
    }
    
    private function setSelectedColor(c:ASColor):Void{
    	color_at_views = c;
    	colorMixer.setSelectedColor(c);
    }
    
    private function updateMixerAllItems():Void{
    	updateMixerAllItemsWithColor(getSelectedColor());
	}	
	
	private function getHFromPos(p:Point):Number{
		return (p.x - getHSColorsStartX()) / getHSSize().width;
	}
	private function getSFromPos(p:Point):Number{
		return 1 - ((p.y - getHSColorsStartY()) / getHSSize().height);
	}
	private function getLFromPos(p:Point):Number{
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
	private function updateHAdjusterWithH(h:Number):Void{
		HAdjusterUpdating = true;
		HAdjuster.setValue(getHAdjusterValueFromH(h));
		HAdjusterUpdating = false;
	}
	private function getSFromSAdjuster():Number{
		return SAdjuster.getValue() / (SAdjuster.getMaximum()- SAdjuster.getMinimum());
	}
	private var SAdjusterUpdating:Boolean;
	private function updateSAdjusterWithS(s:Number):Void{
		SAdjusterUpdating = true;
		SAdjuster.setValue(getSAdjusterValueFromS(s));
		SAdjusterUpdating = false;
	}
	private function getLFromLAdjuster():Number{
		return LAdjuster.getValue() / (LAdjuster.getMaximum()- LAdjuster.getMinimum());
	}
	private var LAdjusterUpdating:Boolean;
	private function updateLAdjusterWithL(l:Number):Void{
		LAdjusterUpdating = true;
		LAdjuster.setValue(getLAdjusterValueFromL(l));
		LAdjusterUpdating = false;
	}
	
	private function getRFromRAdjuster():Number{
		return RAdjuster.getValue();
	}
	private var RAdjusterUpdating:Boolean;
	private function updateRAdjusterWithL(v:Number):Void{
		RAdjusterUpdating = true;
		RAdjuster.setValue(v);
		RAdjusterUpdating = false;
	}
	private function getGFromGAdjuster():Number{
		return GAdjuster.getValue();
	}
	private var GAdjusterUpdating:Boolean;
	private function updateGAdjusterWithG(v:Number):Void{
		GAdjusterUpdating = true;
		GAdjuster.setValue(v);
		GAdjusterUpdating = false;
	}
	private function getBFromBAdjuster():Number{
		return BAdjuster.getValue();
	}
	private var BAdjusterUpdating:Boolean;
	private function updateBAdjusterWithB(v:Number):Void{
		BAdjusterUpdating = true;
		BAdjuster.setValue(v);
		BAdjusterUpdating = false;
	}
	
	private function getAFromAAdjuster():Number{
		return AAdjuster.getValue();
	}
	private var AAdjusterUpdating:Boolean;
	private function updateAAdjusterWithA(v:Number):Void{
		AAdjusterUpdating = true;
		AAdjuster.setValue(v);
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
	private function create(c:Component):Void{
		super.create(c);
		updateSectionVisibles();
	}	
	private function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		updateSectionVisibles();
		updateMixerAllItems();
	}
	private function updateSectionVisibles():Void{
		hexText.setVisible(colorMixer.isHexSectionVisible());
		AAdjuster.setVisible(colorMixer.isAlphaSectionVisible());
	}
	//------------------------------------------------------------------------
	private function __AAdjusterValueChanged():Void{
		if(!AAdjusterUpdating){
			updatePreviewColorWithHSL();
		}
	}
	private function __AAdjusterValueAction():Void{
		colorMixer.setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __RGBAdjusterValueChanged():Void{
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
	private function __RGBAdjusterValueAction():Void{
		colorMixer.setSelectedColor(getColorFromRGBAAdjusters());
	}
	private function __HLSAdjusterValueChanged():Void{
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
	private function __HLSAdjusterValueAction():Void{
		colorMixer.setSelectedColor(getColorFromHLSAAdjusters());
	}
	private function __hexTextChanged():Void{
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
	private function __hexTextAction():Void{
		colorMixer.setSelectedColor(getColorFromHexTextAndAdjuster());
	}
	
	private var H_at_HSMC:Number;
	private var S_at_HSMC:Number;
	private var L_at_LMC:Number;
	private var color_at_views:ASColor;
	
	private function __colorSelectionChanged():Void{
		var color:ASColor = getSelectedColor();
		previewColorIcon.setColor(color);
		previewColorLabel.repaint();
		if(!color.equals(color_at_views)){
			updateMixerAllItemsWithColor(color);
			color_at_views = color;
		}
	}	
		
	private function __mixerPanelCreated():Void{
		HSMC = mixerPanel.createMovieClip();
		HSPosMC = mixerPanel.createMovieClip();
		LMC = mixerPanel.createMovieClip();
		LPosMC = mixerPanel.createMovieClip();
		paintHSMC();
		paintHSPosMC();
		paintLMC();
		paintLPosMC();
		HSMC.onPress = Delegate.create(this, __HSMCPress);
		HSMC.onRelease = Delegate.create(this, __HSMCRelease);
		HSMC.onReleaseOutside = HSMC.onRelease;
		LMC.onPress = Delegate.create(this, __LMCPress);
		LMC.onRelease = Delegate.create(this, __LMCRelease);
		LMC.onReleaseOutside = LMC.onRelease;
	}
	private function __HSMCPress():Void{
		colorMixer.supplyOnPress();
		HSMC.onMouseMove = Delegate.create(this, __HSMCDragging);
		__HSMCDragging();
	}
	private function __HSMCRelease():Void{
		HSMC.onMouseMove = undefined;
		delete HSMC.onMouseMove;
		countHSWithMousePosOnHSMCAndUpdateViews();
		setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __HSMCDragging():Void{
		countHSWithMousePosOnHSMCAndUpdateViews();
		updateAfterEvent();
	}
	
	private function __LMCPress():Void{
		colorMixer.supplyOnPress();
		LMC.onMouseMove = Delegate.create(this, __LMCDragging);
		__LMCDragging();
	}
	private function __LMCRelease():Void{
		LMC.onMouseMove = undefined;
		delete LMC.onMouseMove;
		countLWithMousePosOnLMCAndUpdateViews();
		setSelectedColor(getColorFromMCViewsAndAAdjuster());
	}
	private function __LMCDragging():Void{
		countLWithMousePosOnLMCAndUpdateViews();
		updateAfterEvent();
	}
	
	private function countHSWithMousePosOnHSMCAndUpdateViews():Void{
		var p:Point = mixerPanel.getMousePosition();
		var hs:Object = getHSWithPosOnHSMC(p);
		HSPosMC._x = p.x;
		HSPosMC._y = p.y;
		var h:Number = hs.h;
		var s:Number = hs.s;
		H_at_HSMC = h;
		S_at_HSMC = s;
		updateOthersWhenHSMCAdjusting();
	}
	private function getHSWithPosOnHSMC(p:Point):Object{
		var hsSize:Dimension = getHSSize();
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
	
	private function countLWithMousePosOnLMCAndUpdateViews():Void{
		var p:Point = mixerPanel.getMousePosition();
		var ll:Number = getLWithPosOnLMC(p);
		LPosMC._y = p.y;
		L_at_LMC = ll;
		updateOthersWhenLMCAdjusting();
	}
	private function getLWithPosOnLMC(p:Point):Number{
		var lSize:Dimension = getLSize();
		var minY:Number = getLColorsStartY();
		var maxY:Number = minY + lSize.height;
		p.y = Math.max(minY, Math.min(maxY, p.y));
		return getLFromPos(p);
	}
	
	private function getColorFromMCViewsAndAAdjuster():ASColor{
		return HLSA2ASColor(H_at_HSMC, L_at_LMC, S_at_HSMC, getAFromAAdjuster());
	}
	
	private function updatePreviewColorWithHSL():Void{
		updatePreviewWithColor(getColorFromMCViewsAndAAdjuster());
	}
	
	private function updatePreviewWithColor(color:ASColor):Void{
		previewColorIcon.setCurrentColor(color);
		previewColorLabel.repaint();
    	colorMixer.getModel().fireColorAdjusting(color);
	}
	
	private function updateOthersWhenHSMCAdjusting():Void{
		paintLMCWithHS(H_at_HSMC, S_at_HSMC);
		var color:ASColor = getColorFromMCViewsAndAAdjuster();
		updateHexTextWithColor(color);
		updateHLSAdjustersWithHLS(H_at_HSMC, L_at_LMC, S_at_HSMC);
		updateRGBAdjustersWithColor(color);
		updatePreviewWithColor(color);
	}
	
	private function updateOthersWhenLMCAdjusting():Void{
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
	
	private function updateMixerAllItemsWithColor(color:ASColor):Void{
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
	
	private function updateHSLPosFromHLS(hh:Number, ll:Number, ss:Number):Void{
		var hsSize:Dimension = getHSSize();
		HSPosMC._x = hh*hsSize.width + getHSColorsStartX();
		HSPosMC._y = (1-ss)*hsSize.height + getHSColorsStartY();
		paintLMCWithHS(hh, ss);
		LPosMC._y = getLColorsStartY() + (1-ll)*getLSize().height;
	}
		
	private function updateRGBAdjustersWithColor(color:ASColor):Void{
		var rr:Number = color.getRed();
		var gg:Number = color.getGreen();
		var bb:Number = color.getBlue();
		updateRAdjusterWithL(color.getRed());
		updateGAdjusterWithG(color.getGreen());
		updateBAdjusterWithB(color.getBlue());
	}
	
	private function updateHLSAdjustersWithColor(color:ASColor):Void{
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		var ll:Number = hls.l;		
		updateHLSAdjustersWithHLS(hh, ll, ss);
	}
	private function updateHLSAdjustersWithHLS(h:Number, l:Number, s:Number):Void{
		updateHAdjusterWithH(h);
		updateLAdjusterWithL(l);
		updateSAdjusterWithS(s);
	}
	
	private function updateAlphaAdjusterWithColor(color:ASColor):Void{
		updateAAdjusterWithA(color.getAlpha());
	}

    private var hexTextColor:ASColor;
    private var hexTextUpdating:Boolean;
    private function updateHexTextWithColor(color:ASColor):Void{
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
	private function getLColorsStartX():Number{
		return getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 + getHS2LGap();
	}	
	
	private function paintHSMC():Void{
		HSMC.clear();
		var g:Graphics = new Graphics(HSMC);
		var offset:Number = getMCsMarginSize();
		var thickness:Number = getColorRectMargin();
		var hsSize:Dimension = getHSSize();
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
		var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR);
		var colors:Array = [0, 0x808080];
		var alphas:Array = [100, 100];
		var ratios:Array = [0, 255];
		var matrix:Object = {matrixType:"box", x:offset, y:0, w:1, h:S, r:(90/180)*Math.PI};
		brush.setColors(colors);
		brush.setAlphas(alphas);
		brush.setRatios(ratios);
		brush.setMatrix(matrix);
		for(var x:Number=0; x<H; x++){
			var pc:ASColor = HLSA2ASColor(x/H, 0.5, 1, 100);
			colors[0] = pc.getRGB();
			matrix.x = x+offset;
			g.fillRectangle(brush, x+offset, offset, 1, S);
		}
	}
	
	private function paintHSPosMC():Void{
		HSPosMC.clear();
		var g:Graphics = new Graphics(HSPosMC);
		g.drawLine(new Pen(ASColor.BLACK, 2), -6, 0, -3, 0);
		g.drawLine(new Pen(ASColor.BLACK, 2), 6, 0, 3, 0);
		g.drawLine(new Pen(ASColor.BLACK, 2), 0, -6, 0, -3);
		g.drawLine(new Pen(ASColor.BLACK, 2), 0, 6, 0, 3);
	}
	private function paintLMCWithHS(hh:Number, ss:Number):Void{
		LMC.clear();
		var g:Graphics = new Graphics(LMC);
		var x:Number = getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 + getHS2LGap();
		var y:Number = getMCsMarginSize();
		
		var thickness:Number = getColorRectMargin();
		var lSize:Dimension = getLSize();
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

		var brush:GradientBrush = new GradientBrush(GradientBrush.LINEAR);
		var colors:Array = [0xFFFFFF, HLSA2ASColor(hh, 0.5, ss, 100).getRGB(), 0];
		var alphas:Array = [100, 100, 100];
		var ratios:Array = [0, 127.5, 255];
		var matrix:Object = {matrixType:"box", x:x, y:y, w:w, h:h, r:(90/180)*Math.PI};
		brush.setColors(colors);
		brush.setAlphas(alphas);
		brush.setRatios(ratios);
		brush.setMatrix(matrix);
		g.fillRectangle(brush, x, y, w, h);		
	}
	private function paintLMCWithColor(color:ASColor):Void{
		var hls:Object = getHLS(color);
		var hh:Number = hls.h;
		var ss:Number = hls.s;
		paintLMCWithHS(hh, ss);
	}
	private function paintLMC():Void{
		paintLMCWithColor(getSelectedColor());
	}
	private function paintLPosMC():Void{
		LPosMC.clear();
		var g:Graphics = new Graphics(LPosMC);
		g.fillPolygon(new SolidBrush(ASColor.BLACK), [
		new Point(0, 0), new Point(4, -4), new Point(4, 4)]);
		LPosMC._x = getHSSize().width + getMCsMarginSize() + getColorRectMargin()*2 
					+ getHS2LGap() + getLStripWidth() + 1;
	}
	
	private function createMixerPanel():JPanel{
		var p:JPanel = new JPanel();
		p.setBorder(null); //esure there is no border
		p.addEventListener(JPanel.ON_CREATED, __mixerPanelCreated, this);
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
	
	private function createAdjusters():Void{
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