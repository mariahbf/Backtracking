def is_valid_position(board, row, col, gang):
    # Check if placing a gunman of the given gang (B or C) at (row, col) is valid
    # 1. Ensure no same gang member is adjacent (including diagonals)
    directions = [(-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (-1, 1), (1, -1), (1, 1)]
    for dr, dc in directions:
        r, c = row + dr, col + dc
        if 0 <= r < len(board) and 0 <= c < len(board) and board[r][c] == gang:
            return False
    
    # 2. Ensure no gunman of the same gang is in line of sight (rows, columns, diagonals)
    rival = 'B' if gang == 'C' else 'C'
    
    # Check for line of sight in straight lines
    lines = [(-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (-1, 1), (1, -1), (1, 1)]
    for dr, dc in lines:
        r, c = row + dr, col + dc
        while 0 <= r < len(board) and 0 <= c < len(board):
            if board[r][c] == gang:
                # If we see the same gang member without seeing a rival, it's invalid
                return False
            elif board[r][c] == rival:
                break  # Rival is in between, so it's okay
            r, c = r + dr, c + dc
    
    return True

def count_rivals_aimed_at(board, row, col, gang):
    # Count how many rival gang members the gunman at (row, col) is "aiming" at
    rival = 'B' if gang == 'C' else 'C'
    aiming_count = 0
    
    # Check for rivals in straight lines (left-right, up-down, diagonals)
    lines = [(-1, 0), (1, 0), (0, -1), (0, 1), (-1, -1), (-1, 1), (1, -1), (1, 1)]
    for dr, dc in lines:
        r, c = row + dr, col + dc
        while 0 <= r < len(board) and 0 <= c < len(board):
            if board[r][c] == rival:
                aiming_count += 1
                break
            elif board[r][c] != '.':
                break  # Blocked by a teammate
            r, c = r + dr, c + dc
    return aiming_count

def is_valid_standoff(board):
    # Check the entire board to ensure all gunmen are aiming at at least two rivals
    for row in range(len(board)):
        for col in range(len(board)):
            if board[row][col] in ('B', 'C'):
                gunman = board[row][col]
                if count_rivals_aimed_at(board, row, col, gunman) < 2:
                    return False
    return True

def board_to_string(board):
    # Convert the board to a string for easy comparison in a set
    return '\n'.join(''.join(row) for row in board)

def solve(board, b_count, c_count, solution_counter, unique_solutions):
    # If no more members to place, we check if the full configuration is valid
    if b_count == 0 and c_count == 0:
        if is_valid_standoff(board):
            # Convert the board to a string representation
            board_str = board_to_string(board)
            
            # Only count the solution if it's unique (not already in the set)
            if board_str not in unique_solutions:
                unique_solutions.add(board_str)  # Add the unique solution to the set
                solution_counter[0] += 1  # Increment the solution counter
                
                # Print the valid and unique configuration
                #for row in board:
                    #print(' '.join(row))
                #print()  # Print a blank line between solutions
        return
    
    # Try to place gang members in each empty cell
    for row in range(len(board)):
        for col in range(len(board)):
            if board[row][col] == '.':
                # Try placing a gang B member
                if b_count > 0:
                    board[row][col] = 'B'
                    if is_valid_position(board, row, col, 'B'):
                        solve(board, b_count - 1, c_count, solution_counter, unique_solutions)
                    board[row][col] = '.'  # Backtrack

                # Try placing a gang C member
                if c_count > 0:
                    board[row][col] = 'C'
                    if is_valid_position(board, row, col, 'C'):
                        solve(board, b_count, c_count - 1, solution_counter, unique_solutions)
                    board[row][col] = '.'  # Backtrack

# Initialize the board (example: 3x3 board)
n = 5
board = [['.' for _ in range(n)] for _ in range(n)]

# Number of gunmen from each gang
b_count = 3  # Number of gang B members
c_count = 4  # Number of gang C members

# Initialize the solution counter as a list so it can be modified inside the recursive function
solution_counter = [0]

# Set to store unique solutions
unique_solutions = set()

# Start the recursive solving process
solve(board, b_count, c_count, solution_counter, unique_solutions)

# Print the total number of unique solutions at the end
print(f"Total number of unique solutions: {solution_counter[0]}")
