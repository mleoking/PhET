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
			this.stageW = this.myMainView.stageW;
			this.stageH = this.myMainView.stageH;
			this.canvas.x = this.stageW - this.borderWidth;
			this.canvas.y = this.stageH - this.borderHeight;
			//trace("MomentumView.stageW: "+this.stageW);
			//trace("MomentumView.stageH: "+this.stageH);
			this.drawBorder();
			this.drawMarquee();
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
		
		public function update():void{
			//trace("momentum view update");
		}
	}//end of class
}//end of package