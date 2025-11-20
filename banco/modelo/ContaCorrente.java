package banco.modelo;

import javax.swing.JOptionPane;

public class ContaCorrente extends Conta {
    private double limite;

    public ContaCorrente(Cliente dono, double depositoInicial, double limite) {
        super(dono, depositoInicial);
        this.limite = limite;
    }

    public double getLimite() {
        return limite;
    }
    
    @Override
    public boolean saca(double valor) {
        if (!super.saca(valor)) {
            return false;
        }
        
        if (this.saldo - valor >= -this.limite) {
            this.saldo -= valor;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Saque não permitido. O valor ultrapassa o limite de R$ " + this.limite + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void remunera() {
        this.saldo *= 1.01;
        JOptionPane.showMessageDialog(null, "Remuneração de 1% aplicada à Conta Corrente " + getNumero() + ".\nNovo Saldo: R$ " + String.format("%.2f", this.saldo), "Remuneração Aplicada", JOptionPane.INFORMATION_MESSAGE);
    }
    
    @Override
    public String toString() {
        return "Conta Corrente Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Limite: R$ " + String.format("%.2f", limite) + ")";
    }
}
