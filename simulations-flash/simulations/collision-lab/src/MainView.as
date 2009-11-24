//MainView contains all views, acts as mediator, communication hub for views
package{
	import flash.display.*;
	
	public class MainView extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var myDataTable:DataTable;
		//var myPlayButtons;
		//var 
		var controlPanel:ControlPanel;
		var momentumView:MomentumView;
		var phetLogo:Sprite;
		var stageH:Number;
		var stageW:Number;
		
		public function MainView(myModel:Model, stageW:Number, stageH:Number){
			this.stageH = stageH;
			this.stageW = stageW;
			this.myModel = myModel;
			
			//this.initialize();
		}//end of constructor
		
		public function initialize():void{
			//trace("myMainView initialize called");
			//this.stageW = this.stage.stageWidth;
			//this.stageH = this.stage.stageHeight;
			this.myTableView = new TableView(myModel, this);
			this.myDataTable = new DataTable(myModel, this);
			this.controlPanel = new ControlPanel(myModel, this);
			this.momentumView = new MomentumView(myModel, this);
			this.phetLogo = new PhETLogo();
			this.myModel.updateViews();
			this.myDataTable.x = 40;
			this.myDataTable.y = this.myTableView.canvas.height + 2.0*this.myTableView.playButtons.height;
			this.controlPanel.background.width = 150;
			this.controlPanel.background.height = 300;
			this.controlPanel.x = stageW - 0.75*this.controlPanel.width;
			this.controlPanel.y = 0.6*this.controlPanel.height;
			//trace("stageW: "+stageW+"   stageH: "+stageH);
		}//end of initialize()
	}//end of class
}//end of package