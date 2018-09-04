package model;

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
