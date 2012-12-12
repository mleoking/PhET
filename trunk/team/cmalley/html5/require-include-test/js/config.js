require.config( {

    deps: ["main"],

    paths: {
       modelpath: "./model"
    },

    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
} );