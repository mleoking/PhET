// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

    deps: ["faraday-main"],

    paths: {
        easel: "../vendor/easeljs-0.5.0.min",
        image: "../vendor/image-0.2.1"
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );