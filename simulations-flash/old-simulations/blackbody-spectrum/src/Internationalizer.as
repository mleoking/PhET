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
		this.setString(_root.glowingBall_mc.b_txt, "b", "center");
		this.setString(_root.glowingBall_mc.g_txt, "g", "center");
		this.setString(_root.glowingBall_mc.r_txt, "r", "center");
		
		this.setString(_root.saveButton_mc.label_txt, "save", "center");
		this.setString(_root.clearButton_mc.label_txt, "clear", "center");
		
		this.setString(_root.thermometer_mc.sun_txt, "sun", "left");
		this.setString(_root.thermometer_mc.lightBulb_txt, "lightBulb", "left");
		this.setString(_root.thermometer_mc.oven_txt, "oven", "left");
		this.setString(_root.thermometer_mc.earth_txt, "earth", "left");
		
		this.setString(_root.rulerCheckbox_mc.label_txt, "showRuler", "left");
		this.setString(_root.xAxis_mc.rightArrow.label_txt, "zoomOut", "left");
		this.setString(_root.xAxis_mc.leftArrow.label_txt, "zoomIn", "right");
		
		this.setString(_root.xAxis_mc.xAxisLabel_mc.label_txt, "wavelength", "right");
		//this.setString(_root.yAxis_mc.yAxisLabel_mc.label_txt, "intensity", "right");
		//vertical text cannot be handled with setString():
		_root.yAxis_mc.yAxisLabel_mc.label_txt = this.simStrings.get("intensity");
		
		this.setString(_root.tempSlider_mc.label_txt, "tempInK", "center");
		
		//this.setComponentLabel(_root.gui_mc.none_rb, "none");
				
		//_root.gui_mc.clearAllButton_mc.buttonLabel = this.simStrings.get("clearAll");
		//_root.gui_mc.clearAllButton_mc.gotoAndPlay(2);
		//_root.gui_mc.clearAllButton_mc.gotoAndStop(1);
		
		//this.setComponentLabel(_root.airResistance_ch, "airResistance");
		//this.setListComponentLabel(_root.dropList, 0, "userChoice");
		
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