require.config( {
                    deps:["main"],

                    paths:{
                        vendor:"../js/vendor",
                        plugins:"../js/plugins",

                        underscore:"../js/vendor/underscore-min",
                        mymath:"mymath"

                    },

                    shim:{

                        underscore:{
                            exports:"_"
                        },

                        kinetic:{
                            exports:"Kinetic"
                        }

                    }
                } );

