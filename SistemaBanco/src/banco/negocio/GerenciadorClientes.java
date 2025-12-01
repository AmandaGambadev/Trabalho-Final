package banco.negocio;

import banco.modelo.Cliente;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Classe de lógica de negócio responsável por gerenciar a lista de objetos Cliente.
 * Inclui operações de CRUD, busca e ordenação.
 */
public class GerenciadorClientes {
    private List<Cliente> clientes;
    // Referência ao GerenciadorContas para acessar informações de saldo durante a ordenação
    private final GerenciadorContas gerenciadorContas;
    
    /**
     * Construtor. Inicializa a lista de clientes e define a referência ao GerenciadorContas.
     * @param gerenciadorContas A instância do GerenciadorContas.
     */
    public GerenciadorClientes(GerenciadorContas gerenciadorContas) {
        this.clientes = new ArrayList<>();
        this.gerenciadorContas = gerenciadorContas; // Define a referência
        
        // Adicionar clientes iniciais para teste
        clientes.add(new Cliente("Amanda", "Cristine ", "1234567", "11111111111", "Rua A"));
        clientes.add(new Cliente("Eduardo", "Almeida", "7654321", "22222222222", "Rua B"));
        clientes.add(new Cliente("Guilherme", "Gemniczak", "9876543", "33333333333", "Rua C"));
    }

    /**
     * Retorna a lista completa de clientes.
     * @return Lista de todos os Clientes.
     */
    public List<Cliente> listarTodos() {
        return clientes;
    }

    /**
     * Adiciona um novo cliente à lista.
     * @param cliente O objeto Cliente a ser adicionado.
     */
    public void adicionar(Cliente cliente) {
        clientes.add(cliente);
    }

    /**
     * Remove um cliente da lista.
     * @param cliente O objeto Cliente a ser removido.
     * @return true se o cliente foi encontrado e removido, false caso contrário.
     */
    public boolean excluir(Cliente cliente) {
        return clientes.remove(cliente);
    }
    
    /**
     * Busca um cliente pelo seu CPF.
     * @param cpf O CPF do cliente (apenas números).
     * @return O objeto Cliente encontrado ou null.
     */
    public Cliente buscarPorCpf(String cpf) {
        for (Cliente c : clientes) {
            if (c.getCpf().equals(cpf)) {
                return c; // Retorna o cliente se o CPF for encontrado
            }
        }
        return null; // Retorna null se não encontrar
    }

    /**
     * Realiza uma busca em clientes por nome, sobrenome, RG ou CPF.
     * @param termo O termo de busca (case insensitive para nome/sobrenome).
     * @return Uma lista de Clientes que correspondem ao termo.
     */
    public List<Cliente> buscar(String termo) {
        List<Cliente> resultados = new ArrayList<>();
        String termoLower = termo.toLowerCase(); // Converte o termo para minúsculas
        
        for (Cliente c : clientes) {
            // Verifica se o termo está contido em Nome, Sobrenome, RG ou CPF
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
     * Ordena uma lista de clientes por um campo específico (Nome, Sobrenome ou Salário).
     * @param campo O critério de ordenação ("Nome", "Sobrenome", "Salário").
     * @param lista A lista de clientes a ser ordenada.
     * @return A lista de clientes ordenada.
     */
    public List<Cliente> ordenar(String campo, List<Cliente> lista) {
        List<Cliente> listaOrdenada = new ArrayList<>(lista); // Cria uma cópia da lista
        
        if (campo.equalsIgnoreCase("nome")) {
            // Ordenação natural (usa o compareTo da classe Cliente)
            Collections.sort(listaOrdenada); 
            
        } else if (campo.equalsIgnoreCase("sobrenome")) {
            // Ordena usando um Comparator (por sobrenome)
            Collections.sort(listaOrdenada, Comparator.comparing(Cliente::getSobrenome));
            
        } else if (campo.equalsIgnoreCase("salário")) { 
            // Ordena usando um Comparator customizado baseado no saldo da conta (acessado via GerenciadorContas)
            Collections.sort(listaOrdenada, (c1, c2) -> {
                // Obtém o saldo do Cliente 1 (0.0 se não tiver conta)
                double saldo1 = gerenciadorContas.buscarContaPorCpfCliente(c1.getCpf()) != null ? 
                                gerenciadorContas.buscarContaPorCpfCliente(c1.getCpf()).getSaldo() : 0.0;
                
                // Obtém o saldo do Cliente 2 (0.0 se não tiver conta)
                double saldo2 = gerenciadorContas.buscarContaPorCpfCliente(c2.getCpf()) != null ? 
                                gerenciadorContas.buscarContaPorCpfCliente(c2.getCpf()).getSaldo() : 0.0;
                
                // Ordem decrescente (do maior saldo para o menor): compara saldo2 com saldo1.
                return Double.compare(saldo2, saldo1); 
            }); 
        }
        return listaOrdenada;
    }
}