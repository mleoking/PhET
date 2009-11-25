//View and Controller of ball in TableView
//BallImage has 5 sprite layers,
// 1: colored ball, on bottom, not grabbable
// 2: velocity arrow, not grabbable
// 3: textField label
// 4: transparent disk for dragging ball, to set position
// 5: transparent arrow head, for dragging velocity arrow, to set velocity, on top

package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	import flash.geom.Point;
	
	public class BallImage extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var myBall:Ball;
		var ballIndex:int;			//index labels ball 1, 2, 3,  
		var pixelsPerMeter:int;
		var ballBody:Sprite;
		var arrowImage:Arrow;
		var ballHandle:Sprite;
		var arrowHeadHandle:Sprite;
		var tFormat:TextFormat;
		var tField:TextField;
		
		public function BallImage(myModel:Model, indx:int, myTableView:TableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.ballIndex = indx;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.myBall = this.myModel.ball_arr[this.ballIndex];
			this.ballBody = new Sprite();
			this.arrowImage = new Arrow(indx);
			this.ballHandle = new Sprite();
			this.arrowHeadHandle = new Sprite();
			
			//this.canvas = new Sprite();
			//this.addChild(this.canvas);
			
			this.tField = new TextField();
			var ballNbr:String = String(1 + this.ballIndex);
			this.tField.text = ballNbr;
			this.tField.selectable = false;
			this.tFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0xffffff;
			tFormat.size = 20;
			this.tField.setTextFormat(tFormat);
			this.setLayerDepths();
			this.drawLayer1();
			this.drawLayer2();
			this.drawLayer3();
			this.drawLayer4();
			this.drawLayer5();
			this.makeBallDraggable();
			this.makeArrowDraggable();
		}//end of constructor
		
		private function setLayerDepths():void{
			this.myTableView.canvas.addChild(this);
			this.addChild(this.ballBody);
			this.addChild(this.arrowImage);
			this.addChild(this.tField);
			this.addChild(this.ballHandle);
			this.addChild(this.arrowHeadHandle);
		}
		
		public function drawLayer1():void{
			var g:Graphics = this.ballBody.graphics;
			var currentColor:uint = this.myTableView.ballColor_arr[this.ballIndex];
			var r:Number = this.myBall.radius;
			g.clear();
			g.lineStyle(1,0x000000);
			g.beginFill(currentColor);
			g.drawCircle(0,0,r*pixelsPerMeter);
			g.endFill();
			
			
		}//end of drawLayer1()
		
		public function drawLayer2():void{
			this.arrowImage.setArrow(this.myModel.ball_arr[this.ballIndex].velocity);
			//trace("velocityY: "+this.myModel.ball_arr[this.ballIndex].velocity.getY());
			this.arrowImage.setText("");
		}//end of drawLayer2()
		
		public function drawLayer3():void{
			this.tField.autoSize = TextFieldAutoSize.CENTER;
			this.tField.x = -this.tField.width/2;
			this.tField.y = -this.tField.height/2;
		}//end of drawLayer3();
		
		public function drawLayer4():void{	//ballHandle
			var g:Graphics = this.ballHandle.graphics;
			var currentColor:uint = 0xffff00;
			var alpha1:Number = 0;
			var r:Number = this.myBall.radius;
			g.clear();
			g.beginFill(currentColor, alpha1);
			g.drawCircle(0,0,r*pixelsPerMeter);
			g.endFill();
		}//end of drawLayer4()
		
		public function drawLayer5():void{  //arrowHeadHandle
			var g:Graphics = this.arrowHeadHandle.graphics;
			var currentColor:uint = 0xffffff;
			var alpha1:Number = 0.4;
			var r:Number = 5;
			g.clear();
			g.beginFill(currentColor, alpha1);
			g.drawCircle(0,0,r);
			g.endFill();
			this.arrowHeadHandle.x = this.arrowImage.getTipX();
			this.arrowHeadHandle.y = this.arrowImage.getTipY();
		}//end of drawLayer5()
		
		public function makeBallDraggable():void{
			var target:Sprite = this.ballHandle;
			var thisBallImage:BallImage = this;
			target.buttonMode = true;
			var indx:int = ballIndex;
			var modelRef:Model = this.myModel;
			var H:Number = modelRef.borderHeight;
			
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			target.addEventListener(MouseEvent.MOUSE_OVER, showPosition);
			target.addEventListener(MouseEvent.MOUSE_OUT, unshowPosition);
			var theStage:Object = thisBallImage.myTableView.canvas;//target.parent;
			var clickOffset:Point;
			function startTargetDrag(evt:MouseEvent):void{	
				//problem with localX, localY if sprite is rotated.
				clickOffset = new Point(evt.localX, evt.localY);
				//trace("evt.localX: "+evt.localX);
				//trace("evt.localY: "+evt.localY);
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				clickOffset = null;
			}
			function dragTarget(evt:MouseEvent):void{
				if(clickOffset != null){  //if dragging
					//trace("theStage.mouseX: "+theStage.mouseX);
					//trace("theStage.mouseY: "+theStage.mouseY);
					thisBallImage.x = theStage.mouseX - clickOffset.x;
					thisBallImage.y = theStage.mouseY - clickOffset.y;
					var ballX:Number = thisBallImage.x/pixelsPerMeter;
					var ballY:Number = H - thisBallImage.y/pixelsPerMeter;
					//trace("ballY: "+ballY);
					modelRef.ball_arr[indx].position.setXY(ballX, ballY);
					if(modelRef.atInitialConfig){
						modelRef.initPos[indx].setXY(ballX, ballY);
					}
					modelRef.updateViews();
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
			
			function showPosition(evt:MouseEvent):void{
				trace("BallImage rollover ballhandle" + indx);
			}
			
			function unshowPosition(evt:MouseEvent):void{
				trace("BallImage rollout ballhandle" + indx);
			}
			
		}//end makeBallDraggable()
		
		public function makeArrowDraggable():void{
			var target:Sprite = this.arrowHeadHandle;
			var thisBallImage:BallImage = this;
			target.buttonMode = true;
			var indx:int = ballIndex;
			var modelRef:Model = this.myModel;
			var H:Number = modelRef.borderHeight;
			
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			target.addEventListener(MouseEvent.MOUSE_OVER, showVelocity);
			target.addEventListener(MouseEvent.MOUSE_OUT, unshowVelocity);
			var theStage:Object = thisBallImage;//target.parent;
			var clickOffset:Point;
			function startTargetDrag(evt:MouseEvent):void{	
				//problem with localX, localY if sprite is rotated.
				clickOffset = new Point(evt.localX, evt.localY);
				//trace("evt.localX: "+evt.localX);
				//trace("evt.localY: "+evt.localY);
			}
			function stopTargetDrag(evt:MouseEvent):void{
				//trace("stop dragging");
				clickOffset = null;
			}
			function dragTarget(evt:MouseEvent):void{
				if(clickOffset != null){  //if dragging
					//trace("theStage.mouseX: "+theStage.mouseX);
					//trace("theStage.mouseY: "+theStage.mouseY);
					target.x = theStage.mouseX - clickOffset.x;
					target.y = theStage.mouseY - clickOffset.y;
					var velocityX:Number = target.x/thisBallImage.arrowImage.scale;
					var velocityY:Number = -target.y/thisBallImage.arrowImage.scale;
					//trace("velocityX: "+velocityX+"    velocityY: "+velocityY);
					modelRef.ball_arr[indx].velocity.setXY(velocityX, velocityY);
					if(modelRef.atInitialConfig){
						modelRef.initVel[indx].setXY(velocityX, velocityY);
					}
					modelRef.updateViews();
					thisBallImage.arrowImage.setArrow(modelRef.ball_arr[indx].velocity);
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
			function showVelocity(evt:MouseEvent):void{
				trace("showVelocity rollover " +indx);
			}
			function unshowVelocity(evt:MouseEvent):void{
				trace("showVelocity rollout" + indx);
			}
		}//end of makeArrowDraggable()
		
		public function showArrow(tOrF:Boolean):void{
			if(tOrF){
				this.arrowImage.visible = true;
			}else{
				this.arrowImage.visible = false;
			}
		}//end showArrow()
		
		public function updateVelocityArrow():void{
			var vel:TwoVector = this.myModel.ball_arr[this.ballIndex].velocity;
			//this.myModel.updateViews();
			this.arrowImage.setArrow(vel);
			var scaleFactor:Number = this.arrowImage.scale;
			this.arrowHeadHandle.x = scaleFactor*vel.getX();
			this.arrowHeadHandle.y = -scaleFactor*vel.getY();
		}
		
	}//end of class
}//end of package