var blimp = require('./blimp.js');
var Bridge = require('./bridge/bridge.js');
var bridge = new Bridge({apiKey:"abcdefgh"});
var curTimeout = null;
bridge.connect();
console.log(blimp);
var sharkad = {
  left:function(speed, duration){
         console.log("LEFT");
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
         console.log("RIGHT");
          setTimeout(function(){
            blimp.rightOff();
          },speed*1000/2);
          blimp.rightOn();
          curTimeout = setInterval(function(){
            blimp.rightOn();
            setTimeout(function(){
              blimp.rightOff();
            },speed*1000/2);
          }, speed*1000);
          if (duration != -1){
            setTimeout(function(){blimp.rightOff();blimp.leftOff();clearInterval(curTimeout)},duration*1000);
          }
        },
  stop:function(){
         console.log("STOP");
         clearInterval(curTimeout);
         blimp.leftOff();
         blimp.rightOff();
         blimp.forwardOff();
         blimp.backwardOff();
       },
  forward:function(speed, duration){
            console.log("FORWARD");
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
  noseDown:function(){
            console.log("DOWN");
             blimp.forwardOn();
           },noseUp:function(){
            console.log("UP");
             blimp.backwardOn();
           }
}
bridge.publishService("sharkad",sharkad);
