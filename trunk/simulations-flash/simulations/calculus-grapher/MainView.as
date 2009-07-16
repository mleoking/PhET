package{
	import flash.display.Sprite;
	import flash.display.Graphics;
	
	public class MainView extends Sprite{
		var myModel:Model;
		var yView:View;				//view of function curve
		var dView:View;				//view of derivative of function
		var iView:View;				//view of integral of function
		//var pushPinCup:PushPinCup;
		var controlPanel:ControlPanel;
		var ruler:Ruler;
		var dragMeSign:Sprite;
		var phetLogo:Sprite;
		//var grabbedPin:PushPin;
		//var pinGrabbed:Boolean;
		var smoothOn:Boolean;	//true if smooth graph making is on
		var linearOn:Boolean;	//true if piece-wise linear graph making is on
		var sketchOn:Boolean;
		
		public function MainView(myModel:Model){
			this.myModel = myModel;
			yView = new View(myModel, this, "original");
			dView = new View(myModel, this, "derivative");
			iView = new View(myModel, this, "integral");
			controlPanel = new ControlPanel(myModel, this);
			phetLogo = new PhETLogo()
			dragMeSign = new DragMe();
			ruler = new Ruler();
		}//end of constructor
		
		public function initialize():void{
			//trace("h: " + stage.stageHeight + "   w: " + stage.stageWidth);
			addChild(iView);
			addChild(dView);
			addChild(yView);
			addChild(controlPanel);
			addChild(dragMeSign);
			addChild(phetLogo);
			addChild(ruler);
			//addChild(pushPinCup);
			//addChild(grabbedPin);
			//trace("myMainView.stage.stageWidth: "+this.stage.stageWidth);
			yView.initialize();
			dView.initialize();
			iView.initialize();
			controlPanel.initialize();
			Util.makeClipDraggable(this.ruler);
			this.ruler.x = this.stage.stageWidth/2;
			this.ruler.visible = false;
			//pushPinCup.initialize();
			//dView.y = stage.stageHeight/2;
			//iView.visible = false;
			this.showDerivativeOnly();
			controlPanel.x = stage.stageWidth - 0.35*controlPanel.width;
			controlPanel.y = 0.6*controlPanel.height;
			phetLogo.x = stage.stageWidth - 1.2*phetLogo.width;
			phetLogo.y = stage.stageHeight - 1.0*phetLogo.height;
			this.drawStageBorder();
			//pushPinCup.x = stage.stageWidth - 1.5*pushPinCup.width;
			//pushPinCup.y = 1.5*pushPinCup.height;
			//grabbedPin.visible = false;
			this.flyInDragMe();
		}//end of initialize()
		
		public function flyInDragMe():void{
			this.dragMeSign.x = 0.45*this.stage.stageWidth;
			this.dragMeSign.y = this.yView.y + this.yView.originY;
			//trace("this.yView.y:" + this.yView.y);
		}
		
		public function drawStageBorder():void{
			var W:Number = this.parent.stage.stageWidth - 2;
			var H:Number = this.parent.stage.stageHeight - 2;
			with(this.graphics){
				clear();
				lineStyle(1, 0xF4800B);
				moveTo(0,0);
				lineTo(W,0);
				lineTo(W,H);
				lineTo(0,H);
				lineTo(0,0);
				//moveTo(W/2,0);
				//lineTo(W/2,H);
				//moveTo(0,H/2);
				//lineTo(W,H/2);
			}
		}//end of drawStageBorder()
		
		public function showIntegralOnly():void{
			//trace("showIntegralOnly");
			dView.visible = false;
			iView.visible = true;
			var myHeight:Number = 0.48*this.stage.stageHeight;
			iView.setViewHeight(myHeight);
			yView.setViewHeight(myHeight);
			iView.y = 0.035*this.stage.stageHeight;
			yView.y = 0.52*this.stage.stageHeight;
		}
		
		public function showDerivativeOnly():void{
			//trace("showDerivativeOnly");
			dView.visible = true;
			iView.visible = false;
			var myHeight:Number = 0.48*this.stage.stageHeight;
			yView.setViewHeight(myHeight);
			dView.setViewHeight(myHeight);
			yView.y = 0.035*this.stage.stageHeight;
			dView.y = 0.52*this.stage.stageHeight;
		}
		
		public function showDerivativeAndIntegral():void{
			//trace("showDerivativeAndIntegral");
			dView.visible = true;
			iView.visible = true;
			var myHeight:Number = 0.32*this.stage.stageHeight;
			iView.setViewHeight(myHeight);
			yView.setViewHeight(myHeight);
			dView.setViewHeight(myHeight);
			iView.y = 0.05*this.stage.stageHeight;
			yView.y = 0.35*this.stage.stageHeight;
			dView.y = 0.65*this.stage.stageHeight;
		}
		public function showOriginalCurveOnly():void{
			//trace("showOriginalCurveOnly");
			dView.visible = false;
			iView.visible = false;
			var myHeight:Number = 0.70*this.stage.stageHeight;
			yView.setViewHeight(myHeight);
			yView.y = (stage.stageHeight/2) - yView.originY;
			
		}
		
		function makeRulerGrabbable():void{
			
		}
		
	}//end of class
}//end of package