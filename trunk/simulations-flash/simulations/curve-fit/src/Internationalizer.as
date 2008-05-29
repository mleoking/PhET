//All internationalizable string collected here
//All strings must default 
class Internationalizer{
	private var simStrings:SimStrings;
	
	function Internationalizer(simStrings:SimStrings){
		this.simStrings = simStrings;
		//trace("simStrings.getBaseName(): "+this.simStrings.getLocale());
		this.initialize();
	}//end of constructor
	
	/*
	List internationalizable strings here
	(For human readers only. All commented out.)
	
	_root.controlPanel_mc.clearButton_mc.label_txt = "Clear All"
	
	_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt
	_root.controlPanel_mc.fitTypeRadioGroup.linear_txt
	_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt
	
	_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt
	_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt
	
	_root.equation_mc.bestFit_mc
	*/
	
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		this.setString(_root.controlPanel_mc.clearButton_mc.label_txt, "clearAll", "center");
		
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt, "noFit", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.linear_txt, "linear", "left");
		this.setString(_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt, "quadratic", "left");
		
		this.setString(_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt, "bestFit", "left");
		this.setString(_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt, "adjustableFit", "left");
		
		this.setString(_root.equation_mc.bestFit_txt, "bestFit2", "left");
	}
	
	function setString(field:TextField, key:String, alignment:String){
		var stringValue:String = this.simStrings.get( key );
		if(stringValue == "keyNotFound"){
		   //Do nothing.  String will default to English
		}else{
			field.text = stringValue;
			this.resizeText(field, alignment);
		}
	}
	
	function resizeText(txtField:Object, alignment:String):Void{  //get an error when Object = textField
		var mTextField:Object = txtField;
		var mTextFormat:TextFormat = txtField.getTextFormat();
		var alignment:String = alignment;
		//trace(mTextField.text+" has alignment"+alignment);
		//trace(mTextField.text+" has textWidth "+mTextField.textWidth+" and field._width " + mTextField._width);
		//Check that string fits inside button and reduce font size if necessary
		if(mTextField.textWidth + 2 >= mTextField._width) {
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