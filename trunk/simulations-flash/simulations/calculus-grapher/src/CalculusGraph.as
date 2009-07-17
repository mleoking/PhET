package{
	
	import flash.display.Sprite;
	
	public class CalculusGraph extends Sprite{
		
		var myModel:Model;			//underlying math model
		var myMainView:MainView;	//main view
		
		public function CalculusGraph(){
			myModel = new Model();  
			myMainView = new MainView(myModel);
			addChild(myMainView);
			myMainView.initialize();
		}//end of constructor
		
	}//end of class
}//end of package