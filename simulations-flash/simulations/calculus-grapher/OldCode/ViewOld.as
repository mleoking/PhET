package{
	import flash.events.*;
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.geom.*;
	import fl.motion.Color;
	
	public class View extends Sprite{
		
		var myModel:Model;
		var canvas:Sprite;
		var handles:Array;
		var currentY:Array;
		var oldY:Array;			//copy of currentY array needed for smoothing
		var newY:Array;			//copy of currentY array needed for smoothing
		var curve:Sprite;		//displayed curve of function
		var range:Number;   	//range of smoothing algorithm
		var curveGraphics:Graphics;
		
		var nbrHandles:int;
		var handleSpacing:Number;   //in pixels
		var someHandleSelected:Boolean;  //true if mouse is dragging a handle
		var originX:Number;		//x-position of origin in pixels
		var originY:Number;		//y-position of origin in pixels
		//var handleColor:Color;	//to control color of handles
		
		public function View(myModel:Model){
			
			this.myModel = myModel;
			this.canvas = new Sprite();
			this.curve = new Sprite();
			this.nbrHandles = myModel.nbrPoints;
			this.handleSpacing = 6;
			this.someHandleSelected = false;
			//this.handleColor = new Color();
			this.originX = 50;
			this.originY = 200;
			this.handles = new Array(nbrHandles);
			this.currentY = new Array(nbrHandles);
			this.oldY = new Array(nbrHandles);
			this.newY = new Array(nbrHandles);
			this.range = 1;
			addChild(curve);
			addChild(canvas);		
			
			//trace("view instantiated");
			
		}//end of constructor
		
		public function initialize():void{
			this.originY = stage.stageHeight/2;
			//create array of handles for grabbing points on curve
			for (var i:int = 0; i < nbrHandles; i++){
				this.handles[i] = new Handle(i);
				this.canvas.addChild(handles[i]);
				handles[i].y = this.originY;   //class Handle extends Sprite, so it has x and y variables
				handles[i].x = this.originX + i*this.handleSpacing;
				this.makeHandleDraggable(handles[i]);
				this.currentY[i] = this.originY - handles[i].y;
				myModel.y_arr[i] = this.currentY[i];
				this.oldY[i] = this.currentY[i];
				this.newY[i] = this.currentY[i];
			}
			
			this.curveGraphics = this.curve.graphics;
			this.curveGraphics.lineStyle(2, 0x0000ff);
			this.drawCurve();
		}
		
		public function makeHandleDraggable(target:Sprite):void{
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			target.addEventListener(MouseEvent.MOUSE_OVER, onTarget);
			target.addEventListener(MouseEvent.MOUSE_OUT, offTarget);
			var localRef:Object = this;
			var handleIndex:int;
			var clickOffset:Point;
			function startTargetDrag(evt:MouseEvent):void{
				handleIndex = evt.currentTarget.index;
				localRef.copyCurrentY();
				localRef.doubleSmoothCurve(handleIndex, localRef.range);
				localRef.drawHandles();
				localRef.drawCurve();
				clickOffset = new Point(evt.localX, evt.localY);
				evt.target.alpha = 1.0;
				evt.target.setSelected(true);
				localRef.someHandleSelected = true;
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				localRef.handles[handleIndex].setSelected(false);
				localRef.handles[handleIndex].alpha = 0.1;
				localRef.someHandleSelected = false;
				clickOffset = null;
				localRef.updateCurrentY();
			}
			
			function dragTarget(evt:MouseEvent):void{
				if(clickOffset != null){  //if dragging
					target.y = mouseY - clickOffset.y;
					localRef.currentY[handleIndex] = localRef.originY - target.y;
					localRef.doubleSmoothCurve(handleIndex, localRef.range);
					localRef.drawHandles();
					localRef.drawCurve();
					evt.updateAfterEvent();
				}
			}
			function onTarget(evt:MouseEvent):void{
				if(!localRef.someHandleSelected){
					evt.target.alpha = 1.0;
				}
			}
			function offTarget(evt:MouseEvent):void{
				if(!localRef.someHandleSelected){
					evt.target.alpha = 0.1;
				}
			}

		}//end of makeHandleDraggable()
		
		public function drawCurve():void{
			//trace("drawCurve called");
			with(this.curveGraphics){
				clear();
				lineStyle(2, 0x0000ff);
				moveTo(this.originX, this.handles[0].y);
				
				//trace("originY:"+this.originY + "   this.handles[0].y:" + this.handles[0].y);
			}
			for(var i:int = 1; i < nbrHandles ; i++){
				this.curveGraphics.lineTo(handles[i].x, handles[i].y);
			}
		}//end of drawCurve()
		
		public function drawHandles():void{
			for (var i:Number = 0; i < this.nbrHandles; i++){
				this.handles[i].y = this.originY - this.myModel.y_arr[i];
			}
		}//end of drawHandles();
		

		
		public function copyCurrentY():void{
			this.oldY = this.currentY.concat();  //concat function clones array
		}//end of copyCurrentY
		
		public function updateCurrentY():void{
			this.currentY = this.newY.concat();
		}
		
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
		
	}//end of class
}//end of package

//obsolete code
		//smooth curve around chosen point (center point) with given range
/*		public function smoothCurve1(cntrPt:int, range:Number):void{ 
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
		}//end of smoothCurve()*/