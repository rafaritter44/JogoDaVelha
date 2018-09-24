package model;

/*
 * Enum que representa um elemento do tabuleiro, que pode ser:
 * Xis; Bolinha; ou Vazio (i.e., espaço livre para jogar)
 */
public enum Elemento {
    XIS("X"),
    BOLINHA("O"),
    VAZIO("_");
    
    private String representacao;
    
    Elemento(String representacao) {
        this.representacao = representacao;
    }
    
    public String getRepresentacao() { return representacao; }
    
}
