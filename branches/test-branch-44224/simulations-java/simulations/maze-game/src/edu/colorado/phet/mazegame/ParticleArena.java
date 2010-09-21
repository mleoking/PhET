package edu.colorado.phet.mazegame;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class ParticleArena extends JPanel implements Runnable {
    private MazeGameSimulationPanel mazeGUI1;
    private ScorePanel scrPanel;
    private Particle myParticle;
    private int radius;                //radius of particle (and goal)
    private int barrierState;         //Barrier State is level 0, 1, 2, or 3
    private Color barrierColor;
    private boolean collisionDetected;
    private boolean goalDetected;
    private int nbrCollisions;        //number of collisions with walls during play
    private int lastX, lastY;        //last location
    private int collisionX;            //X, Y location just before collision
    private int collisionY;

    boolean setToPlayTada = true;  //used to prevent multiple restarting audioclip
    private int goalX = MazeGameSimulationPanel.fullWidth / 8;     //position of goal
    private int goalY = 3 * MazeGameSimulationPanel.fullHeight / 8;
    private ControlBoxPanel cbPanel;

    private PositionUpdater pUpdater;
    private Thread myThread;
    private int timeStep = 30;

    private double positionFactor = 1.5;        //"gain" of controller arrow in Position state
    private double vTimeStep = (double) ( 0.0010 * timeStep );    //timeStep for velocity update
    private double aTimeStep = (double) ( 0.0010 * timeStep ); //timeStep for acceleration update

    private Border raisedBevel, compound2;
    private Font arenaFont = new PhetFont( 25, true );


    public ParticleArena( MazeGameSimulationPanel mazeGUI1 ) {

        this.mazeGUI1 = mazeGUI1;
        setBackground( new Color( 255, 232, 45 ) );
        barrierColor = new Color( 50, 50, 250 );
        raisedBevel = BorderFactory.createRaisedBevelBorder();
        compound2 = BorderFactory.createTitledBorder( raisedBevel, MazeGameResources.getString( "ParticleArena.ArenaBorder" ), TitledBorder.CENTER, TitledBorder.ABOVE_BOTTOM );
        setBorder( compound2 );

        collisionDetected = false;
        goalDetected = false;
        nbrCollisions = 0;
        cbPanel = new ControlBoxPanel( this );
        scrPanel = new ScorePanel( this, cbPanel );
        pUpdater = new PositionUpdater( positionFactor * cbPanel.getDeltaX() + MazeGameSimulationPanel.fullWidth / 2, positionFactor * cbPanel.getDeltaY() + MazeGameSimulationPanel.fullHeight / 4 );  //argument is initial positionudio

        myParticle = new Particle( pUpdater.getX(), pUpdater.getY() );
        radius = myParticle.getRadius();
    }

    public void start() {
        if ( myThread == null ) {
            myThread = new Thread( this );
            myThread.start();
        }
    }

    public void stop() {
        if ( myThread != null ) {
            myThread = null;
        }
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
        while ( myThread == thisThread ) {
            if ( cbPanel.getControlState() == ControlBoxPanel.POSITION ) {
                double X = positionFactor * cbPanel.getDeltaX() + MazeGameSimulationPanel.fullWidth / 2;
                double Y = positionFactor * cbPanel.getDeltaY() + MazeGameSimulationPanel.fullHeight / 4;
                //myParticle.setXY((int)cbPanel.getDeltaX() + 200, (int)cbPanel.getDeltaY() + 100);
                if ( pUpdater != null ) {
                    pUpdater.updateWithPos( X, Y );
                }

            }
            if ( cbPanel.getControlState() == ControlBoxPanel.VELOCITY ) {
                double vX = cbPanel.getDeltaX();
                double vY = cbPanel.getDeltaY();
                pUpdater.updateWithVel( vX, vY, vTimeStep );

            }
            if ( cbPanel.getControlState() == ControlBoxPanel.ACCELERATION ) {
                double aX = cbPanel.getDeltaX();
                double aY = cbPanel.getDeltaY();
                pUpdater.updateWithAcc( aX, aY, aTimeStep );
            }
            else {
            }

            myParticle.setXY( pUpdater.getIntX(), pUpdater.getIntY() );

            //Check for barrier collision*******************************************************
            int x = (int) myParticle.getX();
            int y = (int) myParticle.getY();
            int w = 20;  //width for checking if disqualified
            if ( x > 0 && x < MazeGameSimulationPanel.fullWidth && y > 0 && y < MazeGameSimulationPanel.fullHeight / 2 )  //check that particle is inside arena window
            {
                if ( BarrierList.currentCollisionArray[x][y] == 0 && !collisionDetected ) {
                    lastX = x;
                    lastY = y;         //record last position
                }
                else if ( BarrierList.currentCollisionArray[x][y] > 0 && !collisionDetected ) {
                    collisionDetected = true;
                    collisionX = lastX;
                    collisionY = lastY;
                    nbrCollisions += 1;
                    scrPanel.nbrCollisionsLbl.setText( new Integer( nbrCollisions ).toString() );
//                    System.out.println("# collisions = " + nbrCollisions);
                    if ( mazeGUI1.cork != null && scrPanel.soundOn ) {
                        mazeGUI1.cork.play();
                    }
                    //setToPlayBang = false;
                }
                else if ( BarrierList.currentCollisionArray[x][y] == 0 && collisionDetected ) {
                    if ( ( x < collisionX + w ) && ( x > collisionX - w ) &&
                         ( y < collisionY + w ) && ( y > collisionY - w ) ) {
                        collisionDetected = false;

                        lastX = x;
                        lastY = y;
                        //System.out.println("Qualified!");
                    }
                    else {

                        collisionDetected = true;
                        //System.out.print("Disqualified." + collisionX + " " + collisionY + " ");
                        //System.out.println("x = " + x + " y = " + y);
                    }
                }


            }//end of barrier collision check****************************************************

            if ( x < ( goalX + radius ) && x > ( goalX - radius ) && y < ( goalY + radius ) && y > ( goalY - radius ) ) {
                if ( setToPlayTada && !collisionDetected ) {

                    if ( mazeGUI1.figaro != null && scrPanel.soundOn ) {
                        mazeGUI1.figaro.play();
                    }
                    goalDetected = true;

                    scrPanel.myClock1.stop();
                    double finalTime = scrPanel.myClock1.getTime();
                    double finalScore = finalTime + 5 * nbrCollisions;
                    scrPanel.ptsScoreLbl.setText( new Double( finalScore ).toString() );
                    setToPlayTada = false;
                }
                else {
                    goalDetected = true;

                }
            }
            else {
                goalDetected = false;

                setToPlayTada = true;
            }


            repaint();
            try {
                Thread.sleep( timeStep );
            }  //millisec interval between mouse events
            catch( Exception e ) {
            }
        }//end of while loop
    }//end of run()

    public ControlBoxPanel getControlBoxPanel() {
        return cbPanel;
    }

    public ScorePanel getScorePanel() {
        return scrPanel;
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
        repaint();

    }

    public double getPositionFactor() {
        return this.positionFactor;
    }

    public void setCollisionDetected( boolean b ) {
        this.collisionDetected = b;
    }

    public void setGoalDetected( boolean b ) {
        this.goalDetected = b;
    }

    public void setNbrCollisions( int n ) {
        this.nbrCollisions = n;
    }

    public double getCurrentX() {
        return myParticle.getX();
    }

    public double getCurrentY() {
        return myParticle.getY();
    }


    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );  //necessary for drawing background!

        //draw barriers
        g.setColor( barrierColor );
        for ( int i = 0; i < BarrierList.currentRectArray[barrierState].length; i++ ) {
            int x = BarrierList.currentRectArray[barrierState][i].x;
            int y = BarrierList.currentRectArray[barrierState][i].y;
            int w = BarrierList.currentRectArray[barrierState][i].width;
            int h = BarrierList.currentRectArray[barrierState][i].height;
            g.fill3DRect( x, y, w, h, false );    //last argument: true = raised, false = sunken; doesn't work!
        }

        //Draw collision report
        if ( collisionDetected ) {
            //g.setColor(Color.yellow);
            //g.fillOval(collisionX - 15, collisionY - 15, 30, 30);
            g.drawImage( mazeGUI1.splat, collisionX - 15, collisionY - 15, this );
            g.setColor( Color.red );
            g.setFont( arenaFont );
            g.drawString( MazeGameResources.getString( "ParticleArena.CollisionText" ), MazeGameSimulationPanel.fullWidth / 2, 7 * MazeGameSimulationPanel.fullHeight / 16 );
        }

        //Draw Goal indicator
        if ( goalDetected && !collisionDetected ) {
            g.setColor( Color.blue );
            g.setFont( arenaFont );
            g.drawString( MazeGameResources.getString( "ParticleArena.GoalText" ), MazeGameSimulationPanel.fullWidth / 10, 11 * MazeGameSimulationPanel.fullHeight / 32 );
        }

        if ( goalDetected && collisionDetected ) //
        {
            g.setColor( Color.red );
            g.setFont( arenaFont );
            g.drawString( MazeGameResources.getString( "ParticleArena.NoGoalText" ), MazeGameSimulationPanel.fullWidth / 12, 11 * MazeGameSimulationPanel.fullHeight / 32 );
        }

        //Draw goal
        g.fillOval( goalX - radius, goalY - radius, 2 * radius, 2 * radius );
        g.setFont( new PhetFont( 15, true ) );
        g.drawString( MazeGameResources.getString( "ParticleArena.FinishText" ), goalX - 2 * radius, goalY + 25 );

        //draw particle
        g.drawImage( mazeGUI1.ballImage, (int) myParticle.getX() - radius, (int) myParticle.getY() - radius, this );
    }

}
