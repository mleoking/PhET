package phet.ohm1d.volt;

import phet.math.functions.Transform;
import phet.ohm1d.AngelPaint;
import phet.ohm1d.AverageCurrent2;
import phet.ohm1d.Electron;
import phet.ohm1d.Resistance;
import phet.ohm1d.collisions.Collider;
import phet.ohm1d.collisions.DefaultCollisionEvent;
import phet.ohm1d.gui.CoreCountSlider;
import phet.ohm1d.gui.ShowPainterListener;
import phet.ohm1d.gui.ShowPainters;
import phet.ohm1d.gui.VoltageSlider;
import phet.ohm1d.oscillator2d.DefaultOscillateFactory;
import phet.ohm1d.oscillator2d.OscillateFactory;
import phet.ohm1d.regions.AndRegion;
import phet.ohm1d.regions.PatchRegion;
import phet.ohm1d.regions.SimplePatch;
import phet.ohm1d.util.MakeMETransp;
import phet.paint.*;
import phet.paint.gauges.GaugeScaling;
import phet.paint.gauges.IGauge;
import phet.paint.gauges.ImageGauge;
import phet.paint.gauges.Scaling;
import phet.paint.particle.ParticlePainter;
import phet.paint.vector.DefaultVectorPainter;
import phet.paint.vector.VectorPainter;
import phet.paint.vector.VectorPainterAdapter;
import phet.phys2d.DoublePoint;
import phet.phys2d.ParticleLaw;
import phet.phys2d.System2D;
import phet.phys2d.SystemRunner;
import phet.phys2d.laws.Repaint;
import phet.utils.AlphaFixer2;
import phet.utils.MakeTransparentImage;
import phet.utils.ResourceLoader4;
import phet.wire1d.Circuit;
import phet.wire1d.WirePatch;
import phet.wire1d.WireSystem;
import phet.wire1d.forces.*;
import phet.wire1d.paint.WireParticlePainter;
import phet.wire1d.paint.WirePatchPainter;
import phet.wire1d.propagators.CompositePropagator1d;
import phet.wire1d.propagators.DualJunction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class Ohm1dModule extends JApplet {
    public Ohm1dModule() {
        //System.err.println("HI");
    }

    public void init() {
        super.init();
        //System.err.println("HI");
        JButton jb = new JButton( "Start applet" );
        getContentPane().add( jb );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                try {
                    mainBAK();
                }
                catch( Throwable t ) {
                    throw new RuntimeException( t );
                }
            }
        } );
    }

    public void mainBAK() throws IOException, FontFormatException {
        ResourceLoader4 loader = new ResourceLoader4( getClass().getClassLoader(), this );
        int moveRight = 68;
        //int scatInset = 60;
        int scatInset = 60 + moveRight;
        int battInset = scatInset;
        WirePatch wp = new WirePatch();//wp
        DoublePoint topLeftWirePoint = new DoublePoint( 25 + moveRight, 120 );       //top left
        DoublePoint topRightWirePoint = new DoublePoint( 700 + moveRight, 120 );     //top right
        DoublePoint bottomRightWirePoint = new DoublePoint( 700 + moveRight, 270 );    //bottom right
        DoublePoint bottomLeftWirePoint = new DoublePoint( 25 + moveRight, 270 ); //bottom left.
        DoublePoint topLeftInset = topLeftWirePoint.add( new DoublePoint( scatInset - moveRight, 0 ) );
        DoublePoint topRightInset = topRightWirePoint.add( new DoublePoint( -scatInset + moveRight, 0 ) );

        DoublePoint bottomLeftInset = bottomLeftWirePoint.add( new DoublePoint( battInset - moveRight, 0 ) );
        DoublePoint bottomRightInset = bottomRightWirePoint.add( new DoublePoint( -battInset + moveRight, 0 ) );

        wp.start( bottomLeftInset, bottomLeftWirePoint );
        wp.add( topLeftWirePoint );
        wp.add( topRightWirePoint );
        wp.add( bottomRightWirePoint );
        wp.add( bottomRightInset );
//        wp.start(bottomLeftWirePoint, topLeftWirePoint);
//        wp.add(topRightWirePoint);
//        wp.add(bottomRightWirePoint);

        WirePatch wp2 = new WirePatch();
        //wp2.start(bottomRightWirePoint, bottomLeftWirePoint);
        wp2.start( bottomRightInset, bottomLeftInset );

        Circuit cir = new Circuit();
        cir.addWirePatch( wp );
        cir.addWirePatch( wp2 );

        LayeredPainter cp = new LayeredPainter();
        AffineTransform at = AffineTransform.getScaleInstance( 1.5, 1.5 );
        Point affineTransformPanelDimension = new Point( 736 + moveRight, 586 );
        //AffineTransformPanel pp = new AffineTransformPanel(cp, affineTransformPanelDimension.x, affineTransformPanelDimension.y, at);
        PainterPanel pp = new PainterPanel( cp );

        Stroke wireStroke = new BasicStroke( 35.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL );
        //Color gold = Color.yellow;
        BasicStroke goldStroke = new BasicStroke( 95f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
        //WirePatchPainter goldWire = (new WirePatchPainter(goldStroke, gold, wp));

        //cp.addPainter(new WirePatchPainter(wireStroke, Color.green, wp2));

        /**Try painting separate regions for the scattering region and not.*/
        WirePatch scatterPatch = new WirePatch();
        scatterPatch.start( topLeftInset, topRightInset );

        WirePatch leftPatch = new WirePatch();
        leftPatch.start( bottomLeftInset, bottomLeftWirePoint );
        leftPatch.add( topLeftWirePoint );
        //leftPatch.start(bottomLeftWirePoint,topLeftWirePoint);
        leftPatch.add( topLeftInset );
        //Color darkBrown=new Color(180,90,100);
        Color darkBrown = new Color( 200, 120, 90 );
        cp.addPainter( new WirePatchPainter( wireStroke, darkBrown, leftPatch ) );

        WirePatch rightPatch = new WirePatch();
        rightPatch.start( topRightInset, topRightWirePoint );
        rightPatch.add( bottomRightWirePoint );
        rightPatch.add( bottomRightInset );
        cp.addPainter( new WirePatchPainter( wireStroke, darkBrown, rightPatch ) );

        int maxResistance = 14;
        double vMax = 10;
        //double vMax = 30;
        int yOffsetForSpct = 400;
        Spectrum cm = new Spectrum( loader.loadBufferedImage( "images/spectra/spect3.jpg" ), 100 );
        Filament filament = new Filament( goldStroke, scatterPatch, vMax * .6, 1, cm );

        BufferedImage xar = loader.loadBufferedImage( "icons/Play24.GIF" );
        AlphaFixer2 alphaFixerForIcons = new AlphaFixer2( new int[]{192, 192, 192, 255} );
        xar = alphaFixerForIcons.patchAlpha( xar );

        BlackbodyScale bs = new BlackbodyScale( loader.loadBufferedImage( "images/spectra/fullSpectrum.jpg" ), 350, 10 + yOffsetForSpct,
                                                xar );
        cm.addPowerListener( bs );
        cp.addPainter( bs );
        float xhot = 660;
        float yhot = 30 + yOffsetForSpct;
        float xcold = 360;
        float ycold = yhot;
        Font heatFont = new Font( null, -1, 19 );
        cp.addPainter( new TextPainter( "hot", xhot, yhot, heatFont, Color.black ) );
        cp.addPainter( new TextPainter( "cold", xcold, ycold, heatFont, Color.white ) );
        cp.addPainter( filament );

        WireSystem ws = new WireSystem();
        CompositePropagator1d prop = new CompositePropagator1d();

        double vmax = 15;

        double k = 900;//Math.pow(10,2.3);

        double coulombPower = -1.3;

        CoulombForceParameters cfp = new CoulombForceParameters( k, coulombPower );
        CoulombForce cf = ( new CoulombForce( cfp, ws ) );
        cfp.setMinDistance( 2 );

        double CORE_START = 300;//230
        double CORE_END = 775;//720
        int numCores = 6;

        double amplitude = 70;//30
        double freq = 2.6;
        double decay = .93;//.99
        System2D sys = new System2D();
        int CORE_LEVEL = 4;
        int CORE_LEVEL_BOTTOM = 1;
        ShowPainters showCores = new ShowPainters( cp, new int[]{CORE_LEVEL, CORE_LEVEL_BOTTOM} );

        //CenteredDotPainter dp = new CenteredDotPainter(Color.red, 12);
        BufferedImage greeny = loader.loadBufferedImage( "images/ron/particle-green-med.gif" );
        greeny = new MakeMETransp( new int[]{0} ).patchAlpha( greeny );
        ParticlePainter dp = new phet.paint.particle.ImagePainter( greeny );
        Resistance resistance = new Resistance( CORE_START, CORE_END, numCores, wp, amplitude, freq, decay, dp, CORE_LEVEL, CORE_LEVEL_BOTTOM, cp, showCores, sys );
        //double accelInset = 35;
        double accelInset = 15;
        double coulombInset = 10;
        PatchRegion accelRegion = new PatchRegion( CORE_START - accelInset, CORE_END + accelInset, wp );
        PatchRegion scatteringRegionNoCoulomb = new PatchRegion( CORE_START - coulombInset, CORE_END + coulombInset, wp );

        WireRegion batt = new SimplePatch( wp2 );
        CompositePropagator1d cpr = new CompositePropagator1d();
        RangedPropagator range = new RangedPropagator();
        double inset = 50;
        double battX = CORE_START - inset;
        double battY = CORE_END + inset;
        //o.O.bottomRightWirePoint("Battx="+battX+", batty="+battY);
        PatchRegion leftSideBatt = new PatchRegion( 0, battX, wp );
        PatchRegion rightSideBatt = new PatchRegion( battY, wp.getLength(), wp );//(CORE_END+CORE_START/2),wp.getLength(),wp);

        //Batt battery = (new Batt(leftSideBatt, rightSideBatt, ws, batterySpeed, 3));
        double batterySpeed = 35;//10
        SmoothBatt battery = ( new SmoothBatt( leftSideBatt, rightSideBatt, ws, batterySpeed, 18, 75 ) );
        range.addPropagator( batt, battery );
        range.addPropagator( batt, new ResetElectron() );
        cpr.addPropagator( range );
        cpr.addPropagator( new Crash() );
        prop.addPropagator( cpr );
        //ForcePropagator fp = new ForcePropagator(-vmax, vmax);
        BatteryForcePropagator fp = new BatteryForcePropagator( 0, 10 * vmax );
        fp.addForce( cf );
        /*Add a coulomb force from the end of wp2 onto the beginning of wp.*/
        fp.addForce( new AdjacentPatchCoulombForce( cfp, ws, wp2, wp ) );
        fp.addForce( new AdjacentPatchCoulombForce2( cfp, ws, wp2, wp ) );
        fp.addForce( new Friction1d( .9999999 ) );

        ResetScatterability rs = new ResetScatterability( ws );
        AndRegion nonCoulombRegion = new AndRegion();
        nonCoulombRegion.addRegion( batt );
        //PatchRegion scatteringRegionNoCoulomb2 = new PatchRegion(CORE_START - accelInset, CORE_END + accelInset, wp);
        nonCoulombRegion.addRegion( scatteringRegionNoCoulomb );  //comment out this line to put coulomb interactions into the scattering region

        double accelScale = 1.4;
        Accel scatProp = new Accel( 2, vmax * 15, accelScale );
        range.addPropagator( accelRegion, scatProp );
        range.addInverse( nonCoulombRegion, fp );
        prop.addPropagator( new DualJunction( wp, wp2 ) );
        prop.addPropagator( new DualJunction( wp2, wp ) );

        BufferedImage ronImage = loader.loadBufferedImage( "images/ron/particle-blue-sml.gif" );
        //ronImage=new AlphaFixer2(new int[]{252,254,252,255}).patchAlpha(ronImage);
        ParticlePainter painter = new phet.paint.particle.ImagePainter( ronImage );

        BufferedImage batteryImage = loader.loadBufferedImage( "images/ron/AA-battery-555-left.gif" );
        //batteryImage=new MakeTransparentImage(195).patchAlpha(batteryImage);
        int battImageX = (int)bottomLeftWirePoint.getX() + 59;
        int battImageY = (int)bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3;
        BufferedImagePainter battPainter = new BufferedImagePainter( batteryImage, battImageX, battImageY );
        int BATT_LAYER = 10;

        //Need an image changer component
        //BufferedImage batteryImage2 = loader.loadBufferedImage("images/components/batteries/AA256_H100-Hollow-Right-big-empty-hack2.GIF");
        //batteryImage2 = new AlphaFixer2(new int[]{248, 248, 247, 255}).patchAlpha(batteryImage2);
        BufferedImage batteryImage2 = loader.loadBufferedImage( "images/ron/AA-battery-555.gif" );
        //batteryImage2=new MakeTransparentImage(195).patchAlpha(batteryImage2);
        //batteryImage2=batteryImage;
        BufferedImagePainter battPainter2 = new BufferedImagePainter( batteryImage2, (int)bottomLeftWirePoint.getX() + 59, (int)bottomLeftWirePoint.getY() - batteryImage2.getHeight() / 2 + 3 );

        int batteryTransparentness = 150;//195
        BufferedImagePainter transLeft = new BufferedImagePainter( new MakeTransparentImage( batteryTransparentness ).patchAlpha( batteryImage ), (int)bottomLeftWirePoint.getX() + 59, (int)bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3 );
        BufferedImagePainter transRight = new BufferedImagePainter( new MakeTransparentImage( batteryTransparentness ).patchAlpha( batteryImage2 ), (int)bottomLeftWirePoint.getX() + 59, (int)bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3 );

        BatteryPainter bp = new BatteryPainter( battPainter, battPainter2, transLeft, transRight );
        BatteryDirectionChanger bdc = new BatteryDirectionChanger( bp );//battPainter,battPainter2,cp,BATT_LAYER);

        cp.addPainter( bp, BATT_LAYER );
        //Add bottomLeftWirePoint gauge to monitor the current.
        int gaugeX = 25;
        int gaugeY = 390;
        int numParticles = 50;
        double maxCurrent = vmax * 4;//numParticles*1.2;
        //BufferedImage gaugeImage = loader.loadBufferedImage("images/components/gauges/vdo_samp_srr.JPG");
        AlphaFixer2 alphaFixer = new AlphaFixer2( new int[]{252, 254, 252, 255} );
        BufferedImage gaugeImage = loader.loadBufferedImage( "images/components/gauges/vdo_samp_srr_edit3.gif" );
        gaugeImage = alphaFixer.patchAlpha( gaugeImage );

        int needleX = gaugeImage.getWidth() / 2;
        int needleY = gaugeImage.getHeight() / 2 + 38;
        int needleLength = gaugeImage.getWidth() / 2 - 30;
        IGauge gauge = new ImageGauge( gaugeImage, gaugeX, gaugeY, needleX, needleY, needleLength );
        cp.addPainter( gauge );

        //WireRegion currentRegion=new PatchRegion(CORE_START+10,CORE_END-10,wp);
        AndRegion currentRegion = new AndRegion();
        currentRegion.addRegion( new SimplePatch( wp ) );
        currentRegion.addRegion( new SimplePatch( wp2 ) );
        AverageCurrent2 current = new AverageCurrent2( gauge, 100, currentRegion );

        //current.addCurrentListener(gauge);
        //wp,CORE_START+10,CORE_END-10);//wp.getLength());
        //AverageCurrent current=new AverageCurrent(gauge,45);
        GaugeScaling gus = new GaugeScaling();
        gus.add( new Scaling( gauge, "Low", -maxCurrent / 4, maxCurrent / 4 ), false );
        Scaling medium = new Scaling( gauge, "Medium", -maxCurrent / 2, maxCurrent / 2 );
        gus.add( medium, true );
        gus.add( new Scaling( gauge, "High", -maxCurrent, maxCurrent ), false );
        gus.setBorder( BorderFactory.createTitledBorder( "Ammeter Scale" ) );
        medium.actionPerformed( null );
        int ELECTRON_LEVEL = 3;
        ShowPainters showElectrons = new ShowPainters( cp, new int[]{ELECTRON_LEVEL, ELECTRON_LEVEL - 1} );

        //double height=topLeftWirePoint.getY()-topRightWirePoint.getY();
        resistance.layoutCores();
        double aMax = Double.MAX_VALUE;//10000;//35;
        DoublePoint axis = new DoublePoint( 1, 2 );
        double vToAmplitudeScale = .9;
        Random random = new Random();
        OscillateFactory of = new DefaultOscillateFactory( random, vToAmplitudeScale, decay, freq, aMax, axis );//Provides the collisionsDeprecated.
        double amplitudeThreshold = 2000;//1.6;
        double collisionDist = 18;
        DefaultCollisionEvent ce = new DefaultCollisionEvent( collisionDist, amplitudeThreshold, of );
        sys.addLaw( ce );//to time the collisionsDeprecated.
//        sys.addLaw(equalizeRight);
//        sys.addLaw(equalizeLeft);//equalize left and right
        Collider coll = new Collider( ws, ce, wp );

        int dx = (int)( cir.getLength() / numParticles );
        int mod = 0;
        for( int i = 0; i < numParticles; i++ ) {
            Electron particle1 = new Electron( prop, wp, ce );
            double position = dx * i;
            boolean makeParticle = true;
            if( position > CORE_START && position < CORE_END && mod++ % 2 == 0 ) {
                makeParticle = false;
            }
            if( makeParticle ) {
                //particle1.setPosition(position);
                particle1.setVelocity( 0 );
                ws.add( particle1 );
                particle1.setWirePatch( cir.getPatch( position ) );
                particle1.setPosition( cir.getLocalPosition( position, cir.getPatch( position ) ) );
                Painter p = ( new WireParticlePainter( particle1, painter ) );
                int rand = random.nextInt( 2 );         //!!!
                cp.addPainter( p, ELECTRON_LEVEL - rand );
                current.addParticle( particle1 );
                showElectrons.add( p );
            }
        }

        sys.addLaw( ws );
        sys.addLaw( coll );
        sys.addLaw( new ParticleLaw() );
        sys.addLaw( current ); //Uncomment this to show the actual current.
        sys.addLaw( new Repaint( pp ) );

        JFrame f = new JFrame( "Ohm's Law" );
        pp.setBackground( new Color( 235, 230, 240 ) );
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        //pp.addComponentListener(new AffineTransformResize(pp, pp, 736, 586));//uncomment this line to use a resizable painter panel.
        mainPanel.add( pp, BorderLayout.CENTER );

        int baseFrameWidth = 1028;
        f.setSize( baseFrameWidth, 620 );

        JFrame control = new JFrame( "Controls" );
        control.setLocation( baseFrameWidth, 0 );
        JPanel conPan = new JPanel();
        conPan.setBorder( BorderFactory.createTitledBorder( "Control Panel" ) );
        GridBagLayout gridLayout = new GridBagLayout();
        conPan.setLayout( gridLayout );
        //conPan.setLayout(conPanLayout);//new BoxLayout(conPan, BoxLayout.Y_AXIS));

        JPanel butPan = new JPanel();
        butPan.setLayout( new BoxLayout( butPan, BoxLayout.Y_AXIS ) );

        JCheckBox showCoreBox = new JCheckBox( "Show Cores", true );
        showCoreBox.addActionListener( new ShowPainterListener( showCoreBox, showCores ) );
        //conPan.add(showCoreBox);
        butPan.add( showCoreBox );

        JCheckBox showElectronBox = new JCheckBox( "Show Electrons", true );
        showElectronBox.addActionListener( new ShowPainterListener( showElectronBox, showElectrons ) );
        //conPan.add(showElectronBox);
        butPan.add( showElectronBox );

        JCheckBox showVoltDesc = new JCheckBox( "Show Voltage Calculation", false );
        int VP_LEVEL = 100;
        CompositePainter vp = new CompositePainter();
        ShowPainters showVoltPaint = new ShowPainters( cp, VP_LEVEL );
        showVoltPaint.add( vp );
        showVoltPaint.setShowPainters( showVoltDesc.isSelected() );
        showVoltDesc.addActionListener( new ShowPainterListener( showVoltDesc, showVoltPaint ) );
        //conPan.add(showVoltDesc);
        //butPan.add(showVoltDesc);
        JCheckBox showInsideBattery = new JCheckBox( "Show Inside Battery", false );
        ShowInsideBattery sib = new ShowInsideBattery( showInsideBattery, bp );
        showInsideBattery.addActionListener( sib );

        butPan.add( showInsideBattery );
        //conPan.add(showInsideBattery);

        double minAcc = -12;
        double accWidth = Math.abs( minAcc * 2 );
        double accDefault = 3;
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits( 2 );
        nf.setMinimumFractionDigits( 2 );
        Image tinyBatteryImage = loader.loadBufferedImage( "images/ron/AA-battery-100.gif" );
        VoltageSlider voltageSlider = new VoltageSlider( new Transform( 0, 100, minAcc, accWidth ), "Voltage", tinyBatteryImage, accDefault, nf, "Volts" );
        voltageSlider.addVoltageListener( battery );
        voltageSlider.addVoltageListener( bdc );
        voltageSlider.addVoltageListener( current );
        voltageSlider.addVoltageListener( scatProp );
        voltageSlider.addVoltageListener( filament );

        voltageSlider.addVoltageListener( fp );
        voltageSlider.addVoltageListener( rs );

        Image coreThumbnail = loader.loadBufferedImage( "images/ron/CoreCountImage.gif" );
        CoreCountSlider is = new CoreCountSlider( 3, maxResistance, 6, "Resistance", coreThumbnail );
        is.addIntListener( resistance );
        is.addIntListener( current );
        is.addIntListener( filament );
        is.addIntListener( battery );
        is.fireChange();

        JPanel mediaControlPanel = new JPanel();
        BufferedImage playImage = loader.loadBufferedImage( "icons/Play16.gif" );
        BufferedImage pauseImage = loader.loadBufferedImage( "icons/Pause16.gif" );
        ImageIcon playIcon = new ImageIcon( playImage );
        ImageIcon pauseIcon = new ImageIcon( pauseImage );
        JButton playButton = new JButton( "Play", playIcon );
        JButton pauseButton = new JButton( "Pause", pauseIcon );
        mediaControlPanel.add( playButton );
        mediaControlPanel.add( pauseButton );
        playButton.setEnabled( false );

        //butPan.add(mediaControlPanel);
        //JLabel voltageLabel=new JLabel("Voltage",new ImageIcon(tinyBatteryImage),JLabel.TRAILING);
        //conPan.add(voltageLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        //gbc.fill=GridBagConstraints.BOTH;
        //gbc.gridwidth=1;
        //gbc.gridheight=3;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gridLayout.setConstraints( butPan, gbc );
        conPan.add( butPan );

        gbc.gridx = 0;
        gbc.gridy = 2;
        gridLayout.setConstraints( voltageSlider, gbc );
        conPan.add( voltageSlider );

        gbc.gridy = 1;
        gridLayout.setConstraints( is, gbc );
        conPan.add( is );

        gbc.gridy = 3;
        gbc.gridx = 0;
        gridLayout.setConstraints( mediaControlPanel, gbc );
        conPan.add( mediaControlPanel );

        //conPan.add(new JPanel());
        //control.setContentPane(conPan);
        //control.pack();
        //control.setVisible(true);

        Stroke circleStroke = new BasicStroke( 11.2f );
        double circleInset = 25;

        DoublePoint upleft = topLeftWirePoint.add( new DoublePoint( -circleInset, -circleInset ) );
        DoublePoint downright = bottomLeftWirePoint.add( new DoublePoint( circleInset, circleInset ) );
        DoublePoint dim = downright.subtract( upleft );
        OvalPainter leftOval = new OvalPainter( Color.blue, circleStroke, (int)upleft.getX(), (int)upleft.getY(), (int)dim.getX(), (int)dim.getY() );
        vp.addPainter( leftOval );

        DoublePoint upleft2 = topRightWirePoint.add( new DoublePoint( -circleInset, -circleInset ) );
        DoublePoint downright2 = bottomRightWirePoint.add( new DoublePoint( circleInset, circleInset ) );
        DoublePoint dim2 = downright2.subtract( upleft2 );
        OvalPainter leftOval2 = new OvalPainter( Color.blue, circleStroke, (int)upleft2.getX(), (int)upleft2.getY(), (int)dim2.getX(), (int)dim2.getY() );
        //o.O.bottomRightWirePoint(leftOval2);
        vp.addPainter( leftOval2 );

        Font font = new Font( null, -1, 19 );
        Color textColor = Color.black;
        int subTextX = 150;
        int subTextY = 170;
        int fontDX = 20;
        TextPainter rightTP = new TextPainter( "3 electrons", subTextX, subTextY, font, textColor );
        TextPainter leftTP = new TextPainter( "-5 electrons", subTextX, subTextY + fontDX, font, textColor );
        TextPainter tot = new TextPainter( "= -2 Volts", subTextX, subTextY + fontDX * 2, font, textColor );
        VoltCount vc = new VoltCount( rightTP, battery, tot, leftTP );
        vc.iterate( 0, null );
        sys.addLaw( vc );

        vp.addPainter( rightTP );
        vp.addPainter( leftTP );
        vp.addPainter( tot );

        Stroke vecStroke = new BasicStroke( 2.3f );
        VectorPainter vec1 = new DefaultVectorPainter( Color.blue, vecStroke );
        DoublePoint sourceRight = ( new DoublePoint( subTextX + 110, subTextY - 5 ) );
        DoublePoint sourceLeft = new DoublePoint( subTextX - 4, subTextY + fontDX - 7 );

        DoublePoint targetvp = new DoublePoint( downright.getX(), ( downright.getY() + upleft.getY() ) / 2 );
        DoublePoint vpdx = targetvp.subtract( sourceLeft );
        VectorPainterAdapter vpa = new VectorPainterAdapter( vec1, (int)sourceLeft.getX(), (int)sourceLeft.getY(), (int)vpdx.getX() + 15, (int)vpdx.getY() );

        DoublePoint targetvp2 = new DoublePoint( upleft2.getX() - 25, ( downright.getY() + upleft.getY() ) / 2 );
        DoublePoint vpdx2 = targetvp2.subtract( sourceRight );//new DoublePoint(subTextX+150,subTextY+fontDX-10));
        VectorPainterAdapter vpa2 = new VectorPainterAdapter( vec1, (int)sourceRight.getX(), (int)sourceRight.getY(), (int)vpdx2.getX() + 15, (int)vpdx2.getY() );

        vp.addPainter( vpa );
        vp.addPainter( vpa2 );


        BufferedImage leftAngel = loader.loadBufferedImage( "images/pushers/PushLeft.gif" );
        leftAngel = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( leftAngel );
        BufferedImage rightAngel = loader.loadBufferedImage( "images/pushers/PushRight.gif" );
        rightAngel = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( rightAngel );

        Point angeldx = new Point( -leftAngel.getWidth() / 2 - 20, -leftAngel.getHeight() / 2 + 4 );
        Point angeldx2 = new Point( -leftAngel.getWidth() / 2 + 18, -leftAngel.getHeight() / 2 + 4 );
        WireRegion angelRegion = new PatchRegion( 20, wp2.getLength() - 20, wp2 );
        AngelPaint angelPaint = new AngelPaint( angelRegion, leftAngel, rightAngel, ws, wp2, angeldx, angeldx2, showInsideBattery );
        cp.addPainter( angelPaint, 2 );

        //BufferedImage turnstileImage = loader.loadBufferedImage("images/ron/turnstile-0-deg.gif");
        ///no region necessary for ron's image.
        BufferedImage turnstileImage = loader.loadBufferedImage( "images/wheels/Pinwheel2.gif" );
        AlphaFixer2 alphaFixerPureWhite = new AlphaFixer2( new int[]{255, 255, 255, 255} );

        turnstileImage = alphaFixerPureWhite.patchAlpha( turnstileImage );
        Point turnstileCenter = new Point( 5, 140 );// turnstileImage.getWidth()/2+5);
        double turnstileSpeedScale = .02;
        Turnstile turnstile = new Turnstile( turnstileCenter, turnstileImage, turnstileSpeedScale );
        cp.addPainter( turnstile, ELECTRON_LEVEL - 1 );
        sys.addLaw( turnstile );
        current.addCurrentListener( turnstile );

        BufferedImage ammeterWireImage = loader.loadBufferedImage( "images/components/cable-srr3.gif" );
        ammeterWireImage = alphaFixer.patchAlpha( ammeterWireImage );
        BufferedImagePainter ammeterWirePainter = new BufferedImagePainter( ammeterWireImage, turnstileCenter.x + turnstileImage.getWidth() / 2 - ammeterWireImage.getWidth() / 2, turnstileCenter.y + turnstileImage.getHeight() / 2 );
        cp.addPainter( ammeterWirePainter, -1 );

        //cp.addPainter(vp,VP_LEVEL);
        //o.O.bottomRightWirePoint(leftOval);

        URL fontLocation = loader.getResource( "fonts/SYLFAEN.TTF" );
        //URL fontLocation=loader.getResource("fonts/PAPYRUS.TTF");

        Font sylfFont = Font.createFont( Font.TRUETYPE_FONT, fontLocation.openStream() );
        sylfFont = font.deriveFont( Font.PLAIN, 44.2f );

        //System.err.println("Battx="+battX+", batty="+battY);
        //
        Point positiveLocation = new Point( battImageX + 30, battImageY + 45 );
        Point negativeLocation = new Point( battImageX + batteryImage.getWidth() - 170, positiveLocation.y );
        VoltageOnBattery voltagePainter = new VoltageOnBattery( positiveLocation, negativeLocation, sylfFont, "Hi there." );
        voltageSlider.addVoltageListener( voltagePainter );
        cp.addPainter( voltagePainter, 100 );

        voltageSlider.addVoltageListener( angelPaint );
        voltageSlider.fireChange();
        SystemRunner sr = new SystemRunner( sys, .2, 20 );

        new MediaHandler( playButton, pauseButton, sr );

        Thread t = new Thread( sr );
//        t.setPriority( Thread.MAX_PRIORITY );
        t.start();

        End e = new End( f, sr );
        f.addWindowListener( e );
        f.getContentPane().setLayout( new BorderLayout() );
        f.setContentPane( mainPanel );
        JPanel jp = new JPanel();
        jp.add( conPan );
        mainPanel.add( jp, BorderLayout.EAST );
        //jp.setSize(50,100);
        conPan.setPreferredSize( new Dimension( 200, 300 ) );
        // jp.setPreferredSize(new Dimension(150,100));
        //getContentPane().add(conPan,BorderLayout.EAST);
        f.setVisible( true );
        System.out.println( "Default painter panel=" + pp );
    }

    public static class End extends WindowAdapter {
        SystemRunner sr;
        Frame f;

        public End( Frame f, SystemRunner sr ) {
            this.f = f;
            this.sr = sr;
        }

        public void windowClosing( WindowEvent e ) {
            f.dispose();
            f.setVisible( false );
            sr.setAlive( false );
            System.exit( 0 );
        }
    }

    public static void main( String[] args ) throws Throwable {
        new Ohm1dModule().mainBAK();
    }

    class MediaHandler {
        JButton playButton;
        JButton pauseButton;
        SystemRunner sr;

        public MediaHandler( JButton playButton, JButton pauseButton, SystemRunner sr ) {
            this.playButton = playButton;
            this.sr = sr;
            this.pauseButton = pauseButton;
            playButton.addActionListener( new PlayListener() );
            pauseButton.addActionListener( new PauseListener() );
        }

        class PlayListener implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                playButton.setEnabled( false );
                pauseButton.setEnabled( true );
                sr.setRunning( true );
            }

        }

        class PauseListener implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                playButton.setEnabled( true );
                pauseButton.setEnabled( false );
                sr.setRunning( false );
            }

        }
    }
}
