package com.dbd.cms.kits;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/**
 * Created by yuhaihui8913 on 2017/5/31.
 */
public class ExcelKit {

    public static String cellValue2Str(Cell cell){
        CellType ct=cell.getCellTypeEnum();
        String string=null;
        switch (ct){
            case STRING:
                string=cell.getStringCellValue();
                break;
            case NUMERIC:
                string=cell.getNumericCellValue()+"";
                break;
            case BOOLEAN:
                string=cell.getBooleanCellValue()+"";
                break;
            case BLANK:
                string="";
                break;
            case FORMULA:
                string=cell.getCellFormula();
                break;
        }
        return string;
    }
}
