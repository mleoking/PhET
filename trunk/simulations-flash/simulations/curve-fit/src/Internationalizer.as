//All internationalizable string collected here
//All strings must default 
class Internationalizer{
	private var simStrings:SimStrings;
	private var mainView:Object;
	
	function Internationalizer(simStrings:SimStrings, mainView:Object){
		this.simStrings = simStrings;
		this.mainView = mainView;
		//trace("simStrings.getBaseName(): "+this.simStrings.getLocale());
		this.initialize();
	}//end of constructor
	
	/*
	List internationalizable strings here
	(For human readers only. All commented out.)
	_root.controlPanel_mc.helpButton_mc.label1_txt = "Help"
	_root.controlPanel_mc.helpButton_mc.label2_txt = "Hide Help"
	
	_root.controlPanel_mc.clearButton_mc.label_txt = "Clear All"
	
	_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt
	_root.controlPanel_mc.fitTypeRadioGroup.linear_txt
	_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt
	
	_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt
	_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt
	
	_root.equation_mc.bestFit_mc
	_root.equation_mc.adjustableFit_mc
	
	_root.pointsBucket_mc.bucketLabel_txt
	_root.pointsBucket_mc.help_mc.help1_txt
	_root.pointsBucket_mc.help_mc.help2_txt
	
	_root.helpDataPoint_mc.help_txt
	
	_root.chiDisplay_mc.help_mc.help1_txt
	_root.chiDisplay_mc.help_mc.help2_txt
	_root.chiDisplay_mc.help_mc.help3_txt
	_root.chiDisplay_mc.help_mc.help4_txt
	_root.chiDisplay_mc.help_mc.help5_txt
	*/
	
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		this.setString(_root.controlPanel_mc.helpButton_mc.label1_txt, "help", "center");
		this.setString(_root.controlPanel_mc.helpButton_mc.label2_txt, "hideHelp", "center");
		//trace("hideHelpLabel: "+this.mainView.helpViewMaker.hideHelpLabel);
		
		this.setString(_root.controlPanel_mc.clearButton_mc.label_txt, "clearAll", "center");
		
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt, "noFit", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.linear_txt, "linear", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt, "quadratic", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.cubic_txt, "cubic", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.quartic_txt, "quartic", "left");
		
		this.setString(_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt, "bestFit", "left");
		this.setString(_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt, "adjustableFit", "left");
		
		this.setString(_root.equation_mc.bestFit_txt, "bestFit2", "left");
		this.setString(_root.equation_mc.adjustableFit_txt, "adjustableFit2", "left");
		
		_root.pointsBucket_mc.bucketLabel_txt.wordwrap = true;
		this.setString(_root.pointsBucket_mc.bucketLabel_txt, "bucketLabel", "center");
		this.setString(_root.pointsBucket_mc.help_mc.help1_txt, "bucketHelp1", "center");
		this.setString(_root.pointsBucket_mc.help_mc.help2_txt, "bucketHelp2", "center");
		
		_root.helpDataPoint_mc.help_txt.wordwrap = true;
		this.setString(_root.helpDataPoint_mc.help_txt, "dataPointHelp", "center");
		
		this.setString(_root.chiDisplay_mc.help_mc.help1_txt, "chiSqHelp1", "left");
		this.setString(_root.chiDisplay_mc.help_mc.help2_txt, "chiSqHelp2", "left");
		this.setString(_root.chiDisplay_mc.help_mc.help3_txt, "chiSqHelp3", "left");
		this.setString(_root.chiDisplay_mc.help_mc.help4_txt, "chiSqHelp4", "left");
		this.setString(_root.chiDisplay_mc.help_mc.help5_txt, "chiSqHelp5", "left");
	}
	
	function setString(field:TextField, key:String, alignment:String){
		var stringValue:String = this.simStrings.get( key );
		if(stringValue == "keyNotFound"){
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
	}
	
	
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
			//trace(mTextField.text + " too long by factor of " + ratio + "   Initial height is " + mTextField._height+ "   Initial y is "+mTextField._y);
			mTextFormat.size = Math.round(mTextFormat.size/ratio);  
			mTextField.setTextFormat(mTextFormat);
			//trace("New font size is "+ mTextField.getTextFormat().size);
			mTextField.autoSize = alignment;  //resize bounding box
			var finalHeight = mTextField._height;
			mTextField._y += (initialHeight - finalHeight)/2;  //keep text field vertically centered in button
			//trace("New height is "+ mTextField._height+ "   Final y is " + mTextField._y);
			//trace(mTextField.text+" has field._width " + mTextField._width);
		}
	}
						
}//end of class