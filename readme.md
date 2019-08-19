## 一，信令服务器
- 创建证书
    - openssl genrsa -out server.key 2048
    - openssl req -new -sha256 -key server.key -out csr.pem
    - openssl x509 -req -in csr.pem -signkey server.key -out cert.pem
    
2. server.js


## 二，WEB客户端
1. public下


## 三，Android客户端

