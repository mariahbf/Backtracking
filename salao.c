#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_SIZE 20 // Adjust based on your needs
#define DIRECTIONS 8

typedef struct {
    char board[MAX_SIZE][MAX_SIZE];
    int occupied[MAX_SIZE][MAX_SIZE];
    int n;
} Salao;

int dir[DIRECTIONS][2] = {
    {-1, 0}, {1, 0}, {0, -1}, {0, 1},
    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
};

// Check if a position is within bounds
int isInBounds(int r, int c, int n) {
    return r >= 0 && r < n && c >= 0 && c < n;
}

// Check if placing a gunman of the given gang (B or C) at (row, col) is valid
int isValidPosition(Salao *s, int row, int col, char gang) {
    for (int i = 0; i < DIRECTIONS; i++) {
        int r = row + dir[i][0], c = col + dir[i][1];
        if (isInBounds(r, c, s->n) && s->occupied[r][c] && s->board[r][c] == gang) {
            return 0;
        }
    }

    char rival = (gang == 'B') ? 'C' : 'B';
    for (int i = 0; i < DIRECTIONS; i++) {
        int r = row + dir[i][0], c = col + dir[i][1];
        while (isInBounds(r, c, s->n)) {
            if (s->occupied[r][c] && s->board[r][c] == gang) {
                return 0; // Same gang member in line of sight
            } else if (s->occupied[r][c] && s->board[r][c] == rival) {
                break; // Rival blocks the sight
            }
            r += dir[i][0];
            c += dir[i][1];
        }
    }
    return 1;
}

// Count how many rival gang members the gunman at (row, col) is "aiming" at
int countRivalsAimedAt(Salao *s, int row, int col, char gang) {
    char rival = (gang == 'B') ? 'C' : 'B';
    int aimingCount = 0;

    for (int i = 0; i < DIRECTIONS; i++) {
        int r = row + dir[i][0], c = col + dir[i][1];
        while (isInBounds(r, c, s->n)) {
            if (s->occupied[r][c] && s->board[r][c] == rival) {
                aimingCount++;
                break;
            } else if (s->occupied[r][c]) {
                break; // Blocked by a teammate
            }
            r += dir[i][0];
            c += dir[i][1];
        }
    }
    return aimingCount;
}

// Check the entire board to ensure all gunmen are aiming at at least two rivals
int isValidStandoff(Salao *s) {
    for (int row = 0; row < s->n; row++) {
        for (int col = 0; col < s->n; col++) {
            if (s->occupied[row][col]) {
                char gunman = s->board[row][col];
                if (countRivalsAimedAt(s, row, col, gunman) < 2) {
                    return 0; // Invalid standoff
                }
            }
        }
    }
    return 1; // Valid standoff
}

// Convert the board to a string for easy comparison and storing in a set
void boardToString(Salao *s, char *result) {
    for (int row = 0; row < s->n; row++) {
        for (int col = 0; col < s->n; col++) {
            result[row * s->n + col] = s->board[row][col];
        }
    }
    result[s->n * s->n] = '\0'; // Null-terminate
}

// The main backtracking function
void solve(Salao *s, int bCount, int cCount, int *solutionCounter, char uniqueSolutions[][MAX_SIZE * MAX_SIZE], int *uniqueCount) {
    if (bCount == 0 && cCount == 0) {
        if (isValidStandoff(s)) {
            char boardStr[MAX_SIZE * MAX_SIZE + 1];
            boardToString(s, boardStr);

            // Check for uniqueness
            for (int i = 0; i < *uniqueCount; i++) {
                if (strcmp(uniqueSolutions[i], boardStr) == 0) {
                    return; // Already counted
                }
            }

            // Store the unique solution
            strcpy(uniqueSolutions[*uniqueCount], boardStr);
            (*uniqueCount)++;
            (*solutionCounter)++;
        }
        return;
    }

    for (int row = 0; row < s->n; row++) {
        for (int col = 0; col < s->n; col++) {
            if (!s->occupied[row][col]) {
                // Try placing a gang B member
                if (bCount > 0) {
                    s->board[row][col] = 'B';
                    s->occupied[row][col] = 1;
                    if (isValidPosition(s, row, col, 'B')) {
                        solve(s, bCount - 1, cCount, solutionCounter, uniqueSolutions, uniqueCount);
                    }
                    s->occupied[row][col] = 0; // Backtrack
                }

                // Try placing a gang C member
                if (cCount > 0) {
                    s->board[row][col] = 'C';
                    s->occupied[row][col] = 1;
                    if (isValidPosition(s, row, col, 'C')) {
                        solve(s, bCount, cCount - 1, solutionCounter, uniqueSolutions, uniqueCount);
                    }
                    s->occupied[row][col] = 0; // Backtrack
                }
                s->board[row][col] = '.'; // Clear the cell
            }
        }
    }
}

int main(int argc, char *argv[]) {
    if (argc != 4) {
        printf("Usage: %s <n> <bCount> <cCount>\n", argv[0]);
        return 1;
    }

    int n = atoi(argv[1]);
    int bCount = atoi(argv[2]);
    int cCount = atoi(argv[3]);

    Salao salao;
    salao.n = n;
    memset(salao.board, '.', sizeof(salao.board));
    memset(salao.occupied, 0, sizeof(salao.occupied));

    int solutionCounter = 0;
    char uniqueSolutions[100000][MAX_SIZE * MAX_SIZE + 1]; // Adjust size as needed
    int uniqueCount = 0;

    solve(&salao, bCount, cCount, &solutionCounter, uniqueSolutions, &uniqueCount);

    printf("Total number of unique solutions: %d\n", solutionCounter);
    return 0;
}