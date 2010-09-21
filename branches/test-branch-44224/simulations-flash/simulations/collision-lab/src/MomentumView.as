package{
import edu.colorado.phet.flashcommon.SimStrings;

import edu.colorado.phet.flashcommon.TextFieldUtils;

import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	import fl.controls.*;
	
	public class MomentumView extends Sprite{
		var myModel:Model;
		var myMainView:MainView;	//mediator and container of views
		var canvas:Sprite;			//background on which everything is placed
		var border:Sprite;			//border
		var invisibleBorder:Sprite;	//grabbable border
		var borderColor:uint;		//color of border 0xrrggbb
		var borderWidth:Number;
		var borderHeight:Number;
		var marquee:TextField;
		var stageH:Number;
		var stageW:Number;
		var pixelsPerSIMomentum:int;	//scale of view
		var momentum_arr:Array;		//array of momentum arrows in graphic
		var totMomentum:Arrow;
		var tipToTailDisplayOn:Boolean; //true if momentum arrows displayed tip-to-tail
		var tipToTail_cb:CheckBox;
		var scale_slider:Slider;
		
		//following 3 strings to be internationalized, see function initializeStrings() below
		var momenta_str:String;
		var tipToTail_str:String;
		var tot_str:String;
		
		public function MomentumView(myModel:Model, myMainView:MainView){
			this.myModel = myModel;
			this.myMainView = myMainView;
			this.myModel.registerView(this);
			this.canvas = new Sprite();
			this.invisibleBorder = new Sprite();
			this.myMainView.addChild(this);
			this.addChild(this.canvas);
			this.canvas.addChild(this.invisibleBorder);
			this.tipToTail_cb = new CheckBox();
			this.scale_slider = new Slider();
			
			//this.canvas.addChild(this.border);
			this.initialize();
			
		}//end of constructor
		
		public function initialize():void{
			this.borderWidth = 250;
			this.borderHeight = 250;
			this.tipToTailDisplayOn = true;
			this.stageW = this.myMainView.stageW;
			this.stageH = this.myMainView.stageH;
			this.canvas.x = this.stageW - this.borderWidth;
			this.canvas.y = this.stageH - this.borderHeight - 30;
			this.momentum_arr = new Array(this.myModel.nbrBalls);
			//trace("MomentumView.stageW: "+this.stageW);
			//trace("MomentumView.stageH: "+this.stageH);
			this.drawBorder();
			this.drawInvisibleBorder();
			this.initializeStrings();
			this.drawMarquee();
			this.setupCheckBox();
			this.setupSlider();
			this.drawArrows();
			Util.makePanelDraggableWithBorder(this, this.invisibleBorder);
			this.update();
		}
		
		public function initializeStrings():void{
			this.momenta_str = SimStrings.get("MomentumView.momenta","Momenta");
			this.tipToTail_str = SimStrings.get("MomentumView.tipToTail","tip-to-tail");
			this.tot_str = SimStrings.get("MomentumView.total","tot");
		}//end of initializeStrings()
		
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
				lineTo(-del, +H+del);
				lineTo(-del, -del);
				endFill();
			}
		}//end of drawBorder();
		
		public function drawInvisibleBorder():void{
			var W:Number = this.borderWidth;
			var H:Number = this.borderHeight;
			var thickness:Number = 6;  //border thickness in pixels
			var del:Number = thickness/2;
			//trace("width: "+W+"    height: "+H);
			with(this.invisibleBorder.graphics){
				clear();
				lineStyle(thickness,0x000000,0);
				moveTo(-del, -del);
				lineTo(W+del, -del);
				lineTo(W+del, +H+del);
				lineTo(-del, +H);
				lineTo(-del, -del);
			}
		}//end of drawInvisibleBorder();
		
		public function drawMarquee():void{
			this.marquee = new TextField();
			this.marquee.text = this.momenta_str; //"Momenta"; //
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
            TextFieldUtils.resizeText(this.marquee,TextFieldAutoSize.CENTER);
			this.canvas.addChild(this.marquee);

		}//end of drawMarquee
		
		private function setupCheckBox():void{
			this.tipToTail_cb.label = this.tipToTail_str; //"tip-to-tail"; //
			this.tipToTail_cb.selected = true;
			this.tipToTail_cb.textField.width = 0.7*this.borderWidth;
			this.tipToTail_cb.y = this.borderHeight - this.tipToTail_cb.height;
			this.canvas.addChild(this.tipToTail_cb);
			this.tipToTail_cb.addEventListener(MouseEvent.CLICK, tipToTailChangeListener);
		}//end of setupCheckBox
		
		private function setupSlider():void{
			this.scale_slider.direction = SliderDirection.VERTICAL;
			this.scale_slider.minimum = 0;
			this.scale_slider.maximum = 1;
			this.scale_slider.snapInterval = 0.01;
			this.scale_slider.value = 0.2;
			this.scale_slider.liveDragging = true;
			this.canvas.addChild(this.scale_slider);
			this.scale_slider.height = 0.6*this.borderHeight;
			this.scale_slider.x = 0.93*this.borderWidth;
			this.scale_slider.y = 0.5*this.borderHeight- this.scale_slider.height;// - this.scale_slider.height/2;
			this.scale_slider.addEventListener(Event.CHANGE, sliderChangeListener);
		}
		
		private function tipToTailChangeListener(evt:MouseEvent):void{
			this.tipToTailDisplayOn = evt.target.selected;
			if(this.tipToTailDisplayOn){this.arrangeArrowsTipToTail();}
			//trace("MomentumView.evt.target.selected: "+evt.target.selected);
		}
		
		private function sliderChangeListener(evt:SliderEvent):void{
			this.setScaleOfArrows(10+evt.target.value*200);
			//trace("MomentumView slider value = "+evt.target.value);
		}
		
		private function tipToTailCheckBoxOff():void{
			this.tipToTailDisplayOn = false;
			this.tipToTail_cb.selected = false;
		}
		
		private function setScaleOfArrows(scale:Number):void{
			var maxN:int = this.myModel.maxNbrBalls;
			for(var i:int = 0; i < maxN; i++){
				this.momentum_arr[i].setScale(scale);
			}
			this.totMomentum.setScale(scale);
			this.update();
		}//end setScaleOfArrows()
		
		//called once, at startUp
		private function drawArrows():void{
			var maxN:int = this.myModel.maxNbrBalls;
			for(var i:int = 0; i < maxN; i++){
				this.momentum_arr[i] = new Arrow(i);
				this.momentum_arr[i].setShaftWidth(8);
				this.momentum_arr[i].x = this.borderWidth/2;
				this.momentum_arr[i].y = this.borderHeight/2;
				this.canvas.addChild(this.momentum_arr[i]);
				this.momentum_arr[i].setArrow(this.myModel.ball_arr[i].getMomentum());
				this.momentum_arr[i].makeThisDraggable();
				this.momentum_arr[i].addEventListener(MouseEvent.MOUSE_DOWN, dragListener);
			}
			//trace("this.momentum_arr[0].parent"+this.momentum_arr[0].parent);
			
			this.totMomentum = new Arrow(maxN);  //index of total momentum is N = maxNbrBalls
			this.totMomentum.setText(tot_str);
			this.totMomentum.setColor(0xff8800);	//tot momentum arrow is orange
			this.totMomentum.setShaftWidth(4);
			this.totMomentum.x = this.borderWidth/2;
			this.totMomentum.y = this.borderHeight/2;
			this.canvas.addChild(this.totMomentum);
			this.totMomentum.setArrow(this.myModel.getTotalMomentum());
			this.totMomentum.makeThisDraggable();
			this.totMomentum.addEventListener(MouseEvent.MOUSE_DOWN, dragListener);
		}
		
		private function arrangeArrowsTipToTail():void{
			this.totMomentum.x = this.borderWidth/2;
			this.totMomentum.y = this.borderHeight/2;
			this.momentum_arr[0].x = this.borderWidth/2;
			this.momentum_arr[0].y = this.borderHeight/2;
			var N:int = this.myModel.nbrBalls;			
			for(var i:int = 1; i < N; i++){
				var arrowIM1:Arrow = this.momentum_arr[i-1];  //IM1 = "i minus 1"
				this.momentum_arr[i].x = arrowIM1.x + arrowIM1.getTipX();
				this.momentum_arr[i].y = arrowIM1.y + arrowIM1.getTipY();
			}//end for
		}//end arangeArrowsTipToTail();
		
		
		public function dragListener(evt:MouseEvent):void{
			this.tipToTailDisplayOn = false;
			this.tipToTail_cb.selected = false;
		}
		
		public function update():void{
			var N:int = this.myModel.nbrBalls;
			var i:int;
			if(this.myModel.nbrBallsChanged){
				var maxN:int = this.myModel.maxNbrBalls;
				for(i = 0; i < N; i++){
					this.momentum_arr[i].visible = true;
				}
				for(i = N; i < maxN; i++){
					this.momentum_arr[i].visible = false
				}
			}//end if()
			for(i = 0; i < N; i++){
				this.momentum_arr[i].setArrow(this.myModel.ball_arr[i].getMomentum());
			}
			//position momentum arrows tip-to-tail
			if(tipToTailDisplayOn){
				this.arrangeArrowsTipToTail();
			}//end if
			this.totMomentum.setArrow(this.myModel.getTotalMomentum());
			//trace("momentum view update");
		}//end of update()
		
	}//end of class
}//end of package