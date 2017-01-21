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
		// �ͻ��������뱾����20006�˿ڽ���TCP����

//		Socket client = new Socket("haitao.uicp.io", 20005);
		final Socket client = new Socket("127.0.0.1", 20005);
		// client.setSoTimeout(10000);
		// ��ȡ��������
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		// ��ȡSocket��������������������ݵ������
		PrintStream out = new PrintStream(client.getOutputStream());
		// ��ȡSocket�����������������մӷ���˷��͹���������
		BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
		
		OutputStream clientStreamOut = client.getOutputStream();
		
		
		//�����߳���ѯ������
		new Thread() {
			
			public void run() {
				while (flag) {
					try {
						// �ӷ������˽��������и�ʱ�����ƣ�ϵͳ���裬Ҳ�����Լ����ã������������ʱ�䣬����׳����쳣
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

		
		//����id�ͽ�����
		System.out.println("�����û�����");
		userId = input.readLine();
		out.print("userId-"+userId);
		
		
		
		//��������
		while (flag) {
			// System.out.print("input��");
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
			//�������ݵ������
			//out.print(str);
			
			//����
			if ("bye".equals(str)) {
				flag = false;
			}
		}
		input.close();
		
		
		if (client != null) {
			// ������캯�������������ӣ���ر��׽��֣����û�н��������ӣ���Ȼ���ùر�
			client.close(); //ֻ�ر�socket������������������Ҳ�ᱻ�ر�
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
