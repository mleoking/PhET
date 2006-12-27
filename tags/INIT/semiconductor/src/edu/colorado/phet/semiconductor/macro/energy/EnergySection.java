package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.common.EnergySpaceRegion;
import edu.colorado.phet.semiconductor.macro.BucketSection;
import edu.colorado.phet.semiconductor.macro.EntryPoint;
import edu.colorado.phet.semiconductor.macro.SemiconductorModule;
import edu.colorado.phet.semiconductor.macro.circuit.CircuitSection;
import edu.colorado.phet.semiconductor.macro.circuit.ConductionListener;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.Battery;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatteryListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantChangeListener;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.bands.*;
import edu.colorado.phet.semiconductor.macro.energy.statemodels.CompleteStateModel;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitLeftState;
import edu.colorado.phet.semiconductor.macro.energy.states.ExitRightState;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energy.states.Speed;
import edu.colorado.phet.semiconductor.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 8:26:00 PM
 * Copyright (c) Feb 7, 2004 by Sam Reid
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
    private double particleWidth;
    private BufferedImage plusImage;

//    StateModel stateModel;
    final StateModel stateModel = new CompleteStateModel( this );
    private Speed speed;
    private Rectangle2D.Double bounds;
    private BucketSection bucketSection;
    InternalBiasManager biasManager = new InternalBiasManager();
    StateModelSet stateModelSet;

    public EnergySection( ModelViewTransform2D transform, double particleWidth, Battery battery, Speed speed, Rectangle2D.Double bounds ) throws IOException {
        this.particleWidth = particleWidth;
        this.transform = transform;
        this.battery = battery;
        this.speed = speed;
        this.bounds = bounds;
        this.plusImage = SemiconductorModule.imageLoader.loadImage( "images/particle-red-plus.gif" );
        particleImage = MacroCircuitGraphic.getParticleImage();
        setupTwoRegions();
        PhetVector textLocation = new PhetVector( .65, 1 );
        energyTextGraphic = new EnergyTextGraphic( transform, textLocation );
        bucketSection = new BucketSection( transform, this, particleImage );
        stateModelSet = new StateModelSet( this );
        //TODO
        //stateModel = stateModelSet.getNullModel();
    }

    public int numParticles() {
        return particles.size();
    }

    public BandParticle particleAt( int i ) {
        return (BandParticle)particles.get( i );
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

        if( jagged ) {
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
        for( int i = 0; i < b.numEnergyLevels(); i++ ) {
            EnergyLevel level = b.energyLevelAt( i );
            fillLevel( level );
        }
    }

    public void fillLevel( EnergyLevel level ) {
        for( int k = 0; k < level.numCells(); k++ ) {
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

    public void addBandSet( BandSet bandSet ) {
        bandSets.add( bandSet );
    }

    public void stepInTime( double v ) {
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            bandParticle.stepInTime( v );
        }
//        if( stateModel != null ) {
        stateModel.updateStates();
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
        for( int i = 0; i < bandSetGraphics.size(); i++ ) {
            BandSetGraphic bandSetGraphic = (BandSetGraphic)bandSetGraphics.get( i );
            Shape viewBounds = transform.createTransformedShape( bandSetGraphic.getViewport() );

            if( clip == null ) {
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
        for( int i = 0; i < bandSetGraphics.size(); i++ ) {
            BandSetGraphic bandSetGraphic = (BandSetGraphic)bandSetGraphics.get( i );
            bandSetGraphic.paint( graphics2D );
        }

        for( int i = 0; i < particleGraphics.size(); i++ ) {
            BandParticleGraphic bandParticleGraphic = (BandParticleGraphic)particleGraphics.get( i );
            bandParticleGraphic.paint( graphics2D );
        }
        for( int i = 0; i < plusGraphics.size(); i++ ) {
            Graphic g = (Graphic)plusGraphics.get( i );
            g.paint( graphics2D );
        }

        for( int i = 0; i < electricFieldGraphics.size(); i++ ) {
            ElectricFieldSectionGraphic electricFieldGraphic = (ElectricFieldSectionGraphic)electricFieldGraphics.get( i );
            electricFieldGraphic.paint( graphics2D );
        }
        graphics2D.setClip( orig );
        bucketSection.paint( graphics2D );
    }

    public void addParticle( BandParticle bandParticle ) {
        particles.add( bandParticle );
        BandParticleGraphic graphic = new BandParticleGraphic( bandParticle, transform, particleImage );
        particleGraphics.add( graphic );
    }

    public void removeParticle( BandParticle bandParticle ) {
        particles.remove( bandParticle );
        for( int i = 0; i < particleGraphics.size(); i++ ) {
            BandParticleGraphic bandParticleGraphic = (BandParticleGraphic)particleGraphics.get( i );
            if( bandParticleGraphic.getBandParticle() == bandParticle ) {
                particleGraphics.remove( bandParticleGraphic );
                i--;
            }
        }
    }

    private void removePlus( PlusCharge plusCharge ) {
        plusses.remove( plusCharge );
        for( int i = 0; i < plusGraphics.size(); i++ ) {
            PlusGraphic plusGraphic = (PlusGraphic)plusGraphics.get( i );
            if( plusGraphic.getPlusCharge() == plusCharge ) {
                plusGraphics.remove( plusGraphic );
                i--;
            }
        }
    }

    public SemiconductorBandSet bandSetAt( int i ) {
        return (SemiconductorBandSet)bandSets.get( i );
    }

    public int numBandSets() {
        return bandSets.size();
    }

    void setDopant( SemiconductorBandSet bandSet, DopantType dopantType ) {
        bandSet.setDopantType( dopantType );
    }

    public void clear( EnergyLevel energyLevel ) {
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            if( bandParticle.getEnergyLevel() == energyLevel ) {
                removeParticle( bandParticle );
                i--;
            }
        }
        for( int i = 0; i < plusses.size(); i++ ) {
            PlusCharge plusCharge = (PlusCharge)plusses.get( i );
            if( plusCharge.getEnergyLevel() == energyLevel ) {
                removePlus( plusCharge );
                i--;
            }
        }
    }

    public void dopingChanged( CircuitSection circuitSection ) {
//        clearRegions();
        clearDoping();
        for( int i = 0; i < circuitSection.numDopantSlots() && i < numBandSets(); i++ ) {
            setDopant( bandSetAt( i ), circuitSection.dopantSlotAt( i ).getDopantType() );
        }

        if( getVoltage() == 0 ) {
            biasManager.setupInternalBias();
        }
        determineState();
        recomputeElectricFields();
    }

    private void determineState() {
        //TODO
//        if( isNP() && getVoltage() > 0 ) {
//            this.stateModel = stateModelSet.getNPForward();
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
        for( int i = 0; i < electricFields.size(); i++ ) {
            ElectricFieldSection electricFieldSection = (ElectricFieldSection)electricFields.get( i );
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
        if( continuousBiasChangeListeners.size() == 0 ) {
            return 1;
        }
        double sum = 0;
        for( int i = 0; i < continuousBiasChangeListeners.size(); i++ ) {
            ContinuousBiasObserver continuousBiasObserver = (ContinuousBiasObserver)continuousBiasChangeListeners.get( i );
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
            if( getFractionalDistanceToDestination() >= .99 ) {
                continuousBiasChangeListeners.remove( this );
                donor.removeObserver( this );
            }
            EnergySection.this.recomputeElectricFields();
        }
    }

    public void voltageChanged( Battery source ) {
        for( int i = 0; i < electricFields.size(); i++ ) {
            ElectricFieldSection electricFieldSection = (ElectricFieldSection)electricFields.get( i );
            electricFieldSection.voltageChanged( source );
        }
        determineState();
    }


    public ArrayList getParticles() {
        return particles;
    }

    public Battery getBattery() {
        return battery;
    }

    public int indexOf( BandSet bs ) {
        return bandSets.indexOf( bs );
    }


    public void addConductionListener( ConductionListener cl ) {
        conductionListeners.add( cl );
    }

    public void setConductionAllowed( boolean allowed ) {
        for( int i = 0; i < conductionListeners.size(); i++ ) {
            ConductionListener cl = (ConductionListener)conductionListeners.get( i );
            cl.setConductionAllowed( allowed );
        }
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
        if( stateModel == null ) {
            return new EntryPoint[0];
        }
        return stateModel.getEntryPoints();
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
        if( dstBand < 0 || dstBand >= numBandSets() ) {
            return null;
        }
        SemiconductorBandSet band = bandSetAt( dstBand );
        if( level < 0 || level >= numRows() ) {
            return null;
        }
        if( destIndex == 0 || destIndex == 1 ) {
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
        for( int i = 0; i < bandSet.numEnergyLevels(); i++ ) {
            EnergyLevel level = bandSet.levelAt( i );
            if( !isFilled( level ) ) {
                int abs = level.getAbsoluteHeight();
                if( abs == -1 ) {
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
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            if( bandParticle.getEnergyCell() == cell ) {
                return bandParticle;
            }
        }
        return null;
    }

    public EnergyCell getLowerNeighbor( EnergyCell cell ) {
        int index = cell.getIndex();
        int level = cell.getEnergyLevel().getAbsoluteHeight();
        if( level == 0 ) {
            return null;
        }
//        System.out.println( "Getting lower neighber for level=" + level + ", id=" + cell.getEnergyLevel().getID() );
        EnergyLevel lower = cell.getBandSet().levelAt( level - 1 );

        if( lower == null ) {
//            System.out.println("lower = " + lower);
            return null;
        }
        return lower.cellAt( index );
    }

    public Speed getFallSpeed() {
        return new ConstantSpeed( .7 );
    }

    public int getExcessCharge( BandSet bandSet ) {
        int sum = 0;
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            Band b = bandParticle.getBand();
            if( b == null ) {
                continue;
            }
            EnergySpaceRegion region = b.getRegion();
            if( bandParticle.getBandSet() == bandSet && b != null
                && region.contains( bandParticle.getPosition() )
//            && !bandParticle.isMoving()
            ) {
                sum++;
            }
        }
        for( int i = 0; i < plusses.size(); i++ ) {
            PlusCharge plusCharge = (PlusCharge)plusses.get( i );
            if( plusCharge.getBandSet() == bandSet ) {
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
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            PhetVector vel = bandParticle.getDX();
            double x = vel.getX();
            sum += x;
        }
        return sum / particles.size();
    }

    public int getNumParticlesLeavingLeft() {
        int sum = 0;
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            if( bandParticle.getState() instanceof ExitLeftState ) {
                sum++;
            }
        }
        return sum;
    }

    public int getNumParticlesLeavingRight() {
        int sum = 0;
        for( int i = 0; i < particles.size(); i++ ) {
            BandParticle bandParticle = (BandParticle)particles.get( i );
            if( bandParticle.getState() instanceof ExitRightState ) {
                sum++;
            }
        }
        return sum;
    }

    public ElectricFieldSection electricFieldSectionAt( int i ) {
        return (ElectricFieldSection)electricFields.get( i );
    }

    public ElectricFieldSection getElectricFieldSection( BandSet curBS, BandSet dstBS ) {
        int index1 = indexOf( curBS );
        int index2 = indexOf( dstBS );
        if( Math.abs( index1 - index2 ) != 1 ) {
            return null;
        }
        else {
            return electricFieldSectionAt( Math.min( index1, index2 ) );
        }
    }

    public BandSetGraphic bandSetGraphicAt( int bandIndex ) {
        return (BandSetGraphic)bandSetGraphics.get( bandIndex );
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

    public boolean isPP() {
        return isDiodeForTypes( DopantType.P, DopantType.P );
    }

    public boolean isNN() {
        return isDiodeForTypes( DopantType.N, DopantType.N );
    }

    public boolean isDiodeForTypes( DopantType left, DopantType right ) {
        if( numBandSets() == 2 ) {
            DopantType leftDopant = bandSetAt( 0 ).getDopantType();
            DopantType rightDopant = bandSetAt( 1 ).getDopantType();
            return leftDopant == left && rightDopant == right;
        }
        else {
            return false;
        }
    }

    public boolean isTriodeForTypes( DopantType left, DopantType mid, DopantType right ) {
        if( numBandSets() == 3 ) {
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
        BandParticle bp = getBandParticle( src );
        if( bp.isLocatedAtCell() && !isClaimed( dstCell ) ) {
            EnergySection.ContinuousBiasObserver cbo = new EnergySection.ContinuousBiasObserver( dstCell, bp );
            continuousBiasChangeListeners.add( cbo );
            bp.setState( new MoveToCell( bp, dstCell, .2 ) );
        }
    }

    public int numColumns() {
        return numBandSets() * 2;
    }

    public int getNumParticles( EnergyLevel above ) {
        int sum = 0;
        if( isClaimed( above.cellAt( 0 ) ) ) {
            sum++;
        }
        if( isClaimed( above.cellAt( 1 ) ) ) {
            sum++;
        }
        return sum;
    }

    public int getNumParticlesAtCells( EnergyLevel above ) {
        int sum = 0;
        BandParticle a = getBandParticle( above.cellAt( 0 ) );
        BandParticle b = getBandParticle( above.cellAt( 1 ) );
        if( a != null && a.isLocatedAtCell() ) {
            sum++;
        }
        if( b != null && b.isLocatedAtCell() ) {
            sum++;
        }
        return sum;
    }

    public int numRows() {
        return bandSetAt( 0 ).numEnergyLevels();
    }

//    public double getElectricForce( BandParticle bp ) {
//        EnergyCell cell = bp.getEnergyCell();
//        int column = cell.getColumn();
//        int left = getColumnCharge( column - 1 );
//        int right = getColumnCharge( column + 1 );
//        return right - left;
//    }

    public int getColumnCharge( int column ) {
        if( column == -1 ) {
            double volts = getVoltage();
            return (int)volts;
        }
        else if( column == numColumns() ) {
            double volts = getVoltage();
            return -(int)volts;
        }
        else {
            int sum = 0;
            Rectangle2D.Double rect = getColumnRect( column );
            for( int i = 0; i < numParticles(); i++ ) {
                if( rect.contains( particleAt( i ).getPosition().toPoint2D() ) ) {
                    sum++;
                }
            }
            for( int i = 0; i < numPlusses(); i++ ) {
                PlusCharge pc = plusAt( i );
                if( rect.contains( pc.getPosition().toPoint2D() ) ) {
                    sum--;
                }
            }
            return sum;
        }
    }

    private PlusCharge plusAt( int i ) {
        return (PlusCharge)plusses.get( i );
    }

    private int numPlusses() {
        return plusses.size();
    }

    public Rectangle2D.Double getColumnRect( int column ) {
        int index = column % 2;
        BandSet bs = bandSetAt( column / 2 );
        Rectangle2D.Double rect = bs.getBounds();
        if( index == 0 ) {
            Rectangle2D.Double leftSide = new Rectangle2D.Double( rect.x, rect.y, rect.width / 2, rect.height );
            return leftSide;
        }
        else if( index == 1 ) {
            Rectangle2D.Double rightSide = new Rectangle2D.Double( rect.x + rect.width / 2, rect.y, rect.width / 2, rect.height );
            return rightSide;
        }
        throw new RuntimeException( "No such case." );
    }

    public double getElectricForceV1( BandParticle bp ) {
        EnergyCell cell = bp.getEnergyCell();
        if( numBandSets() == 1 ) {
            return getVoltage();
        }
        else if( numBandSets() == 2 ) {
            return ElectricFieldSection.toVoltStrength( getVoltage() ) + electricFieldSectionAt( 0 ).getInternalField().getStrength();
        }
        else if( numBandSets() == 3 ) {
            int column = cell.getColumn();
            if( column <= 2 ) {
                return ElectricFieldSection.toVoltStrength( getVoltage() ) + electricFieldSectionAt( 0 ).getInternalField().getStrength();
            }
            else if( column > 2 ) {
                return ElectricFieldSection.toVoltStrength( getVoltage() ) + electricFieldSectionAt( 1 ).getInternalField().getStrength();
            }
        }
        throw new RuntimeException( "No case found." );
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
//            EnergyCell asrc = bandSetAt( 1 ).bandAt( 2 ).energyLevelAt( 1 ).cellAt( 0 );
//            moveLeft1Down2( asrc );
//            EnergyCell bsrc = bandSetAt( 1 ).bandAt( 2 ).energyLevelAt( 1 ).cellAt( 1 );
//            moveRight1Down2( bsrc );
        }

    }

    class NPNSetup implements InternalBiasSetup {
        public boolean isValid( EnergySection energySection ) {
            return energySection.isNPN();
        }

        public void apply( EnergySection energySection ) {
//            EnergyCell asrc = bandSetAt( 0 ).bandAt( 2 ).energyLevelAt( 1 ).cellAt( 1 );
//            moveRight1Down2( asrc );
//            EnergyCell bsrc = bandSetAt( 2 ).bandAt( 2 ).energyLevelAt( 1 ).cellAt( 0 );
//            moveLeft1Down2( bsrc );
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
            for( int i = 0; i < biases.size(); i++ ) {
                InternalBiasSetup internalBiasSetup = (InternalBiasSetup)biases.get( i );
                if( internalBiasSetup.isValid( EnergySection.this ) ) {
                    internalBiasSetup.apply( EnergySection.this );
                    break;
                }
            }
        }
    }


}
