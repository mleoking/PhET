//View.  The Galton Board consists of a triangular lattice of pegs

class BigView{
	var model:Object;
	var maxNbrRows:Number;	//max nbr rows on Galton board
	var nbrRows:Number;		//displayed nbr of rows on Galton board
	var vertSpace:Number;	//vertical spacing between rows in pixels
	var horizSpace:Number;	//2*horizontal spacing between pegs in pixels
	var stageH:Number;		//height of stage in pixels
	var stageW:Number;		//width of stage in pixels
	var pegBoardCanvas:MovieClip; 	//movieclip on which pegboard is drawn
	var pathCanvas:MovieClip;
	//var pathCanvas:Array;		//array of canvases for path of dropped ball
	var nbrPaths:Number;		//nbr of paths shown = length of pathCanvas array
	var currentPathNbr:Number;
	var histogramCanvas:MovieClip;	//movieclip on which histogram is drawn
	var histN:Number;			//numbers of balls in histogram
	var histDisplayState:Number; //1 = display fraction, 2 = number, 3 = autoScale
	var histMean:Number;		//mean of histogram
	var histStdev:Number;
	var histSumSquares:Number;
	var histVariance:Number;
	//var bouncingBallCanvas:MovieClip;
	//var histMagFactor:Number;		//integer-to-pixel height conversion factor
	var maxHistHeight:Number;		//max height of histogram in pixels
	var tallestBin:Number;
	//var scaleHeightFactor:Number;	//fraction to pixel height coversion factor
	var fracToPixConversion:Number;		//fraction to pixel conversion factor, used to scale distriibution and histogram image
	var nbrToPixConversion:Number;		//number to pixel conversion factor, used to scale histogram image
	var distributionCanvas:MovieClip;	//movieclip on which distribution is drawn
	var distribution:Array;			//holds binomial distribution
	var maxInDistribution:Number;	//largest number in distribution
	var maxHistHeightArray:Array;	//used when auto-scaling histogram
	var histHeightArrayIndex:Number;
	//var histogram:Array;
	
	//constructor
	function BigView(model:Object){
		this.model = model;
		this.model.registerBigView(this);
		this.nbrRows = this.model.nbrSteps;
		this.maxNbrRows = this.nbrRows;
		this.stageH = Util.STAGEH;
		this.stageW = Util.STAGEW;
		this.vertSpace = 0.5*this.stageH/this.nbrRows;
		this.horizSpace = vertSpace;
		//trace("this.vertSpace:"+this.vertSpace+"    this.horizSpace:"+this.horizSpace);
		this.maxHistHeight = 180;
		this.histDisplayState = 3;
		//this.histMagFactor = this.maxHistHeight/20;
		
		this.histN = 0;
		this.histMean = 0;
		this.histStdev = 0;
		this.histVariance = 0;
		this.histSumSquares = 0;
		this.maxHistHeightArray = [20,50,100,200,500,1000,2000,5000,10000, 20000,50000,100000];
		this.histHeightArrayIndex = 0;
		this.nbrToPixConversion = this.maxHistHeight/this.maxHistHeightArray[0];
		//pegBoardCanvas mc has registration point at top center, rather than top left
		this.pegBoardCanvas = _root.attachMovie("pegBoardCanvas","pegBoard_mc", Util.getNextDepth());
		Util.setXYPosition(this.pegBoardCanvas, Util.ORIGINX, Util.ORIGINY);
		this.drawPegBoard();  
		
		_root.attachMovie("histogramBackground", "histogramBackground_mc", Util.getNextDepth());
		Util.setXYPosition(_root.histogramBackground_mc, Util.ORIGINX, Util.BOTTOMY);
		_root.histogramBackground_mc._height = this.maxHistHeight;
		
		//this.histogramCanvas = _root.attachMovie("histogramCanvas","histogramCanvas_mc", Util.getNextDepth());
		this.histogramCanvas = _root.createEmptyMovieClip("histogramCanvas_mc",Util.getNextDepth());
		Util.setXYPosition(this.histogramCanvas, Util.ORIGINX, Util.BOTTOMY);
		
		
		this.distributionCanvas = _root.attachMovie("distributionCanvas","distributionCanvas_mc", Util.getNextDepth());
		Util.setXYPosition(this.distributionCanvas, Util.ORIGINX, Util.BOTTOMY);
		
		this.nbrPaths = 10;
		this.currentPathNbr = 0;
		//this.pathCanvas = new Array(this.nbrPaths);
		//for(var i:Number = this.nbrPaths - 1; i >= 0; i--){
			//this.pathCanvas[i] = _root.attachMovie("pathCanvas","pathCanvas"+i, Util.getNextDepth());
			//Util.setXYPosition(this.pathCanvas[i], Util.ORIGINX, Util.ORIGINY);
		//}
		this.pathCanvas = _root.attachMovie("pathCanvas","pathCanvas", Util.getNextDepth());
		Util.setXYPosition(this.pathCanvas, Util.ORIGINX, Util.ORIGINY);
		
			
		_root.attachMovie("phetLogo","phetLogo_mc",Util.getNextDepth());
		Util.setXYPosition(_root.phetLogo_mc, 0.97*stageW, 0.97*stageH);
		//this.bouncingBallCanvas = _root.createEmptyMovieClip("bouncingBallCanvas_mc", Util.getNextDepth());
		//Util.setXYPosition(this.bouncingBallCanvas, this.stageW/2, 0.15*stageH);
		this.distribution = new Array(this.nbrRows + 1);
		for(var m:Number = 0; m <= this.maxNbrRows; m++){
			this.distributionCanvas.attachMovie("bar","bar"+m,this.distributionCanvas.getNextHighestDepth());
			this.distributionCanvas["bar"+m]._width = 2*this.horizSpace;
			this.distributionCanvas["bar"+m]._height = 10;
		}
		//this.drawDistribution();
		for(var m:Number = 0; m <= this.maxNbrRows; m++){
			this.histogramCanvas.attachMovie("histBar","bar"+m,this.histogramCanvas.getNextHighestDepth());
			this.histogramCanvas["bar"+m]._width = 2*this.horizSpace;
			this.histogramCanvas["bar"+m]._height = 0;
		}
		//this.histogram = new Array(this.nbrRows + 1);
		
	}//end of constructor
	
	function drawPegBoard():Void{
		for(var i:Number = 0; i < this.maxNbrRows; i++){
			for(var j:Number = -i; j <= i; j += 2){
				this.pegBoardCanvas.attachMovie("peg", "peg"+i+j, this.pegBoardCanvas.getNextHighestDepth());
				Util.setXYPosition(this.pegBoardCanvas["peg"+i+j], j*this.horizSpace, i*this.vertSpace);
			}//end of j loop
		}//end of for i loop
		
		//add cover, used to cover up bottom rows when displaying fewer rows
		this.pegBoardCanvas.attachMovie("cover", "cover_mc", this.pegBoardCanvas.getNextHighestDepth());
		Util.setXYPosition(this.pegBoardCanvas.cover_mc, 0,(this.maxNbrRows - 0.5)*vertSpace);
	}//end of drawPegBoard()
	
	function rotatePegs(angle:Number){  //p is probability of right step (success) 
		for(var i:Number = 0; i < this.maxNbrRows; i++){
			for(var j:Number = -i; j <= i; j += 2){
				this.pegBoardCanvas["peg"+i+j].peg_mc._rotation = angle;
			}//end of j loop
		}//end of for i loop
	}
	
	function zeroHistogram():Void{
		for(var m:Number = 0; m <= this.maxNbrRows; m++){
			this.histogramCanvas["bar"+m]._height = 0;
		}
		this.histogramCanvas._yscale = 100;
		this.histN = 0;
		this.histMean = 0;
		this.histSumSquares = 0;
		this.histStdev = NaN;
		this.model.axesView.updateIndicators();
		this.histHeightArrayIndex = 0;
		this.tallestBin = 0;
		this.nbrToPixConversion = this.maxHistHeight/this.maxHistHeightArray[0];
		this.scaleHistogramHeight();
	}
	

	function updateHistogram(column:Number):Void{
		this.histogramCanvas["bar"+column]._height = this.model.histogram[column];
		this.model.axesView.updateIndicators();
		this.model.readOutView.update();
		if(this.histogramCanvas._height > this.maxHistHeight){ //rescale if necessary
			this.histogramCanvas._yscale *= 0.5;
		}
	}
	
	function addOneToHistogram(column:Number):Void{
		this.histogramCanvas["bar"+column]._height += 1;
		if(this.histogramCanvas["bar"+column]._height > this.tallestBin){
			this.tallestBin = this.histogramCanvas["bar"+column]._height;
			//trace("this.tallestBin:" + this.tallestBin);
		}
		//trace("this.histogramCanvas._height:"+this.histogramCanvas._height);
		this.histN++;
		var N = this.histN;
		this.histMean = (1/N)*((N-1)*this.histMean + column);
		var xNsqrd:Number = column*column;
		this.histVariance = (1/(N-1))*((histSumSquares + xNsqrd) - N*histMean*histMean);
		this.histSumSquares += xNsqrd;
		this.histStdev = Math.sqrt(histVariance);
		this.model.axesView.updateIndicators();
		this.model.readOutView.update();
		if(this.tallestBin > this.maxHistHeightArray[this.histHeightArrayIndex]){
			this.histHeightArrayIndex += 1;
			var i = this.histHeightArrayIndex;
			this.nbrToPixConversion = this.maxHistHeight/this.maxHistHeightArray[i];
			this.histogramCanvas._yscale = 100*this.nbrToPixConversion;
			this.model.axesView.writeYAxisNumbers();
		}
		//this.scaleHistogramHeight();
		if(this.histDisplayState == 1){//display fraction
			this.histogramCanvas._yscale = 100*this.fracToPixConversion/this.histN;
		}
	}//end of addOneToHistogram()
	
	function scaleHistogramHeight():Void{
		//this.histogramCanvas._yscale = 100;
		if(this.histDisplayState == 1){//display fraction
			this.histogramCanvas._yscale = 100*this.fracToPixConversion/this.histN;
		}else if(this.histDisplayState == 2){//display absolute number
			this.histogramCanvas._yscale = 100*10; //10 pixels per number
		}else if(this.histDisplayState == 3) {//dispaly auto-scaled number
			this.histogramCanvas._yscale = 100*this.nbrToPixConversion;
		}
	}
	
	function setDistribution():Void{
		var N:Number = this.nbrRows;
		var p:Number = this.model.p;
		var q:Number = 1 - p;
		this.maxInDistribution = this.distribution[0];
		for(var m:Number = 0; m <= N; m++){
			this.distribution[m] = Util.BC(N,m)*Math.pow(p,m)*Math.pow(q,(N-m));
			this.maxInDistribution = Math.max(this.maxInDistribution,this.distribution[m]);
		}
		//trace("this.maxInDistribution"+this.maxInDistribution);
	}//end of computeDistribution()
	
	function drawDistribution():Void{
		this.zeroDistribution();
		this.setDistribution();
		var numRows:Number = this.nbrRows;
		for(var m:Number = 0; m <= this.nbrRows; m++){
			this.distributionCanvas["bar"+m]._height = this.distribution[m]*this.fracToPixConversion; //0.75*this.maxHistHeight/this.maxInDistribution;//*20*numRows;
		}
	}//end of drawHistogram()
	
	function zeroDistribution():Void{
		for(var m:Number = 0; m <= this.maxNbrRows; m++){
			this.distributionCanvas["bar"+m]._height = 0;
		}
	}
	function changeNbrRows(){
		this.nbrRows = this.model.nbrSteps;
		//this.scaleHeightFactor = 30*(this.nbrRows);
		this.fracToPixConversion = 30*this.nbrRows;
		this.displayNRows(this.nbrRows);
		for(var m:Number = 0; m <= this.nbrRows; m++){
			Util.setXYPosition(this.distributionCanvas["bar"+m],(m - nbrRows/2)*2*horizSpace,0);
			this.distributionCanvas._xscale = 100*this.maxNbrRows/nbrRows;
			Util.setXYPosition(this.histogramCanvas["bar"+m],(m - nbrRows/2)*2*horizSpace,0);
			this.histogramCanvas._xscale = 100*this.maxNbrRows/nbrRows;
		}
		this.drawDistribution();
		this.model.zeroHistogram();
		//trace("bigView changeNbrRows Called");
	}
	
	//display top N rows of board and magnify board, distribution, and histogram, as necessary
	function displayNRows(N):Void{
		Util.setXYPosition(this.pegBoardCanvas.cover_mc, 0,(this.maxNbrRows -(0.5+this.maxNbrRows-N))*vertSpace);
		this.pegBoardCanvas._xscale = this.pegBoardCanvas._yscale = 100*this.maxNbrRows/N;
		Util.setXYPosition(this.pathCanvas.cover_mc, 0,(this.maxNbrRows -(0.5+this.maxNbrRows-N))*vertSpace);
		this.pathCanvas._xscale = this.pathCanvas._yscale = 100*this.maxNbrRows/N;
		//for(var i:Number = 0; i < this.pathCanvas.length; i++){
			//this.pathCanvas[i]._xscale = this.pathCanvas[i]._yscale = 100*this.maxNbrRows/N;
		//}
	}
	
	function drawPath():Void{
		//this.currentPathNbr = (this.currentPathNbr + 1)%10;
		with(this.pathCanvas){ //with(this.pathCanvas[0])
			clear();
			_alpha = 100;
			var lineWidth:Number = 2;
			var delY = -2;
			lineStyle(lineWidth, 0xFF0000, 100);
			moveTo(0, 0 + delY);
			//lineTo(0, topY);
			
			for(var i:Number = 0; i < this.nbrRows; i++){
				var nextX = this.model.path[i]*this.horizSpace;
				var nextY = delY + (i+1)*this.vertSpace;
				lineTo(nextX, nextY);
			}
			lineStyle(1.0, 0xFFFFFF, 100);
			moveTo(0, 0 + delY - 1.5);
			for(var i:Number = 0; i < this.nbrRows; i++){
				var nextX = this.model.path[i]*this.horizSpace;
				var nextY = delY + (i+1)*this.vertSpace - 1.5;
				lineTo(nextX, nextY);
			}
			
		}
	}//end of drawPath
	
	
	function erasePath():Void{
		this.pathCanvas.clear();
		//for(var i:Number = 0; i < this.pathCanvas.length; i++){
			//this.pathCanvas[i].clear();
		//}
	}

}//end of class