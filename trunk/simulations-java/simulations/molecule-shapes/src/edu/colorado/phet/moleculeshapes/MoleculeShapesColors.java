// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.util.ColorProfile;
import edu.colorado.phet.moleculeshapes.util.JMEColorHandler;

import com.jme3.math.ColorRGBA;

public enum MoleculeShapesColors {
    BACKGROUND,
    CONTROL_PANEL_BORDER,
    CONTROL_PANEL_TITLE,
    ATOM_CENTER,
    ATOM;

    public static ColorProfile<MoleculeShapesColors> DEFAULT = new ColorProfile<MoleculeShapesColors>( "Screen Mode" ) {{ // TODO: i18n
        add( BACKGROUND, Color.BLACK );
        add( CONTROL_PANEL_BORDER, new Color( 210, 210, 210 ) );
        add( CONTROL_PANEL_TITLE, new Color( 240, 240, 240 ) );
        add( ATOM_CENTER, Color.RED );
        add( ATOM, Color.WHITE );
    }};

    // TODO: better handling of non-specified colors!
    public static ColorProfile<MoleculeShapesColors> PROJECTOR = new ColorProfile<MoleculeShapesColors>( "Projector Mode" ) {{ // TODO: i18n
        add( BACKGROUND, Color.WHITE );
        add( CONTROL_PANEL_BORDER, new Color( 30, 30, 30 ) );
        add( CONTROL_PANEL_TITLE, Color.BLACK );
        add( ATOM, Color.GRAY );
    }};

    // TODO: add high-contrast?

    public static List<ColorProfile<MoleculeShapesColors>> PROFILES = new ArrayList<ColorProfile<MoleculeShapesColors>>() {{
        add( DEFAULT );
        add( PROJECTOR );
    }};

    public static JMEColorHandler<MoleculeShapesColors> handler = new JMEColorHandler<MoleculeShapesColors>() {{
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

}
