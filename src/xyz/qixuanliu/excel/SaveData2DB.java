package xyz.qixuanliu.excel;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import xyz.qixuanliu.util.DbUtil;
import xyz.qixuanliu.entity.Thing;
import xyz.qixuanliu.reflect.MyReflect;


public class SaveData2DB {

    @SuppressWarnings({ "rawtypes" })
    public static void save(String excelPath,String clsPath) throws Exception {
        //加载配置文件sql.properties，获取配置文件中的参数
        Properties properties = MyReflect.loadProperties();
        //获取配置文件中的sql语句
        String INSERT_STUDENT_SQL = properties.getProperty("INSERT_STUDENT_SQL");
        String SELECT_STUDENT_SQL = properties.getProperty("SELECT_STUDENT_SQL");
        //获得配置文件中的get方法
        Map<String,Method> mMethod = MyReflect.getAllGetMethod(clsPath);
        //判断Excel文件的扩展名，并调用相应的方法
        List<Thing> list = null;
        String[] resultStr = excelPath.split("\\.");
        if(resultStr[resultStr.length-1].equals("xls")) {
            ReadExcelForXls xlsMain = new ReadExcelForXls();
            list = xlsMain.read(excelPath,clsPath);
        }else if(resultStr[resultStr.length-1].equals("xlsx")) {
            ReadExcelForXlsx xlsxMain = new ReadExcelForXlsx();
            list = xlsxMain.read(excelPath,clsPath);
        }else {
            throw new RuntimeException("文件格式不正确!");
        }
        //遍历list.首先查询数据库中是否存在当前记录，不存在就插入当前记录
        Thing th = null;
        for (int i = 0; i < list.size(); i++) {
            th = list.get(i);
            System.out.println("save:" +th.toString());
            List l = DbUtil.selectOne(SELECT_STUDENT_SQL + "'%" + (mMethod.get(properties.getProperty("byWhat"))).invoke(th) + "%'",th,clsPath);
            if (!l.contains(1)) {
                DbUtil.insert(INSERT_STUDENT_SQL, th,clsPath);
            } else {
                System.out.println("The Record was Exist :" + th.toString() +", and has been throw away!");
            }
        }
    }
}
