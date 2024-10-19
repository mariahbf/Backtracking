import java.util.HashSet;
import java.util.Set;

public class salao2 {

    // Check if placing a gunman of the given gang (B or C) at (row, col) is valid
    public static boolean isValidPosition(char[][] board, int row, int col, char gang){
        // 1. Ensure no same gang member is adjacent (including diagonals)
        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            if (r >= 0 && r < board.length && c >= 0 && c < board[0].length && board[r][c] == gang) {
                return false;
            }
        }

        // 2. Ensure no gunman of the same gang is in line of sight (rows, columns, diagonals)
        char rival = (gang == 'B') ? 'C' : 'B';

        for (int[] dir : directions) {
            int r = row + dir[0], c = col + dir[1];
            while (r >= 0 && r < board.length && c >= 0 && c < board[0].length) {
                if (board[r][c] == gang) {
                    return false; // Same gang member in line of sight without a rival
                } else if (board[r][c] == rival) {
                    break; // Rival blocks the sight, valid
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

        int[][] directions = { {-1, 0}, {1, 0}, {0, -1}, {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1} };
        for (int[] dir : directions) {
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
    public static void solve(char[][] board, int bCount, int cCount, int[] solutionCounter, Set<String> uniqueSolutions) {
        // If no more members to place, we check if the full configuration is valid
        if (bCount == 0 && cCount == 0) {
            if (isValidStandoff(board)) {
                // Convert the board to a string representation
                String boardStr = boardToString(board);
                // Only count the solution if it's unique (not already in the set)
                if (!uniqueSolutions.contains(boardStr)) {
                    uniqueSolutions.add(boardStr); // Add the unique solution to the set
                    solutionCounter[0]++;  // Increment the solution counter

                    // Print the valid and unique configuration
                    //System.out.println(boardStr);
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
                            solve(board, bCount - 1, cCount, solutionCounter, uniqueSolutions);
                        }
                        board[row][col] = '.';  // Backtrack
                    }

                    // Try placing a gang C member
                    if (cCount > 0) {
                        board[row][col] = 'C';
                        if (isValidPosition(board, row, col, 'C')) {
                            solve(board, bCount, cCount - 1, solutionCounter, uniqueSolutions);
                        }
                        board[row][col] = '.';  // Backtrack
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);
        int bCount = Integer.parseInt(args[1]);
        int cCount = Integer.parseInt(args[2]);
        
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = '.';
            }
        }

        // Initialize the solution counter
        int[] solutionCounter = {0};

        // Set to store unique solutions
        Set<String> uniqueSolutions = new HashSet<>();

        // Start the recursive solving process
        solve(board, bCount, cCount, solutionCounter, uniqueSolutions);

        // Print the total number of unique solutions at the end
        System.out.println("Total number of unique solutions: " + solutionCounter[0]);
    }
}
