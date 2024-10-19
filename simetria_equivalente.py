import sys
import copy

DIRECTIONS = [(-1, 0), (1, 0), (0, -1), (0, 1),  # Up, Down, Left, Right
              (-1, -1), (-1, 1), (1, -1), (1, 1)]  # Diagonals


def rotate_board(board):
    return [list(row) for row in zip(*board[::-1])]

def reflect_board_vertical(board):
    return [row[::-1] for row in board]

def reflect_board_horizontal(board):
    return board[::-1]

def generate_symmetries(board):
    symmetries = []
    current_board = board
    
    # Add all 4 rotations
    for _ in range(4):
        current_board = rotate_board(current_board)
        symmetries.append(copy.deepcopy(current_board))
    
    # Reflect and add all 4 rotations of the reflected board (vertical)
    reflected_board = reflect_board_vertical(board)
    current_board = reflected_board
    for _ in range(4):
        current_board = rotate_board(current_board)
        symmetries.append(copy.deepcopy(current_board))
    
    return symmetries


def is_unique(board, seen_configurations):
    # Generate all symmetrical variants (rotations and reflections)
    symmetries = generate_symmetries(board)
    
    # Check if any of the symmetries is in the set of already seen configurations
    for sym in symmetries:
        if tuple(map(tuple, sym)) in seen_configurations:
            return False  # If any symmetry matches, it's not unique
    
    return True  # No matches found, so it's unique


def is_valid_position(x, y, n):
    return 0 <= x < n and 0 <= y < n


def has_same_gang_neighbor(board, i, j, gang):
    for dx, dy in DIRECTIONS:
        ni, nj = i + dx, j + dy
        if is_valid_position(ni, nj, len(board)) and board[ni][nj] == gang:
            return True
    return False


def count_enemies_in_sight(board, i, j, gang):
    n = len(board)
    enemy_gang = 'B' if gang == 'P' else 'P'
    enemies_in_sight = 0

    for dx, dy in DIRECTIONS:
        ni, nj = i + dx, j + dy

        while 0 <= ni < n and 0 <= nj < n:
            if board[ni][nj] == gang:
                # If we hit a shooter from the same gang, it's invalid
                return -1
            elif board[ni][nj] == enemy_gang:
                enemies_in_sight += 1
                break  # Stop once we've found an enemy in this direction

            # Move further in the current direction
            ni += dx
            nj += dy

    return enemies_in_sight



def place_shooters(board, b_gang, p_gang, b_remaining, p_remaining, gang_turn):
    n = len(board)
    
    if b_remaining == 0 and p_remaining == 0:
        return validate_standoff(board)
    
    for i in range(n):
        for j in range(n):
            if board[i][j] == '.':  # Empty spot
                
                if gang_turn == 'B' and b_remaining > 0:
                    if not has_same_gang_neighbor(board, i, j, 'B'):
                        board[i][j] = 'B'
                        if place_shooters(board, b_gang, p_gang, b_remaining - 1, p_remaining, 'P'):
                            return True  # A valid configuration was found
                        board[i][j] = '.'  # Backtrack
                        
                elif gang_turn == 'P' and p_remaining > 0:
                    if not has_same_gang_neighbor(board, i, j, 'P'):
                        board[i][j] = 'P'
                        if place_shooters(board, b_gang, p_gang, b_remaining, p_remaining - 1, 'B'):
                            return True  # A valid configuration was found
                        board[i][j] = '.'  # Backtrack

    return False  # No valid configuration found

def validate_standoff(board):
    n = len(board)
    
    for i in range(n):
        for j in range(n):
            if board[i][j] in ('B', 'P'):  # If there's a shooter in this cell
                gang = board[i][j]
                
                enemies_in_sight = count_enemies_in_sight(board, i, j, gang)
                
                if enemies_in_sight == -1 or enemies_in_sight < 2:
                    return False

    return True

def count_standoff_with_symmetry(board, b_remaining, p_remaining, gang_turn, seen_configurations):
    n = len(board)
    
    # Base case: If no more shooters to place, validate the configuration
    if b_remaining == 0 and p_remaining == 0:
        # Validate the standoff configuration
        if validate_standoff(board):
            # Check if the configuration is unique (not a symmetrical equivalent)
            if is_unique(board, seen_configurations):
                # Add all symmetries of this configuration to the seen set
                symmetries = generate_symmetries(board)
                for sym in symmetries:
                    seen_configurations.add(tuple(map(tuple, sym)))
                
                # Each unique configuration has 8 symmetrical variants
                return 8  # Count the original and its 7 transformations
        return 0

    # Initialize counter for valid configurations
    count = 0

    # Try placing shooters in each cell of the grid
    for i in range(n):
        for j in range(n):
            if board[i][j] == '.':  # Empty spot
                
                if gang_turn == 'B' and b_remaining > 0:
                    # Check if the position satisfies the neighbor rule for gang B
                    if not has_same_gang_neighbor(board, i, j, 'B'):
                        board[i][j] = 'B'  # Place a gang B member
                        # Recurse to place the next shooter
                        count += count_standoff_with_symmetry(board, b_remaining - 1, p_remaining, 'P', seen_configurations)
                        board[i][j] = '.'  # Backtrack
                        
                elif gang_turn == 'P' and p_remaining > 0:
                    # Check if the position satisfies the neighbor rule for gang P
                    if not has_same_gang_neighbor(board, i, j, 'P'):
                        board[i][j] = 'P'  # Place a gang P member
                        # Recurse to place the next shooter
                        count += count_standoff_with_symmetry(board, b_remaining, p_remaining - 1, 'B', seen_configurations)
                        board[i][j] = '.'  # Backtrack

    return count  # Return the total number of valid configurations






n = int(sys.argv[1])
board = [['.' for _ in range(n)] for _ in range(n)]
b_remaining = int(sys.argv[2])
p_remaining = int(sys.argv[3])


seen_configurations = set()

total_valid_standoffs = count_standoff_with_symmetry(board, b_remaining, p_remaining, 'B', seen_configurations)


print(total_valid_standoffs)