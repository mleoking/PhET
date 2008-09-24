package edu.colorado.phet.semiconductor.macro.energy;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.semiconductor.SemiconductorApplication;
import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;
import edu.colorado.phet.semiconductor.macro.BucketSection;
import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.circuit.CircuitSection;
import edu.colorado.phet.semiconductor.macro.circuit.ConductionListener;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatteryListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantChangeListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.*;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.DefaultCriteria;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ExciteForConduction;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.ModelCriteria;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.phetcommon.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 8:26:00 PM
 */
public class EnergySection implements ModelElement, Graphic, DopantChangeListener, BatteryListener {
    ArrayList conductionListeners = new ArrayList();

    ArrayList bandSets = new ArrayList();
    ArrayList bandSetGraphics = new ArrayList();

    ArrayList particles = new ArrayList();
    ArrayList particleGraphics = new ArrayList();

    ArrayList plusses = new ArrayList();
    ArrayList plusGraphics = new ArrayList();

    ArrayList electricFields = new ArrayList();
    ArrayList electricFieldGraphics = new ArrayList();

    private EnergyTextGraphic energyTextGraphic;
    private ModelViewTransform2D transform;
    private Battery battery;
    private BufferedImage particleImage;
    private BufferedImage plusImage;

    ChoiceStateModel stateModel = new ChoiceStateModel( this );
    //    final StateModel stateModel = new CompleteStateModel( this );
    private Speed speed;
    private Rectangle2D.Double bounds;
    private BucketSection bucketSection;
    InternalBiasManager biasManager = new InternalBiasManager();
//    StateModelSet stateModelSet;

    public EnergySection( ModelViewTransform2D transform, double particleWidth, Battery battery, Speed speed, Rectangle2D.Double bounds ) throws IOException {
        this.transform = transform;
        this.battery = battery;
        this.speed = speed;
        this.bounds = bounds;
        this.plusImage = SemiconductorApplication.imageLoader.loadImage( "semiconductor/images/particle-red-plus.gif" );
        particleImage = MacroCircuitGraphic.getParticleImage();
        setupTwoRegions();
        PhetVector textLocation = new PhetVector( .65, 1 );
        energyTextGraphic = new EnergyTextGraphic( transform, textLocation );
        bucketSection = new BucketSection( transform, this, particleImage );
//        stateModelSet = new StateModelSet( this );
        //TODO
        //stateModel = stateModelSet.getNullModel();
        generateStateDiagrams();
    }

    public int numParticles() {
        return particles.size();
    }

    public BandParticle particleAt( int i ) {
        return (BandParticle) particles.get( i );
    }

    void clearDoping() {
        plusses.clear();
        plusGraphics.clear();
        particles.clear();
        particleGraphics.clear();
    }

    void clearRegions() {
        bandSets.clear();
        bandSetGraphics.clear();
        clearDoping();
        electricFields.clear();
        electricFieldGraphics.clear();
    }

    double getX() {
        return bounds.getX();
    }

    double getY() {
        return bounds.getY();
    }

    double getWidth() {
        return bounds.getWidth();
    }

    double getHeight() {
        return bounds.getHeight();
    }

    void setupOneRegion() {
        double bandWidth = getWidth() / 2;
        double insetX = getWidth() / 2 - bandWidth / 2;
        newBandSet( new Rectangle2D.Double( getX() + insetX, getY(), bandWidth, getHeight() ), 0 );
        voltageChanged( getBattery() );
        generateStateDiagrams();
    }

    void setupTwoRegions() {
        double bandWidth = getWidth() / 3;
        double dx = bandWidth / 4;
        double insetX1 = getWidth() / 3 - bandWidth / 2;
        double insetX2 = 2 * getWidth() / 3 - bandWidth / 2;
        Rectangle2D.Double bandrect = new Rectangle2D.Double( getX() + insetX1 - dx / 2, getY(), bandWidth, getHeight() );
        Rectangle2D.Double bandrect2 = new Rectangle2D.Double( getX() + insetX2 + dx / 2, getY(), bandWidth, getHeight() );
        newBandSet( bandrect, 0 );
        newBandSet( bandrect2, 2 );

        addEField( bandrect, bandrect2 );
        voltageChanged( getBattery() );
        generateStateDiagrams();
    }

    private void setupThreeRegions() {
        double bandWidth = getWidth() / 4;
        double dx = bandWidth / 5;
        double insetX1 = getWidth() / 4 - bandWidth / 2;
        double insetX2 = 2 * getWidth() / 4 - bandWidth / 2;
        double insetX3 = 3 * getWidth() / 4 - bandWidth / 2;
        Rectangle2D.Double a = new Rectangle2D.Double( getX() + insetX1 - dx / 2, getY(), bandWidth, getHeight() );
        Rectangle2D.Double b = new Rectangle2D.Double( getX() + insetX2, getY(), bandWidth, getHeight() );
        Rectangle2D.Double c = new Rectangle2D.Double( getX() + insetX3 + dx / 2, getY(), bandWidth, getHeight() );
        newBandSet( a, 0 );
        newBandSet( b, true, 2 );
        newBandSet( c, 4 );
        addEField( a, b );
        addEField( b, c );

        voltageChanged( getBattery() );
        generateStateDiagrams();
    }

//    private void generateStateDiagrams() {
//        stateModel.clear();
//        double minVolts = .1;
//        double maxVolts = 10;
//
//        DefaultStateDiagram rightDiagram = new DefaultStateDiagram( this );
//        //setup excitations.
//        for( int i = 0; i < numBandSets(); i++ ) {
//            SemiconductorBandSet bandSet = bandSetAt( i );
//            if( bandSet.getDopantType() == DopantType.P ) {
//                rightDiagram.exciteP( DopantType.P.getDopingBand(), i );
//            }
//            else if( bandSet.getDopantType() == DopantType.N ) {
//                rightDiagram.exciteN( DopantType.N.getDopingBand(), i );
//            }
//        }
//        for( int i = 0; i < numBandSets(); i++ ) {
//            SemiconductorBandSet bandSet = bandSetAt( i );
//            DopantType dt = bandSet.getDopantType();
//            if( dt != null ) {
//
//                Band band = bandSet.bandAt( dt.getDopingBand() );
//                EnergyLevel level = band.energyLevelAt( dt.getNumFilledLevels() );
//                int levelHeight = level.getAbsoluteHeight();
//                rightDiagram.propagateRight( energyCellAt( levelHeight, 0 ) );
//            }
//        }
//        //setup the left side.
//
//
//        SemiconductorBandSet bs0 = bandSetAt( 0 );
//        DopantType leftDopant = bs0.getDopantType();
//        if( leftDopant != null ) {
//            Band band = bs0.bandAt( leftDopant.getDopingBand() );
//            EnergyCell conductionCell = band.energyLevelAt( leftDopant.getNumFilledLevels() ).cellAt( 0 );
//            rightDiagram.enter( conductionCell );
//            rightDiagram.propagateRight( conductionCell );
//        }
//
//        //setup the right side.
//        SemiconductorBandSet bsR = getRightBand();
//        DopantType rightDopant = bsR.getDopantType();
//        if( bsR.getDopantType() != null ) { //okay to leave.
//            Band leaveBand = bsR.bandAt( rightDopant.getDopingBand() );
//            EnergyCell exitCell = leaveBand.energyLevelAt( rightDopant.getNumFilledLevels() ).cellAt( 1 );
//            rightDiagram.exitRight( exitCell );
//        }
//
//
//        stateModel.addModel( new PositiveVoltage(), rightDiagram );
//    }

    private void generateStateDiagrams() {
        stateModel.clear();
        double minVolts = .1;
        double maxVolts = 10;
        if ( numBandSets() == 1 ) {
            ConductRight1 pright = new ConductRight1( this, 1, 0, DopantType.P );
            stateModel.addModel( new DefaultCriteria( DopantType.P, 0, maxVolts ), pright );

            ConductLeft1 pleft = new ConductLeft1( this, 1, 0, DopantType.P );
            stateModel.addModel( new DefaultCriteria( DopantType.P, -maxVolts, -minVolts ), pleft );

            ConductRight1 nright = new ConductRight1( this, 2, 0, DopantType.N );
            stateModel.addModel( new DefaultCriteria( DopantType.N, 0, maxVolts ), nright );

            ConductLeft1 nleft = new ConductLeft1( this, 2, 0, DopantType.N );
            stateModel.addModel( new DefaultCriteria( DopantType.N, -maxVolts, -minVolts ), nleft );
        }
        if ( numBandSets() == 2 ) {
            add2StateDiagrams();
        }
        if ( numBandSets() == 3 ) {
            add3StateDiagrams();
        }
    }

    class DopantList {
        ArrayList list = new ArrayList();

        public DopantList( DopantType a, DopantType b, DopantType c ) {
            list.add( a );
            list.add( b );
            list.add( c );
        }

        public String toString() {
            return list.toString();
        }
    }

    private void add3StateDiagrams() {
        double minVolts = .1;
        double maxVolts = 10;
        DopantType[] keys = new DopantType[]{null, DopantType.P, DopantType.N};
        ArrayList lists = new ArrayList();
        for ( int i = 0; i < keys.length; i++ ) {
            DopantType a = keys[i];
            for ( int j = 0; j < keys.length; j++ ) {
                DopantType b = keys[j];
                for ( int k = 0; k < keys.length; k++ ) {
                    DopantType c = keys[k];
                    lists.add( new DopantList( a, b, c ) );
                }
            }
        }
        System.out.println( "lists = " + lists );
        add3StateBothDir( DopantType.P, maxVolts );
        add3StateBothDir( DopantType.N, maxVolts );
//        addPNP();
        PNPHandler pnpHandler = new PNPHandler( this );
        stateModel.addModel( pnpHandler, pnpHandler );
//        NNPHandler nnpHandler = new NNPHandler( this );
//        stateModel.addModel( nnpHandler, nnpHandler );
//        DiodeIn3Handler criteria = new DiodeIn3Handler(this );
//        stateModel.addModel( criteria,criteria );
//        stateModel.addModel( new DefaultCriteria( DopantType.N, DopantType.N, DopantType.P, minVolts, maxVolts ), new ModelElement() {
//            public void stepInTime( double dt ) {
//                System.out.println( "EnergySection.stepInTime" );
//            }
//        } );
    }

    private void add3StateBothDir( DopantType type, double maxVolts ) {
        {//right
            SimpleConductRight3 nnn = new SimpleConductRight3( this, type );
            DefaultCriteria nnnCrit = new DefaultCriteria( type, type, type, 0, maxVolts );
            Clear3 clear3 = new Clear3( this, nnn.getLeftExcite(), nnn.getMidExcite(), nnn.getRightExcite() );
            VoltageSplit vs = new VoltageSplit( this, clear3, nnn );
            stateModel.addModel( nnnCrit, vs );
        }
        {//left
            SimpleConductLeft3 nnn = new SimpleConductLeft3( this, type );
            DefaultCriteria nnnCrit = new DefaultCriteria( type, type, type, -maxVolts, 0 );
            Clear3 clear3 = new Clear3( this, nnn.getLeftExcite(), nnn.getMidExcite(), nnn.getRightExcite() );
            VoltageSplit vs = new VoltageSplit( this, clear3, nnn );
            stateModel.addModel( nnnCrit, vs );
        }
    }

    private void add2StateDiagrams() {
        {
            addSimpleConductRight( DopantType.P );//pp
            addSimpleConductRight( DopantType.N );//nn
            addS_( DopantType.P );//sp
            addS_( DopantType.N );//sn

            //ps right and ns right are trivial, maybe shouldn't show anything.

            addSimpleConductLeft( DopantType.P );//pp left
            addSimpleConductLeft( DopantType.N );//nn left
            add_S( DopantType.P );//ps
            add_S( DopantType.N );//pn
        }
        PNHandler pnHandler = new PNHandler( this );
        stateModel.addModel( pnHandler, pnHandler );
        NPHandler npHandler = new NPHandler( this );
        stateModel.addModel( npHandler, npHandler );
    }

    private void add_S( DopantType dopantType ) {
        DefaultStateDiagram sp = new DefaultStateDiagram( this );
        ExciteForConduction excite = sp.excite( dopantType.getDopingBand(), 0, dopantType.getNumFilledLevels() - 1 );
        sp.move( excite.getRightCell(), excite.getLeftCell(), getSpeed() );
        sp.exitLeft( excite.getLeftCell() );
        ModelCriteria mc = new DefaultCriteria( dopantType, null, Double.NEGATIVE_INFINITY, 0 );

        DefaultStateDiagram restore = new DefaultStateDiagram( this );
        EnergyCell r = getLowerNeighbor( excite.getRightCell() );
        EnergyCell l = getLeftNeighbor( r );
        restore.enter( l );
        restore.move( l, r, getSpeed() );
        restore.move( excite.getRightCell(), excite.getLeftCell(), getSpeed() );
        restore.exitLeft( excite.getLeftCell() );
        restore.unexcite( r );
        restore.unexcite( l );
        VoltageSplit vs = new VoltageSplit( this, restore, sp );
        stateModel.addModel( mc, vs );

        DefaultStateDiagram right = new DefaultStateDiagram( this );
        right.enter( l );
        right.move( l, r, getSpeed() );
        right.unexcite( r );
        right.unexcite( l );
        ModelCriteria mc2 = new DefaultCriteria( null, dopantType, 0, Double.POSITIVE_INFINITY );
        stateModel.addModel( mc2, right );
    }

    private void addS_( DopantType dopantType ) {
        DefaultStateDiagram sp = new DefaultStateDiagram( this );
        ExciteForConduction excite = sp.excite( dopantType.getDopingBand(), 1, dopantType.getNumFilledLevels() - 1 );
        sp.move( excite.getLeftCell(), excite.getRightCell(), getSpeed() );
        sp.exitRight( excite.getRightCell() );
        ModelCriteria mc = new DefaultCriteria( null, dopantType, 0, Double.POSITIVE_INFINITY );

        DefaultStateDiagram restore = new DefaultStateDiagram( this );
        EnergyCell r = getLowerNeighbor( excite.getRightCell() );
        EnergyCell l = getLeftNeighbor( r );
        restore.enter( r );
        restore.move( r, l, getSpeed() );
        restore.move( excite.getLeftCell(), excite.getRightCell(), getSpeed() );
        restore.exitRight( excite.getRightCell() );
        restore.unexcite( r );
        restore.unexcite( l );
        VoltageSplit vs = new VoltageSplit( this, restore, sp );
        stateModel.addModel( mc, vs );

        DefaultStateDiagram left = new DefaultStateDiagram( this );
        left.enter( r );
        left.move( r, l, getSpeed() );
        left.unexcite( r );
        left.unexcite( l );
        ModelCriteria mc2 = new DefaultCriteria( null, dopantType, Double.NEGATIVE_INFINITY, 0 );
        stateModel.addModel( mc2, left );
    }

    private void addSimpleConductLeft( DopantType type ) {
        SimpleConductLeft2 scl2 = new SimpleConductLeft2( this, type );
        Clear2 clear = new Clear2( this, scl2.getLeftExcite(), scl2.getRightExcite() );
        ConductRight2 cr2 = new ConductRight2( this, clear, scl2 );
        ModelCriteria mc = new DefaultCriteria( type, type, Double.NEGATIVE_INFINITY, 0 );
        stateModel.addModel( mc, cr2 );
    }

    private void addSimpleConductRight( DopantType type ) {
        SimpleConductRight2 scr2 = new SimpleConductRight2( this, type );
        Clear2 clear = new Clear2( this, scr2.getLeftExcite(), scr2.getRightExcite() );
        ConductRight2 cr2 = new ConductRight2( this, clear, scr2 );
        ModelCriteria mc = new DefaultCriteria( type, type, 0, Double.POSITIVE_INFINITY );
        stateModel.addModel( mc, cr2 );
    }

    public void addEField( Rectangle2D.Double bandrect, Rectangle2D.Double bandrect2 ) {
        PhetVector center = getCenter( bandrect, bandrect2 );
        ElectricFieldSection field = new ElectricFieldSection( center );
        ElectricFieldSectionGraphic fieldGraphic = new ElectricFieldSectionGraphic( field, transform );
        electricFields.add( field );
        electricFieldGraphics.add( fieldGraphic );
    }

    public static PhetVector getCenter( Rectangle2D.Double a, Rectangle2D.Double b ) {
        Rectangle2D.Double ctrRect = new Rectangle2D.Double( a.x, a.y, a.width, a.height );
        ctrRect.add( b );
        PhetVector center = RectangleUtils.getCenter( ctrRect );
        return center;
    }

    private void newBandSet( Rectangle2D.Double rect, int index ) {
        newBandSet( rect, false, index );
    }

    private BandSetDescriptor toBandSetDescriptor( Rectangle2D.Double rect ) {
        double heightExpand = rect.getHeight() / 3.2;

        //TODO Uncomment this to expand the visible region.
        rect = new Rectangle2D.Double( rect.getX(), rect.getY() - heightExpand / 2, rect.getWidth(), rect.getHeight() + heightExpand );

        BandSetDescriptor bsd = new BandSetDescriptor();
        int numBands = 4;
        int numLevels = 10;
        double semicBandGap = rect.getHeight() / 8;
        double normalBandGap = rect.getHeight() / 40;

        double remainingEnergy = rect.getHeight() - semicBandGap - normalBandGap;
        double energyPerBand = remainingEnergy / numBands;

        BandDescriptor bot = new BandDescriptor( numLevels, new EnergySpaceRegion( rect.getX(), rect.getY(), rect.getWidth(), energyPerBand ), normalBandGap );
        BandDescriptor val = new BandDescriptor( numLevels, new EnergySpaceRegion( rect.getX(), bot.getNextEnergyStart(), rect.getWidth(), energyPerBand ), semicBandGap );
        BandDescriptor con = new BandDescriptor( numLevels, new EnergySpaceRegion( rect.getX(), val.getNextEnergyStart(), rect.getWidth(), energyPerBand ), normalBandGap );
        BandDescriptor top = new BandDescriptor( numLevels, new EnergySpaceRegion( rect.getX(), con.getNextEnergyStart(), rect.getWidth(), energyPerBand ), normalBandGap );
        bsd.addBandDescriptor( bot );
        bsd.addBandDescriptor( val );
        bsd.addBandDescriptor( con );
        bsd.addBandDescriptor( top );
        return bsd;
    }

    private void newBandSet( Rectangle2D.Double rect, boolean jagged, int index ) {
        BandSetDescriptor bsd = toBandSetDescriptor( rect );
        SemiconductorBandSet bandSet = new SemiconductorBandSet( bsd, this, index );

        BandSetGraphic graphic = null;

        if ( jagged ) {
            graphic = new JaggedSplitBandGraphic( this, transform, bandSet, rect );
        }
        else {
            graphic = new BandSetGraphic( this, transform, bandSet, rect );
        }

        bandSets.add( bandSet );
        bandSetGraphics.add( graphic );
        initParticles( bandSet );
    }

    private void initParticles( SemiconductorBandSet bandSet ) {
        Band b = bandSet.getBottomBand();
        fillBand( b );
        fillBand( bandSet.getValenceBand() );
    }

    private void fillBand( Band b ) {
        for ( int i = 0; i < b.numEnergyLevels(); i++ ) {
            EnergyLevel level = b.energyLevelAt( i );
            fillLevel( level );
        }
    }

    public void fillLevel( EnergyLevel level ) {
        for ( int k = 0; k < level.numCells(); k++ ) {
            BandParticle bp = new BandParticle( level.cellAt( k ) );
            PlusCharge plus = new PlusCharge( level.cellAt( k ) );
            addPlus( plus );
            addParticle( bp );
        }
    }

    private void addPlus( PlusCharge plus ) {
        PlusGraphic plusGraphic = new PlusGraphic( plus, transform, plusImage );
        plusses.add( plus );
        plusGraphics.add( plusGraphic );
    }

    public void stepInTime( double dt ) {
        for ( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle) particles.get( i );
            bandParticle.stepInTime( dt );
        }
//        if( stateModel != null ) {
        stateModel.stepInTime( dt );
//        }
        /**Fudge for widening depletion region for NPN*/
//        if( isNPN() ) {
        recomputeElectricFields();
//        }
    }

    public void paint( Graphics2D graphics2D ) {
        energyTextGraphic.paint( graphics2D );
//        Area clip = null;// For keeping electrons within the valid range.
        Rectangle2D clip = null;
        for ( int i = 0; i < bandSetGraphics.size(); i++ ) {
            BandSetGraphic bandSetGraphic = (BandSetGraphic) bandSetGraphics.get( i );
            Shape viewBounds = transform.createTransformedShape( bandSetGraphic.getViewport() );

            if ( clip == null ) {
//                clip = new Area( viewBounds);//bandSetGraphic.getViewport();
                clip = viewBounds.getBounds2D();
            }
            else {
                clip = clip.createUnion( viewBounds.getBounds2D() );
//                clip.add(new Area( viewBounds ) );//createUnion( bandSetGraphic.getViewport() );
            }
        }
        Shape orig = graphics2D.getClip();
        graphics2D.setClip( clip );

        for ( int i = 0; i < bandSetGraphics.size(); i++ ) {
            BandSetGraphic bandSetGraphic = (BandSetGraphic) bandSetGraphics.get( i );
            bandSetGraphic.paint( graphics2D );
        }

        for ( int i = 0; i < particleGraphics.size(); i++ ) {
            BandParticleGraphic bandParticleGraphic = (BandParticleGraphic) particleGraphics.get( i );
            bandParticleGraphic.paint( graphics2D );
        }
        for ( int i = 0; i < plusGraphics.size(); i++ ) {
            Graphic g = (Graphic) plusGraphics.get( i );
            g.paint( graphics2D );
        }

        for ( int i = 0; i < electricFieldGraphics.size(); i++ ) {
            ElectricFieldSectionGraphic electricFieldGraphic = (ElectricFieldSectionGraphic) electricFieldGraphics.get( i );
            electricFieldGraphic.paint( graphics2D );
        }

        graphics2D.setClip( orig );
//        graphics2D.setColor( Color.green );
//        graphics2D.setStroke( new BasicStroke( 1) );
//        graphics2D.draw( clip );
        bucketSection.paint( graphics2D );
    }

    public void addParticle( BandParticle bandParticle ) {
        particles.add( bandParticle );
        BandParticleGraphic graphic = new BandParticleGraphic( bandParticle, transform, particleImage );
        particleGraphics.add( graphic );
    }

    public void removeParticle( BandParticle bandParticle ) {
        particles.remove( bandParticle );
        for ( int i = 0; i < particleGraphics.size(); i++ ) {
            BandParticleGraphic bandParticleGraphic = (BandParticleGraphic) particleGraphics.get( i );
            if ( bandParticleGraphic.getBandParticle() == bandParticle ) {
                particleGraphics.remove( bandParticleGraphic );
                i--;
            }
        }
    }

    private void removePlus( PlusCharge plusCharge ) {
        plusses.remove( plusCharge );
        for ( int i = 0; i < plusGraphics.size(); i++ ) {
            PlusGraphic plusGraphic = (PlusGraphic) plusGraphics.get( i );
            if ( plusGraphic.getPlusCharge() == plusCharge ) {
                plusGraphics.remove( plusGraphic );
                i--;
            }
        }
    }

    public SemiconductorBandSet bandSetAt( int i ) {
        return (SemiconductorBandSet) bandSets.get( i );
    }

    public int numBandSets() {
        return bandSets.size();
    }

    void setDopant( SemiconductorBandSet bandSet, DopantType dopantType ) {
        bandSet.setDopantType( dopantType );
    }

    public void clear( EnergyLevel energyLevel ) {
        for ( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle) particles.get( i );
            if ( bandParticle.getEnergyLevel() == energyLevel ) {
                removeParticle( bandParticle );
                i--;
            }
        }
        for ( int i = 0; i < plusses.size(); i++ ) {
            PlusCharge plusCharge = (PlusCharge) plusses.get( i );
            if ( plusCharge.getEnergyLevel() == energyLevel ) {
                removePlus( plusCharge );
                i--;
            }
        }
    }

    public void dopingChanged( CircuitSection circuitSection ) {
//        clearRegions();
        clearDoping();
        for ( int i = 0; i < circuitSection.numDopantSlots() && i < numBandSets(); i++ ) {
            setDopant( bandSetAt( i ), circuitSection.dopantSlotAt( i ).getDopantType() );
        }

        if ( getVoltage() == 0 ) {
            biasManager.setupInternalBias();
        }
        determineState();
        recomputeElectricFields();
        generateStateDiagrams();
    }

    private void determineState() {
        //TODO
//        if( isNP() && getVoltage() > 0 ) {
//            this.stateModel = stateModelSet.getPNForward();
//        }
//        else if( isPN() && getVoltage() < 0 ) {
//            this.stateModel = stateModelSet.getPnforward();
//        }
//        else if( isPN() && getVoltage() > 0 ) {
//            this.stateModel = stateModelSet.getPNBackward();
//        }
//        else {
//            this.stateModel = new NullStateModel();
//        }
    }

    private void recomputeElectricFields() {
        /**Recompute the internal electric field.*/
        for ( int i = 0; i < electricFields.size(); i++ ) {
            ElectricFieldSection electricFieldSection = (ElectricFieldSection) electricFields.get( i );
            BandSet pre = bandSetAt( i );
            BandSet post = bandSetAt( i + 1 );
            double voltage = getExcessCharge( post ) - getExcessCharge( pre );
            double amountComplete = getAmountComplete();
//            System.out.println( "amountComplete = " + amountComplete );
//            double norm = -voltage / 6.0 * amountComplete;
            double norm = -voltage / 12.0 * amountComplete;
            electricFieldSection.getInternalField().setStrength( norm );
        }
    }

    private double getAmountComplete() {
        if ( continuousBiasChangeListeners.size() == 0 ) {
            return 1;
        }
        double sum = 0;
        for ( int i = 0; i < continuousBiasChangeListeners.size(); i++ ) {
            ContinuousBiasObserver continuousBiasObserver = (ContinuousBiasObserver) continuousBiasChangeListeners.get( i );
            double frac = continuousBiasObserver.getFractionalDistanceToDestination();
//            System.out.println("frac = " + frac);
            sum += frac;
        }
        double avg = sum / continuousBiasChangeListeners.size();
        return avg;
    }

    ArrayList continuousBiasChangeListeners = new ArrayList();

    class ContinuousBiasObserver implements SimpleObserver {
        EnergyCell dst;
        BandParticle donor;
        private double initDist;

        public ContinuousBiasObserver( EnergyCell dst, BandParticle donor ) {
            this.dst = dst;
            this.donor = donor;
            donor.addObserver( this );
            this.initDist = getDistanceToDestination();
        }

        public double getDistanceToDestination() {
            return dst.getPosition().getSubtractedInstance( donor.getPosition() ).getMagnitude();
        }

        public double getFractionalDistanceToDestination() {
            return ( initDist - getDistanceToDestination() ) / initDist;
        }

        public void update() {
            if ( getFractionalDistanceToDestination() >= .99 ) {
                continuousBiasChangeListeners.remove( this );
                donor.removeObserver( this );
            }
            EnergySection.this.recomputeElectricFields();
        }
    }

    public void voltageChanged( Battery source ) {
        for ( int i = 0; i < electricFields.size(); i++ ) {
            ElectricFieldSection electricFieldSection = (ElectricFieldSection) electricFields.get( i );
            electricFieldSection.voltageChanged( source );
        }
        determineState();
    }


    public Battery getBattery() {
        return battery;
    }


    public void addConductionListener( ConductionListener cl ) {
        conductionListeners.add( cl );
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSingleSection() {
        clearRegions();
        setupOneRegion();
    }

    public void setDoubleSection() {
        clearRegions();
        setupTwoRegions();
    }

    public void setTripleSection() {
        clearRegions();
        setupThreeRegions();
    }

    public EntryPoint[] getSources() {
        return new EntryPoint[0];
    }

    public double getVoltage() {
        return battery.getVoltage();
    }

    public SemiconductorBandSet getRightBand() {
        return bandSetAt( numBandSets() - 1 );
    }

    public EnergyCell getNeighbor( EnergyCell energyCell, int dlevel, int dcolumn ) {
        int levelValue = energyCell.getEnergyLevelAbsoluteIndex();
        int column = energyCell.getColumn();
        return energyCellAt( levelValue + dlevel, column + dcolumn );
    }

    public EnergyCell energyCellAt( int level, int column ) {
        int dstBand = column / 2;
        int destIndex = column % 2;
        if ( dstBand < 0 || dstBand >= numBandSets() ) {
            return null;
        }
        SemiconductorBandSet band = bandSetAt( dstBand );
        if ( level < 0 || level >= numRows() ) {
            return null;
        }
        if ( destIndex == 0 || destIndex == 1 ) {
            return band.energyCellAt( level, destIndex );
        }
        else {
            return null;
        }
    }

    public EnergyCell getUpperNeighbor( EnergyCell cell ) {
        return getNeighbor( cell, 1, 0 );
    }

    public EnergyCell getRightNeighbor( EnergyCell energyCell ) {
        return getNeighbor( energyCell, 0, 1 );
    }

    public EnergyCell getLeftNeighbor( EnergyCell energyCell ) {
        return getNeighbor( energyCell, 0, -1 );
    }

    public EnergyLevel getHighestFilledLevel( SemiconductorBandSet bandSet ) {
        for ( int i = 0; i < bandSet.numEnergyLevels(); i++ ) {
            EnergyLevel level = bandSet.levelAt( i );
            if ( !isFilled( level ) ) {
                int abs = level.getAbsoluteHeight();
                if ( abs == -1 ) {
                    return null;
                }
                return bandSet.levelAt( abs - 1 );
            }
        }
        return null;
    }

    private boolean isFilled( EnergyLevel energyLevel ) {
        BandParticle left = getBandParticle( energyLevel.cellAt( 0 ) );
        BandParticle right = getBandParticle( energyLevel.cellAt( 1 ) );
        return ( left != null && right != null );
    }

    public boolean isClaimed( EnergyCell cell ) {
        return getBandParticle( cell ) != null;
    }

    public BandParticle getBandParticle( EnergyCell cell ) {
        for ( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle) particles.get( i );
            if ( bandParticle.getEnergyCell() == cell ) {
                return bandParticle;
            }
        }
        return null;
    }


    public Speed getFallSpeed() {
        return new ConstantSpeed( .7 );
    }

    public int getExcessCharge( BandSet bandSet ) {
        int sum = 0;
        for ( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle) particles.get( i );
            Band b = bandParticle.getBand();
            if ( b == null ) {
                continue;
            }
            EnergySpaceRegion region = b.getRegion();
            if ( bandParticle.getBandSet() == bandSet && b != null
                 && region.contains( bandParticle.getPosition() )
//            && !bandParticle.isMoving()
                    ) {
                sum++;
            }
        }
        for ( int i = 0; i < plusses.size(); i++ ) {
            PlusCharge plusCharge = (PlusCharge) plusses.get( i );
            if ( plusCharge.getBandSet() == bandSet ) {
                sum--;
            }
        }
        return sum;
    }

    public SemiconductorBandSet getLeftBand() {
        return bandSetAt( 0 );
    }

    public double getAverageParticleDX() {
        double sum = 0;
        for ( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle) particles.get( i );
            PhetVector vel = bandParticle.getDX();
            double x = vel.getX();
            sum += x;
        }
        return sum / particles.size();
    }

    public BandSetGraphic bandSetGraphicAt( int bandIndex ) {
        return (BandSetGraphic) bandSetGraphics.get( bandIndex );
    }

    private boolean isPNP() {
        return isTriodeForTypes( DopantType.P, DopantType.N, DopantType.P );
    }

    private boolean isNPN() {
        return isTriodeForTypes( DopantType.N, DopantType.P, DopantType.N );
    }

    public boolean isPN() {
        return isDiodeForTypes( DopantType.P, DopantType.N );
    }

    public boolean isNP() {
        return isDiodeForTypes( DopantType.N, DopantType.P );
    }

    public boolean isDiodeForTypes( DopantType left, DopantType right ) {
        if ( numBandSets() == 2 ) {
            DopantType leftDopant = bandSetAt( 0 ).getDopantType();
            DopantType rightDopant = bandSetAt( 1 ).getDopantType();
            return leftDopant == left && rightDopant == right;
        }
        else {
            return false;
        }
    }

    public boolean isTriodeForTypes( DopantType left, DopantType mid, DopantType right ) {
        if ( numBandSets() == 3 ) {
            DopantType leftDopant = bandSetAt( 0 ).getDopantType();
            DopantType midDopant = bandSetAt( 1 ).getDopantType();
            DopantType rightDopant = bandSetAt( 2 ).getDopantType();
            return leftDopant == left && midDopant == mid && rightDopant == right;
        }
        else {
            return false;
        }
    }

    private void setupBias( EnergyCell src, int dcolumn ) {
        EnergyCell neighbor = getNeighbor( src, 0, dcolumn );
        SemiconductorBandSet bs = neighbor.getBandSet();
        EnergyLevel hi = getHighestFilledLevel( bs );
        EnergyLevel dst = bs.levelAt( hi.getAbsoluteHeight() + 1 );        //up one.
        EnergyCell dstCell = dst.cellAt( neighbor.getIndex() );
        setupBias( src, dstCell );
    }

    private void setupBias( EnergyCell src, EnergyCell dstCell ) {
        BandParticle bp = getBandParticle( src );
        if ( bp.isLocatedAtCell() && !isClaimed( dstCell ) ) {
            EnergySection.ContinuousBiasObserver cbo = new EnergySection.ContinuousBiasObserver( dstCell, bp );
            continuousBiasChangeListeners.add( cbo );
            bp.setState( new MoveToCell( bp, dstCell, .2 ) );
        }
    }

    public int numColumns() {
        return numBandSets() * 2;
    }

    public int numRows() {
        return bandSetAt( 0 ).numEnergyLevels();
    }

    public int getColumnCharge( int column ) {
        if ( column == -1 ) {
            double volts = getVoltage();
            return (int) volts;
        }
        else if ( column == numColumns() ) {
            double volts = getVoltage();
            return -(int) volts;
        }
        else {
            int sum = 0;
            Rectangle2D.Double rect = getColumnRect( column );
            for ( int i = 0; i < numParticles(); i++ ) {
                if ( rect.contains( particleAt( i ).getPosition().toPoint2D() ) ) {
                    sum++;
                }
            }
            for ( int i = 0; i < numPlusses(); i++ ) {
                PlusCharge pc = plusAt( i );
                if ( rect.contains( pc.getPosition().toPoint2D() ) ) {
                    sum--;
                }
            }
            return sum;
        }
    }

    private PlusCharge plusAt( int i ) {
        return (PlusCharge) plusses.get( i );
    }

    private int numPlusses() {
        return plusses.size();
    }

    public Rectangle2D.Double getColumnRect( int column ) {
        int index = column % 2;
        BandSet bs = bandSetAt( column / 2 );
        Rectangle2D.Double rect = bs.getBounds();
        if ( index == 0 ) {
            Rectangle2D.Double leftSide = new Rectangle2D.Double( rect.x, rect.y, rect.width / 2, rect.height );
            return leftSide;
        }
        else if ( index == 1 ) {
            Rectangle2D.Double rightSide = new Rectangle2D.Double( rect.x + rect.width / 2, rect.y, rect.width / 2, rect.height );
            return rightSide;
        }
        throw new RuntimeException( "No such case." );
    }

    public EnergyCell getLowerNeighbor( EnergyCell cell ) {
        int index = cell.getIndex();
        int level = cell.getEnergyLevel().getAbsoluteHeight();
        if ( level == 0 ) {
            return null;
        }
//        System.out.println( "Getting lower neighber for level=" + level + ", id=" + cell.getEnergyLevel().getID() );
        EnergyLevel lower = cell.getBandSet().levelAt( level - 1 );

        if ( lower == null ) {
//            System.out.println("lower = " + lower);
            return null;
        }
        return lower.cellAt( index );
    }

    public boolean isOwned( EnergyLevel level ) {
        return isOwned( level.cellAt( 0 ) ) && isOwned( level.cellAt( 1 ) );
    }

    public boolean isOwned( EnergyCell cell ) {
        BandParticle bp = getBandParticle( cell );
        if ( bp != null && bp.isLocatedAtCell() ) {
            return true;
        }
        return false;
    }

    interface InternalBiasSetup {
        boolean isValid( EnergySection energySection );

        void apply( EnergySection energySection );
    }

    class OneBandSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.numBandSets() == 1;
        }

        public void apply( EnergySection energySection ) {
            return;
        }

    }

    class PNSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.isPN();
        }

        public void apply( EnergySection energySection ) {
            EnergyLevel hi = getHighestFilledLevel( bandSetAt( 1 ) );
            EnergyCell asrc = hi.cellAt( 0 );
            setupBias( asrc, -2 );
            EnergyCell bsrc = hi.cellAt( 1 );
            setupBias( bsrc, -2 );
        }
    }

    class NPSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.isNP();
        }

        public void apply( EnergySection energySection ) {
            EnergyLevel hi = getHighestFilledLevel( bandSetAt( 0 ) );
            EnergyCell asrc = hi.cellAt( 0 );
            setupBias( asrc, 2 );
            EnergyCell bsrc = hi.cellAt( 1 );
            setupBias( bsrc, 2 );
        }
    }

    class PNPSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.isPNP();
        }

        public void apply( EnergySection energySection ) {
            EnergyLevel hi = getHighestFilledLevel( bandSetAt( 1 ) );
            setupBias( hi.cellAt( 0 ), -1 );
            setupBias( getLowerNeighbor( hi.cellAt( 0 ) ), -2 );

            setupBias( hi.cellAt( 1 ), 1 );
            setupBias( getLowerNeighbor( hi.cellAt( 1 ) ), 2 );
        }

    }

    class NPNSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.isNPN();
        }

        public void apply( EnergySection energySection ) {
            EnergyLevel ctr = getHighestFilledLevel( bandSetAt( 1 ) );
            EnergyLevel hiLeft = getHighestFilledLevel( bandSetAt( 0 ) );
            EnergyCell leftCell = ctr.cellAt( 0 );
            EnergyCell rightCell = ctr.cellAt( 1 );

            setupBias( hiLeft.cellAt( 0 ), getUpperNeighbor( leftCell ) );
            setupBias( hiLeft.cellAt( 1 ), getUpperNeighbor( getUpperNeighbor( leftCell ) ) );

            EnergyLevel hiRight = getHighestFilledLevel( bandSetAt( 2 ) );
            setupBias( hiRight.cellAt( 0 ), getUpperNeighbor( rightCell ) );
            setupBias( hiRight.cellAt( 1 ), getUpperNeighbor( getUpperNeighbor( rightCell ) ) );
        }

    }

    class InternalBiasManager {
        ArrayList biases = new ArrayList();

        public InternalBiasManager() {
            addInternalBiasSetup( new OneBandSetup() );
            addInternalBiasSetup( new NPNSetup() );
            addInternalBiasSetup( new NPSetup() );
            addInternalBiasSetup( new PNPSetup() );
            addInternalBiasSetup( new PNSetup() );
        }

        public void addInternalBiasSetup( InternalBiasSetup ibs ) {
            biases.add( ibs );
        }

        private void setupInternalBias() {
            continuousBiasChangeListeners.clear();
            for ( int i = 0; i < biases.size(); i++ ) {
                InternalBiasSetup internalBiasSetup = (InternalBiasSetup) biases.get( i );
                if ( internalBiasSetup.isValid( EnergySection.this ) ) {
                    internalBiasSetup.apply( EnergySection.this );
                    break;
                }
            }
        }
    }


}
