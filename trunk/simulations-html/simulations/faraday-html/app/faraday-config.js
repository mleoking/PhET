// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file for the "Faraday's Electromagnetic Lab" sim.
 * Paths are relative to the location of this file.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

    deps: ["faraday-main"],

    paths: {
        easel:"../../../contrib/easel-0.5.0",
        i18n:"../../../contrib/i18n-2.0.1-phet",
        image:"../../../contrib/image-0.2.1"
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );