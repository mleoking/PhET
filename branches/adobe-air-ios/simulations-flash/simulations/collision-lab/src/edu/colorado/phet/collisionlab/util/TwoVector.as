//TwoVector.  Simple 2-component real vector
//AS3 has built-in Vector Class, so named my class differently

package edu.colorado.phet.collisionlab.util {

public class TwoVector {
    private var xComponent: Number;
    private var yComponent: Number;
    private var xLast: Number;	//previous xComponent, unused currently
    private var yLast: Number;	//previous yComponent, unused currently
    private var length: Number;
    private var angle: Number;  //angle in degrees, to match rotation property

    public function TwoVector( xComp: Number, yComp: Number ) {
        this.xComponent = xComp;
        this.yComponent = yComp;
        this.xLast = yComp;
        this.yLast = xComp;
        this.setLength();
        this.setAngle();
    }//end of constructor

    public function clone(): TwoVector {
        return new TwoVector( xComponent, yComponent );
    }

    public function initializeXLastYLast(): void {
        this.xLast = this.xComponent;
        this.yLast = this.yComponent;
    }

    private function setLength(): void {
        this.length = Math.sqrt( xComponent * xComponent + yComponent * yComponent );
    }

    private function setAngle(): void {
        this.angle = (180 / Math.PI) * Math.atan2( yComponent, xComponent );
    }

    public function getLength(): Number {
        return this.length;
    }

    public function getAngle(): Number {
        return this.angle;  //angle in degrees
    }

    public function setX( x: Number ): void {
        this.xLast = this.xComponent;
        this.xComponent = x;
        this.setLength();
        this.setAngle();
    }

    public function setY( y: Number ): void {
        this.yLast = this.yComponent;
        this.yComponent = y;
        this.setLength();
        this.setAngle();
    }

    public function setXY( x: Number, y: Number ): void {
        this.xLast = this.xComponent;
        this.yLast = this.yComponent;
        this.xComponent = x;
        this.yComponent = y;
        this.setLength();
        this.setAngle();
    }

    public function flipVector(): void {
        //trace("flipVector() called.");
        this.xComponent *= -1;
        this.yComponent *= -1;
        this.setLength();
        this.setAngle();
    }

    public function getX(): Number {
        return this.xComponent;
    }

    public function getY(): Number {
        return this.yComponent;
    }

    public function getXLast(): Number {
        return this.xLast;
    }

    public function getYLast(): Number {
        return this.yLast;
    }

    public function setXLast( xLast: Number ): void {
        this.xLast = xLast;
    }

    public function setYLast( yLast: Number ): void {
        this.yLast = yLast;
    }


}//end of class

}//end of package