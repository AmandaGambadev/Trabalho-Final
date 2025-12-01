package banco.negocio;

import banco.modelo.Cliente;
import banco.modelo.Conta;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de lógica de negócio responsável por gerenciar a lista de objetos Conta.
 * Inclui operações de CRUD, busca e wrappers para operações bancárias (saque/depósito/remunera).
 */
public class GerenciadorContas {
    private List<Conta> contas; // Lista de contas ativas
    
    /**
     * Construtor. Inicializa a lista de contas.
     */
    public GerenciadorContas() {
        this.contas = new ArrayList<>();
    }
    
    /**
     * Inicializa a lista de contas com dados de teste.
     * Deve ser chamada APÓS a inicialização do GerenciadorClientes.
     * @param gerenciadorClientes A instância do GerenciadorClientes para buscar os titulares.
     */
    public void inicializarContasDeTeste(GerenciadorClientes gerenciadorClientes) {
        this.contas.clear(); // Limpa a lista para garantir um estado inicial limpo

        // Busca os clientes usando os CPFs LIMPOS (sem máscara)
        Cliente amanda = gerenciadorClientes.buscarPorCpf("11111111111"); 
        Cliente eduardo = gerenciadorClientes.buscarPorCpf("22222222222"); 

        if (amanda != null) {
            // Conta Corrente para Amanda: Depósito Inicial 1000, Limite 500
            contas.add(new ContaCorrente(amanda, 1000.0, 500.0));
        }
        if (eduardo != null) {
            // Conta Investimento para Eduardo: Depósito 5000, Montante Mínimo 1000, Depósito Mínimo 100
            contas.add(new ContaInvestimento(eduardo, 5000.0, 1000.0, 100.0));
        }
    }

    /**
     * Retorna a lista completa de contas.
     * @return Lista de todas as Contas.
     */
    public List<Conta> listarTodas() {
        return contas;
    }

    /**
     * Adiciona uma nova conta à lista (vincula um cliente a uma conta).
     * @param conta O objeto Conta a ser adicionado.
     */
    public void adicionar(Conta conta) {
        contas.add(conta);
    }
    
    /**
     * Exclui todas as contas vinculadas a um cliente específico.
     * @param cliente O Cliente cujas contas devem ser removidas.
     */
    public void excluirContasDoCliente(Cliente cliente) {
        List<Conta> contasParaRemover = new ArrayList<>();
        
        // Percorre a lista de contas para identificar as contas do cliente
        for (Conta c : contas) {
            if (c.getDono().equals(cliente)) {
                contasParaRemover.add(c);
            }
        }
        
        // Remove todas as contas encontradas de uma vez
        contas.removeAll(contasParaRemover);
    }

    /**
     * Busca uma conta pelo CPF do seu cliente titular.
     * @param cpf O CPF do cliente (apenas números).
     * @return O objeto Conta encontrado ou null.
     */
    public Conta buscarContaPorCpfCliente(String cpf) {
        for (Conta c : contas) {
            if (c.getDono().getCpf().equals(cpf)) {
                return c; // Retorna a conta se o CPF corresponder
            }
        }
        return null; // Retorna null se não encontrar
    }
    
    /**
     * Wrapper para a operação de saque (chamada polimórfica).
     * @param conta A conta na qual a operação será realizada.
     * @param valor O valor do saque.
     * @return true se o saque for bem-sucedido, false caso contrário.
     */
    public boolean sacar(Conta conta, double valor) {
        return conta.saca(valor); // O saca() correto (Conta, ContaCorrente ou ContaInvestimento) será chamado
    }
    
    /**
     * Wrapper para a operação de depósito (chamada polimórfica).
     * @param conta A conta na qual a operação será realizada.
     * @param valor O valor do depósito.
     * @return true se o depósito for bem-sucedido, false caso contrário.
     */
    public boolean depositar(Conta conta, double valor) {
        return conta.deposita(valor); // O deposita() correto será chamado
    }
    
    /**
     * Wrapper para a operação de remuneração (chamada polimórfica).
     * @param conta A conta na qual a remuneração será aplicada.
     */
    public void remunerar(Conta conta) {
        conta.remunera(); // O remunera() correto será chamado
    }
}