package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.buildanatom.BuildAnAtomApplication;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.view.SimpleAtom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class represents that atom in the model.  It supplies static
 * information such as the position of the atom, as well as dynamic
 * information such as the number of protons present.  This class contains and
 * tracks instances of subatomic particles, rather than just the numbers of
 * such particles.
 *
 * @author John Blanco
 * @author Sam Reid
 * @author Kevin Bacon
 */
public class Atom extends SimpleAtom {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static Random RAND = new Random();

    // Nuclear radius, in picometers.  This is not to scale - we need it
    // to be larger than real life.
    private static final double NUCLEUS_RADIUS = 10;

    // Electron shell radii.
    public static final double ELECTRON_SHELL_1_RADIUS = 34;
    public static final double ELECTRON_SHELL_2_RADIUS = 102;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // Position in model space.
    private final Point2D position = new Point2D.Double();

    //The total translation of all nucleons, used for animating instability
    private ImmutableVector2D unstableNucleusJitterVector = new Vector2D( 0, 0 );

    // List of the subatomic particles that are currently in the nucleus.
    // Note that the electrons are maintained in the shells.
    public final ArrayList<Proton> protons = new ArrayList<Proton>();
    public final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // Shells for containing electrons.
    private final ElectronShell electronShell1 = new ElectronShell( ELECTRON_SHELL_1_RADIUS, 2 );
    private final ElectronShell electronShell2 = new ElectronShell( ELECTRON_SHELL_2_RADIUS, 8 );

    // Observer for electron shells.
    private final SimpleObserver electronShellChangeObserver = new SimpleObserver() {
        public void update() {
            checkAndReconfigureShells();
            updateElectronCount();
        }
    };

    // Repository from which to draw particles if told to add them non-
    // specifically, i.e. "addProton()" instead of "addProton( Proton )".
    private final SubatomicParticleRepository subatomicParticleRepository = new NullSubatomicParticleRepository();

    // Used for animating the unstable nuclei.
    private int animationCount = 0;
    private boolean isAway = false;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor that creates an empty atom, i.e. one that initially
     * contains no protons, neutrons, or electrons.
     */
    public Atom( Point2D position, BuildAnAtomClock clock ) {
        this.position.setLocation( position );

        //Only need to listen for 'removal' notifications on the inner shell
        //to decide when an outer electron should fall
        electronShell1.addObserver( electronShellChangeObserver );

        // Need to watch for changes to the number of electrons.
        final SimpleObserver electronChangeAdapter = new SimpleObserver() {
            public void update() {
                setNumElectrons( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
            }
        };
        electronShell1.addObserver( electronChangeAdapter );
        electronShell2.addObserver( electronChangeAdapter );
        notifyObservers();

        clock.addClockListener( new ClockAdapter() {

            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }

        } );
    }

    /**
     * Constructor that creates an atom that is initially configured as
     * specified in the supplied atom configuration.  Creates subatomic
     * particles as needed.
     */
    public Atom( Point2D position, BuildAnAtomClock clock, AtomValue initialConfiguration ) {
        this( position, clock );
        for ( int i = 0; i < initialConfiguration.getNumElectrons(); i++ ) {
            addElectron( new Electron( clock ), true );
        }
        for ( int i = 0; i < initialConfiguration.getNumProtons(); i++ ) {
            addProton( new Proton( clock ), true );
        }
        for ( int i = 0; i < initialConfiguration.getNumNeutrons(); i++ ) {
            addNeutron( new Neutron( clock ), true );
        }
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void stepInTime( double simulationTimeChange ) {
        animationCount++;
        if ( BuildAnAtomApplication.animateUnstableNucleusProperty.getValue() && !isStable() && !isAway && animationCount % 2 == 0 ) {
            // Jump away from the current location.
            unstableNucleusJitterVector = Vector2D.parseAngleAndMagnitude( RAND.nextDouble() * 5, RAND.nextDouble() * Math.PI * 2 );
            jumpAway();
        }
        else if ( ( animationCount % 2 == 0 && isAway ) || ( isStable() && isAway ) ) {
            jumpBack();
        }
    }

    /**
     * Move the nucleus away from its current location.  Usually this is
     * used in alternation with jumpBack.
     */
    private void jumpAway() {
        translateNucleons( unstableNucleusJitterVector, true );
    }

    /**
     * Move the nucleus back to the original location (with acknowledgments
     * to Kevin Bacon).
     */
    private void jumpBack() {
        translateNucleons( unstableNucleusJitterVector.getScaledInstance( -1 ), false );
    }

    private void translateNucleons( ImmutableVector2D motionVector, boolean away ) {
        for ( SubatomicParticle nucleon : getNucleons() ) {
            nucleon.setPositionAndDestination( motionVector.getDestination( nucleon.getDestination() ) );
        }
        isAway = away;
    }

    /**
     * Remove the specified nucleon from the nucleus of the atom.  Returns the
     * removed particle if it is found in the nucleus, and null if not.
     */
    public SubatomicParticle removeNucleon( SubatomicParticle particle ) {
        assert !( particle instanceof Electron ); // This method cannot be used to remove electrons.
        boolean particleFound = false;
        if ( particle instanceof Proton && protons.contains( particle ) ) {
            particleFound = true;
            protons.remove( particle );
            super.setNumProtons( protons.size() );
        }
        else if ( particle instanceof Neutron && neutrons.contains( particle ) ) {
            particleFound = true;
            neutrons.remove( particle );
            super.setNumNeutrons( neutrons.size() );
        }
        particle.removeListener( nucleonGrabbedListener );
        reconfigureNucleus( false );
        return particleFound ? particle : null;
    }

    /**
     * Remove a specific proton.
     */
    public SubatomicParticle removeProton( Proton proton ) {
        boolean found = protons.remove( proton );
        super.setNumProtons( protons.size() );
        proton.removeListener( nucleonGrabbedListener );
        reconfigureNucleus( false );
        return found ? proton : null;
    }

    /**
     * Remove an arbitrary proton.
     */
    public SubatomicParticle removeProton() {
        assert protons.size() > 0;
        return removeProton( protons.get( 0  ) );
    }

    /**
     * Remove a specific neutron.
     */
    public SubatomicParticle removeNeutron( Neutron neutron ){
        boolean found = neutrons.remove( neutron );
        super.setNumNeutrons( neutrons.size() );
        neutron.removeListener( nucleonGrabbedListener );
        reconfigureNucleus( false );
        return found ? neutron : null;
    }

    /**
     * Remove an arbitrary neutron.
     */
    public SubatomicParticle removeNeutron() {
        assert neutrons.size() > 0;
        return removeNeutron( neutrons.get( 0 ) );
    }

    /**
     * Remove a specific electron.
     *
     * @param electron
     * @return
     */
    public Electron removeElectron( Electron electron ){
        Electron removedElectron = null;
        if ( electronShell1.containsElectron( electron )){
            removedElectron = electronShell1.removeElectron( electron );
        }
        else if ( electronShell2.containsElectron( electron )){
            removedElectron = electronShell1.removeElectron( electron );
        }
        super.setNumElectrons( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
        return removedElectron;
    }

    /**
     * Remove an arbitrary electron.
     */
    public Electron removeElectron() {
        Electron removedElectron = null;
        if ( electronShell2.getNumElectrons() > 0 ) {
            removedElectron = electronShell2.removeElectron();
        }
        else {
            removedElectron = electronShell1.removeElectron();
        }
        super.setNumElectrons( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
        return removedElectron;
    }

    /**
     * Check if the shells need to be reconfigured.  This can be necessary
     * if an electron was removed from shell 1 while there were electrons
     * present in shell 2.
     */
    private void checkAndReconfigureShells() {
        checkAndReconfigureShells( electronShell1, electronShell2 );
    }

    private void updateElectronCount() {
        // Update the count of electrons maintained in the super class.  This
        // will cause any needed change notifications to be sent out.
        super.setNumElectrons( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
    }

    private void checkAndReconfigureShells( ElectronShell inner, ElectronShell outer ) {
        if ( !inner.isFull() && !outer.isEmpty() ) {

            // Need to move an electron from shell 2 to shell 1.
            ArrayList<Point2D> openLocations = inner.getOpenShellLocations();

            // We expect there to be one and only one open location, so test that this is true.
            assert openLocations.size() == 1;

            // Get the electron that is nearest to this location in shell 2
            // and move it to shell 1.
            Electron electronToMove = outer.getClosestElectron( openLocations.get( 0 ) );

            outer.removeElectron( electronToMove );
            inner.addElectron( electronToMove, false );
        }
    }

    @Override
    public void reset() {
        for ( Proton proton : protons ) {
            proton.removeListener( nucleonGrabbedListener );
        }
        for ( Neutron neutron : neutrons ) {
            neutron.removeListener( nucleonGrabbedListener );
        }
        protons.clear();
        neutrons.clear();
        // It is important to reset the electron shells from the outside in so
        // that we don't end up with electrons trying to migrate from outer to
        // inner shells during reset.
        electronShell2.reset();
        electronShell1.reset();

        // Call into the base class.  This will ultimately cause any resulting
        // change notifications to be sent out.
        super.reset();
    }

    public ArrayList<ElectronShell> getElectronShells() {
        ArrayList<ElectronShell> electronShells = new ArrayList<ElectronShell>();
        electronShells.add( electronShell1 );
        electronShells.add( electronShell2 );
        return electronShells;
    }

    public int getRemainingElectronCapacity() {
        return electronShell1.getNumOpenLocations() + electronShell2.getNumOpenLocations();
    }

    public double getNucleusRadius() {
        return NUCLEUS_RADIUS;
    }

    public Point2D getPosition() {
        return position;
    }

    /**
     * Compare this atom's configuration with the supplied atom value and
     * return true if they match and false if not.  Note that the
     * configuration means only the quantity of the various subatomic
     * particles.
     *
     * @param atomValue
     * @return
     */
    public boolean equals( AtomValue atomValue ){
        return getNumProtons() == atomValue.getNumProtons() &&
               getNumNeutrons() == atomValue.getNumNeutrons() &&
               getNumElectrons() == atomValue.getNumElectrons();

    }

    public void addProton( final Proton proton, boolean moveImmediately ) {
        assert !protons.contains( proton );

        // Add to the list of protons that are in the atom.
        protons.add( proton );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reconfigureNucleus( moveImmediately );

        proton.addListener( nucleonGrabbedListener );

        // Update count in super class.  This sends out the change notification.
        super.setNumProtons( protons.size() );
    }

    @Override
    public void setNumElectrons( int numElectrons ) {
        int numElectronsInShells = electronShell1.getNumElectrons() + electronShell2.getNumElectrons();
        if ( numElectrons > numElectronsInShells ) {
            // Attempt to get electrons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numElectrons - numElectronsInShells; i++ ) {
                Electron electron = subatomicParticleRepository.getElectron();
                if ( electron != null ) {
                    addElectron( electron, true );
                }
                else {
                    assert false;
                    System.err.println( "Error: Not enough electrons available to allow set operation to succeed." );
                    continue;
                }
            }
        }
        else if ( numElectrons < numElectronsInShells ) {
            // Move electrons to the repository until the numbers match.
            for ( int i = 0; i < numElectronsInShells - numElectrons; i++ ) {
                subatomicParticleRepository.addElectron( removeElectron() );
            }
        }
        super.setNumElectrons( numElectrons );
    }

    @Override
    public void setNumNeutrons( int numNeutrons ) {
        if ( numNeutrons > neutrons.size() ) {
            // Attempt to get neutrons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numNeutrons - neutrons.size(); i++ ) {
                Neutron neutron = subatomicParticleRepository.getNeutron();
                if ( neutron != null ) {
                    addNeutron( neutron, true );
                }
                else {
                    assert false;
                    System.err.println( "Error: Not enough neutrons available to allow set operation to succeed." );
                    continue;
                }
            }
        }
        super.setNumNeutrons( numNeutrons );
    }

    @Override
    public void setNumProtons( int numProtons ) {
        if ( numProtons > protons.size() ) {
            // Attempt to get protons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numProtons - protons.size(); i++ ) {
                Neutron neutron = subatomicParticleRepository.getNeutron();
                if ( neutron != null ) {
                    addNeutron( neutron, true );
                }
                else {
                    assert false;
                    System.err.println( "Error: Not enough protons available to allow set operation to succeed." );
                    continue;
                }
            }
        }
        super.setNumProtons( numProtons );
    }

    public void addNeutron( final Neutron neutron, boolean moveImmediately ) {
        assert !neutrons.contains( neutron );

        // Add to the list of neutrons that are in the atom.
        neutrons.add( neutron );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reconfigureNucleus( moveImmediately );

        neutron.addListener( nucleonGrabbedListener );

        // Update count in super class.  This sends out the change notification.
        super.setNumNeutrons( neutrons.size() );
    }

    public void addElectron( final Electron electron, boolean moveImmediately ) {
        if ( !electronShell1.isFull() ) {
            electronShell1.addElectron( electron, moveImmediately );
        }
        else if ( !electronShell2.isFull() ) {
            electronShell2.addElectron( electron, moveImmediately );
        }
        else {
            // Too many electrons.  The sim should be designed such that this
            // does not occur.  If it does, it should be debugged.
            assert false;
        }

        // Update count in super class.  This sends out the change notification.
        updateElectronCount();
    }

    /**
     * Distribute the nucleons in the nucleus in such a way that the nucleus
     * will look good when shown in the view.
     */
    public void reconfigureNucleus( boolean moveImmediately ) {

        if ( isAway ) {
            // This is necessary to keep things from getting off when
            // animation of the unstable nucleus is occurring at the same
            // time as a nucleus reconfiguration.
            jumpBack();
        }
        double nucleonRadius = Proton.RADIUS;

        // Get all the nucleons onto one list.  Add them alternately so that
        // they don't get clustered together by type when distributed in the
        // nucleus.
        final ArrayList<SubatomicParticle> nucleons = getNucleons();

        if ( nucleons.size() == 0 ) {
            // Nothing to do.
            return;
        }
        else if ( nucleons.size() == 1 ) {
            // There is only one nucleon present, so place it in the center
            // of the atom.
            nucleons.get( 0 ).setDestination( getPosition() );
        }
        else if ( nucleons.size() == 2 ) {
            // Two nucleons - place them side by side with their meeting point in the center.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            nucleons.get( 0 ).setDestination( nucleonRadius * Math.cos( angle ), nucleonRadius * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
        }
        else if ( nucleons.size() == 3 ) {
            // Three nucleons - form a triangle where they all touch.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            double distFromCenter = nucleonRadius * 1.155;
            nucleons.get( 0 ).setDestination( distFromCenter * Math.cos( angle ), distFromCenter * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( distFromCenter * Math.cos( angle + 2 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 2 * Math.PI / 3 ) );
            nucleons.get( 2 ).setDestination( distFromCenter * Math.cos( angle + 4 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 4 * Math.PI / 3 ) );
        }
        else if ( nucleons.size() == 4 ) {
            // Four nucleons - make a sort of diamond shape with some overlap.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            nucleons.get( 0 ).setDestination( nucleonRadius * Math.cos( angle ), nucleonRadius * Math.sin( angle ) );
            nucleons.get( 2 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
            double distFromCenter = nucleonRadius * 2 * Math.cos( Math.PI / 3 );
            nucleons.get( 1 ).setDestination( distFromCenter * Math.cos( angle + Math.PI / 2 ),
                    distFromCenter * Math.sin( angle + Math.PI / 2 ) );
            nucleons.get( 3 ).setDestination( -distFromCenter * Math.cos( angle + Math.PI / 2 ),
                    -distFromCenter * Math.sin( angle + Math.PI / 2 ) );
        }
        else if ( nucleons.size() >= 5 ) {
            // This is a generalized algorithm that should work for five or
            // more nucleons.
            double placementRadius = 0;
            int numAtThisRadius = 1;
            int level = 0;
            double placementAngle = 0;
            double placementAngleDelta = 0;
            for ( int i = 0; i < nucleons.size(); i++ ) {
                nucleons.get( i ).setDestination( placementRadius * Math.cos( placementAngle ),
                        placementRadius * Math.sin( placementAngle ) );
                numAtThisRadius--;
                if ( numAtThisRadius > 0 ) {
                    // Stay at the same radius and update the placement angle.
                    placementAngle += placementAngleDelta;
                }
                else {
                    // Move out to the next radius.
                    level++;
                    placementRadius += nucleonRadius * 1.35 / level;
                    placementAngle += Math.PI / 8; // Arbitrary value chosen based on looks.
                    numAtThisRadius = (int) Math.floor( placementRadius * Math.PI / nucleonRadius );
                    placementAngleDelta = 2 * Math.PI / numAtThisRadius;
                }
            }

            //WARNING: THIS IS A SPECIAL CASE FOR HANDLING A CERTAIN ISOTOPE OF LITHIUM
            //Make this isotope of lithium look better, some of the neutrons overlap
            //too much for discerning in the game mode
            if ( nucleons.size() == 7 && neutrons.size() == 4 ) {
                final Neutron neutron = neutrons.get( neutrons.size() - 1 );
                neutron.setDestination( neutron.getDestination().getX(), neutron.getDestination().getY() - 3 );
            }
        }

        //If the particles shouldn't be animating, they should immediately move to their destination
        if ( moveImmediately ) {
            for ( SubatomicParticle nucleon : nucleons ) {
                nucleon.moveToDestination();
            }
        }
    }

    private ArrayList<SubatomicParticle> getNucleons() {
        final ArrayList<SubatomicParticle> nucleons = new ArrayList<SubatomicParticle>();
        for ( int i = 0; i < Math.max( protons.size(), neutrons.size() ); i++ ) {
            if ( i < protons.size() ) {
                nucleons.add( protons.get( i ) );
            }
            if ( i < neutrons.size() ) {
                nucleons.add( neutrons.get( i ) );
            }
        }
        return nucleons;
    }

    public ArrayList<Electron> getElectrons() {
        ArrayList<Electron> allElectrons = new ArrayList<Electron>();
        allElectrons.addAll( electronShell1.getElectrons() );
        allElectrons.addAll( electronShell2.getElectrons() );
        return allElectrons;
    }

    public boolean containsElectron( Electron electron ) {
        return electronShell1.containsElectron( electron ) || electronShell2.containsElectron( electron );
    }

    public ArrayList<Proton> getProtons() {
        return protons;
    }

    public ArrayList<Neutron> getNeutrons() {
        return neutrons;
    }

    //For the game mode
    public ArrayList<SubatomicParticle> setState( AtomValue answer, BuildAnAtomModel model, boolean moveImmediately ) {//provide the model to draw free particles from
        ArrayList<SubatomicParticle> removedParticles = new ArrayList<SubatomicParticle>();
        while ( getNumProtons() > answer.getNumProtons() ) {
            removedParticles.add( removeProton() );
        }
        while ( getNumProtons() < answer.getNumProtons() ) {
            addProton( model.getFreeProton(), moveImmediately );
        }

        while ( getNumNeutrons() > answer.getNumNeutrons() ) {
            removedParticles.add( removeNeutron() );
        }
        while ( getNumNeutrons() < answer.getNumNeutrons() ) {
            addNeutron( model.getFreeNeutron(), moveImmediately );
        }

        while ( getNumElectrons() > answer.getNumElectrons() ) {
            removedParticles.add( removeElectron() );
        }
        while ( getNumElectrons() < answer.getNumElectrons() ) {
            addElectron( model.getFreeElectron(), moveImmediately );
        }
        return removedParticles;
    }

    public boolean containsProton( Proton proton ) {
        return protons.contains( proton );
    }

    public boolean containsNeutron( Neutron neutron ) {
        return neutrons.contains( neutron );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    protected final SubatomicParticle.Adapter nucleonGrabbedListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( SubatomicParticle particle ) {
            // The user has picked up this particle, which instantly
            // removes it from the nucleus.
            removeNucleon( particle );
        }
    };
}