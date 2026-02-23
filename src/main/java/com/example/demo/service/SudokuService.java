package com.example.demo.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SudokuService {

	public int[][] solve(int[][] grid) {
		validateInput(grid);
		int[][] copy = deepCopy(grid);

		if (!solveBacktrack(copy)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無解的數獨或輸入衝突");
		}
		return copy;
	}

	private void validateInput(int[][] grid) {
		if (grid == null || grid.length != 9) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "grid 必須是 9x9");
		}
		for (int i = 0; i < 9; i++) {
			if (grid[i].length != 9) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "grid 必須是 9x9");
			}
			for (int j = 0; j < 9; j++) {
				int v = grid[i][j];
				if (v < 0 || v > 9) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "格子值必須介於 0 到 9");
				}
				if (v != 0 && !isValidPlacement(grid, i, j, v)) {
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							String.format("初始棋盤在 (%d,%d) 衝突 (%d)", i, j, v));
				}
			}
		}
	}

	private int[][] deepCopy(int[][] grid) {
		int[][] copy = new int[9][9];
		for (int i = 0; i < 9; i++) {
			System.arraycopy(grid[i], 0, copy[i], 0, 9);
		}
		return copy;
	}

	// Backtracking solver
	private boolean solveBacktrack(int[][] board) {
		int[] pos = findEmpty(board);
		if (pos == null)
			return true;

		int row = pos[0];
		int col = pos[1];

		for (int num = 1; num <= 9; num++) {
			if (isValid(board, row, col, num)) {
				board[row][col] = num;
				if (solveBacktrack(board))
					return true;
				board[row][col] = 0;
			}
		}
		return false;
	}

	private int[] findEmpty(int[][] board) {
		for (int r = 0; r < 9; r++)
			for (int c = 0; c < 9; c++)
				if (board[r][c] == 0)
					return new int[] { r, c };
		return null;
	}

	private boolean isValid(int[][] board, int row, int col, int num) {
		// row & col
		for (int i = 0; i < 9; i++) {
			if (board[row][i] == num)
				return false;
			if (board[i][col] == num)
				return false;
		}
		// block
		int br = (row / 3) * 3, bc = (col / 3) * 3;
		for (int r = br; r < br + 3; r++)
			for (int c = bc; c < bc + 3; c++)
				if (board[r][c] == num)
					return false;

		return true;
	}

	private boolean isValidPlacement(int[][] grid, int row, int col, int num) {
		for (int i = 0; i < 9; i++) {
			if (i != col && grid[row][i] == num)
				return false;
			if (i != row && grid[i][col] == num)
				return false;
		}
		int br = (row / 3) * 3, bc = (col / 3) * 3;
		for (int r = br; r < br + 3; r++)
			for (int c = bc; c < bc + 3; c++)
				if ((r != row || c != col) && grid[r][c] == num)
					return false;

		return true;
	}

}
