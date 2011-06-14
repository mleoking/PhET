//All internationalizable string collected here
//All strings must default to english

import edu.colorado.phet.flashcommon.*;

class Internationalizer{
	private var simStrings:SimStrings;
	private var theGUI:Object;
	
	function Internationalizer(simStrings:SimStrings, theGUI:Object){
		this.simStrings = simStrings;
		this.theGUI = theGUI;
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
		this.setString(_level0.plusChargeBag_mc.label_txt, "oneNanoC", "center");
		this.setString(_level0.minusChargeBag_mc.label_txt, "oneNanoC", "center");
		this.setString(_level0.sensorBag_mc.label_txt, "EFieldSensors", "center");
		this.theGUI.voltageUnit_str = simStrings.get( "voltageUnit" );
		this.theGUI.eFieldUnit_str = simStrings.get( "eFieldUnit" );
		this.theGUI.angleUnit_str = simStrings.get( "angleUnit" );
		_level0.tapeMeasure_mc.distanceUnit_str = simStrings.get( "distanceUnit" );
		this.setString(_level0.backgroundGrid_mc.scaleRuler_mc.label_txt, "oneMeter", "center");
		this.setString(_level0.controlPanel_mc.showE_ch.label_txt,"showEField","left");
		this.setString(_level0.controlPanel_mc.directionOnly_ch.label_txt,"directionOnly","left");
		this.setString(_level0.controlPanel_mc.showVoltage_ch.label_txt,"showLoRes","left");
		this.setString(_level0.controlPanel_mc.hiResVoltage_ch.label_txt,"showHiRes","left");
		this.setString(_level0.controlPanel_mc.showGrid_ch.label_txt,"grid","left");
		this.setString(_level0.controlPanel_mc.showNumbers_ch.label_txt,"showNumbers","left");
		this.setString(_level0.controlPanel_mc.showTapeMeasure_ch.label_txt,"tapeMeasure","left");
		this.setString(_level0.controlPanel_mc.clearButton_mc.label_txt, "clearAll", "center");
		this.setString(_level0.controlPanel_mc.optimize_ch.label_txt,"moreSpeedLessRes","left");
		this.setString(_level0.voltageSensor_mc.traceVButton_mc.label_txt, "plot", "center");
		this.setString(_level0.voltageSensor_mc.clearTraceV_mc.label_txt, "clear", "center");
		this.setString(_level0.voltageSensor_mc.label1_txt,"equipotential","center");
		this.setString(_level0.voltageSensor_mc.label2_txt,"voltage","center");
		//_root.landingSpeed_str = this.simStrings.get("landingSpeedEq");

		//this.setString(_level0.helpPlaque_mc.longHelp_txt, "longHelp", "left");
		
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
			//field.setTextFormat(currentTextFormat);
			_level0.common.prepareTranslatedTextField(field);
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