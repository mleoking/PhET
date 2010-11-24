//package edu.colorado.phet.resonance.model {

package{
//model of a mass on a spring, with one end of spring fixed, other end attached to mass
public class MassSpringModel {

    private var m:Number; 		//mass in kg
    private var k:Number; 		//spring constant in N/m
    private var L0:Number;		//unstretched length of spring in meters
    private var y0:Number;		//position of fixed end of spring
    private var y:Number;		//position of mass end of spring  (up is positive direction)
    private var b:Number;		//drag constant; f_drag = - b*v
    private var g:Number;		//accleration due to gravity
    private var t:Number;		//time in seconds
    private var tRate:Number;	//1 = real time; 0.25 = 1/4 of real time, etc.
    private var dt:Number;  	//time step in seconds
	private var v:Number;		//velocity of mass
	private var a:Number;		//acceleration of mass
	private var ORIENTATION:String; //UP or DOWN  (DOWN if mass is hanging)

    public function MassSpringModel(m:Number, k:Number, L0:Number, b:Number, ORIENTATION:String) {
        this.m = m;
        this.k = k;
        this.L0 = L0;
        this.b = b;
		this.ORIENTATION = "UP";
        this.initialize();

    }//end of constructor

    private function initialize():void {
        this.g = 9.8;
        this.y0 = 0;
		if(this.ORIENTATION == "UP"){
			this.y = (-m * g / k) + L0 + y0;  //start with mass hanging in equilibrium position
		}else{
        	this.y = (-m * g / k) - L0 + y0;
		}
        this.t = 0;

    }//end of initialize()
	
	public function setOrientation(ORIENTATION:String):void{
		this.ORIENTATION = ORIENTATION;
	}

    public function stepForward():void {

    }

}//end of class
}//end of package