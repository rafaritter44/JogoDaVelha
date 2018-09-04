package model;

public class Jogador {
    
    private String nome;
    private Elemento elemento;
    
    public Jogador(String nome, Elemento elemento) {
        this.nome = nome;
        this.elemento = elemento;
    }
    
    public String getNome() { return nome; }
    public Elemento getElemento() { return elemento; }
    
}
