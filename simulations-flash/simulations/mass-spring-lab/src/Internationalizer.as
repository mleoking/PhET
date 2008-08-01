//All internationalizable string collected here
//All strings must default to english
class Internationalizer{
	private var simStrings:SimStrings;
	private var countryCode:String;
	//private var mainView:Object;
	
	function Internationalizer(simStrings:SimStrings, countryCode:String){
		this.simStrings = simStrings;
		this.countryCode = countryCode;
		//this.mainView = mainView;
		//trace("simStrings.getBaseName(): "+this.simStrings.getLocale());
		this.initialize();
	}//end of constructor
	
	/*
	List internationalizable strings here
	(For human readers only. All commented out.)
	_root.ruler_ch.Label = "Ruler"
	_root.timer_ch.Label = "Timer"
	_root.controlPanel_mc.helpButton_mc.label2_txt = "Hide Help"
	

	*/
	
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		//this.setString(_root.controlPanel_mc.helpButton_mc.label1_txt, "help", "center");
		//trace("internationalizer.initialize() called");

		
		this.setString(_root.label1_txt, "friction", "center");
		this.setString(_root.label2_txt, "none", "left");
		this.setString(_root.label3_txt, "lots", "right");
		this.setString(_root.label4_txt, "softness", "center");
		this.setString(_root.label5_txt, "soft", "left");
		this.setString(_root.label6_txt, "hard", "right");
		this.setString(_root.label7_txt, "showEnergyOf", "center");
		
		this.setString(_root.hangme_mc.label_txt, "hangMe", "center");
		
		this.setComponentLabel(_root.noShow_rb, "noShow");
		//_root.noShow_rb.label = this.simStrings.get("noShow");
		
		this.setComponentLabel(_root.realTime_rb, "realTime");
		this.setComponentLabel(_root.quarterTime_rb, "quarterTime");
		this.setComponentLabel(_root.sixteenthTime_rb, "16thTime");
		this.setComponentLabel(_root.pause_rb, "pause");
		
		this.setComponentLabel(_root.jupiter_rb, "jupiter");
		this.setComponentLabel(_root.moon_rb, "moon");
		this.setComponentLabel(_root.earth_rb, "earth");
		this.setComponentLabel(_root.planetX_rb, "planetX");
		this.setComponentLabel(_root.gEqZero_rb, "gEqZero");
		
		this.setComponentLabel(_root.stopWatch_ch, "stopWatch");
		this.setComponentLabel(_root.sound_ch, "sound");
		
		_root.helpButton_mc.showHelpLabel = this.simStrings.get("showHelp");
		_root.helpButton_mc.hideHelpLabel = this.simStrings.get("hideHelp");
		
		_root.noHelp_mc.label1 = this.simStrings.get("noHelp");
		_root.noHelp_mc.label2 = this.simStrings.get("dontBother");
		_root.noHelp_mc.label3 = this.simStrings.get("nothinOK");
		
		this.setString(_root.myPauseSign_mc.label_txt, "paused", "center");
		
		this.setString(_level0.eChart_mc.eLine_mc.label_txt, "totalE", "center");
		
		if(countryCode == "en" || countryCode == undefined){
			_level0.yAxisLabel = this.simStrings.get("energyOf");
			_level0.eChart_mc.xAxis_mc.label1_txt.text = this.simStrings.get("KE");
			_level0.eChart_mc.xAxis_mc.label2_txt.text = this.simStrings.get("PEgrav");
			_level0.eChart_mc.xAxis_mc.label3_txt.text = this.simStrings.get("PEelas");
			_level0.eChart_mc.xAxis_mc.label4_txt.text = this.simStrings.get("thermal");
			_level0.eChart_mc.xAxis_mc.label5_txt.text = this.simStrings.get("total");
		}else{
			//erase English
			_level0.yAxisLabel = "";
			//_level0.eChart_mc.yAxis_mc.energyOf_txt.text = "";
			_level0.eChart_mc.xAxis_mc.label1_txt.text = "";
			_level0.eChart_mc.xAxis_mc.label2_txt.text = "";
			_level0.eChart_mc.xAxis_mc.label3_txt.text = "";
			_level0.eChart_mc.xAxis_mc.label4_txt.text = "";
			_level0.eChart_mc.xAxis_mc.label5_txt.text = "";
			this.stackString(_level0.eChart_mc.stackedText1_txt, "energyOf");
			this.stackString(_level0.eChart_mc.stackedText2_txt, "KE");
			this.stackString(_level0.eChart_mc.stackedText3_txt, "PEgrav");
			this.stackString(_level0.eChart_mc.stackedText4_txt, "PEelas");
			this.stackString(_level0.eChart_mc.stackedText5_txt, "thermal");
			this.stackString(_level0.eChart_mc.stackedText6_txt, "total");
		}
		
		this.setString(_root.body1_mc.grams_txt, "mass1Label", "center");
		this.setString(_root.body2_mc.grams_txt, "mass2Label", "center");
		this.setString(_root.body3_mc.grams_txt, "mass3Label", "center");
		this.setString(_root.body4_mc.grams_txt, "mass4Label", "center");
		this.setString(_root.body5_mc.grams_txt, "mass5Label", "center");
		this.setString(_root.body6_mc.grams_txt, "mass6Label", "center");
		this.setString(_root.body7_mc.grams_txt, "mass7Label", "center");
		
		this.setString( _level0.myRuler_mc.rulerHelp_mc.label_txt, "help1", "center");
		this.setString( _level0.myRefLine_mc.refLineHelp_mc.label_txt, "help2", "center");
		this.setString( _level0.pullHelp_mc.label_txt, "help3", "center");
		this.setString( _level0.timer_mc.timerHelp_mc.label_txt, "help4", "center");
		this.setString( _level0.timer_mc.label1_txt, "resetTimer", "center");
		this.setString( _level0.timer_mc.label2_txt, "startTimer", "center");
		//trace("_root.ruler_ch.Label after: "+_root.ruler_ch.label);

	}//end of initialize
	
	
	
	function setString(field:TextField, key:String, alignment:String){
		var stringValue:String = this.simStrings.get( key );
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
			var fontSize:Number = myComponent.getStyle("fontSize");
			var labelWidth:Number = myComponent.width;
			if(stringValue.length*fontSize > labelWidth*1.72){
				trace("label too big: "+myComponent.label);
				trace("font: "+myComponent.getStyle("fontSize"));
				var newFontSize = Math.floor(labelWidth*1.72/stringValue.length);
				trace("newFontSize: "+newFontSize);
				myComponent.setStyle("fontSize", newFontSize);
			}
			
			//trace("string length: "+stringValue.length);
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
				var subString_arr:Array = stringValue.split('newline');
				if(subString_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < subString_arr.length; i++){
						newStringValue += subString_arr[i]+newline;
					}
					stringValue = newStringValue;
				}
				//set background to cover original string
				field._parent.backgrdColorState = true;
				field._parent.backgrdColor = 0xFFFF99
				var frameNbr:Number = field._parent._currentframe;
				field._parent["textString" + frameNbr] = stringValue;
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
	
	function stackString(field:TextField, key:String){
		//trace("key: "+key);
		var stringValue:String = this.simStrings.get( key );
		var currentTextFormat:TextFormat = field.getTextFormat();
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			if(field.html){
				field.htmlText = stringValue;
			}else{
				//search for "newline" 
				var chars_arr:Array = stringValue.split("");
				if(chars_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < chars_arr.length; i++){
						newStringValue += chars_arr[i]+newline;
					}
					stringValue = newStringValue;
				}
				field.text = stringValue;
			}
			//field.setTextFormat(currentTextFormat);
			//this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of stackString()
						
}//end of class