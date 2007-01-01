package edu.colorado.phet.rotation.graphs;

/**
 * User: Sam Reid
 * Date: Jan 1, 2007
 * Time: 11:34:09 AM
 * Copyright (c) Jan 1, 2007 by Sam Reid
 */
public class CombinedGraphComponent {
    private String name;
    private String rangeAxisTitle;

    public CombinedGraphComponent( String name, String rangeAxisTitle ) {
        this.name = name;
        this.rangeAxisTitle = rangeAxisTitle;
    }

    public String getName() {
        return name;
    }

    public String getRangeAxisTitle() {
        return rangeAxisTitle;
    }

}
