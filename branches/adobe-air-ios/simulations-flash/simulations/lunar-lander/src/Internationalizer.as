//All internationalizable string collected here
//All strings must default to english

import edu.colorado.phet.flashcommon.*;

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
		this.setString(_level0.GUIPanel_mc.height_mc.label_txt, "altitude", "center");
		this.setString(_level0.GUIPanel_mc.range_mc.label_txt, "range", "center");
		this.setString(_level0.GUIPanel_mc.vX_mc.label_txt, "vX", "center");
		this.setString(_level0.GUIPanel_mc.vY_mc.label_txt, "vY", "center");
		this.setString(_level0.GUIPanel_mc.thrust_mc.label_txt, "thrust", "center");
		this.setString(_level0.GUIPanel_mc.fuelGauge_mc.label_txt, "fuel", "center");
		this.setString(_level0.GUIPanel_mc.fuelGauge_mc.f_txt, "full", "right");
		this.setString(_level0.GUIPanel_mc.fuelGauge_mc.e_txt, "empty", "right");
		this.setString(_level0.moreGUI_mc.sound_ch.label_txt, "sound", "left");
		this.setString(_level0.moreGUI_mc.vectors_ch.label_txt, "vectors", "left");
		this.setString(_level0.view2_mc.scoreReadout_mc.label_txt, "score", "right");
		this.setString(_root.view2_mc.resetButton_mc.label_txt, "reset", "center");
		
		_root.view2_mc.helpButton_mc.buttonLabel1 = this.simStrings.get("help");
		_root.view2_mc.helpButton_mc.buttonLabel2 = this.simStrings.get("unpause");
		_root.view2_mc.helpButton_mc.gotoAndPlay(2);
		_root.view2_mc.helpButton_mc.gotoAndStop(1);
		
		this.setString(_root.introPlaque_mc.label1_txt, "label1", "left");
		this.setString(_root.introPlaque_mc.label2_txt, "label2", "right");
		this.setString(_root.introPlaque_mc.label3_txt, "label3", "right");
		this.setString(_root.introPlaque_mc.label4_txt, "label4", "right");
		this.setString(_root.introPlaque_mc.label5_txt, "label5", "left");
		this.setString(_root.introPlaque_mc.label6_txt, "label6", "center");
		this.setString(_root.introPlaque_mc.label7_txt, "label7", "left");
		this.setString(_root.introPlaque_mc.startButton_mc.label_txt, "start", "center");
		
		this.setString(_root.view2_mc.pausedSign_mc.label_txt, "paused", "center");
		this.setString(_level0.landingSpeedReport_mc.label1_txt, "softLandingsColon", "right");
		this.setString(_level0.landingSpeedReport_mc.label2_txt, "hardLandingsColon", "right");
		
		_root.landingSpeed_str = this.simStrings.get("landingSpeedEq");
		_root.softLanding_str = this.simStrings.get("softLanding");
		_root.hardLanding_str = this.simStrings.get("hardLanding");
		_root.crashDamaged_str = this.simStrings.get("crash1");
		_root.crashKilled_str = this.simStrings.get("crash2");
		
		this.setString(_root.boulderCrashReport_mc.label_txt, "hitBoulder", "center");
		
		this.setString(_level0.helpPlaque_mc.help1_txt, "help1", "left");
		this.setString(_level0.helpPlaque_mc.help2_txt, "help2", "center");
		this.setString(_level0.helpPlaque_mc.help3_txt, "help3", "center");
		this.setString(_level0.helpPlaque_mc.r_txt, "r", "center");
		this.setString(_level0.helpPlaque_mc.p_txt, "p", "center");
		this.setString(_root.helpPlaque_mc.label2_txt, "label2", "right");
		this.setString(_root.helpPlaque_mc.label3_txt, "label3", "right");
		this.setString(_root.helpPlaque_mc.label4_txt, "label4", "right");
		this.setString(_root.helpPlaque_mc.label5_txt, "label5", "left");
		this.setString(_root.helpPlaque_mc.label6_txt, "label6", "center");
		this.setString(_level0.helpPlaque_mc.velVector_txt, "velVector", "left");
		this.setString(_level0.helpPlaque_mc.accVector_txt, "accVector", "left");
		this.setString(_level0.helpPlaque_mc.longHelp_txt, "longHelp", "left");
		
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