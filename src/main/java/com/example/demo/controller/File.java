package com.example.demo.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.FileService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class File {

	private static String separator;
	@Autowired
	private FileService fileService;

//	@PostMapping("/file")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
		try {
			// 處理檔案，例如生成新 Excel
			String tmpDir = System.getProperty("java.io.tmpdir");
			String processedFilename = UUID.randomUUID() + "-processed.xlsx";
			String fullPath = tmpDir + File.separator + processedFilename;

			// 模擬生成新檔
			try (InputStream input = file.getInputStream(); Workbook workbook = new XSSFWorkbook(input);) {
				Workbook companyworkbook = fileService.file(workbook, "company");
				FileOutputStream fos = new FileOutputStream(fullPath);
				companyworkbook.write(fos);
			}

			// 把檔名傳給前端（或用 session、隱藏欄位）
			model.addAttribute("message", "檔案處理成功！");
			model.addAttribute("filename", processedFilename);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("message", "檔案處理失敗：" + e.getMessage());
		}
		return "home"; // thymeleaf 頁面
	}

//	@GetMapping("/download/{filename}")
//	public void downloadFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
//		String tmpDir = System.getProperty("java.io.tmpdir");
//		File file = new File(tmpDir, filename);
//
//		if (file.exists()) {
//			String encodedFilename = URLEncoder.encode("處理好的檔案.xlsx", StandardCharsets.UTF_8.toString())
//					.replaceAll("\\+", "%20");
//			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
//
//			try (FileInputStream fis = new FileInputStream(file); OutputStream os = response.getOutputStream()) {
//				byte[] buffer = new byte[8192];
//				int len;
//				while ((len = fis.read(buffer)) != -1) {
//					os.write(buffer, 0, len);
//				}
//			}
//
//			// 可選：下載完就刪檔
//			// file.delete();
//		} else {
//			response.sendError(404, "File not found");
//		}
//	}

	@PostMapping("/file")
	public void postMethodName(@RequestParam("file") MultipartFile file, @RequestParam("type") String type,
			HttpServletResponse response) {
		String originalFilename = file.getOriginalFilename();

		try (InputStream input = file.getInputStream();
				Workbook workbook = new XSSFWorkbook(input);) {

			Workbook newworkbook = fileService.file(workbook, type);
			String filename = type + originalFilename;
			String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()).replaceAll("\\+",
					"%20");

			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);
			newworkbook.write(response.getOutputStream());
			newworkbook.close();

			response.getOutputStream().flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
