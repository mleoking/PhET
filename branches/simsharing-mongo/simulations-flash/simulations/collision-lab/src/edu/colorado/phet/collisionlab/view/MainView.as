//MainView contains all views, acts as mediator, communication hub for views
package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.CollisionLab;
import edu.colorado.phet.collisionlab.CollisionLabModule;
import edu.colorado.phet.collisionlab.control.ControlPanel;
import edu.colorado.phet.collisionlab.control.DataTable;
import edu.colorado.phet.collisionlab.control.NiceButton;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.SoundMaker;
import edu.colorado.phet.flashcommon.SimStrings;

import flash.display.*;
import flash.geom.Point;
import flash.geom.Rectangle;

public class MainView extends Sprite {
    var myModel: Model;
    public var myTableView: TableView;
    public var myDataTable: DataTable;
    public var controlPanel: ControlPanel;
    public var momentumView: MomentumView;
    public var module: CollisionLabModule;
    public var returnBallsButtonSprite: Sprite;
    var mySoundMaker: SoundMaker;
    var stageH: Number;
    var stageW: Number;

    public function MainView( myModel: Model, module: CollisionLabModule, stageW: Number, stageH: Number ) {
        this.myModel = myModel;
        this.module = module;
        this.stageW = stageW;
        this.stageH = stageH;
    }

    public function initialize(): void {
        this.myTableView = new TableView( myModel, this );
        this.myDataTable = new DataTable( myModel, this );
        this.controlPanel = createControlPanel( myModel, this );
        this.momentumView = new MomentumView( myModel, this );
        this.mySoundMaker = new SoundMaker( myModel, this );
        this.myModel.resetAll();
        resetPositions();
        this.momentumView.visible = false;

        returnBallsButtonSprite = new DataTableButtonBody();
        var returnBallsButton: * = new NiceButton( returnBallsButtonSprite, 110, function(): void {
            returnBallsButtonSprite.visible = false;
            myModel.returnBalls();
        } );
        returnBallsButtonSprite.visible = false;
        returnBallsButton.setLabel( SimStrings.get( "TableView.returnBalls", "Return Balls" ) );
        addChild( returnBallsButtonSprite );
        returnBallsButtonSprite.x = 330;
        returnBallsButtonSprite.y = myTableView.y + 230;

        myModel.registerView( this ); // get update() called
    }

    public function reset(): void {
        resetPositions();
    }

    public function resetPositions(): void {
        var paddingForTabs: Number = 10; // we need to add padding at the top for the new tab bar
        this.myTableView.x = 0;
        this.myTableView.y = paddingForTabs;
        if ( myModel.isIntro ) {
            this.myDataTable.y = 0.60 * this.stageH + paddingForTabs / 2;
            //this.myDataTable.x = 380; // hardcoded for now, since we have different widths of play areas...
            myDataTable.x = 15;
        }
        else {
//            this.myDataTable.x = 340; // hardcoded for now, since we have different widths of play areas...
            myDataTable.x = 15;
            this.myDataTable.y = 0.75 * this.stageH + paddingForTabs / 2 - 30;
        }
        //this.controlPanel.sub_background.width = 170;
        //this.controlPanel.sub_background.height = 330;
        this.controlPanel.x = this.stageW - 0.75 * this.controlPanel.width;
        this.controlPanel.y = 30 + paddingForTabs;
        this.momentumView.x = 0;
        this.momentumView.y = 0;
    }

    public function update(): void {
        // we need to handle detecting where the actual bounds are in our stage pixels
        var dimensions: Rectangle = CollisionLab.stageDimensions( this );
//        var idealWidth: Number = 950;
//        var idealHeight: Number = 700;
//        var idealRatio: Number = idealWidth / idealHeight;
//        var ratio: Number = stage.stageWidth / stage.stageHeight;
        var leftBound: Number = dimensions.left;
        var rightBound: Number = dimensions.right;
        var topBound: Number = dimensions.top;
        var bottomBound: Number = dimensions.bottom;
//        if ( ratio < idealRatio ) {
//            // width-constrained
//            leftBound = 0;
//            rightBound = idealWidth;
//            topBound = (idealHeight / 2) * (1 - idealRatio / ratio);
//            bottomBound = (idealHeight / 2) * (1 + idealRatio / ratio);
//        }
//        else {
//            // height-constrained
//            topBound = 0;
//            bottomBound = idealHeight;
//            leftBound = (idealWidth / 2) * (1 - ratio / idealRatio);
//            rightBound = (idealWidth / 2) * (1 + ratio / idealRatio);
//        }

        // then make sure at least one ball is inside the area
        var allOutside: Boolean = true;
        for ( var i: Number = 0; i < myModel.nbrBalls; i++ ) {
            var ball: BallImage = myTableView.ballImage_arr[i];
            var location: Point = ball.parent.localToGlobal( new Point( ball.x, ball.y ) ); // snag the ball's global coordinates
            var toTheLeft: Boolean = location.x < leftBound;
            var toTheRight: Boolean = location.x > rightBound;
            var toTheTop: Boolean = location.y < topBound;
            var toTheBottom: Boolean = location.y > bottomBound;
            if ( toTheLeft || toTheRight || toTheTop || toTheBottom ) {
                //trace( i, "outside", toTheLeft, toTheRight, toTheTop, toTheBottom );
            }
            else {
                allOutside = false;
            }
        }
        returnBallsButtonSprite.visible = allOutside;
    }

    public function createControlPanel( myModel: Model, myMainView: MainView ): ControlPanel {
        throw new Error( "abstract" );
    }
}
}