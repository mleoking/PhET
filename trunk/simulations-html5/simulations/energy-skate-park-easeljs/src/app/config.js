require.config( {
                    deps:["main"],

                    paths:{
                        vendor:"../js/vendor",
                        plugins:"../js/plugins",

                        underscore:"../js/vendor/underscore-min"


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

