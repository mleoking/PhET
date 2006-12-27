/*
java -cp %classpath%;../classes edu.colorado.phet.balloon.BalloonApplet
*/

package edu.colorado.phet.balloon;

import phet.paint.*;
import phet.paint.particle.ParticlePainterAdapter;
import phet.phys2d.DoublePoint;
import phet.phys2d.ParticleLaw;
import phet.phys2d.System2D;
import phet.phys2d.SystemRunner;
import phet.phys2d.Repaint;
import phet.utils.AlphaFixer2;
import phet.utils.ImageLoader;
import phet.utils.ResourceLoader4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class BalloonApplet extends JApplet {
    static final int CHARGE_LEVEL = 1;
    static boolean isApplet = true;
    int width;
    int height;
    static final int W = 750;
    static final int H = 470;

    static Color red = new Color( 255, 0, 0 );
    static Color minColor = new Color( 0, 0, 255 );
    static Color oval = new Color( 255, 255, 255, 80 );

    static PlusPainter plussy = new PlusPainter( 14, 4, red, oval );
    static MinusPainter minnie = new MinusPainter( 14, 4, minColor, oval );
    private PainterPanel painterPanel;

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
        plussy.setPaint( PlusPainter.NONE );
        minnie.setPaint( MinusPainter.NONE );

        width = W;
        height = H;
        ImageLoader loader = new ResourceLoader4( getClass().getClassLoader(), this );

        BufferedImage balloon = loader.loadBufferedImage( "images/FilteredBalloon-ii.GIF" );
        balloon = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( balloon );
        BufferedImage blueCharge = loader.loadBufferedImage( "images/FilteredBalloon-ii.GIF" );
        blueCharge = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( balloon );

        BufferedImage yelBal = loader.loadBufferedImage( "images/balloon5_filter-ii.GIF" );
        yelBal = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( yelBal );
        BufferedImage yelCharge = loader.loadBufferedImage( "images/balloon5_filter-ii.GIF" );
        yelCharge = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( yelBal );

        paintCharge( yelCharge );
        paintCharge( blueCharge );

        Point bPoint = new Point( 400, 10 );
        BalloonImage bPainter = new BalloonImage( bPoint.x, bPoint.y, balloon, blueCharge );
        Point strAttach = new Point( 59, 233 );
        Point strBase = new Point( 430 + yelBal.getWidth() / 2, 600 );
        int strLength = 300;
        Stroke stringStroke = new BasicStroke( 2.2f );
        Color stringColor = new Color( 200, 10, 230, 160 );
        BalloonPainter b = new BalloonPainter( bPainter, strAttach, strBase, strLength, stringStroke, stringColor );
        b.setInitialPosition( bPoint );

        Point yPoint = new Point( 480, 18 );
        BalloonImage yelPainter = new BalloonImage( yPoint.x, yPoint.y, yelBal, yelCharge );
        BalloonPainter yel = new BalloonPainter( yelPainter, strAttach, strBase, strLength, stringStroke, stringColor );
        yel.setInitialPosition( yPoint );

        LayeredPainter lp = new LayeredPainter();
        Reset reset = new Reset( this, lp, CHARGE_LEVEL );
        reset.addBalloonPainter( yel );
        reset.addBalloonPainter( b );

//        lp.addPainter( new FilledRectanglePainter( width, height, Color.white ), -1 );
        lp.addPainter( new FilledRectanglePainter( width, height, new Color( 240, 240, 255 ) ), -1 );
        DoubleBufferPainter db = new DoubleBufferPainter( lp, width, height );

        painterPanel = new PainterPanel( db );

        int wallWidth = 80;
        Rectangle dragBounds = new Rectangle( 0, 0, W - balloon.getWidth() - wallWidth, H - balloon.getHeight() - 55 );
        BalloonDragger bd = new BalloonDragger( new BalloonPainter[]{b, yel}, painterPanel, dragBounds );
        painterPanel.addMouseListener( bd );
        painterPanel.addMouseMotionListener( bd );

        //double thresholdSpeed=.2;
        double thresholdSpeed = .38;
        ThresholdFilter cm = new ThresholdFilter( 5, 5, thresholdSpeed );//3.2);

        int moveToBalloonSpeed = 9;
        MoveToBalloon mtb = new MoveToBalloon( moveToBalloonSpeed, lp, CHARGE_LEVEL, minnie );
        ChargeMover chargeMover = new ChargeMover( mtb, lp );
        reset.setChargeMover( chargeMover );
        cm.addBalloonDragListener( chargeMover );
        bd.addBalloonDragListener( cm );

        BufferedImage sweaterImage = loader.loadBufferedImage( "images/sweaterWidth300.gif" );

        FixedImagePainter sweater = new FixedImagePainter( sweaterImage );
        lp.addPainter( sweater, 0 );
        lp.addPainter( bd, 2 );

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout( new BoxLayout( controlPanel, BoxLayout.X_AXIS ) );

        JPanel panel = new JPanel();
        panel.setLayout( new BorderLayout() );
        panel.add( painterPanel, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.SOUTH );

        JCheckBox chargedBalloonBtn = new JCheckBox( "Ignore Initial Balloon Charge", true );
        JRadioButton showAllCharges = new JRadioButton( "Show all charges" );
        showAllCharges.addActionListener( new ShowAll( plussy, minnie ) );
        JRadioButton showNoCharges = new JRadioButton( "Show no charges" );
        showNoCharges.addActionListener( new ShowNone( plussy, minnie ) );
        JRadioButton showDiff = new JRadioButton( "Show charge differences" );
        showDiff.addActionListener( new ShowDiff( plussy, minnie ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( showAllCharges );
        bg.add( showNoCharges );
        bg.add( showDiff );
        showNoCharges.setSelected( true );
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
        buttonPanel.add( showAllCharges );
        buttonPanel.add( showNoCharges );
        buttonPanel.add( showDiff );
        buttonPanel.setBorder( BorderFactory.createTitledBorder( "Charge Display" ) );
        JButton resetBtn = new JButton( "Reset" );
        controlPanel.add( resetBtn );
        controlPanel.add( buttonPanel );
        JCheckBox soloBalloon = new JCheckBox( "Only One Balloon", false );
        soloBalloon.addActionListener( new SoloBalloon( soloBalloon, b ) );

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
        twoBtn.add( soloBalloon );
        twoBtn.add( chargedBalloonBtn );
        controlPanel.add( twoBtn );

        JCheckBox showWall = new JCheckBox( "Wall", true );
        controlPanel.add( showWall );

        int wallInset = 10;
        Rectangle wallBounds = new Rectangle( W - wallWidth, 0, wallWidth, H );
        Rectangle wallChargeBounds = new Rectangle( W - wallWidth + wallInset, 0, wallWidth - wallInset * 2, H );
        Painter wallBack = new FilledRectanglePainter( wallBounds.x, wallBounds.y, wallBounds.width, wallBounds.height, Color.yellow );
        Random r = new Random();
        Wall w = new Wall( showWall, 50, wallChargeBounds, wallBack, plussy, minnie, r, b, yel );
        lp.addPainter( w, 10 );

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
            lp.addPainter( plusPaint, CHARGE_LEVEL );
            Painter minusPaint = new ParticlePainterAdapter( minnie, minus );
            minus.setDefaultPainter( minusPaint );
            lp.addPainter( minusPaint, CHARGE_LEVEL );
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
        Rectangle bounds = new Rectangle( sweaterImage.getWidth() / 2, 0, widdie + 143, H - 50 );
        System.err.println( "bounds=" + bounds );
        sys.addLaw( new BalloonForces( b, yel, wool, bounds, wallBounds.x, w ) );
        //sys.addLaw(new BalloonForces(b,yel,wool,dragBounds,wallBounds.x));
        sys.addLaw( w );
        sys.addLaw( ( new Repaint( painterPanel ) ) );
        //sys.addLaw((new Update(painterPanel)));
        double dt = 1;
        int waitTime = 25;
        setContentPane( panel );
        SystemRunner sr = new SystemRunner( sys, dt, waitTime );
        Thread t = new Thread( sr );
        t.setPriority( Thread.MAX_PRIORITY );
        t.start();
        validate();
        //new SizeChecker(this).start();
    }

    public static void main( String[] args ) {
        isApplet = false;
        BalloonApplet ba = new BalloonApplet();
        ba.init();
        JFrame jf = new JFrame( ba.getClass().getName() );
        jf.addWindowListener( new Exit() );
        jf.setContentPane( ba );
        jf.setSize( W, H + 40 );
        jf.setVisible( true );
        //System.out.println("main: height="+ba.getHeight()); //476 for full application.
    }

    public static class Exit extends WindowAdapter {
        public void windowClosing( WindowEvent e ) {
            System.exit( 0 );
        }
    }
}
