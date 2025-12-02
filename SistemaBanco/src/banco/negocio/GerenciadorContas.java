package banco.negocio;

import banco.modelo.Cliente;
import banco.modelo.Conta;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import java.util.ArrayList;
import java.util.List;

// Classe de lógica de negócio responsável por gerenciar a lista de objetos Conta.
// Inclui operações de CRUD, busca e wrappers para operações bancárias (saque/depósito/remunera).
public class GerenciadorContas {
    private List<Conta> contas; // A lista principal de contas ativas no sistema.
    
    // Construtor simples. Inicializa a lista de contas como uma lista vazia.
    public GerenciadorContas() {
        this.contas = new ArrayList<>();
    }
    
    // Inicializa a lista de contas com dados de teste.
    // Este método deve ser chamado depois que o GerenciadorClientes for configurado.
    public void inicializarContasDeTeste(GerenciadorClientes gerenciadorClientes) {
        this.contas.clear(); // Limpa as contas existentes para começar do zero.

        // Buscamos os clientes de teste pelo CPF (limpo, sem máscara).
        Cliente amanda = gerenciadorClientes.buscarPorCpf("11111111111"); 
        Cliente ClienteEduardo = gerenciadorClientes.buscarPorCpf("22222222222"); 

        if (amanda != null) {
            // Cria uma Conta Corrente para Amanda com depósito inicial de 1000 e limite de 500.
            contas.add(new ContaCorrente(amanda, 1000.0, 500.0));
        }
        if (ClienteEduardo != null) {
            // Cria uma Conta Investimento para Eduardo com regras específicas.
            contas.add(new ContaInvestimento(ClienteEduardo, 5000.0, 1000.0, 100.0));
        }
    }

    // Retorna a lista completa de contas.
    public List<Conta> listarTodas() {
        return contas;
    }

    // Adiciona uma nova conta à lista (vincula um cliente a uma conta).
    public void adicionar(Conta conta) {
        contas.add(conta);
    }
    
    // Exclui todas as contas vinculadas a um cliente específico (usado na exclusão de cliente).
    public void excluirContasDoCliente(Cliente cliente) {
        List<Conta> contasParaRemover = new ArrayList<>();
        
        // Iteramos a lista de contas para encontrar as contas que pertencem ao cliente.
        for (Conta c : contas) {
            if (c.getDono().equals(cliente)) {
                contasParaRemover.add(c);
            }
        }
        
        // Removemos o conjunto de contas encontradas da lista principal.
        contas.removeAll(contasParaRemover);
    }

    // Busca uma conta pelo CPF do seu cliente titular.
    public Conta buscarContaPorCpfCliente(String cpf) {
        // Percorre a lista para encontrar a conta do cliente pelo CPF.
        for (Conta c : contas) {
            if (c.getDono().getCpf().equals(cpf)) {
                return c; // Retorna a conta assim que o CPF corresponder.
            }
        }
        return null; // Retorna null se não houver conta.
    }
    
    // Wrapper para a operação de saque. Chama o método saca() polimórfico da conta.
    public boolean sacar(Conta conta, double valor) {
        return conta.saca(valor); // O método saca() correto (subclasse) será executado.
    }
    
    // Wrapper para a operação de depósito. Chama o método deposita() polimórfico da conta.
    public boolean depositar(Conta conta, double valor) {
        return conta.deposita(valor); // O método deposita() correto será executado.
    }
    
    // Wrapper para a operação de remuneração. Chama o método remunera() polimórfico da conta.
    public void remunerar(Conta conta) {
        conta.remunera(); // O método remunera() correto será executado.
    }
}