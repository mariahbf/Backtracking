import java.util.HashSet;
import java.util.Set;

public class StandoffSolver {
    private int n;
    private char[][] grid;
    private int bMembersLeft;
    private int pMembersLeft;
    private boolean firstPlacement;
    private Set<String> uniqueConfigs;
    
    public StandoffSolver(int n, int bMembers, int pMembers) {
        this.n = n;
        this.bMembersLeft = bMembers;
        this.pMembersLeft = pMembers;
        this.grid = new char[n][n];
        this.uniqueConfigs = new HashSet<>();
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                grid[i][j] = '.';
            }
        }
    }
    
    public int solve() {
        // Try both strategies with shared uniqueConfigs set
        firstPlacement = true;
        backtrackInterleaved(0, 0);
        
        // Reset grid and counters for sequential strategy
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                grid[i][j] = '.';
            }
        }
        bMembersLeft = n;
        pMembersLeft = n;
        firstPlacement = true;
        
        backtrackSequential(0, 0, true);
        
        // Print unique configurations for debugging
        System.out.println("Unique configurations found: " + uniqueConfigs.size());
        for (String config : uniqueConfigs) {
            System.out.println(config);
        }
        
        return uniqueConfigs.size() * 8;
    }

    private String gridToString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                sb.append(grid[i][j]);
            }
        }
        return sb.toString();
    }

    // Função para gerar todas as rotações e reflexões da configuração
    private String[] generateSymmetries(String config) {
        char[][] matrix = new char[n][n];
        int idx = 0;
        
        // Convert string to matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = config.charAt(idx++);
            }
        }
        
        String[] symmetries = new String[8];
        symmetries[0] = config;                        // Original
        symmetries[1] = rotate90(matrix);              // Rotated 90°
        symmetries[2] = rotate90(matrix);              // Rotated 180°
        symmetries[3] = rotate90(matrix);              // Rotated 270°
        symmetries[4] = reflectVertical(matrix);       // Reflect Vertically
        symmetries[5] = rotate90(matrix);              // Rotated 90° + reflect
        symmetries[6] = rotate90(matrix);              // Rotated 180° + reflect
        symmetries[7] = rotate90(matrix);              // Rotated 270° + reflect
        
        return symmetries;
    }

    // Funções de rotação e reflexão
    private String rotate90(char[][] matrix) {
        char[][] rotated = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                rotated[j][n - i - 1] = matrix[i][j];
            }
        }
        return matrixToString(rotated);
    }
    
    private String reflectVertical(char[][] matrix) {
        char[][] reflected = new char[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                reflected[i][n - j - 1] = matrix[i][j];
            }
        }
        return matrixToString(reflected);
    }
    
    private String matrixToString(char[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(matrix[i][j]);
            }
        }
        return sb.toString();
    }
    
    private void addUniqueConfig(String config) {
        String[] symmetries = generateSymmetries(config);
        String smallest = symmetries[0];
        
        for (String sym : symmetries) {
            if (sym.compareTo(smallest) < 0) {
                smallest = sym;
            }
        }
        
        uniqueConfigs.add(smallest);  // Armazena apenas a menor versão lexicográfica
    }
    
    private void backtrackInterleaved(int row, int col) {
        if (bMembersLeft == 0 && pMembersLeft == 0) {
            if (isValidConfiguration()) {
                addUniqueConfig(gridToString());
            }
            return;
        }
        
        if (row == n) return;
        
        int nextRow = (col == n-1) ? row + 1 : row;
        int nextCol = (col == n-1) ? 0 : col + 1;
        
        // Special handling for first placement
        if (firstPlacement) {
            firstPlacement = false;
            if (row == 0 && col <= 1) {
                if (bMembersLeft > 0) {
                    grid[row][col] = 'B';
                    bMembersLeft--;
                    backtrackInterleaved(nextRow, nextCol);
                    grid[row][col] = '.';
                    bMembersLeft++;
                }
            }
            if (col < n-1) {
                backtrackInterleaved(nextRow, nextCol);
            }
            return;
        }
        
        // Try placing nothing
        backtrackInterleaved(nextRow, nextCol);
        
        // Interleaved placement - try placing from the gang with more members
        if (bMembersLeft > 0 && bMembersLeft >= pMembersLeft) {
            grid[row][col] = 'B';
            bMembersLeft--;
            backtrackInterleaved(nextRow, nextCol);
            grid[row][col] = '.';
            bMembersLeft++;
        }
        
        if (pMembersLeft > 0 && pMembersLeft >= bMembersLeft) {
            grid[row][col] = 'P';
            pMembersLeft--;
            backtrackInterleaved(nextRow, nextCol);
            grid[row][col] = '.';
            pMembersLeft++;
        }
    }
    
    private void backtrackSequential(int row, int col, boolean placingB) {
        if (bMembersLeft == 0 && pMembersLeft == 0) {
            if (isValidConfiguration()) {
                addUniqueConfig(gridToString());
            }
            return;
        }
        
        if (row == n) return;
        
        int nextRow = (col == n-1) ? row + 1 : row;
        int nextCol = (col == n-1) ? 0 : col + 1;
        
        // Special handling for first placement
        if (firstPlacement) {
            firstPlacement = false;
            if (row == 0 && col <= 1) {
                grid[row][col] = 'B';
                bMembersLeft--;
                backtrackSequential(nextRow, nextCol, true);
                grid[row][col] = '.';
                bMembersLeft++;
            }
            if (col < n-1) {
                backtrackSequential(nextRow, nextCol, true);
            }
            return;
        }
        
        // Try placing nothing
        backtrackSequential(nextRow, nextCol, placingB);
        
        // Sequential placement - place all B first, then all P
        if (placingB && bMembersLeft > 0) {
            grid[row][col] = 'B';
            bMembersLeft--;
            backtrackSequential(nextRow, nextCol, bMembersLeft > 0);
            grid[row][col] = '.';
            bMembersLeft++;
        } else if (!placingB && pMembersLeft > 0) {
            grid[row][col] = 'P';
            pMembersLeft--;
            backtrackSequential(nextRow, nextCol, false);
            grid[row][col] = '.';
            pMembersLeft++;
        }
    }
    
    private boolean isValidConfiguration() {
        // Check each position in the grid
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                char current = grid[row][col];
                if (current == 'B') {
                    if (row > 0 && grid[row-1][col] == 'B') return false;
                    if (col > 0 && grid[row][col-1] == 'B') return false;
                } else if (current == 'P') {
                    if (row > 0 && grid[row-1][col] == 'P') return false;
                    if (col > 0 && grid[row][col-1] == 'P') return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        StandoffSolver solver = new StandoffSolver(3, 2, 2);
        System.out.println("Number of possible configurations: " + solver.solve());
    }
}
