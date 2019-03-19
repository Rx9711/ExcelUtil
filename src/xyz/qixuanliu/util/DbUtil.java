package xyz.qixuanliu.util;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import xyz.qixuanliu.entity.Thing;
import xyz.qixuanliu.reflect.MyReflect;

public class DbUtil {

    public static void insert(String sql, Object obj,String clsPath) throws Exception {

        Thing th = (Thing)obj;
        //加载配置文件sql.properties，获取配置文件参数
        Properties pro = MyReflect.loadProperties();
        //获取配置文件中的属性个数
        String fieldCount = pro.getProperty("fieldCount");
        //获得配置文件中的get方法
        Map<String,Method> mGetMethod = MyReflect.getAllGetMethod(clsPath);
        //加载配置文件db.properties，获取数据库四大参数
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/db.properties"));
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            ps = conn.prepareStatement(sql);
            for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
                ps.setString(i, String.valueOf((mGetMethod.get(pro.getProperty("getField" + i))).invoke(th)));
            }
            boolean flag = ps.execute();
            if(!flag){
                System.out.println("Save data : " + th.toString() + " succeed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static List selectOne(String sql, Object obj,String clsPath) throws Exception {

        Thing th = (Thing) obj;
        //加载配置文件db.properties
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/db.properties"));
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List list = new ArrayList();
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(test(th,rs,clsPath)){
                    list.add(1);
                }else{
                    list.add(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }
    //判断数据库中是否存在当前要插入的数据
    public static boolean test(Thing th,ResultSet rs,String clsPath) throws Exception{
        //加载配置文件sql.properties，获取配置文件参数
        Properties pro2 = MyReflect.loadProperties();
        //获取配置文件中的属性个数
        String count = MyReflect.getFieldCount();
        //获得配置文件中的get方法
        Map<String,Method> method = MyReflect.getAllGetMethod(clsPath);
        //判断是否存在数据
        for(int i = 1;i<=Integer.valueOf(count);i++) {
            if((rs.getString(pro2.getProperty("field" + i))).equals((method.get("getField" + i)).invoke(th) )) {
                return true;
            }
        }
        return false;
    }


    public static ResultSet selectAll(String sql) throws Exception {

        //加载配置文件db.properties，并获取相应的参数
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/db.properties"));
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return rs;
    }

    //从数据库中查询所有的数据
    public static List<Thing> findtAll(String clsPath) throws Exception {
        List<Thing> list=new ArrayList<Thing>();
        //加载配置文件sql.properties，获取配置文件参数
        Properties pro = MyReflect.loadProperties();
        //获取Class对象
        Class<?> clazz = MyReflect.getClass(clsPath);
        //获取配置文件中的属性个数
        String fieldCount = pro.getProperty("fieldCount");
        //获取配置文件中的所有属性
        Map<String, String> fieldName = MyReflect.getFieldName();
        //获取实体类中所有的set方法
        Map<String, Method> allSetMethod = MyReflect.getAllSetMethod(clsPath);
        //获取配置文件中的set方法参数的类型
        Map<String, String> setParameterType = MyReflect.getSetParameterType();
        //加载配置文件db.properties，并获取相应的参数
        Properties properties = new Properties();
        properties.load(new FileInputStream("src/db.properties"));
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");
        //查询所有数据的sql语句
        String sql = pro.getProperty("SELECTALL_STUDENT_SQL");
        //连接数据库获取数据
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                //实例化对象
                Thing th = (Thing) clazz.newInstance();
                for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
                    //实体类中的set方法参数类型
                    String shi = setParameterType.get("Type" + i);
                    //属性名称
                    String field = "field" + i;
                    String field_real = fieldName.get(field);
                    //判断属性类型
                    if(shi.contains("java.lang.Float")) {
                        Float f = rs.getFloat(field_real);
                        (allSetMethod.get("set" + i)).invoke(th,f);
                    }else if(shi.contains("java.lang.Integer")){
                        Integer in = rs.getInt(field_real);
                        (allSetMethod.get("set" + i)).invoke(th,in);
                    }else if(shi.contains("java.lang.Boolean")){
                        Boolean b = rs.getBoolean(field_real);
                        (allSetMethod.get("set" + i)).invoke(th,b);
                    }else if(shi.contains("java.lang.String")){
                        String ss = rs.getString(field_real);
                        (allSetMethod.get("set" + i)).invoke(th,ss);
                    }else {
                        continue;
                    }
                }
                list.add(th);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }


}
