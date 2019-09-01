package xyz.qixuanliu.test;

import xyz.qixuanliu.excel.SaveData2DB;

public class TestProgram {

    /**
     * 测试功能
     * @param args
     * @throws Exception
     */
    public static void main(String[] args)throws Exception{
        SaveData2DB.save("C:\\Users\\dell-1\\Desktop\\Temp\\student_info.xls","xyz.qixuanliu.entity.Student");
        //ReadDateToExcel.read("xyz.qixuanliu.entity.Student", "D://student.xls");
    }
}
