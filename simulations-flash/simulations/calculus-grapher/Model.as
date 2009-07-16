package{
	import flash.events.*;
	
	public class Model{
		
		var y_arr:Array;		//y-values of the curves
		var deriv_arr:Array;	//y-values of the derivative of the curve
		var integ_arr:Array;	//y-values of the integral of the curve
		var pre_arr:Array;		//y-values of dent shown as preview Curve
		var scratch_arr:Array;	//y-values of temporary array for intermediate calcs
		var tempSmooth_arr:Array; //y-values of temp array for calculating smoothed curve
		var tempSmooth2_arr:Array;//need two scratch arrays for the smoothing algorithm
		var undo_arr:Array;		//array of previous y_arr arrays for undo button
		var dx:Number;			//delta-x = x-increment
		var nbrPoints:int;		//number of points in the curve
		var nbrPtsPreview:int;	//number of points in the preview curve
		
		var cntrPt:int;			//x-value of handle grabbed in view
		var range:Number;		//range of smoothing in "hill"
		var smoothingRange:Number; //range of smoothing when whole curve is smoothed
		var lastX:Number;		//last x-value drawn in freeform mode
		var lastY:Number;		//last y-value drawn in freeform mode
		var slope:Number;		//slope of dent in curve
		var waveNbr:Number;	//k of sine wave curve
		var pedestalWidth:Number;
		private var alterMode:String;	//"Hill", "Linear", "Parabola", etc
		
		var handleLocation:int;	//x-position of currently grabbed handle
		//following scratch arrays needed for doubleSmoothing algorithm
		var oldY:Array;
		var newY:Array;
		
		var view_arr:Array;  	//Different views of this model, views of the curves
		var nbrOfViews:int;
		var preView:Object;		//preview of curve displayed in control panel (Sprite)
		//var currentY:Array;
		
		public function Model(){
			this.nbrPoints = 4*90 + 1;  //nbrPointsPerHandle*integer + 1
			this.nbrPtsPreview = 108;  //must be width of preview screen on control panel (should be updated by controlPanel)
			this.dx = 1;
			this.y_arr = new Array(this.nbrPoints);
			this.deriv_arr = new Array(this.nbrPoints);
			this.integ_arr = new Array(this.nbrPoints);
			this.pre_arr = new Array(this.nbrPtsPreview);
			this.scratch_arr = new Array(this.nbrPoints);
			this.smoothingRange = 5;
			this.tempSmooth_arr = new Array(this.nbrPoints + 2*this.smoothingRange);
			this.tempSmooth2_arr = new Array(this.nbrPoints + 2*this.smoothingRange);
			this.undo_arr = new Array();
			
			this.view_arr = new Array(0);
			this.nbrOfViews = 0;
			
			this.range = 0.3*Math.pow(1.5, 3);  //second argument is initial slider value
			this.slope = 1;
			this.waveNbr = 10;
			
			this.oldY = new Array(this.nbrPoints);
			this.newY = new Array(this.nbrPoints);
			//this.currentY = new Array(this.nbrPoints);
			this.initialize();
			
			
			//trace("model instantiated.");
			
		}//end of constructor
		
		public function initialize():void{
			for(var i:int = 0; i < this.nbrPoints; i++){
				this.y_arr[i] = this.deriv_arr[i] = this.integ_arr[i] = 0;
				this.oldY[i] = newY[i] = 0;
			}
			this.zeroTempSmoothArray();
		}
		
		public function zeroAllCurves():void{
			//trace("zeroAllCurves");
			for(var i:int = 0; i < this.nbrPoints; i++){
				this.y_arr[i] = this.deriv_arr[i] = this.integ_arr[i] = 0;
				this.oldY[i] = newY[i] = 0;
			}
			this.updateViews();
		}
		
		//needed for curve smoothing algorithm
		public function zeroTempSmoothArray():void{
			for (var i:int = 0; i < this.tempSmooth_arr.length; i++){
				this.tempSmooth_arr[i] = 0
			}
		}
		
		public function takeDerivative():void{
			//trace("model.derivativeOf called");
			for(var i:int = 1; i < this.nbrPoints; i++){
				this.deriv_arr[i] = (y_arr[i] - y_arr[i-1])/this.dx;
			}
			this.deriv_arr[0] = this.deriv_arr[1];
			//trace(scratch_arr[50]);
		}
		
		public function takeIntegral():void{
			this.integ_arr[0] = 0;
			for(var i:int = 1; i < this.nbrPoints; i++){
				this.integ_arr[i] = this.integ_arr[i-1] + y_arr[i-1]*this.dx;
			}
		}
		

		
		public function copyCurrentY():void{
			this.oldY = this.y_arr.concat();  //concat function clones array
			var temp_arr:Array = new Array();
			temp_arr = this.oldY.concat();
			var nbrArraysInUndoStack:int = this.undo_arr.length;
			this.undo_arr.push(temp_arr);
			if(nbrArraysInUndoStack > 50){
				this.undo_arr.shift();
			}
		}//end of copyCurrentY
		
		public function undoLastChange():void{
			var temp_arr:Array = new Array();
			if(this.undo_arr.length > 0){
				temp_arr = this.undo_arr.pop();
				for(var i:int = 0; i < this.nbrPoints; i++){
					this.y_arr[i] = temp_arr[i];
				}
			}//end of if
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}
		
		public function setAlterMode(newMode:String):void{
			this.alterMode = newMode;
			this.updatePreviewCurve();
			//trace("myModel.alterMode is "+this.alterMode);
		}//end of setAlterMode()
		
		public function getAlterMode():String{
			return this.alterMode;
			//trace("myModel.alterMode is "+this.alterMode);
		}//end of setAlterMode()
		
		public function alterCurve():void{
			
			switch(this.alterMode){
				case "Offset":
					makeOffset();
					break;
				case "Pedestal":
					makeDent();
					break;
				case "Hill":
					//makeDent();
					smoothCurve();
					break;
				case "Linear":
					makeDent();
					break;
				case "Parabola":
					makeDent();
					break;
				case "Sine":
					makeDent();
					//makeSine();
					break;
				case "Tilt":
					makeTilt();
					//makeSine();
					break;
				case "Freeform":
					//drawFreeform();
					//makeSine();
					break;
				default:
					trace("ERROR: myModel.alterMode set with invalid string");
			}
		}//end of alterCurve()
		
		public function setWidthOfDent(sliderValue:Number):void{
			if(this.alterMode == "Hill"){
				var max:Number = 50;
				var min:Number = 1;
				var fG:Number = Math.pow(max/min, 1/10);
				this.range = min*Math.pow(fG, sliderValue);
				//trace("range: "+this.range);
				//this.range = 0.2*Math.pow(1.7, sliderValue);
			}else if(this.alterMode == "Linear"){
				var slopeMin:Number = 1/5;
				var slopeMax:Number = 15;
				var fS:Number = Math.pow(slopeMax/slopeMin, 1/10);
				this.slope = slopeMin*Math.pow(fS,10-sliderValue);//this.slope = Math.tan(0.155*(10-sliderValue));
				//trace("linear alterMode has slope = "+this.slope);
			}else if (this.alterMode == "Parabola"){
				this.slope = 0.001*Math.pow(2.2,10-sliderValue);
				//trace("slope of parabola = "+slope);
			}else if (this.alterMode == "Pedestal"){
				var widthMin:Number = 5;
				var widthMax:Number = 350
				var fP:Number = Math.pow(widthMax/widthMin, 1/10);
				this.pedestalWidth = 2.5*Math.pow(fP, sliderValue);
			}else if (this.alterMode == "Sine"){
				var lambdaMin:Number = 10;
				var lambdaMax:Number = 300;
				var fSi:Number = Math.pow(lambdaMax/lambdaMin, 1/10);
				var lambda:Number = lambdaMin*Math.pow(fSi, sliderValue);
				this.waveNbr = 2*Math.PI/lambda;
			}
			this.updatePreviewCurve();
		}//end of setWidthOfDent()
		
		public function makeOffset():void{
			var cntrPt = this.handleLocation;
			var deltaY:Number = this.y_arr[cntrPt] - this.oldY[cntrPt];  
			var newY:Number;
			for (var i:Number = 0; i < this.nbrPoints; i++){
				this.y_arr[i] = this.oldY[i] + deltaY;
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of makeOffset()
		
		public function makeTilt():void{
			var cntrPt = this.handleLocation;
			var deltaY:Number = this.y_arr[cntrPt] - this.oldY[cntrPt];  
			var newY:Number;
			for (var i:Number = 0; i < this.nbrPoints; i++){
				this.y_arr[i] = this.oldY[i] + deltaY*i/cntrPt;
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}
		
		public function drawFreeform(xP:Number, yP:Number):void{
			xP = Math.round(xP);
			this.y_arr[xP] = yP;
			var distX:int = Math.abs(xP - lastX);
			var sign:Number = xP - lastX;  //positive or negative
			var W:Number;  //weighting factor
			//trace("sign: "+sign);
			if(distX > 1){
				for(var i:int = 1; i < distX; i++){
					W = i/distX;
					if(sign > 0){
						this.y_arr[lastX + i] = (1-W)*this.lastY + W*yP;
					}else{
						this.y_arr[lastX - i] = (1-W)*this.lastY + W*yP;
					}
					
				}//end of for
			}//end of if(distX > 1)
			this.lastX = xP;
			this.lastY = yP;
			//trace("Model.drawFreeform called");
			/*var cntrPt = this.handleLocation;
			var N:int = 4; 	//range of averaging is cntrPt +/- N
			var sign:int;	//+1 or -1
			var W:Number;	//weighting factor
			for (var i:int = 1; i < N; i++){
				W = (N - i)/N;
				if(cntrPt+N < this.nbrPoints){
					this.y_arr[cntrPt + i] = W*this.y_arr[cntrPt] + (1 - W)*this.y_arr[cntrPt + N];
				}
				if(cntrPt - N >= 0){
					this.y_arr[cntrPt - i] = W*this.y_arr[cntrPt] + (1 - W)*this.y_arr[cntrPt - N];
				}
			}//end of for loop*/
			//smooth slightly
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of drawFreeform()
		
		public function setLastXAndLastY(xP:int, yP:Number){
			this.lastX = xP;
			this.lastY = yP;
		}
		
		
		
		public function simpleSmoothAllPoints():void{
			var range:int = this.smoothingRange;
			var sumOfYs:Number;
			var N:Number;
			var dist:int;
			for(var i:int = 0; i < this.nbrPoints; i++){
				sumOfYs = 0;
				N = 0; 
				for(var j:int = -range; j <= range; j++){
					//dist = Math.abs(j);
					if((i+j >= 0) && (i+j < this.nbrPoints)){
						sumOfYs += this.y_arr[i+j]; //1/(dist+1)*this.y_arr[i+j];
						N += 1;//1/(dist+1);
					}else{
						N += 1;
						if(i+j < 0){
							sumOfYs += this.y_arr[0];
						}else {   //if(i+j >= this.nbrPoints)
							sumOfYs += this.y_arr[this.nbrPoints-1];
						}
					}
				}//end of for j
				this.scratch_arr[i] = sumOfYs/N;
			}//end of for i
			for(var k:int = 0; k < this.nbrPoints; k++){
				this.y_arr[k] = this.scratch_arr[k];
			}
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of simpleSmoothAllPoints()
		
		public function makeDent():void{
			var cntrPt = this.handleLocation;
			var sign:int = 1.0;  //+1 if new curve above old curve, -1 if below
			if(this.y_arr[cntrPt] < this.oldY[cntrPt]){
				sign = -1.0;
			}
			var newY:Number;
			var clearOnRight:Boolean = true;
			var clearOnLeft:Boolean = true;
			var clearAboveOrBelow:Boolean;
			var clearForSine:Boolean;
			var j:Number = 1;
			var i:Number = cntrPt;
			while(i < this.nbrPoints){
				newY = this.getNewY(cntrPt, sign, j);
				i = cntrPt + j;
				clearAboveOrBelow =(this.alterMode != "Sine")&&(sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i]);
				clearForSine = (this.alterMode == "Sine") && (Math.abs(newY) > Math.abs(this.oldY[i]));
				if(clearOnRight && (clearAboveOrBelow || clearForSine)){
				//if(clearOnRight && Math.abs(newY) > Math.abs(this.oldY[i])){
					this.y_arr[i] = newY;
				}else{
					this.y_arr[i] = this.oldY[i];
					clearOnRight = false;
				}
				j++;
			}
			j = 1;
			i = cntrPt
			while(i > 0){
				newY = this.getNewY(cntrPt, sign, j);
				i = cntrPt - j;
				clearAboveOrBelow =(this.alterMode != "Sine")&&(sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i]);
				clearForSine = (this.alterMode == "Sine") && (Math.abs(newY) > Math.abs(this.oldY[i]));
				if(clearOnLeft && (clearAboveOrBelow || clearForSine)){
				//if(clearOnLeft && Math.abs(newY) > Math.abs(this.oldY[i])){
					this.y_arr[i] = newY;
				}else{
					this.y_arr[i] = this.oldY[i];
					clearOnLeft = false;
				}
				j++;
			}
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of makeDent()
		
		public function getNewY(cntrPt:int, sign:int, j:int):Number{
			var newY:Number;
			if(this.alterMode == "Linear"){
				newY = this.y_arr[cntrPt] - sign*j*this.slope;
			}else if(this.alterMode == "Parabola"){
				newY = this.y_arr[cntrPt] - sign*j*j*this.slope;//this.y_arr[cntrPt] - sign*j*j*this.slope;
				//trace(this.oldY[cntrPt]);
			}else if(this.alterMode == "Pedestal"){
				if(j < this.pedestalWidth){
					newY = this.y_arr[cntrPt];
				}
			}else if(this.alterMode == "Hill"){
				var delY:Number = this.y_arr[cntrPt] - this.oldY[cntrPt];
				//newY = this.y_arr[cntrPt]*Math.exp(-(j*j)/(this.range*this.range));
				var P:Number = Math.exp(-j/(this.range*Math.log(delY+1)));
				newY = P*y_arr[cntrPt] + (1-P)*oldY[cntrPt + j];
				newY = P*y_arr[cntrPt] + (1-P)*newY;
			}else if(this.alterMode =="Sine"){
				newY = this.y_arr[cntrPt]*Math.cos(j*this.waveNbr);
			}
			return newY;
		}//end of makeNewY()
		

		public function smoothCurve():void{
			var P:Number = 1;
			var cntrPt = this.handleLocation;
			var delY:Number = Math.abs(this.y_arr[cntrPt] - this.oldY[cntrPt]);
			//trace("delY:"+delY);
			for (var i:Number = 0; i < this.nbrPoints; i++){
				
				if(i != cntrPt){
					var dist = Math.abs(i - cntrPt);
					P = Math.exp(-dist/(this.range*Math.log(delY+1)));
					this.y_arr[i] = P*y_arr[cntrPt] + (1-P)*oldY[i];
				}
				this.y_arr[i] = P*y_arr[cntrPt] + (1-P)*y_arr[i];
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of smoothCurve()
		
		
		public function makeSine():void{
			var cntrPt = this.handleLocation;
			for (var i:Number = 0; i < this.nbrPoints; i++){
				//i = cntrPt + j, j = i - cntrPt
				var sinValue:Number  = this.y_arr[cntrPt]*Math.cos((i - cntrPt)*this.waveNbr);
				if(Math.abs(sinValue) > Math.abs(this.oldY[i])){
					this.y_arr[i] = sinValue;
				}else{
					this.y_arr[i] = this.oldY[i];
				}
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of makeSine()
		
		public function updatePreviewCurve():void{
			var cntrPt:int = this.nbrPtsPreview/2;
			var cntrY:Number = 25;
			var j:Number = 0;
			var xFactor:Number = 1;
			//trace("cntrPt:"+cntrPt);
			// i = cntrPt + j so j = i - cntrPt
			for(var i:int = 0; i < this.nbrPtsPreview; i++){
				if(this.alterMode == "Linear"){
					//newY = this.y_arr[cntrPt] - sign*j*this.slope
					j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = cntrY  - j*this.slope;
					if(this.pre_arr[i] < 0){this.pre_arr[i] = 0;}
					//pre_arr[i] = this.getNewY(cntrPt, 1.0, i - cntrPt);
				}else if(this.alterMode == "Parabola"){
					j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = cntrY  - j*j*this.slope;
					if(this.pre_arr[i] < 0){this.pre_arr[i] = 0;}
				}else if(this.alterMode == "Hill"){
					j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = cntrY*Math.exp(-(j*j)/(this.range*this.range));
					//j = xFactor*Math.abs(cntrPt - i);
					//var P:Number = Math.exp(-j/(this.range*Math.log(cntrY+1)));
					//this.pre_arr[i] = P*cntrY;// + (1-P)*oldY[i];
					//this.pre_arr[i] = P*cntrY + (1-P)*pre_arr[i];
				}else if(this.alterMode == "Pedestal"){
					j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = cntrY;
					if(j > this.pedestalWidth/2){
						this.pre_arr[i] = 0;
					}
				}else if(this.alterMode == "Offset"){
					//j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = cntrY;
				}else if(this.alterMode == "Tilt"){
					//j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = i*cntrY/this.pre_arr.length;
				}else if(this.alterMode == "Sine"){
					//j = xFactor*Math.abs(cntrPt - i);
					this.pre_arr[i] = 0.45*cntrY + 0.5*cntrY*Math.sin(i*this.waveNbr);
					//if(this.pre_arr[i] < 0){this.pre_arr[i] = 0;}
				}else if (this.alterMode == "Freeform"){
					this.pre_arr[i] = 0;
					//do nothing
				}
			}//end of for loop
			
			this.updatePreview();
			//for(var k:int = 0; k < this.nbrPtsPreview; k++){
//				//trace("k: " + k);
//				trace("x: " + k + "   y:" + pre_arr[k]);
//			}
//			trace("alterMode: " + this.alterMode);
		}//end of updatePreviewCurve()
		
		public function registerView(aView:Object):void{
			this.nbrOfViews += 1;
			this.view_arr.push(aView);
			//trace("the view "+this.view_arr[nbrOfViews-1].curveType+" is registered");
		}
		
		public function registerPreview(aView:Object):void{
			this.preView = aView;
			//trace("preview registered.");
		}
		
		public function updateViews():void{
			for(var i:int = 0; i < nbrOfViews; i++){
				this.view_arr[i].update();
			}
		}
		
		public function updatePreview():void{
			this.preView.update();
		}
		
		
	}//end of class
}//end of package

/*
//replace y-value of point with average of y-values of near-neighbors
		//following is obsolete, replaced with simpleSmoothAllPoints()
		public function smoothAllPoints():void{
			var range:int = this.smoothingRange;
			//copy current curve into middle of tempSmooth array and set ends of tempSmooth to zero
			for (var iS:int = 0; iS < this.tempSmooth_arr.length; iS++){
				if(iS >= range && iS < this.nbrPoints + range){
					this.tempSmooth_arr[iS] = this.y_arr[iS - range];
				}else{
					//this.tempSmooth_arr[iS] = 0;
				}
			//trace("iS: "+iS+"  this.tempSmooth_arr[iS]:"+this.tempSmooth_arr[iS]);
			}//end of for loop
			var sumOfYs:Number;
			var N:Number;
			for(iS = 0; iS < this.tempSmooth_arr.length; iS++){
				sumOfYs = 0;
				N = 0;
				for(var j:int = -range; j <= range; j++){
					if(iS+j >= 0 && iS+j < this.tempSmooth_arr.length){
						sumOfYs += this.tempSmooth_arr[iS+j];
						N += 1;
					}else{
						N += 1;  //necessary for points near ends of curve
					}
				}//end of for j
				this.tempSmooth2_arr[iS] = sumOfYs/N;
				if(iS == range || iS == this.nbrPoints+range-1){
				}
			}//end of for iS
			for(var k:int = 0; k < this.nbrPoints; k++){
				this.y_arr[k] = this.tempSmooth2_arr[k+range];
			}
			this.tempSmooth_arr = this.tempSmooth2_arr.concat();
			//trace("this.y_arr[0]: "+this.y_arr[0]+"     this.y_arr[this.nbrPoints - 1]:"+this.y_arr[this.nbrPoints - 1]);
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of smoothAllPoints
*/
/*
//following functions currently unused
		public function derivativeOf(yVal:Array):Array{
			//trace("model.derivativeOf called");
			for(var i:int = 1; i < this.nbrPoints; i++){
				this.scratch_arr[i] = (yVal[i] - yVal[i-1])/this.dx;
			}
			this.scratch_arr[0] = this.scratch_arr[1];
			//trace(scratch_arr[50]);
			return this.scratch_arr;
		}
		public function integralOf(yVal:Array, offset:Number):Array{
			this.scratch_arr[0] = offset;
			for(var i:int = 1; i < this.nbrPoints; i++){
				this.scratch_arr[i] = (yVal[i] - yVal[i-1])*this.dx;
			}
			this.scratch_arr[0] = this.scratch_arr[1];
			return this.scratch_arr;
		}
		public function makePedestal():void{
			var cntrPt = this.handleLocation;
			var sign:Number = 1.0;  //+1 if new curve above old curve, -1 if below
			var newY:Number;
			if(this.y_arr[cntrPt] < this.oldY[cntrPt]){
				sign = -1.0;
			}
			for (var i:Number = 0; i < this.nbrPoints; i++){
				var dist = Math.abs(i - cntrPt);
				newY = this.y_arr[cntrPt];
				if((sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i]) ){
					if(dist < this.pedestalWidth){
						this.y_arr[i] = newY;
					}
				}else{
					this.y_arr[i] = this.oldY[i];
				}
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//end of makePedestal()
		
		public function makeLinearDent():void{
			var cntrPt = this.handleLocation;
			var sign:Number = 1.0;  //+1 if new curve above old curve, -1 if below
			var newY:Number;
			if(this.y_arr[cntrPt] < this.oldY[cntrPt]){
				sign = -1.0;
			}
//			for (var i:Number = 0; i < this.nbrPoints; i++){
//				var dist = Math.abs(i - cntrPt);
//				newY = this.y_arr[cntrPt] - sign*dist*this.slope;
//				if((sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i]) ){
//					this.y_arr[i] = newY;
//				}else{
//					this.y_arr[i] = this.oldY[i];
//				}
//			}//end of for loop
			var clearOnRight:Boolean = true;
			var clearOnLeft:Boolean = true;
			var j:Number = 1;
			var i:Number = cntrPt;
			while(i < this.nbrPoints){
				newY = this.y_arr[cntrPt] - sign*j*this.slope;
				i = cntrPt + j;
				if(clearOnRight &&((sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i])) ){
					this.y_arr[i] = newY;
				}else{
					this.y_arr[i] = this.oldY[i];
					clearOnRight = false;
				}
				j++;
			}
			j = 1;
			i = cntrPt
			while(i > 0){
				newY = this.y_arr[cntrPt] - sign*j*this.slope;
				i = cntrPt - j;
				if(clearOnLeft && ((sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i])) ){
					this.y_arr[i] = newY;
				}else{
					this.y_arr[i] = this.oldY[i];
					clearOnLeft = false;
				}
				j++;
			}
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//makeLinearDent()
		
		public function makeParabolicDent():void{
			var cntrPt = this.handleLocation
			var sign:Number = 1.0;  //+1 if new curve above old curve, -1 if below
			var newY:Number;
			if(this.y_arr[cntrPt] < this.oldY[cntrPt]){
				sign = -1.0;
			}
			for (var i:Number = 0; i < this.nbrPoints; i++){
				var dist = Math.abs(i - cntrPt);
				newY = this.y_arr[cntrPt] - sign*dist*dist*this.slope;
				if((sign == 1 && newY > this.oldY[i]) || (sign == -1 && newY < this.oldY[i]) ){
					this.y_arr[i] = newY;
				}else{
					this.y_arr[i] = this.oldY[i];
				}
			}//end of for loop
			this.takeDerivative();
			this.takeIntegral();
			this.updateViews();
		}//makeParabolicDent()
		*/