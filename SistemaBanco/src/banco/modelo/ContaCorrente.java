package banco.modelo;

import javax.swing.JOptionPane;

/**
 * Subclasse de Conta que representa uma Conta Corrente.
 * Adiciona a funcionalidade de limite de cheque especial.
 */
public class ContaCorrente extends Conta {
    private double limite; // Limite do cheque especial

    /**
     * Construtor da Conta Corrente.
     * @param dono O Cliente titular da conta.
     * @param depositoInicial O saldo inicial.
     * @param limite O limite do cheque especial.
     */
    public ContaCorrente(Cliente dono, double depositoInicial, double limite) {
        super(dono, depositoInicial); // Chama o construtor da classe pai
        this.limite = limite;
    }

    /**
     * Retorna o limite do cheque especial.
     * @return O valor do limite.
     */
    public double getLimite() {
        return limite;
    }
    
    /**
     * Implementa a lógica de saque da Conta Corrente.
     * Permite saque até o valor do saldo + limite.
     * @param valor O valor a ser sacado.
     * @return true se o saque foi permitido, false caso contrário.
     */
    @Override
    public boolean saca(double valor) {
        // 1. Validação do valor (chama o saca do pai para validar valor > 0)
        if (!super.saca(valor)) {
            return false;
        }

        double novoSaldo = this.saldo - valor; // Calcula o saldo após o saque

        // 2. Verifica a regra de limite: O novo saldo deve ser MAIOR OU IGUAL a -limite
        if (novoSaldo >= -this.limite) { 
            this.saldo = novoSaldo; // Efetua o saque
            return true;
        } else {
            // Saque não permitido: saldo ultrapassa o limite negativo
            JOptionPane.showMessageDialog(null, "Saque não permitido. O valor ultrapassa o limite negativo de R$ " + String.format("%.2f", this.limite) + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE); 
            return false;
        }
    }

    /**
     * Aplica a remuneração (juros) da Conta Corrente (1%).
     */
    @Override
    public void remunera() {
        this.saldo *= 1.01; // Multiplica por 1.01 (aumenta em 1%)
    }
    
    /**
     * Retorna uma representação em String da Conta Corrente.
     * @return String formatada da conta.
     */
    @Override
    public String toString() {
        return "Conta Corrente Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Limite: R$ " + String.format("%.2f", limite) + ")";
    }
}