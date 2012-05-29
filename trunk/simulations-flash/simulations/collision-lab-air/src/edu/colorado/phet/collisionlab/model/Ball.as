//ball object in the Model for Collision Lab

package edu.colorado.phet.collisionlab.model {
import edu.colorado.phet.collisionlab.util.TwoVector;

//import statements go here

public class Ball {
    public var mass: Number;
    public var radius: Number;
    public var position: TwoVector;
    public var velocity: TwoVector;
    public var momentum: TwoVector;
    public var initialPosition: TwoVector;
    public var initialVelocity: TwoVector;

    public function Ball( mass: Number, position: TwoVector, velocity: TwoVector ) {
        this.mass = mass;
        //2D mass = rho*pi*r^2, so r = C*mass^1/2
        //3D mass = rho*(4*pi/3)*r^3, so r = C*mass^1/3
        //this.radius = 0.15*Math.sqrt(this.mass);   //radius in meters
        this.radius = 0.15 * Math.pow( this.mass, 1 / 3 );
        this.position = position;
        this.velocity = velocity;
        this.momentum = new TwoVector( this.mass * this.velocity.getX(), this.mass * this.velocity.getY() );
        setAsInitialState();
    }//end of constructor

    public function setAsInitialState(): void {
        initialPosition = new TwoVector( position.getX(), position.getY() );
        initialVelocity = new TwoVector( velocity.getX(), velocity.getY() );
    }

    //reset position to position at previous timestep
    public function backupOneStep(): void {
        this.position.setXY( this.position.getXLast(), this.position.getYLast() );
    }

    //following to used to reset existing ball object to initial configuration in Model
    public function setBall( mass: Number, position: TwoVector, velocity: TwoVector ): void {
        this.mass = mass;
        this.radius = 0.15 * Math.pow( this.mass, 1 / 3 );
        this.position = position;
        this.velocity = velocity;
        this.momentum = new TwoVector( this.mass * this.velocity.getX(), this.mass * this.velocity.getY() );
        setAsInitialState();
    }

    public function setMass( mass: Number ): void {
        this.mass = mass;
        this.radius = 0.15 * Math.pow( this.mass, 1 / 3 );
        //this.radius = 0.15*Math.sqrt(this.mass);
    }

    public function getMass(): Number {
        return this.mass;
    }

    public function getRadius(): Number {
        return this.radius;
    }

    public function getMomentum(): TwoVector {
        this.momentum.setXY( this.mass * this.velocity.getX(), this.mass * this.velocity.getY() );
        return this.momentum;
    }

    public function getKE(): Number {
        var speed: Number = this.velocity.getLength();
        return 0.5 * this.mass * speed * speed;
    }

    public function reverseVelocity(): void {
        this.velocity.flipVector();
    }

}//end of class

}//end of package