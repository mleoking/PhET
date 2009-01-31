//Class ControlButton provides universal button behavior
//and adjusts font size of labels so that labels in translated sims
//always remains fitted and centered in button.
//Class ControlButton named to avoid collision with built-in Button Class.

class ControlButton{
	private var clip_mc:MovieClip;  //button clip usually nested in controlPanel clip
	private var label_str:String;	//label on button
	private var model:Object;  		//the model for the buttonAction function 
	private var buttonAction:Function;	//what happens when you push the button
	private var myTextFormat:TextFormat;
	
	function ControlButton(buttonClip:MovieClip, buttonLabel:String, model:Object, buttonAction:Function){
		this.clip_mc = buttonClip;
		//buttonClip must have a dynamic textfield label_txt on top of button graphic
		if(this.clip_mc.label_txt == null){
			trace("Error: Control Button Clip does not have label_txt.  Clip is " + this.clip_mc);
		}
		//var myTextField:TextField = this.clip_mc.label_txt;
		//this.myTextFormat = this.clip_mc.label_txt.getTextFormat();
		//trace("clip "+ this.clip_mc + " has font size "+ this.myTextFormat.size);
		//trace("Button initialized.  Initial text is " + myTextField.text)
		//this.label_str = buttonLabel;
		this.buttonAction = buttonAction;
		this.model = model; 
		//myTextField.text = this.label_str;
		
		//Check that string fits inside button and reduce font size if necessary
		/*
		if(myTextField.textWidth > myTextField._width) {
			var ratio = 1.15*myTextField.textWidth/myTextField._width;  //fudge factor of 1.15 to cover BOLDed text
			var initialHeight = myTextField._height;
			//trace("too long by factor of " + ratio + "   Initial height is " + myTextField._height+ "   Initial y is "+myTextField._y);
			myTextFormat.size = Math.floor(myTextFormat.size/ratio);  
			//trace("New font size is "+ myTextFormat.size);
			myTextField.setTextFormat(myTextFormat);
			myTextField.autoSize = "center";  //resize bounding box
			var finalHeight = myTextField._height;
			myTextField._y += (initialHeight - finalHeight)/2;  //keep text field vertically centered in button
			//trace("New height is "+ myTextField._height+ "   Final y is " + myTextField._y);
		}//end of if
		*/
		this.initialize();
	}//end of contructor
	
	function initialize():Void{
		var instanceRef:Object = this;
		this.clip_mc.onRollOver = function(){
			var format = new TextFormat();
			format.bold = true;
			this.label_txt.setTextFormat(format);
			//instanceRef.myTextFormat.bold = true;
			//this.label_txt.setTextFormat(instanceRef.myTextFormat);
		}
		this.clip_mc.onRollOut = function(){
			var format = new TextFormat();
			format.bold = false;
			this.label_txt.setTextFormat(format);
			//instanceRef.myTextFormat.bold = false;
			//this.label_txt.setTextFormat(instanceRef.myTextFormat);
		}
		this.clip_mc.onPress = function(){
			this._x += 2;
			this._y += 2;
		}
		this.clip_mc.onRelease = function(){
			this._x -= 2;
			this._y -= 2;
			instanceRef.buttonAction();
		}
		this.clip_mc.onReleaseOutside = this.clip_mc.onRelease;
	}//end of initialize()
	

}//end of class Button