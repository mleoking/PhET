//Model for CurveFit Flash sim.  
//Author: Michael Dubson, project PhET, started March 2008

class Model{
	//Views
	var points_arr:Array; 	 			//array of data points
	var yOnCurve_arr:Array;				//array of y(x_i) points on curve
	private var nbrPoints:Number;  		//number of points on the graph
	private var fitOn:Boolean;			//true if best fit is on
	private var findRSqOn:Boolean;		//true is computing r^2 correlation coefficient
	private var SSerr:Number;			//used to compute r^2 correlation coefficient
	private var fitMaker:FitMaker;		//generates the best fit to the data
	var fitParameters:Array;			//coefficients a, b, c of polynomial fit. If OrderOfFit = 1, ignore c
	var chiSquare:Number;				//value of reduced chi-squared
	private var orderOfFit:Number;		//0, 1, 2, 3, 4 (nofit, linear, quadratic, cubic, quartic)
	var degOfFreedom:Number; 			//number of degrees of freedom = nbrPoints - orderOfFit;
	private var maxOrderOfFit:Number;	//highest orderOfFit ever computed (currently quartic: orderOfFit = 4)
	private var graphView:Object;		
	var mainView:Object;
	
	function Model(){
		this.nbrPoints = 0;
		this.points_arr = new Array();
		this.yOnCurve_arr = new Array();
		this.orderOfFit = 1;
		this.degOfFreedom = this.nbrPoints - this.orderOfFit - 1;
		this.maxOrderOfFit = 4;
		this.fitOn = true;
		this.findRSqOn = false;
		//this.curveType = "best";
		//this.linearFit_arr = new Array(2);  //2 elements in array are a, b: y = a + b*x
		//this.LFit = new LinearFit(this);
		//this.QFit = new QuadraticFit(this);
		this.fitMaker = new FitMaker(this);
		this.fitParameters = new Array(this.maxOrderOfFit + 1);    //maximum 
	}//end of constructor
	
	function addPoint(x:Number, y:Number, pointClip:MovieClip):Point{
		var arrayPosition:Number = this.nbrPoints; //add point to end of array
		this.points_arr[arrayPosition] = new Point(x, y, pointClip, this);
		this.nbrPoints+=1
		this.degOfFreedom = this.nbrPoints - this.orderOfFit - 1;
		this.points_arr[arrayPosition].setPositionInArray(arrayPosition);
		if(this.fitOn){
			this.makeFit();
		}
		this.setReducedChiSquare();
		return points_arr[arrayPosition];
	}
	
	function deletePoint(indx:Number):Void{
		this.points_arr[indx].clip_mc.removeMovieClip();
		this.points_arr.splice(indx,1);
		this.nbrPoints -= 1;
		this.degOfFreedom = this.nbrPoints - this.orderOfFit - 1;
		//re-index Points
		for(var i:Number = indx; i < this.nbrPoints; i++){
			this.points_arr[i].setPositionInArray(i);
		}
		if(this.fitOn){
			this.makeFit();
		}
		this.setReducedChiSquare();
	}//end of deletePoint()
	
	function deleteAllPoints():Void{
		for(var i:Number = 0; i < this.nbrPoints; i++){
			this.points_arr[i].clip_mc.removeMovieClip();
		}
		this.points_arr.length = 0;
		this.nbrPoints = 0;
		this.degOfFreedom = this.nbrPoints - this.orderOfFit - 1;
		this.fitParameters = this.fitMaker.getFit();
		this.mainView.displayFit();
		this.setReducedChiSquare();
		this.clearFit();
	}
	
	function setFitOn(trueOrFalse:Boolean):Void{
		this.fitOn = trueOrFalse;
	}
	
	function setOrderOfFit(fitType:Number):Void{
		this.orderOfFit = fitType;
		this.degOfFreedom = this.nbrPoints - this.orderOfFit - 1;
		if(fitType == 0){
			this.clearFit();
			this.setReducedChiSquare();
		}
		if(this.fitOn){
			this.makeFit();
		}else{ //if fit adjustable, set parameters
			for(var i:Number = this.orderOfFit + 1; i < this.maxOrderOfFit + 1 ; i++){
				this.fitParameters[i] = 0;
			}
			this.updateDisplays();
		}
	}
	
	function getOrderOfFit():Number{
		return this.orderOfFit;
	}
	
	function clearFit():Void{
		this.graphView.clearFit();
	}
	
	function setReducedChiSquare():Void{
		var a:Number = this.fitParameters[0];
		var b:Number = this.fitParameters[1];
		var c:Number = this.fitParameters[2];
		var d:Number = this.fitParameters[3];
		var e:Number = this.fitParameters[4];
		var N:Number = this.nbrPoints;
		var sum:Number = 0;
		var pointsRef_arr = this.points_arr;
		var yOnCurveRef_arr = this.yOnCurve_arr;
		var modelRef = this;
		this.SSerr = 0; //SSerr used to compute r^2 correlation coefficient
		if(orderOfFit == 0){
			sum = 0;
		}else if(N > this.orderOfFit +1){
			sum = computeSum();
		}else{
			sum = 0;
			//set yOnCurve_arr
			if(this.fitOn){
				for(var i:Number = 0; i < N; i++){
					yOnCurveRef_arr[i] = pointsRef_arr[i].yPos;
				}
			}else{ //if fit adjustable, update yOnCurve_arr by calling computeSum()
				sum = computeSum();
			}
		}
				
		function computeSum():Number{
			sum = 0;
			for(var i:Number = 0; i < N; i++){
				var yi:Number = pointsRef_arr[i].yPos;
				var xi:Number = pointsRef_arr[i].xPos;
				var xiSq:Number = xi*xi;
				var sigma:Number = pointsRef_arr[i].deltaY;
				var yFit = a + b*xi + c*xiSq + d*xi*xiSq + e*xiSq*xiSq ;
				this.yOnCurveRef_arr[i] = yFit;
				//trace("i: " + i + "   this.yOnCurveRef_arr[i]:" + this.yOnCurveRef_arr[i]);
				//trace("i:"+i+"   yi:"+yi+"   yFit:"+yFit+"   sigma"+sigma);
				//trace("term:"+(yi - yFit)/sigma);
				var delYiFitSq:Number = (yi - yFit)*(yi - yFit)
				sum += delYiFitSq/(sigma*sigma);
				modelRef.SSerr += delYiFitSq;
			}//end of for loop
			return sum;
		}//end of computeSum()
			
		this.chiSquare = sum/this.degOfFreedom;
		if(this.nbrPoints <= (orderOfFit +1)){  
			this.chiSquare = 0;
		}
		this.mainView.updateChiScaleDisplay();
	}//end of getReduceChiSquare();
	
	function getChiSquare():Number{
		return this.chiSquare;
	}
	
	function makeFit():Void{
		if(this.fitOn && this.nbrPoints < 2){
			this.fitParameters = this.fitMaker.getFit();
			//trace("model.makeFit called with fitOn and nbrPoints = 0  fitParameters: " +this.fitParameters  );
			this.clearFit();
		}else if(this.fitOn && this.orderOfFit > 0){
			this.fitParameters = this.fitMaker.getFit();
			//trace("this.fitParameters:"+ this.fitParameters);
			this.graphView.drawFit();
		}else if(this.orderOfFit == 0){
			this.clearFit();
			//No fit. Do nothing.
		}
		//trace(this.fitParameters);
		this.mainView.displayFit();
		this.setReducedChiSquare();
		//trace("this.getRSquared()"+this.getRSquared());
		this.graphView.updateDeviations();
		//trace("reducedChiSq:"+this.getReducedChiSquare());
	}//end of makeFit()
	
	function showDeviations(tOrF:Boolean):Void{
		//hide middle bars on errorBars
		for(var i:Number = 0; i < this.nbrPoints; i++){
			this.points_arr[i].setVerticalBarVisibility(!tOrF);
		}
		this.graphView.showDeviations = tOrF;
		this.graphView.updateDeviations();
	}//end of showDeviations
	

	
	function showRSquared(tOrF:Boolean):Void{
		//trace(tOrF);
	}
		
	function setA(a:Number):Void{
		this.fitParameters[0] = a;
		this.updateDisplays();
	}
	
	function setB(b:Number):Void{
		this.fitParameters[1] = b;
		this.updateDisplays();
	}
	
	function setC(c:Number):Void{
		this.fitParameters[2] = c;
		this.updateDisplays();
	}
	
	function setD(d:Number):Void{
		this.fitParameters[3] = d;
		this.updateDisplays();
	}
	
	function setE(e:Number):Void{
		this.fitParameters[4] = e;
		this.updateDisplays();
	}
	
	function updateDisplays():Void{
		this.mainView.displayFit();
		this.graphView.drawCurve();
		this.setReducedChiSquare();
		//trace("this.getRSquared()"+this.getRSquared());
		this.graphView.updateDeviations();
	}
	
	function getFitAtPoint(xPos:Number):Number{
		var value:Number;
		return value;
	}
	
	function getRSquared():Number{
		var rSquared:Number = 0;
		var yMean:Number = 0; 
		//var SSerr:Number = 0;
		var SStot:Number = 0;
		var N:Number = this.points_arr.length;
		for(var i = 0; i < N; i++){
			yMean += this.points_arr[i].yPos;
		}
		yMean = yMean/N;   //unweighted mean, ignores error bars
		for(var i = 0; i < N; i++){
			var devFromMean:Number = this.points_arr[i].yPos - yMean;
			SStot +=  devFromMean*devFromMean;
		}
		rSquared = 1 - this.SSerr/SStot;
		//trace("rSquared: " + rSquared);
		if( rSquared < 0  || isNaN(rSquared) ){
			rSquared = 0;
		}
		return rSquared;
	}
	
	function registerGraphView(view:Object):Void{
		this.graphView = view;
	}
	function registerMainView(view:Object):Void{
		this.mainView = view;
	}
	
}//end of class Model