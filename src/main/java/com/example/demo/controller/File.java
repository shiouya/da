package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.modal.Order;

@Controller
public class File {

	@PostMapping("/file")
	public String postMethodName(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			InputStream input = file.getInputStream();
			Workbook workbook = new XSSFWorkbook(input);
			Workbook newworkbook = new XSSFWorkbook();
			Sheet sheet = workbook.getSheetAt(0);
			Row headerRow = sheet.getRow(0);
			int numberOfColumns = headerRow.getLastCellNum();

			CreationHelper createHelper = newworkbook.getCreationHelper();
			CellStyle dateCellStyle = newworkbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/MM/dd"));

			ArrayList<Order> orders = new ArrayList<>();
			HashMap<String, List<Row>> companyMap = new HashMap<>();

			// 跳過第一行
			int rowindex = 0;
			for (Row row : sheet) {
				if (rowindex == 0) {
					rowindex++; // 跳過第一行
					continue;
				}

				Cell cell1 = row.getCell(1); // 公司
				if (cell1 != null) {
					String company = cell1.getStringCellValue();
					companyMap.computeIfAbsent(company, k -> new ArrayList<>()).add(row);
				}
			}

			for (Map.Entry<String, List<Row>> entry : companyMap.entrySet()) {
				String companyKey = entry.getKey();
				List<Row> rows = entry.getValue();

				Sheet newsheet = newworkbook.createSheet(companyKey);

				Row newHeaderRow = newsheet.createRow(0);
				for (int col = 0; col < numberOfColumns; col++) {
					Cell oldCell = headerRow.getCell(col);
					Cell newCell = newHeaderRow.createCell(col);
					if (oldCell != null)
						newCell.setCellValue(oldCell.getStringCellValue());
				}

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
									newCell.setCellStyle(dateCellStyle);
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
			String userHome = System.getProperty("user.home");
			String originalFilename = file.getOriginalFilename();
			String filename = userHome + "/Downloads/" + originalFilename;
			FileOutputStream outputStream = new FileOutputStream(filename);
			newworkbook.write(outputStream);
			// 關閉連線
			outputStream.close();
			redirectAttributes.addFlashAttribute("message", "匯出成功！檔案已儲存到：" + filename);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/page/home";
	}

}
