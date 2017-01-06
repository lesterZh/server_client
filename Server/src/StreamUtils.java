




import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class StreamUtils {

    /**
     * 从输入流中读取字符串
     * @param is
     * @param charSet 读出时的编码格式设定
     * @return
     */
    public static String readString(InputStream is, String charSet) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            String res = new String(baos.toByteArray(), charSet);

            is.close();
            baos.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从文件中读取字符串
     * @param fileName
     * @param charSet 读出时的编码格式设定
     * @return
     */
    public static String readString(String fileName, String charSet) {
        FileInputStream iStream = null;

        try {
            iStream = new FileInputStream(fileName);
            return readString(iStream, charSet);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (iStream != null)
                    iStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 从输入流中读取字符串 默认utf-8的编码格式
     * @param is
     * @return
     */
    public static String readString(InputStream is) {
        return readString(is, "utf-8");
    }

    /**
     * 从文件名中读取文件中的字符串 默认utf-8的编码格式
     * @param is
     * @return
     */
    public static String readString(String is) {
        return readString(is, "utf-8");
    }

    

    /**
     * 复制
     * @param is
     * @param os
     */
    public static void copy(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();

            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制
     * @param source
     * @param dest
     */
    public static void copy(File source, File dest) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            copy(is, os);
            is.close();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 复制
     * @param source
     * @param dest
     */
    public static void copy(String source, String dest) {
        File sFile = new File(source);
        File dFile = new File(dest);
        copy(sFile, dFile);
    }

    /**
     * 将类序列化成字符串
     * @param obj
     * @return
     */
    public static String objectToString(Object obj) {
        try {
            if (obj == null) return null;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);

            String serStr = bos.toString("ISO-8859-1");
            String res = java.net.URLEncoder.encode(serStr, "UTF-8");
            bos.close();
            oos.close();

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串反序列化为类对象
     * @param str
     * @return
     */
    public static Object stringToObject(String str) {
        try {
            if (str == null) return null;
            String redStr = java.net.URLDecoder.decode(str, "UTF-8");
            byte[] bytes = redStr.getBytes("ISO-8859-1");
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);

            Object obj = ois.readObject();
            bis.close();
            ois.close();

            return obj;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


}
