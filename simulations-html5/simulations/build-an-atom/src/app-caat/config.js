require.config({
  deps: ["main"],

  paths: {
    vendor: "../js/vendor",
    plugins: "../js/plugins",

    underscore: "../js/vendor/underscore-min",
    caat: "../js/vendor/caat"

  },

  shim: {

    underscore: {
      exports: "_"
    },

    caat: {
      exports: "CAAT"
    }

  }
});
