import java.util.HashSet;
import java.util.Set;

public class Salao {

    // Directions used to check the 8 surrounding positions
    private static final int[][] DIRECTIONS = { 
        {-1, 0}, {1, 0}, {0, -1}, {0, 1}, 
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1} 
    };

    // Verifies if a position is valid for placing a gang member
    public static boolean isPositionValid(String[][] board, int row, int column, String gangMember) {
        // Check surrounding cells for the same gang member
        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0], newColumn = column + direction[1];
            if (newRow >= 0 && newRow < board.length && newColumn >= 0 && newColumn < board[0].length 
                && board[newRow][newColumn] == gangMember) {
                return false;
            }
        }

        // Check line of sight for gang members
        String enemyGang = (gangMember.equals("Bigode")) ? "Capeta" : "Bigode";

        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0], newColumn = column + direction[1];
            while (newRow >= 0 && newRow < board.length && newColumn >= 0 && newColumn < board[0].length) {
                if (board[newRow][newColumn] == gangMember) {
                    return false; // Same gang member in line of sight
                } else if (board[newRow][newColumn] == enemyGang) {
                    break; // Enemy blocks the sight
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
        return true;
    }

    // Counts how many enemies are in the line of sight of the gunman
    public static int countEnemiesInSight(String[][] board, int row, int column, String gunman) {
        String enemyGang = (gunman == "Bigode") ? "Capeta" : "Bigode";
        int enemiesInSight = 0;

        for (int[] direction : DIRECTIONS) {
            int newRow = row + direction[0], newColumn = column + direction[1];
            while (newRow >= 0 && newRow < board.length && newColumn >= 0 && newColumn < board[0].length) {
                if (board[newRow][newColumn] == enemyGang) {
                    enemiesInSight++;
                    break;
                } else if (board[newRow][newColumn] != ".") {
                    break; // Blocked by another member
                }
                newRow += direction[0];
                newColumn += direction[1];
            }
        }
        return enemiesInSight;
    }

    // Check if every gunman on the board has at least two enemies in sight
    public static boolean isValidConfiguration(String[][] board) {
        for (int row = 0; row < board.length; row++) {
            for (int column = 0; column < board[0].length; column++) {
                if (board[row][column] == "Bigode" || board[row][column] == "Capeta") {
                    String gunman = board[row][column];
                    if (countEnemiesInSight(board, row, column, gunman) < 2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // Converts the current state of the board into a string
    public static String convertBoardToString(String[][] board) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : board) {
            sb.append(String.join("", row)).append("\n");
        }
        return sb.toString();
    }

    // Backtracking to find all valid configurations
    public static void solve(String[][] board, int remainingB, int remainingC, Set<String> solutions, int startRow, int startcolumn) {
        // Base case: no more gang members to place
        if (remainingB == 0 && remainingC == 0) {
            if (isValidConfiguration(board)) {
                solutions.add(convertBoardToString(board)); // Store unique solution
            }
            return;
        }

        // Try placing gang members in available spots
        for (int row = startRow; row < board.length; row++) {
            for (int column = (row == startRow ? startcolumn : 0); column < board[0].length; column++) {
                if (board[row][column] == ".") {
                    // Place a member of gang B
                    if (remainingB > 0) {
                        board[row][column] = "Bigode";
                        if (isPositionValid(board, row, column, "Bigode")) {
                            solve(board, remainingB - 1, remainingC, solutions, row, column + 1);
                        }
                        board[row][column] = ".";  // Backtracking
                    }

                    // Place a member of gang C
                    if (remainingC > 0) {
                        board[row][column] = "Capeta";
                        if (isPositionValid(board, row, column, "Capeta")) {
                            solve(board, remainingB, remainingC - 1, solutions, row, column + 1);
                        }
                        board[row][column] = ".";  // Backtracking
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        int n = Integer.parseInt(args[0]);
        int gangBCount = Integer.parseInt(args[1]);
        int gangCCount = Integer.parseInt(args[2]);
        
        String[][] board = new String[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = ".";
            }
        }

        // Set to hold unique solutions
        Set<String> uniqueSolutions = new HashSet<>();

        // Start solving with backtracking
        solve(board, gangBCount, gangCCount, uniqueSolutions, 0, 0);
        long endTime = System.nanoTime();
        long executionTime = (endTime - startTime) / 1000000;

        // Output the results
        System.out.println("Number of possible solutions found: " + uniqueSolutions.size());
        System.out.println("Execution time: " + executionTime + "ms");
    }
}
