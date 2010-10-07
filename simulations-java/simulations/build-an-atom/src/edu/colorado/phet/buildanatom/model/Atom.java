package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class represents that atom in the model.  It supplies static
 * information such as the position of the atom, as well as dynamic
 * information such as the number of protons present.
 */
public class Atom extends SimpleObservable{

    private static final Random RAND = new Random();

    // Nuclear radius, in picometers.  This is not to scale - we need it
    // to be larger than real life.
    private static final double NUCLEUS_RADIUS = 10;

    // Electron shell radii.
    public static final double ELECTRON_SHELL_1_RADIUS = 34;
    public static final double ELECTRON_SHELL_2_RADIUS = 102;

    // Map of proton numbers to element symbols.
    private static final HashMap<Integer, AtomName> mapNumProtonsToName = new HashMap<Integer, AtomName>(){{
        // TODO: i18n for all element names.
        put(0, new AtomName("-", "--"));//for an unbuilt or empty atom
        put(1, new AtomName("H", "Hydrogen"));
        put(2, new AtomName("He", "Helium"));
        put(3, new AtomName("Li", "Lithium"));
        put(4, new AtomName("Be", "Beryllium"));
        put(5, new AtomName("B", "Boron"));
        put(6, new AtomName("C", "Carbon"));
        put(7, new AtomName("N", "Nitrogen"));
        put(8, new AtomName("O", "Oxygen"));
        put(9, new AtomName("F", "Fluorine"));
        put(10, new AtomName("Ne", "Neon"));
    }};

    // List of stable isotopes for the first 2 rows of the periodic table 
    // See the table and notes at https://docs.google.com/document/edit?id=1VGGhLUetiwijbDneU-U6BPrjRlkI0rt939zk8Y4AuA4&authkey=CMLM4ZUC&hl=en#
    private static final ArrayList<Isotope> stableIsotopes = new ArrayList<Isotope>() {{
        //H
        add( new Isotope( 1, 0 ) );
        add( new Isotope( 2, 1 ) );
        add( new Isotope( 3, 2 ) );
        //He
        add( new Isotope( 3, 1 ) );
        add( new Isotope( 4, 2 ) );
        //Li
        add( new Isotope( 6, 3 ) );
        add( new Isotope( 7, 4 ) );
        //Be
        add( new Isotope( 7, 3 ) );
        add( new Isotope( 9, 5 ) );
        add( new Isotope( 10, 6 ) );
        //B
        add( new Isotope( 10, 5 ) );
        add( new Isotope( 11, 6 ) );
        //C
        add( new Isotope( 12, 6 ) );
        add( new Isotope( 13, 7 ) );
        add( new Isotope( 14, 8 ) );
        //N
        add( new Isotope( 14, 7 ) );
        add( new Isotope( 15, 8 ) );
        //O
        add( new Isotope( 16, 8 ) );
        add( new Isotope( 17, 9 ) );
        add( new Isotope( 18, 10 ) );
        //F
        add( new Isotope( 19, 10 ) );
        //Ne
        add( new Isotope( 20, 10 ) );
        add( new Isotope( 21, 11 ) );
        add( new Isotope( 22, 12 ) );
    }};

    // Position in model space.
    private final Point2D position = new Point2D.Double();

    // List of the subatomic particles that are currently in the nucleus.
    // Note that the electrons are maintained in the shells.
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // Shells for containing electrons.
    private final ElectronShell electronShell1 = new ElectronShell( ELECTRON_SHELL_1_RADIUS, 2 );
    private final ElectronShell electronShell2 = new ElectronShell( ELECTRON_SHELL_2_RADIUS, 8 );

    // Observer for electron shells.
    private final SimpleObserver electronShellChangeObserver = new SimpleObserver() {
        public void update() {
            checkAndReconfigureShells();
        }
    };

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the atom.
    private final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( SubatomicParticle particle ) {
            // The user has picked up this particle, so we assume
            // that it is essentially removed from the atom.
            protons.remove( particle );
            neutrons.remove( particle );
            particle.removeListener( this );
            reconfigureNucleus();
            notifyObservers();
        }
    };
    private final Random random = new Random();

    /**
     * Constructor.
     */
    public Atom( Point2D position ) {
        this.position.setLocation( position );
        //Only need to listen for 'removal' notifications on the inner shell
        //to decide when an outer electron should fall
        electronShell1.addObserver( electronShellChangeObserver );
        notifyObservers();
    }

    /**
     * Check if the shells need to be reconfigured.  This can be necessary
     * if an electron was removed from shell 1 while there were electrons
     * present in shell 2.
     */
    protected void checkAndReconfigureShells() {
        if ( !electronShell1.isFull() && !electronShell2.isEmpty() ) {

            // Need to move an electron from shell 2 to shell 1.
            ArrayList<Point2D> openLocations = electronShell1.getOpenShellLocations();

            // We expect there to be one and only one open location, so test that this is true.
            assert openLocations.size() == 1;

            // Get the electron that is nearest to this location in shell 2
            // and move it to shell 1.
            Electron electronToMove = electronShell2.getClosestElectron( openLocations.get( 0 ) );

            electronShell2.removeElectron( electronToMove );
            electronShell1.addElectron( electronToMove );
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
        electronShell1.reset();
        electronShell2.reset();
        notifyObservers();
    }

    public ArrayList<Double> getElectronShellRadii() {
        ArrayList<Double> electronShellRadii = new ArrayList<Double>();
        electronShellRadii.add( new Double( electronShell1.getRadius() ) );
        electronShellRadii.add( new Double( electronShell2.getRadius() ) );
        return electronShellRadii;
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

    public int getNumProtons(){
        return protons.size();
    }

    public int getAtomicMassNumber(){
        return protons.size() + neutrons.size();
    }

    public int getCharge() {
        return protons.size() - ( electronShell1.getNumElectrons() + electronShell2.getNumElectrons() );
    }

    public String getName(){
        assert mapNumProtonsToName.containsKey( getNumProtons() );
        return mapNumProtonsToName.get( getNumProtons() ).name;
    }

    public String getSymbol(){
        assert mapNumProtonsToName.containsKey( getNumProtons() );
        return mapNumProtonsToName.get( getNumProtons() ).symbol;
    }

    public void addProton( final Proton proton ) {
        assert !protons.contains( proton );

        // Add to the list of protons that are in the atom.
        protons.add( proton );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reconfigureNucleus();

        proton.addListener( particleRemovalListener );
        notifyObservers();
    }

    public void addNeutron( final Neutron neutron ) {
        assert !neutrons.contains( neutron );

        // Add to the list of neutrons that are in the atom.
        neutrons.add( neutron );

        // Reconfigure the nucleus.  This will set the destination for this
        // new nucleon.
        reconfigureNucleus();

        neutron.addListener( particleRemovalListener );
        notifyObservers();
    }

    public void addElectron( final Electron electron ) {
        if ( !electronShell1.isFull() ) {
            electronShell1.addElectron( electron );
            notifyObservers();
        }
        else if ( !electronShell2.isFull() ) {
            electronShell2.addElectron( electron );
            notifyObservers();
        }
        else {
            // Too many electrons.  The sim should be designed such that this
            // does not occur.  If it does, it should be debugged.
            assert false;
        }
    }


    /**
     * Distribute the nucleons in the nucleus in such a way that the nucleus
     * will look good when shown in the view.
     */
    private void reconfigureNucleus() {

        double nucleonRadius = Proton.RADIUS;

        // Get all the nucleons onto one list.  Add them alternately so that
        // they don't get clustered together by type when distributed in the
        // nucleus.
        final ArrayList<SubatomicParticle> nucleons = new ArrayList<SubatomicParticle>();
        for ( int i = 0; i < Math.max( protons.size(), neutrons.size() ); i++ ) {
            if ( i < protons.size() ) {
                nucleons.add( protons.get( i ) );
            }
            if ( i < neutrons.size() ) {
                nucleons.add( neutrons.get( i ) );
            }
        }
        Collections.reverse( nucleons );

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
            double angle = RAND.nextDouble() * 2 * Math.PI;
            nucleons.get( 0 ).setDestination( nucleonRadius * Math.cos( angle ), nucleonRadius * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( -nucleonRadius * Math.cos( angle ), -nucleonRadius * Math.sin( angle ) );
        }
        else if ( nucleons.size() == 3 ) {
            // Form a triangle where they all touch.
            double angle = RAND.nextDouble() * 2 * Math.PI;
            double distFromCenter = nucleonRadius * 1.155;
            nucleons.get( 0 ).setDestination( distFromCenter * Math.cos( angle ), distFromCenter * Math.sin( angle ) );
            nucleons.get( 1 ).setDestination( distFromCenter * Math.cos( angle + 2 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 2 * Math.PI / 3 ) );
            nucleons.get( 2 ).setDestination( distFromCenter * Math.cos( angle + 4 * Math.PI / 3 ),
                    distFromCenter * Math.sin( angle + 4 * Math.PI / 3 ) );
        }
        else if ( nucleons.size() == 4 ) {
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
                    //                    placementAngle = RAND.nextDouble() * Math.PI / 4; // Initialize to a random angle.
                    placementAngle += Math.PI / 8; // Arbitrary value chosen based on looks.
                    numAtThisRadius = (int) Math.floor( placementRadius * Math.PI / nucleonRadius );
                    placementAngleDelta = 2 * Math.PI / numAtThisRadius;
                }
            }

            // Shuffle the location of some of the nucleons in order to
            // minimize the appearance of "clumpiness".
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
    }

    private double getClumpiness( ArrayList<SubatomicParticle> nucleons ) {
        double error = 0;
        for ( SubatomicParticle nucleon : nucleons ) {
            error += getClumpFactor( nucleon, nucleons );
        }
        return error;
    }

    int MAX_CLUMP_FACTOR = 5;

    private double getClumpFactor( SubatomicParticle nucleon, ArrayList<SubatomicParticle> n ) {
        ArrayList<SubatomicParticle> nucleons = new ArrayList<SubatomicParticle>( n );
        nucleons.remove( nucleon );
        final HashMap<SubatomicParticle, Double> distances = new HashMap<SubatomicParticle, Double>();
        for ( SubatomicParticle subatomicParticle : nucleons ) {
            distances.put( subatomicParticle, nucleon.getDestination().distance( subatomicParticle.getDestination() ) );
        }
        Collections.sort( nucleons, new Comparator<SubatomicParticle>() {
                public int compare( SubatomicParticle o1, SubatomicParticle o2 ) {
                return Double.compare( distances.get( o1 ), distances.get( o2 ) );
                }
                } );
        //take the top N particles
        int error = 0;
        for ( int i = 0; i < MAX_CLUMP_FACTOR && i < nucleons.size(); i++ ) {
            if ( nucleon.getClass().equals( nucleons.get( i ).getClass() ) ) {
                error++;
            }
        }
        return error;
    }

    private void swap( ArrayList<SubatomicParticle> nucleons, int particle1, int particle2 ) {
        Point2D point1 = nucleons.get( particle1 ).getDestination();
        nucleons.get( particle1 ).setDestination( nucleons.get( particle2 ).getDestination() );
        nucleons.get( particle2 ).setDestination( point1 );
    }

    public boolean isStable() {
        //taken from the table at https://docs.google.com/document/edit?id=1VGGhLUetiwijbDneU-U6BPrjRlkI0rt939zk8Y4AuA4&authkey=CMLM4ZUC&hl=en#
        return getNumProtons() == 0 || stableIsotopes.contains( new Isotope( getAtomicMassNumber(), neutrons.size() ) );
    }

    /**
     * Structure that contains both the chemical symbol and textual name for
     * an atom.
     *
     * @author John Blanco
     */
    private static class AtomName{
        public final String symbol;
        public final String name;
        public AtomName( String symbol, String name ) {
            this.symbol = symbol;
            this.name = name;
        }
    }

    private static class Isotope {
        public final int massNumber;
        public final int neutronNumber;
        //Regenerate equals and hashcode if you change the contents of the isotope

        public Isotope( int massNumber, int neutronNumber ) {
            this.massNumber = massNumber;
            this.neutronNumber = neutronNumber;
        }

        //Autogenerated

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }

            Isotope isotope = (Isotope) o;

            if ( massNumber != isotope.massNumber ) {
                return false;
            }
            if ( neutronNumber != isotope.neutronNumber ) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = massNumber;
            result = 31 * result + neutronNumber;
            return result;
        }
    }
}