//All internationalizable string collected here
//All strings must default to english
class Internationalizer{
	private var simStrings:SimStrings;
	private var axesView:Object;
	private var countryCode:String;		//may not be needed
	
	function Internationalizer(simStrings:SimStrings, axesView:Object, countryCode:String){
		this.simStrings = simStrings;
		this.axesView = axesView;
		this.countryCode = countryCode;
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
		this.setString(_root.controlPanel_mc.startButton_mc.label_txt, "start", "center");
		this.setString(_root.controlPanel_mc.stopButton_mc.label_txt, "stop", "center");
		
		this.setString(_level0.controlPanel_mc.dropBallRadioGroup.label1_txt, "oneBall", "left");
		this.setString(_level0.controlPanel_mc.dropBallRadioGroup.label2_txt, "continuous", "left");
		
		this.setString(_level0.controlPanel_mc.showBallRadioGroup.label1_txt, "show", "left");
		this.setString(_level0.controlPanel_mc.showBallRadioGroup.label2_txt, "ball", "left");
		this.setString(_level0.controlPanel_mc.showBallRadioGroup.label3_txt, "path", "left");
		this.setString(_level0.controlPanel_mc.showBallRadioGroup.label4_txt, "none", "left");
		
		this.setString(_root.controlPanel_mc.resetButton_mc.label_txt, "reset", "center");
		
		this.setString(_root.controlPanel_mc.rowSlider.label_txt, "rows", "center");
		this.setString(_root.controlPanel_mc.pSlider.label_txt, "p", "center");
		
		this.setString(_level0.controlPanel_mc.showHistRadioGroup.label1_txt, "histogramDisplay", "left");
		this.setString(_level0.controlPanel_mc.showHistRadioGroup.label2_txt, "fraction", "left");
		this.setString(_level0.controlPanel_mc.showHistRadioGroup.label3_txt, "number", "left");
		this.setString(_level0.controlPanel_mc.showHistRadioGroup.label4_txt, "autoScale", "left");
		
		this.setString(_level0.controlPanel_mc.sound_cb.label_txt, "sound", "left");
		
		this.setString(_level0.readOutPanel_mc.label1_txt, "NEquals", "right");
		this.coverStaticString(_level0.readOutPanel_mc.label2_txt, "xAvgEquals", "right");
		this.setString(_level0.readOutPanel_mc.label3_txt, "sEquals", "right");
		this.coverStaticString(_level0.readOutPanel_mc.label4_txt, "sMeanEquals", "right");
		
		this.axesView.fraction_str = this.simStrings.get("fraction");
		this.axesView.number_str = this.simStrings.get("number");
		//_level0.GUIPanel_mc.startButton_mc.theLabel = this.simStrings.get("start");
		//_level0.GUIPanel_mc.startButton_mc.gotoAndPlay(1);
		
		if(countryCode == "en" || countryCode == undefined){
			//embed fonts so they can be greyed out
			var myFormat = new TextFormat();
			myFormat.font = "Arial";
			//_root.panel_mc.length2Slider.label_txt.embedFonts = true;
			//_root.panel_mc.length2Slider.label_txt.setTextFormat(myFormat);
			
			//set rotated, embedded text 
			//_root.energyGraph1_mc.yAxis_txt.text = this.simStrings.get("energyOf")+this.view1.pendulum.labelNbr;
			
		}else{
			//erase english
			//_root.energyGraph1_mc.yAxis_txt.text = "";
			
			//fill in non-english
			//this.stackString(_root.energyGraph1_mc.stackedText1_txt, "energyOf" );
			//_root.energyGraph1_mc.stackedText1_txt.text += this.view1.pendulum.labelNbr;
			
			
			//this.stackString(_root.energyGraph2_mc.stackedText1_txt, "energyOf" );
			
		}
		
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
				field.backgroundColor = 0xFFCC66;	//field._parent.backgrdColor = 0xFFFF66
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