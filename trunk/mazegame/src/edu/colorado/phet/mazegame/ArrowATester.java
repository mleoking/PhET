package edu.colorado.phet.mazegame;

import edu.colorado.phet.mazegame.ArrowA;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ArrowATester extends Applet implements MouseMotionListener {
    private ArrowA aTest = new ArrowA();
    protected int x0; //= this.getWidth()/2;  //Why can't I initialize here?
    protected int y0; //= getHeight()/2;
    protected int xF; //= x0 + 50;
    protected int yF; //= y0 + 50;
    protected Image offScreenImage;
    protected Graphics offScreenGraphics;

    public void init() {
        x0 = getWidth() / 2;
        y0 = getHeight() / 2;
        xF = x0 + 50;
        yF = y0 + 50;
//        System.out.println(this.getWidth() / 2 + " " + x0 + " AOK");
        addMouseMotionListener(this);
        aTest.setPosition(x0, y0, xF, xF);
        setBackground(Color.yellow);
        offScreenImage = createImage(getWidth(), getHeight()); //getSize().width, getSize().height);
        offScreenGraphics = offScreenImage.getGraphics();
    }//end of init()

    public void paint(Graphics g) {
        //g.drawString("Test",0,5);
        g.clearRect(0, 0, getWidth(), getHeight());
        aTest.paint(g);
    }

    public void update(Graphics g) {
        paint(g);
        //Note well: Double buffering slows downs graphics considerably
        //paint(offScreenGraphics);
        //g.drawImage(offScreenImage, 0, 0, this);
    }

    public void mouseDragged(MouseEvent me) {
        xF = me.getX();
        yF = me.getY();
        aTest.setPosition(x0, y0, xF, yF);
        repaint();
    }

    public void mouseMoved(MouseEvent me) {
    }


}//end of public class