/*
java -cp %classpath%;../classes edu.colorado.phet.balloon.BalloonApplet
*/

package edu.colorado.phet.balloon;

import edu.colorado.phet.common.HelpPanel;
import edu.colorado.phet.common.IHelp;
import edu.colorado.phet.common.view.util.ImageDebugFrame;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import phet.ImageLoader2;
import phet.paint.*;
import phet.paint.particle.ParticlePainterAdapter;
import phet.phys2d.*;
import phet.utils.ImageLoader;
import phet.utils.ResourceLoader4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;


/**
 * Test comment.
 */
public class BalloonApplet extends JApplet implements IHelp {
    static final int CHARGE_LEVEL = 1;
    static boolean isApplet = true;
    int width;
    int height;
    static final int PANEL_WIDTH = 750;
    static final int PANEL_HEIGHT = 500;

    static Color red = new Color( 255, 0, 0 );
    static Color minusColor = new Color( 0, 0, 255 );
    static Color oval = new Color( 255, 255, 255, 80 );

    static PlusPainter plussy = new PlusPainter( 14, 4, red, oval );
    static MinusPainter minnie = new MinusPainter( 14, 4, minusColor, oval );
    private PainterPanel painterPanel;
    public LayeredPainter layeredPainter;
    private boolean miniHelpShowing = false;
    private Painter balloonHelp;
    public BufferedImage sweaterImage;
    public int wallWidth;
    public JPanel controlPanel;

    public static void paintCharge( BufferedImage bi ) {
        Graphics2D g2 = (Graphics2D)bi.getGraphics();
        plussy.paintAt( 40, 60, g2 );
        plussy.paintAt( 90, 50, g2 );
        plussy.paintAt( 98, 80, g2 );
        plussy.paintAt( 90, 150, g2 );
        minnie.paintAt( 80, 110, g2 );
        plussy.paintAt( 30, 75, g2 );
        minnie.paintAt( 78, 120, g2 );
        minnie.paintAt( 50, 98, g2 );
        minnie.paintAt( 40, 40, g2 );
        minnie.paintAt( 30, 170, g2 );

        plussy.paintAt( 40, 90, g2 );
        minnie.paintAt( 55, 80, g2 );
        plussy.paintAt( 30, 175, g2 );
        plussy.paintAt( 94, 180, g2 );

        minnie.paintAt( 45, 40, g2 );
        plussy.paintAt( 50, 30, g2 );
        minnie.paintAt( 72, 62, g2 );
        minnie.paintAt( 50, 95, g2 );
        plussy.paintAt( 50, 130, g2 );
        minnie.paintAt( 54, 110, g2 );
        plussy.paintAt( 90, 150, g2 );
        minnie.paintAt( 50, 175, g2 );
        minnie.paintAt( 84, 167, g2 );
    }

    public void init() {
        if( isApplet ) {
            String applicationLocale = Toolkit.getDefaultToolkit().getProperty( "javaws.locale", null );
            if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
                SimStrings.setLocale( new Locale( applicationLocale ) );
            }
            SimStrings.setStrings( BalloonsConfig.localizedStringsPath );
        }

        plussy.setPaint( PlusPainter.NONE );
        minnie.setPaint( MinusPainter.NONE );

        width = PANEL_WIDTH;
        height = PANEL_HEIGHT;
        ImageLoader loader = new ResourceLoader4( getClass().getClassLoader(), this );

        BufferedImage balloon = loader.loadBufferedImage( "images/FilteredBalloon-ii.GIF" );
        BufferedImage blueCharge = loader.loadBufferedImage( "images/FilteredBalloon-ii.GIF" );

        BufferedImage yelBal = loader.loadBufferedImage( "images/balloon5_filter-ii.GIF" );
        BufferedImage yelCharge = loader.loadBufferedImage( "images/balloon5_filter-ii.GIF" );

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

        int moveToBalloonSpeed = 9;
        MoveToBalloon mtb = new MoveToBalloon( moveToBalloonSpeed, layeredPainter, CHARGE_LEVEL, minnie );
        ChargeMover chargeMover = new ChargeMover( mtb, layeredPainter );
        reset.setChargeMover( chargeMover );
        cm.addBalloonDragListener( chargeMover );
        bd.addBalloonDragListener( cm );

        sweaterImage = loader.loadBufferedImage( "images/sweaterWidth300.gif" );

        FixedImagePainter sweater = new FixedImagePainter( sweaterImage );
        layeredPainter.addPainter( sweater, 0 );
        layeredPainter.addPainter( bd, 2 );

        controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( painterPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );

        JCheckBox chargedBalloonBtn = new JCheckBox( SimStrings.get( "BalloonApplet.IgnoreInitialBalloonCharge" ), true );
        JRadioButton showAllCharges = new JRadioButton( SimStrings.get( "BalloonApplet.ShowAllCharges" ) );
        showAllCharges.addActionListener( new ShowAll( plussy, minnie ) );
        JRadioButton showNoCharges = new JRadioButton( SimStrings.get( "BalloonApplet.ShowNoCharges" ) );
        showNoCharges.addActionListener( new ShowNone( plussy, minnie ) );
        JRadioButton showDiff = new JRadioButton( SimStrings.get( "BalloonApplet.ShowChargeDifferences" ) );
        showDiff.addActionListener( new ShowDiff( plussy, minnie ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( showAllCharges );
        bg.add( showNoCharges );
        bg.add( showDiff );

        showAllCharges.setSelected( true );
        ActionListener[] al = showAllCharges.getActionListeners();
        for( int i = 0; i < al.length; i++ ) {
            ActionListener actionListener = al[i];
            actionListener.actionPerformed( new ActionEvent( showAllCharges, (int)System.currentTimeMillis(), "fire" ) );
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.Y_AXIS ) );
        buttonPanel.add( showAllCharges );
        buttonPanel.add( showNoCharges );
        buttonPanel.add( showDiff );
        buttonPanel.setBorder( PhetLookAndFeel.createSmoothBorder( SimStrings.get( "BalloonApplet.ChargeDisplay" ) ) );
        JButton resetBtn = new JButton( SimStrings.get( "BalloonApplet.Reset" ) );
        controlPanel.add( resetBtn );
        controlPanel.add( buttonPanel );

        JCheckBox twoBalloons = new JCheckBox( "Two Balloons", false );
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

        JCheckBox showWall = new JCheckBox( SimStrings.get( "BalloonApplet.Wall" ), true );
        controlPanel.add( showWall );

        HelpPanel helpPanel = new HelpPanel( this );
        controlPanel.add( helpPanel );

        int wallInset = 10;
        Rectangle wallBounds = new Rectangle( PANEL_WIDTH - wallWidth, 0, wallWidth, PANEL_HEIGHT );
        Rectangle wallChargeBounds = new Rectangle( PANEL_WIDTH - wallWidth + wallInset, 0, wallWidth - wallInset * 2, PANEL_HEIGHT );
        Painter wallBack = new FilledRectanglePainter( wallBounds.x, wallBounds.y, wallBounds.width, wallBounds.height, Color.yellow );
        Random r = new Random();
        Wall w = new Wall( showWall, 50, wallChargeBounds, wallBack, plussy, minnie, r, blueBalloon, yellowBalloon );
        layeredPainter.addPainter( w, 10 );

        int numSweaterCharges = 100;
        int minX = 50;
        int maxX = sweaterImage.getWidth() - 50;
        int minY = 50;
        int maxY = sweaterImage.getHeight() - 50;
        System2D sys = new System2D();
        Sweater wool = new Sweater( numSweaterCharges, new DoublePoint( sweaterImage.getWidth() / 2, sweaterImage.getHeight() / 2 ) );

        resetBtn.addActionListener( reset );
        int neighborWidth = 20;
        int neighborHeight = 20;
        for( int i = 0; i < numSweaterCharges; i++ ) {
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

            Painter plusPaint = new ParticlePainterAdapter( plussy, plus );
            plus.setDefaultPainter( plusPaint );
            layeredPainter.addPainter( plusPaint, CHARGE_LEVEL );
            Painter minusPaint = new ParticlePainterAdapter( minnie, minus );
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
        System.err.println( "x=" + x );
        int widdie = x - sweaterImage.getWidth() / 2;
        Rectangle bounds = new Rectangle( sweaterImage.getWidth() / 2, 0, widdie + 143, PANEL_HEIGHT - 50 );
        System.err.println( "bounds=" + bounds );
        sys.addLaw( new BalloonForces( blueBalloon, yellowBalloon, wool, bounds, wallBounds.x, w ) );
        sys.addLaw( w );
        sys.addLaw( ( new Repaint( painterPanel ) ) );
        double dt = 1.2;
        int waitTime = 30;
        setContentPane( panel );
        final SystemRunner sr = new SystemRunner( sys, dt, waitTime );
        Thread t = new Thread( sr );

        t.start();
        validate();
        painterPanel.addMouseMotionListener( new MouseMotionAdapter() {
            public void mouseDragged( MouseEvent e ) {
                painterPanel.repaint();
//                painterPanel.paintImmediately( 0,0,painterPanel.getWidth(), painterPanel.getHeight() );
            }
        } );
//        Timer timer=new Timer( waitTime, new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                sr.iterate();
//            }
//        } );
//        timer.start();
        balloonHelp = new BalloonHelpPainter( this );
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException {
        SimStrings.init( args, BalloonsConfig.localizedStringsPath );

        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        isApplet = false;
        BalloonApplet ba = new BalloonApplet();
        ba.init();
        JFrame jf = new JFrame( "Balloons!" );
        jf.addWindowListener( new Exit() );
        jf.setContentPane( ba );
        jf.setSize( PANEL_WIDTH, PANEL_HEIGHT + ba.controlPanel.getPreferredSize().height + 10 );
        SwingUtils.centerWindowOnScreen( jf );
        jf.setVisible( true );

        jf.invalidate();
        jf.validate();
        jf.repaint();
        jf.getContentPane().invalidate();
        jf.getContentPane().validate();
        jf.getContentPane().repaint();
        //System.out.println("main: height="+ba.getHeight()); //476 for full application.
    }

    public void setHelpEnabled( boolean miniHelpShowing ) {
        if( this.miniHelpShowing != miniHelpShowing ) {
            this.miniHelpShowing = miniHelpShowing;
            int layer = 100;
            if( miniHelpShowing ) {
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
        try {
//            new ImageDebugFrame( ImageLoader2.loadBufferedImage("images/sweaterWidth300.gif" ) ).setVisible( true );
            new ImageDebugFrame( ImageLoader2.loadBufferedImage( "balloon-meg.gif" ) ).setVisible( true );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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

    public static class Exit extends WindowAdapter {
        public void windowClosing( WindowEvent e ) {
            System.exit( 0 );
        }
    }
}
