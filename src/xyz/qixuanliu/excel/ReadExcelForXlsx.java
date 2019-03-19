package xyz.qixuanliu.excel;

import org.apache.poi.ss.usermodel.*;

import xyz.qixuanliu.entity.Thing;
import xyz.qixuanliu.reflect.MyReflect;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XSSF对xlsx后缀名的Excel进行读取内容
 */
public class ReadExcelForXlsx{

    private Thing th;

    public List<Thing> read(String excelPath,String clsPath) throws Exception {
        //通过反射获取实例化对象
        Class<?> cls = MyReflect.getClass(clsPath);
        //获取配置文件中的属性个数
        String fieldCount = MyReflect.getFieldCount();
        //获取配置文件中的set方法的参数类型
        Map<String,String> mSetType = MyReflect.getSetParameterType();
        //获得配置文件中的set方法
        Map<String,Method> mSetMethod = MyReflect.getAllSetMethod(clsPath);
        //生成本地Excel对象
        File file = new File(excelPath);
        InputStream inputStream = null;
        Workbook workbook = null;
        List<Thing> list = new ArrayList<Thing>();
        inputStream = new FileInputStream(file);
        workbook = WorkbookFactory.create(inputStream);
        inputStream.close();
        //工作表对象
        Sheet sheet = (Sheet) workbook.getSheetAt(0);
        // 循环行Row
        for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            Map<String,Cell> mCell = new HashMap<String,Cell>();
            if (row != null) {
                th = (Thing)cls.newInstance();
                for(int i = 0;i<Integer.parseInt(fieldCount);i++) {
                    int j = i+1;
                    String mCellName = "mCell" + j;
                    Cell str = row.getCell(i);
                    if (str != null)
                        str.setCellType(CellType.STRING);
                    mCell.put(mCellName, str);
                }
                for(int i= 1; i<=Integer.parseInt(fieldCount);i++) {
                    String shi = mSetType.get("Type" + i);
                    if(shi.contains("java.lang.Float")) {
                        (mSetMethod.get("set" + i)).invoke(th,Float.valueOf((mCell.get("mCell"+i)).getStringCellValue()));
                    }else if(shi.contains("java.lang.Integer")){
                        (mSetMethod.get("set" + i)).invoke(th,Integer.valueOf((mCell.get("mCell"+i)).getStringCellValue()));
                    }else if(shi.contains("java.lang.Boolean")){
                        (mSetMethod.get("set" + i)).invoke(th,Boolean.valueOf((mCell.get("mCell"+i)).getStringCellValue()));
                    }else if(shi.contains("java.lang.String")){
                        (mSetMethod.get("set" + i)).invoke(th,(mCell.get("mCell"+i)).getStringCellValue());
                    }else {
                        continue;
                    }
                }
                list.add(th);
            }
        }
        return list;
    }

}