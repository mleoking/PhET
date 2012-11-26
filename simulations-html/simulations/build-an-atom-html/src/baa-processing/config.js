require.config({
  deps: ["main"],

  paths: {
    vendor: "../js/vendor",
    plugins: "../js/plugins",

    underscore: "../js/vendor/underscore-min",
    processing: "../js/vendor/processing-1.4.1"

  },

  shim: {

    underscore: {
      exports: "_"
    },

    processing: {
      exports: "Processing"
    }

  }
});

