import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainClient {
	volatile static boolean flag = true;
	volatile static int times_link = 0;
	volatile static int times_1s = 0;
	volatile static String userId = null;
	
	public static void main(String[] args) throws IOException {
		// 客户端请求与本机在20006端口建立TCP连接

//		Socket client = new Socket("haitao.uicp.io", 20005);
		final Socket client = new Socket("127.0.0.1", 20005);
		// client.setSoTimeout(10000);
		// 获取键盘输入
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		// 获取Socket的输出流，用来发送数据到服务端
		PrintStream out = new PrintStream(client.getOutputStream());
		// 获取Socket的输入流，用来接收从服务端发送过来的数据
		BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		OutputStream clientStreamOut = client.getOutputStream();
		
		
		//在子线程轮询读数据
		new Thread() {
			
			public void run() {
				while (flag) {
					try {
						// 从服务器端接收数据有个时间限制（系统自设，也可以自己设置），超过了这个时间，便会抛出该异常
						// String echo = buf.readLine();
						// System.out.println(echo);
						readString(client);
						sleep(100);
						
						if (userId != null)times_1s++;
						
//						if (times_1s == 10) {
//							times_1s = 0;
//							times_link++;
//							if (times_link > 3) {
//								System.out.println("out");
//								flag = false;
//							}
//						}
					} catch (SocketTimeoutException e) {
						System.out.println("Time out, No response");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			private String readString(Socket client) throws IOException {
				InputStream inputStream = client.getInputStream();
				int len = inputStream.available();
				if (len == 1) {
					int res = inputStream.read();
					if (res == 0x39) {
						times_link = 0;
						return null;
					}
				}
				
				if (len != 0) {
					byte[] buffer = new byte[len];
					inputStream.read(buffer);
					printHex(buffer);
					System.out.println();
					System.out.println("string:"+new String(buffer));
					return new String(buffer);
				}
				return null;
			};
			
		}.start();

		
		//设置id和接收者
		System.out.println("输入用户名：");
		userId = input.readLine();
		out.print("userId-"+userId);
		
		
		
		//发送数据
		while (flag) {
			// System.out.print("input：");
			String str = input.readLine();
			
			if (str.equals("o1")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x01,0x51};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("c1")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x01,0x52};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("o2")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x02,0x51};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("c2")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x02,0x52};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("o3")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x03,0x51};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("c3")) {
				byte[] cmd = {0x55,0x51,0x2d,0x00,0x03,0x52};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("h1")) {
				byte[] cmd = {0x73,0x2d,0x5a,0x00,0x01,0x53};
				//clientStreamOut.write("s-".getBytes());
				clientStreamOut.write(cmd);
				
			}
			
			if (str.equals("q")) {
				byte[] cmd = {0x55,0x51,0x2d,0x54};
				//clientStreamOut.write("UQ-".getBytes());
				clientStreamOut.write(cmd);
			}
			//发送数据到服务端
			//out.print(str);
			
			//结束
			if ("bye".equals(str)) {
				flag = false;
			}
		}
		input.close();
		
		
		if (client != null) {
			// 如果构造函数建立起了连接，则关闭套接字，如果没有建立起连接，自然不用关闭
			client.close(); //只关闭socket，其关联的输入输出流也会被关闭
		}
		System.out.println("close");
	}
	
	
	static void printHex(int[] hexs) {
		for (int b : hexs) {
			System.out.printf("%02x", b);
		}
	}

	static void printHex(byte[] hexs) {
		for (byte b : hexs) {
			System.out.printf("%02x ", b);
		}
	}
	
	
}
