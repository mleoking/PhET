package edu.colorado.phet.batteryvoltage.man;

import java.util.Hashtable;
import java.util.Vector;

import edu.colorado.phet.batteryvoltage.Battery;
import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.motions.*;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;
import edu.colorado.phet.batteryvoltage.common.phys2d.Propagator;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;
import edu.colorado.phet.batteryvoltage.man.dir.CarryToDir;
import edu.colorado.phet.batteryvoltage.man.dir.PropagatorDir;
import edu.colorado.phet.batteryvoltage.man.dir.SpeedDir;

public class VoltManFactory {
    int barrierX;
    int barrierWidth;
    double goToElectronSpeed;

    Propagator leftPropagator;
    Propagator rightPropagator;

    double barrierInset;
    Hashtable carrierMap;
    double returnHomeSpeed;
    Vector carried;
    double homeThreshold;
    double minCarrySpeed;
    double maxCarrySpeed;
    Vector targeted;
    Battery b;

    public VoltManFactory(
            int barrierX,
            int barrierWidth,
            double goToElectronSpeed,

            Propagator leftPropagator,
            Propagator rightPropagator,

            double barrierInset,
            Hashtable carrierMap,
            double returnHomeSpeed,
            Vector carried,
            double homeThreshold,
            double minCarrySpeed,
            double maxCarrySpeed,
            Vector targeted,
            Battery b ) {
        this.b = b;
        this.targeted = targeted;
        this.minCarrySpeed = minCarrySpeed;
        this.maxCarrySpeed = maxCarrySpeed;
        this.homeThreshold = homeThreshold;
        this.carried = carried;
        this.barrierX = barrierX;
        this.barrierWidth = barrierWidth;
        this.goToElectronSpeed = goToElectronSpeed;
        this.leftPropagator = leftPropagator;
        this.rightPropagator = rightPropagator;
        this.barrierInset = barrierInset;
        this.carrierMap = carrierMap;
        this.returnHomeSpeed = returnHomeSpeed;
    }

    public VoltMan newMan( Man m, DoublePoint home ) {
        StandStill stand = new StandStill();
        CarryPropagator carryPropagator = new CarryPropagator( m );
        ReadyToDrop readyToDrop = new ReadyToDrop( m, barrierX - barrierInset, false );

        CompositeMotion moveLegs = new CompositeMotion();
        moveLegs.add( new LegSwings( Math.PI / 20 ) );
        moveLegs.add( new LegSwings2( Math.PI / 20 ) );

        CompositeMotion runToElectron = new CompositeMotion();
        TranslateToLocation toLoc = new TranslateToLocation( null, goToElectronSpeed );
        runToElectron.add( toLoc );
        runToElectron.add( moveLegs );

        DefaultAction goToElectron = new DefaultAction( runToElectron );

        CompositeMotion walkToHome = new CompositeMotion();
        walkToHome.add( moveLegs );
        walkToHome.add( new TranslateTo( home.getX(), home.getY(), returnHomeSpeed ) );

        DefaultAction goHomeAndStayThere = new DefaultAction( walkToHome );
        DefaultAction go = new DefaultAction( runToElectron );

        DefaultAction stayAtHome = new DefaultAction( stand );

        Condition closeToHome = new ManIsClose( m, new FixedLocation( new DoublePoint( home.getX(), home.getY() ) ), homeThreshold );

        goHomeAndStayThere.addClause( closeToHome, stayAtHome );
        Get get = new Get( runToElectron, toLoc );
        Condition closeToTarget = new ManIsClose( m, get, homeThreshold );
        Grab grab = new Grab( m, carried, carryPropagator, carrierMap, get, targeted );
        get.addClause( closeToTarget, grab );

        Translate run = new Translate( maxCarrySpeed, 0 );
        CompositeMotion runMotion = new CompositeMotion();
        runMotion.add( run );
        //runMotion.add(moveLegs);
        runMotion.add( moveLegs );

        DefaultAction runRightWithElectron = new DefaultAction( runMotion );
        Release drop = new Release( carried, rightPropagator, carrierMap, get, targeted, b );
        ReadyToDrop closeToDropSite = new ReadyToDrop( m, barrierX + barrierWidth + barrierInset, true );
        runRightWithElectron.addClause( closeToDropSite, drop );

        grab.setCarry( runRightWithElectron );

        VoltMan vm = new VoltMan( goHomeAndStayThere, get );
        drop.setVoltMan( vm );
        vm.goHomeAndStayThere();
        vm.addDirectional( drop );
        vm.addDirectional( new PropagatorDir( drop, leftPropagator, rightPropagator ) );
        vm.addDirectional( new CarryToDir( barrierX - barrierInset, barrierX + barrierWidth + barrierInset, closeToDropSite ) );
        SpeedDir sd = new SpeedDir( b, run, true, minCarrySpeed, maxCarrySpeed );
        b.addParticleMoveListener( sd );
        vm.addDirectional( sd );
        return vm;
    }
}
