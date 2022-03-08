### Module

    ClientCommand模块：控制台命令模块，程序是通过控制台来触发的，根据用户输入的命令来调用响应的Command实现类。
    
        菜单命令类（ClientCommandMenu）：主要用于列出支持的命令菜单，以及响应用户输入的命令类型
        登录命令（LoginConsoleCommand）: 该命令负责从Scanner控制台实例收集客户端登录的用户ID和密码


    ProtoBufBuilder模块：主要用来组装ProtoBuf消息。

        该模块存放的就是各种消息Bean，没有什么实际的逻辑。


    Sender模块：该模块的作用是用来发送数据包的。

        LoginSender:    发送登录消息


    Handler模块：处理器模块，主要是服务端响应处理器，都是入站处理器。

        HeartBeatClientHandler
        LoginResponceHandler:用来处理登录消息的返回逻辑，
        ExceptionHandler:  用来处理Handler中抛出的异常


    CommandController
        负责收集用户在控制台输入的命令，根据响应的命令类型调用响应的命令处理器合收集相关的信息。
        