// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.moleculeshapes.model.Atom;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.view.AtomNode;
import edu.colorado.phet.moleculeshapes.view.BondNode;
import edu.colorado.phet.moleculeshapes.view.MSNode;
import edu.umd.cs.piccolo.PNode;

/**
 * The main application for this simulation.
 */
public class MoleculeShapesApplication extends PiccoloPhetApplication {

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public MoleculeShapesApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {

        Frame parentFrame = getPhetFrame();

        final ConstantDtClock clock = new ConstantDtClock( 30 );

        Module makeMoleculeModule = new PiccoloModule( MoleculeShapesStrings.TITLE, clock, false ) {{
            setClockControlPanel( null );
            setSimulationPanel( new PhetPCanvas() {{
                setBackground( Color.BLACK );
                final PNode holder = new PNode();
                addWorldChild( holder );
                holder.setOffset( 450, 350 );

                final double distance = 250;

                final Atom a = new Atom( 50 );
                final Atom b = new Atom( 50 );
                final Atom c = new Atom( 50 );
                final Atom d = new Atom( 50 );
                final Atom e = new Atom( 50 );
                final Atom f = new Atom( 50 );
                final Atom g = new Atom( 50 );
                final Atom h = new Atom( 50 );

                holder.addChild( new AtomNode( a ) );
                holder.addChild( new AtomNode( b ) );
                holder.addChild( new BondNode( new Bond( a, b ) ) );
                holder.addChild( new AtomNode( c ) );
                holder.addChild( new BondNode( new Bond( a, c ) ) );
                holder.addChild( new AtomNode( d ) );
                holder.addChild( new BondNode( new Bond( a, d ) ) );
                holder.addChild( new AtomNode( e ) );
                holder.addChild( new BondNode( new Bond( a, e ) ) );
                holder.addChild( new AtomNode( f ) );
                holder.addChild( new BondNode( new Bond( a, f ) ) );
                holder.addChild( new AtomNode( g ) );
                holder.addChild( new BondNode( new Bond( a, g ) ) );
                holder.addChild( new AtomNode( h ) );
                holder.addChild( new BondNode( new Bond( a, h ) ) );

                clock.addClockListener( new ClockAdapter() {
                    double t = 0;

                    @Override public void simulationTimeChanged( ClockEvent clockEvent ) {

                        t += 0.1;

                        b.position.set( new ImmutableVector3D( distance * Math.cos( t ), 0, distance * Math.sin( t ) ) );
                        c.position.set( new ImmutableVector3D( distance * Math.cos( t ), 0, distance * Math.sin( t ) ).negated() );

                        double theta = 0.4;
                        for ( Atom atom : Arrays.asList( d, e, f, g, h ) ) {
                            atom.position.set( new ImmutableVector3D( distance * Math.cos( t + Math.PI / 2 ) * Math.cos( theta ), distance * Math.sin( theta ), distance * Math.sin( t + Math.PI / 2 ) * Math.cos( theta ) ) );
                            theta += 2 * Math.PI / 5;
                        }

                        List<MSNode> nodes = new ArrayList<MSNode>( holder.getChildrenReference() );

                        Collections.sort( nodes, new Comparator<MSNode>() {
                            public int compare( MSNode a, MSNode b ) {
                                return -Double.compare( a.getCenter().getZ(), b.getCenter().getZ() );
                            }
                        } );

                        // essentially sort by Z-depth
                        for ( MSNode node : nodes ) {
                            ( (PNode) node ).moveToBack();
                        }
                    }
                } );
            }} );
        }};
        addModule( makeMoleculeModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        // Create main frame.
        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu

        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /*
         * If you want to customize your application (look-&-feel, window size, etc)
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, MoleculeShapesConstants.PROJECT_NAME, MoleculeShapesApplication.class );
    }
}
