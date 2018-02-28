package site.franksite.cet.http.test;

import java.io.File;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

public class PoiTest {
	@Test
	public void testWritePoi() {
		HSSFWorkbook cetGraeBook = new HSSFWorkbook();
		HSSFSheet cetGradeSheet = cetGraeBook.createSheet("CET成绩");
		// 创建头
		HSSFRow header = cetGradeSheet.createRow(0);
		HSSFCell cell = header.createCell(0);
		cell.setCellValue("姓名");
		
		cell = header.createCell(1);
		cell.setCellValue("准考证号");
		
		cell = header.createCell(2);
		cell.setCellValue("学校");
		
		cell = header.createCell(3);
		cell.setCellValue("总成绩");
		
		cell = header.createCell(4);
		cell.setCellValue("听力");
		
		cell = header.createCell(5);
		cell.setCellValue("阅读");
		
		cell = header.createCell(6);
		cell.setCellValue("写作/翻译");
		
		try {
			cetGraeBook.write(new File("test.xls"));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				cetGraeBook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
