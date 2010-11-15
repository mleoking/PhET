//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.collisionlab {
import edu.colorado.phet.flashcommon.FlashCommonCS4;

import flash.display.*;
import flash.events.Event;

public class MainView extends Sprite {
    var myModel: Model;
    var myTableView: TableView;
    var myDataTable: DataTable;
    var controlPanel: ControlPanel;
    var momentumView: MomentumView;
    var mySoundMaker: SoundMaker;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    var common: FlashCommonCS4;

    public function MainView( myModel: Model, stageW: Number, stageH: Number ) {
        this.stageH = stageH;
        this.stageW = stageW;
        this.myModel = myModel;
        //this.initialize();
    }//end of constructor

    public function initialize(): void {
        //trace("myMainView initialize called");
        //this.stageW = this.stage.stageWidth;
        //this.stageH = this.stage.stageHeight;
        this.phetLogo = new PhETLogo();
        this.addChild( this.phetLogo );
        this.myTableView = new TableView( myModel, this );
        this.myDataTable = new DataTable( myModel, this );
        this.controlPanel = new ControlPanel( myModel, this );
        this.momentumView = new MomentumView( myModel, this );
        this.mySoundMaker = new SoundMaker( myModel, this );
        //this.waitForGraphicsLoad();
        this.myModel.resetAll();
        //this.myModel.updateViews();
        //this.myDataTable.x = 60;
        this.myDataTable.y = 0.75 * this.stageH;//this.myTableView.canvas.height + 1.0*this.myTableView.playButtons.height;
        this.myDataTable.x = this.myTableView.width / 2;
        this.controlPanel.background.width = 170;
        this.controlPanel.background.height = 330;
        this.controlPanel.x = this.stageW - 0.75 * this.controlPanel.width;
        this.controlPanel.y = 30;//0.3*this.controlPanel.height;
        this.phetLogo.x = 0;
        this.phetLogo.y = this.stageH - this.phetLogo.height - 35;
        this.momentumView.visible = false;

        addFlashCommon();
        //trace("stageW: "+stageW+"   stageH: "+stageH);
    }//end of initialize()

    //    private function waitForGraphicsLoad(): void {
    //        trace( "MainView.momentumView.scale_slider.height = " + this.momentumView.scale_slider.height );
    //    }

    private function addFlashCommon(): void {
        var ui: Sprite = new Sprite(); // used for FlashCommon UI
        addChild( ui );
        common = FlashCommonCS4.getInstance( ui.stage, this.stageW, this.stageH );//950,700 );
        common.initialize( ui );

        common.addLoadListener( positionButtons );
        stage.addEventListener( Event.RESIZE, function( evt: Event ): void {
            positionButtons();
        } );
        positionButtons();
    }

    protected function positionButtons(): void {
        if ( common.commonButtons == null ) {
            return;
        }
        var height: int = common.commonButtons.getPreferredHeight();
        const x: Number = 5;
        const y: Number = 700 - height - 5;
        common.commonButtons.setLocationXY( x, y );
    }
}//end of class
}//end of package