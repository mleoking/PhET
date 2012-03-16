class Model{
	var nbrSteps:Number;	//number of steps in random walk = number of rows in pegboard
	var maxNbrRows:Number;	//max number of rows in pegboard = max nbr steps in random walk
	var p:Number;			//probability of success
	var trialNbr:Number;	//nbr of current trial (current ball drop)
	var mean:Number;		//mean of the sample (near zero for p = 0.5)
	var trueMean:Number;	//mean of the distribution (= n*p, n = nbrRows)
	var sigma:Number;		//standard deviation of distribution (sigma = root(n*p*(1-p))
	var variance:Number;	//sigma-squared
	var sumSquares:Number;	//sum of squares of trials, used to compute sigma
	var stdev:Number;		//standard deviation
	var stdev_mean:Number;	//standard deviation of the mean
	var histogram:Array;	//current histogram of ball positions
	var path:Array;			//path of ball thru pegboard
	var position:Number;	//current position of ball, in Galton Board coordinates
	var bigView:Object;			//view of pegboard, distribution, and histogram
	var controlPanelView:Object;
	var readOutView:Object;
	var bounceView:Object;
	var axesView:Object;
	var intervalID:Number;
	
	function Model(nbrSteps){
		this.nbrSteps = nbrSteps;
		this.maxNbrRows = this.nbrSteps;
		this.trialNbr = 0;
		this.mean = 0;	
		this.histogram = new Array(this.nbrSteps + 1);  //
		this.path = new Array(this.nbrSteps);
		//this.distribution = new Array(this.nbrSteps + 1);  //
		this.zeroHistogram();
	}//end of constructor
	
	function registerBigView(view:Object){
		this.bigView = view;
	}
	
	function registerControlPanel(view:Object){
		this.controlPanelView = view;
	}
	
	function registerReadOutView(view:Object){
		this.readOutView = view;
	}
	
	function registerBounceView(view:Object){
		this.bounceView = view;
	}
	
	function registerAxesView(view:Object){
		this.axesView = view;
	}

	function zeroHistogram():Void{
		for(var i:Number = 0; i < this.histogram.length; i++){
			this.histogram[i] = 0;
		}
		this.trialNbr = 0;
		this.mean = 0;
		this.sumSquares = 0;
		this.stdev = 0;
		this.stdev_mean = 0;
		this.variance = 0;
		this.bigView.zeroHistogram();
		this.bigView.erasePath();
		this.bounceView.deleteBouncingBalls();
		this.readOutView.update();
		this.readOutView.readOutPanel.mean_txt.text = "?";
		this.axesView.updateIndicators();
	}
	
	//drop a ball thru the pegBoard, then update histogram
	function dropOneBall():Void{
		this.position = 0;  //initial position of ball
		for (var step:Number = 0; step < nbrSteps; step++){
			this.position = this.position + this.nextStep();
			this.path[step]= this.position;
		}
		var histIndex:Number = (this.nbrSteps + this.position)/2;
		this.histogram[histIndex]++;
		this.trialNbr++;
		var N = this.trialNbr;
		this.mean = (1/N)*((N-1)*this.mean + histIndex);
		var xNsqrd:Number = histIndex*histIndex;
		this.variance = (1/(N-1))*((sumSquares + xNsqrd) - N*mean*mean);
		this.sumSquares += xNsqrd;
		this.stdev = Math.sqrt(variance);
		this.stdev_mean = this.stdev/Math.sqrt(this.trialNbr);
		this.readOutView.update();
		if(this.controlPanelView.showState == 1){
			this.bounceView.startBounce(this.trialNbr, histIndex);
		}else if (this.controlPanelView.showState == 2){
			this.bigView.drawPath();
			this.bigView.addOneToHistogram(histIndex);
			//this.bigView.updateHistogram(histIndex);
		}else {
			this.bigView.addOneToHistogram(histIndex);
			//this.bigView.updateHistogram(histIndex);
		}
		
		
	}//end of dropOneBall()
	
	function dropNBalls(N:Number):Void{
		for(var i:Number = 1; i <= N; i++){
			this.dropOneBall();
		}
	}
	
	function startBallDrops():Void{
		clearInterval(this.intervalID);
		this.intervalID = setInterval(this, "dropOneBall", 5);
		//trace("startBallDrops");
	}
	
	function stopBallDrops():Void{
		clearInterval(this.intervalID);
	}
	
	
	function changeNbrRows(nbrRows:Number):Void{
		this.nbrSteps = nbrRows;
		this.path = new Array(this.nbrSteps);
		this.histogram = new Array(this.nbrSteps + 1);  //
		//this.distribution = new Array(this.nbrSteps + 1);  //
		//this.trueMean = this.p*this.nbrSteps;
		//this.readOutView.update();
		this.zeroHistogram();
		this.bigView.changeNbrRows();
		this.bounceView.setXYScale();
		this.axesView.drawAxes();
		this.updateIndicators();
	}
	

	function setP(probSuccess:Number):Void{
		this.p = probSuccess;
		this.bigView.rotatePegs((this.p - 0.5)*90);
		this.zeroHistogram();
		this.bigView.drawDistribution();
		//this.trueMean = this.p*this.nbrSteps;
		//this.sigma = Math.sqrt(this.nbrSteps*this.p*(1-this.p));
		//this.readOutView.update();
		//this.axesView.updateIndicators();
		this.updateIndicators();
	}
	
	function updateIndicators():Void{
		this.trueMean = this.p*this.nbrSteps;
		this.sigma = Math.sqrt(this.nbrSteps*this.p*(1-this.p));
		this.readOutView.update();
		this.axesView.updateIndicators();
	}
	
	//choose a random step direction: +1 with prob p or -1 with prob q = 1-p
	function nextStep():Number{
		var step:Number;
		var randNbr = Math.random();
		if(randNbr < this.p){
			step = 1;
		}else{
			step = -1;
		}
		//var step = 2*(Math.floor(2*Math.random()) - 1/2);  //step is +1 or -1 with equal probability
		return step;
	}
}//end of class