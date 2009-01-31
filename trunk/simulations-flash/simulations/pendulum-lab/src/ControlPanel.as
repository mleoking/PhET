class ControlPanel{
	var view_arr:Array;
	//private var activePendulum:Number;		//0 for pendulum 1 to be adjusted, 1 for pendulum 2
	private var periodPendulum:Number;		//0 for pendulum 1 to have period measured, 1 for pendulum 2
	var panelClip:MovieClip;
	var pausePlayControl:MovieClip;
	var nowPaused:Boolean;
	var savedRate:Number;					//time rate saved during pause
	var protractor:Object;
	var pausedSign:MovieClip;
	var stopWatch:MovieClip;
	var tapeMeasure:MovieClip;
	var photogate:MovieClip;
	var energyGraph:Object;
	var stageW:Number;
	var stageH:Number;
	var myKeyListener:Object;				//used for diagnostics only
	
	
	function ControlPanel(view_arr:Array){
		this.view_arr = view_arr;
		this.registerThisWithModels();
		this.panelClip = panelClip;
		this.protractor = new Protractor();
		//this.protractor.setInitAngle(1, 35);
		//this.protractor.setInitAngle(2, 45);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		//this.activePendulum = 0;
		myKeyListener = new Object();
		Key.addListener(myKeyListener);
		this.initialize();
	}//end of constuctor;
	
	function initialize():Void{
		this.panelClip = _root.attachMovie("controlPanel", "panel_mc", _root.getNextHighestDepth());
		Util.setXYPosition(this.panelClip, stageW - (this.panelClip._width/2), 0.05*stageH);
		this.pausePlayControl = _root.attachMovie("pausePlay", "pausePlay_mc", _root.getNextHighestDepth());
		Util.setXYPosition(this.pausePlayControl, Util.ORIGINX - 0.1*stageW, 0.9*stageH);
		this.pausedSign = _root.attachMovie("paused", "paused_mc",9);  //hard-coded level to get below everything else
		Util.setXYPosition(this.pausedSign, Util.ORIGINX, 0.4*stageH);
		this.pausedSign._visible = false;
		this.nowPaused = false;
		this.savedRate = _global.r;
		this.stopWatch = _root.attachMovie("stopWatch","stopWatch_mc",_root.getNextHighestDepth());
		Util.setXYPosition(this.stopWatch, 0.1*stageW, 0.6*stageH);
		this.tapeMeasure = _root.attachMovie("tapeMeasureHolder","tapeMeasure_mc",_root.getNextHighestDepth());
		Util.setXYPosition(this.tapeMeasure, 0.1*stageW, 0.4*stageH);
		this.photogate = _root.attachMovie("photogate", "photogate_mc", _root.getNextHighestDepth());
		Util.setXYPosition(this.photogate, 0.1*stageW, 0.80*stageH);
		
		//this.panelClip.lengthSlider_mc.label_txt.text = "length";
		//this.panelClip.lengthSlider_mc.unit_str = " m";
		//this.panelClip.massSlider_mc.unit_str = " kg";
		//this.panelClip.lengthSlider_mc.min = 0.5;
		//this.panelClip.lengthSlider_mc.max = 2.5;
		
		this.panelClip.length1Slider.label_txt.text = "length :";
		this.panelClip.length1Slider.min = 0.5;
		this.panelClip.length1Slider.max = 2.5;
		this.panelClip.length1Slider.unit_txt.text = "m";
		this.panelClip.mass1Slider.label_txt.text = "mass :";
		this.panelClip.mass1Slider.min = 0.1;
		this.panelClip.mass1Slider.max = 2.1;
		this.panelClip.mass1Slider.unit_txt.text = "kg";
		this.panelClip.length2Slider.label_txt.text = "length 2:";
		this.panelClip.length2Slider.unit_txt.text = "m";
		this.panelClip.length2Slider.min = 0.5;
		this.panelClip.length2Slider.max = 2.5;
		this.panelClip.mass2Slider.label_txt.text = "mass 2:";
		this.panelClip.mass2Slider.min = 0.1;
		this.panelClip.mass2Slider.max = 2.1;
		this.panelClip.mass2Slider.unit_txt.text = "kg";
		
		//this.panelClip.selectGroup_mc.controlPanelView = this;
		this.panelClip.controlPanelView = this;
		this.panelClip.pendulum2_ch.controlPanelView = this;
		this.panelClip.timeGroup_mc.controlPanelView = this;
		this.panelClip.velocity_ch.controlPanelView = this;
		this.panelClip.acceleration_ch.controlPanelView = this;
		this.panelClip.stopwatch_ch.controlPanelView = this;
		this.panelClip.tapeMeasure_ch.controlPanelView = this;
		this.panelClip.photoGate_ch.controlPanelView = this;
		this.photogate.gateRadioGroup_mc.controlPanelView = this;
		this.panelClip.energyGroup_mc.controlPanelView = this;
		this.panelClip.showTools_ch.controlPanelView = this;
		
		this.makeControlPanelDraggable();
		
		//following is useless except for user-typed input:
		//for some reason onChanged() event handler does not respond to programmatic changes
		//this.panelClip.lengthSlider_mc.value_txt.onChanged = function(tField:TextField){
			//trace(tField.text);
		//}
		
		var controllerRef:Object = this;
		this.panelClip.length1Slider.update = function(){
			var newLength = this.value;
			controllerRef.view_arr[0].pendulum.setLength(newLength);
		}
		this.panelClip.length2Slider.update = function(){
			var newLength = this.value;
			controllerRef.view_arr[1].pendulum.setLength(newLength);
		}
		this.panelClip.mass1Slider.update = function(){
			var newMass = this.value;
			controllerRef.view_arr[0].pendulum.setMass(newMass);
		}
		this.panelClip.mass2Slider.update = function(){
			var newMass = this.value;
			controllerRef.view_arr[1].pendulum.setMass(newMass);
		}
		
		/*
		this.panelClip.lengthSlider_mc.update = function(){
			var newLength = this.value;
			controllerRef.view_arr[controllerRef.activePendulum].pendulum.setLength(newLength);
		}
		
		this.panelClip.massSlider_mc.label_txt.text = "mass";
		this.panelClip.massSlider_mc.min = 0.05;
		this.panelClip.massSlider_mc.max = 2.00;
		this.panelClip.massSlider_mc.update = function(){
			var newMass = this.value;
			controllerRef.view_arr[controllerRef.activePendulum].pendulum.setMass(newMass);
		}
		*/
		this.panelClip.tapeMeasure_ch.label_txt.text = "Tape Measure";
		this.panelClip.tapeMeasure_ch.controller = this;
		this.panelClip.timer_ch.label_txt.text = "Photogate timer";
		
		//var controllerRef:Object = this;
		this.makeButton(this.panelClip.resetButton_mc, this.resetPendula);
		
		this.makeButton(this.photogate.periodButton_mc, this.startPhotogate);
		
		//pause/Play control
		this.pausePlayControl.button_mc.onRelease = function(){
			controllerRef.pausePlay();
			/*
			if(controllerRef.nowPaused){  //if paused, then play
				_global.r = controllerRef.savedRate;
				//trace(_global.r);
				this.gotoAndStop(2);
				controllerRef.nowPaused = false;
				controllerRef.pausedSign._visible = false;
			}else{  //if playing, then pause
				_global.r = 1/1000000;
				//trace(_global.r);
				this.gotoAndStop(1);
				controllerRef.nowPaused = true;
				controllerRef.pausedSign._visible = true;
			}
			if(!controllerRef.stopWatch.paused){
				controllerRef.stopWatch.unpause();
			}
			*/
		}//end onPress
		
		this.pausePlayControl.button_mc.onReleaseOutside = this.pausePlayControl.button_mc.onRelease;
		
		this.photogate.body_mc.onPress = function(){
			this._parent.startDrag(false);
			updateAfterEvent();
		}
		this.photogate.body_mc.onRelease = function(){
			this._parent.stopDrag();
		}
		this.photogate.body_mc.onReleaseOutside = this.photogate.body_mc.onRelease;
		
		//this.photogate.time_txt.backgroundColor = 0x000000;
		//this.setActivePendulum(0);
		this.resetPendula(this);   //needed to set velocity and acceleration arrows to zero
		
		myKeyListener.onKeyDown = function(){
			var pressedKeyCode:Number = Key.getCode();
			if(pressedKeyCode == Key.SPACE){
				controllerRef.view_arr[0].pendulum.dtMax = 0;
				controllerRef.view_arr[0].pendulum.dtMin = 1000;
				controllerRef.view_arr[0].pendulum.dtAvg = 0;
				controllerRef.view_arr[1].pendulum.dtMax = 0;
				controllerRef.view_arr[1].pendulum.dtMin = 1000;
				controllerRef.view_arr[1].pendulum.dtAvg = 0;
				_root.dt._visible = !_root.dt._visible;
				_root.dtMax._visible = !_root.dtMax._visible;
				_root.dtMin._visible = !_root.dtMin._visible;
				_root.dtAvg._visible = !_root.dtAvg._visible;
			}//end if(pressedKeyCode == Key.SPACE)
			if(pressedKeyCode == 80){
				//trace("pause pushed");
				controllerRef.pausePlay();
				//display angles of pendula
				var angleOneInDeg:Number = controllerRef.view_arr[0].pendulum.theta*180/Math.PI;
				var angleTwoInDeg:Number = controllerRef.view_arr[1].pendulum.theta*180/Math.PI;
				_root.angleOne_txt._visible = !_root.angleOne_txt._visible;
				_root.angleTwo_txt._visible = !_root.angleTwo_txt._visible;
				_root.angleOne_txt.text = "angle1 = " + Math.round(10*angleOneInDeg)/10 + " deg";
				_root.angleTwo_txt.text = "angle2 = " + Math.round(10*angleTwoInDeg)/10 + " deg";
				//trace("angle1: "+ angleOneInDeg);
				//trace("angle2: "+ angleTwoInDeg);
			}
		}//end myKeyListener
		
		_root.dt._visible = false;
		_root.dtMax._visible = false;
		_root.dtMin._visible = false;
		_root.dtAvg._visible = false;
		_root.angleOne_txt._visible = false;
		_root.angleTwo_txt._visible = false;
		
	}//end of initialize()
	
	function pausePlay():Void{
		if(this.nowPaused){  //if paused, then play
			_global.r = this.savedRate;
			//trace(_global.r);
			this.pausePlayControl.button_mc.gotoAndStop(2);
			this.nowPaused = false;
			this.pausedSign._visible = false;
		}else{  //if playing, then pause
			_global.r = 1/1000000;
			//trace(_global.r);
			this.pausePlayControl.button_mc.gotoAndStop(1);
			this.nowPaused = true;
			this.pausedSign._visible = true;
		}
		if(!this.stopWatch.paused){
			this.stopWatch.unpause();
		}
	}//end pausePlay()
	
	function setPeriodPendulum(nbr:Number):Void{  //number (0 or 1) of pendulum whose period is measured by photogat
		this.periodPendulum = nbr;
	}
	
	function showVelocity(tOrF:Boolean):Void{
		for(var i:Number = 0; i < this.view_arr.length; i++){
			this.view_arr[i].showVelocity(tOrF);
		}
	}//showVelocity()
	
	function showAcceleration(tOrF:Boolean):Void{
		for(var i:Number = 0; i < this.view_arr.length; i++){
			this.view_arr[i].showAcceleration(tOrF);
		}
	}//showVelocity()
	
	function resetPendula(controller:Object):Void{  //need reference to controlPanel since called from inside button
		for(var i:Number = 0; i < controller.view_arr.length; i++){
			//trace("labelNbr: "+this.view_arr[i].pendulum.getLabelNbr());
			controller.view_arr[i].pendulum.setAngleInDeg(0);
			controller.view_arr[i].pendulum.stopMotion();
			controller.view_arr[i].clearArcClip();
			var velArrow:MovieClip = controller.view_arr[i].clip.pendulum_mc.vectorHolder_mc.velArrow_mc;
			var accArrow:MovieClip = controller.view_arr[i].clip.pendulum_mc.vectorHolder_mc.accArrow_mc;
			velArrow._xscale = velArrow._yscale = 0;
			accArrow._xscale = accArrow._yscale = 0;
		}
		controller.stopPhotogate();
	}
	
	function startPhotogate(controller:Object):Void{
		controller.view_arr[controller.periodPendulum].pendulum.startPhotogate();
		controller.photogate.statusLight_mc.gotoAndStop("green");
		controller.photogate.time_txt.text = "0.000";
	}
	
	function stopPhotogate():Void{
		this.view_arr[this.periodPendulum].pendulum.stopPhotogate();
		this.photogate.statusLight_mc.gotoAndStop("red");
		this.photogate.time_txt.text = "0.000";
	}
	
	function registerThisWithModels():Void{
		for(var i:Number = 0; i < this.view_arr.length; i++){
			this.view_arr[i].pendulum.registerController(this);
		}
	}//end of registerThisWithModels()
	
	function updatePeriod():Void{
		//trace("period is "+this.view_arr[this.activePendulum].pendulum.period);
		this.photogate.statusLight_mc.gotoAndStop("red");
		var period:Number = this.view_arr[this.periodPendulum].pendulum.period;
		this.photogate.time_txt.text = (1/10000)*Math.round(10000*period) + " s";
	}
	
	function updatePhotogate(time:Number):Void{
		this.photogate.time_txt.text = 1/100*Math.round(100*time);
	}
	
	function makeControlPanelDraggable():Void{
		var border:MovieClip = this.panelClip.background_mc.border_mc;
		var thisRef:Object = this;
		border.onPress = function(){
			thisRef.panelClip.startDrag(false);
			updateAfterEvent();
		}
		border.onRelease = function(){
			thisRef.panelClip.stopDrag();
		}
		border.onReleaseOutside = border.onRelease;
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
	
	function makePauseButton():Void{
		var thisRef:Object = this;
		var button:MovieClip = this.panelClip.timeGroup_mc.pauseButton_mc;
		button.label1_txt._visible = true;
		button.label2_txt._visible = false;
		
		button.onRollOver = function(){
			var format = new TextFormat();
			format.bold = true;
			this.label1_txt.setTextFormat(format);
			this.label2_txt.setTextFormat(format);
			//thisRef.showHelp(true);
		}
		button.onRollOut = function(){
			var format = new TextFormat();
			format.bold = false;
			this.label1_txt.setTextFormat(format);
			this.label2_txt.setTextFormat(format);
			//thisRef.showHelp(false);
		}
		button.onPress = function(){
			this._x += 3;
			this._y += 3;
			//trace(thisRef.showing);
		}
		
		button.onRelease = function(){
			this._x -= 3;
			this._y -= 3;
			if(this.label1_txt._visible == true){
				this.label1_txt._visible = false;
				this.label2_txt._visible = true;
			}else{
				this.label1_txt._visible = true;
				this.label2_txt._visible = false;
			}
		}
		button.onReleaseOutside = button.onRelease;
	}//makePauseButton
	
}//end of class