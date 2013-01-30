// Copyright 2002-2013, University of Colorado

/**
 * Model of a solute.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
        ],
        function () {

            /**
             * Constructor.
             * @param {String} name
             * @param {String} formula
             * @param {SoluteColorScheme} colorScheme
             * @param {Number} stockSolutionConcentration
             * @param {Color} particleColor
             * @param {Number} particleSize
             * @param {Number} particlesPerMole
             * @constructor
             */
            function Solute( name, formula, colorScheme, stockSolutionConcentration, particleColor, particleSize, particlesPerMole ) {
                this.name = name;
                this.formula = formula;
                this.colorScheme = colorScheme;
                this.stockSolutionConcentration = stockSolutionConcentration;
                this.particleColor = particleColor;
                this.particleSize = particleSize;
                this.particlesPerMole = particlesPerMole;
            }

            return Solute;
        } );
