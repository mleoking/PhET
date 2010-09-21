class BounceView{
	var model:Object;
	var canvas:MovieClip;	//canvas on which bounce view is drawn
	var paths:Array;		//array of step paths given by model (one path for each ball)
	var nbrBalls:Number;	//number of Balls released so far in current trail
	var delX:Number;		//horiz distance between pegs (in pixels)
	var delY:Number;		//vert distance between pegs (in pixels)
	var ballWidth:Number;	//diameter of ball in pixels
	var nbrRows:Number;		//current nbr of rows in pegboard
	var maxNbrRows:Number;	//max nbr of rows in pegboard (currently 40)
	var delT:Number;		//time to fall between rows (in seconds)
	var g:Number;			//acceleration due to gravity in pix/s^2
	var v0:Number;			//initial speed in x-direction after peg Hit
	var t:Number;			//current time in seconds, t = 0 at start of bounce
	var tLast:Number;		//time since last bounce
	var tStarts:Array;		//absolute time in msec when bounce started
	var lastRowHit:Array;	//last row hit by ball, needed to make sound
	var listOfBouncingBalls:Array;  //array of trial nbrs of balls currently bouncing
	var intervalIDs:Array;	
	var endingBounce:Boolean;	//true if bouncing ball in process of ending path
	var bounce_sound:Sound;
	var soundOn:Boolean;
	var stageW:Number;
	var stageH:Number;
	
	function BounceView(model:Object){
		this.model = model;
		this.model.registerBounceView(this);
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.canvas = _root.createEmptyMovieClip("bounceViewCanvas_mc", Util.getNextDepth());
		Util.setXYPosition(this.canvas, Util.ORIGINX, Util.ORIGINY);
		//put cover over bottom of screen to cover up bouncing balls
		_root.attachMovie("cover","coverHist_mc",_root.getNextHighestDepth());
		Util.setXYPosition(_root.coverHist_mc, Util.ORIGINX, Util.BOTTOMY);
		this.g = 1000;
		this.v0 = 0;
		this.nbrRows = this.model.nbrSteps;
		this.maxNbrRows = this.model.maxNbrRows;
		this.nbrBalls = 0;
		this.listOfBouncingBalls = new Array();
		this.paths = new Array();
		this.tStarts = new Array();
		this.lastRowHit = new Array();
		this.intervalIDs = new Array();
		this.bounce_sound = new Sound();
		this.bounce_sound.attachSound("bounceSound");
		this.soundOn = false;
	}//end of constructor
	
	function startBounce(trialNbr:Number, histIndex:Number):Void{
		this.nbrBalls = trialNbr;
		this.listOfBouncingBalls.push(trialNbr);
		//trace("startBounce:"+this.nbrBalls);
		this.canvas.attachMovie("ball", "ball"+nbrBalls, this.canvas.getNextHighestDepth());
		this.canvas["ball"+nbrBalls]._xscale = this.canvas["ball"+nbrBalls]._yscale = 100*this.maxNbrRows/this.nbrRows;

		this.ballWidth = this.canvas["ball"+nbrBalls]._height;
		Util.setXYPosition(this.canvas["ball"+nbrBalls], 0, -this.ballWidth - this.delY);
		this.paths[nbrBalls-1] = this.cloneArray(this.model.path);
		this.tStarts[nbrBalls-1] = getTimer();
		this.lastRowHit[nbrBalls - 1] = 0;  //-1 < 0, 0-row = 1st row hit
		clearInterval(this.intervalIDs[this.nbrBalls-1]);
		this.intervalIDs[this.nbrBalls-1] = setInterval(this, "evolve", 10, this.nbrBalls, histIndex);
	}
	
	function endBounce(ballNbr:Number):Void{
		clearInterval(this.intervalIDs[ballNbr - 1]);
		this.canvas["ball"+ballNbr].removeMovieClip();
		this.listOfBouncingBalls.shift();  //deletes 1 element of array = first ball dropped in current list of bouncing balls
	}
	
	function deleteBouncingBalls():Void{
		var tempList:Array = this.cloneArray(this.listOfBouncingBalls);
		for(var i:Number = 0; i < tempList.length; i++){
			this.endBounce(tempList[i]);
		}
	}
	
	function evolve(ballNbr:Number, histIndex:Number):Void{
		var d:Number;	//d = +1 or -1 = right or left direction after last peg hit
		this.t = (getTimer() - this.tStarts[ballNbr-1])/1000;
		var m: Number = Math.floor(this.t/this.delT);	//m = row last hit, rows start at m = 1
		if(this.soundOn && m > this.lastRowHit[ballNbr - 1] && m <= this.nbrRows){
			this.lastRowHit[ballNbr - 1] = m;
			this.bounce_sound.start();
		}
		var xPos:Number;	//current x-position
		var yPos:Number;	//current y-position
		this.tLast = this.t - m*this.delT;
		yPos = -1.0*this.ballWidth + (m - 1)*this.delY + 0.5*this.g*this.tLast*this.tLast;
		var path:Array = this.paths[ballNbr-1];
		if(m == 0){
			xPos = 0;
		}else if(m == 1){
			d = path[0];
			xPos = d*this.v0*this.tLast;
		}else if(m < path.length + 1){
			var n = m -1;
			d = path[n] - path[n-1];
			xPos = path[n-1]*this.delX + d*this.v0*this.tLast;
		}else if(m == path.length + 1){
			xPos = path[m-1]*this.delX;
			yPos = -1.0*this.ballWidth + (m-1)*this.delY + this.g*this.delT*this.tLast + 0.5*this.g*(this.tLast)*(this.tLast);
		}else{
			this.endBounce(ballNbr);
			this.model.bigView.addOneToHistogram(histIndex);
		}
		//trace("path.length:"+path.length+"    ballNbr:"+ballNbr+"    m:"+m+"   d:"+d+"   xPos:"+xPos);
		this.canvas["ball" + ballNbr]._x = xPos;
		this.canvas["ball" + ballNbr]._y = yPos;
		
	}//end of evolve()
	
	function setXYScale():Void{
		this.delX = this.model.bigView.horizSpace;	
		this.delY = this.model.bigView.vertSpace;
		this.nbrRows = this.model.nbrSteps;
		this.maxNbrRows = this.model.maxNbrRows;
		this.delX *= maxNbrRows/nbrRows;
		this.delY *= maxNbrRows/nbrRows;
		this.delT = Math.sqrt(2*delY/this.g);
		this.v0 = delX/delT;
		//this.g = delY*something;
	}
	
	function cloneArray(sourceArray:Array):Array{
		var N:Number = sourceArray.length;
		var targetArray:Array = new Array(N);
		for(var i:Number = 0; i < N; i++){
			targetArray[i] = sourceArray[i];
		}
		return targetArray;
	}
	

	
}//end of class