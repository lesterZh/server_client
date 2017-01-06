import java.sql.*;

public class MySqlServer {

	public static void main(String[] args) {
		try{
            //����Class.forName()����������������
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("�ɹ�����MySQL������");
        }catch(ClassNotFoundException e1){
            System.out.println("�Ҳ���MySQL����!");
            e1.printStackTrace();
        }
		
		String url="jdbc:mysql://localhost:3306/zhtDatabase";    //JDBC��URL    
        //����DriverManager�����getConnection()���������һ��Connection����
        Connection conn;
        Statement stmt;
        try {
            conn = DriverManager.getConnection(url, "root","root");
            //����һ��Statement����
            stmt = conn.createStatement(); //����Statement����
            System.out.println("�ɹ����ӵ����ݿ⣡");
            
            int result=0;
            //�������ݱ�
            String createTableSql = "create table nimei (id int not null auto_increment"
            		+ " primary key, name char(20))";
//            result = stmt.executeUpdate(createTableSql);
            System.out.println(result == -1 ? "����ʧ��" : "����ɹ�");
            //��������
            String insertSql = "insert stu values (null,\"tom\",66,\"100989\")";
            result = stmt.executeUpdate(insertSql);
            System.out.println(result == -1 ? "����ʧ��" : "����ɹ�");
            
            //��ѯ
            String querySql = "select * from stu";
            ResultSet resultSet = stmt.executeQuery(querySql);
            
            while(resultSet.next()) {
            	System.out.printf("%-6s %-4d %-13s\n",resultSet.getString(2),resultSet.getInt(3)
            			,resultSet.getString(4));
            }
            
            //����
            String updateSql = "update stu set name=? where age=?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateSql);
            preparedStatement.setString(1, "hanmei");
            preparedStatement.setInt(2, 28);
            preparedStatement.executeUpdate();
            
            //ɾ��
            String deleteSql = "delete from stu where name=?";
            preparedStatement = conn.prepareStatement(deleteSql);
            preparedStatement.setString(1, "С��");
            preparedStatement.executeUpdate();
            
            stmt.close();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	
		}
	}

}
