package edu.colorado.phet.signalcircuit;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireParticle;
import edu.colorado.phet.signalcircuit.electron.wire1d.WirePatch;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireSegment;
import edu.colorado.phet.signalcircuit.electron.wire1d.WireSystem;
import edu.colorado.phet.signalcircuit.electron.wire1d.paint.WireParticlePainter;
import edu.colorado.phet.signalcircuit.electron.wire1d.paint.WirePatchPainter;
import edu.colorado.phet.signalcircuit.electron.wire1d.propagators.CompositePropagator1d;
import edu.colorado.phet.signalcircuit.electron.wire1d.propagators.ForcePropagator;
import edu.colorado.phet.signalcircuit.electron.wire1d.propagators.WireEndPropagator;
import edu.colorado.phet.signalcircuit.paint.*;
import edu.colorado.phet.signalcircuit.paint.animate.Movie;
import edu.colorado.phet.signalcircuit.paint.animate.PointSource;
import edu.colorado.phet.signalcircuit.paint.animate.laws.AnimateAdder;
import edu.colorado.phet.signalcircuit.paint.animate.laws.AnimateLaw;
import edu.colorado.phet.signalcircuit.paint.animate.twinkles.RotatingTwinkle2;
import edu.colorado.phet.signalcircuit.paint.particle.ImagePainter;
import edu.colorado.phet.signalcircuit.paint.particle.ParticlePainter;
import edu.colorado.phet.signalcircuit.phys2d.DoublePoint;
import edu.colorado.phet.signalcircuit.phys2d.System2D;
import edu.colorado.phet.signalcircuit.phys2d.laws.Repaint;

public class Signal extends JApplet {
    LayeredPanel panel;
    System2D sys;
    JPanel controlPanel;

    public JPanel getControlPanel() {
        return controlPanel;
    }

    public System2D getSystem() {
        return sys;
    }

    public LayeredPanel getPanel() {
        return panel;
    }

    public Signal( int width, int height, IClock clock ) {

        BufferedImage bi = SignalCircuitResources.loadBufferedImage( "electron9.gif" );
        BufferedImage phetBattery = SignalCircuitResources.loadBufferedImage( "PhetBattery1.gif" );

        bi= BufferedImageUtils.multiScaleToHeight( bi,20 );
        ParticlePainter painter = new ImagePainter( bi );
        BufferedImage b2 = SignalCircuitResources.loadBufferedImage( "Electron3VX.GIF" );//PaintedElectron.gif");
//        b2 = new AlphaFixer2( new int[]{252, 254, 252, 255} ).patchAlpha( b2 );
//        b2 = ImageUtils.scaleToSizeApproximate( b2, 20, 20 );
        b2=BufferedImageUtils.multiScaleToHeight( b2,20 );
        ParticlePainter tagged = new ImagePainter( b2 );

        panel = new LayeredPanel();
        panel.addPainter( new FilledRectanglePainter( 0, 0, 800, 800, Color.white ) );
        Stroke stroke = new BasicStroke( (int) ( bi.getWidth() * 1.2 ) );
        //DoublePoint start=new DoublePoint(width/2+50,height/2+50);
        DoublePoint start = new DoublePoint( 100, height / 2 + 50 );
        WirePatch wp = new WirePatch();
        //double dx=300;
        double dy = 150;
        int switchLength = 80;
        int leftInset = 50;

        wp.add( start.getX(), start.getY(), -leftInset, 0 );
        wp.addRelative( 0, -dy );
        wp.addRelative( width, 0 );
        wp.addRelative( 0, dy );
        wp.addRelative( -width + leftInset + switchLength, 0 );
        wp.addRelative( -switchLength, 0 );
        WirePatchPainter wpp = new WirePatchPainter( stroke, Color.gray, wp );
        panel.addPainter( wpp );
        WireSegment seg0 = wp.getLastSegment();

        this.sys = new System2D();
        WireSystem wireSystem = new WireSystem();
        sys.addLaw( wireSystem );

        double k = 300000;
        //	double k=100000;
        //Force1d constantForce=new ConstantForce(-10);
        //CoulombForce coulomb=new CoulombForce(k,2,wireSystem);//,wp);
        WraparoundForce openCoulomb = new WraparoundForce( k, -2.2, wireSystem, wp );
        openCoulomb.setMinDistance( 3 );
        openCoulomb.setMaxDistance( 100 );

        int numElectrons = 52;
        SignalPropagator dp = new SignalPropagator( 100, wp );
// 	CompositePropagator cp=new CompositePropagator();
// 	cp.addPropagator(new Battery(30));
// 	cp.addPropagator(dp);
        RegionSelector rs = new RegionSelector();
        rs.addRegion( 0, wp.getLength(), new Friction( .6 ) );
        //rs.addRegion(300,350,new Friction(.85));
        double dist = wp.totalDistance();
        //rs.addExclusiveRegion(dist-250,dist-150,new Battery(100));
        rs.addRegion( 0, wp.totalDistance(), dp );
        dp.setMaxVelocity( 30 );
        dp.addForce( openCoulomb );
        double dx = dist / ( numElectrons );

        int foreSpeed = 150;//200; //230
        int backSpeed = 150;//200;//350; //230
        int electronSpeed = 10;//15

        CompositePropagator1d open = new CompositePropagator1d();
        double maxElectronSpeed = 90;//50
        ForcePropagator openForce = new ForcePropagator( maxElectronSpeed );

        double closedCoulombK = 1.5 * k;
        double closedCoulombPower = -2;
        WraparoundForce closedCoulomb = new WraparoundForce( closedCoulombK, closedCoulombPower, wireSystem, wp );
        //WraparoundForce closedCoulomb=new WraparoundForce(k*.75,-2.2,wireSystem,wp);
        closedCoulomb.setMinDistance( 5 );
        closedCoulomb.setMaxDistance( 80 );

        openForce.addForce( openCoulomb );
        double battForce = 2500;//
        double battStart = 1100;
        double battEnd = 1200;
        int maxOver = 20;
        BatteryForce batt = new BatteryForce( battForce, battStart, battEnd, wireSystem, maxOver );
        sys.addLaw( batt );
        openForce.addForce( batt );
        double maxClosedSpeed = 100;
        ForcePropagator closedForce = new ForcePropagator( maxClosedSpeed );
        closedForce.addForce( closedCoulomb );

        open.addPropagator( openForce );
        open.addPropagator( new Friction( .6 ) );
        open.addPropagator( new WireEndPropagator( wp ) );

        CompositePropagator1d inside = new CompositePropagator1d();
        inside.addPropagator( new InsidePropagator( electronSpeed, wp ) );
        CompositePropagator1d outside = new CompositePropagator1d();
        ClosedPropagator clop = new ClosedPropagator( wp, inside, outside, backSpeed, foreSpeed );
        SignalPropagator3 sp3 = new SignalPropagator3( open, clop, false );
        //closed.addPropagator(clop);
        inside.addPropagator( closedForce );
        sys.addLaw( clop );
        for ( int i = 0; i < numElectrons; i++ ) {
            WireParticle p = new WireParticle( painter, sp3 );
            p.setPosition( i * dx );
            wireSystem.add( p );
        }

        JSlider angleSlider = new JSlider( 0, 90, 45 );
        angleSlider.setMajorTickSpacing( 90 / 10 );
        angleSlider.setPaintTicks( true );
        angleSlider.setBorder( BorderFactory.createTitledBorder( SignalCircuitStrings.getString( "Signal.SwitchSlider" ) ) );
        SegmentAngle sa = new SegmentAngle( angleSlider, seg0 );
        SwitchCloser sc = new SwitchCloser( Math.PI / 20 );

        sc.addSwitchListener( clop );

        sc.addSwitchListener( sp3 );
        sc.addSwitchListener( openCoulomb );
        sc.addSwitchListener( closedCoulomb );
        sa.addAngleListener( sc );

        sa.stateChanged( null );
        angleSlider.addChangeListener( sa );

        WireParticlePainter particlePainter = new WireParticlePainter( wireSystem, wp );
        JCheckBox showElectrons = new JCheckBox( SignalCircuitStrings.getString( "Signal.ShowElectronsCheckBox" ), true );
        ShowElectrons se = new ShowElectrons( showElectrons, particlePainter );
        panel.addPainter( se, 1 );

        sys.addLaw( new Repaint( panel ) );

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                sys.iterate( clockEvent.getSimulationTimeChange() );
            }
        } );

        Signal s = this;
        Point covered = new Point( 105, 110 );
        Point uncovered = new Point( 105, 220 );

        BufferedImage up = SignalCircuitResources.loadBufferedImage( "A.jpg" );
        BufferedImage down = SignalCircuitResources.loadBufferedImage( "B.jpg" );
        Switch top = new Switch( new BufferedImagePainter( down, covered.x, covered.y ),
                                 new BufferedImagePainter( up, covered.x, covered.y ),
                                 false, angleSlider );
        Switch bottom = new Switch( new BufferedImagePainter( down, uncovered.x, uncovered.y ),
                                    new BufferedImagePainter( up, uncovered.x, uncovered.y ),
                                    false, angleSlider );
        JCheckBox switchBox = new JCheckBox( SignalCircuitStrings.getString( "Signal.ShowInsideSwitchCheckBox" ), false );
        SwitchCover switchCover = new SwitchCover( top, bottom, true, switchBox, s.getPanel() );

        sc.addSwitchListener( switchCover );

        BufferedImage chandOn = SignalCircuitResources.loadBufferedImage( "ChandelierOn2.jpg" );
        BufferedImage chandOff = SignalCircuitResources.loadBufferedImage( "ChandelierOff2.jpg" );

        int chandX = 250;
        int chandY = 62;
        Painter on = new BufferedImagePainter( chandOn, chandX, chandY );
        Painter off = new BufferedImagePainter( chandOff, chandX, chandY );
        double foreThreshold = 510;
        Chandelier c = new Chandelier( on, off, false, foreThreshold );
        panel.addPainter( c, 2 );
        clop.addSignalListener( c );

        Arrow a = new Arrow( wp );
        clop.addSignalListener( a );
        sc.addSwitchListener( a );

        JPanel showPanel = new JPanel();
        showPanel.setBorder( BorderFactory.createTitledBorder( SignalCircuitStrings.getString( "Signal.ComparisonToolsBorder" ) ) );
        showPanel.setLayout( new BoxLayout( showPanel, BoxLayout.Y_AXIS ) );

        JCheckBox arrowBox = new JCheckBox( SignalCircuitStrings.getString( "Signal.ShowSignalArrowCheckBox" ) );
        JCheckBox electronBox = new JCheckBox( SignalCircuitStrings.getString( "Signal.PaintElectronCheckBox" ) );
        showPanel.add( arrowBox );
        showPanel.add( electronBox );

        SelectablePainter sp = new SelectablePainter( painter, tagged, false, electronBox );
        electronBox.addActionListener( sp );

        ElectronPainter ep = new ElectronPainter( electronBox, wireSystem, painter, sp, panel, wp );
        SelectableArrow selectableArrow = new SelectableArrow( arrowBox, a, panel );
        arrowBox.addActionListener( selectableArrow );
        panel.addPainter( selectableArrow, 11 );

        sc.addSwitchListener( clop );
        sc.addSwitchListener( ep );
        panel.addPainter( ep, 10 );

        s.getPanel().addMouseListener( switchCover );
        switchBox.addActionListener( switchCover );
        s.getPanel().addPainter( switchCover, 9 );
        s.getPanel().addPainter( new BufferedImagePainter( phetBattery, 400, 175 ), 10 );

        this.controlPanel = new JPanel();
        controlPanel.add( showElectrons );
        controlPanel.add( switchBox );
        controlPanel.add( showPanel );
        controlPanel.add( angleSlider, BorderLayout.SOUTH );

        Movie m = new Movie();
        int layer = 10;
        Stroke twinkleStroke = new BasicStroke( .8f );//,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND );
        RotatingTwinkle2 rt = new RotatingTwinkle2( switchCover, twinkleStroke, 20, 3, 8, 15 );
        m.addAnimation( rt );
        double dt=0.02;
        AnimateLaw al = new AnimateLaw( dt * 2.5, m, s.getPanel(), layer );
        new Thread( new AnimateAdder( 2000, al, s.getSystem(), s.getPanel(), layer ) ).start();
        al = new AnimateLaw( dt * 2.5, m, s.getPanel(), layer );
        new Thread( new AnimateAdder( 8000, al, s.getSystem(), s.getPanel(), layer ) ).start();
    }

}
