### 根据AP3000的第二版文档协议写的netty服务器后端
### 看懂代码的关键点
1. netty的编解码器
2. 理解从TCP流式数据解析出业务数据包
3. 字节的大小端区别

### 监听端口  
TCP 8888 提供给AP3000类型的设备连接，可在AP3000TCPServer的中修改端口号
TCP 9122 http端口号，context-path: /iotApi

### 包结构简介
common 对接已经的安居物联用，用于调用consumer端的接口
controller  http接口暴露出来的控制设备的接口
entity 各种数据结构的基本封装java bean
idl 通过thrift实现的RPC调用
net AP3000设备协议的解析，网络字节流的codec
service 业务层，当前没有太大作用

### 依赖库见pom文件
AP3000IDL 是一个私有的jar包主要定义的RPC调用的接口，需要在此项目的pom文件同目录下先执行
收到设备上报的数据
mvn clean install -DskipTests=true

### demo工程





