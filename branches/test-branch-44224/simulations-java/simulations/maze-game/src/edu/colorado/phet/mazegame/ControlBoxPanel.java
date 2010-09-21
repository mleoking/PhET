package edu.colorado.phet.mazegame;

//Helper class for Maze Game.  Maintains controller arrow

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class ControlBoxPanel extends JPanel    //possible to replace Applet with Panel?
        implements ItemListener, MouseMotionListener {
    private ArrowA arrow; //controller arrow
    private int x0; //fixed x-component of tail of arrow
    private int y0; //fixed tail of arrow
    private int xF; //movable head of arrow
    private int yF;
    private int controlState;    //Control state = 0 (R), 1(V), or 2(A);
    private ParticleArena pArena;
    private GeneralPath trace;

    private boolean traceOn;            //set by edu.colorado.phet.mazegame.ScorePanel
    private boolean traceStarted;        //true if trace started

    private Font arrowFont;
    private Color fontColor;

    public static int POSITION = 0;
    public static int VELOCITY = 1;
    public static int ACCELERATION = 2;

    private JRadioButton rButton, vButton, aButton;
    private Border raisedBevel, loweredBevel, compound1, compound2;

    public ControlBoxPanel( ParticleArena pArena )  //  edu.colorado.phet.mazegame.ControlBox()
    {
        this.pArena = pArena;
        raisedBevel = BorderFactory.createRaisedBevelBorder();
        loweredBevel = BorderFactory.createLoweredBevelBorder();
        compound1 = BorderFactory.createCompoundBorder( raisedBevel, loweredBevel );
        compound2 = BorderFactory.createTitledBorder( compound1, MazeGameResources.getString( "ControlBoxPanel.CrontrolArrowBorder" ), TitledBorder.CENTER, TitledBorder.BOTTOM );
        setBorder( compound2 );
        arrow = new ArrowA();
        x0 = MazeGameSimulationPanel.fullWidth / 4;
        //changed from getWidth()/2; when Applet changed to Panel
        y0 = MazeGameSimulationPanel.fullHeight / 4;
        xF = x0 + 9 * ( MazeGameSimulationPanel.fullWidth / 4 ) / 10;      //Initial Position of throttle
        yF = y0 - 5 * ( MazeGameSimulationPanel.fullHeight / 4 ) / 10;
        controlState = POSITION;                  //Start with throttle in POSITION-control-state

        trace = new GeneralPath();
        traceStarted = false;

        arrowFont = new PhetFont( 40 );
        fontColor = new Color( 255, 0, 0 );

        rButton = new JRadioButton( MazeGameResources.getString( "ControlBoxPanel.PositionRadioButton" ), true );
        rButton.setBackground( Color.yellow );//rButton.setOpaque(false);
        vButton = new JRadioButton( MazeGameResources.getString( "ControlBoxPanel.VelocityRadioButton" ), false );
        vButton.setBackground( Color.yellow );//vButton.setOpaque(false);
        aButton = new JRadioButton( MazeGameResources.getString( "ControlBoxPanel.AccelerationRadioButton" ), false );
        aButton.setBackground( Color.yellow );//aButton.setOpaque(false);
        rButton.addItemListener( this );
        vButton.addItemListener( this );
        aButton.addItemListener( this );
        this.add( rButton );
        this.add( vButton );
        this.add( aButton );

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add( rButton );
        radioGroup.add( vButton );
        radioGroup.add( aButton );

        addMouseMotionListener( this );
        arrow.setPosition( x0, y0, xF, yF );
        setBackground( Color.yellow );
    }

    public double getDeltaX() {
        return (double) ( xF - x0 );
    }

    public double getDeltaY() {
        return (double) ( yF - y0 );
    }

    public int getControlState() {
        return this.controlState;
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        Graphics2D g2D = (Graphics2D) g;
        g.setColor( fontColor );
        g.setFont( arrowFont );
        if ( controlState == POSITION ) {
            g.drawString( MazeGameResources.getString( "ControlBoxPanel.PositionLabel" ), 5 * MazeGameSimulationPanel.fullWidth / 32, 3 * MazeGameSimulationPanel.fullHeight / 8 );
        }
        else if ( controlState == VELOCITY ) {
            g.drawString( MazeGameResources.getString( "ControlBoxPanel.VelocityLabel" ), 5 * MazeGameSimulationPanel.fullWidth / 32, 3 * MazeGameSimulationPanel.fullHeight / 8 );
        }
        else {
            g.drawString( MazeGameResources.getString( "ControlBoxPanel.AccelerationLabel" ), MazeGameSimulationPanel.fullWidth / 8, 3 * MazeGameSimulationPanel.fullHeight / 8 );
        }

        g.setColor( Color.black );
        g2D.draw( trace );
        arrow.paint( g );
        g.setColor( Color.blue );
        g.fillOval( x0 - 4, y0 - 4, 8, 8 );
    }


    public void mouseDragged( MouseEvent mevt ) {
        xF = mevt.getX();
        yF = mevt.getY();
        arrow.setPosition( x0, y0, xF, yF );
        if ( traceOn && !traceStarted ) {
            trace.moveTo( (float) xF, (float) yF );
            traceStarted = true;
        }
        if ( traceOn ) {
            trace.lineTo( (float) xF, (float) yF );
        }

        repaint();
        //System.out.println("x="+ getDeltaX()+ ",  y="+ getDeltaY());
    }

    public void mouseMoved( MouseEvent mevt ) {
    }

    public void itemStateChanged( ItemEvent aevt ) {
        if ( aevt.getSource() == rButton ) {
            //xF = x0 + 9*(edu.colorado.phet.mazegame.MazeGameSimulationPanel.fullWidth/4)/10;
            //yF = y0 - 5*(edu.colorado.phet.mazegame.MazeGameSimulationPanel.fullHeight/4)/10;

            //read current position of particle, set control arrow to match
            int deltX = (int) ( ( pArena.getCurrentX() - ( MazeGameSimulationPanel.fullWidth / 2 ) ) / pArena.getPositionFactor() );
            int deltY = (int) ( ( pArena.getCurrentY() - ( MazeGameSimulationPanel.fullHeight / 4 ) ) / pArena.getPositionFactor() );
            this.controlState = POSITION;
//            System.out.println("x= " + deltX + "   y= " + deltY);
            xF = deltX + x0;
            yF = deltY + y0;
            arrow.setPosition( x0, y0, xF, yF );            //Initial Position of throttle

            //System.out.println(getControlState());
        }
        else if ( aevt.getSource() == vButton ) {
            this.controlState = VELOCITY;
            //reset control arrow to zero length
            xF = x0;
            yF = y0;
            arrow.setPosition( x0, y0, xF, yF );
            repaint();
            //System.out.println(getControlState());
        }
        else if ( aevt.getSource() == aButton ) {
            this.controlState = ACCELERATION;
            xF = x0;
            yF = y0;
            arrow.setPosition( x0, y0, xF, yF );
            repaint();
            //System.out.println(getControlState());
        }
    }

    //reset game to starting position
    public void reset() {
        rButton.doClick();
        xF = x0 + 9 * ( MazeGameSimulationPanel.fullWidth / 4 ) / 10;      //Initial Position of throttle
        yF = y0 - 5 * ( MazeGameSimulationPanel.fullHeight / 4 ) / 10;
        arrow.setPosition( x0, y0, xF, yF );
        this.controlState = POSITION;
        pArena.setNbrCollisions( 0 );
        pArena.setCollisionDetected( false );
        pArena.setGoalDetected( false );
        pArena.getScorePanel().nbrCollisionsLbl.setText( "0" );
        setTraceToZero();
        repaint();
    }

    public void setTraceState( boolean traceOn ) {
        this.traceOn = traceOn;
    }

    public void setTraceStartedState( boolean traceStarted ) {
        this.traceStarted = traceStarted;
    }

    public void setTraceToZero() {
        trace.reset();
        traceStarted = false;
        //System.out.println("Trace zeroed.");
    }


}//end of public class