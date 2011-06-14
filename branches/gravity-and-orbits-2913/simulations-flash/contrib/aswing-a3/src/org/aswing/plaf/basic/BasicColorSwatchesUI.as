/*
 Copyright aswing.org, see the LICENCE.txt.
*/
package org.aswing.plaf.basic{

import flash.display.MovieClip;
import flash.events.Event;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.text.TextField;
import flash.ui.Mouse;

import org.aswing.*;
import org.aswing.border.BevelBorder;
import org.aswing.colorchooser.*;
import org.aswing.event.*;
import org.aswing.geom.*;
import org.aswing.graphics.*;
import org.aswing.plaf.*;

/**
 * @private
 */
public class BasicColorSwatchesUI extends BaseComponentUI implements ColorSwatchesUI{
	
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
	private var selectionRectMC:AWSprite;
	
	public function BasicColorSwatchesUI(){
		super();		
	}
    
    protected function getPropertyPrefix():String{
    	return "ColorSwatches.";
    }

    override public function installUI(c:Component):void{
		colorSwatches = JColorSwatches(c);
		installDefaults();
		installComponents();
		installListeners();
    }
    
	override public function uninstallUI(c:Component):void{
		colorSwatches = JColorSwatches(c);
		uninstallDefaults();
		uninstallComponents();
		uninstallListeners();
    }
	
	private function installDefaults():void{
		var pp:String = getPropertyPrefix();
        LookAndFeel.installColorsAndFont(colorSwatches, pp);
		LookAndFeel.installBasicProperties(colorSwatches, pp);
        LookAndFeel.installBorderAndBFDecorators(colorSwatches, pp);
	}
    private function uninstallDefaults():void{
    	LookAndFeel.uninstallBorderAndBFDecorators(colorSwatches);
    }	
    
	private function installComponents():void{
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
		
		topBar.setUIElement(true);
		colorTilesPane.setUIElement(true);
		
		colorSwatches.setLayout(new BorderLayout(4, 4));
		colorSwatches.append(topBar, BorderLayout.NORTH);
		colorSwatches.append(colorTilesPane, BorderLayout.CENTER);
		createTitles();
		updateSectionVisibles();
    }
	private function uninstallComponents():void{
		colorSwatches.remove(topBar);
		colorSwatches.remove(colorTilesPane);
    }
        
	private function installListeners():void{
		noColorButton.addActionListener(__noColorButtonAction);

		colorSwatches.addEventListener(InteractiveEvent.STATE_CHANGED, __colorSelectionChanged);
		colorSwatches.addEventListener(AWEvent.HIDDEN, __colorSwatchesUnShown);
		
		colorTilesPane.addEventListener(MouseEvent.ROLL_OVER, __colorTilesPaneRollOver);
		colorTilesPane.addEventListener(MouseEvent.ROLL_OUT, __colorTilesPaneRollOut);
		colorTilesPane.addEventListener(DragAndDropEvent.DRAG_EXIT, __colorTilesPaneRollOut);
		colorTilesPane.addEventListener(MouseEvent.MOUSE_UP, __colorTilesPaneReleased);
		
		colorHexText.addActionListener(__hexTextAction);
		colorHexText.getTextField().addEventListener(Event.CHANGE, __hexTextChanged);
		
		alphaAdjuster.addStateListener(__adjusterValueChanged);
		alphaAdjuster.addActionListener(__adjusterAction);
	}
    private function uninstallListeners():void{
    	noColorButton.removeActionListener(__noColorButtonAction);
    	
    	colorSwatches.removeEventListener(InteractiveEvent.STATE_CHANGED, __colorSelectionChanged);
    	colorSwatches.removeEventListener(AWEvent.HIDDEN, __colorSwatchesUnShown);
    	colorSwatches.removeEventListener(MouseEvent.MOUSE_MOVE, __colorTilesPaneMouseMove); 
    	
		colorTilesPane.removeEventListener(MouseEvent.ROLL_OVER, __colorTilesPaneRollOver);
		colorTilesPane.removeEventListener(MouseEvent.ROLL_OUT, __colorTilesPaneRollOut);
		colorTilesPane.removeEventListener(DragAndDropEvent.DRAG_EXIT, __colorTilesPaneRollOut);
		colorTilesPane.removeEventListener(MouseEvent.MOUSE_UP, __colorTilesPaneReleased);   
		
		colorHexText.removeActionListener(__hexTextAction);
		colorHexText.getTextField().removeEventListener(Event.CHANGE, __hexTextChanged);	
		
		alphaAdjuster.removeStateListener(__adjusterValueChanged);
		alphaAdjuster.removeActionListener(__adjusterAction);		
    }
    
    //------------------------------------------------------------------------------
    private function __adjusterValueChanged(e:Event):void{
		updateSelectedColorLabelColor(getColorFromHexTextAndAdjuster());
    }
    private function __adjusterAction(e:Event):void{
    	colorSwatches.setSelectedColor(getColorFromHexTextAndAdjuster());
    }
    
    private function __hexTextChanged(e:Event):void{
		updateSelectedColorLabelColor(getColorFromHexTextAndAdjuster());
    }
    private function __hexTextAction(e:Event):void{
    	colorSwatches.setSelectedColor(getColorFromHexTextAndAdjuster());
    }
    
    private function __colorTilesPaneRollOver(e:Event):void{
    	colorSwatches.removeEventListener(MouseEvent.MOUSE_MOVE, __colorTilesPaneMouseMove);
    	colorSwatches.addEventListener(MouseEvent.MOUSE_MOVE, __colorTilesPaneMouseMove);
    	
    }
    private function __colorTilesPaneRollOut(e:Event):void{
    	stopMouseMovingSelection();
    }
    private var lastOutMoving:Boolean;
    private function __colorTilesPaneMouseMove(e:Event):void{
    	var p:IntPoint = colorTilesPane.getMousePosition();
    	var color:ASColor = getColorWithPosAtColorTilesPane(p);
    	if(color != null){
    		var sp:IntPoint = getSelectionRectPos(p);
    		selectionRectMC.visible = true;
    		selectionRectMC.x = sp.x;
    		selectionRectMC.y = sp.y;
			updateSelectedColorLabelColor(color);
			fillHexTextWithColor(color);
    		lastOutMoving = false;
    		//updateAfterEvent();
    	}else{
    		color = colorSwatches.getSelectedColor();
    		selectionRectMC.visible = false;
    		if(lastOutMoving != true){
				updateSelectedColorLabelColor(color);
				fillHexTextWithColor(color);
    		}
    		lastOutMoving = true;
    	}
    }
    private function __colorTilesPaneReleased(e:MouseEvent):void{
    	var p:IntPoint = new IntPoint(e.localX, e.localY);//colorTilesPane.getMousePosition();
    	var color:ASColor = getColorWithPosAtColorTilesPane(p);
    	if(color != null){
    		colorSwatches.setSelectedColor(color);
    	}
    }
    
    private function __noColorButtonAction(e:AWEvent):void{
    	colorSwatches.setSelectedColor(null);
    }
    
    private var colorTilesMC:AWSprite;
	private function createTitles():void{
		colorTilesMC = new AWSprite();
		selectionRectMC = new AWSprite();
		colorTilesPane.addChild(colorTilesMC);
		colorTilesPane.addChild(selectionRectMC);
		paintColorTiles();
		paintSelectionRect();
		selectionRectMC.visible = false;
	}
	
	private function __colorSelectionChanged(e:Event):void{
		var color:ASColor = colorSwatches.getSelectedColor();
		fillHexTextWithColor(color);
		fillAlphaAdjusterWithColor(color);
		updateSelectedColorLabelColor(color);
	}
	private function __colorSwatchesUnShown(e:Event):void{
		stopMouseMovingSelection();
	}
	private function stopMouseMovingSelection():void{
		colorSwatches.removeEventListener(MouseEvent.MOUSE_MOVE, __colorTilesPaneMouseMove);
		selectionRectMC.visible = false;
		var color:ASColor = colorSwatches.getSelectedColor();
		updateSelectedColorLabelColor(color);
		fillHexTextWithColor(color);
	}
	
	//-----------------------------------------------------------------------
	override public function paint(c:Component, g:Graphics2D, b:IntRectangle):void{
		super.paint(c, g, b);
		updateSectionVisibles();
		updateSelectedColorLabelColor(colorSwatches.getSelectedColor());
		fillHexTextWithColor(colorSwatches.getSelectedColor());
	}
	private function updateSectionVisibles():void{
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
    	return new ASColor(rgb, alphaAdjuster.getValue()/100);
    }
    private var hexTextColor:ASColor;
    private function fillHexTextWithColor(color:ASColor):void{
    	if (color == null){
    		 hexTextColor = color;
	    	colorHexText.setText("#000000");
    	}else if(!color.equals(hexTextColor)){
	    	hexTextColor = color;
	    	var hex:String;
	    	hex = color.getRGB().toString(16);
	    	for(var i:Number=6-hex.length; i>0; i--){
	    		hex = "0" + hex;
	    	}
	    	hex = "#" + hex.toUpperCase();
	    	colorHexText.setText(hex);
    	}
    }
    private function fillAlphaAdjusterWithColor(color:ASColor):void{
    	var alpha:Number = (color == null ? 100 : color.getAlpha());
		alphaAdjuster.setValue(alpha*100);
    }
    
    private function isEqualsToSelectedIconColor(color:ASColor):Boolean{
		if(color == null){
			return selectedColorIcon.getColor() == null;
		}else{
			return color.equals(selectedColorIcon.getColor());
		}
	}
    private function updateSelectedColorLabelColor(color:ASColor):void{
    	if(!isEqualsToSelectedIconColor(color)){
	    	selectedColorIcon.setColor(color);
	    	selectedColorLabel.repaint();
	    	colorSwatches.getModel().fireColorAdjusting(color);
    	}
    }
    private function getSelectionRectPos(p:IntPoint):IntPoint{
    	var L:Number = getTileL();
    	return new IntPoint(Math.floor(p.x/L)*L, Math.floor(p.y/L)*L);
    }
    //if null returned means not in color tiles bounds
    private function getColorWithPosAtColorTilesPane(p:IntPoint):ASColor{
    	var L:Number = getTileL();
    	var size:IntDimension = getColorTilesPaneSize();
    	if(p.x < 0 || p.y < 0 || p.x >= size.width || p.y >= size.height){
    		return null;
    	}
    	var alpha:Number = alphaAdjuster.getValue()/100;
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
    private function getTileColorByTXY(t:Number, x:Number, y:Number, alpha:Number=1):ASColor{
		var rr:Number = 0x33*t;
		var gg:Number = 0x33*x;
		var bb:Number = 0x33*y;
		var c:ASColor = ASColor.getASColor(rr, gg, bb, alpha);
		return c;
    }
	private function paintColorTiles():void{
		var g:Graphics2D = new Graphics2D(colorTilesMC.graphics);	
		var startX:Number = 0;
		var startY:Number = 0;
		var L:Number = getTileL();
		var leftLine:Array = getLeftColumColors();
		for(var y:Number=0; y<6*2; y++){
			fillRect(g, startX, startY+y*L, new ASColor(leftLine[y]));
		}
		startX += L;
		for(var y2:Number=0; y2<6*2; y2++){
			fillRect(g, startX, startY+y2*L, ASColor.BLACK);
		}
		startX += L;		
		
		for(var t:Number=0; t<6; t++){
			for(var x:Number=0; x<6; x++){
				for(var y3:Number=0; y3<6; y3++){
					var c:ASColor = getTileColorByTXY(t, x, y3);
					fillRect(g, 
							 startX + (t%3)*(6*L) + x*L, 
							 startY + Math.floor(t/3)*(6*L) + y3*L, 
							 c);
				}
			}
		}
	}
	private function paintSelectionRect():void{
		var g:Graphics2D = new Graphics2D(selectionRectMC.graphics);
		g.drawRectangle(new Pen(ASColor.WHITE, 0), 0, 0, getTileL(), getTileL());
	}
	
	private function fillRect(g:Graphics2D, x:Number, y:Number, c:ASColor):void{
		g.beginDraw(new Pen(ASColor.BLACK, 0));
		g.beginFill(new SolidBrush(c));
		g.rectangle(x, y, getTileL(), getTileL());
		g.endFill();
		g.endDraw();
	}
	private function getColorTilesPaneSize():IntDimension{
		return new IntDimension((3*6+2)*getTileL(), (2*6)*getTileL());
	}
	
	private function getTileL():Number{
		return 12;
	}
    
	//*******************************************************************************
	//              Override these methods to easiy implement different look
	//******************************************************************************
	public function addComponentColorSectionBar(com:Component):void{
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
		return new JButton("", new NoColorIcon(16, 16));
	}
	private function createColorTilesPane():JPanel{
		var p:JPanel = new JPanel();
		p.setBorder(null); //ensure there is no border there
    	var size:IntDimension = getColorTilesPaneSize();
    	size.change(1, 1);
		p.setPreferredSize(size);
		p.mouseChildren = false;
		return p;
	}
}
}