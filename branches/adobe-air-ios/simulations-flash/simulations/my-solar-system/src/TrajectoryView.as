class TrajectoryView{
	private var model:Object;
	private var isVisible:Boolean;  //trajectory view visible or not
	private var viewHolder_mc:MovieClip;
	private var explosion_mc:MovieClip;
	//private var controller:Object;
	private var stageW:Number;
	private var stageH:Number;
	//private var colors:Array;
	private var lineWidth:Number;
	private var tracesOn:Boolean;
	private var originScreenX:Number;
	private var originScreenY:Number;
	
	function TrajectoryView(model:Object){
		this.model = model;
		//this.controller = controller;
		this.model.registerView(this);
		this.model.registerTrajectoryView(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.originScreenX = Util.ORIGINSCREENX;
		this.originScreenY = Util.ORIGINSCREENY;
		this.lineWidth = 2.0;  //linewidth of orbit trace
		
		_root.createEmptyMovieClip("trajectoryHolder_mc",Util.getNextDepth());
		this.drawBackgroundGrid();
		this.setGridVisibility(false);
		this.viewHolder_mc = _root.trajectoryHolder_mc;
		this.setVisibility(true);
		this.tracesOn = true;
		this.initialize();
	}//end of constructor
	
	function initialize():Void{
		this.clearBodies();
		var thisModel:Object = this.model;
		var thisHereView:TrajectoryView = this;
		for(var j:Number = 1; j <= this.model.getN(); j++){
			_root.trajectoryHolder_mc.createEmptyMovieClip("orbit"+j, _root.trajectoryHolder_mc.getNextHighestDepth());
			_root.trajectoryHolder_mc.attachMovie("currentBody","body"+j,_root.trajectoryHolder_mc.getNextHighestDepth());
			var clip:MovieClip = _root.trajectoryHolder_mc["body"+j];
			var drawing:MovieClip = _root.trajectoryHolder_mc["orbit"+j];
			var i:Number = j - 1;
			var mass = this.model.getMassOfBodyI(i);
			this.setMassOfClip(clip, mass);
			var bodyColor:Color = new Color(clip.disk_mc);
			bodyColor.setRGB(Util.BODYCOLORS[i]);
			var initPos:Vector = this.model.getInitPosOfBodyI(i);
			clip._x = initPos.x + this.originScreenX;
			clip._y = -initPos.y + this.originScreenY;  //screen coords of clip: y increasing downward
			drawing.clear();
			drawing.lineStyle(this.lineWidth,Util.TRACECOLORS[i],100); 
			drawing.moveTo(clip._x, clip._y);
			clip.onRollOver = function(){
				var bodyNbr:Number = Number(this._name.charAt(this._name.length - 1));
				var mass:Number = thisModel.bodies[bodyNbr - 1].mass;
				var xPos:Number = thisModel.bodies[bodyNbr - 1].pos.x;
				var yPos:Number = thisModel.bodies[bodyNbr - 1].pos.y;
				var xVel:Number = thisModel.bodies[bodyNbr - 1].vel.x;
				var yVel:Number = thisModel.bodies[bodyNbr - 1].vel.y;
				//trace("xPos:" + xPos + "   yPos:" + yPos + "    xVel:"+xVel + "    yVel:"+yVel);
				var window:MovieClip = _root.trajectoryHolder_mc.specWindow_mc;
				window.mass_txt.text = mass;
				window.x_txt.text = Math.round(10*xPos)/10;
				window.y_txt.text = Math.round(10*yPos)/10;
				window.vx_txt.text = Math.round(10*xVel)/10;
				window.vy_txt.text = Math.round(10*yVel)/10;
				window._visible = true;
				//window._x = thisHereView.originScreenX + xPos;
				//window._y = thisHereView.originScreenY - yPos;
				
			}
			clip.onRollOut = function(){
				var window:MovieClip = _root.trajectoryHolder_mc.specWindow_mc;
				window._visible = false;
			}
		}
		_root.trajectoryHolder_mc.attachMovie("explosion", "explosion_mc", _root.trajectoryHolder_mc.getNextHighestDepth());
		_root.trajectoryHolder_mc.attachMovie("specWindow", "specWindow_mc", _root.trajectoryHolder_mc.getNextHighestDepth());
		var window:MovieClip = _root.trajectoryHolder_mc.specWindow_mc;
		window._visible = false;
		window._x = 0.70*stageW;
		window._y = 0.98*stageH;
		this.explosion_mc = _root.trajectoryHolder_mc.explosion_mc;
		this.explosion_mc.gotoAndStop("finish");
	}//end of initialize()
	
	function drawBackgroundGrid():Void{
		_root.createEmptyMovieClip("grid_mc",Util.getNextDepth());
		var canvas = _root.grid_mc;
		var nbrYLines = 8; //grid set for 800x600 window
		var nbrXLines = 6;
		var majorSpace = stageW/nbrYLines;
		var nbrMinorLines = 4;
		var minorSpace = majorSpace/nbrMinorLines;
		var OriginX:Number = Util.ORIGINSCREENX;
		var OriginY:Number = Util.ORIGINSCREENY;
		var colorNumber:Number = 0x555588;
		//draw major grid lines
		canvas.lineStyle(1.0, colorNumber);
		//for (var y = 0; y <= nbrXLines; y++){
		for(var y:Number = -nbrXLines/2 - 1 ; y <= nbrXLines/2 + 1; y++){
			canvas.moveTo(-majorSpace,OriginY + y*majorSpace);
			canvas.lineTo(stageW + majorSpace,OriginY + y*majorSpace);
		}
		for (var x = -1; x <= nbrYLines +1 ; x++){
			canvas.moveTo(x*majorSpace, -1.6*majorSpace);
			canvas.lineTo(x*majorSpace,stageH + 1.6*majorSpace);
		}
		//draw minor grid lines
		/*
		canvas.lineStyle(1, colorNumber);
		for (var y = 0; y < nbrMinorLines*nbrXLines; y++){
			canvas.moveTo(0,y*minorSpace);
			canvas.lineTo(stageW,y*minorSpace);
		}
		for (var x = 0; x < nbrMinorLines*nbrYLines; x++){
			canvas.moveTo(x*minorSpace,0);
			canvas.lineTo(x*minorSpace,stageH);
		}
		*/
		//canvas.attachMovie("scaleRuler","scaleRuler_mc",Util.getNextDepth());
		//this.positionClip(canvas.scaleRuler_mc,(1/6)*stageW,(17/18)*stageH);
		//canvas.scaleRuler_mc._visible = false;
	 }//end of drawBackgroundGrid()

	function setGridVisibility(trueOrFalse:Boolean):Void{
		_root.grid_mc._visible = trueOrFalse;
	}

	function setVisibility(trueOrFalse:Boolean):Void{
		this.viewHolder_mc._visible = trueOrFalse;
		this.isVisible = trueOrFalse;
	}
	
	
	function clearBodies():Void{
		for(var clip:String in _root.trajectoryHolder_mc){
			_root.trajectoryHolder_mc[clip].removeMovieClip();
		}
	}
	
	function clearTraces():Void{
		this.tracesOn = false;
		for(var j:Number = 1; j <= this.model.getN(); j++){
			this.viewHolder_mc["orbit"+j].clear();
		}
	}
	
	function resetTraces():Void{
		this.tracesOn = true;
		for(var j:Number = 1; j <= this.model.getN(); j++){
			this.viewHolder_mc["orbit"+j].clear();
			this.viewHolder_mc["orbit"+j].lineStyle(this.lineWidth, Util.TRACECOLORS[j - 1],100); 
			var initPos:Vector = this.model.getPosOfBodyI(j - 1);
			var screenX = initPos.x + this.originScreenX;
			var screenY = -initPos.y + this.originScreenY;
			this.viewHolder_mc["orbit"+j].moveTo(screenX, screenY);
		}
	}
	
	function setMassOfClip(clip:MovieClip, mass:Number):Void{
		clip._width = clip._height = 2.5*Math.pow(mass,1/3) + 6;
	}
	
	function hideBody(indexToHide){
		var bodyNumber:Number = indexToHide + 1;
		_root.trajectoryHolder_mc["body" + bodyNumber]._visible = false;
		_root.trajectoryHolder_mc["orbit" + bodyNumber].moveTo(10000, 0);
	}
	
	function showExplosion(position:Vector):Void{
		this.explosion_mc._x = position.x + this.originScreenX;
		this.explosion_mc._y = -position.y + this.originScreenY;
		this.explosion_mc.gotoAndPlay("start");
	}
	
	function showAllBodies():Void{
		for(var bodyNumber:Number = 1; bodyNumber <= this.model.getN(); bodyNumber++){
			_root.trajectoryHolder_mc["body" + bodyNumber]._visible = true;
			_root.trajectoryHolder_mc["orbit" + bodyNumber]._visible = true;
		}
	}
	
	function update():Void{
		//trace("Trajectory View update called");
		if(this.model.resettingN){
			this.initialize();
		}
		/*
		if(this.model.integrationOn){
			_root.trajectoryHolder_mc._visible = true;
			this.isVisible = true;
		}else{
			_root.trajectoryHolder_mc._visible = false;
			this.isVisible = false;
		}
		*/
		for(var j:Number = 1; j <= this.model.getN(); j++){
			var clip:MovieClip = _root.trajectoryHolder_mc["body"+j];
			if(!this.model.integrationOn || this.model.collisionJustOccurred){
				var mass = this.model.getMassOfBodyI(j - 1);
				this.setMassOfClip(clip, mass);
			}
			var pos:Vector = this.model.getPosOfBodyI(j-1)
			clip._x = pos.x + this.originScreenX;
			clip._y = -pos.y + this.originScreenY;  //screen coords of clip: y increasing downward
			var drawing:MovieClip = _root.trajectoryHolder_mc["orbit"+j];
			if(this.tracesOn&&this.model.integrationOn){
				drawing.lineTo(clip._x, clip._y);
			}else{
				drawing.clear();
				drawing.lineStyle(this.lineWidth, Util.TRACECOLORS[j - 1],100); 
				drawing.moveTo(clip._x, clip._y);
			}
		}

	}//end of update();
}//end of class