package edu.colorado.phet.batteryvoltage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.batteryvoltage.common.electron.components.Gauge;
import edu.colorado.phet.batteryvoltage.common.electron.gui.mouse2.ParticleSelector;
import edu.colorado.phet.batteryvoltage.common.electron.laws.CoulombsLaw;
import edu.colorado.phet.batteryvoltage.common.electron.laws.ForceLawPropagator;
import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.ManMaker;
import edu.colorado.phet.batteryvoltage.common.electron.man.ManPainter;
import edu.colorado.phet.batteryvoltage.common.electron.man.laws.ManLaw;
import edu.colorado.phet.batteryvoltage.common.electron.man.laws.MotionChooser;
import edu.colorado.phet.batteryvoltage.common.electron.man.laws.ThoughtfulMover;
import edu.colorado.phet.batteryvoltage.common.electron.paint.ImageUtils;
import edu.colorado.phet.batteryvoltage.common.electron.paint.LayeredPanel;
import edu.colorado.phet.batteryvoltage.common.electron.paint.animate.AnimateLaw;
import edu.colorado.phet.batteryvoltage.common.electron.paint.animate.Movie;
import edu.colorado.phet.batteryvoltage.common.electron.paint.animate.ParticlePoint;
import edu.colorado.phet.batteryvoltage.common.electron.paint.animate.RotatingTwinkle2;
import edu.colorado.phet.batteryvoltage.common.electron.paint.particle.ImagePainter;
import edu.colorado.phet.batteryvoltage.common.electron.paint.particle.ParticlePainterAdapter;
import edu.colorado.phet.batteryvoltage.common.phys2d.*;
import edu.colorado.phet.batteryvoltage.common.phys2d.laws.Repaint;
import edu.colorado.phet.batteryvoltage.common.phys2d.propagators.*;
import edu.colorado.phet.batteryvoltage.man.Director;
import edu.colorado.phet.batteryvoltage.man.VoltMan;
import edu.colorado.phet.batteryvoltage.man.VoltManFactory;
import edu.colorado.phet.batteryvoltage.man.voltListeners.CompositeVoltageListener;
import edu.colorado.phet.batteryvoltage.man.voltListeners.VoltageListener;

public class Battery {
    Vector papas = new Vector();
    Vector moveListeners = new Vector();
    JPanel controlPanel = new JPanel();
    CompositePropagator leftPropagator;
    CompositePropagator rightPropagator;
    LayeredPanel panel;
    int height;
    System2D sys;
    Random r;
    int y;
    ForceLawPropagator cla;
    BufferedImage electronImage;
    BufferedImage leftPlusImage;
    BufferedImage rightPlusImage;
    Vector leftElectrons = new Vector();
    Vector rightElectrons = new Vector();

    public int numElectrons() {
        return numLeft() + numRight();
    }

    public int numLeft() {
        return leftElectrons.size();
    }

    public int numRight() {
        return rightElectrons.size();
    }

    public System2D getSystem() {
        return sys;
    }

    public Battery( int x, int y, int width, int height, int barrierX, int barrierWidth, int numElectrons, Random r, int numMen, double dt ) {
        this.r = r;
        this.y = y;
        this.height = height;
        this.panel = new LayeredPanel();
        this.sys = new System2D();
        sys.addLaw( new ParticleLaw() );

        double coulombConstantK = 150000;
        double minCoulombDist = 5;//1;
        double maxCoulombDist = Double.MAX_VALUE;//200;
        CoulombsLaw law = new CoulombsLaw( maxCoulombDist, coulombConstantK, minCoulombDist );
        this.cla = new ForceLawPropagator( law );

        FourBounds leftBounds = ( new FourBounds( x, y, barrierX, height, 1.2 ) );
        int a = x + width;
        //util.Debug.traceln("x+width="+a);
        FourBounds rightBounds = ( new FourBounds( barrierX + barrierWidth, y, x + width - barrierX - barrierWidth, height, 1.2 ) );

        double maxElectronSpeed = 225;//200

        leftPropagator = new CompositePropagator();
        leftPropagator.add( new ResetAcceleration() );
        leftPropagator.add( cla );
        leftPropagator.add( leftBounds );
        VelocityUpdate leftMaxSpeed = new VelocityUpdate( maxElectronSpeed );
        leftPropagator.add( leftMaxSpeed );
        leftPropagator.add( new PositionUpdate() );

        rightPropagator = new CompositePropagator();
        rightPropagator.add( new ResetAcceleration() );
        rightPropagator.add( cla );
        rightPropagator.add( rightBounds );
        VelocityUpdate rightMaxSpeed = ( new VelocityUpdate( maxElectronSpeed ) );
        rightPropagator.add( rightMaxSpeed );
        rightPropagator.add( new PositionUpdate() );

        sys.addLaw( new Repaint( panel ) );
        this.electronImage = BatteryVoltageResources.getImage( "electron9.gif" );
        electronImage = ImageUtils.scale( electronImage, .45 );
        this.rightPlusImage = BatteryVoltageResources.getImage( "components/batteries/AA-battery-600.gif" );
        this.leftPlusImage = BatteryVoltageResources.getImage( "components/batteries/AA-battery-600-left.gif" );
        this.rightPlusImage = ImageUtils.scaleToSizeApproximate( rightPlusImage, width + 60, height + 35 );
        this.leftPlusImage = ImageUtils.scaleToSizeApproximate( leftPlusImage, width + 60, height + 35 );

        WallPainter wp = new WallPainter( leftBounds.toRectangle(), 10 );
        panel.addPainter( wp );
        wp = new WallPainter( rightBounds.toRectangle(), 10 );
        panel.addPainter( wp );
        BatteryImagePainter bip = new BatteryImagePainter( leftPlusImage );
        panel.addPainter( bip );

        VoltageListener imageChanger = new ImageChanger( bip, leftPlusImage, rightPlusImage, numElectrons );

        Movie movie = new Movie();

        for ( int i = 0; i < numElectrons / 2; i++ ) {
            addParticle( x, barrierX, leftPropagator, leftElectrons, movie );
        }
        for ( int i = numElectrons / 2; i < numElectrons; i++ ) {
            addParticle( barrierX + barrierWidth, x + width - barrierX - barrierWidth, rightPropagator, rightElectrons, movie );
        }

        /*Add the Magnesium chemicals.*/
        double timeScale = 12;
        ManLaw ml = new ManLaw( timeScale );
        sys.addLaw( ml );
        double manSizeScale = .17;//.2
        Vector carried = new Vector();
        double thresholdX = barrierX + barrierWidth / 2;
        double grabDist = 10;
        double goToElectronSpeed = 16;
        double minCarrySpeed = 2;
        double maxCarrySpeed = goToElectronSpeed * 1.2;
        double barrierExitThreshold = 10;
        Hashtable carrierMap = new Hashtable();
        double goHomeSpeed = goToElectronSpeed;
        double homeThreshold = 10;
        int barrierInset = 10;
        Vector targeted = new Vector();

        VoltManFactory brainFactory = new VoltManFactory( barrierX, barrierWidth, goToElectronSpeed, leftPropagator, rightPropagator, barrierInset, carrierMap, goHomeSpeed, carried, homeThreshold, minCarrySpeed, maxCarrySpeed, targeted, this );

        Color[] colors = new Color[]{Color.red, Color.blue, Color.green, Color.gray, Color.orange, Color.yellow, new Color( 200, 20, 200 ), Color.white};

        JSlider heightSlider = new JSlider( 0, numElectrons, numElectrons / 2 );
        heightSlider.setPreferredSize( new Dimension( 275, 50 ) );
        heightSlider.setMajorTickSpacing( 4 );
        heightSlider.setMinorTickSpacing( 2 );
        heightSlider.setPaintTicks( true );
        CompositeVoltageListener cvl = new CompositeVoltageListener( heightSlider, this );
        heightSlider.setBorder( BorderFactory.createTitledBorder( BatteryVoltageResources.getString( "Battery.VoltageSlider" ) ) );
        heightSlider.addChangeListener( cvl );
        controlPanel.setLayout( new FlowLayout() );

        JCheckBox show = new JCheckBox( BatteryVoltageResources.getString( "Battery.ShowBatteryCheckBox" ) );
        show.addItemListener( new ShowBattery( show, bip ) );
        controlPanel.add( show );
        controlPanel.add( heightSlider );
        int homeInset = 5;//20
        int homeY = y + homeInset;
        int homeHeight = height - 2 * homeInset;
        int homeDY = homeHeight / numMen;
        Director director = new Director( sys, carried, targeted, 0, thresholdX );

        addParticleMoveListener( director );
        cvl.addVoltageListener( director );
        cvl.addVoltageListener( imageChanger );
        addParticleMoveListener( new MaxSpeed( leftMaxSpeed, rightMaxSpeed ) );

        for ( int i = 0; i < numMen; i++ ) {
            Man m = new ManMaker( manSizeScale ).newMan();

            DoublePoint home = new DoublePoint( thresholdX, homeY + i * homeDY );
            DoublePoint dh = home.subtract( m.getNeck().getPosition() );
            m.getNeck().translate( dh.getX(), dh.getY() );

            VoltMan vm = brainFactory.newMan( m, home );
            director.addVoltMan( vm );
            MotionChooser mc = vm;
            ThoughtfulMover tm = new ThoughtfulMover( m, mc );

            ml.add( tm );
            Color color = colors[i % colors.length];
            panel.addPainter( new ManPainter( m, new BasicStroke( 2 ), color ) );//Color.white));
        }

        int gx = x + width + 60;
        int gy = 200;
        int gaugeWidth = 250;
        Gauge g = new Gauge( gx, gy, -numElectrons, numElectrons, 0, gaugeWidth );
        GaugeUpdate gu = new GaugeUpdate( g, sys, rightPropagator, leftPropagator );
        addParticleMoveListener( gu );
        g.setText( BatteryVoltageResources.getString( "Battery.GaugeText" ) );
        panel.addPainter( new GuiToPaint( g ) );

        Vector all = new Vector();
        all.addAll( leftElectrons );
        all.addAll( rightElectrons );
        director.initTags( (Particle[]) all.toArray( new Particle[0] ) );
        cvl.stateChanged( null );
        fireParticleMoved( null );

        ParticleSelector ps = new ParticleSelector();
        ps.addAll( (ParticlePainterAdapter[]) papas.toArray( new ParticlePainterAdapter[0] ) );
        VoltParticleGrabber pg = new VoltParticleGrabber( panel, ps, new NullPropagator(), rightPropagator, leftPropagator, x + width / 2, this, director );
        panel.addMouseMotionListener( pg );
        panel.addMouseListener( pg );

        int layer = 100;
        AnimateLaw al = new AnimateLaw( dt * 2.5, movie, panel, layer );
        new Thread( new AnimateAdder( 1200, al, sys, panel, layer ) ).start();

        al = new AnimateLaw( dt * 2.5, movie, panel, layer );
        new Thread( new AnimateAdder( 5000, al, sys, panel, layer ) ).start();

        al = new AnimateLaw( dt * 2.5, movie, panel, layer );
        new Thread( new AnimateAdder( 10000, al, sys, panel, layer ) ).start();
    }

    public static class AnimateAdder implements Runnable {
        int waitTime;
        AnimateLaw al;
        System2D sys;
        LayeredPanel p;
        int layer;

        public AnimateAdder( int waitTime, AnimateLaw al, System2D sys, LayeredPanel p, int layer ) {
            this.waitTime = waitTime;
            this.al = al;
            this.sys = sys;
            this.p = p;
            this.layer = layer;
        }

        public void run() {
//            util.ThreadHelper.quietNap( waitTime );
            try {
                Thread.sleep( waitTime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            sys.addLaw( al );
            p.addPainter( al, layer );
        }
    }

    public void addParticleMoveListener( ParticleMoveListener pml ) {
        this.moveListeners.add( pml );
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public void setLeft( Particle p ) {
        rightElectrons.remove( p );
        if ( !leftElectrons.contains( p ) ) {
            leftElectrons.add( p );
        }
        fireParticleMoved( p );
    }

    public void setRight( Particle p ) {
        leftElectrons.remove( p );
        if ( !rightElectrons.contains( p ) ) {
            rightElectrons.add( p );
        }
        fireParticleMoved( p );
    }

    void fireParticleMoved( Particle p ) {
        for ( int i = 0; i < moveListeners.size(); i++ ) {
            ParticleMoveListener pml = (ParticleMoveListener) moveListeners.get( i );
            pml.particleMoved( this, p );
        }
    }

    public void addParticle( int x, int width, Propagator p, Vector container, Movie m ) {
        PropagatingParticle pp = new PropagatingParticle( p );
        container.add( pp );
        int xPos = r.nextInt( width ) + x;
        int yPos = r.nextInt( height ) + y;
        pp.setVelocity( new DoublePoint( 0, 0 ) );
        pp.setPosition( new DoublePoint( xPos, yPos ) );
        ParticlePainterAdapter pta = ( new ParticlePainterAdapter( new ImagePainter( electronImage ), pp ) );
        panel.addPainter( pta );
        papas.add( pta );
        cla.add( pp );
        pp.setCharge( -1 );
        sys.addParticle( pp );

        Stroke s = new BasicStroke( .8f );//,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND );
        //if (i%2==0)
        {
            //TwinkleMovie tm=new TwinkleMovie(new ParticlePoint(pp));
            //FastTwinkle tm=new FastTwinkle(new ParticlePoint(pp));

            RotatingTwinkle2 rt = new RotatingTwinkle2( new ParticlePoint( pp ), s, 20, 3, 8, 15 );

            m.addAnimation( rt );//,15);
            //m.addAnimation(tm);//,15);
        }
    }

    public JPanel getJPanel() {
        return panel;
    }
}
