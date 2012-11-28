// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file.
 * Paths are relative to the location of this file.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

    deps: ["main"],

    paths: {
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );