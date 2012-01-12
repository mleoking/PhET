package {
	import flash.display.DisplayObject;
        import flash.display.Graphics;
        import flash.display.MovieClip;
        import flash.display.Sprite;
        import flash.display.Stage
        import flash.display.StageAlign;
        import flash.display.StageScaleMode;
        import flash.events.Event;
        import flash.events.KeyboardEvent;
        import flash.events.MouseEvent;
        import flash.geom.ColorTransform;
        import flash.text.TextField;
        import flash.ui.Keyboard;
	
	public class ChargesAndFieldsDisplay extends Sprite {
		private var myWidth : Number;
		private var myHeight : Number;
		
		private var background : BackgroundSprite;
		private var ovals : Array = new Array();
		
		public function ChargesAndFieldsDisplay(tempStage : Stage) {
			myWidth = tempStage.stageWidth;
			myHeight = tempStage.stageHeight;
			
			background = new BackgroundSprite(myWidth, myHeight);
			addChild(background);
			
			for(var i : uint = 0; i < 500; i++) {
				var oval : OvalSprite = new OvalSprite(myWidth, myHeight);
				addChild(oval);
				ovals.push(oval);
				oval.setPosition(Math.random() * myWidth, Math.random() * myHeight);
				oval.alpha = Math.random() / 2 + 0.25;
			}
			
			var txt1 : TextField = new TextField();
			txt1.text = "foobar";
			txt1.alpha = 50;
			addChild(txt1);
			
			var txt2 : TextField = new TextField();
			txt2.text = "foobar";
			txt2.alpha = 100;
			txt2.x = 100;
			addChild(txt2);
		}
		
		public function onResize(evt : Event) : void {
			myWidth = this.stage.stageWidth;
			myHeight = this.stage.stageHeight;
			background.changeSize(myWidth, myHeight);
			for(var idx : uint = 0; idx < ovals.length; idx++) {
				var oval : OvalSprite = ovals[idx] as OvalSprite;
				oval.setSize(myWidth, myHeight);
			}
		}
	}
}
