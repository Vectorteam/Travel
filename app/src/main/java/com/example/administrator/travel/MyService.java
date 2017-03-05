package com.example.administrator.travel;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class MyService extends Service
{
    private XStream xStream = new XStream(new DomDriver());
    private DataOutputStream dos = null;
    private Socket socket = null;
    private Socket socket1 = null;


    public MyService()
    {

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate()
    {
        System.out.println("启动Service");
        new Initializer().start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int msg = intent.getIntExtra("MSG", 0);
        if (msg == 1)
        {


            Object obj = intent.getSerializableExtra("obj");
//        LoginRequest loginRequest = new LoginRequest();
//        Object obj = (Object)loginRequest;
            String s = xStream.toXML(obj);
            System.out.println("转换完毕");
            try
            {
                dos = new DataOutputStream(socket.getOutputStream());
                System.out.println("发送请求");
                dos.writeUTF(s);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            return super.onStartCommand(intent, flags, startId);
        }
        else if (msg == 2)
        {

            try
            {
                final String url = intent.getStringExtra("url");
                System.out.println(url+"将被上传");
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Client client = null;
                        try
                        {
                            client = new Client();
                            client.sendFile(url);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return super.onStartCommand(intent, flags, startId);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class Initializer extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                socket = new Socket("192.168.43.44", 10000);
//                socket1 = new Socket("10.0.2.2", 10001);
                System.out.println(socket);
                ReadHandler rh = new ReadHandler();
                Thread t = new Thread(rh);
                t.start();
                System.out.println("t.start()");


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    class ReadHandler implements Runnable
    {

        @Override
        public void run()
        {
            System.out.println("run");
            DataInputStream dis = null;
            try
            {
//                System.out.println("socket created");
                while (true)
                {
//                    XStream xStream = new XStream(new DomDriver());
                    dis = new DataInputStream(socket.getInputStream());
                    String s = dis.readUTF();
                    Object object = xStream.fromXML(s);
                    if (object instanceof TextMessage)
                    {
                        TextMessage textMessage = (TextMessage) object;
                        System.out.println(textMessage.text);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("key", textMessage);
                        Intent intent = new Intent();
                        intent.setAction("default");
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                    }
                    if (object instanceof PicReply)
                    {
                        PicReply picReply = (PicReply)object;
                        System.out.println("列表："+picReply.getFilelist());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("key", picReply);
                        Intent intent = new Intent();
                        intent.setAction("default");
                        intent.putExtras(bundle);
                        sendBroadcast(intent);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}