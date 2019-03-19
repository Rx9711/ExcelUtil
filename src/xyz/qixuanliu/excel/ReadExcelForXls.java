package xyz.qixuanliu.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import xyz.qixuanliu.entity.Thing;
import xyz.qixuanliu.reflect.MyReflect;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * HSSF对xls后缀名的Excel进行读取内容
 */
public class ReadExcelForXls {

    private Thing th;

    public List<Thing> read(String excelPath,String clsPath) throws Exception{
        //通过反射获取实例化对象
        Class<?> cls = MyReflect.getClass(clsPath);
        //获取配置文件中的属性个数
        String fieldCount = MyReflect.getFieldCount();
        //获得配置文件中的set方法
        Map<String,Method> mSetMethod = MyReflect.getAllSetMethod(clsPath);
        //获取配置文件中的set方法的参数类型
        Map<String,String> mSetType = MyReflect.getSetParameterType();
        //生成本地Excel对象
        File file = new File(excelPath);
        List<Thing> list = new ArrayList<Thing>();
        //判断文件是否存在
        if (!file.exists())
            System.out.println("文件不存在");
        //1.读取Excel的对象
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(file));
        //2.Excel工作薄对象
        @SuppressWarnings("resource")
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        //3.Excel工作表对象
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        // 循环行Row
        for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            Row row = hssfSheet.getRow(rowNum);
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


