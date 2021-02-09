package org.rpc.server;

import org.rpc.common.MethodParameter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author jiankang Li
 * @ClassName IOService.java
 * @Description 服务端 - 网络数据处理
 * @createTime 2021年02月08日 11:04:00
 */
public class IOService implements Runnable{
    private int port;
    private ServerSocket serverSocket;
    private RpcExploreService rpcExploreService;
    private volatile boolean flag;

    public IOService(RpcExploreService rpcExploreService, int port) throws IOException {
        this.rpcExploreService = rpcExploreService;
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        this.flag = true;
        System.out.println("初始化IOService，服务端启动了");

        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                flag = false;
                System.out.println("服务端关闭了");
            }
        });
    }

    @Override
    public void run() {
        while (flag) {
            Socket socket = null;
            try {
                System.out.println("等待接收数据。");
                socket = serverSocket.accept();
            } catch (IOException e) {
            }
            if (socket == null) {
                continue;
            }
            new Thread(new ServerSocketRunnable(socket)).start();
        }
    }

    class ServerSocketRunnable implements Runnable {
        private Socket socket;
        public ServerSocketRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                System.out.println("处理接收的数据。");
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                MethodParameter methodParameter = MethodParameter.convert(inputStream);
                Object result = rpcExploreService.invoke(methodParameter);
                ObjectOutputStream output = new ObjectOutputStream(outputStream);
                output.writeObject(result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
