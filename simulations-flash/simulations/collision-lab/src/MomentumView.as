package{
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	
	public class MomentumView extends Sprite{
		var myModel:Model;
		var myMainView:MainView;			//mediator and container of views
		var canvas:Sprite;		//background on which everything is placed
		var border:Sprite;		//border
		var borderColor:uint;	//color of border 0xrrggbb
		var borderWidth:Number;
		var borderHeight:Number;
		var marquee:TextField;
		var stageH:Number;
		var stageW:Number;
		var pixelsPerSIMomentum:int;	//scale of view
		var momentum_arr:Array;		//array of momentum arrows in graphic
		var totMomentum:Arrow;
		var tipToTailDisplayOn:Boolean; //true if momentum arrows displayed tip-to-tail
		
		public function MomentumView(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myModel.registerView(this);
			this.canvas = new Sprite();
			this.border = new Sprite();
			this.myMainView.addChild(this.canvas);
			//this.canvas.addChild(this.border);
			this.initialize();
			
		}//end of constructor
		
		public function initialize():void{
			this.borderWidth = 300;
			this.borderHeight = 300;
			this.tipToTailDisplayOn = true;
			this.stageW = this.myMainView.stageW;
			this.stageH = this.myMainView.stageH;
			this.canvas.x = this.stageW - this.borderWidth;
			this.canvas.y = this.stageH - this.borderHeight;
			this.momentum_arr = new Array(this.myModel.nbrBalls);
			//trace("MomentumView.stageW: "+this.stageW);
			//trace("MomentumView.stageH: "+this.stageH);
			this.drawBorder();
			this.drawMarquee();
			this.drawArrows();
		}
		
		public function drawBorder():void{
			var W:Number = this.borderWidth;
			var H:Number = this.borderHeight;
			var thickness:Number = 6;  //border thickness in pixels
			var del:Number = thickness/2;
			//trace("width: "+W+"    height: "+H);
			with(this.canvas.graphics){
				clear();
				lineStyle(thickness,0x0000ff);
				var x0:Number = 0;
				var y0:Number = 0;
				beginFill(0xffffff);
				moveTo(-del, -del);
				lineTo(W+del, -del);
				lineTo(W+del, +H+del);
				lineTo(-del, +H);
				lineTo(-del, -del);
				endFill();
			}
		}//end of drawBorder();
		
		public function drawMarquee():void{
			this.marquee = new TextField();
			this.marquee.text = "Momenta";
			this.marquee.selectable = false;
			this.marquee.autoSize = TextFieldAutoSize.LEFT;
			this.marquee.x = 10;
			var tFormat:TextFormat = new TextFormat();
			tFormat.font = "Arial";
			tFormat.bold = true;
			tFormat.italic = true;
			tFormat.color = 0xffaaaa;
			tFormat.size = 40;
			//textFormat must be applied to text, so set text property first then apply format
			this.marquee.setTextFormat(tFormat);
			this.canvas.addChild(this.marquee);

		}//end of drawMarquee
		
		public function drawArrows():void{
			var N:int = this.myModel.nbrBalls;
			for(var i:int = 0; i < N; i++){
				this.momentum_arr[i] = new Arrow(i);
				this.momentum_arr[i].x = this.borderWidth/2;
				this.momentum_arr[i].y = this.borderHeight/2;
				this.canvas.addChild(this.momentum_arr[i]);
				this.momentum_arr[i].setArrow(this.myModel.ball_arr[i].getMomentum());
				//trace("MomentumView.canvas.stage: "+this.canvas.stage);
				//Util.makeClipDraggable(this.momentum_arr[i]);
				//trace("i = "+i+"  momentum_arr[i]"+momentum_arr[i]);
			}
			//trace("this.momentum_arr[0].parent"+this.momentum_arr[0].parent);
			
			this.totMomentum = new Arrow(N);  //index of total momentum is N = nbrBalls
			this.totMomentum.setText("Tot");
			this.totMomentum.setColor(0x00ff00);	//tot momentum arrow is green
			this.totMomentum.x = this.borderWidth/2;
			this.totMomentum.y = this.borderHeight/2;
			this.canvas.addChild(this.totMomentum);
			this.totMomentum.setArrow(this.myModel.getTotalMomentum());
			//trace("py: "+this.myModel.ball_arr[0].getMomentum().getY());
			//trace("px: "+this.myModel.ball_arr[0].getMomentum().getX());
			//trace("theta: "+this.myModel.ball_arr[0].getMomentum().getAngle())
		}
		
	
		
		public function update():void{
			var N:int = this.myModel.nbrBalls;
			for(var i:int = 0; i < N; i++){
				this.momentum_arr[i].setArrow(this.myModel.ball_arr[i].getMomentum());
			}
			//position momentum arrows tip-to-tail
			if(tipToTailDisplayOn){
				for(i = 1; i < N; i++){
					var arrowIM1:Arrow = this.momentum_arr[i-1];  //IM1 = "i minus 1"
					this.momentum_arr[i].x = arrowIM1.x + arrowIM1.getTipX();
					this.momentum_arr[i].y = arrowIM1.y + arrowIM1.getTipY();
				}//end for
			}//end if
			this.totMomentum.setArrow(this.myModel.getTotalMomentum());
			//trace("momentum view update");
		}//end of update()
		
	}//end of class
}//end of package