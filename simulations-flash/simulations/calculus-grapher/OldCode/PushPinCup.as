package{
	import flash.display.Sprite;
	import flash.events.*;
	import flash.geom.*;
	import flash.text.*;
	
	public class PushPinCup extends Sprite{
		
		var myMainView:MainView;
		//var pinGrabbed:Boolean;
		
		public function PushPinCup(myMainView:MainView):void{
			this.myMainView = myMainView;
			this.hitAreaPins.alpha = 0;
			this.hitAreaCup.alpha = 0;
			this.hitAreaPins.buttonMode = true;
			this.hitAreaCup.buttonMode = true;
			
			//this.pinGrabbed = false;
		}//end of constructor
		
		public function initialize():void{
			this.makeCupDraggable();
			this.makePinsGrabbable();
		}
		
		public function makePinsGrabbable():void{
			var target:Sprite = this.hitAreaPins;
			target.addEventListener(MouseEvent.MOUSE_DOWN, pickUpPin);
			this.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragPin);
			this.stage.addEventListener(MouseEvent.MOUSE_UP, putDownPin);
			var localRef:Object = this;
			var thePin:PushPin = this.myMainView.grabbedPin;
			function pickUpPin(evt:MouseEvent):void{
				//localRef.myMainView.pinGrabbed = true;
				//Util.PINGRABBED = true;
				myMainView.pinGrabbed = true;
				thePin.visible = true;
				thePin.x = evt.stageX;
				thePin.y = evt.stageY;
			}
			function dragPin(evt:MouseEvent):void{
				//if(localRef.myMainView.pinGrabbed){
				if(myMainView.pinGrabbed){
					thePin.x = evt.stageX + 3;
					thePin.y = evt.stageY;
					evt.updateAfterEvent();
				}
			}
			function putDownPin(evt:MouseEvent):void{
				//localRef.myMainView.pinGrabbed = false;
				myMainView.pinGrabbed = false;
				thePin.visible = false
			}
		}//end of makePinsGrabbable()

		
		
		public function makeCupDraggable():void{
			
			//make cup draggable
			var target:Sprite = this.hitAreaCup;
			target.addEventListener(MouseEvent.MOUSE_OVER, onBorder);
			target.addEventListener(MouseEvent.MOUSE_DOWN, startMyDrag);
			this.stage.addEventListener(MouseEvent.MOUSE_MOVE, dragging);
			this.stage.addEventListener(MouseEvent.MOUSE_UP, stopMyDrag);
			target.addEventListener(MouseEvent.MOUSE_OUT, offBorder);
			var localRef = this;
			var clickOffset:Point;
			function onBorder(evt:MouseEvent):void {
				//trace("overborder");
			}
			function startMyDrag(evt:MouseEvent):void {
				//trace("start drag");
				clickOffset = new Point(evt.localX, evt.localY);
				//trace("evt.localX "+evt.localX+"  evt.localY "+evt.localY);
			}
			function stopMyDrag(evt:MouseEvent):void {
				clickOffset = null;
				//trace("stop drag");
			}
			function dragging(evt:MouseEvent):void {
				//trace("dragging");

				if (clickOffset != null) {//if dragging
					//trace("evt.stageX "+evt.stageX+"  evt.stageY "+evt.stageY);
					localRef.x = evt.stageX - clickOffset.x;
					localRef.y = evt.stageY - clickOffset.y;
					evt.updateAfterEvent();
				}
			}
			function offBorder(evt:MouseEvent):void {
				//trace("offborder");

			}
			
		}//end of makeDraggable()
	}//end of class
}//end of package