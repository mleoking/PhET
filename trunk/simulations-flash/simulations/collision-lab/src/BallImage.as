//View and Controller of ball in TableView
//BallImage has 5 sprite layers,
// 1: colored ball, on bottom, not grabbable, has position textField as Child
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
		var tFormat:TextFormat;				//format for ball label text
		var tFormat2:TextFormat;			//format for ball position and velocity readouts
		var tFieldBallNbr:TextField;		//label = ball number
		var tFieldPosition:TextField;		//label showing x, y coords of ball during dragging
		var tFieldVelocity:TextField;		//label showing v_x, v_y during dragging
		var xEqString:String;				//"x = "  All text must be programmatically set for internationalization
		var yEqString:String;				//"y = "
		
		
		public function BallImage(myModel:Model, indx:int, myTableView:TableView){
			this.myModel = myModel;
			this.myTableView = myTableView;
			this.ballIndex = indx;
			this.pixelsPerMeter = this.myTableView.pixelsPerMeter;
			this.myBall = this.myModel.ball_arr[this.ballIndex];
			this.ballBody = new Sprite();
			this.arrowImage = new Arrow(indx);
			this.arrowImage.setColor(0x00ff00);
			this.ballHandle = new Sprite();
			this.arrowHeadHandle = new Sprite();
			
			//this.canvas = new Sprite();
			//this.addChild(this.canvas);
			
			this.tFieldBallNbr = new TextField();
			this.tFieldPosition = new TextField();
			this.tFieldVelocity = new TextField();
			var ballNbr:String = String(1 + this.ballIndex);
			this.tFieldBallNbr.text = ballNbr;
			this.xEqString = "x = ";
			this.yEqString = "y = ";
			this.tFieldBallNbr.selectable = false;
			this.tFieldPosition.selectable = false;
			this.tFieldVelocity.selectable = false;
			this.tFieldPosition.visible = false;
			this.tFieldPosition.multiline = true;
			//this.tFieldPosition.border = true;
			this.tFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0xffffff;
			tFormat.size = 20;
			this.tFormat2 = new TextFormat();
			tFormat2.bold = true;
			tFormat2.font = "Arial";
			tFormat2.color = 0x000000;
			tFormat2.size = 14;
			this.tFieldBallNbr.setTextFormat(tFormat);
			this.tFieldPosition.defaultTextFormat = tFormat2;
			this.tFieldVelocity.defaultTextFormat = tFormat2;
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
			this.addChild(this.tFieldPosition);
			this.addChild(this.arrowImage);
			this.addChild(this.tFieldBallNbr);
			this.addChild(this.ballHandle);
			this.addChild(this.arrowHeadHandle);
		}
		
		public function drawLayer1():void{
			var g:Graphics = this.ballBody.graphics;
			var currentColor:uint = this.myTableView.ballColor_arr[this.ballIndex];
			var r:Number = this.myBall.getRadius();
			g.clear();
			g.lineStyle(1,0x000000,1,false);
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
			this.tFieldBallNbr.autoSize = TextFieldAutoSize.CENTER;
			this.tFieldBallNbr.x = -this.tFieldBallNbr.width/2;
			this.tFieldBallNbr.y = -this.tFieldBallNbr.height/2;
		}//end of drawLayer3();
		
		public function drawLayer4():void{	//ballHandle
			var g:Graphics = this.ballHandle.graphics;
			var currentColor:uint = 0xffff00;
			var alpha1:Number = 0;
			var r:Number = this.myBall.getRadius();
			g.clear();
			g.beginFill(currentColor, alpha1);
			g.drawCircle(0,0,r*pixelsPerMeter);
			g.endFill();
		}//end of drawLayer4()
		
		public function drawLayer5():void{  //arrowHeadHandle
			var g:Graphics = this.arrowHeadHandle.graphics;
			var currentColor:uint = 0xffffff;
			var alpha1:Number = 0;
			var r:Number = 10;
			g.clear();
			g.beginFill(currentColor, alpha1);
			g.drawCircle(0,0,r);
			g.endFill();
			this.arrowHeadHandle.x = this.arrowImage.getHeadCenterX();
			this.arrowHeadHandle.y = this.arrowImage.getHeadCenterY();
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
			target.addEventListener(MouseEvent.MOUSE_OVER, highlightPositionTextFields);
			target.addEventListener(MouseEvent.MOUSE_OUT, unHighlightPositionTextFields);
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
				thisBallImage.myModel.separateAllBalls();
			}
			function dragTarget(evt:MouseEvent):void{
				if(clickOffset != null){  //if dragging
					//adjust x position
					thisBallImage.x = theStage.mouseX - clickOffset.x;
					var ballX:Number = thisBallImage.x/pixelsPerMeter;
					modelRef.setX(indx, ballX);
					//if not in 1DMode, adjust y position
					if(!thisBallImage.myModel.oneDMode){
						thisBallImage.y = theStage.mouseY - clickOffset.y;
						var ballY:Number = H - thisBallImage.y/pixelsPerMeter;
						modelRef.setY(indx, ballY);
					}
					if(modelRef.atInitialConfig){
						modelRef.initPos[indx].setXY(ballX, ballY);
					}
					modelRef.updateViews();
					//thisBallImage.updateTFieldPosition();
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
			
			//following produced dataTable = null  maybe due to startup order?
			//var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;
			
			function highlightPositionTextFields():void{
				//trace("BallImage.myTableView.myMainView.myDataTable:"+thisBallImage.myTableView.myMainView.myDataTable);
				var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;
				//dataTable.text_arr[thisBallImage.ballIndex+1][2].background = true;
				//dataTable.text_arr[thisBallImage.ballIndex+1][3].background = true;
				dataTable.text_arr[thisBallImage.ballIndex+1][2].backgroundColor = 0xffff33;
				dataTable.text_arr[thisBallImage.ballIndex+1][3].backgroundColor = 0xffff33;
			}
			function unHighlightPositionTextFields():void{
				var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;
				dataTable.text_arr[thisBallImage.ballIndex+1][2].backgroundColor = 0xffffff;
				dataTable.text_arr[thisBallImage.ballIndex+1][3].backgroundColor = 0xffffff;
				//dataTable.text_arr[thisBallImage.ballIndex+1][2].background = false;
				//dataTable.text_arr[thisBallImage.ballIndex+1][3].background = false;
				
			}
			
			//unused
			function showPosition(evt:MouseEvent):void{
				thisBallImage.tFieldPosition.visible = true;
				thisBallImage.updateTFieldPosition();
				//trace("BallImage rollover ballhandle" + indx);
			}
			//unused
			function unshowPosition(evt:MouseEvent):void{
				thisBallImage.tFieldPosition.visible = false;
				//trace("BallImage rollout ballhandle" + indx);
			}
			
		}//end makeBallDraggable()
		
		//unused
		private function updateTFieldPosition():void{
			var pos:TwoVector = this.myModel.ball_arr[this.ballIndex].position;
			var xPos = Util.round(pos.getX(),2);;
			var yPos = Util.round(pos.getY(),2);
			this.tFieldPosition.text = this.xEqString + xPos + "\n" + this.yEqString + yPos;
			this.tFieldPosition.x = 0.6*this.ballBody.width;
			this.tFieldPosition.y = -15;
		}
		
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
					//adjust x-component of velocity
					target.x = theStage.mouseX;// - clickOffset.x;
					var velocityX:Number = target.x/thisBallImage.arrowImage.scale;
					modelRef.setVX(indx, velocityX);
					//if not in 1DMode, set y-component of velocity
					if(!modelRef.oneDMode){
						target.y = theStage.mouseY;// - clickOffset.y;
						var velocityY:Number = -target.y/thisBallImage.arrowImage.scale;
						modelRef.setVY(indx, velocityY);
					}
					//trace("velocityX: "+velocityX+"    velocityY: "+velocityY);
					//modelRef.ball_arr[indx].velocity.setXY(velocityX, velocityY);
					if(modelRef.atInitialConfig){
						modelRef.initVel[indx].setXY(velocityX, velocityY);
					}
					modelRef.updateViews();
					thisBallImage.arrowImage.setArrow(modelRef.ball_arr[indx].velocity);
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
			function showVelocity(evt:MouseEvent):void{
				//trace("showVelocity rollover " +indx);
				var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;
				//dataTable.text_arr[thisBallImage.ballIndex+1][4].background = true;
				//dataTable.text_arr[thisBallImage.ballIndex+1][5].background = true;
				dataTable.text_arr[thisBallImage.ballIndex+1][4].backgroundColor = 0xffff33;
				dataTable.text_arr[thisBallImage.ballIndex+1][5].backgroundColor = 0xffff33;
			}
			function unshowVelocity(evt:MouseEvent):void{
				//trace("showVelocity rollout" + indx);
				var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;
				dataTable.text_arr[thisBallImage.ballIndex+1][4].backgroundColor = 0xffffff;
				dataTable.text_arr[thisBallImage.ballIndex+1][5].backgroundColor = 0xffffff;
				//dataTable.text_arr[thisBallImage.ballIndex+1][4].background = false;
				//dataTable.text_arr[thisBallImage.ballIndex+1][5].background = false;
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