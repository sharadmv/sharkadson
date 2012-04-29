var blimp = require('./blimp.js');
var Bridge = require('./bridge/bridge.js');
var bridge = new Bridge({apiKey:"abcdefgh"});
var curTimeout = null;
bridge.connect();
var sharkad = {
  forward:function(speed,duration){
    setTimeout(function(){
      blimp.leftOff();
      blimp.rightOn();
    },speed*1000/2);
    blimp.leftOn();
    blimp.rightOff();
    curTimeout = setInterval(function(){
      blimp.leftOn(); 
      blimp.rightOff();
      setTimeout(function(){
        blimp.leftOff();
        blimp.rightOn();
      },speed*1000/2);
    }, speed*1000);
    if (duration != -1){
      setTimeout(function(){blimp.rightOff();blimp.leftOff();clearInterval(curTimeout)},duration*1000);
    }
  },
  left:function(speed,duration){
         setTimeout(function(){
           blimp.leftOff();
         },speed*1000/2);
         blimp.leftOn();
         curTimeout = setInterval(function(){
           blimp.leftOn();
           setTimeout(function(){
             blimp.leftOff();
           },speed*1000/2);
         }, speed*1000);
         if (duration != -1){
           setTimeout(function(){blimp.rightOff();blimp.leftOff();clearInterval(curTimeout)},duration*1000);
         }
       },
  right:function(speed,duration){
         setTimeout(function(){
           blimp.rightOff();
         },speed*1000/2);
         blimp.rightOn();
         curTimeout = setInterval(function(){
           blimp.rightON();
           setTimeout(function(){
             blimp.rightOff();
           },speed*1000/2);
         }, speed*1000);
         if (duration != -1){
           setTimeout(function(){blimp.rightOff();blimp.leftOff();clearInterval(curTimeout)},duration*1000);
         }
        },
  stop:function(){
         clearInterval(curTimeout);
         blimp.forwardOff();
         blimp.backwardOff();
       },
  decel:function(){
          blimp.forwardOn();
          console.log("decel");
        },
  accel:function(){
          console.log("ACCEL");
          blimp.backwardOn();
        },
  leftOn:function(){
    blimp.leftOn();
  },leftOff:function(){
    blimp.leftOff();
  },rightOn:function(){
    blimp.rightOn();
  },rightOff:function(){
    blimp.rightOff();
  },noseDown:function(){
    blimp.forwardOn();
  },noseUp:function(){
    blimp.forwardDown();
  }
}
//sharkad.forward(2,-1);
bridge.publishService("sharkad",sharkad);
