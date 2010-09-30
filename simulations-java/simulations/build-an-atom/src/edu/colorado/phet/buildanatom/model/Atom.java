package edu.colorado.phet.buildanatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * This class represents that atom in the model.  It supplies static
 * information such as the position of the atom, as well as dynamic
 * information such as the number of protons present.
 */
public class Atom {

    private static final Random RAND = new Random();

    // Nuclear radius, in picometers.  This is not to scale - we need it
    // to be larger than real life.
    private static final double NUCLEUS_RADIUS = 10;

    // Electron shell radii.
    private static final double INNER_SHELL_RADIUS = 34;
    public static final double OUTER_SHELL_RADIUS = 102;

    // Position in model space.
    private final Point2D position = new Point2D.Double();

    // List of the subatomic particles that are currently a part of this
    // atom.
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();
    private final ArrayList<Electron> electronsInShell1 = new ArrayList<Electron>();
    private final ArrayList<Electron> electronsInShell2 = new ArrayList<Electron>();

    // Radii of the electron shells.  The values used for these distances
    // are remotely related to reality (based on covalent bond radii of
    // various molecules) but they have been tweaked significantly in
    // order to be at a scale that works visually in the sim.
    private final ArrayList<Double> electronShellRadii = new ArrayList<Double>() {
        {
            add( new Double( 34 ) );
            add( new Double( 102 ) );
        }
    };

    // Electron shell positions
    private final ArrayList<Point2D> openShell1Positions = new ArrayList<Point2D>() {
        {
            add( new Point2D.Double( INNER_SHELL_RADIUS, 0 ) );
            add( new Point2D.Double( -INNER_SHELL_RADIUS, 0 ) );
        }
    };

    private final ArrayList<Point2D> openShell2Positions = new ArrayList<Point2D>() {
        {
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 0 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 0 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 1 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 1 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 2 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 2 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 3 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 3 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 4 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 4 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 5 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 5 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 6 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 6 * Math.PI / 4 ) ) );
            add( new Point2D.Double( OUTER_SHELL_RADIUS * Math.cos( 7 * Math.PI / 4 ), OUTER_SHELL_RADIUS * Math.sin( 7 * Math.PI / 4 ) ) );
        }
    };

    private final ArrayList<Point2D> occupiedShell1Positions = new ArrayList<Point2D>();
    private final ArrayList<Point2D> occupiedShell2Positions = new ArrayList<Point2D>();

    /**
     * Constructor.
     */
    public Atom( Point2D position ) {
        this.position.setLocation( position );
    }

    public void reset() {
        protons.clear();
        neutrons.clear();
        electronsInShell1.clear();
        electronsInShell2.clear();
        openShell1Positions.addAll( occupiedShell1Positions );
        occupiedShell1Positions.clear();
        openShell2Positions.addAll( occupiedShell2Positions );
        occupiedShell2Positions.clear();
    }

    public ArrayList<Double> getElectronShellRadii() {
        return electronShellRadii;
    }

    public double getNucleusRadius() {
        return NUCLEUS_RADIUS;
    }

    public Point2D getPosition() {
        return position;
    }

    private Electron findClosestOuterShellElectron( Point2D location ) {

        Electron closestElectron = null;
        if ( electronsInShell2.size() > 0 ) {
            for ( Electron candidateElectron : electronsInShell2 ) {
                if ( closestElectron == null || closestElectron.getPosition().distance( location ) > candidateElectron.getPosition().distance( location ) ) {
                    // The candidate is a better choice.
                    closestElectron = candidateElectron;
                }
            }
        }
        return closestElectron;
    }

    public void addProton( final Proton proton ) {
        assert !protons.contains( proton );

        // Add to the list of protons that are in the atom.
        protons.add( proton );

        // Set the destination so that the proton will go to the location.
        double randAngle = RAND.nextDouble() * 2 * Math.PI;
        double randLength = RAND.nextDouble() * NUCLEUS_RADIUS;
        proton.setDestination( Math.cos( randAngle ) * randLength + getPosition().getX(),
                Math.sin( randAngle ) * randLength + getPosition().getY() );
        proton.addUserControlListener( new SimpleObserver() {

            public void update() {
                if ( proton.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    protons.remove( proton );
                    proton.removeUserControlListener( this );
                }
            }
        } );
    }

    public void addNeutron( final Neutron neutron ) {
        assert !neutrons.contains( neutron );

        // Add to the list of neutrons that are in the atom.
        neutrons.add( neutron );

        // Set the destination so that the neutron will go to the location.
        double randAngle = RAND.nextDouble() * 2 * Math.PI;
        double randLength = RAND.nextDouble() * NUCLEUS_RADIUS;
        neutron.setDestination( Math.cos( randAngle ) * randLength + getPosition().getX(),
                Math.sin( randAngle ) * randLength + getPosition().getY() );
        neutron.addUserControlListener( new SimpleObserver() {

            public void update() {
                if ( neutron.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    neutrons.remove( neutron );
                    neutron.removeUserControlListener( this );
                }
            }
        } );
    }

    public void addElectron( final Electron electron ) {
        assert !electronsInShell1.contains( electron ) && !electronsInShell2.contains( electron );

        if ( electronsInShell1.size() < 2 ) {
            // The inner shell isn't full yet, so put this electron there.
            electronsInShell1.add( electron );
            Point2D locationInShell = findClosestPointInList( electron.getPosition(), openShell1Positions );
            electron.setDestination( locationInShell );
            openShell1Positions.remove( locationInShell );
            occupiedShell1Positions.add( locationInShell );
        }
        else if ( electronsInShell2.size() < 8 ) {
            // Put the electron in the outer shell.
            electronsInShell2.add( electron );
            Point2D locationInShell = findClosestPointInList( electron.getPosition(), openShell2Positions );
            electron.setDestination( locationInShell );
            openShell2Positions.remove( locationInShell );
            occupiedShell2Positions.add( locationInShell );
        }
        else {
            assert false; // Should never get here, debug if it does.
        }

        // Add a listener for when this electron is grabbed by the user.
        electron.addUserControlListener( new SimpleObserver() {
            public void update() {
                if ( electron.isUserControlled() ) {
                    // The user has picked up this particle, so we assume
                    // that it is essentially removed from the atom.
                    electron.removeUserControlListener( this );
                    if ( electronsInShell1.contains( electron ) ) {
                        // This electron was in the innermost shell.
                        electronsInShell1.remove( electron );
                        Point2D innerShellLocation = findClosestPointInList( electron.getPosition(), occupiedShell1Positions );
                        // If there are one or more electrons in the outer
                        // shell, the closest one must move down into the
                        // newly opened spot.
                        if (electronsInShell2.size() > 0){
                            Electron electronToMove = findClosestOuterShellElectron( innerShellLocation );
                            assert electronToMove != null;
                            Point2D outerShellLocation = findClosestPointInList( electronToMove.getPosition(), occupiedShell2Positions );
                            electronsInShell2.remove( electronToMove );
                            electronsInShell1.add( electronToMove );
                            electronToMove.setDestination( innerShellLocation );
                            occupiedShell2Positions.remove( outerShellLocation );
                            openShell2Positions.add(  outerShellLocation );
                        }
                        else{
                            occupiedShell1Positions.remove( innerShellLocation );
                            openShell1Positions.add( innerShellLocation );
                        }
                    }
                    else if ( electronsInShell2.contains( electron ) ){
                        electronsInShell2.remove( electron );
                        Point2D locationInShell = findClosestPointInList( electron.getPosition(), occupiedShell2Positions );
                        occupiedShell2Positions.remove( locationInShell );
                        openShell2Positions.add( locationInShell );
                    }
                    else{
                        assert false; // Should never get here, debug if it does.
                    }
                }
            }
        } );
    }

    private Point2D findClosestPointInList( Point2D comparisonPt, ArrayList<Point2D> pointList ) {
        if ( pointList.size() == 0 ) {
            return null;
        }

        Point2D closestPoint = null;
        for ( Point2D candidatePoint : pointList ) {
            if ( closestPoint == null || closestPoint.distance( comparisonPt ) > candidatePoint.distance( comparisonPt ) ) {
                // Candidate point is better.
                closestPoint = candidatePoint;
            }
        }
        return closestPoint;
    }
}