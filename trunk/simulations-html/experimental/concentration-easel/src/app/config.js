require.config({
    deps: ['main'],
    
    paths: {
        common: '../common',
        lib:    '../lib',
    },
    
    shim: {
        'lib/underscore': { exports: "_" },
        'lib/easel':      { exports: "createjs" },
        'lib/jquery':     { exports: "$" },
    },

    urlArgs: new Date().getTime() // add cache buster query string to make browser refresh actually reload everything
});
