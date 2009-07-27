package{
	
	import flash.display.Sprite;
	import flash.display.Graphics;
	import flash.display.*;
	import edu.colorado.phet.flashcommon.*;
	
	public class CalculusGraph extends Sprite{
		
		//Value of countryCode from FlashVars in HTML container
		var countryCode:String;

		// Create the SimStrings instance needed for internationalization
		//var simStrings:SimStrings;//  = new SimStrings( "calculus-grapher-strings", countryCode );
		//var myInternationalizer:Internationalizer;
		
		var myModel:Model;			//underlying math model
		var myMainView:MainView;	//main view
		
		public function CalculusGraph(){
			SimStrings.init( this.root.loaderInfo )
			myModel = new Model();  
			myMainView = new MainView(myModel);
			addChild(myMainView);
			myMainView.initialize();
			//this.drawStageDimension();
		}//end of constructor
		
		
		//used for testing only
		public function drawStageDimension():void{
			var W:Number = this.stage.stageWidth;
			var H:Number = this.stage.stageHeight;
			with(this.graphics){
				clear();
				lineStyle(3, 0x000000);
				beginFill(0xff0000, 1);
				moveTo(0,0);
				lineTo(W,0);
				lineTo(W,H);
				lineTo(0,H);
				lineTo(0,0);
				endFill();
				moveTo(W/2,0);
				lineTo(W/2,H);
				moveTo(0,H/2);
				lineTo(W,H/2);
				
			}
			
		}
		
	}//end of class
}//end of package