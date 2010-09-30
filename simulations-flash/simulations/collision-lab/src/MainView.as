//MainView contains all views, acts as mediator, communication hub for views
package{
import edu.colorado.phet.flashcommon.FlashCommonCS4;

import flash.display.*;
import flash.events.Event;
import flash.geom.ColorTransform;

public class MainView extends Sprite{
		var myModel:Model;
		var myTableView:TableView;
		var myDataTable:DataTable; 
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
			this.addChild(this.phetLogo);
			this.myModel.updateViews();
			//this.myDataTable.x = 60;
			this.myDataTable.y = 0.75*this.stageH;//this.myTableView.canvas.height + 1.0*this.myTableView.playButtons.height;
			this.myDataTable.x = this.myTableView.width/2;
			this.controlPanel.background.width = 170;
			this.controlPanel.background.height = 350;
			this.controlPanel.x = this.stageW - 0.75*this.controlPanel.width;
			this.controlPanel.y = 20;//0.3*this.controlPanel.height;
			this.phetLogo.x = 0*this.phetLogo.width;
			this.phetLogo.y = this.stageH - 1.0*this.phetLogo.height;
            
            addFlashCommon(); 
			//trace("stageW: "+stageW+"   stageH: "+stageH);
		}//end of initialize()

        private function addFlashCommon():void {
            var ui:Sprite = new Sprite(); // used for FlashCommon UI
            addChild( ui );

            var common:FlashCommonCS4 = FlashCommonCS4.getInstance( ui.stage,950,700 );
            common.initialize( ui );

//            common.highContrastFunction = function ( contrast:Boolean ):void {
//                if ( contrast ) {
//                    var stretch:Number = 2.0;
//                    var newCenter:Number = 128;
//                    var offset:Number = newCenter - 128 * stretch;
//                    root.stage.transform.colorTransform = new ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
//                }
//                else {
//                    root.stage.transform.colorTransform = new ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
//                }
//            };
        }
	}//end of class
}//end of package