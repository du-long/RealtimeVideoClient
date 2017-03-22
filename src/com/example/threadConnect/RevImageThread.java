package com.example.threadConnect;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

public class RevImageThread implements Runnable {

	public Socket s;
	public ServerSocket ss;
	
	//向UI线程发送消息
	private Handler handler;
	
	private Bitmap bitmap;
	private static final int COMPLETED = 0x222;
	
	public RevImageThread(Handler handler){
		this.handler = handler;
	}
	
	public void run()
	{	
		byte [] buffer = new byte[1024];
		int len = 0;

	
		try {
			ss = new ServerSocket(40000);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		InputStream ins = null;
		while(true){
			
			try {
				s = ss.accept();
				ins = s.getInputStream();
				
				ByteArrayOutputStream outStream = new ByteArrayOutputStream(); 
				while( (len=ins.read(buffer)) != -1){      
				    outStream.write(buffer, 0, len); 
				}
				ins.close();
				
				byte data[] = outStream.toByteArray(); 
				bitmap = BitmapFactory.decodeByteArray(data, 0, data.length); 
				
				Message msg =handler.obtainMessage();
			    msg.what = COMPLETED;
			    msg.obj = bitmap;
			    handler.sendMessage(msg);
			    
			    outStream.flush();
				outStream.close();
				if(!s.isClosed()){
					s.close();
				}
			    
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Bitmap bitmap = BitmapFactory.decodeStream(ins);
			
		}
	}

}
