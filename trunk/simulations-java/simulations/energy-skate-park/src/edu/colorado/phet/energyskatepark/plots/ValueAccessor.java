// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.plots;

import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 16, 2005
 * Time: 9:42:43 PM
 */

public abstract class ValueAccessor {
    private String name;
    private String html;
    private String units;
    private String unitAbbreviation;
    private Color color;
    private String fullName;

    public static final String joules = EnergySkateParkStrings.getString( "units.joules" );
    public static final String joulesAbbreviation = "J";

    protected ValueAccessor( String name, String html, String units, String unitAbbreviation, Color color, String fullName ) {
        this.name = name;
        this.html = html;
        this.units = units;
        this.unitAbbreviation = unitAbbreviation;
        this.color = color;
        this.fullName = fullName;
    }

    public abstract double getValue( Object model );

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public String getUnits() {
        return units;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUnitsAbbreviation() {
        return unitAbbreviation;
    }

    public String getHTML() {
        return html;
    }

}
