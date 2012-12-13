/**
 * @author Mobile.Lab (http://mlearner.com)
 **/

window.phet = {};

(function($, name, module) {

  var touch = !!('ontouchstart' in document.documentElement);

  var _mousedown = (touch) ? 'touchstart' : 'mousedown';
  var _mousemove = (touch ) ? 'touchmove' : 'mousemove';
  var _mouseup = (touch) ? 'touchend' : 'mouseup';

  // ---- board constructor ----
  var massSpring = function(id) {
    var self = this;
    // link to main holder
    this.holder = $('#' + id).addClass(name).addClass('noselection');

    this.consts = {
      bodies : [0.25, 0.05, 0.07, 0.16, 0.31, 0.1, 0.1],
      springs : [10, 10, 10],
      f : 500, //px units per meter,
      dtt : 10, //time increment, ms
      koefE : 20, //svg units per px
      s : 250, //energy scale: s pixels = 1 joule,
      defaultW : 720,
      defaultH : 500
    };

    this.params = {
      energyShow : 0,
      timeSpeed : 1,
      grav : 9.8,
      sound : 0,
      timer : 0,
      fric : 0, //friction of springs
      stiff : 0, //stiffness for 3 spring
      viewScale : 1, //view scale to defaul value
      timerPaused : 1,
      width : self.consts.defaultW,
      height : self.consts.defaultH
    };
    this.dragObj = null;

    //objects to handle each tick
    this.objs = [];
    this.interval = setInterval(function() {
      self.nextStep(self);
    }, this.consts.dtt);
    this.cTime = -1, this.oTime = -1;

    this.initSound();
    this.findView();
    this.createModel();
    this.bindUserEvents();

    this.resize();

    this.setStiff(5), this.setFric(5);
  };

  //scale svg to fit window
  massSpring.prototype.resize = function() {
    var oldScale = this.params.viewScale;
    //make svg fit to window
    this.resizeToMax();
    //recalculate model
    this.resizeModel(oldScale);
  };

  massSpring.prototype.resizeToMax = function() {
    //set svg max size
    var self = this;
    var k = this.consts.defaultH / this.consts.defaultW;
    var wk = $(window).height() / $(window).width();
    if (k <= wk) {
      this.params.viewScale = $(window).width() / this.consts.defaultW;
    } else {
      this.params.viewScale = $(window).height() / this.consts.defaultH;
    }
    this.holder.find("svg").css({
      width : Math.floor(self.consts.defaultW * self.params.viewScale) + "px",
      height : Math.floor(self.consts.defaultH * self.params.viewScale) + "px"
    });
    this.holder.find(".mainGroup")[0].setAttribute("transform", "scale(" + self.params.viewScale + " " + self.params.viewScale + ")");

    //resize about and place it Correctly
    this.resizeHTMLComponents();
  };

  massSpring.prototype.resizeHTMLComponents = function() {
    var ab = this.layer.aboutButton[0], sc = this.params.viewScale;
    scaleHTML(ab, sc);
    ab.style.left = this.holder.find("svg").offset().left + this.holder.find("svg").width() / 20 + "px";
    ab.style.top = this.holder.offset().top + this.holder.find("svg").height() - ab.offsetHeight - 5 * sc + "px";
  };

  massSpring.prototype.resizeModel = function(oldScale) {
    //recalculate all needed view params of spring
    var container = this;
    this.hoffset = this.holder.find("svg").offset();

    for (var i = this.layer.springs.length; i--; ) {
      var spr = this.layer.springs[i];
      spr.hook.h = spr.hook[0].getBoundingClientRect().height / (container.consts.koefE * container.consts.f);

      var rect = spr.view.children().eq(1).show()[0].getBoundingClientRect();
      spr.width = 1 * spr.view[0].getAttribute("data-width") * container.params.viewScale;
      //top y, middle x, tolerance, unchanged y, new y
      spr.sy = (rect.top - container.hoffset.top);
      spr.x = rect.left - container.hoffset.left + spr.width / 2;
      spr.tl = spr.width / 2;
      spr.y = spr.sy + 1 * spr.view[0].getAttribute("data-height") * container.params.viewScale;
      spr.oy = spr.y;
      spr.svgHeight = spr.y - spr.sy;
    }

    this.params.width = this.consts.defaultW * this.params.viewScale;
    this.params.height = this.consts.defaultH * this.params.viewScale;

  };

  massSpring.prototype.findView = function() {
    //svg elements
    this.layer = {
      svg : this.holder.find("svg")[0],
      timer : this.holder.find(".timer"),
      timerValue : this.holder.find(".timerValue"),
      bodies : [],
      springs : [],
      energyDisplay : this.holder.find(".energyDisplay"),
      help : this.holder.find(".help"),
      hangMeText : this.holder.find(".hangMeText"),
      pausedText : this.holder.find(".pausedText"),
      aboutButton : this.holder.find(".aboutButton"),
      aboutPopup : this.holder.find(".aboutPopup"),
      aboutSoftware : this.holder.find(".aboutSoftware")
    };

    //energyDisplay bars
    this.layer.energyDisplay.KE = this.layer.energyDisplay.find(".KE")[0];
    this.layer.energyDisplay.Q = this.layer.energyDisplay.find(".Thermal")[0];
    this.layer.energyDisplay.PEgrav = this.layer.energyDisplay.find(".PEgrav")[0];
    this.layer.energyDisplay.PEelas = this.layer.energyDisplay.find(".PEelas")[0];
    this.layer.energyDisplay.Etot = this.layer.energyDisplay.find(".Etot").children();
    this.layer.totEnergy = this.layer.energyDisplay.find(".totEnergy")[0];

    //fillin about browser and os version
    this.holder.find(".browserVersion").html(BrowserDetect.browser);
    this.holder.find(".osVersion").html(BrowserDetect.OS);
  };

  massSpring.prototype.createModel = function() {
    var self = this;
    this.holder.find(".weights").children().each(function(index) {
      self.layer.bodies.push(new self.body(self.consts.bodies[index], $(this), self));
    });

    this.holder.find(".spring").each(function(index) {
      self.layer.springs.push(new self.spring(self.consts.springs[index], $(this), self));
    });
  };

  massSpring.prototype.bindUserEvents = function() {
    var self = this;
    //bind event to buttons controls
    this.bindButtons();

    //bind events to all draggable objects
    this.bindDraggable();

    this.holder.bind("touchstart", function(evt) {
      evt.preventDefault();
    });

    window.addEventListener("resize", function(evt) {
      self.resize();
    }, false);

  };

  massSpring.prototype.bindButtons = function() {
    var self = this;
    self.radioCallbacks = {
      energyShow : function(name) {
        if (self.params[name] === 0) {
          self.layer.energyDisplay.hide();
        } else {
          self.layer.energyDisplay.show();
          self.holder.find(".energySpringNumber")[0].firstChild.nodeValue = "Energy of "+ self.params[name];
          self.setTotEnergy(self.layer.springs[self.params[name] - 1]);
        }
      },
      timeSpeed : function(name) {
        if (self.params[name] === 0) {
          self.layer.pausedText.show();
        } else {
          self.layer.pausedText.hide();
        }
      },
      grav : function(name) {
      }
    };

    //show radioButtons then
    // set Event listener to handle click on each group of radiobuttons
    // name of group = name of param in model - attribute "name"
    // value of button - attribute "value"
    // then we hide all selected svg views and show only selected
    // then call specificname callback
    this.holder.find(".radioButtonsView").show();
    this.holder.find(".radioButtons").each(function(index) {
      var childs = $(this).children(), name = this.getAttribute("name");
      childs.each(function() {
        this.addEventListener(_mouseup, function(evt) {
          evt.preventDefault();
          self.params[name] = 1 * this.getAttribute("value");
          childs.find(".checked").each(function(index) {
            this.setAttribute("opacity", 0);
          });
          $(this).find(".checked")[0].setAttribute("opacity", 1);
          //call name-depended function
          self.radioCallbacks[name](name);
        }, false);
      });
    });

    // set Event listener to handle click on checkboxes
    // name of group = name of param in model - attribute "name"
    // then we switch view of box
    var chBut = this.holder.find(".checkButtons");

    chBut.show().each(function(index) {
      var checkedGroup = $(this).find(".checked")[0], name = this.getAttribute("name");
      this.addEventListener(_mouseup, function(evt) {
        evt.preventDefault();
        self.params[name] = (self.params[name] + 1) % 2;
        checkedGroup.setAttribute("opacity", self.params[name]);
        if (name == "timer") {
          self.layer.timer[0].setAttribute("opacity", self.params[name]);
        } else {
          //self.sound.load();
          self.sound.play();
          self.sound.pause();
        }
      }, false);
    });

    //timer

    //set startDate of timer (oTime), difference between start moment and current - cTime
    //if !timerPaused we don't need to calculate timer
    this.layer.timer.find(".start").bind(_mousedown, function(evt) {
      self.oTime = new Date().getTime();
      self.params.timerPaused = 0;
      self.layer.timer.find(".pause").show();
      return false;
    });

    this.layer.timer.find(".stop").bind(_mousedown, function(evt) {
      self.params.timerPaused = 1;
      self.layer.timer.find(".pause").hide();
      self.layer.timer.find(".start").show();
      self.cTime = 0;
      self.layer.timerValue[0].textContent = "00:00:00";
      return false;
    });

    this.layer.timer.find(".pause").bind(_mousedown, function(evt) {
      $(this).hide();
      self.params.timerPaused = 1;
      return false;
    });

    //show help, hide help
    this.holder.find(".button.helpButton").bind(_mousedown, function(evt) {
      var txt = $(this).find("text")[0].firstChild.nodeValue;
      if (txt == "Show Help") {
        self.layer.help.show();
        $(this).find("text")[0].firstChild.nodeValue = "Hide Help";
      } else {
        self.layer.help.hide();
        $(this).find("text")[0].firstChild.nodeValue = "Show Help";
      }
    });
    //about buttons
    this.layer.aboutButton.bind(_mousedown, function() {
      $.blockUI({
        message : $(".aboutPopup")
      });
    });

    this.layer.aboutButton.bind(_mousedown, function() {
      $.blockUI({
        message : $(".aboutPopup")
      });
    });

    this.holder.find(".okButton").bind(_mousedown, function() {
      $.unblockUI();
    });

    this.holder.find(".closeButton").bind(_mousedown, function() {
      $.unblockUI();
    });

    this.holder.find(".closeButtonS").bind(_mousedown, function() {
      $.blockUI({
        message : $(".aboutPopup")
      });
    });

    this.holder.find(".softwareButton").bind(_mousedown, function() {
      $.blockUI({
        message : $(".aboutSoftware")
      });
      self.layer.aboutSoftware.find("iframe").css("height", self.layer.aboutSoftware.find("iframe").parent().height());
    });
  };

  massSpring.prototype.bindDraggable = function() {
    var self = this;

    //on mouseup we are clearing dragObject,
    // if body : pushing to handler stack and checking if we must slip it to spring
    window.document.addEventListener(_mouseup, function(evt) {
      if (self.dragObj) {
        if (self.dragObj.type == "body") {
          var o = self.layer.bodies[self.dragObj.prevAll().length];
          var tr = self.getXYfromTransform(o.view[0].getAttribute("transform"));
          o.cxy = {
            x : tr.x / self.params.viewScale,
            y : tr.y / self.params.viewScale
          };
          o.remove = false;
          self.objs.push(o);
          self.checkBound(o);
        }
        self.dragObj = null;
      }
    }, false);

    //mouse down on any object that we can drag (class='draggable' in svg)
    //setting start coords of mouse, transformation of element
    //if body : remove from handler stack
    this.holder.find(".draggable").each(function(index) {
      this.addEventListener(_mousedown, function(evt) {
        var o = $(this);
        //mouse start coords
        self.sc = getCoords(evt, self.hoffset);
        o.xy = self.getXYfromTransform(o[0].getAttribute("transform"));

        //getting container of object, then determining how far can we drag object before end of svg element
        // dx1 = dx left, dy2 = dy bottom
        var box = o[0].getBoundingClientRect();

        o.dxl = self.hoffset.left - box.left;
        o.dxr = self.hoffset.left + self.params.width - (box.left + box.width);
        o.dyt = self.hoffset.top - box.top;
        o.dyb = self.hoffset.top + self.params.height - (box.top + box.height);

        self.dragObj = o;
        self.dragObj.type = o[0].getAttribute("type");

        if (self.dragObj.type == "body") {
          o = self.layer.bodies[self.dragObj.prevAll().length];
          self.dragObj.model = o;
          o.remove = true;
          self.layer.hangMeText.hide();
        }
        evt.preventDefault();
      }, false);
    });

    //mousemove = if we have dragObj transfer control to mousemove function
    this.holder[0].addEventListener(_mousemove, function(evt) {
      if (self.dragObj) {
        self.mousemove(evt);
        evt.preventDefault();
      }
    }, false);
  };
  //slider params -> svg borders for slider
  var slMax = 1190, slStep = slMax * 2 / 10;

  massSpring.prototype.mousemove = function(evt) {
    //mousemove and we have dragObject
    //changing attribute transform of element according to mousecoords
    //if body on spring, changing spring too
    var o = this.dragObj, ec = getCoords(evt, this.hoffset);

    //check if we inside box svg
    var mdx = ec.x - this.sc.x, mdy = ec.y - this.sc.y;
    if (mdx < o.dxl || mdx > o.dxr || mdy < o.dyt || mdy > o.dyb) {
      //do nothing
      return;
    }

    var dx = (o.xy.x + (ec.x - this.sc.x) * this.consts.koefE) / this.params.viewScale;
    var dy = (o.xy.y + (ec.y - this.sc.y) * this.consts.koefE) / this.params.viewScale;
    if (this.dragObj.type == "slider") {
      dy = 0, dx = Math.max(-slMax, Math.min(dx, slMax));
      var slVal = Math.round((dx + slMax) / slStep);
      dx = slVal * slStep - slMax;
      this["set"+this.dragObj[0].getAttribute("name")](slVal);
    }
    o[0].setAttribute("transform", "translate(" + dx + " " + dy + ")");
    if (o.type == "body") {
      if (!o.model.spring) {
        //free
        this.checkBound(o.model, true);
      } else {
        //slipped to spring
        var spr = o.model.spring;
        var bbox = o.model.hook[0].getBoundingClientRect();
        //for firefox 3.6, it can't change prop left of bbox
        box = {
          left : bbox.left - this.hoffset.left,
          top : bbox.top - this.hoffset.top,
          width : bbox.width,
          height : bbox.height
        };
        o.model.vel = 0;

        if (spr.isIntersect(box)) {
          //scale of spring
          var scale = (spr.y - spr.sy - spr.hook.h * this.consts.f) / spr.svgHeight;
          spr.view[0].setAttribute("transform", "scale(1 " + scale + ")");
          spr.hook[0].setAttribute("transform", "translate(1 " + (spr.y - spr.oy) * this.consts.koefE / this.params.viewScale + ")");
          spr.y = box.top;
          spr.dY = (spr.y - spr.oy) / (this.consts.f * this.params.viewScale);
          if (spr.body) {
            spr.body.y = spr.h - spr.dY;
          }
          var delY = (spr.dY);
          spr.energy.KE = 0;
          spr.energy.PEelas = (0.5) * spr.k * delY * delY;
          spr.energy.PEgrav = o.model.mass * this.params.grav * (spr.h - spr.dY);
          spr.energy.Q = 0;
          this.Q = 0;
          if (o.model.sprNumber == this.params.energyShow) {
            this.updateEnergyChart();
            this.setTotEnergy(spr);
          }
        } else {
          if (spr) {
            this.playSound("boing");
          }
          spr.body = null, o.model.spring = null;
          spr.reset(this);
        }
      }
    }
  };

  massSpring.prototype.initSound = function() {
    if ($.browser.mozilla) {
      this.sound = new Audio("sound.ogg");
    } else {
      this.sound = new Audio("sound.mp3");
    }
    this.sound.load();
    this.sound.addEventListener("timeupdate", function() {
      if (this.currentTime > 0.6 && this.currentTime < 0.95) {
        this.pause();
      } else if (this.currentTime > 1.8) {
        this.sound.currentTime = 1;
        this.pause();
      }
    }, false);
  };

  massSpring.prototype.playSound = function(name) {
    if (this.params.sound) {
      var self = this;
      if (name == "boing") {
        this.sound.currentTime = 1;
      } else if (name == "drop") {
        this.sound.currentTime = 0.01;
      }
      this.sound.play();
    }
  };

  massSpring.prototype.updateEnergyChart = function(adjust) {
    if (this.params.energyShow !== 0) {
      var spre = this.layer.springs[this.params.energyShow - 1].energy;
      var erg = ["KE", "PEgrav", "PEelas", "Q"];
      var hs = 0;
      var sum = 0;
      var k = 1;
      if (adjust) {
        //sometimes precision is not enough if bouncing very fast(especially iPad), adjust display of tot Energy
        k = this.layer.springs[this.params.energyShow - 1].Etot / spre.et || 1;
      }
      //draw
      for (var i = erg.length; i--; ) {
        var h = spre[erg[i]] * this.consts.s * k;
        sum += spre[erg[i]];  
        if (h < 0)
          h = 0;
        this.layer.energyDisplay[erg[i]].setAttribute("d", "M 0 0 L 0 -" + h + " L 10 -" + h + " L 10 0 Z");
        this.layer.energyDisplay.Etot.eq(i)[0].setAttribute("d", "M 0 " + -hs + " L 0 -" + (h + hs) + " L 10 -" + (h + hs) + " L 10 " + -hs + " Z");
        hs += h;
      }
    }
  };

  massSpring.prototype.setTotEnergy = function(spr) {
    var val = spr.energy.KE + spr.energy.PEelas + spr.energy.PEgrav + spr.energy.Q;
    spr.Etot = val;
    this.layer.totEnergy.setAttribute("transform", "translate(1 -" + this.consts.koefE * val * this.consts.s + ")");
  };

  massSpring.prototype.evolveBody = function(o, dt) {
    var a = this.params.grav, dx, ap = this.params.grav, spr;
    if (o.spring) {
      spr = o.spring;
      if (dt > o.period / 15) {
        dt = o.period / 15;
      }
      a = a - (spr.k / o.mass) * spr.dY - this.params.fric * o.vel;
    }

    //change position model
    o.dy = (o.vel * dt + dt * dt * a * (0.5));
    o.y -= o.dy;
    if (o.spring) {
      spr.dY = (spr.h - o.y);
      var scale = (spr.l + spr.dY - spr.hook.h) / spr.l;
      spr.view[0].setAttribute("transform", "scale(1 " + scale + ")");
      spr.hook[0].setAttribute("transform", "translate(1 " + spr.dY * this.consts.koefE * this.consts.f + ")");
      spr.y = spr.oy + spr.dY * this.consts.f * this.params.viewScale;
      //post speed and acc
      var vp = o.vel + a * dt;
      ap = this.params.grav - (spr.k / o.mass) * spr.dY - this.params.fric * vp;

      o.spring.energy.KE = (0.5) * o.mass * vp * vp;
      o.spring.energy.PEelas = (0.5) * spr.k * spr.dY * spr.dY;
      o.spring.energy.PEgrav = o.mass * this.params.grav * o.y;
      o.spring.energy.Q += o.mass * this.params.fric * vp * o.dy;
      o.spring.energy.et = o.spring.energy.KE + o.spring.energy.PEelas + o.spring.energy.PEgrav + o.spring.energy.Q;
      if (o.sprNumber == this.params.energyShow) {
        this.updateEnergyChart(true);
      }
    }

    o.vel += 0.5 * (a + ap) * dt;

    //view
    o.cxy.y = o.dy * this.consts.koefE * this.consts.f + o.cxy.y;
    if (o.cxy.y > 0 && !o.spring) {
      //fell to ground
      o.cxy.y = 1, o.remove = true;
      o.vel = 0;
      this.playSound("drop");
    }
    o.view[0].setAttribute("transform", "translate(" + o.cxy.x + " " + o.cxy.y + ")");
  };

  massSpring.prototype.setTimerValue = function(d, dt) {
    this.cTime += dt;
    this.layer.timerValue[0].textContent = getTimeFromMs(this.cTime);
  };

  massSpring.prototype.nextStep = function(self) {
    if (self.params.timeSpeed !== 0) {
      var srd = this.layer.svg.suspendRedraw(250);
      var d = new Date().getTime();
      var dt = this.params.timeSpeed * (d - this.oTime);
      this.oTime = d;

      if (!self.params.timerPaused) {
        self.setTimerValue(d, dt);
      }

      if (self.objs.length > 0) {
        //remove unhandled
        var tobjs = [];
        for (var i = 0; i < self.objs.length; i++) {
          if (!self.objs[i].remove) {
            tobjs.push(self.objs[i]);
          }
        }
        self.objs = tobjs;
        //evolve
        dt = dt / 1000;
        for (i = 0; i < self.objs.length; i++) {
          self.evolveBody(self.objs[i], dt);
        }
      }
      this.layer.svg.unsuspendRedraw(srd);
    } else {
      this.oTime = new Date().getTime();
    }
  };

  massSpring.prototype.checkBound = function(o, notJumpToSpring) {
    var bbox = o.hook[0].getBoundingClientRect();
    //for firefox 3.6, it can't change prop left of bbox
    var box = {
      left : bbox.left - this.hoffset.left,
      top : bbox.top - this.hoffset.top,
      width : bbox.width,
      height : bbox.height
    };

    for (var i = this.layer.springs.length; i--; ) {
      var spr = this.layer.springs[i];
      if (spr.isIntersect(box)) {
        //spring and body intersection!
        o.sprNumber = i + 1;
        o.slipToSpring(spr, box, this, notJumpToSpring);
        return true;
      }
    }
    if (o.spring) {
      o.spring.body = null, o.spring = null;
    }
  };

  massSpring.prototype.setFric = function(sliderValue) {
    this.params.fric = (0.1 * Math.pow(1.5, sliderValue)) - 0.1;
  };

  massSpring.prototype.setStiff = function(sliderValue) {
    var spr = this.layer.springs[2];
    spr.k = 10 * 0.18593 * Math.pow(1.4, sliderValue);
    if (spr.body) {
      spr.body.period = 2 * Math.PI * Math.sqrt(spr.body.mass / spr.k);
    }

    spr.view.children().hide().eq(sliderValue + 1).show();
    spr.view.children().eq(0).show();
  };

  massSpring.prototype.body = function(mass, view, self) {
    //length and height over ground, meters
    this.view = view;
    this.hook = this.view.find(".hook");
    this.mass = mass;
    this.vel = 0;
    this.spring = null;
    //height over ground
    this.y = 0;
    this.eTot = 0;
  };

  massSpring.prototype.body.prototype.slipToSpring = function(spr, box, self, notJumpToSpring) {

    if (!spr.body || spr.body == this) {
      spr.body = this, this.spring = spr;
      this.period = 2 * Math.PI * Math.sqrt(this.mass / spr.k);
      var xy = self.getXYfromTransform(this.view[0].getAttribute("transform"));
      var newx = ((spr.x - box.left - box.width / 2) * self.consts.koefE + xy.x) / self.params.viewScale;
      var newy = ((spr.y - box.top) * self.consts.koefE + xy.y) / self.params.viewScale;
      this.cxy = {
        x : newx,
        y : newy
      };
      if (!notJumpToSpring) {
        this.view[0].setAttribute("transform", "translate(" + newx + " " + newy + ")");
        spr.body.y = spr.h - spr.dY;
      }

      spr.energy = {
        KE : 0,
        PEelas : 0.5 * spr.k * spr.dY * spr.dY,
        PEgrav : spr.body.mass * self.params.grav * spr.body.y,
        Q : 0
      };
      if (this.sprNumber == self.params.energyShow) {
        self.setTotEnergy(spr);
      }
      self.updateEnergyChart();
    }
  };

  massSpring.prototype.spring = function(k, view, container) {
    this.view = view;
    this.hook = view.parent().find(".springHook");
    //length, height over ground, meters
    this.l = 0.3;
    this.h = 0.6;

    this.k = k;
    this.body = null;
    this.period = 0;

    this.dY = 0;

    //energy
    this.energy = {
      KE : 0,
      PEelas : 0,
      PEgrav : 0,
      Q : 0,
      Etot : 0
    };
  };

  massSpring.prototype.spring.prototype.reset = function(self) {
    this.y = this.oy;
    this.dY = 0;
    this.view[0].setAttribute("transform", "scale(1 1)");
    this.hook[0].setAttribute("transform", "translate(1 1)");
    this.energy = {
      KE : 0,
      PEelas : 0,
      PEgrav : 0,
      Q : 0,
      Etot : 0
    };
    self.updateEnergyChart();
    self.setTotEnergy(this);
  };

  massSpring.prototype.spring.prototype.isIntersect = function(box) {
    return !(this.x + this.tl < box.left || this.x - this.tl > box.left + box.width || this.y + this.tl < box.top || this.y - this.tl > box.top + box.height);
  };
  // ---- utils ----

  massSpring.prototype.getXYfromTransform = function(str) {
    var arr = str.split("(")[1].split(")")[0].split(" ");
    //.replace(",","") for firefox 3.6
    return {
      x : arr[0].replace(/,$/gi, "") * this.params.viewScale,
      y : arr[1] * this.params.viewScale
    };
  };

  var getTimeFromMs = function(ms) {
    var mins = ~~(ms / 60000);
    ms -= mins * 60000;
    var secs = ~~(ms / 1000);
    ms -= secs * 1000;
    mss = ~~(ms / 10);
    if (mins < 10)
      mins = "0" + mins;
    if (secs < 10)
      secs = "0" + secs;
    if (mss < 10)
      mss = "0" + mss;
    return mins + ":" + secs + ":" + mss;
  };
  var getCoords = function(evt, offset) {
    evt = evt || window.event;
    var posx = 0, posy = 0;

    if (evt.pageX || evt.pageY) {
      posx = evt.pageX;
      posy = evt.pageY;
    } else if (evt.clientX || evt.clientY) {
      posx = evt.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
      posy = evt.clientY + document.body.scrollTop + document.documentElement.scrollTop;
    }
    if (evt.changedTouches) {
      posx = evt.changedTouches[0].pageX;
      posy = evt.changedTouches[0].pageY;
    }

    return {
      x : (parseInt(posx, 10) - offset.left),
      y : (parseInt(posy, 10) - offset.top)
    };
  };

  var scaleHTML = function(obj, scaleFactor) {
    var scale = "scale(" + scaleFactor + ", " + scaleFactor + ")";
    obj.style.webkitTransform = scale;
    obj.style.MozTransform = scale;
    obj.style.oTransform = scale;
    obj.style.msTransform = scale;
    obj.style.transform = scale;
  };
  window[module] = massSpring;

})(jQuery, "phet", "massSpring");

var BrowserDetect = {
  init : function() {
    this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
    this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "an unknown version";
    this.OS = this.searchString(this.dataOS) || "an unknown OS";
  },
  searchString : function(data) {
    for (var i = 0; i < data.length; i++) {
      var dataString = data[i].string;
      var dataProp = data[i].prop;
      this.versionSearchString = data[i].versionSearch || data[i].identity;
      if (dataString) {
        if (dataString.indexOf(data[i].subString) != -1)
          return data[i].identity;
      } else if (dataProp)
        return data[i].identity;
    }
  },
  searchVersion : function(dataString) {
    var index = dataString.indexOf(this.versionSearchString);
    if (index == -1)
      return;
    return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
  },
  dataBrowser : [{
    string : navigator.userAgent,
    subString : "Chrome",
    identity : "Chrome"
  }, {
    string : navigator.userAgent,
    subString : "OmniWeb",
    versionSearch : "OmniWeb/",
    identity : "OmniWeb"
  }, {
    string : navigator.vendor,
    subString : "Apple",
    identity : "Safari",
    versionSearch : "Version"
  }, {
    prop : window.opera,
    identity : "Opera",
    versionSearch : "Version"
  }, {
    string : navigator.vendor,
    subString : "iCab",
    identity : "iCab"
  }, {
    string : navigator.vendor,
    subString : "KDE",
    identity : "Konqueror"
  }, {
    string : navigator.userAgent,
    subString : "Firefox",
    identity : "Firefox"
  }, {
    string : navigator.vendor,
    subString : "Camino",
    identity : "Camino"
  }, {// for newer Netscapes (6+)
    string : navigator.userAgent,
    subString : "Netscape",
    identity : "Netscape"
  }, {
    string : navigator.userAgent,
    subString : "MSIE",
    identity : "Explorer",
    versionSearch : "MSIE"
  }, {
    string : navigator.userAgent,
    subString : "Gecko",
    identity : "Mozilla",
    versionSearch : "rv"
  }, {// for older Netscapes (4-)
    string : navigator.userAgent,
    subString : "Mozilla",
    identity : "Netscape",
    versionSearch : "Mozilla"
  }],
  dataOS : [{
    string : navigator.platform,
    subString : "Win",
    identity : "Windows"
  }, {
    string : navigator.platform,
    subString : "Mac",
    identity : "Mac"
  }, {
    string : navigator.userAgent,
    subString : "iPhone",
    identity : "iPhone/iPod"
  }, {
    string : navigator.platform,
    subString : "Linux",
    identity : "Linux"
  }]

};
BrowserDetect.init();
