package banco.modelo;

import javax.swing.JOptionPane;

/**
 * Subclasse de Conta que representa uma Conta Investimento.
 * Adiciona regras de Montante Mínimo para saque e Depósito Mínimo.
 */
public class ContaInvestimento extends Conta {
    private double montanteMinimo; // Saldo mínimo que deve ser mantido após um saque
    private double depositoMinimo; // Valor mínimo para cada depósito

    /**
     * Construtor da Conta Investimento.
     * @param dono O Cliente titular.
     * @param depositoInicial O saldo inicial (que deve atender ao depósito mínimo).
     * @param montanteMinimo O saldo mínimo a ser mantido.
     * @param depositoMinimo O valor mínimo para cada depósito.
     */
    public ContaInvestimento(Cliente dono, double depositoInicial, double montanteMinimo, double depositoMinimo) {
        super(dono, 0); // Inicializa com saldo 0, o depósito inicial será validado no método deposita()
        this.montanteMinimo = montanteMinimo;
        this.depositoMinimo = depositoMinimo;
        
        // Tenta depositar o valor inicial (sujeito à regra do depósito mínimo)
        if (depositoInicial > 0) {
            deposita(depositoInicial);
        }
    }

    /**
     * Retorna o montante mínimo de saldo.
     * @return O montante mínimo.
     */
    public double getMontanteMinimo() { return montanteMinimo; }
    
    /**
     * Retorna o valor mínimo para depósito.
     * @return O depósito mínimo.
     */
    public double getDepositoMinimo() { return depositoMinimo; }

    /**
     * Implementa a lógica de depósito da Conta Investimento.
     * Valida o valor mínimo do depósito.
     * @param valor O valor a ser depositado.
     * @return true se o depósito foi permitido, false caso contrário.
     */
    @Override
    public boolean deposita(double valor) {
        // Verifica se o valor é maior ou igual ao depósito mínimo
        if (valor >= this.depositoMinimo) {
            return super.deposita(valor); // Chama o depósito da classe pai (valida valor > 0 e efetua)
        } else {
            // Depósito não permitido: valor abaixo do mínimo
            JOptionPane.showMessageDialog(null, "Depósito não permitido. O valor mínimo para depósito é de R$ " + String.format("%.2f", this.depositoMinimo) + ".", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Implementa a lógica de saque da Conta Investimento.
     * Valida se o saldo restante é maior ou igual ao montante mínimo.
     * @param valor O valor a ser sacado.
     * @return true se o saque foi permitido, false caso contrário.
     */
    @Override
    public boolean saca(double valor) {
        // 1. Validação do valor (chama o saca do pai para validar valor > 0)
        if (!super.saca(valor)) {
            return false;
        }
        
        // 2. Verifica a regra do Montante Mínimo
        if (this.saldo - valor >= this.montanteMinimo) {
            this.saldo -= valor; // Efetua o saque
            return true;
        } else {
            // Saque não permitido: saldo restante abaixo do montante mínimo
            JOptionPane.showMessageDialog(null, "Saque não permitido. O saldo restante deve ser maior ou igual ao montante mínimo de R$ " + String.format("%.2f", this.montanteMinimo) + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Aplica a remuneração (rendimento) da Conta Investimento (2%).
     */
    @Override
    public void remunera() {
        this.saldo *= 1.02; // Multiplica por 1.02 (aumenta em 2%)
    }
    
    /**
     * Retorna uma representação em String da Conta Investimento.
     * @return String formatada da conta.
     */
    @Override
    public String toString() {
        return "Conta Investimento Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Mínimo: R$ " + String.format("%.2f", montanteMinimo) + ")";
    }
}