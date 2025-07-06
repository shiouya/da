package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.service.FileService;

@Controller
public class File {

	@Autowired
	private FileService fileService;

	@PostMapping("/file")
	public String postMethodName(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		String originalFilename = file.getOriginalFilename();
		if (originalFilename == null || !(originalFilename.endsWith(".xlsx") || originalFilename.endsWith(".xls"))) {
			redirectAttributes.addFlashAttribute("message", "上傳失敗：請上傳 Excel 檔案 (.xlsx 或 .xls)");
			return "redirect:/page/home";
		}

		try (InputStream input = file.getInputStream();
				Workbook workbook = new XSSFWorkbook(input);) {

			Workbook companyworkbook = fileService.file(workbook, "company");
			Workbook dateworkbook = fileService.file(workbook, "date");

			String userHome = System.getProperty("user.home");
			String companyFilename = userHome + "/Downloads/公司-" + originalFilename;
			FileOutputStream companyoutputStream = new FileOutputStream(companyFilename);
			String dateFilename = userHome + "/Downloads/日期-" + originalFilename;
			FileOutputStream dateoutputStream = new FileOutputStream(dateFilename);
			companyworkbook.write(companyoutputStream);
			dateworkbook.write(dateoutputStream);
			// 關閉連線
			companyoutputStream.close();
			dateoutputStream.close();
			redirectAttributes.addFlashAttribute("message", "匯出成功！檔案已儲存到：" + companyFilename + " 和 " + dateFilename);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/page/home";
	}

}
