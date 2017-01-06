
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.NetworkChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//git
public class MainServer {

	static final int PORT = 20005;

	static List<Socket> sockets = new ArrayList<>();
	static Map<String, Socket> map = new HashMap<>();
	
	public static void main(String[] args) throws Exception {
		// 服务端在20006端口监听客户端请求的TCP连接
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("服务器启动，等待客户端连接……\r\n");
		Socket client = null;
		boolean f = true;
		while (f) {
			// 等待客户端的连接，如果没有获取连接
			client = server.accept();
			//System.out.println("与客户端连接成功！");
			// 为每个客户端连接开启一个线程
			// new Thread(new ServerThread(client)).start();
			handleClient(client);

		}
		server.close();
	}

	static void handleClient(final Socket client) {
		new Thread() {
			String clientInfo = null;
			String userId = null;
			String toId = null;
			volatile boolean runFlag = true;
			volatile boolean linkFlag = false;
			volatile int times_1s = 0;
			volatile int times_link = 0;
			
			@Override
			public void run() {
				try {
					clientInfo = client.getInetAddress() 
							+ ":" + client.getPort();
					System.out.println("客户端：" + clientInfo);
					
					sockets.add(client);
					// 获取Socket的输出流，用来向客户端发送数据
					PrintStream out = new PrintStream(client.getOutputStream());
					// 获取Socket的输入流，用来接收从客户端发送过来的数据
					BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
					
					OutputStream dout = client.getOutputStream();
					
					while (runFlag) {
						// 接收从客户端发送过来的数据
						//根据发送者、接受者分发消息
						try {
							String data = readString(client);
							if (data != null) {
//								System.out.println("rec:"+data);
								String[] res = data.split("-");
								if (res[0].equals("userId")) {//注册userID
									userId = res[1];
									map.put(userId, client);
									System.out.println(map);
									
									//返回连接成功标示
									dout.write("welcome".getBytes());
									linkFlag = true;
								} else if (res[0].equals("toId")) {
									toId = res[1];
									//很奇怪 硬件在自动发送的时候不能发送第一个 's' 字符
								} else if (res[0].equals("") || res[0].equals("s") || res[0].equals("ss")) {//接收到硬件发过来的数据
									int splitId = data.indexOf("-");
									
									//sentDataToAllPhone(userId, data.substring(splitId+1));
									sentDataToAllPhone(userId, res[1]);
									
								} else {//手机客户端发送过来的数据
									int splitId = data.indexOf("-");
									sentData(userId, res[0], 
											data.substring(splitId+1));
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
						
						sleep(100);
						
						//只是针对手机客户端，心跳连接，每秒发一次数据
						times_1s++;
						if ((times_1s >= 10) && linkFlag && userId.startsWith("1")) {
//							dout.write(0x39);
							times_1s = 0;
						}
						
					}
					
					out.close();
					client.close();
					map.remove(userId);
					System.out.println("client:"+clientInfo+" 关闭了");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	static String readString(Socket client) throws IOException {
		InputStream inputStream = client.getInputStream();
		int len = inputStream.available();
		if (len != 0) {
			byte[] buffer = new byte[len];
			inputStream.read(buffer);
			//printHex(buffer);
			System.out.println("r:" + new String(buffer));
			return new String(buffer);
		}
		return null;
	};
	
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
	
	//給所有的客戶端發送消息
	static void sentData(String msg) {
		try {
			for (Socket socket : sockets) {
				if (!socket.isClosed()) {
					OutputStream dout = socket.getOutputStream();
					dout.write(msg.getBytes());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//給所有的手机客戶端發送消息,userId指的是硬件客户端的id
	static void sentDataToAllPhone(String userId, String msg) {
		try {
			for (String id : map.keySet()) {
				if (id.startsWith("1")) {//判断是否是手机客户端id
					Socket socket = map.get(id);
					if (!socket.isClosed()) {
						try {
							OutputStream dout = socket.getOutputStream();
							dout.write((userId+"-"+msg).getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	//userId:指的是当前发送者，源端
	//toId:被发送者，目的端
	//給指定的客戶端發送消息
	static void sentData(String userId, String toId, String msg) {
		try {
			Socket socket = map.get(toId);
			if (socket == null) {//目标id 不存在
				socket = map.get(userId);
				if (!socket.isClosed()) {//判断当前发送者状态是否保持连接					
					OutputStream dout = socket.getOutputStream();
					dout.write("user is not exit!".getBytes());//向发送者反馈目标不存在
				}
				
			} else {//目标id 存在
				if (!socket.isClosed()) {//目标id 连接可用
					//如果目标id是硬件终端，修正需要转发给终端的数据，将待发送数据 以0x5A开头，
					if (toId.charAt(0) != '1') {
						OutputStream dout = socket.getOutputStream();
						//以0x5A开头
						dout.write(("Z"+msg).getBytes());
						return;
					}
					
					//目标id是手机客户端 ，貌似这部分不会被执行，因为这里硬件客户端才会给手机客户端发送
					//且硬件客户端的转发已经单独处理过了
					OutputStream dout = socket.getOutputStream();
					dout.write((userId+"-"+msg).getBytes());
				} else {//目标id 当前断开连接了
					socket = map.get(userId);
					if (!socket.isClosed()) {
						OutputStream dout = socket.getOutputStream();
						dout.write("user is closed!".getBytes());
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
