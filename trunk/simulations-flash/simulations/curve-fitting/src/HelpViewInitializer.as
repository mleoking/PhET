class HelpViewInitializer{
	var helpButton:MovieClip;
	var mainView:Object;
	var showing:Boolean; //true if help is showing
	//var helpLabel:String;
	//var hideHelpLabel:String;
	
	function HelpViewInitializer(button:MovieClip, mainView:Object){
		this.helpButton = button;
		this.mainView = mainView;
		this.initializeButton();
		this.showing = false;
		//this.helpLabel = "Help";
		//this.hideHelpLabel = "Hide Help";
	}//end of constructor
	
	function initializeButton():Void{
		var thisRef:Object = this;
		this.helpButton.label1_txt._visible = true;
		this.helpButton.label2_txt._visible = false;
		
		this.helpButton.onRollOver = function(){
			var format = new TextFormat();
			format.bold = true;
			this.label1_txt.setTextFormat(format);
			this.label2_txt.setTextFormat(format);
			//thisRef.showHelp(true);
		}
		this.helpButton.onRollOut = function(){
			var format = new TextFormat();
			format.bold = false;
			this.label1_txt.setTextFormat(format);
			this.label2_txt.setTextFormat(format);
			//thisRef.showHelp(false);
		}
		this.helpButton.onPress = function(){
			thisRef.showing = !thisRef.showing;
			thisRef.showHelp(thisRef.showing);
			this._x += 3;
			this._y += 3;
			//trace(thisRef.showing);
		}
		
		this.helpButton.onRelease = function(){
			this._x -= 3;
			this._y -= 3;
			if(thisRef.showing){
				this.label1_txt._visible = false;
				this.label2_txt._visible = true;
			}else{
				this.label1_txt._visible = true;
				this.label2_txt._visible = false;
			}
		}
		this.helpButton.onReleaseOutside = this.helpButton.onRelease;
	}
	
	function showHelp(trueOrFalse:Boolean):Void{
		var tOrF:Boolean = trueOrFalse;
		this.mainView.pointsBucket.help_mc._visible = tOrF;
		_root.helpDataPoint_mc._visible = tOrF;
		if(_root.helpDataPoint_mc._visible){
			_root.helpDataPoint_mc.wiggleBars();
		}
		this.mainView.myChiDisplay.chiDisplay_mc.help_mc._visible = tOrF;
		this.mainView.equationDisplay.help_mc._visible = tOrF;
		//trace("true or false:"+tOrF);
	}
	
	
}//end of class