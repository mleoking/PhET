// Copyright 2013, University of Colorado

/**
 *  A solvent (in this case a liquid) that dissolves another liquid (the solute) to create a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [], function () {

    /**
     * Constructor.
     * @param {String} name
     * @param {String} formula
     * @param {Color} color
     * @constructor
     */
    function Solvent( name, formula, color ) {
        this.name = name;
        this.formula = formula;
        this.color = color;
    }

    return Solvent;
} );
