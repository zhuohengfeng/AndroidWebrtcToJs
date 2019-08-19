// Setup basic express server
var express = require('express');
var app = express();
var path = require('path');
var server = require('http').createServer(app);
var io = require('socket.io')(server);
var port = process.env.PORT || 3000;

server.listen(port, () => {
  console.log('Server listening at port %d', port);
});

// Routing
app.use(express.static(path.join(__dirname, 'public')));

// Chatroom

var numUsers = 0;

io.on('connection', (socket) => {
  console.log('===> has a client connection!!!');

      // 收到消息，直接转换给room中的其他用户
  	socket.on('message', (room, data)=>{
  		socket.to(room).emit('message',room, data);
  	});

      // 收到加入的消息
  	socket.on('join', (room)=>{
  		socket.join(room);
  		var myRoom = io.sockets.adapter.rooms[room];
  		var users = (myRoom)? Object.keys(myRoom.sockets).length : 0;
  		console.log('the user number of room is: ' + users);

  		if(users < USERCOUNT){
  			socket.emit('joined', room, socket.id); //发送给登录的用户
  			if(users > 1){
  				socket.to(room).emit('otherjoin', room, socket.id); // 发送给其他人
  			}
  		}else{
  			socket.leave(room); // 已经满了，离开房间
  			socket.emit('full', room, socket.id); // 发送给登录的人
  		}
  		//socket.emit('joined', room, socket.id); //发给自己
  		//socket.broadcast.emit('joined', room, socket.id); //发给除自己之外的这个节点上的所有人
  		//io.in(room).emit('joined', room, socket.id); //发给房间内的所有人
  	});

  	socket.on('leave', (room)=>{
  		var myRoom = io.sockets.adapter.rooms[room];
  		var users = (myRoom)? Object.keys(myRoom.sockets).length : 0;
  		console.log('the user number of room is: ' + (users-1));
  		//socket.emit('leaved', room, socket.id);
  		//socket.broadcast.emit('leaved', room, socket.id);
  		socket.to(room).emit('bye', room, socket.id); // 发送给其他用户，有人离开了
  		socket.emit('leaved', room, socket.id); // 发送给登录用户，已经离开完成
  		//io.in(room).emit('leaved', room, socket.id);
  	});

});
