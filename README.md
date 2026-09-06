### 根据友电硬件对接文档协议写的netty服务器后端，完成对硬件通信

### 文档参考
#### 本地
- [AP3000第二版-设备与服务器通信协议.pdf](doc/AP3000%E7%AC%AC%E4%BA%8C%E7%89%88-%E8%AE%BE%E5%A4%87%E4%B8%8E%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%80%9A%E4%BF%A1%E5%8D%8F%E8%AE%AE.pdf)
- [第3版——主机-服务器通信协议.pdf](doc/%E7%AC%AC3%E7%89%88%E2%80%94%E2%80%94%E4%B8%BB%E6%9C%BA-%E6%9C%8D%E5%8A%A1%E5%99%A8%E9%80%9A%E4%BF%A1%E5%8D%8F%E8%AE%AE.pdf)
- [对接硬件问答.docx](doc/%E5%AF%B9%E6%8E%A5%E7%A1%AC%E4%BB%B6%E9%97%AE%E7%AD%94.docx)
#### 在线文档（可能会失效）
- [AP3000第二版-设备与服务器通信协议](https://docs.qq.com/doc/DRVdoeFFRaWFQUnRp)
- [电动自行车：第3版——主机-服务器通信协议指南](https://docs.qq.com/doc/DRUloVUJ0a2paUFJF)
- [对接硬件问答](http://xiaoyaoji.cn/project/1ayCUxqdawz/1ayCUzvDssC?st=1jJuMeEX0m8&sid=1jJuMeEX0m8)
### 看懂代码的关键点
1. netty的编解码器
2. 理解从TCP流式数据解析出业务数据包
3. 字节的大小端区别

### 监听端口  
TCP 8888 提供给AP3000类型的设备连接，可在AP3000TCPServer的中修改端口号
TCP 9122 http端口号，context-path: /iotApi

### 包结构简介
- common 对接已经的安居物联用，用于调用consumer端的接口
- controller  http接口暴露出来的控制设备的接口
- dto 各种数据结构的基本封装java bean
- net AP3000设备协议的解析，网络字节流的codec
- service 业务层，暴露硬件控制指令方法

### 依赖库见pom文件
- 执行单元测试 
``` mvn clean test```
- 生成单元测试报告
``` mvn surefire-report:report```






