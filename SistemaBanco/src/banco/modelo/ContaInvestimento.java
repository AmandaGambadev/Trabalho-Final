package banco.modelo;

import javax.swing.JOptionPane;

// Subclasse de Conta que representa uma Conta Investimento.
// Possui regras adicionais: Montante Mínimo para saque e Depósito Mínimo.
public class ContaInvestimento extends Conta {
    private double montanteMinimo; // O saldo mínimo que deve permanecer na conta após um saque.
    private double depositoMinimo; // O valor mínimo para cada depósito.

    // Construtor da Conta Investimento.
    public ContaInvestimento(Cliente dono, double depositoInicial, double montanteMinimo, double depositoMinimo) {
        super(dono, 0); // Inicializa com saldo zero, pois o depósito inicial será validado separadamente.
        this.montanteMinimo = montanteMinimo;
        this.depositoMinimo = depositoMinimo;
        
        // Se houver valor inicial, tentamos depositar, aplicando a validação de depósito mínimo.
        if (depositoInicial > 0) {
            deposita(depositoInicial);
        }
    }

    // Retorna o montante mínimo de saldo.
    public double getMontanteMinimo() { return montanteMinimo; }
    
    // Retorna o valor mínimo para depósito.
    public double getDepositoMinimo() { return depositoMinimo; }

    // Implementa a lógica de depósito da Conta Investimento, checando a regra do Depósito Mínimo.
    @Override
    public boolean deposita(double valor) {
        // Checamos se o valor atende ao depósito mínimo configurado.
        if (valor >= this.depositoMinimo) {
            return super.deposita(valor); // Se sim, chama a lógica do pai para adicionar ao saldo.
        } else {
            // Se o valor for menor, emitimos o erro.
            JOptionPane.showMessageDialog(null, "Depósito não permitido. O valor mínimo para depósito é de R$ " + String.format("%.2f", this.depositoMinimo) + ".", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Implementa a lógica de saque da Conta Investimento, checando a regra do Montante Mínimo.
    @Override
    public boolean saca(double valor) {
        // Primeiro, chamamos o saca do pai para validar se o valor é positivo.
        if (!super.saca(valor)) {
            return false;
        }
        
        // Checamos a regra do Montante Mínimo: o saldo após o saque deve ser maior ou igual ao mínimo.
        if (this.saldo - valor >= this.montanteMinimo) {
            this.saldo -= valor; // Efetua o saque.
            return true;
        } else {
            // Se o saldo restante for insuficiente, emitimos o erro.
            JOptionPane.showMessageDialog(null, "Saque não permitido. O saldo restante deve ser maior ou igual ao montante mínimo de R$ " + String.format("%.2f", this.montanteMinimo) + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Aplica a remuneração (rendimento) da Conta Investimento, que é de 2%.
    @Override
    public void remunera() {
        this.saldo *= 1.02; // Aumenta o saldo em 2%.
    }
    
    // Retorna uma representação em String da Conta Investimento.
    @Override
    public String toString() {
        return "Conta Investimento Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Mínimo: R$ " + String.format("%.2f", montanteMinimo) + ")";
    }
}