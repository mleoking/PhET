// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.*;

import edu.colorado.phet.batteryresistorcircuit.collisions.Collider;
import edu.colorado.phet.batteryresistorcircuit.collisions.DefaultCollisionEvent;
import edu.colorado.phet.batteryresistorcircuit.common.math.functions.Transform;
import edu.colorado.phet.batteryresistorcircuit.common.paint.*;
import edu.colorado.phet.batteryresistorcircuit.common.paint.Painter;
import edu.colorado.phet.batteryresistorcircuit.common.paint.gauges.GaugeScaling;
import edu.colorado.phet.batteryresistorcircuit.common.paint.gauges.IGauge;
import edu.colorado.phet.batteryresistorcircuit.common.paint.gauges.ImageGauge;
import edu.colorado.phet.batteryresistorcircuit.common.paint.gauges.Scaling;
import edu.colorado.phet.batteryresistorcircuit.common.paint.particle.ParticlePainter;
import edu.colorado.phet.batteryresistorcircuit.common.paint.vector.DefaultVectorPainter;
import edu.colorado.phet.batteryresistorcircuit.common.paint.vector.VectorPainter;
import edu.colorado.phet.batteryresistorcircuit.common.paint.vector.VectorPainterAdapter;
import edu.colorado.phet.batteryresistorcircuit.common.phys2d.*;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.Circuit;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WirePatch;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WireSystem;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.forces.*;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.paint.WireParticlePainter;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.paint.WirePatchPainter;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.propagators.CompositePropagator1d;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.propagators.DualJunction;
import edu.colorado.phet.batteryresistorcircuit.gui.CoreCountSlider;
import edu.colorado.phet.batteryresistorcircuit.gui.ShowPainterListener;
import edu.colorado.phet.batteryresistorcircuit.gui.ShowPainters;
import edu.colorado.phet.batteryresistorcircuit.gui.VoltageSlider;
import edu.colorado.phet.batteryresistorcircuit.oscillator2d.DefaultOscillateFactory;
import edu.colorado.phet.batteryresistorcircuit.oscillator2d.OscillateFactory;
import edu.colorado.phet.batteryresistorcircuit.regions.AndRegion;
import edu.colorado.phet.batteryresistorcircuit.regions.PatchRegion;
import edu.colorado.phet.batteryresistorcircuit.regions.SimplePatch;
import edu.colorado.phet.batteryresistorcircuit.volt.*;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

public class BatteryResistorCircuitSimulationPanel extends JPanel {
    public static int BASE_FRAME_WIDTH = 1028;
    private IClock clock;

    public BatteryResistorCircuitSimulationPanel( IClock clock ) {
        this.clock = clock;
    }

    public void startApplication() throws IOException, FontFormatException {
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

        WirePatch wp2 = new WirePatch();
        wp2.start( bottomRightInset, bottomLeftInset );

        Circuit cir = new Circuit();
        cir.addWirePatch( wp );
        cir.addWirePatch( wp2 );

        LayeredPainter cp = new LayeredPainter();
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
        Spectrum cm = new Spectrum( BatteryResistorCircuitResources.loadBufferedImage( "spectra/spect3.jpg" ), 100 );
        Filament filament = new Filament( goldStroke, scatterPatch, vMax * .6, 1, cm );

        BufferedImage xar = BatteryResistorCircuitResources.loadBufferedImage( "icons/Play24.GIF" );
//        AlphaFixer2 alphaFixerForIcons = new AlphaFixer2( new int[]{192, 192, 192, 255} );
//        xar = alphaFixerForIcons.patchAlpha( xar );

        BlackbodyScale bs = new BlackbodyScale( BatteryResistorCircuitResources.loadBufferedImage( "spectra/fullSpectrum.jpg" ), 350, 10 + yOffsetForSpct,
                                                xar );
        cm.addPowerListener( bs );
        cp.addPainter( bs );
        float xhot = 660;
        float yhot = 30 + yOffsetForSpct;
        float xcold = 360;
        float ycold = yhot;
        Font heatFont = new PhetFont( 19 );
        cp.addPainter( new TextPainter( BatteryResistorCircuitStrings.get( "Ohm1dModule.Hot" ), xhot, yhot, heatFont, Color.black ) );
        cp.addPainter( new TextPainter( BatteryResistorCircuitStrings.get( "Ohm1dModule.Cold" ), xcold, ycold, heatFont, Color.white ) );
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
        ShowPainters showCores = new ShowPainters( cp, new int[] { CORE_LEVEL, CORE_LEVEL_BOTTOM } );

        BufferedImage greeny = BatteryResistorCircuitResources.loadBufferedImage( "ron/particle-green-med.png" );
//        greeny = new MakeMETransp().patchAlpha( greeny );
        ParticlePainter dp = new edu.colorado.phet.batteryresistorcircuit.common.paint.particle.ImagePainter( greeny );
        Resistance resistance = new Resistance( CORE_START, CORE_END, numCores, wp, amplitude, freq, decay, dp, CORE_LEVEL, CORE_LEVEL_BOTTOM, cp, showCores, sys );
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
        SmoothBatt battery = ( new SmoothBatt( leftSideBatt, rightSideBatt, ws, batterySpeed, 18 ) );
        range.addPropagator( batt, battery );
        range.addPropagator( batt, new ResetElectron() );
        cpr.addPropagator( range );
        cpr.addPropagator( new Crash() );
        prop.addPropagator( cpr );
        BatteryForcePropagator fp = new BatteryForcePropagator( 0, 10 * vmax );
        fp.addForce( cf );
        /*Add a coulomb force from the end of wp2 onto the beginning of wp.*/
        fp.addForce( new AdjacentPatchCoulombForce( cfp, ws, wp2, wp ) );
        fp.addForce( new AdjacentPatchCoulombForce2( cfp, ws, wp2, wp ) );
        fp.addForce( new Friction1d( .9999999 ) );

        ResetScatterability rs = new ResetScatterability( ws );
        AndRegion nonCoulombRegion = new AndRegion();
        nonCoulombRegion.addRegion( batt );
        nonCoulombRegion.addRegion( scatteringRegionNoCoulomb );  //comment out this line to put coulomb interactions into the scattering region

        double accelScale = 1.4;
        Accel scatProp = new Accel( 2, vmax * 15, accelScale );
        range.addPropagator( accelRegion, scatProp );
        range.addInverse( nonCoulombRegion, fp );
        prop.addPropagator( new DualJunction( wp, wp2 ) );
        prop.addPropagator( new DualJunction( wp2, wp ) );

        BufferedImage ronImage = BatteryResistorCircuitResources.loadBufferedImage( "ron/particle-blue-sml.gif" );
        //ronImage=new AlphaFixer2(new int[]{252,254,252,255}).patchAlpha(ronImage);
        ParticlePainter painter = new edu.colorado.phet.batteryresistorcircuit.common.paint.particle.ImagePainter( ronImage );

        BufferedImage batteryImage = BatteryResistorCircuitResources.loadBufferedImage( "ron/AA-battery-555.png" );
        batteryImage = BufferedImageUtils.flipX( batteryImage );
        int battImageX = (int) bottomLeftWirePoint.getX() + 59;
        int battImageY = (int) bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3;
        BufferedImagePainter battPainter = new BufferedImagePainter( batteryImage, battImageX, battImageY );
        int BATT_LAYER = 10;

        //Need an image changer component
        BufferedImage batteryImageLeft = BufferedImageUtils.flipX( batteryImage );
        BufferedImagePainter battPainter2 = new BufferedImagePainter( batteryImageLeft, (int) bottomLeftWirePoint.getX() + 59, (int) bottomLeftWirePoint.getY() - batteryImageLeft.getHeight() / 2 + 3 );

        BufferedImagePainter transLeft = new BufferedImagePainter( BufferedImageUtils.scaleAlpha( batteryImage, 0.5 ), (int) bottomLeftWirePoint.getX() + 59, (int) bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3 );
        BufferedImagePainter transRight = new BufferedImagePainter( BufferedImageUtils.scaleAlpha( batteryImageLeft, 0.5 ), (int) bottomLeftWirePoint.getX() + 59, (int) bottomLeftWirePoint.getY() - batteryImage.getHeight() / 2 + 3 );

        BatteryPainter bp = new BatteryPainter( battPainter, battPainter2, transLeft, transRight );
        BatteryDirectionChanger bdc = new BatteryDirectionChanger( bp );//battPainter,battPainter2,cp,BATT_LAYER);

        cp.addPainter( bp, BATT_LAYER );
        //Add bottomLeftWirePoint gauge to monitor the current.
        int gaugeX = 25;
        int gaugeY = 390;
        int numParticles = 50;
        double maxCurrent = vmax * 4;//numParticles*1.2;
        //BufferedImage gaugeImage = Ohm1DResources.loadBufferedImage("components/gauges/vdo_samp_srr.JPG");
//        AlphaFixer2 alphaFixer = new AlphaFixer2( new int[]{252, 254, 252, 255} );
        BufferedImage gaugeImage = BatteryResistorCircuitResources.loadBufferedImage( "components/gauges/vdo_samp_srr_edit3.gif" );
//        gaugeImage = alphaFixer.patchAlpha( gaugeImage );

        int needleX = gaugeImage.getWidth() / 2;
        int needleY = gaugeImage.getHeight() / 2 + 38;
        int needleLength = gaugeImage.getWidth() / 2 - 30;
        IGauge gauge = new ImageGauge( gaugeImage, gaugeX, gaugeY, needleX, needleY, needleLength );
        cp.addPainter( gauge );

        //WireRegion currentRegion=new PatchRegion(CORE_START+10,CORE_END-10,wp);
        AndRegion currentRegion = new AndRegion();
        currentRegion.addRegion( new SimplePatch( wp ) );
        currentRegion.addRegion( new SimplePatch( wp2 ) );
        AverageCurrent current = new AverageCurrent( gauge, 100, currentRegion );

        GaugeScaling gus = new GaugeScaling();
        gus.add( new Scaling( gauge, BatteryResistorCircuitStrings.get( "Ohm1dModule.Low" ), -maxCurrent / 4, maxCurrent / 4 ), false );
        Scaling medium = new Scaling( gauge, BatteryResistorCircuitStrings.get( "Ohm1dModule.Medium" ), -maxCurrent / 2, maxCurrent / 2 );
        gus.add( medium, true );
        gus.add( new Scaling( gauge, BatteryResistorCircuitStrings.get( "Ohm1dModule.High" ), -maxCurrent, maxCurrent ), false );
        gus.setBorder( BorderFactory.createTitledBorder( BatteryResistorCircuitStrings.get( "Ohm1dModule.AmmeterScaleBorder" ) ) );
        medium.actionPerformed( null );
        int ELECTRON_LEVEL = 3;
        ShowPainters showElectrons = new ShowPainters( cp, new int[] { ELECTRON_LEVEL, ELECTRON_LEVEL - 1 } );

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
        Collider coll = new Collider( ws, ce, wp );

        int dx = (int) ( cir.getLength() / numParticles );
        int mod = 0;
        for ( int i = 0; i < numParticles; i++ ) {
            Electron particle1 = new Electron( prop, wp, ce );
            double position = dx * i;
            boolean makeParticle = true;
            if ( position > CORE_START && position < CORE_END && mod++ % 2 == 0 ) {
                makeParticle = false;
            }
            if ( makeParticle ) {
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

        pp.setBackground( new Color( 235, 230, 240 ) );
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout( new BorderLayout() );
        mainPanel.add( pp, BorderLayout.CENTER );

        JFrame control = new JFrame( BatteryResistorCircuitStrings.get( "Ohm1dModule.ControlsTitle" ) );
        control.setLocation( BASE_FRAME_WIDTH, 0 );
        JPanel conPan = new JPanel();
        conPan.setBorder( BorderFactory.createTitledBorder( BatteryResistorCircuitStrings.get( "Ohm1dModule.ControlPanelBorder" ) ) );
        GridBagLayout gridLayout = new GridBagLayout();
        conPan.setLayout( gridLayout );

        JPanel butPan = new JPanel();
        butPan.setLayout( new BoxLayout( butPan, BoxLayout.Y_AXIS ) );

        JCheckBox showCoreBox = new JCheckBox( BatteryResistorCircuitStrings.get( "Ohm1dModule.ShowCoresCheckbox" ), true );
        showCoreBox.addActionListener( new ShowPainterListener( showCoreBox, showCores ) );
        butPan.add( showCoreBox );

        JCheckBox showElectronBox = new JCheckBox( BatteryResistorCircuitStrings.get( "Ohm1dModule.ShowElectronsCheckbox" ), true );
        showElectronBox.addActionListener( new ShowPainterListener( showElectronBox, showElectrons ) );

        JCheckBox showVoltDesc = new JCheckBox( BatteryResistorCircuitStrings.get( "Ohm1dModule.ShowVoltageCalcCheckbox" ), false );
        int VP_LEVEL = 100;
        CompositePainter vp = new CompositePainter();
        ShowPainters showVoltPaint = new ShowPainters( cp, VP_LEVEL );
        showVoltPaint.add( vp );
        showVoltPaint.setShowPainters( showVoltDesc.isSelected() );
        showVoltDesc.addActionListener( new ShowPainterListener( showVoltDesc, showVoltPaint ) );
        //conPan.add(showVoltDesc);
        butPan.add( showVoltDesc );
        JCheckBox showInsideBattery = new JCheckBox( BatteryResistorCircuitStrings.get( "Ohm1dModule.ShowInsideBatteryCheckbox" ), false );
        ShowInsideBattery sib = new ShowInsideBattery( showInsideBattery, bp );
        showInsideBattery.addActionListener( sib );

        butPan.add( showInsideBattery );

        double minAcc = -12;
        double accWidth = Math.abs( minAcc * 2 );
        double accDefault = 3;
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits( 2 );
        nf.setMinimumFractionDigits( 2 );
        Image tinyBatteryImage = BatteryResistorCircuitResources.loadBufferedImage( "ron/AA-battery-100.gif" );
        VoltageSlider voltageSlider = new VoltageSlider( new Transform( 0, 100, minAcc, accWidth ),
                                                         BatteryResistorCircuitStrings.get( "Ohm1dModule.VoltageSliderName" ), tinyBatteryImage, accDefault,
                                                         nf, BatteryResistorCircuitStrings.get( "Ohm1dModule.VoltageSliderUnits" ) );
        voltageSlider.addVoltageListener( battery );
        voltageSlider.addVoltageListener( bdc );
        voltageSlider.addVoltageListener( current );
        voltageSlider.addVoltageListener( scatProp );
        voltageSlider.addVoltageListener( filament );

        voltageSlider.addVoltageListener( fp );
        voltageSlider.addVoltageListener( rs );

        Image coreThumbnail = BatteryResistorCircuitResources.loadBufferedImage( "ron/CoreCountImage.gif" );
        CoreCountSlider is = new CoreCountSlider( 3, maxResistance, 6, BatteryResistorCircuitStrings.get( "Ohm1dModule.CoreCountSliderName" ),
                                                  coreThumbnail, BatteryResistorCircuitStrings.get( "Ohm1dModule.CoreCountSliderUnits" ) );
        is.addIntListener( resistance );
        is.addIntListener( current );
        is.addIntListener( filament );
        is.addIntListener( battery );
        is.fireChange();

        JPanel mediaControlPanel = new JPanel();
        BufferedImage playImage = BatteryResistorCircuitResources.loadBufferedImage( "icons/Play16.gif" );
        BufferedImage pauseImage = BatteryResistorCircuitResources.loadBufferedImage( "icons/Pause16.gif" );
        ImageIcon playIcon = new ImageIcon( playImage );
        ImageIcon pauseIcon = new ImageIcon( pauseImage );
        JButton playButton = new JButton( BatteryResistorCircuitStrings.get( "Ohm1dModule.PlayButton" ), playIcon );
        JButton pauseButton = new JButton( BatteryResistorCircuitStrings.get( "Ohm1dModule.PauseButton" ), pauseIcon );
        mediaControlPanel.add( playButton );
        mediaControlPanel.add( pauseButton );
        playButton.setEnabled( false );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
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

        Stroke circleStroke = new BasicStroke( 11.2f );
        double circleInset = 25;

        DoublePoint upleft = topLeftWirePoint.add( new DoublePoint( -circleInset, -circleInset ) );
        DoublePoint downright = bottomLeftWirePoint.add( new DoublePoint( circleInset, circleInset ) );
        DoublePoint dim = downright.subtract( upleft );
        OvalPainter leftOval = new OvalPainter( Color.blue, circleStroke, (int) upleft.getX(), (int) upleft.getY(), (int) dim.getX(), (int) dim.getY() );
        vp.addPainter( leftOval );

        DoublePoint upleft2 = topRightWirePoint.add( new DoublePoint( -circleInset, -circleInset ) );
        DoublePoint downright2 = bottomRightWirePoint.add( new DoublePoint( circleInset, circleInset ) );
        DoublePoint dim2 = downright2.subtract( upleft2 );
        OvalPainter leftOval2 = new OvalPainter( Color.blue, circleStroke, (int) upleft2.getX(), (int) upleft2.getY(), (int) dim2.getX(), (int) dim2.getY() );
        //o.O.bottomRightWirePoint(leftOval2);
        vp.addPainter( leftOval2 );

        Font font = new PhetFont( 19 );
        Color textColor = Color.black;
        int subTextX = 150;
        int subTextY = 170;
        int fontDX = 20;
        TextPainter rightTP = new TextPainter( "3 " + BatteryResistorCircuitStrings.get( "VoltCount.Electrons" ), subTextX, subTextY, font, textColor );
        TextPainter leftTP = new TextPainter( "-5 " + BatteryResistorCircuitStrings.get( "VoltCount.Electrons" ), subTextX, subTextY + fontDX, font, textColor );
        TextPainter tot = new TextPainter( "= -2 " + BatteryResistorCircuitStrings.get( "VoltCount.Volts" ), subTextX, subTextY + fontDX * 2, font, textColor );
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
        VectorPainterAdapter vpa = new VectorPainterAdapter( vec1, (int) sourceLeft.getX(), (int) sourceLeft.getY(), (int) vpdx.getX() + 15, (int) vpdx.getY() );

        DoublePoint targetvp2 = new DoublePoint( upleft2.getX() - 25, ( downright.getY() + upleft.getY() ) / 2 );
        DoublePoint vpdx2 = targetvp2.subtract( sourceRight );//new DoublePoint(subTextX+150,subTextY+fontDX-10));
        VectorPainterAdapter vpa2 = new VectorPainterAdapter( vec1, (int) sourceRight.getX(), (int) sourceRight.getY(), (int) vpdx2.getX() + 15, (int) vpdx2.getY() );

        vp.addPainter( vpa );
        vp.addPainter( vpa2 );


        BufferedImage leftAngel = BatteryResistorCircuitResources.loadBufferedImage( "pushers/PushLeft.gif" );
        BufferedImage rightAngel = BatteryResistorCircuitResources.loadBufferedImage( "pushers/PushRight.gif" );

        Point angeldx = new Point( -leftAngel.getWidth() / 2 - 20, -leftAngel.getHeight() / 2 + 4 );
        Point angeldx2 = new Point( -leftAngel.getWidth() / 2 + 18, -leftAngel.getHeight() / 2 + 4 );
        WireRegion angelRegion = new PatchRegion( 20, wp2.getLength() - 20, wp2 );
        AngelPaint angelPaint = new AngelPaint( angelRegion, leftAngel, rightAngel, ws, wp2, angeldx, angeldx2, showInsideBattery );
        cp.addPainter( angelPaint, 2 );

        BufferedImage turnstileImage = BatteryResistorCircuitResources.loadBufferedImage( "wheels/Pinwheel2.gif" );
        Point turnstileCenter = new Point( 5, 140 );// turnstileImage.getWidth()/2+5);
        double turnstileSpeedScale = .02;
        Turnstile turnstile = new Turnstile( turnstileCenter, turnstileImage, turnstileSpeedScale );
        cp.addPainter( turnstile, ELECTRON_LEVEL - 1 );
        sys.addLaw( turnstile );
        current.addCurrentListener( turnstile );

        BufferedImage ammeterWireImage = BatteryResistorCircuitResources.loadBufferedImage( "components/cable-srr3.gif" );
//        ammeterWireImage = alphaFixer.patchAlpha( ammeterWireImage );
        BufferedImagePainter ammeterWirePainter = new BufferedImagePainter( ammeterWireImage, turnstileCenter.x + turnstileImage.getWidth() / 2 - ammeterWireImage.getWidth() / 2, turnstileCenter.y + turnstileImage.getHeight() / 2 );
        cp.addPainter( ammeterWirePainter, -1 );

        Font sylfFont = font.deriveFont( Font.PLAIN, 44.2f );

        Point positiveLocation = new Point( battImageX + 30, battImageY + 45 );
        Point negativeLocation = new Point( battImageX + batteryImage.getWidth() - 170, positiveLocation.y );
        VoltageOnBattery voltagePainter = new VoltageOnBattery( positiveLocation, negativeLocation, sylfFont,
                                                                BatteryResistorCircuitStrings.get( "VoltageOnBattery.DefaultText" ) );
        voltageSlider.addVoltageListener( voltagePainter );
        cp.addPainter( voltagePainter, 100 );

        voltageSlider.addVoltageListener( angelPaint );
        voltageSlider.fireChange();
        final SystemRunner sr = new SystemRunner( sys, .2 );

        new MediaHandler( playButton, pauseButton, sr );

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                sr.step();
            }
        } );

        JPanel jp = new JPanel();
        jp.add( conPan );
        mainPanel.add( jp, BorderLayout.EAST );
        conPan.setPreferredSize( new Dimension( 200, conPan.getPreferredSize().height + 20 ) ); //#2914

        setLayout( new BorderLayout() );
        add( mainPanel, BorderLayout.CENTER );
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
                clock.start();
            }

        }

        class PauseListener implements ActionListener {
            public void actionPerformed( ActionEvent e ) {
                playButton.setEnabled( true );
                pauseButton.setEnabled( false );
                clock.pause();
            }

        }
    }

}
