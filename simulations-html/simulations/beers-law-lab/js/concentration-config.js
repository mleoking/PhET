// Copyright 2002-2012, University of Colorado

/*
 * RequireJS configuration file for the "Beer's Law Lab" sim.
 * Paths are relative to the location of this file.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require.config( {

    deps: ["concentration-main"],

    paths: {
        // contrib
        easel:"../../../contrib/easel-0.5.0",
        i18n:"../../../contrib/i18n-2.0.1-phet",
        image:"../../../contrib/image-0.2.1",
        tpl: "../../../contrib/tpl-0.2",

        // common
        phetcommon:"../../../common/phetcommon/js",
        phetcommon_html:"../../../common/phetcommon/html",
        'easel-phet':"../../../common/easel-phet/js"
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );