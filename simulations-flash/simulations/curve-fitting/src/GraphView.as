

class GraphView{
	var stageW:Number;
	var stageH:Number;
	var myModel:Object;
	private var axes_mc:MovieClip;
	private var curve_mc:MovieClip;
	private var deviations_mc:MovieClip;
	var showDeviations:Boolean;
	private var scale:Number;
	
	function GraphView(model:Object){
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.myModel = model;
		this.scale = this.myModel.mainView.scale;
		this.myModel.registerGraphView(this);
		this.axes_mc = _root.createEmptyMovieClip("axes_mc", Util.getNextDepth());
		this.curve_mc = _root.createEmptyMovieClip("curve_mc", Util.getNextDepth());
		this.deviations_mc = _root.createEmptyMovieClip("deviations_mc", Util.getNextDepth());
		this.showDeviations = false;
		this.drawAxes();
	}//end of constructor
	
	function drawAxes():Void{
		var del:Number = 5;
		var xTickMin:Number = Math.ceil(this.scale*(-Util.ORIGINX));
		xTickMin = xTickMin - xTickMin%del;  //multiples of 5
		var xTickMax:Number = Math.floor(this.scale*(this.stageW - Util.ORIGINX) );
		xTickMax = xTickMax - xTickMax%del;
		var yTickMin:Number = Math.ceil(this.scale*(-Util.ORIGINY));
		yTickMin = yTickMin - yTickMin%del;
		var yTickMax:Number = Math.floor(this.scale*(this.stageH - Util.ORIGINY) );
		yTickMax = yTickMax - yTickMax%del;
		//trace(xTickMin); trace(xTickMax);
		axes_mc.clear();
		var lineWidth = 3;
		axes_mc.lineStyle(lineWidth, 0xFF9900, 100, false, "normal", "square");
		//draw x-axis
		axes_mc.moveTo(0,Util.ORIGINY);
		axes_mc.lineTo(this.stageW, Util.ORIGINY);
		//draw y-axis
		axes_mc.moveTo(Util.ORIGINX,0);
		axes_mc.lineTo(Util.ORIGINX, this.stageH);
		//draw tick marks on x-axis
		lineWidth = 3;
		axes_mc.lineStyle(lineWidth, 0xFF9900, 100, false, "normal", "square");
		var tickLength:Number = 7;
		for(var i:Number = xTickMin; i <= xTickMax; i+=del ){
			//trace("i: "+i+"    i/scale: "+i/scale);
			axes_mc.moveTo((Util.ORIGINX + (i/scale)), Util.ORIGINY+2);
			axes_mc.lineTo((Util.ORIGINX + (i/scale)), Util.ORIGINY + tickLength);
			//trace("i: "+scale*i); trace(Util.ORIGINY); trace(Util.ORIGINY + tickLength);
		}
		//tick marks on y-axis
		for(var i:Number = yTickMin; i <= yTickMax; i+=del ){
			//trace("i: "+i+"    i/scale: "+i/scale);
			axes_mc.moveTo(Util.ORIGINX+2  , Util.ORIGINY+ (i/scale));
			axes_mc.lineTo(Util.ORIGINX + tickLength, Util.ORIGINY+ (i/scale));
			//trace("i: "+scale*i); trace(Util.ORIGINY); trace(Util.ORIGINY + tickLength);
		}
		
	}//end of drawAxes()
	
	function clearFit():Void{
		this.curve_mc.clear();
		this.deviations_mc.clear();
	}
	
	
	function drawFit():Void{
		this.drawCurve();
	}//end of drawFit()
	
	function drawCurve():Void{  //a, b, c are polynomial coefficients
		var a:Number = this.myModel.fitParameters[0];
		var b:Number = this.myModel.fitParameters[1];
		var c:Number = this.myModel.fitParameters[2];
		var d:Number = this.myModel.fitParameters[3];
		var e:Number = this.myModel.fitParameters[4];
		if(this.myModel.orderOfFit == 1){  
			with(this.curve_mc){
				clear();
				var lineWidth = 3;
				lineStyle(lineWidth, 0x0000FF, 100);
				if(Math.abs(b) <= 3 ){  //large slope lines must be handled separately
				var xScreenBegin = -0.5*this.stageW; 
				var xScreenEnd = 1.5*this.stageW;
				moveTo(xScreenBegin, this.screenY(a + b*(this.graphX(xScreenBegin))));
				lineTo(xScreenEnd, this.screenY(a + b*(this.graphX(xScreenEnd))));
				}else{  //if slope is too large
					var yScreenBegin = -0.3*this.stageH;
					var yScreenEnd = 1.3*this.stageH;
					moveTo( this.screenX((this.graphY(yScreenBegin) - a) / b), yScreenBegin);
					lineTo( this.screenX((this.graphY(yScreenEnd) - a) / b), yScreenEnd);
				}
			}//end of with()
		}else if(this.myModel.orderOfFit == 2){  // && this.myModel.nbrPoints > 2
			var scaleFactor = this.scale;
			with(this.curve_mc){
				clear();
				var lineWidth = 3;
				lineStyle(lineWidth, 0x0000FF, 100);
				var xBegin = -scaleFactor*this.stageW/2;
				var yBegin = a + b*xBegin + c*xBegin*xBegin;
				moveTo(this.screenX(xBegin),this.screenY(yBegin));
				var delX = scaleFactor*1;
				//var count = 0;
				for(var xG:Number = xBegin; xG < -1.3*xBegin; xG += delX){
					delX = Math.min(scaleFactor*10,0.05*(1+(b+2*c*xG)*(b+2*c*xG))/Math.abs(2*c));
					//count++;
					//trace("count:"+count+"   delX: "+delX);
					var yG = a + b*xG + c*xG*xG;
				lineTo(this.screenX(xG), this.screenY(yG));
				}//end of for
			}//end of with()
		}else if(this.myModel.orderOfFit > 2){  // && this.myModel.nbrPoints > 3
			var scaleFactor = this.scale;
			with(this.curve_mc){
				clear();
				var lineWidth = 3;
				lineStyle(lineWidth, 0x0000FF, 100);
				var xBegin = -scaleFactor*this.stageW/2;
				var xBeginSq = xBegin*xBegin;
				var yBegin = a + b*xBegin + c*xBeginSq + d*xBegin*xBeginSq + e*xBeginSq*xBeginSq;
				moveTo(this.screenX(xBegin),this.screenY(yBegin));
				var delX = scaleFactor*1;
				//var count = 0;
				for(var xG:Number = xBegin; xG < -1.3*xBegin; xG += delX){
					delX = scaleFactor*10;//Math.min(scaleFactor*10,0.05*(1+(b+2*c*xG)*(b+2*c*xG))/Math.abs(2*c));
					//count++;
					//trace("count:"+count+"   delX: "+delX);
					var yG = a + b*xG + c*xG*xG + d*xG*xG*xG  + e*xG*xG*xG*xG;
				lineTo(this.screenX(xG), this.screenY(yG));
				}//end of for
			}//end of with()
		}else if(this.myModel.orderOfFit == 0 || (this.myModel.fitOn && this.myModel.nbrPoints == 0)){
			this.curve_mc.clear();
		}//end of else if
	}//end of drawCurve()
	
	
	
	function updateDeviations():Void{
		this.deviations_mc.clear();
		if(this.showDeviations && this.myModel.orderOfFit != 0){
			with(this.deviations_mc){
				var lineWidth = 3;
				lineStyle(lineWidth, 0x000000, 100);
				var pointRef_arr:Array = this.myModel.points_arr;
				var curveRef_arr:Array = this.myModel.yOnCurve_arr;
				var N:Number = pointRef_arr.length;
				
				for(var i:Number = 0; i < N; i++){
					var xPoint:Number = pointRef_arr[i].xPos;
					var yPoint:Number = pointRef_arr[i].yPos;
					var yCurve:Number = curveRef_arr[i];
					//trace("xPoint: " + xPoint + "   yCurve: "+ yCurve);
					moveTo(this.screenX(xPoint), this.screenY(yPoint));
					lineTo(this.screenX(xPoint), this.screenY(yCurve));
				}//end of for loop
			}//end of with()
		}//end of if()
	}//end of drawDeviations()
	
	//convert graph coordinates into screen coordinates(pixels)
	function screenY(graphY:Number):Number{
		//Relation is: scale*(screenY - Util.ORIGINY) = - graphY
		return (-graphY/this.scale + Util.ORIGINY); 
	}
	function screenX(graphX:Number):Number{
		//Relation is scale*(screenX - Util.ORIGINX) = graphX
		return (graphX/this.scale + Util.ORIGINX);
	}
	//convert screen coordinates into graph coordinates
	function graphX(screenX:Number):Number{
		//Relation is scale*(screenX - Util.ORIGINX) = graphX
		return (this.scale*(screenX - Util.ORIGINX));
	}
	function graphY(screenY:Number):Number{
		//Relation is scale*(screenY - Util.ORIGINY) = -graphY
		return (this.scale*(-1)*(screenY - Util.ORIGINY));
	}
}//end of class