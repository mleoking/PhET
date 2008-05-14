/*
java -cp %classpath%;../classes edu.colorado.phet.balloon.BalloonApplet
*/

package edu.colorado.phet.balloons;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.balloons.common.paint.*;
import edu.colorado.phet.balloons.common.paint.particle.ParticlePainterAdapter;
import edu.colorado.phet.balloons.common.phys2d.DoublePoint;
import edu.colorado.phet.balloons.common.phys2d.ParticleLaw;
import edu.colorado.phet.balloons.common.phys2d.Repaint;
import edu.colorado.phet.balloons.common.phys2d.System2D;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;


/**
 * Test comment.
 */
public class BalloonsApplication extends JApplet implements IHelp {
    private int width;
    private int height;
    private PainterPanel painterPanel;
    private LayeredPainter layeredPainter;
    private boolean miniHelpShowing = false;
    private Painter balloonHelp;
    private BufferedImage sweaterImage;
    private int wallWidth;
    private JPanel controlPanel;
    private JFrame frame;
    private PhetApplicationConfig phetApplicationConfig;

    static final int CHARGE_LEVEL = 1;
    static boolean isApplet = false;

    static final int PANEL_WIDTH = 750;
    static final int PANEL_HEIGHT = 500;

    static Color plusColor = new Color( 255, 0, 0 );
    static Color minusColor = new Color( 0, 0, 255 );
    static Color ovalColor = new Color( 255, 255, 255, 80 );

    static PlusPainter plusPainter = new PlusPainter( 14, 4, plusColor, ovalColor );
    static MinusPainter minusPainter = new MinusPainter( 14, 4, minusColor, ovalColor );

    public BalloonsApplication( String[] args ) {
        phetApplicationConfig = new PhetApplicationConfig( args, new FrameSetup.NoOp(), BalloonsResources.getResourceLoader() );
    }

    public static void paintCharge( BufferedImage bi ) {
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        plusPainter.paintAt( 40, 60, g2 );
        plusPainter.paintAt( 90, 50, g2 );
        plusPainter.paintAt( 98, 80, g2 );
        plusPainter.paintAt( 90, 150, g2 );
        minusPainter.paintAt( 80, 110, g2 );
        plusPainter.paintAt( 30, 75, g2 );
        minusPainter.paintAt( 78, 120, g2 );
        minusPainter.paintAt( 50, 98, g2 );
        minusPainter.paintAt( 40, 40, g2 );
        minusPainter.paintAt( 30, 170, g2 );

        plusPainter.paintAt( 40, 90, g2 );
        minusPainter.paintAt( 55, 80, g2 );
        plusPainter.paintAt( 30, 175, g2 );
        plusPainter.paintAt( 94, 180, g2 );

        minusPainter.paintAt( 45, 40, g2 );
        plusPainter.paintAt( 50, 30, g2 );
        minusPainter.paintAt( 72, 62, g2 );
        minusPainter.paintAt( 50, 95, g2 );
        plusPainter.paintAt( 50, 130, g2 );
        minusPainter.paintAt( 54, 110, g2 );
        plusPainter.paintAt( 90, 150, g2 );
        minusPainter.paintAt( 50, 175, g2 );
        minusPainter.paintAt( 84, 167, g2 );
    }

    public void init( final String[] args ) throws IOException {
//        BalloonStrings.init( args, BalloonsConfig.localizedStringsPath );

        plusPainter.setPaint( PlusPainter.NONE );
        minusPainter.setPaint( MinusPainter.NONE );

        width = PANEL_WIDTH;
        height = PANEL_HEIGHT;

        String blueBalloonIm = "balloon-blue.gif";
        String yellowBalloonIm = "balloon-yellow.gif";
        //require a deep copy so we can edit it independently of other reference
        BufferedImage balloon = BufferedImageUtils.copyImage( BalloonsResources.getImage( "" + blueBalloonIm ), BufferedImage.TYPE_INT_ARGB );
        BufferedImage blueCharge = BalloonsResources.getImage( "" + blueBalloonIm );

        BufferedImage yelBal = BufferedImageUtils.copyImage( BalloonsResources.getImage( "" + yellowBalloonIm ), BufferedImage.TYPE_INT_ARGB );
        BufferedImage yelCharge = BalloonsResources.getImage( "" + yellowBalloonIm );

        paintCharge( yelCharge );
        paintCharge( blueCharge );

        Point bPoint = new Point( 400, 10 );
        BalloonImage bPainter = new BalloonImage( bPoint.x, bPoint.y, balloon, blueCharge );
        Point strAttach = new Point( 59, 233 );
        Point strBase = new Point( 430 + yelBal.getWidth() / 2, 600 );
        int strLength = 300;
        Stroke stringStroke = new BasicStroke( 2.2f );
        Color stringColor = new Color( 200, 10, 230, 160 );
        BalloonPainter blueBalloon = new BalloonPainter( bPainter, strAttach, strBase, strLength, stringStroke, stringColor );
        blueBalloon.setInitialPosition( bPoint );
        blueBalloon.setVisible( false );
        Point yPoint = new Point( 480, 18 );
        BalloonImage yelPainter = new BalloonImage( yPoint.x, yPoint.y, yelBal, yelCharge );
        BalloonPainter yellowBalloon = new BalloonPainter( yelPainter, strAttach, strBase, strLength, stringStroke, stringColor );
        yellowBalloon.setInitialPosition( yPoint );

        this.layeredPainter = new LayeredPainter();
        Reset reset = new Reset( this, layeredPainter, CHARGE_LEVEL );
        reset.addBalloonPainter( yellowBalloon );
        reset.addBalloonPainter( blueBalloon );

//        layeredPainter.addPainter( new FilledRectanglePainter( width, height, Color.white ), -1 );
        this.layeredPainter.addPainter( new FilledRectanglePainter( width, height, new Color( 240, 240, 255 ) ), -1 );
        DoubleBufferPainter db = new DoubleBufferPainter( layeredPainter, width, height );

        painterPanel = new PainterPanel( db );

        wallWidth = 80;
        Rectangle dragBounds = new Rectangle( 0, 0, PANEL_WIDTH - balloon.getWidth() - wallWidth, PANEL_HEIGHT - balloon.getHeight() - 55 );
        BalloonDragger bd = new BalloonDragger( new BalloonPainter[]{blueBalloon, yellowBalloon}, painterPanel, dragBounds );
        painterPanel.addMouseListener( bd );
        painterPanel.addMouseMotionListener( bd );

        //double thresholdSpeed=.2;
        double thresholdSpeed = .38;
        ThresholdFilter cm = new ThresholdFilter( 5, 5, thresholdSpeed );//3.2);

//        int moveToBalloonSpeed = 9;
        int moveToBalloonSpeed = 15;
        MoveToBalloon mtb = new MoveToBalloon( moveToBalloonSpeed, layeredPainter, CHARGE_LEVEL, minusPainter );
        ChargeMover chargeMover = new ChargeMover( mtb, layeredPainter );
        reset.setChargeMover( chargeMover );
        cm.addBalloonDragListener( chargeMover );
        bd.addBalloonDragListener( cm );

        sweaterImage = BalloonsResources.getImage( "sweaterWidth300.gif" );

        FixedImagePainter sweater = new FixedImagePainter( sweaterImage );
        layeredPainter.addPainter( sweater, 0 );
        layeredPainter.addPainter( bd, 2 );

        controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( painterPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );

        JCheckBox chargedBalloonBtn = new JCheckBox( BalloonsResources.getString( "BalloonApplet.IgnoreInitialBalloonCharge" ), true );
        JRadioButton showAllCharges = new JRadioButton( BalloonsResources.getString( "BalloonApplet.ShowAllCharges" ) );
        showAllCharges.addActionListener( new ShowAll( plusPainter, minusPainter ) );
        JRadioButton showNoCharges = new JRadioButton( BalloonsResources.getString( "BalloonApplet.ShowNoCharges" ) );
        showNoCharges.addActionListener( new ShowNone( plusPainter, minusPainter ) );
        JRadioButton showDiff = new JRadioButton( BalloonsResources.getString( "BalloonApplet.ShowChargeDifferences" ) );
        showDiff.addActionListener( new ShowDiff( plusPainter, minusPainter ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( showAllCharges );
        bg.add( showNoCharges );
        bg.add( showDiff );

        showAllCharges.setSelected( true );
        ActionListener[] al = showAllCharges.getActionListeners();
        for ( int i = 0; i < al.length; i++ ) {
            ActionListener actionListener = al[i];
            actionListener.actionPerformed( new ActionEvent( showAllCharges, (int) System.currentTimeMillis(), "fire" ) );
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.Y_AXIS ) );
        buttonPanel.add( showAllCharges );
        buttonPanel.add( showNoCharges );
        buttonPanel.add( showDiff );
        buttonPanel.setBorder( createSmoothBorder( BalloonsResources.getString( "BalloonApplet.ChargeDisplay" ) ) );
        JButton resetBtn = new JButton( BalloonsResources.getString( "BalloonApplet.Reset" ) );
        controlPanel.add( resetBtn );
        controlPanel.add( buttonPanel );

        JCheckBox twoBalloons = new JCheckBox( BalloonsResources.getString( "BalloonApplet.TwoBalloons" ), false );
        twoBalloons.addActionListener( new TwoBalloonsHandler( twoBalloons, blueBalloon ) );

        SetBalloonCharge sbc = ( new SetBalloonCharge( chargedBalloonBtn, bPainter, showNoCharges, showDiff, showAllCharges ) );
        chargedBalloonBtn.addActionListener( sbc );
        SetBalloonCharge s2 = ( new SetBalloonCharge( chargedBalloonBtn, yelPainter, showNoCharges, showDiff, showAllCharges ) );
        chargedBalloonBtn.addActionListener( s2 );
        showAllCharges.addActionListener( sbc );
        showAllCharges.addActionListener( s2 );
        showDiff.addActionListener( sbc );
        showDiff.addActionListener( s2 );
        showNoCharges.addActionListener( sbc );
        showNoCharges.addActionListener( s2 );

        JPanel twoBtn = new JPanel();
        twoBtn.setLayout( new BoxLayout( twoBtn, BoxLayout.Y_AXIS ) );
        twoBtn.add( twoBalloons );
        twoBtn.add( chargedBalloonBtn );
        controlPanel.add( twoBtn );

        JCheckBox showWall = new JCheckBox( BalloonsResources.getString( "BalloonApplet.Wall" ), true );
        controlPanel.add( showWall );

        HelpPanel helpPanel = new HelpPanel( this );
        controlPanel.add( helpPanel );

        JButton about = new JButton( "About" );
        about.addActionListener( new ActionListener() {


            public void actionPerformed( ActionEvent e ) {
                PhetAboutDialog phetAboutDialog = new PhetAboutDialog( frame, new PhetAboutDialog.PhetApplicationConfigDialogConfig( phetApplicationConfig ) );
//                PhetAboutDialog phetAboutDialog = new PhetAboutDialog( frame, new PhetAboutDialog.SimpleDialogConfig(
//                        phetApplicationConfig.getName(), phetApplicationConfig.getDescription(), phetApplicationConfig.getVersion().formatForAboutDialog(), phetApplicationConfig.getCredits() ) );
                phetAboutDialog.show();
            }
        } );
        controlPanel.add( about );

        int wallInset = 10;
        Rectangle wallBounds = new Rectangle( PANEL_WIDTH - wallWidth, 0, wallWidth, PANEL_HEIGHT );
        Rectangle wallChargeBounds = new Rectangle( PANEL_WIDTH - wallWidth + wallInset, 0, wallWidth - wallInset * 2, PANEL_HEIGHT );
        Painter wallBack = new FilledRectanglePainter( wallBounds.x, wallBounds.y, wallBounds.width, wallBounds.height, Color.yellow );
        Random r = new Random();
        Wall w = new Wall( showWall, 50, wallChargeBounds, wallBack, plusPainter, minusPainter, r, blueBalloon, yellowBalloon );
        layeredPainter.addPainter( w, 10 );

        int numSweaterCharges = 100;
        int minX = 50;
        int maxX = sweaterImage.getWidth() - 50;
        int minY = 50;
        int maxY = sweaterImage.getHeight() - 50;
        final System2D sys = new System2D();
        Sweater wool = new Sweater( numSweaterCharges, new DoublePoint( sweaterImage.getWidth() / 2, sweaterImage.getHeight() / 2 ) );

        resetBtn.addActionListener( reset );
        int neighborWidth = 20;
        int neighborHeight = 20;
        for ( int i = 0; i < numSweaterCharges; i++ ) {
            Charge plus = new Charge();
            int x = r.nextInt( maxX - minX ) + minX;
            int y = r.nextInt( maxY - minY ) + minY;
            int xNeighbor = r.nextInt( 2 * neighborWidth ) - neighborWidth;
            int yNeighbor = r.nextInt( 2 * neighborHeight ) - neighborHeight;

            plus.setPosition( new DoublePoint( x + xNeighbor, y + yNeighbor ) );
            plus.setInitialPosition( new DoublePoint( x + xNeighbor, y + yNeighbor ) );
            plus.setMass( 1.0 );
            plus.setCharge( 1.0 );

            Charge minus = new Charge();
            minus.setPosition( new DoublePoint( x, y ) );
            minus.setInitialPosition( new DoublePoint( x, y ) );
            minus.setMass( 1.0 );
            minus.setCharge( -1.0 );

            minus.setPartner( plus );

            sys.addParticle( minus );
            chargeMover.addParticle( minus );

            Painter plusPaint = new ParticlePainterAdapter( plusPainter, plus );
            plus.setDefaultPainter( plusPaint );
            layeredPainter.addPainter( plusPaint, CHARGE_LEVEL );
            Painter minusPaint = new ParticlePainterAdapter( minusPainter, minus );
            minus.setDefaultPainter( minusPaint );
            layeredPainter.addPainter( minusPaint, CHARGE_LEVEL );
            plus.setPainter( plusPaint, CHARGE_LEVEL );
            minus.setPainter( minusPaint, CHARGE_LEVEL );
            reset.addCharge( plus );
            reset.addCharge( minus );
        }
        sys.addLaw( new ParticleLaw() );
        //sys.addLaw(new DelayedLaw(new Repaint(painterPanel),2));
        //sys.addLaw(new DelayedLaw(new Repaint(this),20));
        int x = dragBounds.x + dragBounds.width;
//        System.err.println( "x=" + x );
        int widdie = x - sweaterImage.getWidth() / 2;
        Rectangle bounds = new Rectangle( sweaterImage.getWidth() / 2, 0, widdie + 143, PANEL_HEIGHT - 50 );
//        System.err.println( "bounds=" + bounds );
        sys.addLaw( new BalloonForces( blueBalloon, yellowBalloon, wool, bounds, wallBounds.x, w ) );
        sys.addLaw( w );
        sys.addLaw( ( new Repaint( painterPanel ) ) );
        final double dt = 1.2;
        int waitTime = 30;
        setContentPane( panel );
//        final SystemRunner sr = new SystemRunner( sys, dt, waitTime );
//        Thread t = new Thread( sr );
        Timer timer = new Timer( waitTime, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                sys.iterate( dt );
            }
        } );
        timer.start();
//        t.start();
        validate();
        painterPanel.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                sys.iterate( dt );
                painterPanel.repaint();
            }
        } );
        balloonHelp = new BalloonHelpPainter( this );
    }

    public void setHelpEnabled( boolean miniHelpShowing ) {
        if ( this.miniHelpShowing != miniHelpShowing ) {
            this.miniHelpShowing = miniHelpShowing;
            int layer = 100;
            if ( miniHelpShowing ) {
                layeredPainter.addPainter( balloonHelp, layer );
            }
            else {

                layeredPainter.removePainter( balloonHelp, layer );
            }
            repaintAll();
        }

    }

    private void repaintAll() {
    }

    public void showMegaHelp() {
        //            new ImageDebugFrame( ImageLoader2.loadBufferedImage("sweaterWidth300.gif" ) ).setVisible( true );
        new ImageFrame( BalloonsResources.getImage( "balloon-meg.gif" ) ).setVisible( true );
    }

    public boolean hasMegaHelp() {
        return true;
    }

    public float getSweaterMaxX() {
        return sweaterImage.getWidth();
    }

    public int getWallX() {
        return getWidth() - wallWidth;
    }

    public int getWallHeight() {
        return painterPanel.getHeight();
    }

    private void setFrame( JFrame frame ) {
        this.frame = frame;
    }

    public static Border createSmoothBorder( String s ) {
        return new TitledBorder( s ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
    }

    public static void main( final String[] args ) throws UnsupportedLookAndFeelException, IOException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                PhetResources.setFontRenderTest("ja","\u30A8\u30CD\u30EB\u30AE\u30FC\u306E\u6642\u9593\u5909\u5316");
//                Locale.setDefault( new Locale( "ja" ) );
                PhetLookAndFeel laf = new PhetLookAndFeel();
                laf.setBackgroundColor( new Color( 200, 240, 200 ) );
                laf.initLookAndFeel();
//                UIManager.setLookAndFeel( new PhetLookAndFeel() );
                BalloonsApplication ba = new BalloonsApplication( args );
                try {
                    ba.init( args );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }

                JFrame frame = new JFrame( ba.phetApplicationConfig.getName() + " (" + ba.phetApplicationConfig.getVersion().formatForTitleBar() + ")" );
                ba.setFrame( frame );
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame.setContentPane( ba );
                frame.setSize( PANEL_WIDTH, PANEL_HEIGHT + ba.controlPanel.getPreferredSize().height + 10 );
                SwingUtils.centerWindowOnScreen( frame );
                frame.setVisible( true );

                frame.invalidate();
                frame.validate();
                frame.repaint();
                frame.getContentPane().invalidate();
                frame.getContentPane().validate();
                frame.getContentPane().repaint();
                //System.out.println("main: height="+ba.getHeight()); //476 for full application.
            }
        } );

    }

}
