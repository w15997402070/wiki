package com.ouyeel.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 开启线程
 */
public class TailLogThread extends Thread{
    /**
     * 日志打印类
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TailLogThread.class);
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    private BufferedReader reader;
    /**
     * 日志文件
     */
    private File logFile;
    /**
     * 上次文件大小
     */
    private long lastTimeFileSize ;

    /**
     *格式化日期
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

    public TailLogThread( Session session) {
        this.session = session;

    }
    public TailLogThread(InputStream in, Session session) {
        this.reader = new BufferedReader(new InputStreamReader(in));
        this.session = session;
    }
    @Override
    public void run() {
        //日志文件
        logFile = new File("D:\\apache-tomcat-8.0.33\\temp\\spring.log");
        //最后一次日志文件的大小
        lastTimeFileSize = logFile.length();
        //循环读取文件
        while (true) {
            try {
                //日志文件的大小
                long len = logFile.length();
                //比最后一次的文件小,
                if (len < lastTimeFileSize) {
                    LOGGER.info("Log file was reset. Restarting logging from start of file." + dateFormat.format(new Date()));
                    lastTimeFileSize = len;
                } else if(len > lastTimeFileSize) {
                    RandomAccessFile randomFile = new RandomAccessFile(logFile, "r");
                    randomFile.seek(lastTimeFileSize);//跳过上次的文件
                    String tmp = null;
                    while ((tmp = randomFile.readLine()) != null) {
                        // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
                        session.getBasicRemote().sendText(tmp + "<br>");
                    }
                    lastTimeFileSize = randomFile.length();
                    randomFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * tail命令
     */
//    @Override
//    public void run() {
//        String line;
//        try {
//            while((line = reader.readLine()) != null) {
//                // 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
//                session.getBasicRemote().sendText(line + "<br>");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
