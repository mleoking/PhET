require.config( {
                    deps: ["main"],
                    config: {i18n: {locale: 'ar'}},

                    paths: {
                        vendor: "../js/vendor",
                        plugins: "../js/plugins",

                        underscore: "../js/vendor/underscore-min",
                        image: "../js/vendor/image"
                    },

                    shim: {

                        underscore: {
                            exports: "_"
                        },

                        kinetic: {
                            exports: "Kinetic"
                        }

                    }
                } );

