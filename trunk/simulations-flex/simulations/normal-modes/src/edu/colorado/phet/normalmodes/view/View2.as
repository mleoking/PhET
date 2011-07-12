/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/12/11
 * Time: 6:23 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes.view {
import edu.colorado.phet.normalmodes.model.Model2;

import flash.display.Graphics;
import flash.display.LineScaleMode;
import flash.display.Sprite;

public class View2 extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel2: Model2;			//model for this view

    private var _pixPerMeter: Number;		//scale: number of pixels in 1 meter
    private var LinMeters:Number;           //distance between fixed walls in meters
    private var _LinPix:Number;             //distance between fixed walls in pixels
    private var L0Spring:Number;            //equilibrium length of spring in pixels
    private var _topLeftCornerY:Number;     //y-position of topLeftCorner of boundary wall in pixels measured down from top of screen
    private var _topLeftCornerX:Number;     //x-position of topLeftCorner of boundary wall in pixels measured right from left edge of screen
    private var mass_arr:Array;             //2D array of massView instances, N x N elements index 0, 0  = mobile mass in 1st row, 1st column
    private var springH_arr:Array;          //2D array of horizontal spring sprites, (N+1) x (N+1) elements
    private var springV_arr:Array;          //2D array of vertical spring sprites, (N+1) x (N+1) elements
    private var walls:Sprite;               //graphic for the square fixed boundary
    private var _springsVisible:Boolean;     //true if springs are shown

    private var stageW: int;
    private var stageH: int;

    //strings for internationalization
    //public var any_str: String;     //no strings in this view yet

    public function View2( myMainView: MainView, myModel2: Model2 ) {
        this.myMainView = myMainView;
        this.myModel2 = myModel2;
        this.myModel2.registerView( this );
        this.initialize();
    }//end of constructor

    public function initialize(): void {
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.LinMeters =  this.myModel2.L;
        this._LinPix = 0.85*this.stageH;
        this._pixPerMeter = this._LinPix/this.LinMeters;
        this._springsVisible = true;
        this._topLeftCornerX = 0.04*this.stageW;
        this._topLeftCornerY = 0.095*this.stageH;
        var nMax:int = this.myModel2.nMax;        //maximum number of mobile masses
        //create mass array and graphics
        this.mass_arr = new Array( nMax );
        for(var i:int = 0; i < nMax; i++){
            mass_arr[i] = new Array( nMax );
        }
        //mass graphic drawn in MassView
        for(i =0; i < nMax; i++){
            for(j = 0; j < nMax; j++){
               this.mass_arr[i][j] = new MassView2( i+1, j+1, this.myModel2, this );
            }
        }

        //create spring array and graphics
        this.springH_arr = new Array( nMax );       //same number of horizontal spring rows as masses
        this.springV_arr = new Array( nMax + 1 );   //one more row of Vertical springs than masses
        for( i = 0; i < nMax; i++){
            springH_arr[i] = new Array( nMax + 1 );  //one more column of horizontal springs than masses
        }
        for( i = 0; i < nMax + 1; i++){
            springV_arr[i] = new Array( nMax );  //same number columns of vertical springs as masses
        }
        for(i =0; i < nMax; i++){
            for(var j:int = 0; j <= nMax; j++){
                this.springH_arr[i][j] = new Sprite();
                this.springV_arr[j][i] = new Sprite();      //notice intentional switch of i, j.
            }
        }
        this.drawSprings();       //need to positions springs behind masses
        this.walls = new Sprite();
        this.drawWalls();

        for(i = 0; i < nMax; i++){
            for(j = 0; j <= nMax; j++){
                this.addChild(this.springH_arr[i][j]);
                this.addChild(this.springV_arr[j][i]);       //switch of i,j intentional
            }
        }

        for(i = 0; i < nMax; i++){
            for(j = 0; j < nMax; j++){
                this.addChild(this.mass_arr[i][j]);
            }
        }

        //this.showMasses();
        this.addChild(this.walls);

        this.initializeControls();
    }//end of initialize()

    private function drawSprings():void{        //only the visible springs are drawn
        //trace("View2.drawSprings() called.");
        //var d:Number = 10;                    //radius of each mass in pixels, all distance in this function in pixels
        var nMasses:Number = this.myModel2.N;   //number of mobile masses in 1D chain
        this.L0Spring = ( this._LinPix )/(nMasses + 1);  //equilibrium length of single spring in pixels
        var leadL:Number = 20;                  //length of each straight end of spring
        var nTurns:Number = 5;                  //number of turns in spring
        var w:Number = (this.L0Spring - 2*leadL)/nTurns;   //width of each turn
        var r:Number = 5;                      //radius of each turn
        //to make sure that other springs are invisible
        this.makeAllSpringsInvisible();
        //draw only those springs that are visible
        for(var i:int = 0; i < nMasses; i++){
            for(var j:int = 0; j <= nMasses; j++){
                if(this._springsVisible){
                    this.springH_arr[i][j].visible = true;
                    this.springV_arr[j][i].visible = true;
                }
                var gH:Graphics = this.springH_arr[i][j].graphics;
                var gV:Graphics = this.springV_arr[j][i].graphics;      //i, j switched intentionally
                var g_arr:Array = new Array( gH,  gV );
                for( var k:int = 0; k < 2; k++ ){
                    var g:Graphics = g_arr[k];
                    g.clear();
                    g.lineStyle( 2, 0xffcc32, 1, true, LineScaleMode.NONE );
                    g.moveTo( 0, 0 );
                    g.lineTo(leadL, 0);
                    for(var t:int = 0; t < nTurns; t++){
                        g.lineTo( leadL + t*w + w/4, r );
                        g.lineTo( leadL + t*w + 3*w/4, -r);
                        g.lineTo( leadL + t*w + w,  0 );
                    }//end for t
                    g.lineTo( this.L0Spring, 0 );
                }//end for k
            }//end for j
        }//end for i
        //this.update();  //in case sim is paused
    }//end drawSprings()

    private function makeAllSpringsInvisible():void{
        for(var i:int = 0; i < this.myModel2.nMax; i++ ){
            for(var j:int = 0; j <= this.myModel2.nMax; j++){
                this.springH_arr[i][j].visible = false;
                this.springV_arr[j][i].visible = false;     //i, j switched
                this.springV_arr[j][i].rotation = 0;        //rotation performed in update
            }
        }
    }

    private function drawWalls():void{
        var g:Graphics = this.walls.graphics;
        var h:Number = this._LinPix;  //height of boundary in pix
        var w:Number = this._LinPix;  //width of boundary in pix
        g.clear();
        g.lineStyle(5, 0x444444, 1);   //gray walls
        g.moveTo(this._topLeftCornerX, this._topLeftCornerY );
        g.lineTo(this._topLeftCornerX + w, this._topLeftCornerY );
        g.lineTo(this._topLeftCornerX + w, this._topLeftCornerY + h);
        g.lineTo(this._topLeftCornerX, this._topLeftCornerY + h);
        g.lineTo(this._topLeftCornerX, this._topLeftCornerY);
    }

    private function showMasses():void{
        var N:int = this.myModel2.N;            //N x N = number of visible masses
        var nMax:int = this.myModel2.nMax;      //nMax x nMax = max possible number of visible masses
        var separationInPix:Number = this._LinPix/(N + 1);   //center-to-center separation of mobile masses in row or column
        //make all masses invisible and then make visible only those required.
        //no need to position masses or springs, since this is done automatically in update()
        for(i = 0; i < nMax; i++){
            for(j = 0; j < nMax; j++){
                this.mass_arr[i][j].visible = false;
            }
        }

        for(var i:int = 0; i <= N - 1; i++){
            for(var j:int = 0; j <= N - 1; j++){
                this.mass_arr[i][j].visible = true;
            }
        }
    } //end showMasses()

    public function setNbrMasses():void{
        this.drawSprings();
        this.showMasses();
    }

    public function set springsVisible( tOrF: Boolean):void{
        this._springsVisible = tOrF;
        if(!tOrF ){
           this.makeAllSpringsInvisible();
        }else{
           this.drawSprings();
        }
    }


    public function initializeControls(): void {
        //trace("initializeControls() called");

        this.update();
    }

    public function get pixPerMeter():Number{
        return this._pixPerMeter;
    }

    public function get LinPix():Number{
        return this._LinPix;
    }

    public function get topLeftCornerX():Number{
        return this._topLeftCornerX;
    }

    public function get topLeftCornerY():Number{
        return this._topLeftCornerY;
    }

    public function update(): void {
        var xInMeters:Number;
        var yInMeters:Number;
        var xInPix:Number;
        var yInPix:Number;
        var springLengthInPix:Number;
        var N:int =  this.myModel2.N;

        if( this.myModel2.nChanged ){
            this.setNbrMasses();
            //this.myModel2.nChanged = false;
        }

        //position masses
        for(var i:int = 0; i < N; i++){
            for(var j:int = 0; j < N; j++){
                var u:int = i+1;    //u, v are indices of mobile masses
                var v:int = j+1;    //top left mobile mass is u=1, v=1
                xInMeters = this.myModel2.getXY(u, v)[0];
                yInMeters = this.myModel2.getXY(u, v)[1];
                xInPix = this._topLeftCornerX + xInMeters*this._pixPerMeter;
                yInPix = this._topLeftCornerY + yInMeters*this._pixPerMeter;   //+y direction is down in screen coords, in this case, ALSO DOWN in cartesian coords
                this.mass_arr[i][j].x = xInPix;
                this.mass_arr[i][j].y = yInPix;
            } //end for j loop
        }//end for i loop

        //position horizontal springs and vertical springs
        //Start with horizontal springs, N rows, N+1 columns
        if( _springsVisible ){
            for(i = 0; i < N; i++){
                for(j = 0; j <= N; j++){
                    //position left ends of horizontal springs
                    xInMeters = this.myModel2.getXY( i + 1, j )[0];       //0th row has no springs
                    yInMeters = this.myModel2.getXY( i + 1, j )[1];
                    xInPix = this._topLeftCornerX + xInMeters*this._pixPerMeter;
                    yInPix = this._topLeftCornerY + yInMeters*this._pixPerMeter;
                    this.springH_arr[i][j].x = xInPix;
                    this.springH_arr[i][j].y = yInPix;

                    //position right edge of horizontal springs; this requires rotation
                    var sprLX:Number = (this.myModel2.getXY(i + 1, j+1)[0] - this.myModel2.getXY(i + 1, j)[0])*this.pixPerMeter;
                    var sprLY:Number = (this.myModel2.getXY(i + 1, j+1)[1] - this.myModel2.getXY(i + 1, j)[1])*this.pixPerMeter;
                    springLengthInPix = Math.sqrt( sprLX*sprLX + sprLY*sprLY );
                    this.springH_arr[i][j].scaleX = springLengthInPix/this.L0Spring;
                    //set rotation of stretched horizontal spring
                    var angleInDeg:Number = (Math.atan2(sprLY, sprLX))*180/Math.PI;
                    this.springH_arr[i][j].rotation = angleInDeg;

                } //end for j loop
            }//end for i loop

            //position vertical springs, N+1 rows, N columns
            for(i = 0; i <= N; i++){
                for(j = 0; j < N; j++){
                    //position top ends of vertical springs
                    xInMeters = this.myModel2.getXY( i, j + 1 )[0];       //0th column has no vertical springs
                    yInMeters = this.myModel2.getXY( i, j + 1 )[1];
                    xInPix = this._topLeftCornerX + xInMeters*this._pixPerMeter;
                    yInPix = this._topLeftCornerY + yInMeters*this._pixPerMeter;
                    this.springV_arr[i][j].x = xInPix;
                    this.springV_arr[i][j].y = yInPix;

                    //position bottom end of vertical springs; this requires rotation
                    sprLX = (this.myModel2.getXY(i+1, j+1)[0] - this.myModel2.getXY(i, j+1)[0])*this.pixPerMeter;
                    sprLY = (this.myModel2.getXY(i+1, j+1)[1] - this.myModel2.getXY(i, j+1)[1])*this.pixPerMeter;
                    springLengthInPix = Math.sqrt( sprLX*sprLX + sprLY*sprLY );
                    this.springV_arr[i][j].scaleX = springLengthInPix/this.L0Spring;
                    //set rotation of stretched vertical spring
                    angleInDeg = 90 - (Math.atan2(sprLX, sprLY))*180/Math.PI;
                    this.springV_arr[i][j].rotation = angleInDeg;

                } //end for j loop
            }//end for i loop
        }//end if(_springsVisible)
    }//end update()

}//end of class
}
