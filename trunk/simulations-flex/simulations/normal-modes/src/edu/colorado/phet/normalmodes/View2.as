/**
 * Created by IntelliJ IDEA.
 * User: General User
 * Date: 6/12/11
 * Time: 6:23 AM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.normalmodes {
import flash.display.Graphics;
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
    private var spring_arr:Array;           //2D array of spring sprites, (N+1) x (N+1) elements
    private var walls:Sprite;               //graphic for the square fixed boundary

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
        this._LinPix = 0.9*this.stageH;
        this._pixPerMeter = this._LinPix/this.LinMeters;
        this._topLeftCornerX = 0.05*this.stageW;
        this._topLeftCornerY = 0.05*this.stageH;
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
        this.spring_arr = new Array( nMax + 1 );  //one more spring than masses
        for( i = 0; i < nMax + 1; i++){
            spring_arr[i] = new Array( nMax + 1 );
        }
        for(i =0; i <= nMax; i++){       //notice one more spring than masses in each row, column
            for(var j:int = 0; j <= nMax; j++){
                this.spring_arr[i][j] = new Sprite();
            }
        }
        //this.drawSprings();       //need to positions springs behind masses
        this.walls = new Sprite();
        this.drawWalls();

        for(i = 0; i <= nMax; i++){
            for(j = 0; j <= nMax; j++){
                this.addChild(this.spring_arr[i][j]);
            }
        }

        for(i = 0; i < nMax; i++){
            for(j = 0; j < nMax; j++){
                this.addChild(this.mass_arr[i][j]);
            }
        }

        this.positionGraphics();
        this.addChild(this.walls);

        this.initializeControls();
    }//end of initialize()

    private function drawSprings():void{        //only the visible springs are drawn
        //var d:Number = 10;                    //radius of each mass in pixels, all distance in this function in pixels
        var nMasses:Number = this.myModel2.N;   //number of mobile masses in 1D chain
        this.L0Spring = ( this._LinPix )/(nMasses + 1);  //equilibrium length of single spring in pixels
        var leadL:Number = 20;                  //length of each straight end of spring
        var nTurns:Number = 5;                  //number of turns in spring
        var w:Number = (this.L0Spring - 2*leadL)/nTurns;   //width of each turn
        var r:Number = 10;                      //radius of each turn
        for(var i:int = 0; i <= nMasses; i++){
            for(var j:int = 0; j <= nMasses; j++){
                var g:Graphics = this.spring_arr[i][j].graphics;
                g.clear();
                g.lineStyle( 2, 0xff0000, 1 );
                g.moveTo( 0, 0 );
                g.lineTo(leadL, 0);
                for(var j:int = 0; j < nTurns; j++){
                    g.lineTo( leadL + j*w + w/4, r );
                    g.lineTo( leadL + j*w + 3*w/4, -r);
                    g.lineTo( leadL + j*w + w,  0 );
                }//end for j
                g.lineTo( this.L0Spring, 0 );
            }//end for j
        }//end for i
        //to make sure that other springs are invisible
        for(i = nMasses + 1; i <= this.myModel2.nMax; i++ ){
            for(j = nMasses + 1; j <= this.myModel2.nMax; j++){
                this.spring_arr[i][j].visible = false;
            }
        }
    }//end drawSprings()

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

    private function positionGraphics():void{
        var N:int = this.myModel2.N;            //N x N = number of visible masses
        var nMax:int = this.myModel2.nMax;      //nMax x nMax = max possible number of visible masses
        var separationInPix:Number = this._LinPix/(N + 1);   //center-to-center separation of mobile masses in row or column
        //make all invisible and then make visible only those required.
        for(i = 0; i < nMax; i++){
            for(j = 0; j < nMax; j++){
                this.mass_arr[i][j].visible = true;
            }
        }

        for(var i:int = 0; i < N; i++){
            for(var j:int = 0; j < N; j++){
                this.mass_arr[i][j].visible = true;
                trace("View2.positionGraphics. Visible i = " + i + "   j = " + j);
                trace( "index0 = "+this.mass_arr[i][j].iJIndices[0]+ "   index1 = "+this.mass_arr[i][j].iJIndices[1])
                var iIndex:int = this.mass_arr[i][j].iJIndices[0];
                var jIndex:int = this.mass_arr[i][j].iJIndices[1];
                this.mass_arr[i][j].x = this._topLeftCornerX + jIndex*separationInPix;
                this.mass_arr[i][j].y = this._topLeftCornerY + iIndex*separationInPix;
            }
        }
//        for (i = 0; i <= N; i++ ){
//            for (j = 0; j <= N; j++){
//                this.spring_arr[i][j].visible = true;
//                this.spring_arr[i][j].x = this._topLeftCornerX + i*separationInPix;
//                this.spring_arr[i][j].y = this._topLeftCornerY + j*separationInPix;
//            }
//        }

//        for( i = N+1; i <= nMax; i++ ){
//            for( j = N+1; j <= nMax; j++ ){
//                this.spring_arr[i][j].visible = false;
//            }
//        }
    } //end positionGraphics()

    public function setNbrMasses():void{
        //this.drawSprings();
        this.positionGraphics();
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
        //position masses
        for(var i:int = 0; i < N; i++){
            for(var j:int = 0; j < N; j++){
                var u:int = i+1;    //u, v are indices of mobile masses
                var v:int = j+1;    //top left mobilel masses is u=1, v=1
                xInMeters = this.myModel2.getXY(u, v)[0];
                yInMeters = this.myModel2.getXY(u, v)[1];
                xInPix = this._topLeftCornerX + xInMeters*this._pixPerMeter;
                yInPix = this._topLeftCornerY + yInMeters*this._pixPerMeter;   //+y direction is down in screen coords, in this case, ALSO DOWN in cartesian coords
                this.mass_arr[u][v].x = xInPix;
                this.mass_arr[u][v].y = yInPix;
            } //end for j loop
        }//end for i loop

        //position springs, each mass has spring to right, and spring downward
        for(i = 0; i <= N; i++){
            for(j = 0; j <= N; j++){
                /*
                //position mass end of spring
                xInMeters = this.myModel2.getXY(i, j)[0];
                yInMeters = this.myModel2.getXY(i, j)[1];
                xInPix = this._topLeftCornerX + xInMeters*this._pixPerMeter;
                yInPix = this._topLeftCornerY + yInMeters*this._pixPerMeter;
                this.spring_arr[i][j].x = xInPix;
                this.spring_arr[i][j].y = yInPix;
                //position right end of spring; this requires rotation
                var sprLX:Number = (this.myModel2.getXY(i+1)-this.myModel2.getXY(i))*this.pixPerMeter;
                var sprLY:Number = (this.myModel2.getXY(i+1)-this.myModel2.getXY(i))*this.pixPerMeter;
                springLengthInPix = Math.sqrt( sprLX*sprLX + sprLY*sprLY );
                this.spring_arr[i][j].scaleX = springLengthInPix/this.L0Spring;
                //set rotation of stretched spring
                var angleInDeg:Number = (Math.atan2(-sprLY, sprLX))*180/Math.PI;
                this.spring_arr[i][j].rotation = angleInDeg;
                */
            } //end for j loop
        }//end for i loop

    }//end update()

}//end of class
}
