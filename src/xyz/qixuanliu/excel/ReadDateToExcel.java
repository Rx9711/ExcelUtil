package xyz.qixuanliu.excel;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import xyz.qixuanliu.entity.Thing;
import xyz.qixuanliu.reflect.MyReflect;
import xyz.qixuanliu.util.DbUtil;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ReadDateToExcel {

    public static void read(String clsPath,String filePath) {
        try {
            //加载sql.properties文件
            Properties properties = MyReflect.loadProperties();
            //获取配置文件中的属性个数
            String fieldCount = MyReflect.getFieldCount();
            //获取配置文件中的所有属性
            Map<String, String> fieldName = MyReflect.getFieldName();
            //获取配置文件中的set方法参数的类型
            Map<String, String> setParameterType = MyReflect.getSetParameterType();
            //获取实体类中的所有get方法
            Map<String, Method> allGetMethod = MyReflect.getAllGetMethod(clsPath);
            //创建WritableWorkbook对象
            WritableWorkbook wwb = null;
            // 创建可写入的Excel工作簿
            String fileName = filePath;
            File file=new File(fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            //以fileName为文件名来创建一个Workbook
            wwb = Workbook.createWorkbook(file);
            // 创建工作表
            WritableSheet ws = wwb.createSheet("Test Shee 1", 0);
            //查询数据库中所有的数据
            List<Thing> list= DbUtil.findtAll(clsPath);
            for(int i= 0;i<=Integer.parseInt(fieldCount);i++) {
                String labelName = "field" + i;
                Label label = new Label(i,0,fieldName.get(labelName));
                ws.addCell(label);
            }
            for (int i = 0; i < list.size(); i++) {
                Label l = null;
                for(int j= 1;j<=Integer.parseInt(fieldCount);j++) {
                    //实体类中的set方法参数类型
                    String shi = setParameterType.get("Type" + j);
                    //判断属性类型
                    if(shi.contains("java.lang.Float")) {
                        l = new Label(j,i+1,String.valueOf((allGetMethod.get(properties.getProperty("getField" + j))).invoke(list.get(i))));
                        ws.addCell(l);
                    }else if(shi.contains("java.lang.Integer")){
                        l = new Label(j,i+1,String.valueOf((allGetMethod.get(properties.getProperty("getField" + j))).invoke(list.get(i))));
                        ws.addCell(l);
                    }else if(shi.contains("java.lang.Boolean")){
                        l = new Label(j,i+1,String.valueOf((allGetMethod.get(properties.getProperty("getField" + j))).invoke(list.get(i))));
                        ws.addCell(l);
                    }else if(shi.contains("java.lang.String")){
                        l = new Label(j,i+1,(String) (allGetMethod.get(properties.getProperty("getField" + j))).invoke(list.get(i)));
                        ws.addCell(l);
                    }else {
                        continue;
                    }
                }
            }
            //写进文档
            wwb.write();
            // 关闭Excel工作簿对象
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
