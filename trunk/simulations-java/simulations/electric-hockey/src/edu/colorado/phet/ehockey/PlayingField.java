package edu.colorado.phet.ehockey;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

//contains Vector class

public class PlayingField extends JPanel {
    private ElectricHockeyApplication electricHockeyApplication;
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
    private Stroke pathStroke = new BasicStroke( 1, BasicStroke.CAP_SQUARE,
                                                 BasicStroke.JOIN_MITER, 1, new float[]{10, 5}, 0 );
    private Color pathColor = Color.red;

    public PlayingField( int fieldWidth, int fieldHeight, ElectricHockeyApplication electricHockeyApplication ) {
        this.electricHockeyApplication = electricHockeyApplication;
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

        barrierState = electricHockeyApplication.getControlPanel().getLevelState();

        goal = new Rectangle( 89 * fieldWidth / 100, fieldHeight / 2 - 25, 10, 50 );

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
                grabbedChargeForce = new Force( grabbedCharge, electricHockeyApplication.getModel().getPuck() );
            }
            else if( minusBag.contains( mevt.getPoint() ) ) {
                prt( "Minus bag selected." );
                newChargeIsGrabbed = true;
                grabbedCharge = new Charge( mevt.getPoint(), Charge.NEGATIVE );
                grabbedChargeForce = new Force( grabbedCharge, electricHockeyApplication.getModel().getPuck() );
            }
            else {
                for( int i = 0; i < electricHockeyApplication.getModel().getChargeListSize(); i++ ) {
                    Charge chargeI = electricHockeyApplication.getModel().getChargeAt( i ); //(edu.colorado.phet.ehockey.Charge)chargeList.elementAt(i);
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

                    electricHockeyApplication.getModel().addCharge( grabbedCharge ); //chargeList.addElement(grabbedCharge);
                    //hockeyModule.getModel().updateForceList();
                    //repaint();
                    prt( "Nbr of charges " + electricHockeyApplication.getModel().getChargeListSize() );
                    electricHockeyApplication.getControlPanel().setNbrChargesLbl( electricHockeyApplication.getModel().getChargeListSize() );
                }
            }
            if( oldChargeIsGrabbed ) {
                oldChargeIsGrabbed = false;
                if( chargeBag.contains( mevt.getPoint() ) ) {
                    //chargeList.removeElementAt(grabbedChargeIndex);
                    electricHockeyApplication.getModel().removeChargeAt( grabbedChargeIndex );
                    //hockeyModule.getModel().updateForceList();
                    prt( "edu.colorado.phet.ehockey.Charge " + grabbedChargeIndex + " removed." );
                    electricHockeyApplication.getControlPanel().setNbrChargesLbl( electricHockeyApplication.getModel().getChargeListSize() );
                    //repaint();
                }
            }
            electricHockeyApplication.getFieldGrid().updateGridForceArray();
            updateBufferedImage();

            repaint();

        }//end of mouseReleased()


    }//end of FieldMouseListener

    public void updateBufferedImage() {

        Graphics2D g2D = (Graphics2D)fieldLinesImage.getGraphics();

        electricHockeyApplication.getFieldGrid().paint( g2D );

    }

    class FieldMouseMotionListener extends MouseMotionAdapter {
        public void mouseDragged( MouseEvent mevt ) {
            if( newChargeIsGrabbed || oldChargeIsGrabbed ) {
                grabbedCharge.setPosition( mevt.getPoint() );
                grabbedChargeForce = new Force( grabbedCharge, electricHockeyApplication.getModel().getPuck() );


            }

            if( oldChargeIsGrabbed ) {
                electricHockeyApplication.getModel().getChargeList().setElementAt( grabbedCharge, grabbedChargeIndex );
                Charge puck = electricHockeyApplication.getModel().getPuck();
                Force grabbedChargeForce = new Force( grabbedCharge, puck );
                electricHockeyApplication.getModel().getForceList().setElementAt( grabbedChargeForce, grabbedChargeIndex );
                electricHockeyApplication.getFieldGrid().updateGridForceArray();
                //hockeyModule.getModel().updateForceList();
            }
            Graphics2D g2D = (Graphics2D)fieldLinesImage.getGraphics();
            electricHockeyApplication.getFieldGrid().paint( g2D );

            repaint();
        }
    }//end of FieldMouseMotionListener

    public void paintAgain() {
        //Following 3 lines attempt to speed repaint by repainting small clipping region
        //edu.colorado.phet.ehockey.Charge chargeP = hockeyModule.getModel().getPuck();
        //Rectangle rPuck = new Rectangle(chargeP.getPosition().x - 15, chargeP.getPosition().y - 15, 30, 30);
        //repaint(rPuck);

        repaint();

    }


    public void paintComponent( Graphics g ) {
        super.paintComponent( g );	//necessary for painting background
        g2D = (Graphics2D)g;
        if( electricHockeyApplication.isAntialias() ) {
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        if( electricHockeyApplication.getControlPanel().getShowField() && electricHockeyApplication.getModel().getChargeListSize() != 0 ) {
            //nbrPaintsCurrentlySkipped += 1;

            //if(nbrPaintsCurrentlySkipped > nbrPaintsToSkip){
            //super.paint(g);

            g2D.drawRenderedImage( fieldLinesImage, new AffineTransform() );
        }
        g2D.drawImage( electricHockeyApplication.plusBag, plusBag.x, plusBag.y, this );
        g2D.setColor( Color.black );
        g2D.drawRect( plusBag.x, plusBag.y, plusBag.width, plusBag.height );

        g2D.drawImage( electricHockeyApplication.minusBag, minusBag.x, minusBag.y, this );
        g2D.setColor( Color.black );
        g2D.drawRect( minusBag.x, minusBag.y, minusBag.width, minusBag.height );

        if( newChargeIsGrabbed ) {
            grabbedCharge.paint( g2D );
            grabbedChargeForce.paint( g2D );
        }
        //draw barriers
        g2D.setColor( barrierColor );
        barrierState = electricHockeyApplication.getControlPanel().getLevelState();
        for( int i = 0; i < BarrierList.currentRectArray[barrierState].length; i++ ) {
            int x = BarrierList.currentRectArray[barrierState][i].x;
            int y = BarrierList.currentRectArray[barrierState][i].y;
            int w = BarrierList.currentRectArray[barrierState][i].width;
            int h = BarrierList.currentRectArray[barrierState][i].height;
            g2D.fill3DRect( x, y, w, h, true );	//last argument: true = raised, false = sunken; doesn't work!
        }


        //paint charges and forces
        for( int i = 0; i < electricHockeyApplication.getModel().getChargeListSize(); i++ ) {
            Charge chargeI = electricHockeyApplication.getModel().getChargeAt( i );//(edu.colorado.phet.ehockey.Charge)(chargeList.elementAt(i));

            if( chargeI.getSign() == Charge.NEGATIVE ) {
                g2D.drawImage( electricHockeyApplication.minusDisk, chargeI.getPosition().x - chargeI.radius, chargeI.getPosition().y - chargeI.radius, this );
            }
            else if( chargeI.getSign() == Charge.POSITIVE ) {
                g2D.drawImage( electricHockeyApplication.plusDisk, chargeI.getPosition().x - chargeI.radius, chargeI.getPosition().y - chargeI.radius, this );
            }
            //chargeI.paint(g2D);
            Force forceI = electricHockeyApplication.getModel().getForceAt( i );
            //g2D.setColor(Color.lightGray);
            forceI.paint( g2D );
        }

        //draw positivePuckImage
        //hockeyModule.getModel().getPuck().paint(g2D);
        Charge chargeP = electricHockeyApplication.getModel().getPuck();
        if( chargeP.getSign() == Charge.POSITIVE ) {
            g2D.drawImage( electricHockeyApplication.positivePuckImage, chargeP.getPosition().x - chargeP.radius, chargeP.getPosition().y - chargeP.radius, this );
        }
        else {
            g2D.drawImage( electricHockeyApplication.negativePuckImage, chargeP.getPosition().x - chargeP.radius, chargeP.getPosition().y - chargeP.radius, this );
        }

        //draw collision and goal announcements
        g2D.setFont( fieldFont );
        if( electricHockeyApplication.getModel().getGoalState() ) {
            g2D.setColor( new Color( 0, 125, 0 ) );
            g2D.setFont( new Font( "serif", Font.PLAIN, 110 ) );
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2D.drawString( SimStrings.getInstance().getString( "HockeyPlayingField.Goal" ), 3 * fieldWidth / 10, fieldHeight / 5 );
        }
        if( electricHockeyApplication.getModel().getCollisionState() && !electricHockeyApplication.getModel().getGoalState() ) {
            g2D.setColor( Color.red );
            g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            g2D.drawString( SimStrings.getInstance().getString( "HockeyPlayingField.Collision" ), 2 * fieldWidth / 5, fieldHeight / 10 );
        }

        //drawPath
        if( electricHockeyApplication.getControlPanel().getTraceState() ) {
            g2D.setColor( Color.black );
            Stroke origStroke = g2D.getStroke();
            g2D.setStroke( pathStroke );
            g2D.setColor( pathColor );
            g2D.draw( electricHockeyApplication.getModel().getPath() );
            g2D.setStroke( origStroke );
        }

    }//end of paint()


    //A simple print method -- just to avoid typing "System.out.println"
    public void prt( String str ) {
        System.out.println( str );
    }
}

