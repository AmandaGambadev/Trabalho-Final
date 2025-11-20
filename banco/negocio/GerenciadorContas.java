package banco.negocio;

import banco.modelo.Cliente;
import banco.modelo.Conta;
import banco.modelo.ContaCorrente;
import banco.modelo.ContaInvestimento;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe simples para gerenciar a lista de Contas.
 * Não usa o padrão Singleton.
 */
public class GerenciadorContas {
    private List<Conta> contas;
    
    public GerenciadorContas(GerenciadorClientes gerenciadorClientes) {
        this.contas = new ArrayList<>();
        
        // Adicionar contas de teste para os clientes existentes
        Cliente ana = gerenciadorClientes.buscarPorCpf("111.111.111-11");
        Cliente bruno = gerenciadorClientes.buscarPorCpf("222.222.222-22");
        
        if (ana != null) {
            // Conta Corrente para Ana: Depósito 1000, Limite 500
            contas.add(new ContaCorrente(ana, 1000.0, 500.0));
        }
        if (bruno != null) {
            // Conta Investimento para Bruno: Depósito 5000, Montante Mínimo 1000, Depósito Mínimo 100
            contas.add(new ContaInvestimento(bruno, 5000.0, 1000.0, 100.0));
        }
    }

    public List<Conta> listarTodas() {
        return contas;
    }

    // Regra 2: Vincular cliente a uma conta
    public void adicionar(Conta conta) {
        contas.add(conta);
    }
    
    // Regra 1.c: Excluir todas as contas de um cliente
    public void excluirContasDoCliente(Cliente cliente) {
        // Usa laço for simples para remover (mais didático para iniciantes)
        List<Conta> contasParaRemover = new ArrayList<>();
        for (Conta c : contas) {
            if (c.getDono().equals(cliente)) {
                contasParaRemover.add(c);
            }
        }
        contas.removeAll(contasParaRemover);
    }

    // Regra 3.a: Selecionar a conta de um cliente pelo CPF
    public Conta buscarContaPorCpfCliente(String cpf) {
        for (Conta c : contas) {
            if (c.getDono().getCpf().equals(cpf)) {
                return c;
            }
        }
        return null;
    }
    
    // Polimorfismo em ação
    public boolean sacar(Conta conta, double valor) {
        return conta.saca(valor);
    }
    
    public boolean depositar(Conta conta, double valor) {
        return conta.deposita(valor);
    }
    
    public void remunerar(Conta conta) {
        conta.remunera();
    }
}
