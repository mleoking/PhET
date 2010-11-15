package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.buildanatom.BuildAnAtomApplication;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.view.SimpleAtom;
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
 */
public class Atom extends SimpleAtom {

    private static Random RAND = new Random();

    // Nuclear radius, in picometers.  This is not to scale - we need it
    // to be larger than real life.
    private static final double NUCLEUS_RADIUS = 10;

    // Electron shell radii.
    public static final double ELECTRON_SHELL_1_RADIUS = 34;
    public static final double ELECTRON_SHELL_2_RADIUS = 102;

    // Position in model space.
    private final Point2D position = new Point2D.Double();

    //The total translation of all nucleons, used for animating instability
    private final Vector2D nucleusOffset = new Vector2D( 0, 0 );

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
        }
    };

    // Repository from which to draw particles if told to add them non-
    // specifically, i.e. "addProton()" instead of "addProton( Proton )".
    private SubatomicParticleRepository subatomicParticleRepository = new NullSubatomicParticleRepository();

    /**
     * Constructor.
     */
    public Atom( Point2D position, BuildAnAtomClock clock ) {
        this.position.setLocation( position );
        //Only need to listen for 'removal' notifications on the inner shell
        //to decide when an outer electron should fall
        electronShell1.addObserver( electronShellChangeObserver );

        //Need to notify our observers when the number of electrons changes
        final SimpleObserver electronChangeAdapter = new SimpleObserver() {
            public void update() {
                notifyObservers();
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
     * Constructor.  This version specifies a repository from which particles
     * may be drawn if methods are called that add particles generically, e.g.
     * "addProton()" or "setNumProtons(X)".
     */
    public Atom( Point2D position, BuildAnAtomClock clock, SubatomicParticleRepository subatomicParticleRepository ) {
        this( position, clock );
        this.subatomicParticleRepository = subatomicParticleRepository;
    }

    // TODO: This is a quick implementation of animation for unstable nuclei,
    // done for interviews.  If the animation feature is kept, this should be
    // reworked.
    int count = 0;
    Random random = new Random();

    protected final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
                @Override
                public void grabbedByUser( SubatomicParticle particle ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    removeParticle( particle );
                }
            };
    private void stepInTime( double simulationTimeChange ) {
        if ( BuildAnAtomApplication.animateUnstableNucleusProperty.getValue() && !isStable() ) {
            count++;
            if ( count % 4 == 0 ) {
                updateNucleonOffsets( -1 );
            }
            else if ( count % 2 == 0){
                nucleusOffset.setAngle( random.nextDouble() * Math.PI * 2 );
                nucleusOffset.setMagnitude( random.nextDouble() * 5 );
                updateNucleonOffsets( 1 );
            }
        }
    }

    private void updateNucleonOffsets(double factor) {
        for ( SubatomicParticle nucleon : getNucleons() ) {
            nucleon.setPositionAndDestination( nucleusOffset.getScaledInstance( factor ).getDestination( nucleon.getDestination() ) );
        }
    }

    /**Returns the removed particle.*/
    public SubatomicParticle removeParticle( SubatomicParticle particle ) {
        protons.remove( particle );
        neutrons.remove( particle );
        particle.removeListener( particleRemovalListener );
        reconfigureNucleus(true);
        notifyObservers();
        return particle;
    }

    /**
     * Remove an arbitrary proton.
     */
    public SubatomicParticle removeProton(){
        return removeParticle( protons.get( 0 ) );
    }

    /**
     * Remove an arbitrary neutron.
     */
    public SubatomicParticle removeNeutron(){
        return removeParticle( neutrons.get( 0 ) );
    }

    /**
     * Remove an arbitrary electron.
     */
    public Electron removeElectron(){
        return electronShell1.removeElectron();
    }

    /**
     * Check if the shells need to be reconfigured.  This can be necessary
     * if an electron was removed from shell 1 while there were electrons
     * present in shell 2.
     */
    protected void checkAndReconfigureShells() {
        checkAndReconfigureShells( electronShell1, electronShell2);
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

    public void reset() {
        for ( Proton proton : protons ) {
            proton.removeListener( particleRemovalListener );
        }
        for ( Neutron neutron : neutrons ) {
            neutron.removeListener( particleRemovalListener );
        }
        protons.clear();
        neutrons.clear();
        // It is important to reset the electron shells from the outside in so
        // that we don't end up with electrons trying to migrate from outer to
        // inner shells during reset.
        electronShell2.reset();
        electronShell1.reset();
        notifyObservers();
    }

    public ArrayList<ElectronShell> getElectronShells() {
        ArrayList<ElectronShell> electronShells = new ArrayList<ElectronShell>();
        electronShells.add( electronShell1 );
        electronShells.add( electronShell2 );
        return electronShells;
    }

    public int getRemainingElectronCapacity(){
        return electronShell1.getNumOpenLocations() + electronShell2.getNumOpenLocations();
    }

    public double getNucleusRadius() {
        return NUCLEUS_RADIUS;
    }

    public Point2D getPosition() {
        return position;
    }

    public void addProton( final Proton proton,boolean moveImmediately ) {
        assert !protons.contains( proton );

        // Add to the list of protons that are in the atom.
        protons.add( proton );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reconfigureNucleus(moveImmediately);

        proton.addListener( particleRemovalListener );

        // Update count in super class.  This sends out the change notification.
        super.setNumProtons( protons.size() );
    }

    @Override
    public void setNumElectrons( int numElectrons ) {
        int numElectronsInShells = electronShell1.getNumElectrons() + electronShell2.getNumElectrons();
        if ( numElectrons > numElectronsInShells ){
            // Attempt to get electrons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numElectrons - numElectronsInShells; i++ ){
                Electron electron = subatomicParticleRepository.getElectron();
                if ( electron != null ){
                    addElectron( electron, true );
                }
                else{
                    assert false;
                    System.err.println("Error: Not enough electrons available to allow set operation to succeed.");
                    continue;
                }
            }
        }
        else if ( numElectrons < numElectronsInShells ){
            // Move electrons to the repository until the numbers match.
            for ( int i = 0; i < numElectronsInShells - numElectrons; i++ ){
                subatomicParticleRepository.addElectron( removeElectron() );
            }
        }
        super.setNumElectrons( numElectrons );
    }

    @Override
    public void setNumNeutrons( int numNeutrons ) {
        if ( numNeutrons > neutrons.size() ){
            // Attempt to get neutrons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numNeutrons - neutrons.size(); i++ ){
                Neutron neutron = subatomicParticleRepository.getNeutron();
                if ( neutron != null ){
                    addNeutron( neutron, true );
                }
                else{
                    assert false;
                    System.err.println("Error: Not enough neutrons available to allow set operation to succeed.");
                    continue;
                }
            }
        }
        super.setNumNeutrons( numNeutrons );
    }

    @Override
    public void setNumProtons( int numProtons ) {
        if ( numProtons > protons.size() ){
            // Attempt to get protons from the repository to add to this
            // atom until we have enough or run out.
            for ( int i = 0; i < numProtons - protons.size(); i++ ){
                Neutron neutron = subatomicParticleRepository.getNeutron();
                if ( neutron != null ){
                    addNeutron( neutron, true );
                }
                else{
                    assert false;
                    System.err.println("Error: Not enough protons available to allow set operation to succeed.");
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
        reconfigureNucleus(moveImmediately );

        neutron.addListener( particleRemovalListener );

        // Update count in super class.  This sends out the change notification.
        super.setNumNeutrons( neutrons.size() );
    }

    public void addElectron( final Electron electron,boolean moveImmediately ) {
        if ( !electronShell1.isFull() ) {
            electronShell1.addElectron( electron, moveImmediately );
        }
        else if ( !electronShell2.isFull() ) {
            electronShell2.addElectron( electron, moveImmediately );
        } else {
            // Too many electrons.  The sim should be designed such that this
            // does not occur.  If it does, it should be debugged.
            assert false;
        }

        // Update count in super class.  This sends out the change notification.
        super.setNumElectrons( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
    }

    /**
     * Distribute the nucleons in the nucleus in such a way that the nucleus
     * will look good when shown in the view.
     */
    public void reconfigureNucleus(boolean moveImmediately) {

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
            nucleons.get( 1 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
            double distFromCenter = nucleonRadius * 2 * Math.cos( Math.PI / 3 );
            nucleons.get( 2 ).setDestination( distFromCenter * Math.cos( angle + Math.PI / 2 ),
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
                    placementRadius += nucleonRadius * 1.3 / level;
                    placementAngle += Math.PI / 8; // Arbitrary value chosen based on looks.
//                    if( BuildAnAtomApplication.animateUnstableNucleusProperty.getValue() && !isStable() ) {
//                        placementAngle += RAND.nextDouble() * 2 * Math.PI;
//                    }
                    numAtThisRadius = (int) Math.floor( placementRadius * Math.PI / nucleonRadius );
                    placementAngleDelta = 2 * Math.PI / numAtThisRadius;
                }
            }

            // Shuffle the location of some of the nucleons in order to
            // minimize the appearance of "clumpiness".
            /*
            for ( int i = 0; i < nucleons.size() / 2; i++ ) {
                Proton mostClumpedProton = null;
                for ( Proton proton : protons ) {
                    if ( mostClumpedProton == null || getClumpFactor( proton, nucleons ) > getClumpFactor( mostClumpedProton, nucleons ) ) {
                        // This nucleon is more "clumpy".
                        mostClumpedProton = proton;
                    }
                }
                Neutron mostClumpedNeutron = null;
                for ( Neutron neutron : neutrons ) {
                    if ( mostClumpedNeutron == null || getClumpFactor( neutron, nucleons ) > getClumpFactor( mostClumpedNeutron, nucleons ) ) {
                        // This nucleon is more "clumpy".
                        mostClumpedNeutron = neutron;
                    }
                }
                if (mostClumpedProton != null && mostClumpedNeutron != null){
                    // Swap the two most clumped.
                    swap(nucleons, nucleons.indexOf( mostClumpedProton ), nucleons.indexOf( mostClumpedNeutron ) );
                }
            }
            */

            // TODO: There are several different attempts at reducing the
            // clumpiness that are commented out below.  This should be
            // cleaned up once the nucleus appearance is deemed acceptable.

            //            if (nucleons.size() > 10){
            //                // Swap some particles in order to create a more random and
            //                // less clumpy appearance.
            //                for (int i = 0; i < nucleons.size() / 2; i++){
            //                    int index1 = RAND.nextInt( nucleons.size() );
            //                    int index2 = RAND.nextInt( nucleons.size() );
            //                    if (index1 == index2){
            //                        // Some thing, don't bother swapping.
            //                        continue;
            //                    }
            //                    swap(nucleons, index1, index2);
            //                    System.out.println("Swapping " + index1 + " and " + index2);
            //                }
            //            }

            // Move the nucleons around a bit in order to avoid looking too "clumpy".
            //            ArrayList<Proton> sortedProtons = new ArrayList<Proton>( protons );
            //            ArrayList<Neutron> sortedNeutrons = new ArrayList<Neutron>( neutrons );
            //            Collections.sort( sortedProtons, new Comparator<SubatomicParticle>(){
            //                public int compare( SubatomicParticle p1, SubatomicParticle p2 ) {
            //                    return Double.compare( getClumpFactor( p2, nucleons ), getClumpFactor( p1, nucleons ));
            //                }
            //            });
            //            Collections.sort( sortedNeutrons, new Comparator<SubatomicParticle>(){
            //                public int compare( SubatomicParticle p1, SubatomicParticle p2 ) {
            //                    return Double.compare( getClumpFactor( p2, nucleons ), getClumpFactor( p1, nucleons ));
            //                }
            //            });
            //            System.out.println("Sorted neutrons:");
            //            for ( Neutron neutron : sortedNeutrons ) {
            //                System.out.println("Clump factor: " + getClumpFactor( neutron, nucleons ));
            //            }
            //            System.out.println("Sorted protons:");
            //            for ( Proton proton : sortedProtons ) {
            //                System.out.println("Clump factor: " + getClumpFactor( proton, nucleons ));
            //            }
            //            for (int i = 0; i < Math.min( 3, Math.min( protons.size(), neutrons.size() ) ); i++){
            //                Proton proton = sortedProtons.get( i );
            //                Neutron neutron = sortedNeutrons.get( i );
            //                if (getClumpFactor( proton, nucleons ) > (double)MAX_CLUMP_FACTOR / 3 || getClumpFactor( neutron, nucleons ) > (double)MAX_CLUMP_FACTOR / 3){
            //                    // Swap these two locations to reduce the "clumpiness".
            //                    System.out.println("Swapping a proton & and neutron, clump factors are: " + getClumpFactor( proton, nucleons ) + " and "+ getClumpFactor( neutron, nucleons ));
            //                    swap( nucleons, nucleons.indexOf( proton ), nucleons.indexOf( neutron ) );
            //                }
            //            }


            //            double origClump = getClumpiness( nucleons );
            //            for ( int i = 0; i < 10; i++ ) {
            //                double clumpiness = getClumpiness( nucleons );
            //                int particle1 = random.nextInt( nucleons.size() );
            //                int particle2 = random.nextInt( nucleons.size() );
            //                swap( nucleons, particle1, particle2 );
            //                double newClumpiness = getClumpiness( nucleons );
            //                if ( newClumpiness < clumpiness ) {
            //                    //keep it
            //                }
            //                else {
            //                    swap( nucleons, particle1, particle2 );
            //                }
            //            }
            //            double finalClump = getClumpiness( nucleons );
            //            System.out.println( "origClump = " + origClump + ", final clump = " + finalClump );
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

    public ArrayList<Electron> getElectrons(){
        ArrayList<Electron> allElectrons = new ArrayList<Electron>( );
        allElectrons.addAll( electronShell1.getElectrons() );
        allElectrons.addAll( electronShell2.getElectrons() );
        return allElectrons;
    }

    public boolean containsElectron( Electron electron ) {
        return electronShell1.containsElectron(electron) || electronShell2.containsElectron(electron);
    }

    //For the game mode
    public ArrayList<SubatomicParticle> setState( AtomValue answer,BuildAnAtomModel model,boolean moveImmediately) {//provide the model to draw free particles from
        ArrayList<SubatomicParticle> removedParticles=new ArrayList<SubatomicParticle>( );
        while(getNumProtons()>answer.getProtons()){
            removedParticles.add( removeProton() );
        }
        while(getNumProtons()<answer.getProtons()){
            addProton( model.getFreeProton(),moveImmediately);
        }

        while(getNumNeutrons()>answer.getNeutrons()){
            removedParticles.add( removeNeutron());
        }
        while(getNumNeutrons()<answer.getNeutrons()){
            addNeutron( model.getFreeNeutron(),moveImmediately);
        }

        while(getNumElectrons()>answer.getElectrons()){
            removedParticles.add( removeElectron() );
        }
        while(getNumElectrons()<answer.getElectrons()){
            addElectron( model.getFreeElectron(),moveImmediately);
        }
        return removedParticles;
    }

    public boolean containsProton( Proton proton ) {
        return protons.contains( proton );
    }

    public boolean containsNeutron( Neutron neutron ) {
        return neutrons.contains( neutron );
    }
}