package edu.colorado.phet.ehockey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

//contains Vector class

public class PlayingField extends JPanel {
    private Hockey hockey;
    private int fieldWidth, fieldHeight;
    private Rectangle plusBag;
    private Rectangle minusBag;
    private Rectangle chargeBag;  //plusBag and minusBag
    Rectangle goal;
    private int bagWidth;
    private Color fieldColor;
    private JLabel bagLabel;
    private Font fieldFont;
    private Color barrierColor;

    private Graphics2D g2D;

    private Charge grabbedCharge;
    private Force grabbedChargeForce;
    private boolean newChargeIsGrabbed;
    private boolean oldChargeIsGrabbed;
    private int grabbedChargeIndex;

    private int barrierState;
    private BufferedImage fieldLinesImage;

    public PlayingField( int fieldWidth, int fieldHeight, Hockey hockey ) {
        this.hockey = hockey;
        this.fieldWidth = fieldWidth;
        this.fieldHeight = fieldHeight;
        setSize( fieldWidth, fieldHeight );
        fieldLinesImage = new BufferedImage( fieldWidth, fieldHeight, BufferedImage.TYPE_INT_RGB );

        fieldColor = new Color( 230, 235, 255 );
        setBackground( fieldColor );
        barrierColor = new Color( 100, 100, 250 );

        fieldFont = new Font( "serif", Font.PLAIN, 50 );

        bagWidth = fieldWidth / 15;
        plusBag = new Rectangle( 8 * fieldWidth / 10, 10, bagWidth, bagWidth );
        minusBag = new Rectangle( 8 * fieldWidth / 10 + bagWidth, 10, bagWidth, bagWidth );
        chargeBag = plusBag.union( minusBag );

        bagLabel = new JLabel( "edu.colorado.phet.ehockey.Charge Bags" );
        bagLabel.setBackground( Color.white );

        newChargeIsGrabbed = false;
        oldChargeIsGrabbed = false;

        barrierState = hockey.getControlPanel().getLevelState();

        goal = new Rectangle( 89 * fieldWidth / 100, fieldHeight / 2 - 25, 10, 50 );

        //path = new GeneralPath();
        //pathStarted = false;

        this.addMouseListener( new FieldMouseListener() );
        this.addMouseMotionListener( new FieldMouseMotionListener() );

        repaint();
    }//end of constructor

    class FieldMouseListener extends MouseAdapter {
        public void mousePressed( MouseEvent mevt ) {
            if( plusBag.contains( mevt.getPoint() ) ) {
                prt( "Plus bag selected." );
                newChargeIsGrabbed = true;
                grabbedCharge = new Charge( mevt.getPoint(), Charge.POSITIVE );
                grabbedChargeForce = new Force( grabbedCharge, hockey.getModel().getPuck() );
            }
            else if( minusBag.contains( mevt.getPoint() ) ) {
                prt( "Minus bag selected." );
                newChargeIsGrabbed = true;
                grabbedCharge = new Charge( mevt.getPoint(), Charge.NEGATIVE );
                grabbedChargeForce = new Force( grabbedCharge, hockey.getModel().getPuck() );
            }
            else {
                for( int i = 0; i < hockey.getModel().getChargeListSize(); i++ ) {
                    Charge chargeI = hockey.getModel().getChargeAt( i ); //(edu.colorado.phet.ehockey.Charge)chargeList.elementAt(i);
                    if( chargeI.contains( mevt.getPoint() ) ) {
                        oldChargeIsGrabbed = true;
                        grabbedChargeIndex = i;
                        grabbedCharge = chargeI;
                    }
                }
            }
        }//end of mousePressed()

        public void mouseReleased( MouseEvent mevt ) {
            if( newChargeIsGrabbed ) {
                newChargeIsGrabbed = false;
                //repaint();

                if( !chargeBag.contains( mevt.getPoint() ) ) {

                    hockey.getModel().addCharge( grabbedCharge ); //chargeList.addElement(grabbedCharge);
                    //hockey.getModel().updateForceList();
                    //repaint();
                    prt( "Nbr of charges " + hockey.getModel().getChargeListSize() );
                    hockey.getControlPanel().setNbrChargesLbl( hockey.getModel().getChargeListSize() );
                }
            }
            if( oldChargeIsGrabbed ) {
                oldChargeIsGrabbed = false;
                if( chargeBag.contains( mevt.getPoint() ) ) {
                    //chargeList.removeElementAt(grabbedChargeIndex);
                    hockey.getModel().removeChargeAt( grabbedChargeIndex );
                    //hockey.getModel().updateForceList();
                    prt( "edu.colorado.phet.ehockey.Charge " + grabbedChargeIndex + " removed." );
                    hockey.getControlPanel().setNbrChargesLbl( hockey.getModel().getChargeListSize() );
                    //repaint();
                }
            }
            hockey.getFieldGrid().updateGridForceArray();
            Graphics2D g2D = (Graphics2D)fieldLinesImage.getGraphics();

            hockey.getFieldGrid().paintComponent( g2D );

            repaint();

        }//end of mouseReleased()
    }//end of FieldMouseListener


    class FieldMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged( MouseEvent mevt ) {
            if( newChargeIsGrabbed || oldChargeIsGrabbed ) {
                grabbedCharge.setPosition( mevt.getPoint() );
                grabbedChargeForce = new Force( grabbedCharge, hockey.getModel().getPuck() );


            }

            if( oldChargeIsGrabbed ) {
                hockey.getModel().getChargeList().setElementAt( grabbedCharge, grabbedChargeIndex );
                Charge puck = hockey.getModel().getPuck();
                Force grabbedChargeForce = new Force( grabbedCharge, puck );
                hockey.getModel().getForceList().setElementAt( grabbedChargeForce, grabbedChargeIndex );
                hockey.getFieldGrid().updateGridForceArray();
                //hockey.getModel().updateForceList();
            }
            Graphics2D g2D = (Graphics2D)fieldLinesImage.getGraphics();
            hockey.getFieldGrid().paintComponent( g2D );

            repaint();
        }
    }//end of FieldMouseMotionListener

    public void paintAgain() {
        //Following 3 lines attempt to speed repaint by repainting small clipping region
        //edu.colorado.phet.ehockey.Charge chargeP = hockey.getModel().getPuck();
        //Rectangle rPuck = new Rectangle(chargeP.getPosition().x - 15, chargeP.getPosition().y - 15, 30, 30);
        //repaint(rPuck);

        repaint();

    }


    public void paintComponent( Graphics g ) {

        //if(!hockey.getModel().getPuckMovingState())
        //{
        super.paintComponent( g );	//necessary for painting background
        //}

        g2D = (Graphics2D)g;

        //if(hockey.getModel().getPuckMovingState())
        //{
        //	edu.colorado.phet.ehockey.Charge puckCharge = hockey.getModel().getPuck();
        //	Rectangle clippingRect = new Rectangle(puckCharge.getPosition().x, puckCharge.getPosition().y, 20, 20);
        //	g2D.setClip(clippingRect);
        //}

        //draw black border around field


        //draw grid forces if positivePuckImage not moving

        //if(!hockey.getModel().getPuckMovingState()){

        if( hockey.getControlPanel().getShowField() && hockey.getModel().getChargeListSize() != 0 ) {
            //nbrPaintsCurrentlySkipped += 1;

            //if(nbrPaintsCurrentlySkipped > nbrPaintsToSkip){
            //super.paint(g);

            g2D.drawRenderedImage( fieldLinesImage, new AffineTransform() );
            //hockey.getFieldGrid().paint(g2D);
            //nbrPaintsCurrentlySkipped = 0;
            //}
        }
        //}
//        g2D.setColor(Color.black);
//        g2D.drawRect(1, 1, fieldWidth - 2, fieldHeight - 2);


        //draw charge bags and goal
        //g2D.setColor(Color.pink);
        //g2D.fillRect(plusBag.x, plusBag.y, plusBag.width, plusBag.height);
        g2D.drawImage( hockey.plusBag, plusBag.x, plusBag.y, this );
        g2D.setColor( Color.black );
        g2D.drawRect( plusBag.x, plusBag.y, plusBag.width, plusBag.height );
        //g2D.setColor(Color.cyan);
        //g2D.fillRect(minusBag.x, minusBag.y, minusBag.width, minusBag.height);
        g2D.drawImage( hockey.minusBag, minusBag.x, minusBag.y, this );
        g2D.setColor( Color.black );
        g2D.drawRect( minusBag.x, minusBag.y, minusBag.width, minusBag.height );
        //g2D.fillRect(goal.x, goal.y, goal.width, goal.height);

        if( newChargeIsGrabbed ) {
            grabbedCharge.paint( g2D );
            grabbedChargeForce.paint( g2D );
        }


        //draw barriers
        g2D.setColor( barrierColor );
        barrierState = hockey.getControlPanel().getLevelState();
        for( int i = 0; i < BarrierList.currentRectArray[barrierState].length; i++ ) {
            int x = BarrierList.currentRectArray[barrierState][i].x;
            int y = BarrierList.currentRectArray[barrierState][i].y;
            int w = BarrierList.currentRectArray[barrierState][i].width;
            int h = BarrierList.currentRectArray[barrierState][i].height;
            g2D.fill3DRect( x, y, w, h, true );	//last argument: true = raised, false = sunken; doesn't work!
        }


        //paint charges and forces
        for( int i = 0; i < hockey.getModel().getChargeListSize(); i++ ) {
            Charge chargeI = hockey.getModel().getChargeAt( i );//(edu.colorado.phet.ehockey.Charge)(chargeList.elementAt(i));

            if( chargeI.getSign() == Charge.NEGATIVE ) {
                g2D.drawImage( hockey.minusDisk, chargeI.getPosition().x - chargeI.radius, chargeI.getPosition().y - chargeI.radius, this );
            }
            else if( chargeI.getSign() == Charge.POSITIVE ) {
                g2D.drawImage( hockey.plusDisk, chargeI.getPosition().x - chargeI.radius, chargeI.getPosition().y - chargeI.radius, this );
            }
            //chargeI.paint(g2D);
            Force forceI = hockey.getModel().getForceAt( i );
            //g2D.setColor(Color.lightGray);
            forceI.paint( g2D );
        }

        //draw positivePuckImage
        //hockey.getModel().getPuck().paint(g2D);
        Charge chargeP = hockey.getModel().getPuck();
        if( chargeP.getSign() == Charge.POSITIVE ) {
            g2D.drawImage( hockey.positivePuckImage, chargeP.getPosition().x - chargeP.radius, chargeP.getPosition().y - chargeP.radius, this );
        }
        else {
            g2D.drawImage( hockey.negativePuckImage, chargeP.getPosition().x - chargeP.radius, chargeP.getPosition().y - chargeP.radius, this );
        }

        //draw collision and goal announcements
        g2D.setFont( fieldFont );
        if( hockey.getModel().getGoalState() ) {
            g2D.setColor( new Color( 0, 125, 0 ) );
            g2D.setFont( new Font( "serif", Font.PLAIN, 110 ) );
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2D.drawString( "Goal!", 3 * fieldWidth / 10, fieldHeight / 5 );
        }
        if( hockey.getModel().getCollisionState() && !hockey.getModel().getGoalState() ) {
            g2D.setColor( Color.red );
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2D.drawString( "Collision", 2 * fieldWidth / 5, fieldHeight / 10 );
        }

        //drawPath
        if( hockey.getControlPanel().getTraceState() ) {
            g2D.setColor( Color.black );
            g2D.draw( hockey.getModel().getPath() );
        }

    }//end of paint()


    //A simple print method -- just to avoid typing "System.out.println"
    public void prt( String str ) {
        System.out.println( str );
    }
}

