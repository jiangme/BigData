package com.jiang.monitor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jiang
 * <p>
 *     流数据模拟器实现
 *     运行方式:
 *     1.先将当前项目打包为jar包。
 *     2.将项目放到spark集群根目录下。
 *     3.执行语句：
 *     java -cp spark-demo-1.0-SNAPSHOT.jar com.jiang.sparkstream.StreamDataMonitor /opt/testdata/user2.data 9999 1000
 * </p>
 * Create by 2018/5/22 16:23
 */
public class StreamDataMonitor {


    /**
     *
     * 获取随机整数
     * @param len
     * @return
     */
    public static final int index(int len){
        Random random = new Random();
        return random.nextInt(len);
    }


    public static void main(String[] args) throws Exception {

        // 调用该模拟器需要三个参数，分为为文件路径、端口号和间隔时间（单位：毫秒）
        if(args.length!=3){
            System.err.println("Usage: <filename> <port> <millisecond>");
            System.exit(1);
        }

        String fileName =args[0];
        //获取文件内容
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        List<String> lineList = new ArrayList<>();
        while (reader.readLine() != null){
            lineList.add(reader.readLine());
        }
        Object [] lines = lineList.toArray();
        int filerow = lines.length;

        //指定监听某个接口
        ServerSocket litener = new ServerSocket(Integer.parseInt(args[1]));
        while (true){
            Socket socket = litener.accept();
            new Thread(new Runnable() {
                @Override
                public void run()  {
                    System.out.println("socket connect from : " + socket.getInetAddress());
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
                        while (true){
                            Thread.sleep(Long.parseLong(args[2]));
                            //当接口接受请求的时候，随机获取某行数据发送给对方
                            Object line = lines[index(filerow)];
                            System.out.println("server send data : " + line);
                            out.write(line.toString());
                            out.flush();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

}
