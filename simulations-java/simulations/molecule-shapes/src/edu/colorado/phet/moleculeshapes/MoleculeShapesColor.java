// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.util.ColorProfile;
import edu.colorado.phet.moleculeshapes.util.JMEColorHandler;

import com.jme3.math.ColorRGBA;

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
        add( BACKGROUND, Color.BLACK );
        add( CONTROL_PANEL_BORDER, new Color( 210, 210, 210 ) );
        add( CONTROL_PANEL_TITLE, new Color( 240, 240, 240 ) );
        add( CONTROL_PANEL_TEXT, new Color( 230, 230, 230 ) );
        add( ATOM_CENTER, new Color( 159, 102, 218 ) );
        add( ATOM, Color.WHITE );
        add( BOND, Color.WHITE );
        add( REAL_EXAMPLE_FORMULA, new Color( 230, 230, 230 ) );
        add( REAL_EXAMPLE_BORDER, new Color( 60, 60, 60 ) );
        add( LONE_PAIR_SHELL, new Color( 1, 1, 1, 0.7f ) );
        add( LONE_PAIR_ELECTRON, new Color( 1, 1, 0, 0.8f ) );
        add( MOLECULAR_GEOMETRY_NAME, new Color( 255, 255, 140 ) );
        add( ELECTRON_GEOMETRY_NAME, new Color( 255, 204, 102 ) );
        add( BOND_ANGLE_READOUT, Color.WHITE );
        add( BOND_ANGLE_SWEEP, Color.GRAY );
        add( BOND_ANGLE_ARC, Color.RED );
        add( REMOVE_BUTTON_TEXT, Color.BLACK );
        add( REMOVE_BUTTON_BACKGROUND, Color.ORANGE );
        add( SUN, new Color( 0.8f, 0.8f, 0.8f ) );
        add( MOON, new Color( 0.6f, 0.6f, 0.6f ) );
    }};

    // TODO: better handling of non-specified colors!
    public static ColorProfile<MoleculeShapesColor> PROJECTOR = new ColorProfile<MoleculeShapesColor>( "Projector Mode" ) {{ // TODO: i18n
        add( BACKGROUND, Color.WHITE );
        add( CONTROL_PANEL_BORDER, new Color( 30, 30, 30 ) );
        add( CONTROL_PANEL_TITLE, Color.BLACK );
        add( CONTROL_PANEL_TEXT, Color.BLACK );
        add( ATOM, new Color( 153, 153, 153 ) );
        add( REAL_EXAMPLE_BORDER, new Color( 230, 230, 230 ) );
        add( REAL_EXAMPLE_FORMULA, Color.BLACK );
        add( LONE_PAIR_SHELL, new Color( 0.7f, 0.7f, 0.7f, 0.7f ) );
        add( LONE_PAIR_ELECTRON, new Color( 0, 0, 0, 0.8f ) );
        add( MOLECULAR_GEOMETRY_NAME, new Color( 102, 0, 204 ) );
        add( ELECTRON_GEOMETRY_NAME, new Color( 0, 102, 102 ) );
        add( BOND_ANGLE_READOUT, Color.BLACK );
        add( BOND_ANGLE_SWEEP, Color.GRAY.brighter() );
        add( BOND_ANGLE_ARC, Color.RED );
    }};

    // TODO: add high-contrast?

    public static List<ColorProfile<MoleculeShapesColor>> PROFILES = new ArrayList<ColorProfile<MoleculeShapesColor>>() {{
        add( DEFAULT );
        add( PROJECTOR );
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
