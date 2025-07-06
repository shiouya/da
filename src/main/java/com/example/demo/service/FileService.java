package com.example.demo.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class FileService {

	public Workbook file(Workbook workbook, String type) {
		Workbook newworkbook = new XSSFWorkbook();
		
		Sheet sheet = workbook.getSheetAt(0);
		
		TreeMap<String, List<Row>> Map = mapType(sheet, type);
		
		for (Map.Entry<String, List<Row>> entry : Map.entrySet()) {
			String Key = entry.getKey();
			List<Row> rows = entry.getValue();

			Sheet newsheet = newworkbook.createSheet(Key);

			Row newHeaderRow = newsheet.createRow(0);
			int numberOfColumns = headerRow(sheet, newHeaderRow);

			int rowIndex = 1;
			for (Row oldRow : rows) {
				Row newRow = newsheet.createRow(rowIndex++);
				for (int col = 0; col < numberOfColumns; col++) {
					Cell oldCell = oldRow.getCell(col);
					Cell newCell = newRow.createCell(col);
					if (oldCell != null) {
						switch (oldCell.getCellType()) {
						case STRING:
							newCell.setCellValue(oldCell.getStringCellValue());
							break;
						case NUMERIC:
							if (DateUtil.isCellDateFormatted(oldCell)) {
								newCell.setCellValue(oldCell.getDateCellValue());
								newCell.setCellStyle(dateType(newworkbook));
							} else {
								newCell.setCellValue(oldCell.getNumericCellValue());
							}
							break;
						default:
							newCell.setCellValue("");
						}
					}
				}
			}
			newsheet.setColumnWidth(0, 15 * 200);
		}
		return newworkbook;
	}
	
	// 產生第一行，並且回傳一行內有幾格
	public int headerRow(Sheet sheet, Row newHeaderRow) {
		Row headerRow = sheet.getRow(0);
		int numberOfColumns = headerRow.getLastCellNum();
		
		for (int col = 0; col < numberOfColumns; col++) {
			Cell oldCell = headerRow.getCell(col);
			Cell newCell = newHeaderRow.createCell(col);
			if (oldCell != null)
				newCell.setCellValue(oldCell.getStringCellValue());
		}
		return numberOfColumns;
	}
	
	//產生Map，分成公司和日期兩種
	public TreeMap<String, List<Row>> mapType(Sheet sheet, String type) {
		TreeMap<String, List<Row>> map = new TreeMap<>();
		int rowindex = 0;
		for (Row row : sheet) {
			if (rowindex == 0) {
				rowindex++; // 跳過第一行
				continue;
			}
			if(type=="company") {
				Cell companyCell = row.getCell(1); // 公司
				if (companyCell != null) {
					String company = companyCell.getStringCellValue();
					map.computeIfAbsent(company, k -> new ArrayList<>()).add(row);
				}
			} else {
				Cell dateCell = row.getCell(0);
				if (dateCell != null && DateUtil.isCellDateFormatted(dateCell)) {
					Date date = dateCell.getDateCellValue();
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
					String dayKey = String.format("%02d", dayOfMonth); // 轉成 "01", "02"...

					map.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(row);
				}
			}
		}
		return map;
	}

	// 日期格式
	public CellStyle dateType(Workbook workbook) {
		CreationHelper createHelper = workbook.getCreationHelper();
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

		return dateCellStyle;
	}

}
