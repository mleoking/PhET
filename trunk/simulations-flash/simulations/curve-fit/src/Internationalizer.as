//All internationalizable string collected here

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
	*/
	
	function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		//trace("noFit_txt.text:" + _root.controlPanel_mc.fitTypeRadioGroup.noFit_txt.text);
		//trace("simStrings.get( noFit ) is " + this.simStrings.get( "noFit" ));
		_root.controlPanel_mc.clearButton_mc.label_txt.text = this.simStrings.get( "clearAll" );
		
		_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt.text = this.simStrings.get( "noFit" );
		_root.controlPanel_mc.fitTypeRadioGroup.linear_txt.text = this.simStrings.get( "linear" );
		_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt.text = this.simStrings.get( "quadratic" );
		
		_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt.text = this.simStrings.get( "bestFit" );
		_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt.text = this.simStrings.get( "adjustableFit" );
		
		this.reSizeText(_root.controlPanel_mc.clearButton_mc.label_txt, "center");
		
		this.reSizeText(_root.controlPanel_mc.fitTypeRadioGroup.noFit_txt, "left");
		this.reSizeText(_root.controlPanel_mc.fitTypeRadioGroup.linear_txt, "left");
		this.reSizeText(_root.controlPanel_mc.fitTypeRadioGroup.quadratic_txt, "left");
		
		this.reSizeText(_root.controlPanel_mc.fitOrNotRadioGroup.bestFit_txt, "left");
		this.reSizeText(_root.controlPanel_mc.fitOrNotRadioGroup.adjustableFit_txt, "left");
	}
	
	function reSizeText(txtField:Object, alignment:String):Void{  //get an error when Object = textField
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