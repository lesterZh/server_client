import java.sql.*;

public class MySqlServer {

	public static void main(String[] args) {
		try{
            //调用Class.forName()方法加载驱动程序
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("成功加载MySQL驱动！");
        }catch(ClassNotFoundException e1){
            System.out.println("找不到MySQL驱动!");
            e1.printStackTrace();
        }
		
		String url="jdbc:mysql://localhost:3306/zhtDatabase";    //JDBC的URL    
        //调用DriverManager对象的getConnection()方法，获得一个Connection对象
        Connection conn;
        Statement stmt;
        try {
            conn = DriverManager.getConnection(url, "root","root");
            //创建一个Statement对象
            stmt = conn.createStatement(); //创建Statement对象
            System.out.println("成功连接到数据库！");
            
            int result=0;
            //创建数据表
            String createTableSql = "create table nimei (id int not null auto_increment"
            		+ " primary key, name char(20))";
//            result = stmt.executeUpdate(createTableSql);
            System.out.println(result == -1 ? "建表失败" : "建表成功");
            //插入数据
            String insertSql = "insert stu values (null,\"tom\",66,\"100989\")";
            result = stmt.executeUpdate(insertSql);
            System.out.println(result == -1 ? "插入失败" : "插入成功");
            
            //查询
            String querySql = "select * from stu";
            ResultSet resultSet = stmt.executeQuery(querySql);
            
            while(resultSet.next()) {
            	System.out.printf("%-6s %-4d %-13s\n",resultSet.getString(2),resultSet.getInt(3)
            			,resultSet.getString(4));
            }
            
            //更新
            String updateSql = "update stu set name=? where age=?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateSql);
            preparedStatement.setString(1, "hanmei");
            preparedStatement.setInt(2, 28);
            preparedStatement.executeUpdate();
            
            //删除
            String deleteSql = "delete from stu where name=?";
            preparedStatement = conn.prepareStatement(deleteSql);
            preparedStatement.setString(1, "小明");
            preparedStatement.executeUpdate();
            
            stmt.close();
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	
		}
	}

}
