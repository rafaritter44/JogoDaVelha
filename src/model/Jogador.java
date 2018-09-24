package model;

/*
 * Classe que representa um jogador, com o nome que o identifica
 * e o elemento com o qual ele está jogando (xis ou bolinha)
 */
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
