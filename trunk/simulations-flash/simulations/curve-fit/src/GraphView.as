

class GraphView{
	var stageW:Number;
	var stageH:Number;
	private var myModel:Object;
	private var axes_mc:MovieClip;
	private var curve_mc:MovieClip;
	private var scale:Number;
	
	function GraphView(model:Object){
		this.stageW = Util.STAGEW;
		this.stageH = Util.STAGEH;
		this.myModel = model;
		this.scale = this.myModel.mainView.scale;
		this.myModel.registerGraphView(this);
		this.axes_mc = _root.createEmptyMovieClip("axes_mc", Util.getNextDepth());
		this.curve_mc = _root.createEmptyMovieClip("curve_mc", Util.getNextDepth());
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
	}
	
	
	function drawFit():Void{
		this.drawCurve();
	}//end of drawFit()
	
	function drawCurve():Void{  //a, b, c are polynomial coefficients
		var a:Number = this.myModel.fitParameters[0];
		var b:Number = this.myModel.fitParameters[1];
		var c:Number = this.myModel.fitParameters[2];
		if(this.myModel.orderOfFit == 1 || this.myModel.nbrPoints == 2){
			with(this.curve_mc){
				clear();
				var lineWidth = 3;
				lineStyle(lineWidth, 0x000099, 100);
				var xScreenBegin = -0.5*this.stageW; 
				var xScreenEnd = 1.5*this.stageW;
				moveTo(xScreenBegin, this.screenY(a + b*(this.graphX(xScreenBegin))));
				lineTo(xScreenEnd, this.screenY(a + b*(this.graphX(xScreenEnd))));
			}//end of with()
		}else if(this.myModel.orderOfFit == 2 && this.myModel.nbrPoints > 2){
			var scaleFactor = this.scale
			with(this.curve_mc){
				clear();
				var lineWidth = 3;
				lineStyle(lineWidth, 0x0000FF, 100);
				var xBegin = -scaleFactor*this.stageW/2;
				var yBegin = a + b*xBegin + c*xBegin*xBegin;
				moveTo(this.screenX(xBegin),this.screenY(yBegin));
				var delX = scaleFactor*1;
				var count = 0;
				for(var xG:Number = xBegin; xG < -1.3*xBegin; xG += delX){
					delX = Math.min(scaleFactor*10,0.05*(1+(b+2*c*xG)*(b+2*c*xG))/Math.abs(2*c));
					count++;
					//trace("count:"+count+"   delX: "+delX);
					var yG = a + b*xG + c*xG*xG;
				lineTo(this.screenX(xG), this.screenY(yG));
				}//end of for
			}//end of with()
		}//end of else if
	}//end of drawCurve()
	

	function screenY(graphY:Number):Number{
		//Relation is: scale*(screenY - Util.ORIGINY) = - graphY
		return (-graphY/this.scale + Util.ORIGINY); 
	}
	function screenX(graphX:Number):Number{
		//Relation is scale*(screenX - Util.ORIGINX) = graphX
		return (graphX/this.scale + Util.ORIGINX);
	}
	function graphX(screenX:Number):Number{
		//Relation is scale*(screenX - Util.ORIGINX) = graphX
		return (this.scale*(screenX - Util.ORIGINX));
	}
}//end of class