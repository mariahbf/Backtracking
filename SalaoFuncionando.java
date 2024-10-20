
import java.util.HashSet;
import java.util.Set;
import java.io.*;

public class SalaoFuncionando {

    // Directions for adjacent and sight checks
    private static final int[][] DIRECTIONS = { 
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}, 
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1} 
    };

    // Check if placing a gunman of the given gang (B or C) at (row, col) is valid
    public static boolean isValidPosition(char[][] board, int row, int col, char gang) {
        // Check adjacent positions for the same gang member
        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0], c = col + dir[1];
            if (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == gang) {
                return false;
            }
        }

        // Check lines of sight for the same gang member
        char rival = (gang == 'B') ? 'C' : 'B';

        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
                if (board[r][c] == gang) {
                    return false; // Same gang member in line of sight
                } else if (board[r][c] == rival) {
                    break; // Rival blocks the sight
                }
                r += dir[0];
                c += dir[1];
            }
        }

        return true;
    }

    // Count how many rival gang members the gunman at (row, col) is "aiming" at
    public static int countRivalsAimedAt(char[][] board, int row, int col, char gang) {
        char rival = (gang == 'B') ? 'C' : 'B';
        int aimingCount = 0;

        for (int[] dir : DIRECTIONS) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
                if (board[r][c] == rival) {
                    aimingCount++;
                    break;
                } else if (board[r][c] != '.') {
                    break; // Blocked by a teammate
                }
                r += dir[0];
                c += dir[1];
            }
        }
        return aimingCount;
    }

    // Check the entire board to ensure all gunmen are aiming at at least two rivals
    public static boolean isValidStandoff(char[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == 'B' || board[row][col] == 'C') {
                    char gunman = board[row][col];
                    if (countRivalsAimedAt(board, row, col, gunman) < 2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Convert the board to a string for easy comparison and storing in a set
    public static String boardToString(char[][] board) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }

    // The main backtracking function
    public static void backtracking(char[][] board, int bCount, int cCount, Set<String> uniqueSolutions) {
        // If no more members to place, we check if the full configuration is valid
        if (bCount == 0 && cCount == 0) {
            if (isValidStandoff(board)) {
                // Convert the board to a string representation
                String boardStr = boardToString(board);
                // Only count the solution if it's unique
                if (uniqueSolutions.add(boardStr)) { // Adds and checks for uniqueness
                    // Uncomment to print the valid and unique configuration
                    // System.out.println(boardStr);
                }
            }
            return;
        }

        // Try to place gang members in each empty cell
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == '.') {
                    // Try placing a gang B member
                    if (bCount > 0) {
                        board[row][col] = 'B';
                        if (isValidPosition(board, row, col, 'B')) {
                            backtracking(board, bCount - 1, cCount, uniqueSolutions);
                        }
                        board[row][col] = '.';  // Backtrack
                    }

                    // Try placing a gang C member
                    if (cCount > 0) {
                        board[row][col] = 'C';
                        if (isValidPosition(board, row, col, 'C')) {
                            backtracking(board, bCount, cCount - 1, uniqueSolutions);
                        }
                        board[row][col] = '.';  // Backtrack
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int n = Integer.parseInt(args[0]);
        int bCount = Integer.parseInt(args[1]);
        int cCount = Integer.parseInt(args[2]);
        
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }

        // Set to store unique solutions
        Set<String> uniqueSolutions = new HashSet<>();

        // Start the recursive solving process
        backtracking(board, bCount, cCount, uniqueSolutions);
        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1000000;

        // Print the total number of unique solutions and execution time at the end
        System.out.println("Total number of unique solutions: " + uniqueSolutions.size());
        System.out.println("Execution time: " + executionTime + "ms");
    }
}
