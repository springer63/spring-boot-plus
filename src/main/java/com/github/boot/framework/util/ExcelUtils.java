package com.github.boot.framework.util;

import com.github.boot.framework.web.exception.ApplicationException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @Description 读写Excel工具类
 * @author cjh
 * @version 1.0
 * @date：2017年5月14日 上午11:27:16
 */
public class ExcelUtils {

	/**
	 * 读取Excel文件
	 * @param in excel文件流
	 * @param clazz 封装数据的对象类型
	 * @return 数据集合
	 * @throws ApplicationException
	 */
	public static <T> List<T> read(InputStream in, Class<T> clazz) {
		return read(in, clazz, null, "");
	}

	/**
	 * 读取Excel文件
	 * @param in excel文件流
	 * @param clazz 封装数据的对象类型
	 * @param mapping Excel列与数据对象属性之间的映射关系（key为列序号 从1开始， value为属性名）
	 * @return 数据集合
	 * @throws ApplicationException
	 */
	public static <T> List<T> read(InputStream in, Class<T> clazz, Map<Integer, String> mapping) {
		return read(in, clazz, mapping, "");
	}
	
    /**
     * 读取Excel文件
     * @param in excel文件流
     * @param clazz 封装数据的对象类型
     * @param mapping Excel列与数据对象属性之间的映射关系（key为列序号 从1开始， value为属性名）
     * @param sheetName 读取工作表的名称
     * @return 数据集合
     * @throws ApplicationException 
     */
    public static <T> List<T> read(InputStream in, Class<T> clazz, Map<Integer, String> mapping, String sheetName) {
    	List<T> data;
		Workbook workbook = null;
    	try {
			workbook = WorkbookFactory.create(in);
			Sheet sheet;
			if(StringUtils.isEmpty(sheetName)){
				sheet = workbook.getSheetAt(0);
			}else{
				sheet = workbook.getSheet(sheetName);
				if(sheet == null) {
					throw new RuntimeException("Excel文件找不到对应的表格");
				}
			}
			data = new ArrayList<>(sheet.getLastRowNum());
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				data.add(readRow(sheet.getRow(i), clazz, mapping));
			}
		} catch (EncryptedDocumentException e) {
			throw new RuntimeException("Excel文件已被加密");
		} catch (InvalidFormatException e) {
			throw new RuntimeException("Excel文件格式不正确");
		} catch (IOException e) {
			throw new RuntimeException("Excel文件读取加密");
		} catch (SecurityException e) {
			throw new RuntimeException("Excel文件已被加密");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Excel文件格式错误");
		} finally {
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
    }

	/**
	 * 导出Excel文件
	 * @param data
	 * @param mapping
	 * @param excelName
	 * @return
	 */
	public static <T> void write(List<T> data, Map<String, String> mapping, String excelName, OutputStream out){
		HSSFWorkbook wb =  new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(excelName);
		fillSheet(wb, sheet, data, mapping);
		try {
			wb.write(out);
			wb.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    
	/**
     * 读取Excel一行数据
     * @param row
     * @param clazz
     * @return
     * @throws ApplicationException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @SuppressWarnings("unchecked")
	private static <T> T readRow(Row row, Class<T> clazz, Map<Integer, String> fieldMapping){
		if(fieldMapping == null){
			return (T) getCellData(row.getCell(0));
		}
		T t ;
		try {
			t = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("实例化数据对象失败: " + clazz.getName(), e);
		}
		for (Entry<Integer, String> entry : fieldMapping.entrySet()) {
			Object value = getCellData(row.getCell(entry.getKey()));
			setFieldValue(t, entry.getValue(), value);
		}
    	return t;
    }
 
    /**
     * 获取单元格中的值
     * @param cell
     * @return
     */
	private static Object getCellData(Cell cell) {
		if(cell == null){
			return null;
		}
		switch (cell.getCellTypeEnum()) {
			case BLANK ://空白
				return null;
			case NUMERIC: // 数值型 0----日期类型也是数值型的一种
				if(DateUtil.isCellDateFormatted(cell)){
					return cell.getDateCellValue();
				}
				return cell.getNumericCellValue();
			case STRING: // 字符串型 1
				return cell.getStringCellValue().trim();
			case FORMULA: // 公式型 2
				cell.setCellType(CellType.STRING);
				return cell.getStringCellValue();
			case BOOLEAN: // 布尔型 4
				return cell.getBooleanCellValue();
			case ERROR: // 错误 5
				return null;
		default :
			return null;
		}
	}
	
	/**
	 * 为数据对象属性赋值
	 * @param target
	 * @param fieldName
	 * @param value
	 * @throws ApplicationException
	 */
	private static void setFieldValue(Object target, String fieldName, Object value){
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			if(value instanceof Number){
				Class<?> type = field.getType();
				Number number = (Number) value;
				if(type == Long.class || long.class == type){
					value = number.longValue();
				}else if(type == Integer.class || int.class == type){
					value = number.intValue();
				}else if(type == Short.class || short.class == type){
					value = number.byteValue();
				}else if(type == Float.class || float.class == type){
					value = number.floatValue();
				}else if(type == Double.class || double.class == type){
					value = number.doubleValue();
				}
			}
			field.set(target, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("数据对象中未找到属性【" + fieldName + "】",e);
		} catch (SecurityException e) {
			throw new RuntimeException("数据对象中属性【" + fieldName + "】不可访问", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("数据对象中属性【" + fieldName + "】不可访问", e);
		} catch (IllegalArgumentException e){
			throw new RuntimeException("数据对象中属性【" + fieldName + "】类型不匹配", e);
		}
	}
	
	/**
	 * 填充工作表
	 * @param sheet
	 * @param list
	 * @param mapping
	 * @throws Exception
	 */
	private static <T> void fillSheet(HSSFWorkbook wb, HSSFSheet sheet, List<T> list, Map<String,String> mapping){
		String[] fieldNames = new String[mapping.size()];
        String[] headNames = new String[mapping.size()];
        //填充数组 
        int count=0; 
        for(Entry<String, String> entry:mapping.entrySet()){
			headNames[count]=entry.getKey();
            fieldNames[count]=entry.getValue();
            count++; 
        }
        //填充表头 
        HSSFRow firstRow = sheet.createRow(0);
        HSSFCellStyle headStyle = createStyle(wb, 1);
        for(int i=0; i<headNames.length; i++){ 
            sheet.setColumnWidth(i, headNames[i].length()*4*256);   //设置自动列宽
            HSSFCell cell = firstRow.createCell(i);
            cell.setCellStyle(headStyle);  //设置样式
            cell.setCellValue(headNames[i]);
        } 
           
        //填充内容 
        int rowNo = 1;
        HSSFCellStyle contentStyle = createStyle(wb, 2);
        for(int index = 0; index < list.size(); index++){
            T item = list.get(index); 
            HSSFRow row = sheet.createRow(rowNo);
            for(int i=0; i< fieldNames.length; i++){ 
            	Object fieldValue = ReflectionUtils.getFieldValue(item, fieldNames[i]);
            	String fieldValueString = fieldValue == null ? "" : fieldValue.toString();
            	HSSFCell cell2 = row.createCell(i);
            	cell2.setCellStyle(contentStyle);
            	cell2.setCellValue(fieldValueString);
            } 
            rowNo ++;
        } 
	}
	
	/**
	 * 设置单元格样式
	 * @param hssfWorkbook
	 * @param layer
	 * @return
	 * @throws Exception
	 */
	private static HSSFCellStyle createStyle(HSSFWorkbook hssfWorkbook, int layer){
        HSSFCellStyle style = hssfWorkbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        if (layer == 1) {
        	//设置背景颜色
        	style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
	        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格
	        style.setAlignment(HorizontalAlignment.CENTER);//居中显示
	        //设置字体
	        HSSFFont hssfFont = hssfWorkbook.createFont();
	        hssfFont.setFontName("宋体"); // 字体
	        hssfFont.setColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());// 字体颜色
	        hssfFont.setFontHeight((short) 300); // 字高
	        hssfFont.setBold(true);// 字体加粗
	        style.setFont(hssfFont);
        } else if (layer == 2) {
	        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());// 背景
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格
			style.setAlignment(HorizontalAlignment.CENTER);//居中显示
	        HSSFFont font = hssfWorkbook.createFont();
	        font.setFontName("宋体");
	        font.setBold(true);// 字体加粗
	        style.setFont(font);
        } else if (layer == 3) {
			style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.ORANGE.getIndex());
			style.setFillPattern(FillPatternType.SOLID_FOREGROUND);//填充单元格
        }
        return style;
     }

}