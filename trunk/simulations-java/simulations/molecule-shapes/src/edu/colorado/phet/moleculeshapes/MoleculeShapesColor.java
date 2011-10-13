// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.util.ColorProfile;
import edu.colorado.phet.moleculeshapes.util.JMEColorHandler;

import com.jme3.math.ColorRGBA;

/**
 * TODO: in need of a rewrite to common-code color-profile handling. this code is showing multiple holes
 */
public enum MoleculeShapesColor {
    BACKGROUND,
    CONTROL_PANEL_BORDER,
    CONTROL_PANEL_TITLE,
    CONTROL_PANEL_TEXT,
    ATOM_CENTER,
    ATOM,
    BOND,
    REAL_EXAMPLE_FORMULA,
    REAL_EXAMPLE_BORDER,
    LONE_PAIR_SHELL,
    LONE_PAIR_ELECTRON,
    MOLECULAR_GEOMETRY_NAME,
    ELECTRON_GEOMETRY_NAME,
    BOND_ANGLE_READOUT,
    BOND_ANGLE_SWEEP,
    BOND_ANGLE_ARC,
    REMOVE_BUTTON_TEXT,
    REMOVE_BUTTON_BACKGROUND,
    SUN,
    MOON;

    public static ColorProfile<MoleculeShapesColor> DEFAULT = new ColorProfile<MoleculeShapesColor>( "Screen Mode" ) {{ // TODO: i18n
        put( BACKGROUND, Color.BLACK );
        put( CONTROL_PANEL_BORDER, new Color( 210, 210, 210 ) );
        put( CONTROL_PANEL_TITLE, new Color( 240, 240, 240 ) );
        put( CONTROL_PANEL_TEXT, new Color( 230, 230, 230 ) );
        put( ATOM_CENTER, new Color( 159, 102, 218 ) );
        put( ATOM, Color.WHITE );
        put( BOND, Color.WHITE );
        put( REAL_EXAMPLE_FORMULA, new Color( 230, 230, 230 ) );
        put( REAL_EXAMPLE_BORDER, new Color( 60, 60, 60 ) );
        put( LONE_PAIR_SHELL, new Color( 1, 1, 1, 0.7f ) );
        put( LONE_PAIR_ELECTRON, new Color( 1, 1, 0, 0.8f ) );
        put( MOLECULAR_GEOMETRY_NAME, new Color( 255, 255, 140 ) );
        put( ELECTRON_GEOMETRY_NAME, new Color( 255, 204, 102 ) );
        put( BOND_ANGLE_READOUT, Color.WHITE );
        put( BOND_ANGLE_SWEEP, Color.GRAY );
        put( BOND_ANGLE_ARC, Color.RED );
        put( REMOVE_BUTTON_TEXT, Color.BLACK );
        put( REMOVE_BUTTON_BACKGROUND, Color.ORANGE );
        put( SUN, new Color( 0.8f, 0.8f, 0.8f ) );
        put( MOON, new Color( 0.6f, 0.6f, 0.6f ) );
    }};

    public static void switchToBasicColors() {
        DEFAULT.put( BACKGROUND, new Color( 252, 216, 124 ) );
        DEFAULT.put( CONTROL_PANEL_BORDER, new Color( 30, 30, 30 ) );
        DEFAULT.put( CONTROL_PANEL_TITLE, Color.BLACK );
        DEFAULT.put( CONTROL_PANEL_TEXT, Color.BLACK );
        DEFAULT.put( SUN, new Color( 153, 153, 153 ) );
        DEFAULT.put( MOLECULAR_GEOMETRY_NAME, new Color( 54, 47, 14 ) );
        DEFAULT.put( BOND_ANGLE_READOUT, Color.BLACK );
        DEFAULT.put( REMOVE_BUTTON_BACKGROUND, new Color( 204, 204, 204 ) );
        DEFAULT.apply( handler );
    }

    // TODO: better handling of non-specified colors!
    public static ColorProfile<MoleculeShapesColor> PROJECTOR = new ColorProfile<MoleculeShapesColor>( "Projector Mode" ) {{ // TODO: i18n
        put( BACKGROUND, Color.WHITE );
        put( CONTROL_PANEL_BORDER, new Color( 30, 30, 30 ) );
        put( CONTROL_PANEL_TITLE, Color.BLACK );
        put( CONTROL_PANEL_TEXT, Color.BLACK );
        put( ATOM, new Color( 153, 153, 153 ) );
        put( REAL_EXAMPLE_BORDER, new Color( 230, 230, 230 ) );
        put( REAL_EXAMPLE_FORMULA, Color.BLACK );
        put( LONE_PAIR_SHELL, new Color( 0.7f, 0.7f, 0.7f, 0.7f ) );
        put( LONE_PAIR_ELECTRON, new Color( 0, 0, 0, 0.8f ) );
        put( MOLECULAR_GEOMETRY_NAME, new Color( 102, 0, 204 ) );
        put( ELECTRON_GEOMETRY_NAME, new Color( 0, 102, 102 ) );
        put( BOND_ANGLE_READOUT, Color.BLACK );
        put( BOND_ANGLE_SWEEP, Color.GRAY.brighter() );
        put( BOND_ANGLE_ARC, Color.RED );
    }};

    public static JMEColorHandler<MoleculeShapesColor> handler = new JMEColorHandler<MoleculeShapesColor>() {{
        setProfile( DEFAULT );
    }};

    public Color get() {
        return handler.get( this );
    }

    public Property<Color> getProperty() {
        return handler.getProperty( this );
    }

    public ColorRGBA getRGBA() {
        return handler.getRGBA( this );
    }

    public Property<ColorRGBA> getRGBAProperty() {
        return handler.getRGBAProperty( this );
    }

    public void addColorObserver( final VoidFunction1<Color> callback ) {
        getProperty().addObserver( callback );
    }

    public void addColorRGBAObserver( final VoidFunction1<ColorRGBA> callback ) {
        getRGBAProperty().addObserver( JMEUtils.jmeObserver( new Runnable() {
            public void run() {
                callback.apply( getRGBA() );
            }
        } ) );
    }

}
