
import com.alibaba.fastjson.JSON;
import com.ouyeel.common.ReturnMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;




   //使用httpClient下载文件
   @Test
   public  void downLoadFile() throws Exception{

       //需要下载的所有文件名字所在的文件
       File downFile = new File("D:/downLoadAttachments/txt/test.txt");
       BufferedReader downBr = new BufferedReader(new InputStreamReader(new FileInputStream(downFile), "utf-8"));//构造一个BufferedReader类来读取文件
       List<String> list = new ArrayList<>();
       String s = "";
       while ((s = downBr.readLine()) != null) {//使用readLine方法，一次读一行
           list.add(s);
       }
       downBr.close();
       //打印结果的list
       List<Map<String,String>> resultList = new ArrayList<>();
       Map<String,String> map = new HashMap<>();//临时存文件信息的map(打印需要)
       //生成存储文件的文件夹
       Random random = new Random();
       int i = random.nextInt(100);
       logger.info("文件夹名称后缀 : {}",i);
       String dirName = "attachments"+i;
       File dir = new File("D://downLoadAttachments/downLoadFile/"+dirName);
       if (!dir.exists()){
           dir.mkdirs();
           logger.info("文件夹创建");
       }
       //循环下载文件
       for ( String item:list){
           //下载文件的地址
           String url = "http://rfq.ouyeelbuy.com/static-resource/rfq_upload/"+item;
           CloseableHttpClient client = HttpClients.createDefault();
           HttpGet get = new HttpGet(url);
           CloseableHttpResponse response = client.execute(get);
           InputStream is = response.getEntity().getContent();
           try {
              //下载到本地的文件地址
               String localfile = "D://downLoadAttachments/downLoadFile/"+dirName+"/"+item;
              // logger.info("下载的附件 : {}",localfile);
               //打印文件信息
               map.put(item,String.valueOf(response.getEntity().getContentLength()/1024));
               resultList.add(map);
               System.out.println(item+"----"+String.valueOf(response.getEntity().getContentLength()/1024)+"k");

               File file = new File(localfile);//创建文件
               if(!file.exists()){
                   file.createNewFile();
               }
               FileOutputStream  os = new FileOutputStream(file);
               int read = 0;
               byte[] temp = new byte[1024*1024];

               while((read=is.read(temp))>0){
                   os.write(temp,0,read);
               }
               os.flush();
                os.close();
           } catch (ClientProtocolException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }finally {
               is.close();
           }
       }
       //打印json串
       logger.info(JSON.toJSONString(resultList));
   }


//使用ftp下载文件
public static void ftp() throws  Exception{
   File file = new File("C:/Users/OuyeelBuy/Desktop/jar/123.txt");
   BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));//构造一个BufferedReader类来读取文件
   List<String> list = new ArrayList<>();
   String s = "";
   while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
       list.add(s);
   }
   br.close();
   String localfolder = "C:/Users/OuyeelBuy/Desktop/jar/rfqAttachments/";
   String userName = "osm";         //FTP 登录用户名
   String password = "osm1022";         //FTP 登录密码
   String ip = "10.60.1.6";                     //FTP 服务器地址IP地址
   int port = 21;
   FTPClient ftpClient = null; //FTP 端口
   if (ftpClient == null) {
       int reply;
       try {
           ftpClient = new FTPClient();
           ftpClient.setControlEncoding("GBK"); //文件名乱码,默认ISO8859-1，不支持中文
           ftpClient.connect(ip, port);
           ftpClient.login(userName, password);
           FTPClientConfig config = new FTPClientConfig();
           config.setUnparseableEntries(true);
           ftpClient.configure(config);
           reply = ftpClient.getReplyCode();
           ftpClient.setDataTimeout(120000);
           ftpClient.enterLocalPassiveMode();
           if (!FTPReply.isPositiveCompletion(reply)) {
               ftpClient.disconnect();
           }
       } catch (SocketException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
   }
   FTPFile[] fs;
   String remotePath = "/rfq/attachments/";
   try {
       // 检验是否连接成功
       int reply = ftpClient.getReplyCode();
       System.out.println("reply:" + reply);// 成功是230
       if (!FTPReply.isPositiveCompletion(reply)) {
           System.out.println("连接被拒绝!断开连接!" + reply);
           ftpClient.disconnect();
       } else {
           System.out.println("连接成功");
           ftpClient.setKeepAlive(true);
           System.out.println("remotePath:" + remotePath);
           ftpClient.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
           fs = ftpClient.listFiles();
           //判断本地是否有文件夹，没有则新建
           File localfile = new File(localfolder);
           if (!localfile.exists()) {
               System.out.println("新建文件夹！");
               localfile.mkdirs();
           }
           System.out.println("------没有下载成功的文件------");
           for (String item : list) {
               FileOutputStream is = new FileOutputStream(new File(localfolder + item));
               if(!ftpClient.retrieveFile(item, is)){
                   System.out.println(item);
               }
               is.close();
           }
           //            if (fs.length > 0) {
           //                for (int i = 0; i < fs.length; i++) {
           //                    FTPFile ff = fs[i];
           //                    if (!ff.isDirectory()) {
           //                        if (list.contains(ff.getName())) {
           //                            FileOutputStream is = new FileOutputStream(new File(localfolder + ff.getName()));
           //                            ftpClient.retrieveFile(ff.getName(), is);
           //                            is.close();
           //                        }
           //                    }
           //                }
           //            } else {
           //                System.out.println("无可下载文件");
           //            }
       }
       if (ftpClient != null) {
           ftpClient.logout();
           ftpClient.disconnect();
       }
   } catch (IOException e) {
       e.printStackTrace();
   }
}
