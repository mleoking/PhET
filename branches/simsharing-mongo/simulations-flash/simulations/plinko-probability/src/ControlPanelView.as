class ControlPanelView{
	private var model:Object;
	private var controlPanel_mc:MovieClip;
	private var stageW:Number;
	private var stageH:Number;
	public var dropState:Number;	//1 = one ball, 2 = continuous, 3 = 500 (deprecated)
	public var showState:Number;	//1 = show ball, 2 = show path, 3 = show none
	public var histState:Number;	//1 = fraction, 2 = number, 3 = autoscale
	
	function ControlPanelView(model:Object){
		this.model = model;
		this.model.registerControlPanel(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.initialize();
	}//end of constructor
	
	function initialize():Void{
		this.controlPanel_mc = _root.attachMovie("controlPanel", "controlPanel_mc",Util.getNextDepth());
		Util.setXYPosition(this.controlPanel_mc, 0.97*stageW, 0.03*stageH); 
		var modelRef = this.model;
		this.controlPanel_mc.dropBallRadioGroup.controlPanelView = this;
		this.dropState = 1;
		this.controlPanel_mc.showBallRadioGroup.controlPanelView = this;
		this.showState = 1;
		this.controlPanel_mc.showHistRadioGroup.controlPanelView = this;
		this.histState = 1;
		var controlPanelViewRef = this;
		
		this.makeButton(this.controlPanel_mc.startButton_mc, this.startButtonAction);
		
		this.makeButton(this.controlPanel_mc.stopButton_mc, this.stopButtonAction);
		
		this.makeButton(this.controlPanel_mc.resetButton_mc, this.resetButtonAction);
		
		//this.controlPanel_mc.stopButton_mc._visible = false;
		
		
		this.controlPanel_mc.helpButton_mc.onPress = function(){
			trace("help pushed");
		}
		this.controlPanel_mc.rowSlider.label_txt.text = "Rows";
		this.controlPanel_mc.rowSlider.value_txt.text = 23;
		this.controlPanel_mc.pSlider.label_txt.text = "p";
		this.controlPanel_mc.pSlider.value_txt.text = 0.50;
		
		var rSlider:MovieClip = this.controlPanel_mc.rowSlider;
		rSlider.min = 5;
		rSlider.max = 40;
		//initialization done in main fla file!
		//this.model.changeNbrRows(23);
		var modelRef:Object = this.model;
		
		//rSlider.model = this.model;
		rSlider.update = function(){
			//trace("min:"+rSlider.min+"   y:"+rSlider.puck_mc._y);
			var min = 5;//rSlider.min;
			var max = 40;//rSlider.max;
			var value = Math.round((max+min)/2 - (max - min)*(rSlider.puck_mc._y )/100);
			if(value != rSlider.value){
				rSlider.value = value;
				rSlider.value_txt.text = value;
				modelRef.changeNbrRows(value);
				//rSlider.model.changeNbrRows(value);
			}			
			updateAfterEvent();
		}
		
		rSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			//if(isNaN(value)){trace("Trouble! value is " + value);}
			if(!isNaN(value) && value <= max && value >= min){
				//trace("All OK. Value is " + value);
				modelRef.changeNbrRows(value);
				this._parent.puck_mc._y = (22.5 - value)*100/(max - min);
			}else if(!isNaN(value) && value > max){
				value = 40;
				modelRef.changeNbrRows(value);
				this._parent.puck_mc._y = (22.5 - value)*100/(max - min);
				this._parent.value_txt.text = 40;
				//trace("Not OK. Value is " + value);
			}
		}
		
		var pSlider:MovieClip = this.controlPanel_mc.pSlider;
		pSlider.min = 0.00;
		pSlider.max = 1.00;
		//pSlider.model = this.model
		
		pSlider.update = function(){
			var min = pSlider.min;
			var max = pSlider.max;
			var value = Math.round(100*((max+min)/2 - (max - min)*(pSlider.puck_mc._y )/100))/100;
			//trace("pSlider.puck_mc._y:" + pSlider.puck_mc._y);
			//pSlider.value = value;
			pSlider.value_txt.text = value;
			modelRef.setP(value);
			//pSlider.model.setP(value);
		}
		
		
		pSlider.value_txt.onChanged = function(changedField){
			var value = Number(changedField.text);
			var max = this._parent.max;
			var min = this._parent.min;
			if(value <= 1 && value >= 0){
				//All OK
			}else{
				value = 0.5
				changedField.text = value
			}
			modelRef.setP(value);
			this._parent.puck_mc._y = (0.5 - value)*100/(max - min);
		}
		
		this.controlPanel_mc.sound_cb.model = this.model;
	}//end of initialize()
	
	function setDropState(dropState:Number){
		this.dropState = dropState;
		if(dropState == 1){
			this.model.stopBallDrops();
			this.controlPanel_mc.stopButton_mc._visible = true;
		}else if(dropState == 3){ //drop 500 in a batch (this feature deprecated)
			this.model.stopBallDrops();
			this.controlPanel_mc.dropButton_mc._visible = true;
		}else if(dropState == 2){  //drop continuously
			this.controlPanel_mc.stopButton_mc._visible = true;
		}
	}
	
	function setShowState(showState:Number){
		this.showState = showState;
		if(showState == 1){
			this.model.bigView.erasePath();
		}else if(showState == 2){
			this.model.bounceView.deleteBouncingBalls();
		}else if(showState == 3){
			this.model.bigView.erasePath();
			this.model.bounceView.deleteBouncingBalls();
		}
	}
	
	function setHistogramDisplay(histState:Number){
		//trace("histStat:"+histState);
		this.model.bigView.histDisplayState = histState;
		this.model.bigView.scaleHistogramHeight();
		this.model.axesView.labelYAxis();
	}
	
	function startButtonAction(controllerRef:Object):Void{
		if(controllerRef.dropState == 1){
			controllerRef.model.dropOneBall();
		}else if(controllerRef.dropState == 3){ //this state no longer used
			controllerRef.model.dropNBalls(500);
		}else if(controllerRef.dropState == 2){
			controllerRef.model.startBallDrops();
			controllerRef.controlPanel_mc.startButton_mc._visible = false;
		}
	}//end of startButtonAction
	
	function stopButtonAction(controllerRef:Object):Void{
		controllerRef.model.stopBallDrops();
		controllerRef.controlPanel_mc.startButton_mc._visible = true;
	}
	
	function resetButtonAction(controllerRef:Object):Void{
		controllerRef.model.zeroHistogram();
	}
	
	function makeButton(target_mc:MovieClip, buttonAction:Function):Void{
		var instanceRef:Object = this;
		var clip_mc:MovieClip = target_mc;
		clip_mc.onRollOver = function(){
			var format = new TextFormat();
			format.bold = true;
			this.label_txt.setTextFormat(format);
		}
		clip_mc.onRollOut = function(){
			var format = new TextFormat();
			format.bold = false;
			this.label_txt.setTextFormat(format);
		}
		clip_mc.onPress = function(){
			this._x += 2;
			this._y += 2;
			buttonAction(instanceRef);
		}
		clip_mc.onRelease = function(){
			this._x -= 2;
			this._y -= 2;
		}
		clip_mc.onReleaseOutside = clip_mc.onRelease;
	}
	
	function getControlPanelClip():MovieClip{
		return this.controlPanel_mc;
	}
	
	function update():Void{
		
	}
}//end of class