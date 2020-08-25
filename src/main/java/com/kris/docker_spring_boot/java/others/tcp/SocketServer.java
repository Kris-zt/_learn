package com.kris.docker_spring_boot.java.others.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

    public static void main(String[] args) throws IOException {
        System.out.println("---------------------服务开启-----------------------");
        // 建立端口，监听新的请求
        ServerSocket ss = new ServerSocket(9856);
        Socket s = ss.accept();

        // 读取客户端输入的信息
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        String line = null;
        // 进行输出
        while ((line = br.readLine()) != null) {
            System.out.println("-----客户端输入的信息是-----");
            System.out.println(line);
        }
        s.close();
    }
}
