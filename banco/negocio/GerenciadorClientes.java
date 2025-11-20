package banco.negocio;

import banco.modelo.Cliente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Classe simples para gerenciar a lista de Clientes.
 * Não usa o padrão Singleton.
 */
public class GerenciadorClientes {
    private List<Cliente> clientes;
    
    public GerenciadorClientes() {
        this.clientes = new ArrayList<>();
        // Adicionar alguns clientes de teste
        clientes.add(new Cliente("Ana", "Silva", "1234567", "111.111.111-11", "Rua A"));
        clientes.add(new Cliente("Bruno", "Costa", "7654321", "222.222.222-22", "Rua B"));
        clientes.add(new Cliente("Carlos", "Oliveira", "9876543", "333.333.333-33", "Rua C"));
    }

    public List<Cliente> listarTodos() {
        return clientes;
    }

    public void adicionar(Cliente cliente) {
        clientes.add(cliente);
    }

    public boolean excluir(Cliente cliente) {
        return clientes.remove(cliente);
    }
    
    /**
     * Busca por CPF usando laço for simples.
     */
    public Cliente buscarPorCpf(String cpf) {
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Regra 1.d: Busca por nome, sobrenome, RG ou CPF usando laço for simples.
     */
    public List<Cliente> buscar(String termo) {
        List<Cliente> resultados = new ArrayList<>();
        String termoLower = termo.toLowerCase();
        
        for (Cliente c : clientes) {
            if (c.getNome().toLowerCase().contains(termoLower) ||
                c.getSobrenome().toLowerCase().contains(termoLower) ||
                c.getRg().contains(termo) ||
                c.getCpf().contains(termo)) {
                
                resultados.add(c);
            }
        }
        return resultados;
    }
    
    /**
     * Regra 1.e: Ordenação por Nome ou Sobrenome.
     */
    public List<Cliente> ordenar(String campo, List<Cliente> lista) {
        List<Cliente> listaOrdenada = new ArrayList<>(lista);
        
        if (campo.equalsIgnoreCase("nome")) {
            // Usa o compareTo da classe Cliente (ordem alfabética)
            Collections.sort(listaOrdenada); 
        } else if (campo.equalsIgnoreCase("sobrenome")) {
            // Usa um Comparator para ordenar por sobrenome (ordem alfabética)
            Collections.sort(listaOrdenada, Comparator.comparing(Cliente::getSobrenome));
        }
        return listaOrdenada;
    }
}
