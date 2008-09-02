/**
 * Class: GreenhouseConfig
 * Class: edu.colorado.phet.greenhouse
 * User: Ron LeMaster
 * Date: Oct 10, 2003
 * Time: 10:12:46 PM
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;

import edu.colorado.phet.greenhouse.common_greenhouse.view.ApparatusPanel;

public class GreenhouseConfig {

    //
    // Static fields and methods
    //
    public static final double maxIncomingRate = 1E-1;
    public static double defaultSunPhotonProductionRate = 0.034;
    public static double maxEarthEmissivity = 10;
    public static double defaultEarthEmissivity = maxEarthEmissivity / 2;

    // Earth parameters
    public static final double earthBaseTemperature = 251;
    public static final double earthRadius = 6370;
    public static double venusBaseTemperature = 750;

    // Photon parameters. Note that the speedOfLight is for visualization purposes only,
    // not energy calculations.
    public static double photonRadius = .1;
    public static final double speedOfLight = .01;

    public static double greenhouseGasConcentrationToday = 0.0052;
    public static double greenhouseGasConcentration1750 = 0.0050;
    public static double greenhouseGasConcentrationIceAge = 0.0044;
    public static double maxGreenhouseGasConcentration = 0.009;
    public static double minGreenhouseGasConcentration = 0.00001;
    public static double defaultGreenhouseGasConcentration = minGreenhouseGasConcentration;

    public static double sunlightWavelength = 400E-9;
    public static double irWavelength = 850E-9;
    public static double debug_wavelength = 1;

    //
    // Physical constants
    //

    // Plank's constant
    public static final double h = 6.6310E-34;
    // Boltzmann's constant
    public static final double k = 1.3806503E-23;
    // Speed of light (m/s)
    public static final double C = 0.299792458E9;

    public static final double sigma = 5.67E-8;

    // Parameter for the units displayed by the thermometer
    public static final Object FAHRENHEIT = new Object();
    public static final Object CELSIUS = new Object();
    public static Object TEMPERATURE_UNITS = FAHRENHEIT;

    // Graphics-related parameters
    public static double EARTH_BASE_LAYER = ApparatusPanel.LAYER_DEFAULT;
    public static double SUNLIGHT_PHOTON_GRAPHIC_LAYER = EARTH_BASE_LAYER - 1;
    public static double IR_PHOTON_GRAPHIC_LAYER = SUNLIGHT_PHOTON_GRAPHIC_LAYER - 0.000000001;
    public static double ATMOSPHERE_GRAPHIC_LAYER = SUNLIGHT_PHOTON_GRAPHIC_LAYER - 3;
    public static double EARTH_BACKDROP_LAYER = SUNLIGHT_PHOTON_GRAPHIC_LAYER - 2;
    public static final Color PANEL_BACKGROUND_COLOR = new Color( 206, 206, 206 );
}
