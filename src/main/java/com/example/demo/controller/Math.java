package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.SudokuService;

@RestController
public class Math {

	private final SudokuService sudokuService;

	public Math(SudokuService sudokuService) {
		this.sudokuService = sudokuService;
	}

	// 直接接受 int[][]
	@PostMapping("/api/sudoku/solve")
	public ResponseEntity<int[][]> solve(@RequestBody int[][] grid) {
		int[][] solved = sudokuService.solve(grid);
		for (int i = 0; i < solved.length; i++) {
			for (int j = 0; j < solved.length; j++) {
				System.out.print(solved[i][j]);
			}
			System.out.println("");
		}

		return ResponseEntity.ok(solved);
	}

}
