package edu.colorado.phet.electrichockey;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Vector;

public class Model {
    private ElectricHockeyApplication electricHockeyApplication;
    private int fieldWidth, fieldHeight;
    private int barrierState;
    private boolean goalState;
    private boolean collisionState;
    private boolean traceState;
    private boolean pathStarted;
    private boolean puckMoving;
    private GeneralPath path;
    private Vector chargeList;    //methods of Vector class: elementAt(int), size(), removeElementAt(int), addElement(Object)
    //private Vector displacementList;
    private Vector forceList;    //list of forces on positivePuckImage
    //private final Point initialPuckPosition;
    private final Point2D initialPuckPosition2D;
    private Point puckPosition;
    private Point2D puckPosition2D;
    private Charge puck;
    private double mass = 25.0;            //mass of positivePuckImage
    private Force netForce;        //net force on positivePuckImage
    private double fFactor = 0.0020; //arbitrary multiplicative factor in acceleration, adjusted for correct speed of positivePuckImage
    private javax.swing.Timer timer;        //timer controlling motion of positivePuckImage
    private int dt = 3;                  //time step in milliseconds
    private int time;                      //total time in milliseconds
    private double x, y;                //instantaneous positivePuckImage position
    private double xBefore, yBefore;    //position in prior step
    private double xTemp, yTemp;        //temporary storage of x, y
    private boolean starting;
    private double vX, vY;      //x, y components of velocity and acceleration of positivePuckImage

    public Model( int width, int height, ElectricHockeyApplication electricHockeyApplication ) {
        this.electricHockeyApplication = electricHockeyApplication;
        this.fieldWidth = width;
        this.fieldHeight = height;
        chargeList = new Vector();
        //displacementList = new Vector();
        forceList = new Vector();

        goalState = false;
        collisionState = false;
        puckMoving = false;
        puckPosition = new Point( fieldWidth / 5, fieldHeight / 2 );
        initialPuckPosition2D = new Point2D.Double( (double)fieldWidth / 5, (double)fieldHeight / 2 );
        puckPosition2D = initialPuckPosition2D;

        pathStarted = false;
        path = new GeneralPath();

        puck = new Charge( puckPosition2D, Charge.POSITIVE );
        Force netForce;        //net force on positivePuckImage

        x = puckPosition2D.getX();
        y = puckPosition2D.getY();
        starting = true;
        //x = (double)puckPosition.x;
        //y = (double)puckPosition.y;
        vX = 0.0;
        vY = 0.0;   //initial double velocity is zero

        timer = new javax.swing.Timer( dt, new timerHandler() );
    }//end of constructor

/*
	public void updateDisplacementList()
	{
		for (int i = 0; i < chargeList.size(); i++)
		{
			edu.colorado.phet.ehockey.Charge charge = (edu.colorado.phet.ehockey.Charge)chargeList.elementAt(i);
			Point chargePt = charge.getPosition();
			Point displacement = new  Point(positivePuckImage.getPosition().x - chargePt.x, positivePuckImage.getPosition().y - chargePt.y);
			displacementList.setElementAt(displacement, i);
		}
	}
*/

    public void updateForceList() {
        for( int i = 0; i < forceList.size(); i++ ) {
            Charge chargeI = (Charge)chargeList.elementAt( i );
            Force forceI = new Force( chargeI, puck );
            forceList.setElementAt( forceI, i );
        }
    }

    public void addCharge( Charge charge ) {
        chargeList.addElement( charge );
        Force force = new Force( charge, puck );
        forceList.addElement( force );
        //updateForceList();
    }

    public void removeChargeAt( int i ) {
        chargeList.removeElementAt( i );
        forceList.removeElementAt( i );
        //updateForceList();
    }

    public int getChargeListSize() {
        //if(chargeList.isEmpty()) return 0;
        return chargeList.size();
    }

    public int getForceListSize() {
        return forceList.size();
    }

    public Charge getChargeAt( int i ) {
        Charge charge = (Charge)chargeList.elementAt( i );
        return charge;
    }

    public Force getForceAt( int i ) {
        Force force = (Force)forceList.elementAt( i );
        return force;
    }

    public Vector getChargeList() {
        return chargeList;
    }

    public Vector getForceList() {
        return forceList;
    }

    public Charge getPuck() {
        return puck;
    }

    public double getMass() {
        return mass;
    }

    public void setMass( double m ) {
        mass = m;
    }

    public Point getPuckPosition() {
        return puckPosition;
    }

    public Force getNetForce() {
        double netXComp = 0;
        double netYComp = 0;
        for( int i = 0; i < forceList.size(); i++ ) {
            Force forceI = (Force)forceList.elementAt( i );
            netXComp += forceI.getXComp();
            netYComp += forceI.getYComp();
        }
        netForce = new Force( netXComp, netYComp );
        return netForce;
    }

    public void updatePuckPositionVerlet() {
        if( starting ) {
            Force F = getNetForce();
            xBefore = x;
            yBefore = y;
            x += ( fFactor * F.getXComp() / ( 2.0 * mass ) ) * dt * dt;
            y += ( fFactor * F.getYComp() / ( 2.0 * mass ) ) * dt * dt;
            starting = false;
        }
        else {
            Force F = getNetForce();
            xTemp = x;
            yTemp = y;
            x = 2 * x - xBefore + ( fFactor * F.getXComp() / mass ) * dt * dt;
            y = 2 * y - yBefore + ( fFactor * F.getYComp() / mass ) * dt * dt;
            xBefore = xTemp;
            yBefore = yTemp;

            puck.setPosition2D( new Point2D.Double( x, y ) );
            puck.setPosition( new Point( (int)x, (int)y ) );
            updateForceList();

            if( x > 3 * fieldWidth || x < -3 * fieldWidth || y > 3 * fieldHeight || y < -3 * fieldHeight ) {
                stopTimer();
            }
        }
    }

    public void updatePuckPositionEuler() {
        //double x = puckPosition.x;
        //double y = puckPosition.y;
        Force F = getNetForce();

        vX += ( fFactor * F.getXComp() / mass ) * dt;
        vY += ( fFactor * F.getYComp() / mass ) * dt;
        x += vX * dt + ( fFactor * F.getXComp() / ( 2.0 * mass ) ) * dt * dt;
        y += vY * dt + ( fFactor * F.getYComp() / ( 2.0 * mass ) ) * dt * dt;

        puck.setPosition2D( new Point2D.Double( x, y ) );
        puck.setPosition( new Point( (int)x, (int)y ) );
        updateForceList();        //Attention! Rounding error in updated forces because puckPosition integer

        if( x > 3 * fieldWidth || x < -3 * fieldWidth || y > 3 * fieldHeight || y < -3 * fieldHeight ) {
            stopTimer();
        }

    }

    public void updatePath() {
        if( !pathStarted ) {
            path.moveTo( puck.getPosition().x, puck.getPosition().y );
            pathStarted = true;
        }
        else {
            path.lineTo( puck.getPosition().x, puck.getPosition().y );
        }
    }

    public void setPathStarted( boolean pathStarted ) {
        this.pathStarted = pathStarted;
    }

    public GeneralPath getPath() {
        return path;
    }

    public void setBarrierState( int barrierState ) {
        this.barrierState = barrierState;
        switch( this.barrierState ) {
            case 0:
                BarrierList.currentCollisionArray = BarrierList.collisionArray0;
                break;
            case 1:
                BarrierList.currentCollisionArray = BarrierList.collisionArray1;
                break;
            case 2:
                BarrierList.currentCollisionArray = BarrierList.collisionArray2;
                break;
            case 3:
                BarrierList.currentCollisionArray = BarrierList.collisionArray3;
                break;
        }
    }

    public void startTimer() {
        timer.start();
        puckMoving = true;
        prt( "Timer started" );
    }

    public void stopTimer() {
        timer.stop();
        puckMoving = false;
    }

    public void resetTimer() {
        timer.stop();
        puckMoving = false;
        time = 0;
        x = initialPuckPosition2D.getX();
        y = initialPuckPosition2D.getY();
        //x = (double)initialPuckPosition.x;
        //y = (double)initialPuckPosition.y;
        vX = 0.0;
        vY = 0.0;
        //puckPosition = initialPuckPosition;
        puck.setPosition2D( initialPuckPosition2D );
        puck.setPosition( new Point( (int)initialPuckPosition2D.getX(), (int)initialPuckPosition2D.getY() ) );
        updateForceList();
        path.reset();
        pathStarted = false;
        starting = true;
        goalState = false;
        collisionState = false;
        prt( "positivePuckImage reset y = " + puckPosition2D.getY() );
        electricHockeyApplication.getPlayingField().paintAgain();
    }

    class timerHandler implements ActionListener {
        public void actionPerformed( ActionEvent aevt ) {
            time++;
            updatePuckPositionVerlet();
            if( electricHockeyApplication.getControlPanel().getTraceState() ) {
                updatePath();
            }
            int x = puck.getPosition().x;
            int y = puck.getPosition().y;
            //prt("x=" + x);
            if( x > 0 && x < fieldWidth && y > 0 && y < fieldHeight ) {
                if( BarrierList.currentCollisionArray[x][y] == 1 ) {
                    prt( "Collision!" );
                    collisionState = true;
                    if( electricHockeyApplication.getCork() != null ) {
                        electricHockeyApplication.getCork().play();
                    }
                    electricHockeyApplication.getPlayingField().paintAgain();
                    stopTimer();
                }
            }
            if( electricHockeyApplication.getPlayingField().goal.contains( puck.getPosition() ) ) {
                prt( "Goal!" );
                goalState = true;
                if( electricHockeyApplication.getTada()!= null ) {
                    electricHockeyApplication.getTada().play();
                }
                electricHockeyApplication.getPlayingField().paintAgain();
                stopTimer();
            }
            //if(time%4 == 0)		//use to paint at intervals
            electricHockeyApplication.getPlayingField().paintAgain(); //

        }
    }//end of timerHandler()


    public boolean getCollisionState() {
        return collisionState;
    }

    public boolean getGoalState() {
        return goalState;
    }

    public boolean getPuckMovingState() {
        return puckMoving;
    }

    public void prt( String str ) {
        System.out.println( str );
    }

}//end of public class
