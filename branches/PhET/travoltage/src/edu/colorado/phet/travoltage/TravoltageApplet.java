package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.PanelAdapter;
import edu.colorado.phet.common.SystemAdapter;
import edu.colorado.phet.common.gui.*;
import edu.colorado.phet.common.gui.grabber.ParticleGrabber;
import edu.colorado.phet.common.gui.grabber.ParticleThrower;
import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.System2D;
import edu.colorado.phet.common.phys2d.SystemRunner;
import edu.colorado.phet.common.phys2d.laws.CoulombsLaw;
import edu.colorado.phet.common.phys2d.laws.ForceLawPropagator;
import edu.colorado.phet.common.utils.ResourceLoader;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.travoltage.rotate.Leg;
import edu.colorado.phet.travoltage.rotate.RotatingImage;

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TravoltageApplet extends JApplet {
    private BufferedImage carpet;
    private BufferedImage johnBitmap;
    private BufferedImage doorknob;
    private BufferedImage knob;
    private BufferedImage overlay;

    public void init() {
        ParticlePanel pp = new ParticlePanel();

        AudioClip ouch = ResourceLoader.loadAudioClip( "sound/OuchSmallest.wav", pp );
        AudioClip zzt = ResourceLoader.loadAudioClip( "sound/ShockSmallest.wav", pp );
        AudioClip[] audioClips = new AudioClip[]{ouch, zzt};

        johnBitmap = ResourceLoader.loadBufferedImage( "images/travolta/JT5-Bitmap_Colors2.GIF", pp, false );
        doorknob = ResourceLoader.loadBufferedImage( "images/travolta/JT5-Bitmap_Colors_Doorknob2.GIF", pp, false );

        PixelVelocityLookup2 pvl = new PixelVelocityLookup2();
        pvl.add( new int[]{255, 0, 0}, new DoublePoint( 0, -1 ) );
        pvl.add( new int[]{128, 0, 128}, new DoublePoint( 1, 0 ) );
        pvl.add( new int[]{0, 128, 0}, new DoublePoint( -1, -1 ) );//
        pvl.add( new int[]{128, 128, 128}, new DoublePoint( 0, 1 ) );
        pvl.add( new int[]{255, 153, 51}, new DoublePoint( -1, -1 ) );
        pvl.add( new int[]{255, 0, 153}, new DoublePoint( 1, 0 ) );
        pvl.add( new int[]{255, 255, 0}, new DoublePoint( 1, -1 ) );
        pvl.add( new int[]{0, 255, 255}, new DoublePoint( 1, 0 ) );

        TravoltaImageBounce tib = new TravoltaImageBounce( johnBitmap, pvl );
        ForceLawPropagator coulomb = new ForceLawPropagator( new CoulombsLaw( 10000 ) ); //maybe 100000?
        TravoltaPropagator tp = new TravoltaPropagator( tib, coulomb, 50 );

        CompositeParticleContainer cpc = new CompositeParticleContainer();
        cpc.add( tp );
        System2D sys = new System2D();
        double dt = .15;
        int wait = 35;
        SystemRunner sr = new SystemRunner( sys, dt, wait );
        sys.addLaw( new RepaintLaw( pp ) );
        sys.addLaw( new PropagatorToLawAdapter() );
        new Thread( sr ).start();

        BufferedImage bi = edu.colorado.phet.common.utils.ResourceLoader.loadBufferedImage( "images/Electron3.GIF", pp, true );
        bi = edu.colorado.phet.common.utils.AlphaFixer.patchAlpha( bi );
        ParticlePainter painter = new ImagePainter( bi );

        ParticleGrabber pg = new ParticleThrower( pp, sys, sr, 5, 18 );
        pp.addMouseListener( pg );
        pp.addMouseMotionListener( pg );
        pp.setBackground( Color.yellow );
        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( pp, BorderLayout.CENTER );
        validate();

        ShockElectronFactory shoeElectrons = new ShockElectronFactory( 160, johnBitmap.getHeight() - 50, 20, 20, tp );
        BufferedImage arm = ResourceLoader.loadBufferedImage( "images/travolta/arm.GIF", pp, true );
        arm = edu.colorado.phet.common.utils.AlphaFixer.patchAlpha( arm );

        double armAngle = 5.3;
        int armX = 180;
        int armY = 125; //Join the armY.
        final RotatingImage armRI = ( new RotatingImage( arm, armAngle, armX, armY, pp, 80, 20, 45, Math.PI + Math.PI / 8 ) );

        double maxVel = 100.0;

        RemovalPropagator rip = new RemovalPropagator( cpc );
        GoToFinger gtf = new GoToFinger( armRI, tib, coulomb, maxVel, rip );
        GoToElbow gte = new GoToElbow( armRI, tib, coulomb, maxVel, gtf );

//  	ScuffCarpet scuff=new ScuffCarpet(new Vector(),shoeElectrons,pp,painter,tib,gte,johnBitmap,tp);
        PanelAdapter panelAdapter = ( new PanelAdapter( pp, painter ) );
        cpc.add( panelAdapter );
//  	scuff.add(new PanelAdapter(pp,painter));
        cpc.add( new SystemAdapter( sys ) );
//  	//scuff.add(coulomb);
//  	scuff.add(tp);

        JPanel southPanel = new JPanel();
        southPanel.setLayout( new BoxLayout( southPanel, BoxLayout.X_AXIS ) );

        getContentPane().add( southPanel, BorderLayout.SOUTH );

        overlay = edu.colorado.phet.common.utils.ResourceLoader.loadBufferedImage( "images/travolta/JT5.jpg", pp, true );
        pp.add( new BufferedImagePainter( overlay ) );

        knob = ResourceLoader.loadBufferedImage( "images/doorknobs/images.jpg", pp, true );
        knob = edu.colorado.phet.common.utils.AlphaFixer.patchAlpha( knob );
        pp.add( new BufferedImagePainter( knob, AffineTransform.getTranslateInstance( overlay.getWidth() - 15, overlay.getHeight() * .35 ) ) );

        pp.add( armRI );
        //edu.colorado.phet.common.util.Debug.traceln("Adding armRI grabber listener.");
        pp.addMouseMotionListener( armRI );
        pp.addMouseListener( armRI );


        //armRI.addAngleListener(new Arm(scuff,6.58,.557));

        BufferedImage leg = ResourceLoader.loadBufferedImage( "images/travolta/leg.GIF", pp, true );
        leg = edu.colorado.phet.common.utils.AlphaFixer.patchAlpha( leg );
        final RotatingImage legRI = ( new RotatingImage( leg, -0.176614109401938, 130, 255, pp, 80, 20, 20, Math.PI / 2 + Math.PI / 8 ) );
        pp.add( legRI );
        //edu.colorado.phet.common.util.Debug.traceln("Adding legRI grabber listener.");
        pp.addMouseMotionListener( legRI );
        pp.addMouseListener( legRI );
        legRI.addAngleListener( new Leg( shoeElectrons, cpc ) );


        MouseMotionListener mml = new MouseMotionListener() {
            public void mouseDragged( MouseEvent e ) {
            }

            public void mouseMoved( MouseEvent e ) {
                if( armRI.getShape().contains( e.getX(), e.getY() ) || legRI.getShape().contains( e.getX(), e.getY() ) ) {
                    e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                }
                else {
                    e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
                }
            }
        };
        pp.addMouseMotionListener( mml );

        carpet = ResourceLoader.loadBufferedImage( "images/travolta/carpet.GIF", pp, true );

        carpet = edu.colorado.phet.common.utils.AlphaFixer.patchAlpha( carpet );
        pp.add( new BufferedImagePainter( carpet, AffineTransform.getTranslateInstance( 0, overlay.getHeight() - 25 ) ) );

        Point doorknobSpot = new Point( 302, 160 ); //doorknob position
        Point end = new Point( 100, 100 );
        double angle = Math.PI / 3.8;
        Spark sparky = new Spark( doorknobSpot, end, angle, 4, 6 );

        DynamicShockLaw dsl = new DynamicShockLaw( armRI, doorknobSpot.x, doorknobSpot.y, gte, sparky, 100, audioClips );
        armRI.addAngleListener( dsl );

        cpc.add( dsl );
        pp.add( dsl );//add the travoltage painter
    }

    public int getJohnHeight() {
        int insets = 40;
        return carpet.getHeight() + this.overlay.getHeight() + insets;
    }

    public int getJohnWidth() {
        int insets = 40;
        return overlay.getWidth() + knob.getWidth() + insets;
    }

    public static void main( String[] args ) {
        TravoltageApplet applet = new TravoltageApplet();
        applet.init();
        Dimension size = new Dimension( applet.getJohnWidth(), applet.getJohnHeight() );
        applet.setSize( size );

        JFrame f = new JFrame( "Travoltage" );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.setContentPane( applet );
//        applet.setBackground( Color.yellow );
        f.pack();
        f.setSize( size );
        GraphicsUtil.centerFrameOnScreen( f );
        f.setVisible( true );
        f.validate();
    }
}
