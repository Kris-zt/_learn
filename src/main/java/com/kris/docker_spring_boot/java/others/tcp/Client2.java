package com.kris.docker_spring_boot.java.others.tcp;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client2 {

    public static void main(String[] args) throws UnknownHostException, IOException {

        // 建立连接
        Socket s = new Socket("10.1.115.47", 9856);

        // 读取键盘输入流
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        // 端口输出
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

        String line = null;

        // 输入886结束输出
        while ((line = br.readLine()) != null) {
            if ("886".equals(line)) {
                break;
            }
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        s.close();
    }
}
