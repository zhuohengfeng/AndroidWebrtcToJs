'use strict'

var http = require('http');
var https = require('https');
var fs = require('fs');
var socketIo = require('socket.io');

var express = require('express');
var serveIndex = require('serve-index');

var USERCOUNT = 3;


var app = express();
app.use(serveIndex('./public'));
app.use(express.static('./public'));

//http server
var http_server = http.createServer(app);
http_server.listen(80, '0.0.0.0');

var options = {
	key : fs.readFileSync('./cert/server.key'),
	cert: fs.readFileSync('./cert/cert.pem')
}

//https server
var https_server = https.createServer(options, app);
var io = socketIo.listen(https_server);

// 创建信令服务器
io.sockets.on('connection', (socket)=> {

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

// 监听本地https 443
console.log('start the signal service @ ' + 443);
https_server.listen(443, '0.0.0.0');

