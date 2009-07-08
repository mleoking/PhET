/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.model;

import java.util.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.persistence.BunnyConfig;
import edu.colorado.phet.naturalselection.persistence.NaturalSelectionConfig;

/**
 * The model itself for the Natural Selection simulation
 *
 * @author Jonathan Olson
 */
public class NaturalSelectionModel extends ClockAdapter {

    // types of climates
    public static final int CLIMATE_EQUATOR = 0;
    public static final int CLIMATE_ARCTIC = 1;

    // types of selection factors
    public static final int SELECTION_NONE = 0;
    public static final int SELECTION_FOOD = 1;
    public static final int SELECTION_WOLVES = 2;

    /**
     * The simulation clock
     */
    private NaturalSelectionClock clock;

    /**
     * Frenzy object
     */
    private Frenzy frenzy;

    /**
     * A list of all of the (model) bunnies.
     * WARNING: do NOT change the order of this list
     */
    private ArrayList<Bunny> bunnies;
    // starting bunnies
    private Bunny rootFather;

    private Bunny rootMother;

    private List<Shrub> shrubs;
    private List<Tree> trees;

    private ArrayList<Listener> listeners;

    private double time = 0;

    /**
     * the last tick at which a year changed. When the difference between this and the actual tick is large enough,
     * another year will change
     */
    private double lastYearTick = 0;

    private double lastEventTick = NaturalSelectionConstants.getSettings().getSelectionTick();

    /**
     * The current generation
     */
    private int generation = 0;

    private boolean gameEnded = false;

    private int lastFrenziedGeneration = -1;

    /**
     * Whether the user has pressed "Add a friend" yet
     */
    private boolean friendAdded = false;

    /**
     * The current climate (CLIMATE_EQUATOR or CLIMATE_ARCTIC)
     */
    private int climate = CLIMATE_EQUATOR;

    /**
     * The current selection factor (SELECTION_NONE, SELECTION_FOOD, or SELECTION_WOLVES)
     */
    private int selectionFactor = SELECTION_NONE;

    private Landscape landscape;

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Constructor
     *
     * @param clock The simulation clock
     */
    public NaturalSelectionModel( NaturalSelectionClock clock ) {

        this.clock = clock;

        landscape = new Landscape();
        bunnies = new ArrayList<Bunny>();
        listeners = new ArrayList<Listener>();

        // set up the genes to this module
        ColorGene.getInstance().setModel( this );
        TeethGene.getInstance().setModel( this );
        TailGene.getInstance().setModel( this );

        // create the starting bunnies
        rootFather = new Bunny( this, null, null, 0 );
        rootFather.notifyInit();
        bunnies.add( rootFather );

        this.clock.addClockListener( this );

        shrubs = new LinkedList<Shrub>();
        shrubs.add( new Shrub( this, 80, 330, 1 ) );
        shrubs.add( new Shrub( this, 750, 200, 0.8 ) );
        shrubs.add( new Shrub( this, 320, 130, 0.6 ) );

        trees = new LinkedList<Tree>();
        trees.add( new Tree( this, 125, 138, 1 ) );
        trees.add( new Tree( this, 917, 115, 0.7 ) );
        trees.add( new Tree( this, 635, 90, 0.2 ) );

        if ( !NaturalSelectionConstants.getSettings().isClockStartRunning() ) {
            this.clock.stop();
        }
    }

    /**
     * Reset the entire model
     */
    public void reset() {

        if ( isDuringFrenzy() ) {
            prematureEndFrenzy();
        }

        climate = NaturalSelectionDefaults.DEFAULT_CLIMATE;

        selectionFactor = NaturalSelectionDefaults.DEFAULT_SELECTION_FACTOR;

        friendAdded = false;

        generation = 0;

        time = 0;

        gameEnded = false;

        lastFrenziedGeneration = -1;

        lastYearTick = 0;

        lastEventTick = NaturalSelectionConstants.getSettings().getSelectionTick();

        frenzy = null;

        bunnies = new ArrayList<Bunny>();

        ColorGene.getInstance().reset();
        TailGene.getInstance().reset();
        TeethGene.getInstance().reset();

        rootFather = new Bunny( this, null, null, 0 );
        rootFather.notifyInit();
        bunnies.add( rootFather );

        clock.resetSimulationTime();

        if ( NaturalSelectionConstants.getSettings().isClockStartRunning() ) {
            clock.start();
        }
        else {
            clock.stop();
        }

        notifyGenerationChange();

        initialize();
    }

    public void save( NaturalSelectionConfig config ) {
        NaturalSelectionClock clock = getClock();
        config.setClockDt( clock.getDt() );
        config.setClockPaused( clock.isPaused() );
        config.setClockTime( clock.getSimulationTime() );
        config.setClimate( climate );
        config.setSelectionFactor( selectionFactor );
        config.setTime( time );
        config.setLastYearTick( lastYearTick );
        config.setLastEventTick( lastEventTick );
        config.setGeneration( generation );
        config.setGameEnded( gameEnded );
        config.setLastFrenziedGeneration( lastFrenziedGeneration );
        config.setFriendAdded( friendAdded );

        config.setRootFatherId( rootFather.bunnyId );
        if ( rootMother == null ) {
            config.setRootMotherId( -1 );
        }
        else {
            config.setRootMotherId( rootMother.bunnyId );
        }

        BunnyConfig[] bunnyConfigs = new BunnyConfig[bunnies.size()];
        for ( int i = 0; i < bunnyConfigs.length; i++ ) {
            bunnyConfigs[i] = bunnies.get( i ).getConfig();
        }
        config.setBunnies( bunnyConfigs );

        config.setColorRegularDominant( ColorGene.getInstance().getDominantAllele() == ColorGene.WHITE_ALLELE );
        config.setTeethRegularDominant( TeethGene.getInstance().getDominantAllele() == TeethGene.TEETH_SHORT_ALLELE );
        config.setTailRegularDominant( TailGene.getInstance().getDominantAllele() == TailGene.TAIL_SHORT_ALLELE );


    }

    public void load( NaturalSelectionConfig config ) {
        clock.setDt( config.getClockDt() );
        clock.setPaused( config.isClockPaused() );
        clock.setSimulationTime( config.getClockTime() );
        setClimate( config.getClimate() );
        setSelectionFactor( config.getSelectionFactor() );
        time = config.getTime();
        lastYearTick = config.getLastYearTick();
        lastEventTick = config.getLastEventTick();
        generation = config.getGeneration();
        gameEnded = config.isGameEnded();
        lastFrenziedGeneration = config.getLastFrenziedGeneration();
        friendAdded = config.isFriendAdded();

        if ( config.isColorRegularDominant() ) {
            ColorGene.getInstance().setDominantAllele( ColorGene.WHITE_ALLELE );
        }
        else {
            ColorGene.getInstance().setDominantAllele( ColorGene.BROWN_ALLELE );
        }

        if ( config.isTeethRegularDominant() ) {
            TeethGene.getInstance().setDominantAllele( TeethGene.TEETH_SHORT_ALLELE );
        }
        else {
            TeethGene.getInstance().setDominantAllele( TeethGene.TEETH_LONG_ALLELE );
        }

        if ( config.isTailRegularDominant() ) {
            TailGene.getInstance().setDominantAllele( TailGene.TAIL_SHORT_ALLELE );
        }
        else {
            TailGene.getInstance().setDominantAllele( TailGene.TAIL_LONG_ALLELE );
        }

        for ( BunnyConfig bunnyConfig : config.getBunnies() ) {
            addBunnyConfig( bunnyConfig, config );
        }
    }

    public Bunny getBunnyById( int id ) {
        for ( Bunny bunny : bunnies ) {
            if ( bunny.bunnyId == id ) {
                return bunny;
            }
        }
        return null;
    }

    public void addBunnyConfig( BunnyConfig config, NaturalSelectionConfig mainConfig ) {
        /*
        if ( config.getId() == mainConfig.getRootFatherId() ) {
            // don't add root father, he is already added
            return;
        }
        Bunny bunny = new Bunny( this, getBunnyById( config.getFatherId() ), getBunnyById( config.getMotherId() ), config.getGeneration() );
        bunny.notifyInit();
        Bunny potentialMate = getBunnyById( config.getPotentialMateId() );
        if( potentialMate != null ) {
            bunny.setPotentialMate( potentialMate );
            potentialMate.setPotentialMate( bunny );
        }
        if( mainConfig.getRootMotherId() == config.getId() ) {
            rootMother = bunny;
        }

        if( bunny.bunnyId != config.getId() ) {
            System.out.println( "WARNING: bunny IDs do not match!" );
        }

        bunnies.add( bunny );
        notifyNewBunny( bunny );
        */
    }

    public void initialize() {
        notifyNewBunny( rootFather );
    }

    public void addFriend() {
        // reset time variables
        time = 0;
        lastYearTick = 0;
        lastEventTick = NaturalSelectionConstants.getSettings().getTicksPerYear() / 4;

        friendAdded = true;
        rootMother = new Bunny( this, null, null, 0 );
        rootMother.notifyInit();
        rootFather.setPotentialMate( rootMother );
        rootMother.setPotentialMate( rootFather );
        bunnies.add( rootMother );
        notifyNewBunny( rootMother );
    }

    /**
     * Causes another generation to be born (and everything else that happens when a new generation occurs)
     */
    private void nextGeneration() {
        ageBunnies();

        if ( !friendAdded ) {
            int prePop = getPopulation();
            if ( prePop == 0 ) {
                endGame();
            }
            else if ( prePop > NaturalSelectionConstants.getSettings().getMaxPopulation() ) {
                bunniesTakeOver();
            }
            else {
                //System.out.println( "Nothing to do, friend has not been added" );
            }
            return;
        }

        mateBunnies();

        generation++;
        notifyGenerationChange();

        // make sure genes won't mutate anymore
        ColorGene.getInstance().setMutatable( false );
        TailGene.getInstance().setMutatable( false );
        TeethGene.getInstance().setMutatable( false );

        int pop = getPopulation();
        if ( pop == 0 ) {
            endGame();
        }
        else if ( pop > NaturalSelectionConstants.getSettings().getMaxPopulation() ) {
            bunniesTakeOver();
        }
        else {
            //System.out.println( "Population: " + pop );
        }
    }

    public void bunniesTakeOver() {
        if ( gameEnded ) {
            // game already ended!
            return;
        }
        gameEnded = true;
        clock.pause();
        notifyBunniesTakeOver();
    }

    public void endGame() {
        if ( gameEnded ) {
            // game already ended!
            return;
        }
        gameEnded = true;
        clock.pause();
        notifyGameOver();
    }

    /**
     * Causes all bunnies that can reproduce to do so
     */
    private void mateBunnies() {
        List<Bunny> aliveBunnies = getAliveBunnyList();
        Collections.shuffle( aliveBunnies );

        List<Bunny> newBunnies = new LinkedList<Bunny>();
        // we will, for now, ignore anything about bunnies!
        Bunny prev = null;
        for ( Bunny bunny : aliveBunnies ) {
            if ( prev == null ) {
                prev = bunny;
                continue;
            }
            Bunny[] offspring = Bunny.mateBunnies( prev, bunny, generation + 1 );
            for ( int i = 0; i < offspring.length; i++ ) {
                Bunny child = offspring[i];
                newBunnies.add( child );
            }
            prev = null;
        }
        mutateSomeBunnies( newBunnies );
        for ( Bunny bunny : newBunnies ) {
            bunny.notifyInit();
            bunnies.add( bunny );
            notifyNewBunny( bunny );
        }
    }

    private void mutateSomeBunnies( List<Bunny> newBunnies ) {
        Gene gene = null;
        Allele allele = null;

        if ( ColorGene.getInstance().getMutatable() ) {
            gene = ColorGene.getInstance();
            allele = ColorGene.BROWN_ALLELE;
        }
        else if ( TailGene.getInstance().getMutatable() ) {
            gene = TailGene.getInstance();
            allele = TailGene.TAIL_LONG_ALLELE;
        }
        else if ( TeethGene.getInstance().getMutatable() ) {
            gene = TeethGene.getInstance();
            allele = TeethGene.TEETH_LONG_ALLELE;
        }
        else {
            // don't mutate anything!
            return;
        }

        // we only want to check bunnes that haven't mutated already
        ArrayList<Bunny> possibleBunnies = new ArrayList<Bunny>();

        Iterator<Bunny> iter = newBunnies.iterator();

        while ( iter.hasNext() ) {
            Bunny bunny = iter.next();

            if ( gene.getBunnyPhenotype( bunny ) != allele ) {
                possibleBunnies.add( bunny );
            }
        }

        if ( possibleBunnies.size() == 0 ) {
            // they are all mutated to that already (but may carry.... hrmm!)
            return;
        }

        int base = NaturalSelectionConstants.getSettings().getMutatingBunnyBase();
        int perBunnies = NaturalSelectionConstants.getSettings().getMutatingBunnyPerBunnies();
        int bunniesToMutate = base + possibleBunnies.size() / perBunnies;

        for ( int i = 0; i < bunniesToMutate; i++ ) {
            if ( possibleBunnies.isEmpty() ) {
                break;
            }

            Bunny mutant = possibleBunnies.get( random.nextInt( possibleBunnies.size() ) );

            possibleBunnies.remove( mutant );

            mutant.mutateGene( gene, allele );
        }

    }

    public void killAllBunnies() {
        for ( Bunny bunny : bunnies ) {
            if ( bunny.isAlive() ) {
                bunny.die();
            }
        }
    }

    /**
     * Make all of the bunnies older
     */
    private void ageBunnies() {
        Iterator<Bunny> iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            ( iter.next() ).ageMe();
        }
    }

    /**
     * Bunnies will run out of food if the selection factor is food
     */
    private void bunnyFamine() {
        double bunnyOffset = NaturalSelectionConstants.getSettings().getFoodSelectionBunnyOffset();
        double bunnyExponent = NaturalSelectionConstants.getSettings().getFoodSelectionBunnyExponent();
        double scale = NaturalSelectionConstants.getSettings().getFoodSelectionScale();
        double blendScale = NaturalSelectionConstants.getSettings().getFoodSelectionBlendScale();
        double maxKillFraction = NaturalSelectionConstants.getSettings().getMaxKillFraction();

        Iterator<Bunny> iter = bunnies.iterator();

        //double baseFraction = ( Math.sqrt( (double) getPopulation() ) - 3 ) / ( 4 );
        double baseFraction = ( Math.pow( (double) getPopulation() + bunnyOffset, bunnyExponent ) ) * scale;

        while ( iter.hasNext() ) {
            Bunny bunny = iter.next();

            if ( !bunny.isAlive() ) {
                continue;
            }

            double actualFraction = baseFraction;

            if ( bunny.getTeethPhenotype() == TeethGene.TEETH_LONG_ALLELE ) {
                actualFraction *= blendScale;
            }

            if ( actualFraction > maxKillFraction ) {
                actualFraction = maxKillFraction;
            }

            if ( Math.random() < actualFraction ) {
                bunny.die();
            }

        }
    }

    public void startFrenzy() {
        if ( isDuringFrenzy() ) {
            // this is fine now, since frenzies start automatically when switched to wolves
            return;
            //throw new RuntimeException( "Already frenzying" );
        }

        if ( getPopulation() == 0 ) {
            return;
        }

        if ( lastFrenziedGeneration == generation ) {
            // don't frenzy twice in the same generation
            return;
        }

        lastFrenziedGeneration = generation;

        //System.out.println( "Starting frenzy" );

        frenzy = new Frenzy( this, NaturalSelectionConstants.getSettings().getFrenzyTicks() ); // TODO: work on time stuff!

        // so listeners can listen to the frenzy
        notifyFrenzyStart( frenzy );

        // now create wolves, so all listeners hear the wolf creations
        frenzy.init();
    }

    public void prematureEndFrenzy() {
        if ( frenzy != null && frenzy.isRunning() ) {
            frenzy.endFrenzy();
        }

        // sanity check, Frenzy should call this endFrenzy below.
        frenzy = null;
    }

    public void endFrenzy() {
        //System.out.println( "Ending frenzy" );
        frenzy = null;
    }

    public boolean isDuringFrenzy() {
        return frenzy != null;
    }

    //----------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------

    public NaturalSelectionClock getClock() {
        return clock;
    }

    public Frenzy getFrenzy() {
        return frenzy;
    }

    public int getPopulation() {
        // TODO: easier way? maybe count a filtered ArrayList?
        int ret = 0;
        for ( int i = 0; i < bunnies.size(); i++ ) {
            Bunny bunny = bunnies.get( i );
            if ( bunny.isAlive() ) {
                ret++;
            }
        }
        return ret;
    }

    public int getGeneration() {
        return generation;
    }

    public ArrayList<Bunny> getBunnyList() {
        return bunnies;
    }

    /**
     * Get only alive bunnies
     *
     * @return A list of bunnies that are alive
     */
    public List<Bunny> getAliveBunnyList() {
        LinkedList<Bunny> ret = new LinkedList<Bunny>();
        Iterator<Bunny> iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            Bunny bunny = iter.next();
            if ( bunny.isAlive() ) {
                ret.add( bunny );
            }
        }
        return ret;
    }

    /**
     * Get a list of all bunnies that were born in the desired generation
     *
     * @param desiredGeneration The generation that bunnies were born in.
     * @return A list of all bunnies (alive or dead) born in desiredGeneration
     */
    public ArrayList<Bunny> getBunnyGenerationList( int desiredGeneration ) {
        ArrayList<Bunny> ret = new ArrayList<Bunny>();
        Iterator<Bunny> iter = bunnies.iterator();
        while ( iter.hasNext() ) {
            Bunny bunny = iter.next();
            if ( bunny.getGeneration() == desiredGeneration ) {
                ret.add( bunny );
            }
        }
        return ret;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate( int _climate ) {
        if ( climate == _climate ) {
            return;
        }

        climate = _climate;

        notifyClimateChange();
    }

    public int getSelectionFactor() {
        return selectionFactor;
    }

    public void setSelectionFactor( int _selectionFactor ) {
        if ( selectionFactor == _selectionFactor ) {
            return;
        }

        selectionFactor = _selectionFactor;

        if ( selectionFactor == SELECTION_WOLVES ) {
            startFrenzy();
        }
        else {
            prematureEndFrenzy();
        }

        if ( selectionFactor == SELECTION_FOOD ) {
            bunnyFamine();
        }

        notifySelectionFactorChange();
    }

    public Bunny getRootFather() {
        return rootFather;
    }

    public Bunny getRootMother() {
        return rootMother;
    }

    public boolean isFriendAdded() {
        return friendAdded;
    }

    public int getGenerationProgressPercent() {
        if ( !friendAdded ) {
            return 0;
        }
        return (int) ( 100 * ( time - lastYearTick ) / NaturalSelectionConstants.getSettings().getTicksPerYear() );
    }

    public List<Shrub> getShrubs() {
        return shrubs;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public Landscape getLandscape() {
        return landscape;
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    public void simulationTimeChanged( ClockEvent event ) {

        double ticksPerYear = NaturalSelectionConstants.getSettings().getTicksPerYear();

        if ( !isDuringFrenzy() ) {
            time++;

            while ( time - lastYearTick > ticksPerYear ) {
                lastYearTick += ticksPerYear;
                nextGeneration();
            }

            while ( time - lastEventTick > ticksPerYear ) {
                lastEventTick += ticksPerYear;

                if ( selectionFactor == SELECTION_WOLVES ) {
                    startFrenzy();
                }

                if ( selectionFactor == SELECTION_FOOD ) {
                    bunnyFamine();
                }
            }
        }

        clock.notifyPhysicalListeners( event );

        if ( !isDuringFrenzy() ) {
            clock.notifyTimeListeners( event );
        }
    }


    //----------------------------------------------------------------------------
    // Notifications
    //----------------------------------------------------------------------------

    private void notifyGenerationChange() {
        Event event = new Event( this, Event.TYPE_NEW_GENERATION );
        event.setNewGeneration( generation );
        notifyListenersOfEvent( event );
    }

    private void notifyNewBunny( Bunny bunny ) {
        Event event = new Event( this, Event.TYPE_NEW_BUNNY );
        event.setNewBunny( bunny );
        notifyListenersOfEvent( event );
    }

    private void notifyClimateChange() {
        Event event = new Event( this, Event.TYPE_CLIMATE_CHANGE );
        event.setNewClimate( climate );
        notifyListenersOfEvent( event );
    }

    private void notifySelectionFactorChange() {
        Event event = new Event( this, Event.TYPE_SELECTION_CHANGE );
        event.setNewSelectionFactor( selectionFactor );
        notifyListenersOfEvent( event );
    }

    private void notifyFrenzyStart( Frenzy frenzy ) {
        Event event = new Event( this, Event.TYPE_FRENZY_START );
        event.setFrenzy( frenzy );
        notifyListenersOfEvent( event );
    }

    private void notifyListenersOfEvent( Event event ) {
        for ( Listener listener : listeners ) {
            listener.onEvent( event );
        }
    }

    private void notifyGameOver() {
        for ( Listener listener : listeners ) {
            listener.onEvent( new Event( this, Event.TYPE_GAME_OVER ) );
        }
    }

    private void notifyBunniesTakeOver() {
        for ( Listener listener : listeners ) {
            listener.onEvent( new Event( this, Event.TYPE_BUNNIES_TAKE_OVER ) );
        }
    }

    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    /**
     * The interface for objects that wish to receive model change events
     */
    public interface Listener {
        public void onEvent( Event event );
    }

    public class Event {
        public static final int TYPE_NEW_GENERATION = 0;
        public static final int TYPE_NEW_BUNNY = 1;
        public static final int TYPE_CLIMATE_CHANGE = 2;
        public static final int TYPE_SELECTION_CHANGE = 3;
        public static final int TYPE_FRENZY_START = 4;
        public static final int TYPE_GAME_OVER = 5;
        public static final int TYPE_BUNNIES_TAKE_OVER = 6;

        private NaturalSelectionModel model;
        private int type;

        private int newGeneration;
        private int newSelectionFactor;
        private int newClimate;
        private Bunny newBunny;
        private Frenzy frenzy;

        public Event( NaturalSelectionModel model, int type ) {
            this.model = model;
            this.type = type;
        }


        //----------------------------------------------------------------------------
        // Getters
        //----------------------------------------------------------------------------

        public NaturalSelectionModel getModel() {
            return model;
        }

        public int getType() {
            return type;
        }

        public Bunny getNewBunny() {
            return newBunny;
        }

        public int getNewGeneration() {
            return newGeneration;
        }

        public int getNewSelectionFactor() {
            return newSelectionFactor;
        }

        public int getNewClimate() {
            return newClimate;
        }

        public Frenzy getFrenzy() {
            return frenzy;
        }

        //----------------------------------------------------------------------------
        // Setters
        //----------------------------------------------------------------------------

        private void setNewBunny( Bunny bunny ) {
            newBunny = bunny;
        }

        private void setNewGeneration( int newGeneration ) {
            this.newGeneration = newGeneration;
        }

        private void setNewSelectionFactor( int newSelectionFactor ) {
            this.newSelectionFactor = newSelectionFactor;
        }

        private void setNewClimate( int newClimate ) {
            this.newClimate = newClimate;
        }

        private void setFrenzy( Frenzy frenzy ) {
            this.frenzy = frenzy;
        }
    }

}
