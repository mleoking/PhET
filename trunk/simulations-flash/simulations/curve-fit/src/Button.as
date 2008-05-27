

class ContolButton{
	private var clip_mc:MovieClip;
	private var label_str:String;
	private var buttonAction:Function;
	private var fontSize:Number;
	private var fontColor:Number;
	private var myTextFormat:TextFormat;
	
	function Button(buttonClip:MovieClip, buttonLabel:String, buttonAction:Function){
		this.clip_mc = buttonClip;
		trace("Button initialized.  Clip is " + this.clip_mc)
		this.label_str = buttonLabel;
		this.buttonAction = buttonAction;
		this.myTextFormat = new TextFormat("Times New Roman", this.fontSize, this.fontColor);
		//this.clip_mc.createTextField("label_txt", 1, 0,0,this.clip_mc._height, this.clip_mc._width);
		
		this.clip_mc.label_txt.text = this.label_str;
		this.clip_mc.onRollOver = this.rollOverFunction;
		this.clip_mc.onRollOut = this.rollOutFunction;
		this.clip_mc.onPress = this.onPressFunction;
		this.clip_mc.onRelease = this.onReleaseFunction;
	}//end of contructor
	
	function rollOverFunction():Void{
		this.myTextFormat.bold = true;
		this.clip_mc.label_txt.setTextFormat(this.myTextFormat);
	}
	
	function rollOutFunction():Void{
		this.myTextFormat.bold = false;
		this.clip_mc.label_txt.setTextFormat(this.myTextFormat);
	}
	
	function onPressFunction():Void{
		this.clip_mc._x += 5;
		this.clip_mc._y += 5;
	}
	
	function onReleaseFunction():Void{
		this.clip_mc._x -= 5;
		this.clip_mc._y -= 5;
		this.myTextFormat.bold = false;
		this.clip_mc.label_txt.setTextFormat(this.myTextFormat);
		this.buttonAction();
	}
}//end of class Button