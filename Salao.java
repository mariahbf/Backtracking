public class Salao {

    public int findPossibleCombinations(int n, int b, int c){
        String[][] matriz = new String[n][n];
        
        for(int l = 0; l < n; l++){
            for(int col = 0; col < n; col++){
                matriz[l][col] = "b";
            }
        }

        return 0;
    }

    public boolean isPossible(String[][] matriz, int l, int c, String next){
       if (next == "b"){
           if (matriz[l-1][c] == "b" ||matriz[l-1][c-1]=="b" || matriz[l+1][c] == "b" || matriz[l][c-1] == "b" || matriz[l][c+1] == "b"){
               return false;
           }
       }

        return false;
    }

    public static void main(Integer[] args) {
        
        int n = args[0];
        int b = args[1];
        int c = args[2];

    }

}


// três métodos
    // um método que verifica se não aponta para nenhum amigo
    // um método que verifica se aponta para pelo menos dois inimigos

    
        // se for, chama a função recursivamente para a posição marcada
        // se não, desmarca a posição