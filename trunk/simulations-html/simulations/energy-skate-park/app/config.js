require.config( {
                    deps: ['main'],

                    paths: {
                        common: '../common',

                        easel: '../../../contrib/easel-0.5.0',
                        image: '../../../contrib/image-0.2.1',
                        jquery: '../../../contrib/jquery-1.8.3',
                        'jquery.mobile': '../../../contrib/jquery.mobile-1.2.0',
                        underscore: '../../../contrib/underscore-1.4.2',
                        tpl: "../js/vendor/tpl"
                    },

                    shim: {
                        underscore: { exports: "_" },
                        easel: { exports: "createjs" },
                        jquery: { exports: "$" }
                    },

                    urlArgs: new Date().getTime() // add cache buster query string to make browser refresh actually reload everything
                } );