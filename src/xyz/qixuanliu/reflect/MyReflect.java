package xyz.qixuanliu.reflect;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MyReflect {

    //加载配置文件的方法
    public static Properties loadProperties() throws Exception{
        //加载配置文件，获取配置文件参数
        InputStream inputStream = new FileInputStream("src/sql.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }
    //获取class对象的方法
    public static Class<?> getClass(String clsPath) throws Exception{
        //通过反射获取实例化对象
        //Class<?> cls = Class.forName("xyz.qixuanliu.entity." + clsName);
        Class<?> cls = Class.forName(clsPath);
        return cls;
    }
    //获取实体类中属性个数的方法
    public static String getFieldCount() throws Exception{
        //得到Properties
        Properties properties = loadProperties();
        //获取配置文件中的属性个数
        String fieldCount = properties.getProperty("fieldCount");
        return fieldCount;
    }
    //获取配置文件中的属性名称
    public static Map<String,String> getFieldName() throws Exception{
        //得到Properties
        Properties properties = loadProperties();
        //得到实体类中的属性个数
        String fieldCount = getFieldCount();
        //获取配置文件中的属性名称
        Map<String,String> mFieldName = new HashMap<String,String>();
        for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
            String fName = "field" + i;
            String fValue = properties.getProperty(fName);
            mFieldName.put(fName, fValue);
        }
        return mFieldName;
    }
    //获取配置文件中的set方法的参数类型
    public static Map<String,String> getSetParameterType() throws Exception{
        //得到Properties
        Properties properties = loadProperties();
        //得到实体类中的属性个数
        String fieldCount = getFieldCount();
        //获取配置文件中的set方法的参数类型
        Map<String,String> mAllSetType = new HashMap<String,String>();
        for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
            String tName = "Type" + i;
            String tValue = properties.getProperty("SetParameterType"+i);
            if(tValue.equals("java.lang.Float")) {
                tValue="java.lang.Float";
                mAllSetType.put(tName, tValue);
            }else if(tValue.equals("java.lang.Integer")){
                tValue="java.lang.Integer";
                mAllSetType.put(tName, tValue);
            }else if(tValue.equals("java.lang.Boolean")){
                tValue="java.lang.Boolean";
                mAllSetType.put(tName, tValue);
            }else{
                mAllSetType.put(tName, tValue);
            }
        }
        return mAllSetType;
    }
    //获取实体类中所有的set方法
    public static Map<String,Method> getAllSetMethod(String clsPath) throws Exception{
        //得到Properties
        Properties properties = loadProperties();
        //得到Class对象
        Class<?> cls= getClass(clsPath);
        //得到实体类中的属性个数
        String fieldCount = getFieldCount();
        //获得配置文件中的set方法
        Map<String,Method> mSetMethod = new HashMap<String,Method>();
        for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
            String mN = "set" + i;
            String shifou = properties.getProperty("SetParameterType" + i);
            Method mV = null;
            if(shifou.contains("java.lang.Float")) {
                mV=cls.getMethod(properties.getProperty("setField" + i),float.class);
                mSetMethod.put(mN, mV);
            }else if(shifou.contains("java.lang.Integer")){
                mV=cls.getMethod(properties.getProperty("setField" + i),Integer.class);
                mSetMethod.put(mN, mV);
            }else if(shifou.contains("java.lang.String")){
                mV=cls.getMethod(properties.getProperty("setField" + i),String.class);
                mSetMethod.put(mN, mV);
            }else if(shifou.contains("java.lang.Boolean")){
                mV=cls.getMethod(properties.getProperty("setField" + i),Boolean.class);
                mSetMethod.put(mN, mV);
            }else {
                continue;
            }
        }
        return mSetMethod;
    }
    //获取实体类中所有get方法
    public static Map<String,Method> getAllGetMethod(String clsPath) throws Exception{
        //得到Properties
        Properties properties = loadProperties();
        //得到Class对象
        Class<?> cls= getClass(clsPath);
        //得到实体类中的属性个数
        String fieldCount = getFieldCount();
        //获得配置文件中的get方法
        Map<String,Method> mGetMethod = new HashMap<String,Method>();
        for(int i = 1;i<=Integer.parseInt(fieldCount);i++) {
            String mN = properties.getProperty("getField" + i);
            Method mV = cls.getMethod(properties.getProperty("getField" + i));
            mGetMethod.put(mN, mV);
        }
        return mGetMethod;
    }
}
