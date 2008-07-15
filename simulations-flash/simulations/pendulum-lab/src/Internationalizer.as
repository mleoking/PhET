//All internationalizable string collected here
//All strings must default to english
class Internationalizer{
	private var simStrings:SimStrings;
	private var view1:Object;
	private var view2:Object;
	
	function Internationalizer(simStrings:SimStrings, view1:Object, view2:Object){
		this.simStrings = simStrings;
		this.view1 = view1;
		this.view2 = view2;
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
		//this.setComponentLabel(_level0.rayChooser.noRays_ch, "noRays");
		//this.setString(_root.diameterSlider.label_txt, "diameter", "center");
		this.setString(_root.panel_mc.length1Slider.label_txt, "length", "right");
		this.setString(_root.panel_mc.length1Slider.unit_txt, "length_unit", "left");
		this.setString(_root.panel_mc.mass1Slider.label_txt, "mass", "right");
		this.setString(_root.panel_mc.mass1Slider.unit_txt, "mass_unit", "left");
		this.setString(_root.panel_mc.length2Slider.label_txt, "length2", "right");
		this.setString(_root.panel_mc.length2Slider.unit_txt, "length_unit", "left");
		this.setString(_root.panel_mc.mass2Slider.label_txt, "mass2", "right");
		this.setString(_root.panel_mc.mass2Slider.unit_txt, "mass_unit", "left");
		
		this.setString(_root.panel_mc.frictionSlider_mc.label_txt, "friction", "center");
		this.setString(_root.panel_mc.frictionSlider_mc.leftLabel_txt, "noneFriction", "left");
		this.setString(_root.panel_mc.frictionSlider_mc.rightLabel_txt, "lots", "right");
		
		this.setString(_root.panel_mc.timeGroup_mc.label1_txt, "realTime", "left");
		this.setString(_root.panel_mc.timeGroup_mc.label2_txt, "quarterTime", "left");
		this.setString(_root.panel_mc.timeGroup_mc.label3_txt, "sixteenthTime", "left");
		this.setString(_root.panel_mc.timeGroup_mc.label4_txt, "paused", "left");
		
		this.setString(_root.panel_mc.gravityGroup_mc.label1_txt, "moon", "left");
		this.setString(_root.panel_mc.gravityGroup_mc.label2_txt, "earth", "left");
		this.setString(_root.panel_mc.gravityGroup_mc.label3_txt, "jupiter", "left");
		this.setString(_root.panel_mc.gravityGroup_mc.label4_txt, "planetX", "left");
		this.setString(_root.panel_mc.gravityGroup_mc.label5_txt, "gEqZero", "left");
		
		this.setString(_root.panel_mc.label_txt, "show", "right");
		this.setString(_root.panel_mc.velocity_ch.label_txt, "velocity", "left");
		this.setString(_root.panel_mc.acceleration_ch.label_txt, "acceleration", "left");
		
		this.setString(_root.panel_mc.energyGroup_mc.title_txt, "showEnergyOf", "left");
		this.setString(_root.panel_mc.energyGroup_mc.none_txt, "noneEnergy", "left");
		this.setString(_root.panel_mc.photoGate_ch.label_txt, "showPhotoGateTimer", "left");
		this.setString(_root.panel_mc.showTools_ch.label_txt, "otherTools", "left");
		
		this.setString(_root.panel_mc.resetButton_mc.label_txt, "reset", "center");
		
		this.setString(_root.photogate_mc.label1_txt, "photoGateTimer","center");
		this.setString(_root.photogate_mc.gateRadioGroup_mc.label_txt, "pendulum", "right");
		this.setString(_root.photogate_mc.label2_txt, "period","right");
		this.setString(_root.photogate_mc.periodButton_mc.label_txt, "start", "center");
		
		this.setString(_root.stopWatch_mc.label1_txt, "resetStopwatch", "center");
		this.setString(_root.stopWatch_mc.label2_txt, "startPauseStopwatch", "center");
		
		this.view1.angleUnit_str = this.simStrings.get("angleUnit");
		this.view2.angleUnit_str = this.simStrings.get("angleUnit");
		
		//_level0.GUIPanel_mc.startButton_mc.theLabel = this.simStrings.get("start");
		//_level0.GUIPanel_mc.startButton_mc.gotoAndPlay(1);
		
		
		//_root.gui_mc.clearAllButton_mc.buttonLabel = this.simStrings.get("clearAll");
		//_root.gui_mc.clearAllButton_mc.gotoAndPlay(2);
		//_root.gui_mc.clearAllButton_mc.gotoAndStop(1);

		
	}//end of initialize
	
	
	
	function setString(field:TextField, key:String, alignment:String){
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
			trace("parent: "+mTextField._parent+"   name: "+mTextField._name + "  text resized ");
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