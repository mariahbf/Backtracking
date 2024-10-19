import sys
# Funções auxiliares (já existentes):
DIRECTIONS = [(-1, 0), (1, 0), (0, -1), (0, 1),  # Direções: cima, baixo, esquerda, direita
              (-1, -1), (-1, 1), (1, -1), (1, 1)]  # Direções diagonais

# Função para validar o tabuleiro após todas as peças serem colocadas
def validate_standoff(board):
    N = len(board)
    for i in range(N):
        for j in range(N):
            if board[i][j] in ('B', 'P'):  # Se há um atirador nessa célula
                gang = board[i][j]
                enemies_in_sight = count_enemies_in_sight(board, i, j, gang)
                if enemies_in_sight == -1 or enemies_in_sight < 2:
                    return False
    return True

# Função para contar inimigos na linha de visão
def count_enemies_in_sight(board, i, j, gang):
    N = len(board)
    enemy_gang = 'B' if gang == 'P' else 'P'  # Gangue oposta
    enemies_in_sight = 0
    for dx, dy in DIRECTIONS:
        ni, nj = i + dx, j + dy
        while 0 <= ni < N and 0 <= nj < N:
            if board[ni][nj] == gang:
                return -1  # Atirador da mesma gangue, configuração inválida
            elif board[ni][nj] == enemy_gang:
                enemies_in_sight += 1
                break  # Inimigo encontrado, parar de verificar nesta direção
            ni += dx
            nj += dy
    return enemies_in_sight

# Verifica se a célula está na região simétrica permitida (para tabuleiros pares ou ímpares)
def is_in_valid_region(i, j, N):
    if N % 2 == 0:  # Tabuleiro par: quadrante superior esquerdo
        return i < N // 2 and j < N // 2
    else:  # Tabuleiro ímpar: metade superior
        return i < N // 2 or (i == N // 2 and j < N // 2)

# Função principal de backtracking com restrição para a primeira peça
def count_standoff(board, b_remaining, p_remaining, gang_turn, first_b_placed, first_p_placed):
    N = len(board)
    
    # Caso base: se não há mais peças para colocar, validar a solução
    if b_remaining == 0 and p_remaining == 0:
        return 1 if validate_standoff(board) else 0
    
    count = 0  # Contador de soluções válidas

    for i in range(N):
        for j in range(N):
            if board[i][j] == '.':  # Posição vazia
                
                # Gangue B: Restringe a primeira colocação ao quadrante/metade superior
                if gang_turn == 'B' and b_remaining > 0:
                    if not first_b_placed and not is_in_valid_region(i, j, N):
                        continue  # Ignorar posições fora da região permitida
                    board[i][j] = 'B'  # Coloca o membro da gangue B
                    # Alterna para gangue P
                    count += count_standoff(board, b_remaining - 1, p_remaining, 'P', True, first_p_placed)
                    board[i][j] = '.'  # Backtracking, remove a peça

                # Gangue P: Restringe da mesma forma
                elif gang_turn == 'P' and p_remaining > 0:
                    if not first_p_placed and not is_in_valid_region(i, j, N):
                        continue  # Ignorar posições fora da região permitida
                    board[i][j] = 'P'  # Coloca o membro da gangue P
                    # Alterna para gangue B
                    count += count_standoff(board, b_remaining, p_remaining - 1, 'B', first_b_placed, True)
                    board[i][j] = '.'  # Backtracking, remove a peça

    return count

# Função para iniciar o processo de contagem
def solve_standoff(N, b_remaining, p_remaining):
    # Criar tabuleiro vazio
    board = [['.' for _ in range(N)] for _ in range(N)]
    
    # Executar o backtracking
    total_valid_standoffs = count_standoff(board, b_remaining, p_remaining, 'B', False, False)
    print(f"Total de configurações válidas: {total_valid_standoffs}")
    
    
n = int(sys.argv[1])
b = int(sys.argv[2])
p = int(sys.argv[3])

solve_standoff(n, b, p)