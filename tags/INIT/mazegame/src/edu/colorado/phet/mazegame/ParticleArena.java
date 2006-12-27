package edu.colorado.phet.mazegame;

//Driver class for MazeGame. Contains main thread.

import edu.colorado.phet.mazegame.BarrierList;
import edu.colorado.phet.mazegame.ControlBoxPanel;
import edu.colorado.phet.mazegame.MazeGameApplet;
import edu.colorado.phet.mazegame.Particle;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

//Need File class

public class ParticleArena extends JPanel
        implements  Runnable {
    private MazeGameApplet mazeGUI1;
    private ScorePanel scrPanel;
    private Particle myParticle;
    private int radius;				//radius of particle (and goal)
    private int controlState;		//control state of control arrow; 1 = R, 2 = V, 3 = A
    private int barrierState; 		//Barrier State is level 0, 1, 2, or 3
    private Color barrierColor;
    private boolean collisionDetected;
    private boolean goalDetected;
    private boolean badGoal;   		//if ball in goal but collision detected
    private boolean disqualified;   //player disqualified if wall is crossed
    private int nbrCollisions;		//number of collisions with walls during play
    private int lastX, lastY;		//last location
    private int collisionX;			//X, Y location just before collision
    private int collisionY;

    //private AudioClip bangouch;
    //private AudioClip cork;
    //private AudioClip tada;
    //private AudioClip figaro;

    boolean setToPlayBang = true;	//used to prevent multiple restarting audioclip
    boolean setToPlayTada = true;  //used to prevent multiple restarting audioclip
    private int goalX = MazeGameApplet.fullWidth / 8; 	//position of goal
    private int goalY = 3 * MazeGameApplet.fullHeight / 8;
    private ControlBoxPanel cbPanel;

    private PositionUpdater pUpdater;
    private Thread myThread;
    private int timeStep = 30;

    private double positionFactor = 1.5;		//"gain" of controller arrow in Position state
    private double vTimeStep = (double) (0.0010 * timeStep);	//timeStep for velocity update
    private double aTimeStep = (double) (0.0010 * timeStep); //timeStep for acceleration update

    private Border raisedBevel, loweredBevel, compound1, compound2;
    private Font arenaFont = new Font("serif", Font.BOLD, 25);


    public ParticleArena(MazeGameApplet mazeGUI1) {

        this.mazeGUI1 = mazeGUI1;
        setBackground(new Color(255, 232, 45));
        barrierColor = new Color(50, 50, 250);
        raisedBevel = BorderFactory.createRaisedBevelBorder();
        loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound1 = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);
        compound2 = BorderFactory.createTitledBorder(raisedBevel, "The Arena of Pain", TitledBorder.CENTER, TitledBorder.ABOVE_BOTTOM);
        setBorder(compound2);
        //setBorder(BorderFactory.createLineBorder(Color.red));
        /*
        try{
            URL tadaURL = new File("C:\\Java\\Wieman\\Maze\\tada.WAV").toURL();
            tada = java.applet.Applet.newAudioClip(tadaURL);
            URL bangouchURL = new File("C:\\Java\\Wieman\\Maze\\bangouch.WAV").toURL();
            bangouch = java.applet.Applet.newAudioClip(bangouchURL);
            URL corkURL = new File("C:\\Java\\Wieman\\Maze\\cork.au").toURL();
            //cork = java.applet.Applet.newAudioClip(corkURL);
            cork = mazeGUI1.getAudioClip(mazeGUI1.getDocumentBase(),"cork.au");
            URL figaroURL = new File("C:\\Java\\Wieman\\Maze\\figaro.au").toURL();
            figaro = java.applet.Applet.newAudioClip(figaroURL);
        }catch(MalformedURLException e){;
        }catch(java.security.AccessControlException ace){;}
        */

        collisionDetected = false;
        goalDetected = false;
        badGoal = false;
        disqualified = false;
        nbrCollisions = 0;
        cbPanel = new ControlBoxPanel(this);
        scrPanel = new ScorePanel(this, cbPanel);
        pUpdater = new PositionUpdater(positionFactor * cbPanel.getDeltaX() + MazeGameApplet.fullWidth / 2, positionFactor * cbPanel.getDeltaY() + MazeGameApplet.fullHeight / 4);  //argument is initial positionudio

        myParticle = new Particle(pUpdater.getX(), pUpdater.getY());
        radius = myParticle.getRadius();
        this.cbPanel = cbPanel;

//        addMouseMotionListener(this);
        //start();		//Perhaps should have Thread started by Score Panel start button
        //new Thread(this).start();
    }

    public void start() {
        //new Exception().printStackTrace();
        if (myThread == null) {
            myThread = new Thread(this);
            myThread.start();
        }
    }//end of start method

    public void stop() {
        if (myThread != null) {
            myThread = null;
        }
    }//end of stop method

    public void run() {
        Thread thisThread = Thread.currentThread();
        while (myThread == thisThread) {
            if (cbPanel.getControlState() == ControlBoxPanel.POSITION) {
                double X = positionFactor * cbPanel.getDeltaX() + MazeGameApplet.fullWidth / 2;
                double Y = positionFactor * cbPanel.getDeltaY() + MazeGameApplet.fullHeight / 4;
                //myParticle.setXY((int)cbPanel.getDeltaX() + 200, (int)cbPanel.getDeltaY() + 100);
                if (pUpdater != null)
                    pUpdater.updateWithPos(X, Y);

            }
            if (cbPanel.getControlState() == ControlBoxPanel.VELOCITY) {
                double vX = cbPanel.getDeltaX();
                double vY = cbPanel.getDeltaY();
                pUpdater.updateWithVel(vX, vY, vTimeStep);

            }
            if (cbPanel.getControlState() == ControlBoxPanel.ACCELERATION) {
                double aX = cbPanel.getDeltaX();
                double aY = cbPanel.getDeltaY();
                pUpdater.updateWithAcc(aX, aY, aTimeStep);
            } else {
            }

            myParticle.setXY(pUpdater.getIntX(), pUpdater.getIntY());

            //Check for barrier collision*******************************************************
            int x = (int) myParticle.getX();
            int y = (int) myParticle.getY();
            int w = 20;  //width for checking if disqualified
            if (x > 0 && x < MazeGameApplet.fullWidth && y > 0 && y < MazeGameApplet.fullHeight / 2)  //check that particle is inside arena window
            {
                if (BarrierList.currentCollisionArray[x][y] == 0 && !collisionDetected) {
                    lastX = x;
                    lastY = y; 		//record last position
                } else if (BarrierList.currentCollisionArray[x][y] > 0 && !collisionDetected) {
                    collisionDetected = true;
                    collisionX = lastX;
                    collisionY = lastY;
                    nbrCollisions += 1;
                    scrPanel.nbrCollisionsLbl.setText(new Integer(nbrCollisions).toString());
//                    System.out.println("# collisions = " + nbrCollisions);
                    if (mazeGUI1.cork != null && scrPanel.soundOn) mazeGUI1.cork.play();
                    //setToPlayBang = false;
                } else if (BarrierList.currentCollisionArray[x][y] == 0 && collisionDetected) {
                    if ((x < collisionX + w) && (x > collisionX - w) &&
                            (y < collisionY + w) && (y > collisionY - w)) {
                        collisionDetected = false;
                        disqualified = false;
                        lastX = x;
                        lastY = y;
                        //System.out.println("Qualified!");
                    } else {
                        disqualified = true;
                        collisionDetected = true;
                        //System.out.print("Disqualified." + collisionX + " " + collisionY + " ");
                        //System.out.println("x = " + x + " y = " + y);
                    }
                }


            }//end of barrier collision check****************************************************

            if (x < (goalX + radius) && x > (goalX - radius) && y < (goalY + radius) && y > (goalY - radius)) {
                if (setToPlayTada && !collisionDetected) {

                    if (mazeGUI1.figaro != null && scrPanel.soundOn) mazeGUI1.figaro.play();
                    goalDetected = true;
                    badGoal = false;
                    scrPanel.myClock1.stop();
                    double finalTime = scrPanel.myClock1.getTime();
                    double finalScore = finalTime + 5 * nbrCollisions;
                    scrPanel.ptsScoreLbl.setText(new Double(finalScore).toString());
                    setToPlayTada = false;
                } else {
                    goalDetected = true;
                    badGoal = true;
                }
            } else {
                goalDetected = false;
                badGoal = false;
                setToPlayTada = true;
            }


            repaint();
            try {
                Thread.sleep(timeStep);
            }  //millisec interval between mouse events
            catch (Exception e) {
            }
        }//end of while loop
    }//end of run()

    public ControlBoxPanel getControlBoxPanel() {
        return cbPanel;
    }

    public ScorePanel getScorePanel() {
        return scrPanel;
    }

    public void setBarrierState(int barrierState) {
        this.barrierState = barrierState;
        switch (this.barrierState) {
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

    public int getBarrierState() {
        return barrierState;
    }

    public void setCollisionDetected(boolean b) {
        this.collisionDetected = b;
    }

    public void setGoalDetected(boolean b) {
        this.goalDetected = b;
    }

    public void setNbrCollisions(int n) {
        this.nbrCollisions = n;
    }

    public void setDisqualified(boolean b) {
        this.disqualified = b;
    }

    public double getCurrentX() {
        return myParticle.getX();
    }

    public double getCurrentY() {
        return myParticle.getY();
    }


    public void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent(g);  //necessary for drawing background!
        //g.setColor(Color.orange);
        //g.fillRect(0,0,getWidth(),getHeight());

        //draw barriers
        g.setColor(barrierColor);
        for (int i = 0; i < BarrierList.currentRectArray[barrierState].length; i++) {
            int x = BarrierList.currentRectArray[barrierState][i].x;
            int y = BarrierList.currentRectArray[barrierState][i].y;
            int w = BarrierList.currentRectArray[barrierState][i].width;
            int h = BarrierList.currentRectArray[barrierState][i].height;
            g.fill3DRect(x, y, w, h, false);	//last argument: true = raised, false = sunken; doesn't work!
        }

        //Draw collision report
        if (collisionDetected) {
            //g.setColor(Color.yellow);
            //g.fillOval(collisionX - 15, collisionY - 15, 30, 30);
            g.drawImage(mazeGUI1.splat, collisionX - 15, collisionY - 15, this);
            g.setColor(Color.red);
            g.setFont(arenaFont);
            g.drawString("Collision!", MazeGameApplet.fullWidth / 2, 7 * MazeGameApplet.fullHeight / 16);
        }

        //Draw Goal indicator
        if (goalDetected && !collisionDetected) {
            g.setColor(Color.blue);
            g.setFont(arenaFont);
            g.drawString("Goal!", MazeGameApplet.fullWidth / 10, 11 * MazeGameApplet.fullHeight / 32);
        }

        if (goalDetected && collisionDetected) //
        {
            g.setColor(Color.red);
            g.setFont(arenaFont);
            g.drawString("No Goal", MazeGameApplet.fullWidth / 12, 11 * MazeGameApplet.fullHeight / 32);
        }

        //Draw goal
        g.fillOval(goalX - radius, goalY - radius, 2 * radius, 2 * radius);
        g.setFont(new Font("serif", Font.BOLD, 15));
        g.drawString("Finish", goalX - 2 * radius, goalY + 25);

        //draw particle
        //myParticle.paint(g);
        g.drawImage(mazeGUI1.ballImage, (int) myParticle.getX() - radius, (int) myParticle.getY() - radius, this);
    }//end of paintComponent()


//    public void mouseDragged(MouseEvent mevt) {
//        int xF = mevt.getX();
//        int yF = mevt.getY();
//        myParticle.setXY(xF, yF);
//        repaint();
//        //System.out.println("x="+ getDeltaX()+ ",  y="+ getDeltaY());
//    }
//
//    public void mouseMoved(MouseEvent mevt) {
//    }
}
