//All internationalizable string collected here
//All strings must default to english
class Internationalizer{
	private var simStrings:SimStrings;
	//private var axesView:Object;
	private var countryCode:String;		//may not be needed
	
	function Internationalizer(simStrings:SimStrings, countryCode:String){
		this.simStrings = simStrings;
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
		//this.coverStaticString(_level0.readOutPanel_mc.label4_txt, "sMeanEquals", "right");
		//_level0.GUIPanel_mc.startButton_mc.theLabel = this.simStrings.get("start");
		//_level0.GUIPanel_mc.startButton_mc.gotoAndPlay(1);
		
		this.setString(_level0.introPanel_mc.label1_txt, "estimation", "center");
		this.setString(_level0.introPanel_mc.label2_txt, "selectTrainingOrGame", "right");
		this.setString(_level0.introPanel_mc.trainingButton_mc.label_txt, "train", "center");
		this.setString(_level0.introPanel_mc.playButton_mc.label_txt, "play", "center");
		this.setString(_level0.levelPanel_mc.levelGroup.label_txt, "levelColon", "right");
		this.setString(_level0.levelPanel_mc.playButton_mc.label_txt, "play", "center");
					   
		this.setString(_level0.trainingPanel_mc.playButton_mc.label_txt, "play", "center");
		_global.level_str = this.simStrings.get("level");
		this.setString(_level0.trainingPanel_mc.label1_txt, "EnterNumber", "center");
		this.setString(_level0.trainingPanel_mc.enterButton_mc.label_txt, "enter", "center");
		this.setString(_level0.trainingPanel_mc.boundsWarning_mc.label_txt, "PleasePickANumber", "left");
		this.setString(_level0.trainingPanel_mc.rulesButton_mc.label_txt, "gameRules", "center");
		this.setString(_level0.trainingPanel_mc.strategyButton_mc.label_txt, "strategyHelp", "center");
		
		_level0.playPanel_mc.sound_mc.label_str = this.simStrings.get("sound");
		trace("sound label: "+_level0.playPanel_mc.sound_mc.sound_ch.label);
		//this.setComponentLabel(_level0.playPanel_mc.sound_mc.sound_ch, "sound");
		this.setString(_level0.playPanel_mc.label_txt, "howMany", "left");
		this.setString(_level0.playPanel_mc.answerNo_mc.label_txt, "tooFarOff", "center");
		this.setString(_level0.playPanel_mc.answerYes_mc.label_txt, "closeEnough", "center");
		this.setString(_level0.playPanel_mc.enterButton_mc.label_txt, "enter", "center");
		this.setString(_level0.playPanel_mc.nextButton_mc.label_txt, "next", "center");
		this.setString(_level0.playPanel_mc.endGameButton_mc.label_txt, "endGame", "center");
		
		this.setString(_level0.gameOver_mc.playButton_mc.label_txt, "play", "center");
		this.setString(_level0.gameOver_mc.trainButton_mc.label_txt, "train", "center");	  
		this.setString(_level0.scoreKeeper_mc.label1_txt, "score", "center");
		this.setString(_level0.scoreKeeper_mc.simpleTimer_mc.label_txt, "time", "center");
		this.setString(_level0.scoreKeeper_mc.label2_txt, "scorePerTime", "center");
		
		this.setString(_level0.gameOver_mc.label1_txt, "gameOver", "right");
		this.setString(_level0.gameOver_mc.label2_txt, "scorePerTimeColon", "right");
		this.setString(_level0.gameOver_mc.label3_txt, "numberEstimates", "right");
		this.setString(_level0.gameOver_mc.label4_txt, "percentUnderOver", "right");
		this.setString(_level0.gameOver_mc.label5_txt, "averageAccuracy", "right");
		
		this.setString(_level0.strategyContainer_mc.strategyAnimation_mc.homeButton_mc.label_txt, "home", "center");
		//this.setString(_level0.strategyContainer_mc.strategyAnimation_mc.label1_txt, "whatIsRatioOfAreas", "center");
		_level0.strategyContainer_mc.strategyAnimation_mc.label1_str = this.simStrings.get("whatIsRatioOfAreas");
		_level0.strategyContainer_mc.strategyAnimation_mc.label2_str = this.simStrings.get("ratioOfAreasIs");
		_level0.strategyContainer_mc.strategyAnimation_mc.label3_str = this.simStrings.get("whatIsRatioOfVolumes");
		_level0.strategyContainer_mc.strategyAnimation_mc.label4_str = this.simStrings.get("ratioOfVolumesIs");
		_level0.strategyContainer_mc.strategyAnimation_mc.next_str = this.simStrings.get("nextLowerCase");
		_level0.strategyContainer_mc.strategyAnimation_mc.back_str = this.simStrings.get("back");
		
		this.setString(_level0.rulesOfGame_mc.label1_txt, "enter", "left");
		this.setString(_level0.rulesOfGame_mc.label2_txt, "ratioOfLengths", "left");
		this.setString(_level0.rulesOfGame_mc.label3_txt, "levelOneColon", "left");
		this.setString(_level0.rulesOfGame_mc.label4_txt, "fullCreditIf", "left");
		this.setString(_level0.rulesOfGame_mc.label5_txt, "yourAnswerIs", "left");
		this.setString(_level0.rulesOfGame_mc.label6_txt, "noCreditIf", "left");
		this.setString(_level0.rulesOfGame_mc.label7_txt, "answerIsOutside", "left");
		this.setString(_level0.rulesOfGame_mc.label8_txt, "accuracyIsSmaller", "left");
		this.setString(_level0.rulesOfGame_mc.label9_txt, "yourAnswer", "left");
		this.setString(_level0.rulesOfGame_mc.label10_txt, "exactAnswer", "left");
		this.setString(_level0.rulesOfGame_mc.label11_txt, "or", "left");
		this.setString(_level0.rulesOfGame_mc.label12_txt, "exactAnswer", "left");
		this.setString(_level0.rulesOfGame_mc.label13_txt, "yourAnswer", "left");
		this.setString(_level0.rulesOfGame_mc.homeButton_mc.label_txt, "home", "center");
		
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