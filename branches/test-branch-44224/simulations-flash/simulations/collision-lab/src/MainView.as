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
		var mySoundMaker:SoundMaker;
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
			this.mySoundMaker = new SoundMaker(myModel, this);
			this.phetLogo = new PhETLogo();
			this.myModel.updateViews();
			//this.myDataTable.x = 60;
			this.myDataTable.y = 0.75*this.stageH;//this.myTableView.canvas.height + 1.0*this.myTableView.playButtons.height;
			this.controlPanel.background.width = 170;
			this.controlPanel.background.height = 350;
			this.controlPanel.x = this.stageW - 0.75*this.controlPanel.width;
			this.controlPanel.y = 20;//0.3*this.controlPanel.height;
			//trace("stageW: "+stageW+"   stageH: "+stageH);
		}//end of initialize()
	}//end of class
}//end of package