//View of "Pool Table" containing balls
package{
	import flash.display.*;
	import flash.events.*;
	import flash.text.*;
	import flash.geom.*;
	
	public class TableView extends Sprite{
		var myModel:Model;
		var myMainView:MainView;			//mediator and container of views
		var canvas:Sprite;					//background on which everything is placed
		var myTrajectories:Trajectories;	//Sprite showing trajectories (paths) of balls
		var CM:CenterOfMass;				//library symbol
		var showingPaths:Boolean;			//true if paths are shown
		var playButtons:PlayPauseButtons;	//class to hold library symbol, contains dynamic text strings
		var border:Sprite;					//reflecting border
		var invisibleBorder:Sprite;			//handle for dragging
		var borderColor:uint;				//color of border 0xrrggbb
		var timeText:TextField;				//label containing current time
		var totKEText:TextField;			//label showing total KE of particles
		var pixelsPerMeter:int;				//scale of view
		var ball_arr:Array;					//array of ball images
		var ballLabels:Array;				//array of ball labels: 1, 2, 3, ...
		var ballColor_arr:Array;			//array of uint for colors of balls
		var xOffset:Number;					//x of upper left corner of canvas
		var yOffset:Number;					//y of upper left corner of canvas
		
		//following 4 strings set by internationalizer
		var KEtot_str:String;				//string "Kinetic Energy = "
		var joules_str:String;				//string " J"
		var time_str:String;				//string "time = "
		var seconds_str:String;				//string " s"
		
		
		
		public function TableView(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.CM = new CenterOfMass();	//library symbol
			this.canvas = new Sprite();
			this.myMainView.addChild(this);
			this.addChild(this.canvas);
			this.xOffset = 10;  //position of table border relative to origin of stage
			this.yOffset = 30;
			this.canvas.x = xOffset;
			this.canvas.y = yOffset;
			this.border = new Sprite();
			this.invisibleBorder = new Sprite();
			this.playButtons = new PlayPauseButtons(this.myModel);
			this.canvas.addChild(this.border);
			this.canvas.addChild(this.invisibleBorder);
			this.canvas.addChild(this.playButtons);
			this.myModel.registerView(this);
			this.pixelsPerMeter = 200;
			this.showingPaths = false;
			this.myTrajectories = new Trajectories(this.myModel, this);
			//this.canvas.addChild(this.myTrajectories);
			this.
			border.addChild(this.myTrajectories);
			this.makeTimeLabel();
			this.makeTotKELabel();
			this.drawBorder();  //drawBorder() also calls positionLabels() and drawInvisibleBorder()
			this.ballColor_arr = new Array(10);  //start with 10 colors
			this.createBallColors();
			//this.createBallImages2();
			this.createBallImages();
			this.canvas.addChild(this.CM);
			if(this.myModel.nbrBalls == 1){
				this.CM.visible = false;
			}
			Util.makePanelDraggableWithBorder(this, this.invisibleBorder);
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
			var g:Graphics = this.border.graphics
			g.clear();
			if(this.myModel.borderOn){
				g.lineStyle(thickness,0xFF0000);
			}else{
				g.lineStyle(thickness,0xffcccc);
			}
			var x0:Number = 0;
			var y0:Number = 0;
			g.beginFill(0xccffcc);
			g.moveTo(-del, -del);
			g.lineTo(W+del, -del);
			g.lineTo(W+del, +H+del);
			g.lineTo(-del, +H+del);
			g.lineTo(-del, -del);
			g.endFill();
			
			//position playButtons
			this.playButtons.x = W/2;
			this.playButtons.y = H + this.playButtons.height/2;
			
			this.drawInvisibleBorder(); 
			
			//position KE and Time Labels
			this.timeText.x = W - 1.5*this.timeText.width;
			this.timeText.y = H + 10;
			this.totKEText.x = 0;//0.5*this.totKEText.width;//30; //
			this.totKEText.y = H + 10;
			trace("drawBorder() called. this.totKEText.width = "+this.totKEText.width);

		}//end of drawBorder();
		
		//called when 1D/2D mode is switched
		public function reDrawBorder():void{
			this.drawBorder();
			if(this.myModel.oneDMode){
				var oneDH:Number = this.myModel.oneDBorderHeight/2;
				var twoDH:Number = this.myModel.twoDBorderHeight/2;
				this.canvas.y = yOffset + this.pixelsPerMeter*(twoDH - oneDH);
			}else{
				this.canvas.y = yOffset;
			}
			this.update();
		}
		
		public function drawInvisibleBorder():void{  //grayed-out border when Reflecting Border is OFF
			var W:Number = this.myModel.borderWidth * this.pixelsPerMeter;
			var H:Number = this.myModel.borderHeight * this.pixelsPerMeter;
			var thickness:Number = 6;  //border thickness in pixels
			var del:Number = thickness/2;
			//trace("width: "+W+"    height: "+H);
			var g:Graphics = this.invisibleBorder.graphics
			g.clear();
			g.lineStyle(thickness,0xffffff,0);
			g.moveTo(-del, -del);
			g.lineTo(W+del, -del);
			g.lineTo(W+del, +H+del);
			g.lineTo(-del, +H);
			g.lineTo(-del, -del);
		}//end of drawInvisibleBorder();
		
		
		public function makeTimeLabel():void{
			//following two strings should be set by internationalizer
			this.time_str = "Time = ";
			this.seconds_str = " s";
			//
			this.timeText = new TextField();
			this.timeText.text = this.time_str;
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
		}
		
		public function makeTotKELabel():void{
			//following two strings should be set by internationalizer
			this.KEtot_str = "Kinetic Energy = ";
			this.joules_str = " J";
			//
			this.totKEText = new TextField();
			this.totKEText.text = this.KEtot_str; //text is set in update
			this.totKEText.selectable = false;
			this.totKEText.autoSize = TextFieldAutoSize.RIGHT;
			var tFormat:TextFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.color = 0x000000;
			tFormat.size = 12;
			this.totKEText.defaultTextFormat = tFormat;
			//this.timeText.setTextFormat(tFormat);
			this.canvas.addChild(this.totKEText);
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
			
			
			var yMax:Number = this.myModel.borderHeight/2;  //recall origin is set at y = borderHeight/2 
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
			this.timeText.text = this.time_str + Math.round(100*this.myModel.time)/100 + this.seconds_str;
			this.totKEText.text = this.KEtot_str + Math.round(100*this.myModel.getTotalKE())/100 + this.joules_str;
			
			this.CM.x = this.pixelsPerMeter*this.myModel.CM.x;
			this.CM.y = this.pixelsPerMeter*(yMax - this.myModel.CM.y);
		}
		
	}//end of class
}//end of package