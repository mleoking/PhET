require.config( {

    deps: ["main"],

    paths: {
        easel:"../lib/easel-0.5.0",
        image:"../lib/image-0.2.1"
    },

    shim: {
        easel: {
            exports: "createjs"
        }
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );