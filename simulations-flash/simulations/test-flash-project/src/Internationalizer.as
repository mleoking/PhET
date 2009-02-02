//All internationalizable string collected here
//All strings must default to english
class Internationalizer{
	private var simStrings:SimStrings;
	//private var mainView:Object;
	
	function Internationalizer(simStrings:SimStrings){
		this.simStrings = simStrings;
		//this.mainView = mainView;
		//trace("simStrings.getBaseName(): "+this.simStrings.getLocale());
		this.initialize();
	}//end of constructor
	
	/*
	List internationalizable strings here
	(For human readers only. All commented out.)
	
	

	*/
	
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		//trace("internationalizer.initialize() called");
		//this.setString(_root.label1_txt, "friction", "center");
		
		//this.setComponentLabel(_root.gui_mc.none_rb, "none");
				
		//_root.gui_mc.clearAllButton_mc.buttonLabel = this.simStrings.get("clearAll");
		//_root.gui_mc.clearAllButton_mc.gotoAndPlay(2);
		//_root.gui_mc.clearAllButton_mc.gotoAndStop(1);
		
		//this.setComponentLabel(_root.airResistance_ch, "airResistance");
		//this.setListComponentLabel(_root.dropList, 0, "userChoice");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 0, "selectPreset");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 1, "sunAndPlanet");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 2, "sunPlanetMoon");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 3, "sunPlanetComet");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 4, "binaryStarPlanet");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 5, "trojanAsteroids");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 6, "fourStarBallet");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 7, "slingshot");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 8, "doubleSlingshot");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 9, "hyperbolics");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 10, "ellipses");
		this.setListComponentLabel(_root.controlPanel_mc.myComboBox, 11, "doubleDouble");
		
		_level0.controlPanel_mc.startButton_mc.buttonLabel = this.simStrings.get("start");
		_root.controlPanel_mc.startButton_mc.gotoAndPlay(2);
		_root.controlPanel_mc.startButton_mc.gotoAndStop(1);
		_level0.controlPanel_mc.stopButton_mc.buttonLabel = this.simStrings.get("stop");
		_root.controlPanel_mc.stopButton_mc.gotoAndPlay(2);
		_root.controlPanel_mc.stopButton_mc.gotoAndStop(1);
		_level0.controlPanel_mc.resetButton_mc.buttonLabel = this.simStrings.get("reset");
		_root.controlPanel_mc.resetButton_mc.gotoAndPlay(2);
		_root.controlPanel_mc.resetButton_mc.gotoAndStop(1);
		
		setString(_root.controlPanel_mc.trackCM_cb.label_txt, "systemCentered", "left");
		setString(_root.controlPanel_mc.showTraces_cb.label_txt, "showTraces", "left");
		setString(_root.controlPanel_mc.showGrid_cb.label_txt, "showGrid", "left");
		setString(_root.controlPanel_mc.tapeMeasure_cb.label_txt, "tapeMeasure", "left");
		
		setString(_root.controlPanel_mc.slider_mc.leftLabel_txt, "accurate", "left");
		setString(_root.controlPanel_mc.slider_mc.rightLabel_txt, "fast", "right");
		
		_level0.controlPanel_mc.helpButton_mc.buttonLabel = this.simStrings.get("help");
		_root.controlPanel_mc.helpButton_mc.gotoAndPlay(2);
		_root.controlPanel_mc.helpButton_mc.gotoAndStop(1);
		
		setString(_root.labelHolder_mc["Initial Settings:"], "initialSettings", "center");
		setString(_root.labelHolder_mc["position"], "position", "right");
		setString(_root.labelHolder_mc["velocity"], "velocity", "right");
		setString(_root.labelHolder_mc["mass"], "mass", "right");
		setString(_root.labelHolder_mc["x"], "xPos", "center");
		setString(_root.labelHolder_mc["y"], "yPos", "center");
		setString(_root.labelHolder_mc["x "], "xVel", "center");
		setString(_root.labelHolder_mc["y "], "yVel", "center");
		setString(_root.radioGroup_mc.label_txt, "numberOfBodies", "right");
		setString(_root.labelHolder_mc["body 1"], "body1", "right");
		setString(_root.labelHolder_mc["body 2"], "body2", "right");
		setString(_root.labelHolder_mc["body 3"], "body3", "right");
		setString(_root.labelHolder_mc["body 4"], "body4", "right");
		setString(_root.timeHolder_mc["timeEquals"], "timeEquals","right");
		
		_level0.infoPage_mc.backButton_mc.buttonLabel = this.simStrings.get("back");
		_level0.infoPage_mc.backButton_mc.gotoAndPlay(2);
		_level0.infoPage_mc.backButton_mc.gotoAndStop(1);
		
		setString(_root.trajectoryHolder_mc.specWindow_mc.massEquals_txt, "massEquals", "right");
		setString(_root.trajectoryHolder_mc.specWindow_mc.xEquals_txt, "xEquals", "right");
		setString(_root.trajectoryHolder_mc.specWindow_mc.yEquals_txt, "yEquals", "right");
		setString(_root.trajectoryHolder_mc.specWindow_mc.vXEquals_txt, "vXEquals", "right");
		setString(_root.trajectoryHolder_mc.specWindow_mc.vYEquals_txt, "vYEquals", "right");
		
		setString(_root.infoPage_mc.help0_txt, "help0", "left");
		setString(_root.infoPage_mc.help1_txt, "help1", "left");
		setString(_root.infoPage_mc.help2_txt, "help2", "left");
		setString(_root.infoPage_mc.help3_txt, "help3", "left");
		setString(_root.infoPage_mc.help4_txt, "help4", "left");
		setString(_root.infoPage_mc.help5_txt, "help5", "left");
		setString(_root.infoPage_mc.help6_txt, "help6", "left");
		coverStaticString(_root.infoPage_mc.help7_txt, "help7", "left");
		setString(_root.infoPage_mc.help8_txt, "help8", "left");
		setString(_root.infoPage_mc.help9_txt, "help9", "left");
	}//end of initialize
	
	
	
	function setString(field:TextField, key:String, alignment:String){
		var stringValue:String = this.simStrings.get( key );
		var currentTextFormat:TextFormat = field.getTextFormat();
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			if(field.html){
				field.htmlText = stringValue;
			}else{
				//search for "newline" 
				var subString_arr:Array = stringValue.split("\\n");
				if(subString_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < subString_arr.length; i++){
						newStringValue += subString_arr[i]+newline;
					}
					stringValue = newStringValue;
				}
				field.text = stringValue;
			}
			field.setTextFormat(currentTextFormat);
			this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of setString()
	
	function setComponentLabel(myComponent:Object, key:String){
		var stringValue:String = this.simStrings.get( key );
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			myComponent.label = stringValue;
		}
	}
	
	function setListComponentLabel(myComponent:Object, itemNbr:Number, key:String){
		var stringValue:String = this.simStrings.get( key );
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			//trace("before: "+myComponent.getItemAt(itemNbr).label)
			myComponent.replaceItemAt(itemNbr, stringValue);
		}
	}
	
	function coverStaticString(field:TextField, key:String, alignment:String){
		var stringValue:String = this.simStrings.get( key );
		if(stringValue == "keyNotFound" || stringValue == ""){
		   //trace("key = "+ key +  "   Do nothing.");  //String will default to English
		}else{
			if(field.html){
				field.htmlText = stringValue;
			}else{
				//search for "newline" 
				var subString_arr:Array = stringValue.split('\\n');
				if(subString_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < subString_arr.length; i++){
						newStringValue += subString_arr[i]+newline;
					}
					stringValue = newStringValue;
				}
				//set background to cover original string
				field.background = true;
				//field.border = true;	//field._parent.backgrdColorState = true;
				field.backgroundColor = 0xFFFF66;	//field._parent.backgrdColor = 0xFFFF66
				//var frameNbr:Number = field._parent._currentframe;
				//field._parent["textString" + frameNbr] = stringValue;
				field.text = stringValue;
				trace("static cover text: "+field.text);
				//trace("frameNbr: "+frameNbr+"   textString_arr: "+ field._parent["textString" + frameNbr]);
			}
			
			this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of coverStaticString
	
	function resizeText(txtField:Object, alignment:String):Void{  //get an error when Object = textField
		//trace("name: "+txtField._name + "   multiline: "+txtField.multiline + "   wordwrap: "+txtField.wordwrap);
		var mTextField:Object = txtField;
		var mTextFormat:TextFormat = txtField.getTextFormat();
		var alignment:String = alignment;
		//trace(mTextField.text+" has alignment"+alignment);
		//trace(mTextField.text+" has textWidth "+mTextField.textWidth+" and field._width " + mTextField._width);
		//Check that string fits inside button and reduce font size if necessary
		
		if(mTextField.textWidth + 2 >= mTextField._width) {
			trace("name: "+mTextField._name + "  text resized ");
			var ratio = 1.15*mTextField.textWidth/mTextField._width;  //fudge factor of 1.15 to cover BOLDed text
			var initialHeight = mTextField._height;
			trace(mTextField.text + " too long by factor of " + ratio + "   Initial height is " + mTextField._height+ "   Initial y is "+mTextField._y);
			mTextFormat.size = Math.round(mTextFormat.size/ratio);  
			mTextField.setTextFormat(mTextFormat);
			trace("New font size is "+ mTextField.getTextFormat().size);
			mTextField.autoSize = alignment;  //resize bounding box
			var finalHeight = mTextField._height;
			mTextField._y += (initialHeight - finalHeight)/2;  //keep text field vertically centered in button
			//trace("New height is "+ mTextField._height+ "   Final y is " + mTextField._y);
			//trace(mTextField.text+" has field._width " + mTextField._width);
		}
	}
						
}//end of class