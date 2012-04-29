var sp = require('serialport');
var SerialPort = sp.SerialPort;
port = "/dev/ttyACM0";
var serialPort = new SerialPort(port,{
  parser:sp.parsers.readline('\n')
});
serialPort.on('data',function(data){
  //console.log(data);
});
var blimp = {
  leftOn:function(){
           serialPort.write("a");
         },
  leftOff:function(){
            serialPort.write("b");
          },
  rightOn:function(){
            serialPort.write("c");
          },
  rightOff:function(){
             serialPort.write("d");
           },
  forwardOn:function(){
              serialPort.write("e");
            },
  forwardOff:function(){
               serialPort.write("f");
             },
  backwardOn:function(){
               serialPort.write("g");
             },
  backwardOff:function(){
                serialPort.write("h");
              }
}
module.exports = blimp;
