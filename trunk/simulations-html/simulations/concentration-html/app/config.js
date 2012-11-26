require.config({
    deps: ['main'],
    
    paths: {
        common: '../common',
        
        easel:           '../lib/easel-0.5.0',
        image:           '../lib/image-0.2.1',
        jquery:          '../lib/jquery-1.8.3',
        'jquery.mobile': '../lib/jquery.mobile-1.2.0',
        underscore:      '../lib/underscore-1.4.2',
    },
    
    shim: {
        underscore: { exports: "_" },
        easel:      { exports: "createjs" },
        jquery:     { exports: "$" },
    },

    urlArgs: new Date().getTime() // add cache buster query string to make browser refresh actually reload everything
});
