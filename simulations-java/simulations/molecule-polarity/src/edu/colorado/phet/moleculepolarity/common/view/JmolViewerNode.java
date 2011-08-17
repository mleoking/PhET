// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
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

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.model.Molecule3D;
import edu.colorado.phet.moleculepolarity.common.view.ViewProperties.SurfaceType;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Piccolo node that display a Jmol viewer.
 * Jmol scripting language is documented at http://chemapps.stolaf.edu/jmol/docs
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolViewerNode extends PhetPNode {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( JmolViewerNode.class.getCanonicalName() );

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

    /**
     * Jmol script to get the element numbers of atoms in the current molecule.
     * Each element number appears on a separate line.
     */
    private static final String SCRIPT_GET_ELEMENT_NUMBERS =
            "n = {*}.length\n" +
            "for ( i = 0; i < n; i++ ) {\n" +
            "    print {*}[i].elemno\n" +
            "}";

    private final ViewerPanel viewerPanel;
    private boolean bondDipolesVisible, molecularDipoleVisible, partialChargeVisible, atomLabelsVisible;
    private SurfaceType isosurface;

    public JmolViewerNode( Property<Molecule3D> currentMolecule, Color background, Dimension size ) {
        viewerPanel = new ViewerPanel( currentMolecule.get(), background, size );
        addChild( new PSwing( viewerPanel ) );
        addInputEventListener( new CursorHandler() );
        currentMolecule.addObserver( new VoidFunction1<Molecule3D>() {
            public void apply( Molecule3D molecule ) {
                setMolecule( molecule );
            }
        } );
    }

    // Container for Jmol viewer
    private static class ViewerPanel extends JPanel {

        private final JmolViewer viewer;

        public ViewerPanel( final Molecule3D molecule, Color background, Dimension size ) {
            setPreferredSize( size );

            // configure Jmol's logging so we don't spew to the console
            Logger.setLogLevel( Logger.LEVEL_WARN );

            // create the 3D viewer
            viewer = JmolViewer.allocateViewer( ViewerPanel.this, new SmarterJmolAdapter(), null, null, null, "-applet", null );

            // default settings of the viewer, independent of the molecule displayed
            viewer.setBooleanProperty( "antialiasDisplay", true );
            viewer.setBooleanProperty( "autoBond", false );
            doScript( "unbind \"_popupMenu\"" ); // disable popup menu
            doScript( "unbind \"SHIFT-LEFT\"" ); // disable zooming
            doScript( "frank off" ); // hide the "Jmol" watermark in the lower-right corner
            doScript( "background " + JmolViewerNode.toJmolColor( background ) ); //TODO how to make background transparent?

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

        public void doScript( String script ) {
            viewer.scriptWait( script );
        }

        public Object doScriptStatus( String script ) {
            return viewer.scriptWaitStatus( script, null );
        }

        public void setMolecule( Molecule3D molecule ) {
            // load the molecule data
            String errorString = viewer.openStringInline( molecule.getData() );
            if ( errorString != null ) {
                LOGGER.log( Level.SEVERE, "Jmol says: " + errorString ); //TODO improve error handling
            }
        }
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

    public void doScript( String script ) {
        viewerPanel.doScript( script );
    }

    public Object doScriptStatus( String script ) {
        return viewerPanel.doScriptStatus( script );
    }

    private void setMolecule( Molecule3D molecule ) {
        viewerPanel.setMolecule( molecule );
        // these things need to be reset when the viewer loads a new molecule
        adjustAtomColors();
        setBallAndStick();
        setAtomLabelsVisible( atomLabelsVisible );
        setBondDipolesVisible( bondDipolesVisible );
        setMolecularDipoleVisible( molecularDipoleVisible );
        setIsosurfaceType( isosurface );
        doScript( "hover off" ); // don't display labels when hovering over atoms
        updateAtomLabels();
        updateTranslucency();
    }

    private void setBallAndStick() {
        doScript( "wireframe 0.1 " ); // draw bonds as lines
        doScript( "spacefill 25%" ); // render atoms as a percentage of the van der Waals radius
        doScript( "color bonds black" ); // color for all bonds
    }

    public void setAtomLabelsVisible( boolean visible ) {
        atomLabelsVisible = visible;
        updateAtomLabels();
    }

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
            doScript( "set labelfront" ); // make labels float in front of atoms
            doScript( "color labels black" ); // color for all labels
            doScript( "font labels 18 sanserif" ); // font for all labels
        }
        else {
            doScript( "label off" );
        }
    }

    private void updateTranslucency() {
        String arg = ( bondDipolesVisible || molecularDipoleVisible ) ? "0.25" : "0.0"; // 0.0=opaque, 1.0=transparent
        doScript( "color atoms translucent " + arg );
        doScript( "color bonds translucent " + arg );
    }

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

    public void setPartialChargeVisible( boolean visible ) {
        partialChargeVisible = visible;
        updateAtomLabels();
    }

    public void setIsosurfaceType( SurfaceType isosurfaceType ) {
        this.isosurface = isosurfaceType;
        if ( isosurfaceType == SurfaceType.ELECTROSTATIC_POTENTIAL ) {
            if ( isHomogeneousDiatomic() ) {
                doScript( "isosurface VDW color white translucent" );
            }
            else {
                doScript( "isosurface VDW map MEP colorscheme \"RWB\" translucent" );
            }
        }
        else if ( isosurfaceType == SurfaceType.ELECTRON_DENSITY ) {
            if ( isHomogeneousDiatomic() ) {
                doScript( "isosurface VDW color white translucent" );
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
        Object status = doScriptStatus( SCRIPT_IS_HOMOGENEOUS_DIATOMIC );
        LOGGER.info( "isHomogeneousDiatomic status=[" + status.toString() + "]" );
        if ( status == null ) {
            throw new RuntimeException( "Jmol script returned null status" );
        }
        else {
            return parseBoolean( status );
        }
    }

    // Interrogates Jmol to determine element numbers of the atoms in the current molecule.
    public Integer[] getElementNumbers() {
        Object status = doScriptStatus( SCRIPT_GET_ELEMENT_NUMBERS );
//        LOGGER.info( "getElementNumbers status=[" + status.toString() + "]" );
        if ( status == null ) {
            throw new RuntimeException( "Jmol script returned null status" );
        }
        else {
            return parseIntegers( status );
        }
    }

    // Returns true if Jmol status is equal to the String RESULT_TRUE.
    private static boolean parseBoolean( Object status ) {
        return status.toString().trim().equals( RESULT_TRUE );
    }

    /*
     * Parses a string that contains integers separated by space or newlines.
     * The array returned may contain duplicates, since we don't know what the
     * caller wants to do with the results.
     */
    private static Integer[] parseIntegers( Object status ) {

        String statusString = status.toString().trim();

        // parse
        ArrayList<Integer> elementNumbers = new ArrayList<Integer>();
        StringTokenizer tokenizer = new StringTokenizer( statusString, " \n" );
        while ( tokenizer.hasMoreTokens() ) {
            String token = tokenizer.nextToken();
            int elementNumber = Integer.parseInt( token );
            elementNumbers.add( elementNumber );
        }

        // convert to int[]
        Integer[] array = new Integer[elementNumbers.size()];
        for ( int i = 0; i < array.length; i++ ) {
            array[i] = elementNumbers.get( i );
        }
        return array;
    }

    // test
    public static void main( String[] args ) {
        final PhetPCanvas canvas = new PhetPCanvas() {{
            setPreferredSize( new Dimension( 1024, 768 ) );
            setBackground( Color.LIGHT_GRAY );
            Property<Molecule3D> currentMolecule = new Property<Molecule3D>( new Molecule3D( "NH3", "ammonia", "jmol/nh3.sdf" ) );
            JmolViewerNode viewerNode = new JmolViewerNode( currentMolecule, getBackground(), new Dimension( 400, 400 ) );
            getLayer().addChild( viewerNode );
            viewerNode.setOffset( 100, 100 );
        }};
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
