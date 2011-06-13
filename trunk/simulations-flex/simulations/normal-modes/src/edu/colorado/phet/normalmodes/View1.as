package edu.colorado.phet.normalmodes {
import flash.display.*;

//view for Model1, a 1-dimensional array of masses and springs
public class View1 extends Sprite {

    public var myMainView: MainView;		//MainView
    private var myModel1: Model1;			//model for this view

    private var _pixPerMeter: Number;		//scale: number of pixels in 1 meter
    private var LinMeters:Number;           //distance between fixed walls in meters
    private var _LinPix:Number;             //distance between fixed walls in pixels
    private var L0Spring:Number;            //equilibrium length of spring in pixels
    private var _leftEdgeY:Number;          //y-position of leftEdge in pixels measured down from top of screen
    private var _leftEdgeX:Number;          //x-position of leftEdge in pixels measured right from left edge of screen
    private var mass_arr:Array;             //array of massView instances , index 0 = mobile mass 1
    private var spring_arr:Array;           //array of spring sprites
    private var walls:Sprite;               //graphic for the fixed walls

    private var stageW: int;
    private var stageH: int;

    //strings for internationalization
    //public var any_str: String;     //no strings in this view yet

    public function View1( myMainView: MainView, myModel1: Model1 ) {
        this.myMainView = myMainView;
        this.myModel1 = myModel1;
        this.myModel1.registerView( this );
        this.initialize();
    }//end of constructor

    public function initialize(): void {
        this.stageW = this.myMainView.stageW;
        this.stageH = this.myMainView.stageH;
        this.LinMeters =  this.myModel1.L;
        this._LinPix = 0.9*this.stageW;
        this._pixPerMeter = this._LinPix/this.LinMeters;
        this._leftEdgeX = 0.05*this.stageW;
        this._leftEdgeY = 0.3*this.stageH;
        var nMax:int = this.myModel1.nMax;        //maximum number of mobile masses
        this.mass_arr = new Array( nMax );
        //mass graphic drawn in MassView
        for(i =0; i < nMax; i++){
            this.mass_arr[i] = new MassView1( i+1, this.myModel1, this );
        }
        this.spring_arr = new Array( nMax + 1 );  //one more spring than masses
        for(var i:int =0; i <= nMax; i++){       //notice one more spring than nbr masses
            this.spring_arr[i] = new Sprite();
        }
        this.drawSprings();       //need to positions springs behind masses
        this.walls = new Sprite();
        this.drawWalls();

        this.positionGraphics();

        for(i = 0; i <= this.myModel1.nMax; i++){
            this.addChild(this.spring_arr[i]);
        }
        for(i = 0; i < this.myModel1.nMax; i++){
            this.addChild(this.mass_arr[i]);
        }
        this.addChild(this.walls);

        this.initializeControls();
    }//end of initialize()


    private function drawSprings():void{        //only the visible springs are drawn
        //var d:Number = 10;                      //radius of each mass in pixels, all distance in this function in pixels
        var nMasses:Number = this.myModel1.N;    //number of mobile masses in chain
        this.L0Spring = ( this._LinPix )/(nMasses + 1);  //equilibrium length of single spring in pixels
        var leadL:Number = 20;                  //length of each straight end of spring
        var nTurns:Number = 5;                  //number of turns in spring
        var w:Number = (this.L0Spring - 2*leadL)/nTurns;   //width of each turn
        var r:Number = 10;                      //radius of each turn
        for(var i:int = 0; i <= nMasses; i++){
           var g:Graphics = this.spring_arr[i].graphics;
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
         }//end for i
        //make sure that other springs are invisible
        for(i = nMasses + 1; i <= this.myModel1.nMax; i++ ){
            this.spring_arr[i].visible = false;
        }
    }//end drawSprings()

    private function drawWalls():void{
        var g:Graphics = this.walls.graphics;
        var h:Number = 100;  //height of wall in pix
        g.clear();
        g.lineStyle(5, 0x444444, 1);   //gray walls
        g.moveTo(this._leftEdgeX, this._leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX, this._leftEdgeY + h/2);
        g.moveTo(this._leftEdgeX + this._LinPix, this._leftEdgeY - h/2);
        g.lineTo(this._leftEdgeX + this._LinPix, this._leftEdgeY + h/2);
    }

    private function positionGraphics():void{
        var N:int = this.myModel1.N;            //number of visible, mobile masses
        var nMax:int = this.myModel1.nMax;
        //Not necessary to position massView graphics, these are automatically positioned by update();
        for(var i:int = 0; i < N; i++){
            this.mass_arr[i].visible = true;
        }
        for (i = 0; i <= N; i++ ){
            this.spring_arr[i].visible = true;
            var xInMeters:Number = this.myModel1.getX(i);
            var xInPix:Number = xInMeters * this.pixPerMeter;
            this.spring_arr[i].y = this._leftEdgeY;
            this.spring_arr[i].x = this._leftEdgeX + xInPix; //i*separationInPix;
        }
        for(i = N; i < nMax; i++){
            this.mass_arr[i].visible = false;
        }
        for( i = N+1; i <= nMax; i ++ ){
            this.spring_arr[i].visible = false;
        }
    }

    public function setNbrMasses():void{
        this.drawSprings();
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

    public function get leftEdgeX():Number{
        return this._leftEdgeX;
    }

    public function get leftEdgeY():Number{
        return this._leftEdgeY;
    }

    public function update(): void {
        var xInMeters:Number;
        var yInMeters:Number;
        var xInPix:Number;
        var yInPix:Number;
        var springLengthInPix:Number;
        //position masses
        for(var j:int = 0; j < this.myModel1.N; j++){
            var i:int = j+1;    //index of mobile mass, left mass = 1
            xInMeters = this.myModel1.getX(i);       //irrelevant when in transverse mode
            yInMeters = this.myModel1.getY(i);       //irrelevant when in longitudinal mode
            xInPix = this._leftEdgeX + xInMeters*this._pixPerMeter;
            yInPix = this._leftEdgeY -  yInMeters*this._pixPerMeter;   //don't forget. +y direction is down in screen coords, is up in cartesian coords
            this.mass_arr[j].x = xInPix;
            this.mass_arr[j].y = yInPix;
        }//end for loop

        //position springs
        for(i = 0; i <= this.myModel1.N; i++){
            //position left end of spring
            xInMeters = this.myModel1.getX(i);      //irrelevant when in transverse mode
            yInMeters = this.myModel1.getY(i);      //irrelevant when in longitudinal mode
            xInPix = this._leftEdgeX + xInMeters*this._pixPerMeter;
            yInPix = this._leftEdgeY - yInMeters*this._pixPerMeter;
            this.spring_arr[i].x = xInPix;
            this.spring_arr[i].y = yInPix;
            //position right end of spring; when in transverse mode, this requires rotation
            if(this.myModel1.longitudinalMode){
                this.spring_arr[i].rotation = 0;
                springLengthInPix =  (this.myModel1.getX(i+1)-this.myModel1.getX(i))*this.pixPerMeter;
                this.spring_arr[i].scaleX = springLengthInPix/this.L0Spring;
            }else{  //if in transverse mode
                var sprLX:Number = (this.myModel1.getX(i+1)-this.myModel1.getX(i))*this.pixPerMeter;
                var sprLY:Number = (this.myModel1.getY(i+1)-this.myModel1.getY(i))*this.pixPerMeter;
                springLengthInPix = Math.sqrt( sprLX*sprLX + sprLY*sprLY );
                this.spring_arr[i].scaleX = springLengthInPix/this.L0Spring;
                //set rotation of stretched spring
                var angleInDeg:Number = (Math.atan2(-sprLY, sprLX))*180/Math.PI;
                this.spring_arr[i].rotation = angleInDeg;
            }
        }//end for loop

    }//end update()

}//end of class

}//end of package