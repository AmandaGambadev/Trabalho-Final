package banco.modelo;

import javax.swing.JOptionPane;

/**
 * Classe abstrata base que implementa as funcionalidades comuns de todas as contas bancárias.
 * Implementa a interface ContaI.
 */
public abstract class Conta implements ContaI {
    
    private static int PROXIMO_NUMERO = 1000; // Gerador sequencial de número de conta
    
    private Cliente dono;
    private int numero;
    protected double saldo; // Protegido para acesso direto em subclasses (ContaCorrente, ContaInvestimento)

    /**
     * Construtor da Conta. Inicializa o dono, o número e o saldo.
     * @param dono O objeto Cliente que é o titular da conta.
     * @param depositoInicial O valor inicial do saldo da conta.
     */
    public Conta(Cliente dono, double depositoInicial) {
        this.dono = dono;
        this.numero = PROXIMO_NUMERO++; // Atribui o próximo número e incrementa
        this.saldo = depositoInicial; // Inicializa o saldo
    }

    // --- Implementação dos Getters da interface ContaI ---
    @Override
    public Cliente getDono() { return dono; }

    @Override
    public int getNumero() { return numero; }

    @Override
    public double getSaldo() { return saldo; }

    /**
     * Implementa a lógica de depósito.
     * @param valor O valor a ser depositado.
     * @return true se o valor for positivo e o depósito for realizado, false caso contrário.
     */
    @Override
    public boolean deposita(double valor) {
        if (valor > 0) {
            this.saldo += valor; // Adiciona o valor ao saldo
            return true;
        } else {
            // Exibe mensagem de erro se o valor for inválido
            JOptionPane.showMessageDialog(null, "O valor do depósito deve ser positivo.", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

   /**
    * Lógica de validação básica de saque (valor positivo).
    * Subclasses (ContaCorrente, ContaInvestimento) devem sobrescrever este método
    * para adicionar regras específicas (limite, montante mínimo).
    * @param valor O valor a ser sacado.
    * @return true se o valor for positivo, false caso contrário.
    */
    @Override
    public boolean saca(double valor) {
        // Regra: valor sacado deve ser positivo.
        if (valor > 0) {
            return true; // Permite que a subclasse continue a lógica de saque
        } else {
            // Exibe mensagem de erro se o valor for inválido
            JOptionPane.showMessageDialog(null, "O valor do saque deve ser positivo.", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Método abstrato que força as subclasses a implementarem a lógica de remuneração.
     */
    @Override
    public abstract void remunera();
}