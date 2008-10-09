$.fn.droppy = function() {
  
  this.each(function() {
    
    var root = this, zIndex = 1000;
    
    function getSubnav(ele) {
      if (ele.nodeName.toLowerCase() == 'li') {
        var subnav = $('> ul', ele);
        return subnav.length ? subnav[0] : null;
      } else {
        return ele;
      }
    }
    
    function getActuator(ele) {
      if (ele.nodeName.toLowerCase() == 'ul') {
        return $(ele).parents('li')[0];
      } else {
        return ele;
      }
    }
    
    function hide() {
      var subnav = getSubnav(this);
      if (!subnav) return;
      $.data(subnav, 'cancelHide', false);
      
        if (!$.data(subnav, 'cancelHide')) {
          $(subnav).hide();
        }
    }
  
    function show() {
      var subnav = getSubnav(this);
      if (!subnav) return;
      $.data(subnav, 'cancelHide', true);
      $(subnav).css({zIndex: zIndex++}).show();
      if (this.nodeName.toLowerCase() == 'ul') {
        $(getActuator(this)).addClass('hover');
      }
    }
    
    $('ul, li', this).hover(show, hide);
    $('li', this).hover(
      function() { $(this).addClass('hover');$(this).css('z-index','10000')},
      function() { $(this).removeClass('hover');$(this).css('z-index','1000')}
    );
    
  });
  
};

