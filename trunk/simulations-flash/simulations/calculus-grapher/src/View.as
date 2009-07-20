//View of a curve
package{
	import flash.events.*;
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.geom.*;
	import flash.text.*;
	import fl.motion.Color;
	
	public class View extends Sprite{
		
		var myModel:Model;		//reference to controlling model
		var myMainView:MainView;
		var canvas:Sprite;		//background on which curve is placed
		var grid:Sprite;		//xy grid
		var axes:Sprite;
		var curve:Sprite;		//displayed curve of function
		var curveGraphics:Graphics;
		var canvasGraphics:Graphics;
		var magnifyButton:Sprite;
		var deMagnifyButton:Sprite;
		//var currentYScale:Number;	//indicates magnification of y-scale
		var curveType:String;	//curveType is "original", "derivative", or "integral"
		var handles:Array;		//array of "handles" for grabbing points on the curve
		var pointsPerHandle:int;	//nbr of curve points per handle
		var pixelsPerPoint:int;	//nbr of pixels between curve points
		//var curvePoints:Array;
		
		var curve_arr:Array; 	//local reference to curve array in Model
		var nbrHandles:int;		//total nbr of handles
		var nbrPoints:int;		//total nbr of points in curve
		//var handleWidth:int;   //in pixels
		var someHandleSelected:Boolean;  //true if mouse is dragging a handle
		var alphaOnValue:Number;	//used for diagnostics only, to see active handle
		var alphaOffValue:Number;	//diagnostics only
		var curveColor:uint;	//color of the curve 0xrrggbb
		var viewHeight:int;		//height in pixels of this view
		var xOffset:Number;		//offset to shift derivative curve by half-step
		var yScale:Number;		//used to scale y Axis
		var originX:Number;		//x-position of origin in pixels
		var originY:Number;		//y-position of origin in pixels
		
		var xAxisLabel:TextField;
		var yAxisLabel:TextField;
		var yAxisIntegralLabel:Sprite;
		var integralLabel
		var tFormat:TextFormat;
		
		public function View(myModel:Model, myMainView:MainView, curveType:String){
			
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.curveType = curveType;
			this.canvas = new Sprite();
			this.grid = new Sprite();
			this.axes = new Sprite();
			this.curve = new Sprite();
			this.pointsPerHandle = 4;
			this.nbrPoints = myModel.nbrPoints;
			this.nbrHandles = 1+ this.nbrPoints/this.pointsPerHandle;
			//trace("this.nbrPoints: "+this.nbrPoints+"    this.nbrHandles: "+this.nbrHandles);
			this.pixelsPerPoint = 2;
			//this.viewHeight = 300;
			this.xOffset = 0;
			//this.handleWidth = 5;
			this.someHandleSelected = false;
			this.alphaOnValue = 0;
			this.alphaOffValue = 0;
			this.curveColor = 0x0000ff;
			//this.originX = 50;
			//this.originY = this.viewHeight/2;
			this.handles = new Array(nbrHandles);
			this.xAxisLabel = new TextField();
			this.yAxisLabel = new TextField();
			
			this.tFormat = new TextFormat();
			this.tFormat.font = "_sans";
			this.tFormat.size = 20;
			addChild(canvas);
			this.canvas.addChild(grid);
			this.canvas.addChild(axes);
			this.canvas.addChild(curve);
			this.canvas.addChild(xAxisLabel);
			this.canvas.addChild(yAxisLabel);
					
			//trace("view instantiated");
			
		}//end of constructor
		
		public function initialize():void{
			this.myModel.registerView(this);
			this.originX = 40;
			this.originY = this.viewHeight/2;
			this.curve.y = originY;
			this.yScale = 1;
			this.initializeZoomControls();
			this.grid.visible = false;

			
			switch(this.curveType){
				case "original" :
					this.curve_arr = myModel.y_arr;
					this.yAxisLabel.text = "f(x)";
					this.curveColor = 0x0000ff;
					this.yScale = 1;
					//trace("original");
					break;
				case "derivative":
					this.curve_arr = myModel.deriv_arr;
					this.yAxisLabel.text = "df/dx";
					//trace("derivative");
					this.xOffset = 0.5;
					this.curveColor = 0xff0000;
					this.yScale = 10;
					break;
				case "integral":
					this.curve_arr = myModel.integ_arr;
					this.yAxisLabel.text = "";
					this.curveColor = 0x00aa00;
					this.yScale = 0.02;
					this.yAxisIntegralLabel = new IntegralLabel();
					addChild(this.yAxisIntegralLabel);
					//this.yAxisIntegralLabel.scaleX = this.yAxisIntegralLabel.scaleY = 0.25;
					//trace("integral");
					break;
				default:
					trace("ERROR: curveType is wrong string in View.");
			}
			this.yAxisLabel.selectable = false;
			this.xAxisLabel.selectable = false;
			this.yAxisLabel.mouseEnabled = false;
			this.xAxisLabel.mouseEnabled = false;
			this.yAxisLabel.setTextFormat(tFormat);
			this.xAxisLabel.text = "x";
			this.xAxisLabel.setTextFormat(tFormat);
			this.positionAxisLabels();
		 
			//create array of handles for grabbing points on curve
			for (var i:int = 0; i < nbrHandles; i++){
				this.handles[i] = new Handle(i);
				this.handles[i].setWidth(this.pixelsPerPoint*this.pointsPerHandle);
				this.curve.addChild(handles[i]);
				handles[i].y = 0;//this.originY;   //class Handle extends Sprite, so it has x and y variables
				handles[i].x = this.originX + i*this.pixelsPerPoint*this.pointsPerHandle - this.pixelsPerPoint*this.xOffset;
				if(this.curveType == "original"){
					this.handles[i].buttonMode = true;
					this.makeHandleDraggable(handles[i]);
				}
			}//end of for loop
			this.makeCurveDraggable();
			
			this.curveGraphics = this.curve.graphics;
			this.curveGraphics.lineStyle(2, this.curveColor);
			this.canvasGraphics = this.canvas.graphics;
			this.canvasGraphics.lineStyle(2, 0x000000);
			this.drawAxes();

			this.update();
		}//end of initialize
		
		public function initializeZoomControls():void{
			this.magnifyButton = new MagnifyButton();
			this.deMagnifyButton = new DeMagnifyButton();
			//this.currentYScale = 1.0;
			this.canvas.addChild(this.magnifyButton);
			this.canvas.addChild(this.deMagnifyButton);
			this.magnifyButton.addEventListener(MouseEvent.CLICK, magnifyCurve);
			this.deMagnifyButton.addEventListener(MouseEvent.CLICK, demagnifyCurve);
			this.magnifyButton.buttonMode = true;
			this.deMagnifyButton.buttonMode = true;
			this.positionZoomControls();
		}
		
		public function positionZoomControls():void{
			this.magnifyButton.scaleX = this.magnifyButton.scaleY = 0.4;
			this.deMagnifyButton.scaleX = this.deMagnifyButton.scaleY = 0.4;
			this.magnifyButton.x = this.originX - 10;
			this.magnifyButton.y = this.originY - 25;
			this.deMagnifyButton.x = this.originX - 10;
			this.deMagnifyButton.y = this.originY + 25;
		}
		
		public function magnifyCurve(evt:MouseEvent):void{
			this.yScale *= 2;
			//this.drawGrid();
			//this.currentYScale = this.yScale;
			this.update();
		}
		public function demagnifyCurve(evt:MouseEvent):void{
			this.yScale /= 2;
			//this.drawGrid();
			//this.currentYScale = this.yScale;
			this.update();
		}
		
		public function makeHandleDraggable(target:Sprite):void{
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			//target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			//target.addEventListener(MouseEvent.MOUSE_OVER, onTarget);
			//target.addEventListener(MouseEvent.MOUSE_OUT, offTarget);
			//target.addEventListener(MouseEvent.ROLL_OUT, rollOffTarget);
			//target.addEventListener(MouseEvent.ROLL_OVER, rollOverTarget);
			var localRef:Object = this;
			var handleIndex:int;
			//var clickOffset:Point;
			var aHandSelected:Boolean;
			function startTargetDrag(evt:MouseEvent):void{
				handleIndex = evt.currentTarget.index;
				localRef.myModel.handleLocation = handleIndex*localRef.pointsPerHandle;
				//clickOffset = new Point(evt.localX, evt.localY);
				//aHandSelected = true;
				localRef.someHandleSelected = true;
				//trace(localRef.myModel.handleLocation);
				localRef.myModel.copyCurrentY();
				evt.target.alpha = alphaOnValue;
				localRef.myMainView.dragMeSign.visible = false;
				if(localRef.myModel.getAlterMode() == "Freeform"){
					var xP:Number = localRef.mouseX - localRef.originX;
					var yP:Number = localRef.mouseY - localRef.originY;
					var xC:Number = xP/localRef.pixelsPerPoint;
					localRef.myModel.setLastXAndLastY(xC,-yP/localRef.yScale);
				}
				//evt.target.setSelected(true);
				
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				//localRef.handles[handleIndex].setSelected(false);
				localRef.handles[handleIndex].alpha = alphaOffValue;
				localRef.someHandleSelected = false;
				//clickOffset = null;
				//aHandSelected = false;
			}
			
			//function dragTarget(evt:MouseEvent):void{
//				if(aHandSelected){  //if dragging
//					target.y = (mouseY - localRef.originY);
//					var xValueOfHandle:int = handleIndex*localRef.pointsPerHandle;
//					localRef.curve_arr[xValueOfHandle] = -target.y/localRef.yScale;//-target.y/localRef.yScale;
//					localRef.myModel.alterCurve();
//					evt.updateAfterEvent();
//				}
//			}
			function onTarget(evt:MouseEvent):void{
				if(!localRef.someHandleSelected){
					evt.target.alpha = alphaOnValue;
					//new code
					//handleIndex = evt.currentTarget.index;
					//localRef.myModel.handleLocation = handleIndex*localRef.pointsPerHandle;
					//clickOffset = new Point(evt.localX, evt.localY);
					//end of new code
				}
			}


		}//end of makeHandleDraggable()
		
		public function makeCurveDraggable():void{
			this.stage.addEventListener(MouseEvent.MOUSE_MOVE, moveMouse);
		}//makeCurveDraggable()
		
		public function moveMouse(evt:MouseEvent):void{
			var xP:Number = mouseX - this.originX;
			var yP:Number = mouseY - this.originY;
			var xC:Number;  
			var currentMode:String = this.myModel.getAlterMode();
			if(this.someHandleSelected && currentMode != "Freeform"){
					//trace("x: "+evt.localX + "   y: " + evt.localY);
					//trace("xP: " + xP + "  this.nbrPoints*this.pixelsPerPoint: " + this.nbrPoints*this.pixelsPerPoint);
					if(xP >= 0 && xP < this.nbrPoints*this.pixelsPerPoint){
						var handleIndex:Number = Math.round(xP/(this.pointsPerHandle*this.pixelsPerPoint));
						//var handleIndex:int = this.myModel.handleLocation/this.pointsPerHandle;
						//trace("handleIndex: " + handleIndex + "   localhandleIndex:" + localHandleIndex);
						this.myModel.handleLocation = handleIndex*this.pointsPerHandle;
						this.handles[handleIndex].y = yP;
						var xValueOfHandle:int = handleIndex*this.pointsPerHandle;
						//trace("xValueOfHandle: " + xValueOfHandle);
						this.curve_arr[xValueOfHandle] = -yP/this.yScale;//-target.y/localRef.yScale;
						this.myModel.alterCurve();
						evt.updateAfterEvent();
					}//end of if
				}else if(this.someHandleSelected && currentMode == "Freeform"){
					xC = xP/this.pixelsPerPoint;
					this.myModel.drawFreeform(xC,-yP/this.yScale);
					//trace("xP: "+xP);
					evt.updateAfterEvent();
					//trace("got alterMode: "+this.myModel.getAlterMode())
				}
		}//moveMouse()
		
		public function setViewHeight(vHeight:Number):void{
			this.viewHeight = vHeight;
			this.originY = this.viewHeight/2;
			this.curve.y = this.originY;
			this.drawGrid();
			this.drawAxes();
			this.positionAxisLabels();
			this.positionZoomControls();
			this.update();
		}
		
		public function positionAxisLabels():void{
			this.xAxisLabel.x = this.originX + this.nbrPoints*this.pixelsPerPoint - 25;
			this.xAxisLabel.y = this.originY; 
			this.yAxisLabel.x = this.originX + 5;
			this.yAxisLabel.y = 30;
			if(this.curveType == "integral"){
				this.yAxisIntegralLabel.x = this.originX + 10;
				this.yAxisIntegralLabel.y = 20;
			}
		}//positionAxisLabels()
		
		public function drawGrid():void{
			var viewWidth:Number = this.nbrPoints*this.pixelsPerPoint;
			var delX:Number;// = viewWidth/40;
			var delY:Number;// = delX;//*this.yScale;//this.viewHeight/10;
			var nY:Number;// = Math.floor(viewHeight/(2*delY));
			var nX:Number;// = Math.floor(viewWidth/(2*delX));
			var localRef:Object = this;
			this.grid.graphics.clear();
			setupScale(40, 1);
			drawNow();
			//setupScale(20,2);
			//drawNow();
			function setupScale(nbrOfDelX:Number, lineWidth:Number):void{
				localRef.grid.graphics.lineStyle(lineWidth, 0xffcc00);
				delX = viewWidth/nbrOfDelX;
				delY = delX;//*this.yScale;//this.viewHeight/10;
				nY = Math.floor(viewHeight/(2*delY));
				nX = Math.floor(viewWidth/(2*delX));
			}//end of setupScale
			
			function drawNow():void{
				with(localRef.grid.graphics){
					//clear();
					//lineStyle(1, 0xffcc00);
					
					//draw horizontal grid lines
					for (var i:int = -nY; i <= nY; i++){
						moveTo(localRef.originX, localRef.originY + i*delY);
						lineTo(localRef.originX + 2*nX*delX, localRef.originY + i*delY);
					}
					//draw vertical grid lines
					for (var j:int = 0; j <= 2*nX; j++){
						moveTo(localRef.originX + j*delX, localRef.originY - nY*delY);
						lineTo(localRef.originX + j*delX, localRef.originY + nY*delY);
					}
				}//end of with()
			}//end of drawNow
		}
		
		public function drawAxes():void{
			with(this.axes.graphics){
				//draw x-axis
				clear();
				lineStyle(1, 0x000000);
				moveTo(this.originX, this.originY);
				lineTo(this.originX + this.nbrPoints*this.pixelsPerPoint, this.originY);
				//draw y-axis
				moveTo(this.originX, 20);
				lineTo(this.originX, this.viewHeight - 20);
			}
		}//end of drawAxes()
		
		public function drawCanvasBorder():void{
			with(this.canvasGraphics){
				lineStyle(2, 0x0000ff);
				moveTo(0,0);
				lineTo(this.stage.stageWidth, 0);
				lineTo(this.stage.stageWidth, this.viewHeight);
				lineTo(0, this.viewHeight);
				lineTo(0,0);
			}
		}//end of drawCanvasBorder
		
		public function update():void{
			//this.handles[0].y = this.originY - this.yScale*this.curve_arr[0];
			this.handles[0].y = -this.yScale*this.curve_arr[0];
			with(this.curveGraphics){
				clear();
				lineStyle(2.5, this.curveColor);
				moveTo(this.originX, this.handles[0].y);
				//trace("this.handles[0].y : "+this.handles[0].y);
			}
			for (var i:Number = 1; i < this.nbrHandles; i++){
				//this.handles[i].y = this.originY - this.yScale*this.curve_arr[i*this.pointsPerHandle];
				this.handles[i].y = -this.yScale*this.curve_arr[i*this.pointsPerHandle - 1];
			}
			for (var j:Number = 1; j < this.nbrPoints; j++){
				var xj:Number = this.originX + j*this.pixelsPerPoint - this.pixelsPerPoint*this.xOffset;
				//var yj:Number = this.originY - this.yScale*this.curve_arr[j];
				var yj:Number = -this.yScale*this.curve_arr[j];
				//trace("xj: "+xj+"     yj:"+yj+"     this.handles[this.nbrHandles -1].x: "+this.handles[this.nbrHandles -1].x);
				this.curveGraphics.lineTo(xj, yj); 
			}
			
		}//end of update()
		
	}//end of class
}//end of package
		
		//following function is obsolete, used when mousedrag affect y-coordinate only of initially clicked handle
		/*public function makeHandleDraggableOldVersion(target:Sprite):void{
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			target.addEventListener(MouseEvent.MOUSE_OVER, onTarget);
			target.addEventListener(MouseEvent.MOUSE_OUT, offTarget);
			//target.addEventListener(MouseEvent.ROLL_OUT, rollOffTarget);
			//target.addEventListener(MouseEvent.ROLL_OVER, rollOverTarget);
			var localRef:Object = this;
			var handleIndex:int;
			//var clickOffset:Point;
			var aHandSelected:Boolean;
			function startTargetDrag(evt:MouseEvent):void{
				handleIndex = evt.currentTarget.index;
				localRef.myModel.handleLocation = handleIndex*localRef.pointsPerHandle;
				//clickOffset = new Point(evt.localX, evt.localY);
				aHandSelected = true;
				//trace("clickOffset.y:"+clickOffset.y);
				//trace(localRef.myModel.handleLocation);
				localRef.myModel.copyCurrentY();
				//localRef.update();
				//trace("handle.y:"+target.y);
				evt.target.alpha = alphaOnValue;
				evt.target.setSelected(true);
				localRef.someHandleSelected = true;
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				localRef.handles[handleIndex].setSelected(false);
				localRef.handles[handleIndex].alpha = alphaOffValue;
				localRef.someHandleSelected = false;
				//clickOffset = null;
				aHandSelected = false;
			}
			
			function dragTarget(evt:MouseEvent):void{
				if(aHandSelected){//clickOffset != null){  //if dragging
					target.y = (mouseY - localRef.originY);// - clickOffset.y;
					var xValueOfHandle:int = handleIndex*localRef.pointsPerHandle;
					localRef.curve_arr[xValueOfHandle] = -target.y/localRef.yScale;
					localRef.myModel.alterCurve();
					evt.updateAfterEvent();
				}
			}
			function onTarget(evt:MouseEvent):void{
				if(!localRef.someHandleSelected){
					evt.target.alpha = alphaOnValue;
					//new code
					//handleIndex = evt.currentTarget.index;
					//localRef.myModel.handleLocation = handleIndex*localRef.pointsPerHandle;
					//clickOffset = new Point(evt.localX, evt.localY);
					//end of new code
				}
			}
			function offTarget(evt:MouseEvent):void{
				if(!localRef.someHandleSelected){
					evt.target.alpha = alphaOffValue;
					//trace("evt.target.index" + evt.target.index);
//					trace("evt.currentTarget.index" + evt.currentTarget.index);
//					trace("evt.relatedObject.index" + evt.relatedObject);
				}
			}//end of offTarget()
			
			function rollOffTarget(evt:MouseEvent):void{
				trace("rolled off " + evt.target.index);
			}
			function rollOverTarget(evt:MouseEvent):void{
				trace("rolled onto " + evt.target.index);
			}
		}//end of makeHandleDraggableOld()*/
		


//obsolete code

/*
		public function smoothCurve(cntrPt:int, range:Number):void{
			var P:Number;
			for (var i:Number = 0; i < this.nbrHandles; i++){
				var dist = Math.abs(i - cntrPt);
				if(i != cntrPt){
					//P = 0.9/Math.pow(dist,1.8);
					P = Math.exp(-dist/(10));
					
					this.newY[i] = P*currentY[cntrPt] + (1-P)*oldY[i];
					this.handles[i].y = this.originY - newY[i];
				}
				newY[cntrPt] = currentY[cntrPt];
			}
		}//end of smoothCurve()
		
		
		public function doubleSmoothCurve(cntrPt:int, range:Number):void{
			var P:Number;
			var delY:Number = Math.abs(this.newY[cntrPt] - this.oldY[cntrPt]);
			//trace("delY:"+delY);
			for (var i:Number = 0; i < this.nbrHandles; i++){
				var dist = Math.abs(i - cntrPt);
				if(i != cntrPt){
					//P = 0.9/Math.pow(dist,1.8);
					P = Math.exp(-dist/(range*Math.log(delY+1)));
					this.newY[i] = P*currentY[cntrPt] + (1-P)*oldY[i];
					//this.newY[i] = P*currentY[cntrPt];
				}
				newY[cntrPt] = currentY[cntrPt];
				this.newY[i] = P*currentY[cntrPt] + (1-P)*newY[i];
				this.myModel.y_arr[i] = this.newY[i];
				//this.handles[i].y = this.originY - newY[i];
			}//end of for loop
		}//end of doubleSmoothCurve()
		
		
		
		//smooth curve around chosen point (center point) with given range
		public function smoothCurve1(cntrPt:int, range:Number):void{ 
			var sum:Number;  //scratch sum for computing average
			var N:Number;	 //number of points averaged so far
			var avg:Number;
			var avgY:Array = new Array(this.nbrHandles);
			//compute smoothed points
			for (var i:int = cntrPt - range; i <= cntrPt + range; i++){
				sum = N = 0;
				if(i != cntrPt && i >= 0 && i < this.nbrHandles){
					for(var j:int = i - range; j <= i + range; j++){
						if(j >= 0 && j < this.nbrHandles){
							var distance:Number = Math.max(Math.abs(j - i),1);
							sum += this.currentY[j]/(distance*distance);
							N += 1;
							//trace("i:"+i+"  j: " + j + "   distance: " + distance + "  N:" + N + "  sum:"+ sum);
						}//end of if j
					}//end of for j
					avg = sum/N;
					avgY[i] = avg;
					//trace("cntrPt:"+cntrPt+"   i:"+i+"  avg:"+avg);
					//trace("i: " + i + "    avgY: " + avg);
				}//end of if i
			}//end of outermost for i loop
			//update smoothed points
			for (var k:int = cntrPt - range; k <= cntrPt + range; k++){
				if(k != cntrPt && k >= 0 && k < this.nbrHandles){
					this.currentY[k] = avgY[k]
					this.handles[k].y = this.originY - avgY[k];
				}
			}//end of for k
			this.drawCurve();
		}//end of smoothCurve()
		
		
		
		
		
		*/