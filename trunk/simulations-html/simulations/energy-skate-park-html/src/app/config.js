require.config( {
                    deps: ["main"],
//                    config: {i18n: {locale: 'ar'}},

                    paths: {
                        vendor: "../js/vendor",
                        plugins: "../js/plugins",

                        underscore: "../js/vendor/underscore-min",
                        image: "../js/vendor/image",
                        tpl: "../js/vendor/tpl"

                    },

                    shim: {

                        underscore: {
                            exports: "_"
                        },

                        kinetic: {
                            exports: "Kinetic"
                        }

                    },

                    urlArgs: new Date().getTime()  // cache buster to make browser refresh load all included scripts
                } );

