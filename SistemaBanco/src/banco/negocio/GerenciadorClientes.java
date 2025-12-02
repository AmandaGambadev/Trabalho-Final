package banco.negocio;

import banco.modelo.Cliente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Classe de lógica de negócio responsável por gerenciar a lista de objetos Cliente.
// Inclui operações de CRUD, busca e ordenação.
public class GerenciadorClientes {
    private List<Cliente> clientes;
    // Referência ao GerenciadorContas para acessar informações de saldo durante a ordenação.
    private final GerenciadorContas gerenciadorContas;
    
    // Construtor. Inicializa a lista de clientes e define a referência ao GerenciadorContas.
    public GerenciadorClientes(GerenciadorContas gerenciadorContas) {
        this.clientes = new ArrayList<>();
        this.gerenciadorContas = gerenciadorContas; // Define a referência do gerenciador de contas.
        
        // Adicionamos alguns clientes iniciais para teste no sistema.
        clientes.add(new Cliente("Amanda", "Cristine ", "1234567", "11111111111", "Rua A"));
        clientes.add(new Cliente("Eduardo", "Almeida", "7654321", "22222222222", "Rua B"));
        clientes.add(new Cliente("Guilherme", "Gemniczak", "9876543", "33333333333", "Rua C"));
    }

    // Retorna a lista completa de clientes.
    public List<Cliente> listarTodos() {
        return clientes;
    }

    // Adiciona um novo cliente à lista.
    public void adicionar(Cliente cliente) {
        clientes.add(cliente);
    }

    // Remove um cliente da lista.
    public boolean excluir(Cliente cliente) {
        return clientes.remove(cliente);
    }
    
    // Busca um cliente pelo seu CPF.
    public Cliente buscarPorCpf(String cpf) {
        // Percorre a lista de clientes para encontrar a correspondência de CPF.
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c; // Retorna o cliente assim que o CPF é encontrado.
            }
        }
        return null; // Se o laço terminar e nada for encontrado, retorna null.
    }

    // Realiza uma busca em clientes por nome, sobrenome, RG ou CPF.
    public List<Cliente> buscar(String termo) {
        List<Cliente> resultados = new ArrayList<>();
        String termoLower = termo.toLowerCase(); // Convertemos o termo para minúsculas para a busca.
        
        // Percorre a lista checando se o termo está contido em qualquer campo relevante.
        for (Cliente c : clientes) {
            // Verifica o nome, sobrenome (ambos em minúsculas), RG ou CPF.
            if (c.getNome().toLowerCase().contains(termoLower) ||
                c.getSobrenome().toLowerCase().contains(termoLower) ||
                c.getRg().contains(termo) ||
                c.getCpf().contains(termo)) {
                
                resultados.add(c);
            }
        }
        return resultados;
    }
    
    // Ordena uma lista de clientes por um campo específico (Nome, Sobrenome ou Salário).
    public List<Cliente> ordenar(String campo, List<Cliente> lista) {
        List<Cliente> listaOrdenada = new ArrayList<>(lista); // Cria uma cópia da lista para não alterar a original.
        
        if (campo.equalsIgnoreCase("nome")) {
            // Ordena usando a ordem natural (implementada no compareTo da classe Cliente).
            Collections.sort(listaOrdenada); 
            
        } else if (campo.equalsIgnoreCase("sobrenome")) {
            // Ordena usando um Comparator que compara pelo Sobrenome.
            Collections.sort(listaOrdenada, Comparator.comparing(Cliente::getSobrenome));
            
        } else if (campo.equalsIgnoreCase("salário")) { 
            // Ordena por Salário, que é na verdade o saldo da conta (ordem decrescente).
            Collections.sort(listaOrdenada, (c1, c2) -> {
                
                // Busca o saldo do Cliente 1. Se não tiver conta, o saldo é 0.0.
                double saldo1 = gerenciadorContas.buscarContaPorCpfCliente(c1.getCpf()) != null ? 
                                gerenciadorContas.buscarContaPorCpfCliente(c1.getCpf()).getSaldo() : 0.0;
                
                // Busca o saldo do Cliente 2. Se não tiver conta, o saldo é 0.0.
                double saldo2 = gerenciadorContas.buscarContaPorCpfCliente(c2.getCpf()) != null ? 
                                gerenciadorContas.buscarContaPorCpfCliente(c2.getCpf()).getSaldo() : 0.0;
                
                // Compara saldo2 com saldo1 para obter a ordem decrescente (do maior saldo para o menor).
                return Double.compare(saldo2, saldo1); 
            }); 
        }
        return listaOrdenada;
    }
}