package model;

public class Partida {
    
	private String nome;
    private Elemento[][] tabuleiro;
    private Jogador jogadorXis;
    private Jogador jogadorBolinha;
    private Elemento vez;
    
    public Partida(String nomeJogadorXis, String nomeJogadorBolinha) {
        tabuleiro = new Elemento[3][3];
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                tabuleiro[i][j] = Elemento.VAZIO;
        jogadorXis = new Jogador(nomeJogadorXis, Elemento.XIS);
        jogadorBolinha = new Jogador(nomeJogadorBolinha, Elemento.BOLINHA);
        vez = Elemento.XIS;
    }
    
    public boolean jogar(int linha, int coluna) {
        if(linha<0 || linha>2 || coluna<0 || coluna>2)
            return false;
        if(tabuleiro[linha][coluna] == Elemento.VAZIO) {
            tabuleiro[linha][coluna] = vez;
            trocaVez();
            return true;
        }
        return false;
    }
    
    private void trocaVez() {
        if(vez == Elemento.XIS)
            vez = Elemento.BOLINHA;
        else
            vez = Elemento.XIS;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                stringBuilder.append(tabuleiro[i][j].getRepresentacao()).append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    
}
