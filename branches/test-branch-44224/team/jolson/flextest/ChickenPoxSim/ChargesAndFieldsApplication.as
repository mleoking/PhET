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
        import mx.controls.TextInput;
        import mx.core.UIComponent;

        public class ChargesAndFieldsApplication extends UIComponent {
                public var display : ChargesAndFieldsDisplay;
                public function ChargesAndFieldsApplication() {
                       this.addEventListener(Event.ADDED_TO_STAGE, init);
                }
		
                public function init(evt : Event) : void {
			
			display = new ChargesAndFieldsDisplay(stage);
                        this.addChild(display);
			
                        stage.scaleMode = StageScaleMode.NO_SCALE;
                        stage.align = StageAlign.TOP_LEFT;
                        stage.addEventListener(Event.RESIZE, display.onResize);
			
                }
        }
}
