require.config( {

    deps: ["main"],

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );