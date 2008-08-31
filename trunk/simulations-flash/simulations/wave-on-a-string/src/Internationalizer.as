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
	_root.ruler_ch.Label = "Ruler"
	_root.timer_ch.Label = "Timer"
	_root.controlPanel_mc.helpButton_mc.label2_txt = "Hide Help"
	

	*/
	
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		//this.setString(_root.controlPanel_mc.helpButton_mc.label1_txt, "help", "center");
		//trace("internationalizer.initialize() called");

		setComponentLabel(_root.rulers_ch, "rulers" );
		setComponentLabel(_root.timer_ch, "timer" );
		this.setString(_root.myGUI_mc.oscillationASlider.label_txt, "amplitude", "center");
		this.setString(_root.myGUI_mc.oscillationfSlider.label_txt, "frequency", "center");
		this.setString(_root.myGUI_mc.dampingSlider.label_txt, "damping", "center");
		this.setString(_root.myGUI_mc.pulseWidthSlider.label_txt, "pulseWidth", "center");
		this.setString(_root.myGUI_mc.tensionSlider.label1_txt, "tension", "center");
		this.setString(_root.myGUI_mc.tensionSlider.label2_txt, "low", "center");
		this.setString(_root.myGUI_mc.tensionSlider.label3_txt, "high", "center");
		
		//this.setString(_root.myGUI_mc.helpButton_mc.label1_txt, "showHelp", "center");
		//this.setString(_root.myGUI_mc.helpButton_mc.label2_txt, "hideHelp", "center");
		_root.myGUI_mc.helpButton_mc.showHelpLabel = this.simStrings.get( "showHelp" );
		_root.myGUI_mc.helpButton_mc.hideHelpLabel = this.simStrings.get( "hideHelp" );
		
		this.setString(_root.myGUI_mc.wiggleSignFlight_mc.wiggle_mc.label_txt, "wiggleWrench", "center");
		//trace("_root.ruler_ch.Label after: "+_root.ruler_ch.label);
		
		setComponentLabel(_root.manual_rb, "manual" );
		setComponentLabel(_root.oscillate_rb, "oscillate" );
		setComponentLabel(_root.pulse_rb, "pulse" );
		_root.resetButton_mc.theLabel = this.simStrings.get( "reset" );
		_root.pulseButton_mc.theLabel = this.simStrings.get( "pulse2" );
		
		this.setString(_root.paused_txt, "paused", "center");
		
		setComponentLabel(_root.fixedBC_rb, "fixedEnd" );
		setComponentLabel(_root.looseBC_rb, "looseEnd" );
		setComponentLabel(_root.noEndBC_rb, "noEnd" );
		
		this.setString(_level0.seg0.wrench_mc.wrenchHelp_mc.label_txt, "wrenchHelp", "center");
		this.setString(_level0.myRuler_mc.rulerHelp_mc.label_txt, "rulerHelp", "center");
		this.setString(_level0.myRefLine_mc.refLineHelp_mc.label_txt, "refLineHelp", "center");
		this.setString(_level0.timer_mc.timerHelp_mc.label_txt, "timerHelp", "center");
		
		this.setString(_root.labelPause_txt, "pausePlay", "center");
		this.setString(_root.stepForward_mc.label_txt, "step", "center");
		
		this.setString(_root.timer_mc.label1_txt, "resetTimer", "center");
		this.setString(_root.timer_mc.label2_txt, "startTimer", "center");
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
				var subString_arr:Array = stringValue.split('newline');
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
						
}//end of class