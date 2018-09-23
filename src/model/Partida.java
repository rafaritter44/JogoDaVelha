package model;

import java.util.Optional;

public class Partida {

    private Elemento[][] tabuleiro;
    private Jogador jogadorXis;
    private Jogador jogadorBolinha;
    private Elemento vez;
    private static final String ENUMERACAO_DO_TABULEIRO = "1 2 3\n4 5 6\n7 8 9";
    
    public Partida() {
        tabuleiro = new Elemento[3][3];
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                tabuleiro[i][j] = Elemento.VAZIO;
        jogadorXis = new Jogador("Jogador XIS", Elemento.XIS);
        jogadorBolinha = new Jogador("Jogador BOLINHA", Elemento.BOLINHA);
        vez = Elemento.XIS;
    }
    
    public Optional<String> vencedor() {
    	if(venceu(Elemento.XIS))
    		return Optional.ofNullable(jogadorXis.getNome());
    	if(venceu(Elemento.BOLINHA))
    		return Optional.ofNullable(jogadorBolinha.getNome());
    	if(velha())
    		return Optional.ofNullable("Ninguem! Deu velha");
    	return Optional.empty();
    }
    
    private boolean velha() {
    	for(int i=0; i<3; i++)
    		for(int j=0; j<3; j++)
    			if(tabuleiro[i][j] == Elemento.VAZIO)
    				return false;
    	return true;
    }
    
    private boolean venceu(Elemento elemento) {
    	return (tabuleiro[0][0] == elemento && tabuleiro[1][1] == elemento && tabuleiro[2][2] == elemento) ||
    			(tabuleiro[0][2] == elemento && tabuleiro[1][1] == elemento && tabuleiro[2][0] == elemento) ||
    			(tabuleiro[0][0] == elemento && tabuleiro[0][1] == elemento && tabuleiro[0][2] == elemento) ||
    			(tabuleiro[0][0] == elemento && tabuleiro[1][0] == elemento && tabuleiro[2][0] == elemento) ||
    			(tabuleiro[0][1] == elemento && tabuleiro[1][1] == elemento && tabuleiro[2][1] == elemento) ||
    			(tabuleiro[0][2] == elemento && tabuleiro[1][2] == elemento && tabuleiro[2][2] == elemento) ||
    			(tabuleiro[1][0] == elemento && tabuleiro[1][1] == elemento && tabuleiro[1][2] == elemento) ||
    			(tabuleiro[2][0] == elemento && tabuleiro[2][1] == elemento && tabuleiro[2][2] == elemento);
    }
    
    public boolean jogar(String jogada) {
    	int posicao;
    	try {
    		posicao = Integer.parseInt(jogada);
    	} catch(NumberFormatException excecao) {
    		return false;
    	}
    	switch(posicao) {
    	case 1: return jogar(0,0);
    	case 2: return jogar(0,1);
    	case 3: return jogar(0,2);
    	case 4: return jogar(1,0);
    	case 5: return jogar(1,1);
    	case 6: return jogar(1,2);
    	case 7: return jogar(2,0);
    	case 8: return jogar(2,1);
    	case 9: return jogar(2,2);
    		default: return false;
    	}
    }
    
    private boolean jogar(int linha, int coluna) {
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
    
    public String enumeracaoDoTabuleiro() {
    	return ENUMERACAO_DO_TABULEIRO;
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
