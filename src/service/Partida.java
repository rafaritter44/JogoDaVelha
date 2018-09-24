package service;

import java.util.Optional;
import model.Elemento;
import model.Jogador;

/*
 * Classe que representa uma partida, contendo um tabuleiro e os dois jogadores
 */
public class Partida {

    private Elemento[][] tabuleiro;
    private Jogador jogadorXis;
    private Jogador jogadorBolinha;
    private Elemento vez; //Indica de quem é a vez de jogar
    private static final String ENUMERACAO_DO_TABULEIRO = "1 2 3\n4 5 6\n7 8 9";
    //Representação do tabuleiro através de números
    //Essa enumeração será usada para indicar em qual espaço do tabuleiro
    //o jogador deseja realizar sua jogada, informando a posição desejada
    
    public Partida() {
        tabuleiro = new Elemento[3][3];
        for(int i=0; i<3; i++)
            for(int j=0; j<3; j++)
                tabuleiro[i][j] = Elemento.VAZIO; //Tabuleiro começa vazio
        jogadorXis = new Jogador("Jogador XIS", Elemento.XIS);
        jogadorBolinha = new Jogador("Jogador BOLINHA", Elemento.BOLINHA);
        vez = Elemento.XIS; //Xis sempre começa jogando
    }
    
    /*
     * Informa quem é o vencedor, caso já exista algum,
     * ou o empate, caso tenha dado velha
     */
    public Optional<String> vencedor() {
    	if(venceu(Elemento.XIS))
    		return Optional.ofNullable(jogadorXis.getNome());
    	if(venceu(Elemento.BOLINHA))
    		return Optional.ofNullable(jogadorBolinha.getNome());
    	if(velha())
    		return Optional.ofNullable("Ninguem! Deu velha");
    	return Optional.empty();
    }
    
    /*
     * Retorna verdadeiro caso não existam mais jogadas possíveis,
     * isto é, caso todos os espaçoes estejam preenchidos
     */
    private boolean velha() {
    	for(int i=0; i<3; i++)
    		for(int j=0; j<3; j++)
    			if(tabuleiro[i][j] == Elemento.VAZIO)
    				return false;
    	return true;
    }
    
    /*
     * Retorna verdadeiro caso o elemento informado tenha formado uma
     * sequência de três casas: em linha, em coluna, ou em diagonal
     */
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
    
    /*
     * Atualiza o tabuleiro e retorna verdadeiro caso tenha sido informada uma jogada válida
     * Caso a jogada seja inválida (posição não vazia, ou entrada fora do formato esperado),
     * então o método retorna falso e mantém o tabuleiro do jeito que está
     */
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
    
    /*
     * Caso a jogada seja válida (em uma casa vazia) atualiza o tabuleiro, troca a vez, e retora verdadeiro
     * Caso a jogada seja inválida, mantém o estado do objeto e das propriedades da mesma forma e retorna falso
     */
    private boolean jogar(int linha, int coluna) {
        if(tabuleiro[linha][coluna] == Elemento.VAZIO) {
            tabuleiro[linha][coluna] = vez;
            trocaVez();
            return true;
        }
        return false;
    }
    
    /*
     * Caso a última vez tenha sido do xis, configura a vez para bolinha
     * Caso tenha sido bolinha, configura para xis
     */
    private void trocaVez() {
        if(vez == Elemento.XIS)
            vez = Elemento.BOLINHA;
        else
            vez = Elemento.XIS;
    }
    
    /*
     * Retorna a representação visual do tabuleiro enumerado
     */
    public String enumeracaoDoTabuleiro() {
    	return ENUMERACAO_DO_TABULEIRO;
    }
    
    /*
     * Retorna a representação visual do estado atual do tabuleiro
     */
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
