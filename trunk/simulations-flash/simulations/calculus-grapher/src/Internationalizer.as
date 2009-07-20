//All internationalizable string collected here
//All strings must default to english
//Updated from AS2 to AS3:
//newline is deprecated, replaced with "\n", I think
package{
	import flash.text.*;
	
class Internationalizer{
	private var simStrings:SimStrings;
	private var view1:Object;
	private var view2:Object;
	private var countryCode:String;
	
	function Internationalizer(simStrings:SimStrings, view1:Object, view2:Object, countryCode:String){
		this.simStrings = simStrings;
		this.view1 = view1;
		this.view2 = view2;
		this.countryCode = countryCode;
		//this.mainView = mainView;
		//trace("simStrings.getBaseName(): "+this.simStrings.getLocale());
		this.initialize();
	}//end of constructor
	
	/*
	List internationalizable strings here
	(For human readers only. All commented out.)
	
	

	*/
	
	
	private function initialize(){
		//myLabel.text = simStrings.get( "myLabel" );
		//this.setComponentLabel(_level0.rayChooser.noRays_ch, "noRays");
		//this.setString(_root.diameterSlider.label_txt, "diameter", "center");
		//this.setString(_root.panel_mc.length1Slider.label_txt, "length", "right");

		
		//this.view1.angleUnit_str = this.simStrings.get("angleUnit");
		
		if(countryCode == "en"){
			//embed fonts so they can be greyed out
			var myFormat = new TextFormat();
			myFormat.font = "Arial";
			//_root.panel_mc.length2Slider.label_txt.embedFonts = true;
			//_root.panel_mc.length2Slider.label_txt.setTextFormat(myFormat);
			//_root.panel_mc.length2Slider.unit_txt.embedFonts = true;
			//_root.panel_mc.length2Slider.unit_txt.setTextFormat(myFormat);

			//set rotated, embedded text 
			//_root.energyGraph1_mc.yAxis_txt.text = this.simStrings.get("energyOf")+this.view1.pendulum.labelNbr;
			//_root.energyGraph1_mc.kE_txt.text = this.simStrings.get("KE");
		}else{
			//erase english
			//_root.energyGraph1_mc.yAxis_txt.text = "";

			//fill in non-english
			//this.stackString(_root.energyGraph1_mc.stackedText1_txt, "energyOf" );
			//_root.energyGraph1_mc.stackedText1_txt.text += this.view1.pendulum.labelNbr;
			//this.stackString(_root.energyGraph1_mc.stackedText2_txt, "KE" );

		}
		//_level0.GUIPanel_mc.startButton_mc.theLabel = this.simStrings.get("start");
		//_level0.GUIPanel_mc.startButton_mc.gotoAndPlay(1);
		
		
		//_root.gui_mc.clearAllButton_mc.buttonLabel = this.simStrings.get("clearAll");
		//_root.gui_mc.clearAllButton_mc.gotoAndPlay(2);
		//_root.gui_mc.clearAllButton_mc.gotoAndStop(1);

		
	}//end of initialize
	
	
	
	private function setString(field:TextField, key:String, alignment:String){
		//trace("key: "+key);
		var stringValue:String = this.simStrings.get( key );
		var currentTextFormat:TextFormat = field.getTextFormat();
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			//if(field.html){    //deprecated in AS3
				//field.htmlText = stringValue;
			//}else{
				//search for "newline" 
				var subString_arr:Array = stringValue.split("\\n");
				if(subString_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < subString_arr.length; i++){
						newStringValue += subString_arr[i]+"\n";
					}
					stringValue = newStringValue;
				}
				field.text = stringValue;
			//}
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
			//if(field.html){
				//field.htmlText = stringValue;
			//}else{
				//search for "newline" 
				var subString_arr:Array = stringValue.split('\\n');
				if(subString_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < subString_arr.length; i++){
						newStringValue += subString_arr[i]+"\n"; //newline;
					}
					stringValue = newStringValue;
				}
				//set background to cover original string
				field.background = true;
				//field.border = true;	//field.parent.backgrdColorState = true;
				field.backgroundColor = 0xFFFF66;	//field.parent.backgrdColor = 0xFFFF66
				//var frameNbr:Number = field.parent._currentframe;
				//field.parent["textString" + frameNbr] = stringValue;
				field.text = stringValue;
				trace("static cover text: "+field.text);
				//trace("frameNbr: "+frameNbr+"   textString_arr: "+ field.parent["textString" + frameNbr]);
			//}
			
			this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of coverStaticString
	
	function resizeText(txtField:TextField, alignment:String):void{  //get an error when Object = textField
		//trace("name: "+txtField.name + "   multiline: "+txtField.multiline + "   wordwrap: "+txtField.wordwrap);
		var mTextField:TextField = txtField;
		var mTextFormat:TextFormat = txtField.getTextFormat();
		var alignment:String = alignment;
		//trace(mTextField.text+" has alignment"+alignment);
		//trace(mTextField.text+" has textWidth "+mTextField.textWidth+" and field.width " + mTextField.width);
		//Check that string fits inside button and reduce font size if necessary
		
		if(mTextField.textWidth + 2 >= mTextField.width) {
			trace("parent: "+mTextField.parent+"   name: "+mTextField.name + "  text resized ");
			var ratio = 1.15*mTextField.textWidth/mTextField.width;  //fudge factor of 1.15 to cover BOLDed text
			var initialHeight = mTextField.height;
			trace(mTextField.text + " too long by factor of " + ratio + "   Initial height is " + mTextField.height+ "   Initial y is "+mTextField.y);
			var oldSize:int = Number(mTextFormat.size); //TextFormat.size is type Object and must be cast to type Number
			var newSize:int = Math.round(oldSize/ratio);  
			//mTextFormat.size = newSize;
			mTextField.setTextFormat(mTextFormat);
			trace("New font size is "+ mTextField.getTextFormat().size);
			mTextField.autoSize = alignment;  //resize bounding box
			var finalHeight = mTextField.height;
			mTextField.y += (initialHeight - finalHeight)/2;  //keep text field vertically centered in button
			//trace("New height is "+ mTextField.height+ "   Final y is " + mTextField.y);
			//trace(mTextField.text+" has field.width " + mTextField.width);
		}
	}
	
	function stackString(field:TextField, key:String){
		//trace("key: "+key);
		var stringValue:String = this.simStrings.get( key );
		var currentTextFormat:TextFormat = field.getTextFormat();
		if(stringValue == "keyNotFound"  || stringValue == ""){
		   //Do nothing.  String will default to English
		}else{
			//if(field.html){
				//field.htmlText = stringValue;
			//}else{
				//search for "newline" 
				var chars_arr:Array = stringValue.split("");
				if(chars_arr.length > 1){
					var newStringValue:String = "";
					for (var i:Number = 0; i < chars_arr.length; i++){
						newStringValue += chars_arr[i]+"\n";
					}
					stringValue = newStringValue;
				}
				field.text = stringValue;
			//}
			//field.setTextFormat(currentTextFormat);
			//this.resizeText(field, alignment);
			//trace("key: "+key+"   stringValue:"+stringValue);
		}
	}//end of stackString()
						
}//end of class
}//end of package