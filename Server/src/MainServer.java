
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
		// �������20006�˿ڼ����ͻ��������TCP����
		ServerSocket server = new ServerSocket(PORT);
		System.out.println("�������������ȴ��ͻ������ӡ���\r\n");
		Socket client = null;
		boolean f = true;
		while (f) {
			// �ȴ��ͻ��˵����ӣ����û�л�ȡ����
			client = server.accept();
			//System.out.println("��ͻ������ӳɹ���");
			// Ϊÿ���ͻ������ӿ���һ���߳�
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
					System.out.println("�ͻ��ˣ�" + clientInfo);
					
					sockets.add(client);
					// ��ȡSocket���������������ͻ��˷�������
					PrintStream out = new PrintStream(client.getOutputStream());
					// ��ȡSocket�����������������մӿͻ��˷��͹���������
					BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
					
					OutputStream dout = client.getOutputStream();
					
					while (runFlag) {
						// ���մӿͻ��˷��͹���������
						//���ݷ����ߡ������߷ַ���Ϣ
						try {
							String data = readString(client);
							if (data != null) {
//								System.out.println("rec:"+data);
								String[] res = data.split("-");
								if (res[0].equals("userId")) {//ע��userID
									userId = res[1];
									map.put(userId, client);
									System.out.println(map);
									
									//�������ӳɹ���ʾ
									dout.write("welcome".getBytes());
									linkFlag = true;
								} else if (res[0].equals("toId")) {
									toId = res[1];
									//����� Ӳ�����Զ����͵�ʱ���ܷ��͵�һ�� 's' �ַ�
								} else if (res[0].equals("") || res[0].equals("s") || res[0].equals("ss")) {//���յ�Ӳ��������������
									int splitId = data.indexOf("-");
									
									//sentDataToAllPhone(userId, data.substring(splitId+1));
									sentDataToAllPhone(userId, res[1]);
									
								} else {//�ֻ��ͻ��˷��͹���������
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
						
						//ֻ������ֻ��ͻ��ˣ��������ӣ�ÿ�뷢һ������
						times_1s++;
						if ((times_1s >= 10) && linkFlag && userId.startsWith("1")) {
//							dout.write(0x39);
							times_1s = 0;
						}
						
					}
					
					out.close();
					client.close();
					map.remove(userId);
					System.out.println("client:"+clientInfo+" �ر���");
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
	
	//�o���еĿ͑��˰l����Ϣ
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
	
	//�o���е��ֻ��͑��˰l����Ϣ,userIdָ����Ӳ���ͻ��˵�id
	static void sentDataToAllPhone(String userId, String msg) {
		try {
			for (String id : map.keySet()) {
				if (id.startsWith("1")) {//�ж��Ƿ����ֻ��ͻ���id
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
	
	//userId:ָ���ǵ�ǰ�����ߣ�Դ��
	//toId:�������ߣ�Ŀ�Ķ�
	//�oָ���Ŀ͑��˰l����Ϣ
	static void sentData(String userId, String toId, String msg) {
		try {
			Socket socket = map.get(toId);
			if (socket == null) {//Ŀ��id ������
				socket = map.get(userId);
				if (!socket.isClosed()) {//�жϵ�ǰ������״̬�Ƿ񱣳�����					
					OutputStream dout = socket.getOutputStream();
					dout.write("user is not exit!".getBytes());//�����߷���Ŀ�겻����
				}
				
			} else {//Ŀ��id ����
				if (!socket.isClosed()) {//Ŀ��id ���ӿ���
					//���Ŀ��id��Ӳ���նˣ�������Ҫת�����ն˵����ݣ������������� ��0x5A��ͷ��
					if (toId.charAt(0) != '1') {
						OutputStream dout = socket.getOutputStream();
						//��0x5A��ͷ
						dout.write(("Z"+msg).getBytes());
						return;
					}
					
					//Ŀ��id���ֻ��ͻ��� ��ò���ⲿ�ֲ��ᱻִ�У���Ϊ����Ӳ���ͻ��˲Ż���ֻ��ͻ��˷���
					//��Ӳ���ͻ��˵�ת���Ѿ������������
					OutputStream dout = socket.getOutputStream();
					dout.write((userId+"-"+msg).getBytes());
				} else {//Ŀ��id ��ǰ�Ͽ�������
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
