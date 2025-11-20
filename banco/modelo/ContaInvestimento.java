package banco.modelo;

import javax.swing.JOptionPane;

public class ContaInvestimento extends Conta {
    private double montanteMinimo;
    private double depositoMinimo;

    public ContaInvestimento(Cliente dono, double depositoInicial, double montanteMinimo, double depositoMinimo) {
        super(dono, 0);
        this.montanteMinimo = montanteMinimo;
        this.depositoMinimo = depositoMinimo;
        
        if (depositoInicial > 0) {
            deposita(depositoInicial);
        }
    }

    public double getMontanteMinimo() { return montanteMinimo; }
    public double getDepositoMinimo() { return depositoMinimo; }

    @Override
    public boolean deposita(double valor) {
        if (valor >= this.depositoMinimo) {
            return super.deposita(valor);
        } else {
            JOptionPane.showMessageDialog(null, "Depósito não permitido. O valor mínimo para depósito é de R$ " + String.format("%.2f", this.depositoMinimo) + ".", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean saca(double valor) {
        if (!super.saca(valor)) {
            return false;
        }
        
        if (this.saldo - valor >= this.montanteMinimo) {
            this.saldo -= valor;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Saque não permitido. O saldo restante deve ser maior ou igual ao montante mínimo de R$ " + String.format("%.2f", this.montanteMinimo) + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void remunera() {
        this.saldo *= 1.02;
        JOptionPane.showMessageDialog(null, "Remuneração de 2% aplicada à Conta Investimento " + getNumero() + ".\nNovo Saldo: R$ " + String.format("%.2f", this.saldo), "Remuneração Aplicada", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public String toString() {
        return "Conta Investimento Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Mínimo: R$ " + String.format("%.2f", montanteMinimo) + ")";
    }
}
