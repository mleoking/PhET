// wavelength in nm
light.AIR = function( wavelength ) {
    return 1.0008; // index of refraction
};

light.sellmeier = function( params, wavelength ) {
    var lams = wavelength * wavelength / 1000000.0;
    return Math.sqrt( 1.0 + ( params[0] * lams ) / ( lams - params[3] ) + ( params[1] * lams ) / ( lams - params[4] ) + ( params[2] * lams ) / ( lams - params[5] ) );
};

light.BK7 = function( wavelength ) {
    return light.sellmeier( [1.03961212, 0.231792344, 1.01046945, 0.00600069867, 0.0200179144, 103.560653], wavelength );
};

light.FUSED_SILICA = function( wavelength ) {
    return light.sellmeier( [0.6961663, 0.4079426, 0.8974794, 0.00467914826, 0.0135120631, 97.9340025], wavelength );
};