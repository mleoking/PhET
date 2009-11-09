//CollisionLab M.Dubson Nov 5, 2009
//source code resides in /collision-lab
//main class instance
package{
	import flash.display.*;  
	
	public class CollisionLab extends Sprite{  //should the main class extend MovieClip or Sprite?
		var myModel:Model;
		var myMainView:MainView;
		
		public function CollisionLab(){
			myModel = new Model();
			myMainView = new MainView(myModel);
			this.addChild(myMainView);
		}//end of constructor
		
	}//end of class
}//end of package