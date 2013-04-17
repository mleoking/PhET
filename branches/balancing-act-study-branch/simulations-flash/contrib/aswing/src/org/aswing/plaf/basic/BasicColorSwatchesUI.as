/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.AbstractButton;
import org.aswing.ASColor;
import org.aswing.border.BevelBorder;
import org.aswing.BorderLayout;
import org.aswing.colorchooser.ColorRectIcon;
import org.aswing.colorchooser.JColorSwatches;
import org.aswing.colorchooser.NoColorIcon;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.graphics.Graphics;
import org.aswing.graphics.Pen;
import org.aswing.graphics.SolidBrush;
import org.aswing.overflow.JAdjuster;
import org.aswing.JButton;
import org.aswing.JLabel;
import org.aswing.JPanel;
import org.aswing.JTextField;
import org.aswing.LookAndFeel;
import org.aswing.MouseManager;
import org.aswing.plaf.ColorSwatchesUI;
import org.aswing.SoftBox;
import org.aswing.SoftBoxLayout;
import org.aswing.util.Delegate;

/**
 * @author iiley
 */
class org.aswing.plaf.basic.BasicColorSwatchesUI extends ColorSwatchesUI {
	
	private var colorSwatches:JColorSwatches;
	private var selectedColorLabel:JLabel;
	private var selectedColorIcon:ColorRectIcon;
	private var colorHexText:JTextField;
	private var alphaAdjuster:JAdjuster;
	private var noColorButton:AbstractButton;
	private var colorTilesPane:JPanel;
	private var topBar:Container;
	private var barLeft:Container;
	private var barRight:Container;
	private var colorSwatchesListener:Object;
	private var colorTilesPaneListener:Object;
	private var selectionRectMC:MovieClip;
	
	public function BasicColorSwatchesUI(){
	}

    public function installUI(c:Component):Void{
		colorSwatches = JColorSwatches(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	public function uninstallUI(c:Component):Void{
		colorSwatches = JColorSwatches(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():Void{
		var pp:String = "ColorSwatches.";
        LookAndFeel.installColorsAndFont(colorSwatches, pp + "background", pp + "foreground", pp + "font");
		LookAndFeel.installBasicProperties(colorSwatches, pp);
        LookAndFeel.installBorder(colorSwatches, pp + "border");
	}
    private function uninstallDefaults():Void{
    	LookAndFeel.uninstallBorder(colorSwatches);
    }	
    
	private function installComponents():Void{
		selectedColorLabel = createSelectedColorLabel();
		selectedColorIcon = createSelectedColorIcon();
		selectedColorLabel.setIcon(selectedColorIcon);
		
		colorHexText = createHexText();
		alphaAdjuster = createAlphaAdjuster();
		noColorButton = createNoColorButton();
		colorTilesPane = createColorTilesPane();
		
		topBar = new JPanel(new BorderLayout());
		barLeft = SoftBox.createHorizontalBox(2, SoftBoxLayout.LEFT);
		barRight = SoftBox.createHorizontalBox(2, SoftBoxLayout.RIGHT);
		topBar.append(barLeft, BorderLayout.WEST);
		topBar.append(barRight, BorderLayout.EAST);
		
		barLeft.append(selectedColorLabel);
		barLeft.append(colorHexText);
		barRight.append(alphaAdjuster);
		barRight.append(noColorButton);
		
		colorSwatches.setLayout(new BorderLayout(4, 4));
		colorSwatches.append(topBar, BorderLayout.NORTH);
		colorSwatches.append(colorTilesPane, BorderLayout.CENTER);
		updateSectionVisibles();
    }
	private function uninstallComponents():Void{
		colorSwatches.remove(topBar);
		colorSwatches.remove(colorTilesPane);
    }
        
	private function installListeners():Void{
		noColorButton.addActionListener(__noColorButtonAction, this);
		
		colorSwatchesListener = new Object();
		colorSwatchesListener[JColorSwatches.ON_STATE_CHANGED] = Delegate.create(this, __colorSelectionChanged);
		colorSwatchesListener[JColorSwatches.ON_HIDDEN] = Delegate.create(this, __colorSwatchesUnShown);
		colorSwatchesListener[JColorSwatches.ON_DESTROY] = colorSwatchesListener[JColorSwatches.ON_HIDDEN];
		colorSwatches.addEventListener(colorSwatchesListener);
		
		colorTilesPaneListener = new Object();
		colorTilesPaneListener[JPanel.ON_ROLLOVER] = Delegate.create(this, __colorTilesPaneRollOver);
		colorTilesPaneListener[JPanel.ON_ROLLOUT] = Delegate.create(this, __colorTilesPaneRollOut);
		colorTilesPaneListener[JPanel.ON_DRAGOUT] = colorTilesPaneListener[JPanel.ON_ROLLOUT];
		colorTilesPaneListener[JPanel.ON_RELEASE] = Delegate.create(this, __colorTilesPaneReleased);
		colorTilesPane.addEventListener(colorTilesPaneListener);
		
		mouseMoveOnTilesPaneListener = new Object();
		mouseMoveOnTilesPaneListener[MouseManager.ON_MOUSE_MOVE] = Delegate.create(this, __colorTilesPaneMouseMove);
		
		colorHexText.addActionListener(__hexTextAction, this);
		colorHexText.addChangeListener(__hexTextChanged, this);
		
		alphaAdjuster.addChangeListener(__adjusterValueChanged, this);
		alphaAdjuster.addActionListener(__adjusterAction, this);
	}
    private function uninstallListeners():Void{
    	colorSwatches.removeEventListener(colorSwatchesListener);
    	MouseManager.removeEventListener(mouseMoveOnTilesPaneListener);
    }
    
    //------------------------------------------------------------------------------
    private function __adjusterValueChanged():Void{
		updateSelectedColorLabelColor(getColorFromHexTextAndAdjuster());
    }
    private function __adjusterAction():Void{
    	colorSwatches.setSelectedColor(getColorFromHexTextAndAdjuster());
    }
    
    private function __hexTextChanged():Void{
		updateSelectedColorLabelColor(getColorFromHexTextAndAdjuster());
    }
    private function __hexTextAction():Void{
    	colorSwatches.setSelectedColor(getColorFromHexTextAndAdjuster());
    }
    
    private var mouseMoveOnTilesPaneListener:Object;
    private function __colorTilesPaneRollOver():Void{
    	MouseManager.removeEventListener(mouseMoveOnTilesPaneListener);
    	MouseManager.addEventListener(mouseMoveOnTilesPaneListener);
    }
    private function __colorTilesPaneRollOut():Void{
    	stopMouseMovingSelection();
    }
    private var lastOutMoving:Boolean;
    private function __colorTilesPaneMouseMove():Void{
    	var p:Point = colorTilesPane.getMousePosition();
    	var color:ASColor = getColorWithPosAtColorTilesPane(p);
    	if(color != null){
    		var sp:Point = getSelectionRectPos(p);
    		selectionRectMC._visible = true;
    		selectionRectMC._x = sp.x;
    		selectionRectMC._y = sp.y;
			updateSelectedColorLabelColor(color);
			fillHexTextWithColor(color);
    		lastOutMoving = false;
    		updateAfterEvent();
    	}else{
    		color = colorSwatches.getSelectedColor();
    		selectionRectMC._visible = false;
    		if(lastOutMoving != true){
				updateSelectedColorLabelColor(color);
				fillHexTextWithColor(color);
    		}
    		lastOutMoving = true;
    	}
    }
    private function __colorTilesPaneReleased():Void{
    	var p:Point = colorTilesPane.getMousePosition();
    	var color:ASColor = getColorWithPosAtColorTilesPane(p);
    	if(color != null){
    		colorSwatches.setSelectedColor(color);
    	}
    }
    
    private function __noColorButtonAction():Void{
    	colorSwatches.setSelectedColor(null);
    }
    
    private var colorTilesMC:MovieClip;
	private function __colorTilesPaneCreated():Void{
		colorTilesMC = colorTilesPane.createMovieClip();
		selectionRectMC = colorTilesPane.createMovieClip();
		paintColorTiles();
		paintSelectionRect();
		selectionRectMC._visible = false;
	}
	
	private function __colorSelectionChanged():Void{
		var color:ASColor = colorSwatches.getSelectedColor();
		fillHexTextWithColor(color);
		fillAlphaAdjusterWithColor(color);
		updateSelectedColorLabelColor(color);
	}
	private function __colorSwatchesUnShown():Void{
		stopMouseMovingSelection();
	}
	private function stopMouseMovingSelection():Void{
    	MouseManager.removeEventListener(mouseMoveOnTilesPaneListener);
		selectionRectMC._visible = false;
		var color:ASColor = colorSwatches.getSelectedColor();
		updateSelectedColorLabelColor(color);
		fillHexTextWithColor(color);
	}
	
	//-----------------------------------------------------------------------
	private function create(c:Component):Void{
		super.create(c);
		updateSectionVisibles();
	}
	private function paint(c:Component, g:Graphics, b:Rectangle):Void{
		super.paint(c, g, b);
		updateSectionVisibles();
		updateSelectedColorLabelColor(colorSwatches.getSelectedColor());
		fillHexTextWithColor(colorSwatches.getSelectedColor());
	}
	private function updateSectionVisibles():Void{
		colorHexText.setVisible(colorSwatches.isHexSectionVisible());
		alphaAdjuster.setVisible(colorSwatches.isAlphaSectionVisible());
		noColorButton.setVisible(colorSwatches.isNoColorSectionVisible());
	}
    
	//*******************************************************************************
	//      Data caculating methods
	//******************************************************************************
    private function getColorFromHexTextAndAdjuster():ASColor{
    	var text:String = colorHexText.getText();
    	if(text.charAt(0) == "#"){
    		text = text.substr(1);
    	}
    	var rgb:Number = parseInt("0x" + text);
    	return new ASColor(rgb, alphaAdjuster.getValue());
    }
    private var hexTextColor:ASColor;
    private function fillHexTextWithColor(color:ASColor):Void{
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
	    	hex = "#" + hex.toUpperCase();
	    	colorHexText.setText(hex);
    	}
    }
    private function fillAlphaAdjusterWithColor(color:ASColor):Void{
    	var alpha:Number = (color == null ? 100 : color.getAlpha());
		alphaAdjuster.setValue(alpha);
    }
    
    private function isEqualsToSelectedIconColor(color:ASColor):Boolean{
		if(color == null){
			return selectedColorIcon.getColor() == null;
		}else{
			return color.equals(selectedColorIcon.getColor());
		}
	}
    private function updateSelectedColorLabelColor(color:ASColor):Void{
    	if(!isEqualsToSelectedIconColor(color)){
	    	selectedColorIcon.setColor(color);
	    	selectedColorLabel.repaint();
	    	colorSwatches.getModel().fireColorAdjusting(color);
    	}
    }
    private function getSelectionRectPos(p:Point):Point{
    	var L:Number = getTileL();
    	return new Point(Math.floor(p.x/L)*L, Math.floor(p.y/L)*L);
    }
    //if null returned means not in color tiles bounds
    private function getColorWithPosAtColorTilesPane(p:Point):ASColor{
    	var L:Number = getTileL();
    	var size:Dimension = getColorTilesPaneSize();
    	if(p.x < 0 || p.y < 0 || p.x >= size.width || p.y >= size.height){
    		return null;
    	}
    	var alpha:Number = alphaAdjuster.getValue();
    	if(p.x < L){
    		var index:Number = Math.floor(p.y/L);
    		index = Math.max(0, Math.min(11, index));
    		return new ASColor(getLeftColumColors()[index], alpha);
    	}
    	if(p.x < L*2){
    		return new ASColor(0x000000, alpha);
    	}
    	var x:Number = p.x - L*2;
    	var y:Number = p.y;
    	var bigTile:Number = (L*6);
    	var tx:Number = Math.floor(x/bigTile);
    	var ty:Number = Math.floor(y/bigTile);
    	var ti:Number = ty*3 + tx;
    	var xi:Number = Math.floor((x - tx*bigTile)/L);
    	var yi:Number = Math.floor((y - ty*bigTile)/L);
    	return getTileColorByTXY(ti, xi, yi, alpha);
    }
    private function getLeftColumColors():Array{
    	return [0x000000, 0x333333, 0x666666, 0x999999, 0xCCCCCC, 0xFFFFFF, 
							  0xFF0000, 0x00FF00, 0x0000FF, 0xFFFF00, 0x00FFFF, 0xFF00FF];
    }
    private function getTileColorByTXY(t:Number, x:Number, y:Number, alpha:Number):ASColor{
    	if(alpha == undefined) alpha = 100;
		var rr:Number = 0x33*t;
		var gg:Number = 0x33*x;
		var bb:Number = 0x33*y;
		var c:ASColor = ASColor.getASColor(rr, gg, bb, alpha);
		return c;
    }
	private function paintColorTiles():Void{
		var g:Graphics = new Graphics(colorTilesMC);	
		var startX:Number = 0;
		var startY:Number = 0;
		var L:Number = getTileL();
		var leftLine:Array = getLeftColumColors();
		for(var y:Number=0; y<6*2; y++){
			fillRect(g, startX, startY+y*L, new ASColor(leftLine[y]));
		}
		startX += L;
		for(var y:Number=0; y<6*2; y++){
			fillRect(g, startX, startY+y*L, ASColor.BLACK);
		}
		startX += L;		
		
		for(var t:Number=0; t<6; t++){
			for(var x:Number=0; x<6; x++){
				for(var y:Number=0; y<6; y++){
					var c:ASColor = getTileColorByTXY(t, x, y);
					fillRect(g, 
							 startX + (t%3)*(6*L) + x*L, 
							 startY + Math.floor(t/3)*(6*L) + y*L, 
							 c);
				}
			}
		}
	}
	private function paintSelectionRect():Void{
		var g:Graphics = new Graphics(selectionRectMC);
		g.drawRectangle(new Pen(ASColor.WHITE, 0), 0, 0, getTileL(), getTileL());
	}
	
	private function fillRect(g:Graphics, x:Number, y:Number, c:ASColor):Void{
		g.beginDraw(new Pen(ASColor.BLACK, 0));
		g.beginFill(new SolidBrush(c));
		g.rectangle(x, y, getTileL(), getTileL());
		g.endFill();
		g.endDraw();
	}
	private function getColorTilesPaneSize():Dimension{
		return new Dimension((3*6+2)*getTileL(), (2*6)*getTileL());
	}
	
	private function getTileL():Number{
		return 12;
	}
    
	//*******************************************************************************
	//              Override these methods to easiy implement different look
	//******************************************************************************
	public function addComponentColorSectionBar(com:Component):Void{
		barRight.append(com);
	}	
	
	private function createSelectedColorLabel():JLabel{
		var label:JLabel = new JLabel();
		var bb:BevelBorder = new BevelBorder(null, BevelBorder.LOWERED);
		bb.setThickness(1);
		label.setBorder(bb); 
		return label;
	}
	
	private function createSelectedColorIcon():ColorRectIcon{
		return new ColorRectIcon(38, 18, colorSwatches.getSelectedColor());
	}
	
	private function createHexText():JTextField{
		return new JTextField("#FFFFFF", 6);
	}
	
	private function createAlphaAdjuster():JAdjuster{
		var adjuster:JAdjuster = new JAdjuster(4, JAdjuster.VERTICAL);
		adjuster.setValueTranslator(
			function(value:Number):String{
				return Math.round(value) + "%";
			});
		adjuster.setValues(100, 0, 0, 100);
		return adjuster;
	}
	private function createNoColorButton():AbstractButton{
		return new JButton(new NoColorIcon(16, 16));
	}
	private function createColorTilesPane():JPanel{
		var p:JPanel = new JPanel();
		p.setBorder(null); //ensure there is no border there
    	var size:Dimension = getColorTilesPaneSize();
    	size.change(1, 1);
		p.setPreferredSize(size);
		p.addEventListener(JPanel.ON_CREATED, __colorTilesPaneCreated, this);
		return p;
	}
}