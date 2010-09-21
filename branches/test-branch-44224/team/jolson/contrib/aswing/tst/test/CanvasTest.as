/*
 Copyright aswing.org, see the LICENCE.txt.
*/
import org.aswing.JPanel;
import org.aswing.JWindow;

/**
 * @author iiley
 */
class test.CanvasTest {
	
	private var canvasPanel:JPanel;
	private var canvasMC:MovieClip;
	
	public function CanvasTest(){
		canvasPanel = new JPanel();
		canvasPanel.addEventListener(JPanel.ON_CREATED, __canvasCreated, this);
		
		//create a window to show the canvas panel
		var window:JWindow = new JWindow();
		window.setContentPane(canvasPanel);
		window.setSize(550, 400);
		window.show();
	}
	
	private function __canvasCreated():Void{
		//you must create the mc after component created, like the way 
		//here in ON_CREATED handler method
		canvasMC = canvasPanel.createMovieClip("canvas");
		//Then you have a movie clip, you can do anything a movie clip can do here
		//here we draw some thing on it, and create a text filed at it
		canvasMC.lineStyle(4, 0xff0000);
		canvasMC.moveTo(100, 100);
		canvasMC.lineTo(200, 200);
		canvasMC.createTextField("aText", 0, 150, 150, 150, 20);
		canvasMC["aText"].text = "a text on canvas mc";
	}
	
	public static function main():Void{
		Stage.scaleMode = "noScale";
		var t:CanvasTest = new CanvasTest();
	}
}