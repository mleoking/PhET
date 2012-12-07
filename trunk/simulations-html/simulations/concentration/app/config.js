require.config({
    deps: ['main'],
    
    paths: {
        common: '../common',
        
        easel:           '../../../contrib/easel-dev',
        image:           '../../../contrib/image-0.2.1',
        underscore:      '../../../contrib/underscore-1.4.2',
    },
    
    shim: {
        underscore: { exports: "_" },
        easel:      { exports: "createjs" },
    },

    urlArgs: new Date().getTime() // add cache buster query string to make browser refresh actually reload everything
});
