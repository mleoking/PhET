//MainView contains all views, acts as mediator, communication hub for views
package{
	import flash.display.*;
	
	public class MainView extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		
		public function MainView(myModel:Model){
			this.myModel = myModel;
			this.myTableView = new TableView(myModel, this);
			
		}//end of constructor
	}//end of class
}//end of package