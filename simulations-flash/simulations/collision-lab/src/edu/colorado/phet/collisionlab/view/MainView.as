//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.control.ControlPanel;
import edu.colorado.phet.collisionlab.control.DataTable;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.SoundMaker;

import flash.display.*;

public class MainView extends Sprite {
    var myModel: Model;
    public var myTableView: TableView;
    public var myDataTable: DataTable;
    public var controlPanel: ControlPanel;
    public var momentumView: MomentumView;
    var mySoundMaker: SoundMaker;
    var phetLogo: Sprite;
    var stageH: Number;
    var stageW: Number;

    public function MainView( myModel: Model, stageW: Number, stageH: Number ) {
        this.stageH = stageH;
        this.stageW = stageW;
        this.myModel = myModel;
    }

    public function initialize(): void {
        this.phetLogo = new PhETLogo();
        this.addChild( this.phetLogo );
        this.myTableView = new TableView( myModel, this );
        this.myDataTable = new DataTable( myModel, this );
        this.controlPanel = new ControlPanel( myModel, this );
        this.momentumView = new MomentumView( myModel, this );
        this.mySoundMaker = new SoundMaker( myModel, this );
        this.myModel.resetAll();
        var paddingForTabs: Number = 10; // we need to add padding at the top for the new tab bar
        this.myTableView.y += paddingForTabs;
        this.myDataTable.y = 0.75 * this.stageH + paddingForTabs / 2;
        this.myDataTable.x = this.myTableView.width / 2;
        this.controlPanel.background.width = 170;
        this.controlPanel.background.height = 330;
        this.controlPanel.x = this.stageW - 0.75 * this.controlPanel.width;
        this.controlPanel.y = 30 + paddingForTabs;
        this.phetLogo.x = 0;
        this.phetLogo.y = this.stageH - this.phetLogo.height - 35; // our flashcommon buttons now below logo.
        this.momentumView.visible = false;

    }
}
}