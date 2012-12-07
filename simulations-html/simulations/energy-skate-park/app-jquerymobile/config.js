require.config( {
                    deps: ['main'],

                    paths: {
                        common: '../common',

                        easel: '../../../contrib/easel-0.5.0',
                        image: '../../../contrib/image-0.2.1',
                        jquery: '../../../contrib/jquery-1.8.3',
                        'jquery.mobile': '../../../contrib/jquery.mobile-1.2.0',
                        underscore: '../../../contrib/underscore-1.4.2',
                        tpl: "../../../contrib/tpl-0.2",
                        numeric: "../../../contrib/numeric-1.2.3",
                        i18n: "../../../contrib/i18n-2.0.1-phet"
                    },

                    shim: {
                        underscore: { exports: "_" },
                        easel: { exports: "createjs" },
                        jquery: { exports: "$" },
                        numeric: {exports: "numeric"}
                    },

                    urlArgs: new Date().getTime() // add cache buster query string to make browser refresh actually reload everything
                } );
