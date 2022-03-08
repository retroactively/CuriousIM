
### Process

客户端发送登录数据包。

服务端进行用户信息验证。

服务器端创建Session会话。

服务器端返回登录结果的信息给客户端，包括成功标志、Session ID等。


### Module

builder模块：构建各类消息体

Handler模块：客户端请求的处理

Processor模块：以异步的方式完成请求的业务逻辑处理

Session模块：管理用户与通道的绑定关系
