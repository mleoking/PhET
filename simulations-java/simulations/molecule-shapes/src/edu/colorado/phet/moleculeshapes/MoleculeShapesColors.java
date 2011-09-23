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
    BACKGROUND;

    public static ColorProfile<MoleculeShapesColors> DEFAULT = new ColorProfile<MoleculeShapesColors>( "Default Colors" ) {{ // TODO: i18n
        add( BACKGROUND, Color.BLACK );
    }};

    public static ColorProfile<MoleculeShapesColors> PROJECTOR = new ColorProfile<MoleculeShapesColors>( "Projector Colors" ) {{ // TODO: i18n
        add( BACKGROUND, Color.WHITE );
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
