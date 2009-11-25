package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	import flash.geom.*;
	
	public class TableView extends Sprite{
		var myModel:Model;
		var myMainView:MainView;			//mediator and container of views
		var canvas:Sprite;		//background on which everything is placed
		var myTrajectories:Trajectories;	//Sprite showing trajectories (paths) of balls
		var showingPaths:Boolean;			//true if paths are shown
		var playButtons:PlayPauseButtons;	//class to hold library symbol
		var border:Sprite;		//reflecting border
		var borderColor:uint;	//color of border 0xrrggbb
		var timeText:TextField;	//label containing current time
		var pixelsPerMeter:int;	//scale of view
		var ball_arr:Array;		//array of ball images
		//var ballImageTest:BallImage; //for testing BallImage Class
		var ballLabels:Array;	//array of ball labels: 1, 2, 3, ...
		var ballColor_arr:Array;	//array of uint for colors of balls
		var xOffset:Number;		//x of upper left corner of canvas
		var yOffset:Number;		//y of upper left corner of canvas
		
		public function TableView(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.canvas = new Sprite();
			this.myMainView.addChild(this);
			this.addChild(this.canvas);
			this.xOffset = 20;
			this.yOffset = 60;
			this.canvas.x = xOffset;
			this.canvas.y = yOffset;
			this.playButtons = new PlayPauseButtons(this.myModel);
			this.canvas.addChild(this.playButtons);
			this.myModel.registerView(this);
			this.pixelsPerMeter = 200;
			this.showingPaths = false;
			this.myTrajectories = new Trajectories(this.myModel, this);
			this.canvas.addChild(this.myTrajectories);
			this.drawBorder();
			this.makeTimeLabel();
			this.ballColor_arr = new Array(10);  //start with 10 colors
			this.createBallColors();
			//this.createBallImages2();
			this.createBallImages();
			
			this.update();
			//this.ballImageTest = new BallImage(this.myModel, 2, this);
			//this.myModel.startMotion();
		}//end of constructor
		
		public function drawBorder():void{
			var W:Number = this.myModel.borderWidth * this.pixelsPerMeter;
			var H:Number = this.myModel.borderHeight * this.pixelsPerMeter;
			var thickness:Number = 6;  //border thickness in pixels
			var del:Number = thickness/2;
			//trace("width: "+W+"    height: "+H);
			with(this.canvas.graphics){
				clear();
				lineStyle(thickness,0xFF0000);
				var x0:Number = 0;
				var y0:Number = 0;
				beginFill(0x77ff77);
				moveTo(-del, -del);
				lineTo(W+del, -del);
				lineTo(W+del, +H+del);
				lineTo(-del, +H);
				lineTo(-del, -del);
				endFill();
			}
			//position playButtons
			this.playButtons.x = W/2;
			this.playButtons.y = H + this.playButtons.height/2;
		}//end of drawBorder();
		
		public function makeTimeLabel():void{
			this.timeText = new TextField();
			this.timeText.text = "Time = ";
			this.timeText.selectable = false;
			this.timeText.autoSize = TextFieldAutoSize.LEFT;
			var tFormat:TextFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0x000000;
			tFormat.size = 16;
			this.timeText.defaultTextFormat = tFormat;
			//this.timeText.setTextFormat(tFormat);
			this.canvas.addChild(this.timeText);
			var W:Number = this.myModel.borderWidth * this.pixelsPerMeter;
			var H:Number = this.myModel.borderHeight * this.pixelsPerMeter;
			this.timeText.x = W - 2.5*this.timeText.width;
			this.timeText.y = H + 10;
		}
		
		
		public function createBallColors():void{
			this.ballColor_arr[0] = 0xff0000;
			this.ballColor_arr[1] = 0x009900;
			this.ballColor_arr[2] = 0x0000ff;
			this.ballColor_arr[3] = 0xff00ff;
			this.ballColor_arr[4] = 0xffff00;
			
		}
		
		//called once, at startup
		public function createBallImages():void{
			var maxNbrBalls:int = this.myModel.maxNbrBalls;
			this.ball_arr = new Array(maxNbrBalls);
			for(var i:int = 0; i < maxNbrBalls; i++){
				this.ball_arr[i] = new BallImage(this.myModel, i, this);
				ball_arr[i].x = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				ball_arr[i].y = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getY();
			}//end for
			this.update(); //to make extra balls invisible
		}//end of createBallImages()
		
		public function showArrowsOnBallImages(tOrF:Boolean):void{
			var maxNbrBalls:int = this.myModel.maxNbrBalls;
			for(var i:int = 0; i < maxNbrBalls; i++){
				if(tOrF){
					this.ball_arr[i].showArrow(true);
				}else{
					this.ball_arr[i].showArrow(false);
				}
			}
		}//end showArrowsOnBallImages()
		
/*
		public function createBallImages2():void{
			var nbrBalls:int = this.myModel.nbrBalls;
			this.ball_arr = new Array(nbrBalls);
			this.ballLabels = new Array(nbrBalls);
			var tFormat:TextFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0xffffff;
			tFormat.size = 20;
			
			for(var i:int = 0; i < nbrBalls; i++){
				this.ball_arr[i] = new Sprite();
				this.ballLabels[i] = new TextField();
				this.ballLabels[i].text = i+1;		//number is cast to String automatically
				this.ballLabels[i].selectable = false;
				this.ballLabels[i].setTextFormat(tFormat);
				var radius:Number = this.pixelsPerMeter*this.myModel.ball_arr[i].radius;
				//draw ball
				with(this.ball_arr[i].graphics){
					clear();
					var currentColor:uint = this.ballColor_arr[i];
					beginFill(currentColor);
					lineStyle(1, currentColor);
					drawCircle(0,0,radius);
					endFill();
				}//with
				this.ball_arr[i].addChild(this.ballLabels[i]);
				ball_arr[i].x = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				ball_arr[i].y = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getY();
				//trace("i: "+i+"  x: "+ball_arr[i].x+"  y: "+ball_arr[i].y);
				this.canvas.addChild(ball_arr[i]);
				this.makeSpriteDraggable(ball_arr[i], i);
				//center label on ball
				this.ballLabels[i].autoSize = TextFieldAutoSize.CENTER;
				this.ballLabels[i].x = -this.ballLabels[i].width/2;
				this.ballLabels[i].y = -this.ballLabels[i].height/2;
				
			}//for
		}//createBallImages2()
	*/
	
	/*following code is obsolete
		public function makeSpriteDraggable(target:Sprite, ballIndex:int):void{
			var indx:int = ballIndex;
			var pixPerM:int = this.pixelsPerMeter;
			var modelRef:Model = this.myModel;
			var H:Number = modelRef.borderHeight;
			target.buttonMode = true;
			target.addEventListener(MouseEvent.MOUSE_DOWN, startTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_UP, stopTargetDrag);
			target.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragTarget);
			var theStage:Object = target.parent;
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
					var ballX:Number = target.x/pixPerM;
					var ballY:Number = H - target.y/pixPerM;
					//trace("ballY: "+ballY);
					modelRef.ball_arr[indx].position.setXY(ballX, ballY);
					evt.updateAfterEvent();
				}
			}//end of dragTarget()
			
		}//end makeSpriteDraggable()
		*/
		public function update():void{
			//trace("TableView.update() called at time = "+this.myModel.time);
			//trace("TableView.showingPaths: "+this.showingPaths);
			//trace("TableView.myModel.atInitialConfig: "+this.myModel.atInitialConfig);
			var nbrBalls:int = this.myModel.nbrBalls;
			//trace("TableView.update() called. nbrBalls = "+nbrBalls);
			var maxBalls:int = this.myModel.maxNbrBalls;
			
			if (this.myModel.nbrBallsChanged){
				for(var i:int = 0; i < nbrBalls; i++){
					this.ball_arr[i].visible = true;
				}
				for(i = nbrBalls; i < maxBalls; i++){
					this.ball_arr[i].visible = false; 
				}
				this.myTrajectories.updateNbrPaths();
			}//end if()
			
			
			var yMax:Number = this.myModel.borderHeight;
			for(i = 0; i < nbrBalls; i++){
				ball_arr[i].x = this.pixelsPerMeter*this.myModel.ball_arr[i].position.getX();
				ball_arr[i].y = this.pixelsPerMeter*(yMax - this.myModel.ball_arr[i].position.getY());
				ball_arr[i].updateVelocityArrow();
			}
			if(this.showingPaths){
				this.myTrajectories.drawStep();
			}
			if(this.myModel.atInitialConfig){
				this.myTrajectories.erasePaths();
				//this.myModel.atInitialConfig = false;
			}
			this.timeText.text = "Time = " + Math.round(100*this.myModel.time)/100;
		}
		
	}//end of class
}//end of package