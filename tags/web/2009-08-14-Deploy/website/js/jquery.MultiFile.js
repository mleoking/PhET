/*
 ### jQuery Multiple File Upload Plugin ###
 By Diego A., http://www.fyneworks.com, diego@fyneworks.com
 
 Website:
  http://www.fyneworks.com/jquery/multiple-file-upload/
 Project Page:
  http://jquery.com/plugins/project/MultiFile/
 Forums:
  http://www.nabble.com/jQuery-Multiple-File-Upload-f20931.html
 Blog:
  http://fyneworks.blogspot.com/2007/04/multiple-file-upload-plugin-for-jquery.html
 
 12-April-2007: v1.1
                Added events and file extension validation
                See website for details.
*/

/*# AVOID COLLISIONS #*/
(function($){
/*# AVOID COLLISIONS #*/

/*
 ### Default implementation ###
 The plugin will attach itself to file inputs
 with the class 'multi' when the page loads
*/
$(function(){
 $.MultiFile();
});


// extend jQuery - $.MultiFile hook
$.extend($, {
 MultiFile: function( o /* Object */ ){
  return $("INPUT[@type='file'].multi").MultiFile(o);
 }
});

// extend jQuery function library
$.extend($.fn, {
  
  // MultiFile function
  MultiFile: function( o /* Object */ ){
   if(this._MultiFile){ return $(this); }
   this._MultiFile = true;
   
   // Bind to each element in current jQuery object
   return $(this).each(function(i){
      // Remember our ancestors...
      var d = this;
      
      //#########################################
      // Find basic configuration in class string
      // debug???
      d.debug = (d.className.indexOf('debug')>0);
      // limit number of files that can be selected?
      d.max = (d.className.match(/\b((max|limit)\-[0-9]+)\b/gi));
      if(d.max){
       d.max = new Number((d.max+'').match(/[0-9]+/)[0]);
      }else{
       d.max = -1;
      }
      // limit extensions?
      d.accept = (d.className.match(/\b(accept\-[\w\|]+)\b/gi)) || '';
      d.accept = new String((d.accept+'')).replace(/^(accept|ext)\-/i,'');
      //#########################################
      
      
      // Attach a bunch of events, jQuery style ;-)
      $.each("on,after".split(","), function(i,o){
       $.each("FileSelect,FileRemove,FileAppend".split(","), function(j,event){
        d[o+event] = function(e, v, m){ // default functions do absolutelly nothing...
         // if(d.debug) alert(''+o+event+'' +'\nElement:' +e.name+ '\nValue: ' +v+ '\nMaster: ' +m.name+ '');
        };
       });
      });
      // Setup a global event handler
      d.trigger = function(event, e){
        var f = d[event];
        if(f){
         var v = $(this).attr('value');
         var r = f(e, v, d);
         if(r!=null) return r;
        }
        return true;
      };
      
      
      // Initialize options
      if( typeof(o)=='number' ){ o = {max:o}; };
      $.extend(d, d.data || {}, o);
      
      // Default properties - INTERNAL USE ONLY
      $.extend(d, {
       STRING: d.STRING || {}, // used to hold string constants
       n: 0, // How many elements are currently selected?
       k: 'MF_', // Instance Key?
       f: function(z){ return d.k+'_F_'+String(i)+'_'+String(z); }
      });
      
      // Visible text strings...
      // $file = file name (with path), $ext = file extension
      d.STRING = $.extend({
       remove:'remove',
       denied:'You cannot select a $ext file.\nTry again...',
       selected:'File selected: $file'
      }, d.STRING);
      
      
      // Setup dynamic regular expression for extension validation
      // - thanks to John-Paul Bader: http://smyck.de/2006/08/11/javascript-dynamic-regular-expresions/
      if(String(d.accept).length>1){
       d.rxAccept = new RegExp('\\.('+(d.accept?d.accept:'')+')$','gi');
      };
      
      // Create wrapper to hold our file list
      d.w = d.k+'_MF_'+i; // Wrapper ID?
      var x = $(d);
      x.wrap('<div id="'+d.w+'"></div>');
      
      // Bind a new element
      d.add = function( e, ii ){
       
       // Keep track of how many elements have been displayed
       d.n++;
       
       // Add reference to master element
       e.d = d;
       
       // Define element's ID and name (upload components need this!)
       e.i = ii;//d.I;
       e.id = d.f(e.i);
       e.name = e.id;
       
       // If we've reached maximum number, disable input e
       if( d.max != -1 && d.n > (d.max+1) ){ // d.n Starts at 1, so add 1 to d.max.
        e.disabled = true;
       };
       
       // Remember most recent e
       d.current = e;
       
       /// now let's use jQuery
       e = $(e);
       
       // Triggered when a file is selected
       e.change(function(){
         
         //# Trigger Event! onFileSelect
         if(!d.trigger('onFileSelect', this, d)) return false;
         //# End Event!
         
         // check extension
         if(d.accept){
          var v = String(e.attr('value'));
          if(!v.match(d.rxAccept)){
           e.attr('value', '');
           e.get(0).value = '';
           alert(d.STRING.denied.replace('$ext', String(v.match(/\.\w{1,4}$/gi))));
           return false;
          }
         };
         
         // Hide this element: display:none is evil!
         //this.style.display = 'block';
         this.style.position = 'absolute';
         this.style.left = '-1000px';
         
         // Create a new file input element
         var f = $('<input type="file"/>');
         
         // Add it to the form
         $(this).parent().prepend(f);
         
         // Update list
         d.list( this );
         
         // Bind functionality
         d.add( f.get(0), this.i+1 );
         
         //# Trigger Event! afterFileSelect
         if(!d.trigger('afterFileSelect', this, d)) return false;
         //# End Event!
         
       });
      
      };
      // Bind a new element
     
      // Add a new file to the list
      d.list = function( y ){
       
       //# Trigger Event! onFileAppend
       if(!d.trigger('onFileAppend', y, d)) return false;
       //# End Event!
       
       // Insert HTML
       var
        t = $('#'+d.w),
        r = $('<div></div>'),
        v = $(y).attr('value')+'',
        a = $('<span class="file" title="'+d.STRING.selected.replace('$file', v)+'">'+v.match(/[^\/\\]+$/gi)[0]+'</span>'),
        b = $('<a href="#'+d.w+'">'+d.STRING.remove+'</a>');
       t.append(r);
       r.append('[',b,']&nbsp;',a);//.prepend(y.i+': ');
       b.click(function(){
        
         //# Trigger Event! onFileRemove
         if(!d.trigger('onFileRemove', y, d)) return false;
         //# End Event!
         
         d.n--;
         d.current.disabled = false;
         $('#'+d.f(y.i)).remove();
         $(this).parent().remove();
         
         //# Trigger Event! afterFileRemove
         if(!d.trigger('afterFileRemove', y, d)) return false;
         //# End Event!
         
         return false;
       });
       
       //# Trigger Event! afterFileAppend
       if(!d.trigger('afterFileAppend', y, d)) return false;
       //# End Event!
       
      };
      
      // Bind first file element
      if(!d.ft){ d.add(d, 0); d.ft = true; }
      d.I++;
      d.n++;
      
   });
   // each element
  
  }
  // MultiFile function

});
// extend jQuery function library

/*# AVOID COLLISIONS #*/
})(jQuery);
/*# AVOID COLLISIONS #*/
