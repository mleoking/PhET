package edu.colorado.phet.mazegame;

import edu.colorado.phet.mazegame.ArrowA;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ControlBox extends Applet    //possible to replace Applet with Panel?
        implements ActionListener, MouseMotionListener {
    private ArrowA arrow; //controller arrow
    private int x0; 	//fixed x-component of tail of arrow
    private int y0; 	//fixed y-component of tail of arrow
    private int xF; 	//movable head of arrow
    private int yF;
    private int controlState;	//Control state = 0 (Position), 1(Velocity), or 2(Acceleration);

    public static int POSITION = 0;
    public static int VELOCITY = 1;
    public static int ACCELERATION = 2;

    private Button rButton, vButton, aButton;  //Buttons to choose among: Position, Velocity, Acceleration
    private Image offScreenImage; 			//used when double buffering image to prevent flicker
    private Graphics offScreenGraphics;

    public void init()  //  edu.colorado.phet.mazegame.ControlBox()
    {
        //super("Control Box");  //necessary to call for panel construtor?
        //this.setSize(200,200); //added when changed from applet to panel
        arrow = new ArrowA();
        x0 = getWidth() / 2;
        y0 = getHeight() / 2;
        xF = x0 + 50;  //Arbitrary beginning position
        yF = y0 + 50;
        //System.out.println(this.getWidth()/2 + " " + x0 + " AOK");

        rButton = new Button("R");
        vButton = new Button("V");
        aButton = new Button("A");
        this.add(rButton);
        this.add(vButton);
        this.add(aButton);
        rButton.addActionListener(this);
        vButton.addActionListener(this);
        aButton.addActionListener(this);

        addMouseMotionListener(this);
        arrow.setPosition(x0, y0, xF, xF);
        setBackground(Color.yellow);
        offScreenImage = createImage(getWidth(), getHeight()); //getSize().width, getSize().height);
        offScreenGraphics = offScreenImage.getGraphics();

    }//end of init()

    public double getDeltaX() {
        return (double) (xF - x0);
    }

    public double getDeltaY() {
        return (double) (yF - y0);
    }

    public int getControlState() {
        return this.controlState;
    }

    public void paint(Graphics g) {
        //g.drawString("Test",0,5);
        g.clearRect(0, 0, getWidth(), getHeight());
        arrow.paint(g);
    }

    public void update(Graphics g) {
        paint(g);
        //Note well: Double buffering slows downs graphics considerably
        //paint(offScreenGraphics);
        //g.drawImage(offScreenImage, 0, 0, this);
    }

    public void mouseDragged(MouseEvent mevt) {
        xF = mevt.getX();
        yF = mevt.getY();
        arrow.setPosition(x0, y0, xF, yF);
        repaint();
        //System.out.println("x="+ getDeltaX()+ ",  y="+ getDeltaY());
    }

    public void mouseMoved(MouseEvent me) {
    }  //stub needed for MouseMotionListener interface


    public void actionPerformed(ActionEvent aevt) {
        if (aevt.getActionCommand().equals("R")) {
            this.controlState = POSITION;
//            System.out.println(getControlState());
        } else if (aevt.getActionCommand().equals("V")) {
            this.controlState = VELOCITY;
//            System.out.println(getControlState());
        } else if (aevt.getActionCommand().equals("A")) {
            this.controlState = ACCELERATION;
//            System.out.println(getControlState());
        } 
//            System.out.println("Invalid button ActionCommand");
    }

    //Testing Code  -- tried changing Applet to Panel and running main() --- doesn't work
/*
	public static void main(String[] args)
	{
		Frame myFrame = new Frame("Control Box");
		myFrame.setSize(200,200);

		edu.colorado.phet.mazegame.ControlBox cBox = new edu.colorado.phet.mazegame.ControlBox();

		myFrame.add(cBox);
		myFrame.setVisible(true);

	}
*/

}//end of public class