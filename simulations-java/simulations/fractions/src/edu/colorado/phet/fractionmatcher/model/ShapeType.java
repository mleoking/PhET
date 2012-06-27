package edu.colorado.phet.fractionmatcher.model;

/**
 * The different shapes that can be displayed.
 *
 * @author Sam Reid
 */
public enum ShapeType {

    //REVIEW Instances of an enum are constants and should follow the Java conventions for constants, uppercase. Also inconsistent with what you've done elsewhere, eg Mode and FillType.

    //"Easy ones"
    horizontalBars, verticalBars, pies,

    //"More difficult ones"
    plusses, grid, pyramid,
    polygon, tetris, flower, letterLShapes, interleavedLShapes, ringOfHexagons, ninjaStar
}