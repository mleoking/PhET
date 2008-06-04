class HelpViewInitializer{
	var helpButton:MovieClip;
	var mainView:Object;
	
	function HelpViewInitializer(button:MovieClip, mainView:Object){
		this.helpButton = button;
		this.mainView = mainView;
		this.initializeButton();
	}//end of constructor
	
	function initializeButton():Void{
		var thisRef:Object = this;
		this.helpButton.onRollOver = function(){
			var format = new TextFormat();
			format.bold = true;
			this.label_txt.setTextFormat(format);
			thisRef.showHelp(true);
		}
		this.helpButton.onRollOut = function(){
			var format = new TextFormat();
			format.bold = false;
			this.label_txt.setTextFormat(format);
			thisRef.showHelp(false);
		}
	}
	
	function showHelp(trueOrFalse:Boolean):Void{
		var tOrF:Boolean = trueOrFalse;
		this.mainView.pointsBucket.help_mc._visible = tOrF;
		_root.helpDataPoint_mc._visible = tOrF;
		this.mainView.myChiDisplay.chiDisplay_mc.help_mc._visible = tOrF;
		//trace("true or false:"+tOrF);
	}
	
	
}//end of class