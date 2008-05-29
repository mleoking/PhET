//Model for CurveFit Flash sim.  
//Author: Michael Dubson, project PhET, started March 2008

class Model{
	//Views
	var points_arr:Array; 	 			//array of data points
	private var nbrPoints:Number;  		//number of points on the graph
	private var fitOn:Boolean;	
	private var LFit:LinearFit;
	private var QFit:QuadraticFit;
	var fitParameters:Array;			//coefficients a, b, c of polynomial fit. If OrderOfFit = 1, ignore c
	var chiSquare:Number;				//value of reduced chi-squared
	private var orderOfFit:Number;		//1 or 2 (linear or quadratic)
	private var graphView:Object;		
	var mainView:Object;
	
	function Model(){
		this.nbrPoints = 0;
		this.points_arr = new Array();
		this.orderOfFit = 1;
		this.fitOn = true;
		//this.curveType = "best";
		//this.linearFit_arr = new Array(2);  //2 elements in array are a, b: y = a + b*x
		this.LFit = new LinearFit(this);
		this.QFit = new QuadraticFit(this);
		this.fitParameters = new Array(3);  
	}//end of constructor
	
	function addPoint(x:Number, y:Number, pointClip:MovieClip):Point{
		var arrayPosition:Number = this.nbrPoints; //add point to end of array
		this.points_arr[arrayPosition] = new Point(x, y, pointClip, this);
		this.nbrPoints+=1
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
		this.setReducedChiSquare();
		this.clearFit();
	}
	
	function setFitOn(trueOrFalse:Boolean):Void{
		this.fitOn = trueOrFalse;
	}
	
	function setOrderOfFit(fitType:Number):Void{
		this.orderOfFit = fitType;
		this.makeFit();
	}
	function updateFit(pointIndex:Number):Void{
		
	}
	
	function clearFit():Void{
		this.graphView.clearFit();
	}
	
	function setReducedChiSquare():Void{
		var a:Number = this.fitParameters[0];
		var b:Number = this.fitParameters[1];
		var c:Number = this.fitParameters[2];
		var N:Number = this.nbrPoints;
		var sum:Number = 0;
		var pointsRef_arr = this.points_arr;
		if(this.orderOfFit == 1  && N > 2){
			c = 0;
			sum = computeSum();
		}else if(this.orderOfFit == 2  && N > 3){
			sum = computeSum();
		}else{
			sum = 0;
		}
		
		function computeSum():Number{
			sum = 0;
			for(var i:Number = 0; i < N; i++){
				var yi:Number = pointsRef_arr[i].yPos;
				var xi:Number = pointsRef_arr[i].xPos;
				var sigma:Number = pointsRef_arr[i].deltaY;
				var yFit = a + b*xi + c*xi*xi;
				//trace("i:"+i+"   yi:"+yi+"   yFit:"+yFit+"   sigma"+sigma);
				//trace("term:"+(yi - yFit)/sigma);
				sum += ((yi - yFit)*(yi - yFit))/(sigma*sigma);
			}//end of for loop
			return sum;
		}//end of computeSum()
			
		this.chiSquare = sum/(N-1-this.orderOfFit);
		if(this.nbrPoints <= (orderOfFit +1)){  
			this.chiSquare = 0;
		}
		this.mainView.updateChiScaleDisplay();
	}//end of getReduceChiSquare();
	
	function getChiSquare():Number{
		return this.chiSquare;
	}
	
	function makeFit():Void{
		if (this.fitOn && this.orderOfFit == 1 && this.nbrPoints > 1) {
			this.LFit.makeFit();
			this.fitParameters[0]=this.LFit.LFit_arr[0];
			this.fitParameters[1]=this.LFit.LFit_arr[1];
			this.graphView.drawFit();
			
		}else if(this.fitOn && this.orderOfFit == 2 ){
			if(this.nbrPoints == 2){
				this.LFit.makeFit();
				this.fitParameters[0]=this.LFit.LFit_arr[0];
				this.fitParameters[1]=this.LFit.LFit_arr[1];
				this.graphView.drawFit();
			}else if(this.nbrPoints >2){
				this.QFit.makeFit();
				this.fitParameters[0]=this.QFit.QFit_arr[0];
				this.fitParameters[1]=this.QFit.QFit_arr[1];
				this.fitParameters[2]=this.QFit.QFit_arr[2];
				this.graphView.drawFit();
			}
		}else if(this.orderOfFit == 0){
			this.clearFit();
			//No fit. Do nothing.
		}
		//trace(this.fitParameters);
		this.mainView.displayFit();
		this.setReducedChiSquare();
		//trace("reducedChiSq:"+this.getReducedChiSquare());
	}//end of makeFit()
	
		
	function setA(a:Number):Void{
		this.fitParameters[0] = a;
		this.graphView.drawCurve();
		this.setReducedChiSquare();
	}
	
	function setB(b:Number):Void{
		this.fitParameters[1] = b;
		this.graphView.drawCurve();
		this.setReducedChiSquare();
	}
	
	function setC(c:Number):Void{
		this.fitParameters[2] = c;
		this.graphView.drawCurve();
		this.setReducedChiSquare();
	}
	
	function getFitAtPoint(xPos:Number):Number{
		var value:Number;
		return value;
	}
	
	function getRSquared():Number{
		var RSquared:Number = 0;
		var yMean:Number = 0; 
		var sumDeviationsOfPoints:Number = 0;
		var sumDeviationsOfFit:Number = 0;
		var N:Number = this.points_arr.length;
		for(var i = 0; i < N; i++){
			yMean += this.points_arr[i].yPos;
		}
		for(var i = 0; i < N; i++){
			var deviationPt:Number = this.points_arr[i].yPos - yMean;
			sumDeviationsOfPoints +=  deviationPt*deviationPt;
			//var deviationFit:Number = this.points_arr[i].xPos - yMean;
		}
		return RSquared;
	}
	
	function registerGraphView(view:Object):Void{
		this.graphView = view;
	}
	function registerMainView(view:Object):Void{
		this.mainView = view;
	}
	
}//end of class Model