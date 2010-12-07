package{
	//import edu.colorado.phet.flashcommon.SimStrings;
	import flash.display.*;
	
	public class Resonance extends Sprite{
		var myModel:ShakerModel;
		var myMainView:MainView;
		var stageW:Number;
		var stageH:Number;
		
		public function Resonance(){
            //SimStrings.init(loaderInfo);
			myModel = new ShakerModel( 10 );  //argument is max number of resonators
			//stage width and height may need to be hard-coded 
			this.stageW = this.stage.stageWidth;
			this.stageH = this.stage.stageHeight;
			myMainView = new MainView(myModel, this.stageW, this.stageH);
			this.addChild(myMainView);
			//this.myMainView.initialize();
		}//end of constructor
	}//end of class
}//end of package