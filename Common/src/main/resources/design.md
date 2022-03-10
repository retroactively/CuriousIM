在客户端进程中需要用到多线程，在common模块的cocurrent包中添加工具类。


从细分的⻆度来说,整个登录/响应的流程大概包含9个环节:
    (1)首先,客戶端收集用戶ID和密码,这一步需要使用到LoginConsoleCommand控制台命令类。
    (2)然后,客戶端发送Protobuf数据包到客戶端通道,这一步需要通过LoginSender发送器组装Protobuf数据包。
    (3)客戶端通道将Protobuf数据包发送到对端,这一步需要通过Netty底层来完成。
    (4)服务器子通道收到Protobuf数据包,这一步需要通过Netty底层来完成。
    (5)服务端UserLoginHandler入站处理器收到登录消息,交给业务处理器LoginMsgProcesser处理异步的业务逻辑。
    (6)服务端LoginMsgProcesser处理完异步的业务逻辑,就将处理结果写入用戶绑定的子通道。
    (7)服务器子通道将登录响应的Protobuf数据帧写入到对端,这一步需要通过Netty底层来完成。
    (8)客戶端通道收到Protobuf登录响应数据包,这一步需要通过Netty底层来完成。
    (9)客戶端LoginResponceHandler业务处理器处理登录响应,例如设置登录的状态,保存会话的Session ID等等。


TODO:
        
    功能：
        1）登陆鉴权
        2）单聊
        3）群聊
    系统：
        1) 数据库支持 (mysql)
        2）Redis缓存session
        3）Kafka数据消费
        4) Jmeter数据测试
        5) 项目结构的优化
