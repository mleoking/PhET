// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.util.Logger;

import edu.colorado.phet.common.jmolphet.JmolUtil;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingDragListener;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.MPColors;
import edu.colorado.phet.moleculepolarity.MPSimSharing.Parameters;
import edu.colorado.phet.moleculepolarity.MPSimSharing.UserComponents;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Element;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo node that displays a Jmol viewer.
 * Jmol scripting language is documented at http://chemapps.stolaf.edu/jmol/docs
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolViewerNode extends PhetPNode {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( JmolViewerNode.class.getCanonicalName() );

    // option for using "rainbow" (roygb) color scheme for molecular electrostatic potential (mep).
    public static final Property<Boolean> RAINBOW_MEP = new Property<Boolean>( false );

    // Results returned by Jmol scripts. These are returned as a status Object, which must be parsed.
    private static final String RESULT_TRUE = "true";
    private static final String RESULT_FALSE = "false";

    /*
     * Jmol script to determine if the current molecule is homogeneous diatomic (2 atoms of the same type.)
     * Returns RESULT_TRUE or RESULT_FALSE.
     */
    private static final String SCRIPT_IS_HOMOGENEOUS_DIATOMIC =
            "numberOfAtoms = {*}.length\n" +
            "homogeneousDiatomic = \"" + RESULT_TRUE + "\"\n" +
            "if ( numberOfAtoms == 2 ) {\n" +
            "    firstElement = {*}[0].element\n" +
            "    for ( i = 0; i < numberOfAtoms; i++ ) {\n" +
            "        nextElement = {*}[i].element\n" +
            "        if ( firstElement != nextElement ) {\n" +
            "            homogeneousDiatomic = \"" + RESULT_FALSE + "\"\n" +
            "        }\n" +
            "    }\n" +
            "}\n" +
            "else {\n" +
            "    homogeneousDiatomic = \"" + RESULT_FALSE + "\"\n" +
            "}\n" +
            "print homogeneousDiatomic";

    /*
     * Jmol script to determine the element number and color of each atom in the current molecule.
     */
    private static final String SCRIPT_GET_ELEMENT_NUMBERS_AND_COLORS =
            "n = {*}.length\n" +
            "for ( i = 0; i < n; i++ ) {\n" +
            "    print {*}[i].elemno\n" +
            "    print {*}[i].color\n" +
            "}";

    public final Property<Molecule3D> molecule; // the molecule displayed by the viewer
    private final ViewerPanel viewerPanel; // container for the Jmol viewer

    private boolean bondDipolesVisible, molecularDipoleVisible, partialChargeVisible, atomLabelsVisible;
    private SurfaceType surfaceType;

    public JmolViewerNode( final Property<Molecule3D> currentMolecule, Color background, Dimension size ) {

        viewerPanel = new ViewerPanel( currentMolecule.get(), background, size );
        //Record drag events for sim-sharing. Note that we cannot guarantee that these events are sent before the viewer display changes.
        final SimSharingDragListener dragListener = new SimSharingDragListener( UserComponents.realMoleculeViewer, UserComponentTypes.jmolViewer ) {
            @Override public ParameterSet getParametersForAllEvents( MouseEvent event ) {
                return super.getParametersForAllEvents( event ).with( Parameters.currentMolecule, currentMolecule.get().getName() );
            }
        };
        viewerPanel.addMouseListener( dragListener ); // for mousePressed and mouseReleased
        viewerPanel.addMouseMotionListener( dragListener ); // for mouseDragged
        addChild( new PSwing( viewerPanel ) );

        this.molecule = new Property<Molecule3D>( currentMolecule.get() );

        addInputEventListener( new CursorHandler() );

        currentMolecule.addObserver( new VoidFunction1<Molecule3D>() {
            public void apply( Molecule3D molecule ) {
                setMolecule( molecule );
            }
        } );

        RAINBOW_MEP.addObserver( new SimpleObserver() {
            public void update() {
                setSurfaceType( surfaceType );
            }
        } );
    }

    // Container for Jmol viewer
    private static class ViewerPanel extends JPanel {

        private final JmolViewer viewer;

        public ViewerPanel( final Molecule3D molecule, Color background, Dimension size ) {
            setPreferredSize( size );

            // configure Jmol's logging so we don't spew to the console
            Logger.setLogLevel( PhetApplication.getInstance().isDeveloperControlsEnabled() ? Logger.LEVEL_WARN : Logger.LEVEL_FATAL );

            // create the 3D viewer
            viewer = JmolViewer.allocateViewer( ViewerPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );

            // default settings of the viewer, independent of the molecule displayed
            viewer.setColorBackground( toJmolColor( background ) );
            viewer.setFrankOn( false ); // hide the "Jmol" watermark in the lower-right corner
            viewer.setBooleanProperty( "antialiasDisplay", true );
            viewer.setBooleanProperty( "autoBond", false );
            viewer.setFloatProperty( "dipoleScale", 0.8f ); // so that molecular dipole isn't clipped by viewer

            JmolUtil.unbindMouse( viewer ); // unbind all mouse operations
            JmolUtil.bindRotateLeft( viewer ); // bind rotate to left mouse button

            setMolecule( molecule );
        }

        // Jmol's canonical example of embedding in other Java is to override the paint method, so we do that here.
        @Override public void paint( Graphics g ) {
            // copied from Jmol's Integration.java
            Dimension currentSize = new Dimension();
            getSize( currentSize ); // stores size in currentSize
            Rectangle clipBounds = new Rectangle();
            g.getClipBounds( clipBounds );
            viewer.renderScreenImage( g, currentSize, clipBounds );
        }

        public JmolViewer getViewer() {
            return viewer;
        }

        // Runs a Jmol script synchronously and returns status.
        public Object doScript( String script ) {
            return viewer.scriptWaitStatus( script, null );
        }

        public void setMolecule( Molecule3D molecule ) {
            // load the molecule data
            String errorString = viewer.openStringInline( molecule.getData() );
            if ( errorString != null ) {
                LOGGER.log( Level.SEVERE, "Jmol says: " + errorString );
            }
        }
    }

    public JmolViewer getViewer() {
        return viewerPanel.getViewer();
    }

    // use custom colors for some atoms
    protected void adjustAtomColors() {
        doScript( "select oxygen; color " + toJmolColor( PhetColorScheme.RED_COLORBLIND ) );
        doScript( "select all" ); // be polite to other scripts that assume that everything is selected
    }

    // Converts an AWT Color to a Jmol RGB-color argument, a String of the form "[R,G,B]". Eg, Color.ORANGE -> eg "[255,200,0]".
    public static String toJmolColor( Color color ) {
        return MessageFormat.format( "[{0},{1},{2}]", color.getRed(), color.getGreen(), color.getBlue() );
    }

    // Runs a Jmol script synchronously and returns status.
    public Object doScript( String script ) {
        return viewerPanel.doScript( script );
    }

    private void setMolecule( Molecule3D molecule ) {
        viewerPanel.setMolecule( molecule );
        this.molecule.set( molecule );
        // these things need to be reset when the viewer loads a new molecule
        adjustAtomColors();
        setBallAndStick();
        setAtomLabelsVisible( atomLabelsVisible );
        setBondDipolesVisible( bondDipolesVisible );
        setMolecularDipoleVisible( molecularDipoleVisible );
        setSurfaceType( surfaceType );
        doScript( "hover off" ); // don't display labels when hovering over atoms
        updateAtomLabels();
        updateTranslucency();
    }

    // sets the Jmol viewer to ball-and-stick view
    private void setBallAndStick() {
        doScript( "wireframe 0.1 " ); // draw bonds as lines
        doScript( "spacefill 25%" ); // render atoms as a percentage of the van der Waals radius
        doScript( "color bonds " + toJmolColor( MPColors.BOND ) ); // color for all bonds
    }

    // makes atom labels visible
    public void setAtomLabelsVisible( boolean visible ) {
        atomLabelsVisible = visible;
        updateAtomLabels();
    }

    // Atom labels display multiple pieces of information. This updates the labels to show whatever is currently enabled.
    private void updateAtomLabels() {
        String args = "";
        if ( atomLabelsVisible || partialChargeVisible ) {
            if ( atomLabelsVisible ) {
                args += " %[atomName]"; // show element and sequential atom index
            }
            if ( partialChargeVisible ) {
                if ( atomLabelsVisible ) {
                    args += "|"; // line separator
                }
                args += MPStrings.DELTA + "=%.2[partialCharge]"; // show partial charge
            }
            doScript( "label " + args );
            doScript( "set labelalignment center; set labeloffset 0 0" );  // center labels on atoms
            doScript( "color labels black" ); // color for all labels
            doScript( "font labels 18 sanserif" ); // font for all labels
        }
        else {
            doScript( "label off" );
        }
    }

    // Makes atoms and bonds translucent when dipoles are visible.
    private void updateTranslucency() {
        String arg = ( bondDipolesVisible || molecularDipoleVisible ) ? "0.25" : "0.0"; // 0.0=opaque, 1.0=transparent
        doScript( "color atoms translucent " + arg );
        doScript( "color bonds translucent " + arg );
    }

    // Sets visibility of bond dipoles.
    public void setBondDipolesVisible( boolean visible ) {
        bondDipolesVisible = visible;
        if ( visible ) {
            doScript( "dipole bonds on width 0.05" );
        }
        else {
            doScript( "dipole bonds off" );
        }
        updateTranslucency();
    }

    // Sets visibility of molecular dipole.
    public void setMolecularDipoleVisible( boolean visible ) {
        molecularDipoleVisible = visible;
        if ( visible ) {
            doScript( "dipole molecular on width 0.05" );
        }
        else {
            doScript( "dipole molecular off" );
        }
        updateTranslucency();
    }

    // Sets visibility of partial charges.
    public void setPartialChargeVisible( boolean visible ) {
        partialChargeVisible = visible;
        updateAtomLabels();
    }

    // Sets the type of surface.
    public void setSurfaceType( SurfaceType surfaceType ) {
        this.surfaceType = surfaceType;
        if ( surfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL ) {
            if ( isHomogeneousDiatomic() ) {
                if ( RAINBOW_MEP.get() ) {
                    doScript( "isosurface VDW color " + toJmolColor( MPColors.NEUTRAL_GREEN ) + " translucent" );
                }
                else {
                    doScript( "isosurface VDW color white translucent" );
                }
            }
            else {
                if ( RAINBOW_MEP.get() ) {
                    doScript( "isosurface VDW map MEP translucent" );
                }
                else {
                    doScript( "isosurface VDW map MEP colorscheme \"RWB\" translucent" );
                }
            }
        }
        else if ( surfaceType == SurfaceType.ELECTRON_DENSITY ) {
            if ( isHomogeneousDiatomic() ) {
                doScript( "isosurface VDW color " + toJmolColor( MPColors.NEUTRAL_GRAY ) + " translucent" );
            }
            else {
                doScript( "isosurface VDW map MEP colorscheme \"BW\" translucent" );
            }
        }
        else {
            doScript( "isosurface off" );
        }
    }

    // Interrogates Jmol to determine whether the current molecule is homogeneous diatomic.
    private boolean isHomogeneousDiatomic() {
        Object status = doScript( SCRIPT_IS_HOMOGENEOUS_DIATOMIC );
//        LOGGER.info( "isHomogeneousDiatomic status=[" + status.toString() + "]" );
        if ( status == null ) {
            throw new RuntimeException( "Jmol script returned null status" );
        }
        else {
            return parseBoolean( status );
        }
    }

    // Gets the elements for the atoms in the current molecule.
    public ArrayList<Element> getElements() {
        Object status = doScript( SCRIPT_GET_ELEMENT_NUMBERS_AND_COLORS );
//        LOGGER.info( "getElements status=[" + status.toString() + "]" );
        if ( status == null ) {
            throw new RuntimeException( "Jmol script returned null status" );
        }
        else {
            // each set of 4 numbers is: elementNumber red green blue
            ArrayList<Integer> values = parseIntegers( status, " \n{}" );
            ArrayList<Element> elements = new ArrayList<Element>();
            for ( int i = 0; i < values.size(); i += 4 ) {
                int elementNumber = values.get( i );
                Color color = new Color( values.get( i + 1 ), values.get( i + 2 ), values.get( i + 3 ) );
                elements.add( new Element( elementNumber, color ) );
            }
            return elements;
        }
    }

    // Returns true if Jmol status is equal to the String RESULT_TRUE.
    private static boolean parseBoolean( Object status ) {
        return status.toString().trim().equals( RESULT_TRUE );
    }

    // Parses a status that contains integers.
    private static ArrayList<Integer> parseIntegers( Object status, String delimiters ) {
        String statusString = status.toString().trim();
        ArrayList<Integer> values = new ArrayList<Integer>();
        StringTokenizer tokenizer = new StringTokenizer( statusString, delimiters );
        while ( tokenizer.hasMoreTokens() ) {
            String token = tokenizer.nextToken();
            int value = (int) Double.parseDouble( token ); // Jmol formats some ints as doubles
            values.add( value );
        }
        return values;
    }

    // test
    public static void main( String[] args ) {
        final PhetPCanvas canvas = new PhetPCanvas() {{
            setPreferredSize( new Dimension( 500, 500 ) );
            setBackground( Color.LIGHT_GRAY );
            Property<Molecule3D> currentMolecule = new Property<Molecule3D>( new Molecule3D( "H2O", "water", "mol2/h2o.mol2" ) );
            JmolViewerNode viewerNode = new JmolViewerNode( currentMolecule, getBackground(), new Dimension( 400, 400 ) );
            getLayer().addChild( viewerNode );
            viewerNode.setOffset( 50, 50 );
        }};
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
