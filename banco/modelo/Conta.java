package banco.modelo;

import javax.swing.JOptionPane;

public abstract class Conta implements ContaI {
    private static int PROXIMO_NUMERO = 1000;
    
    private Cliente dono;
    private int numero;
    protected double saldo;

    public Conta(Cliente dono, double depositoInicial) {
        this.dono = dono;
        this.numero = PROXIMO_NUMERO++;
        this.saldo = 0;
        deposita(depositoInicial);
    }

    @Override
    public Cliente getDono() { return dono; }

    @Override
    public int getNumero() { return numero; }

    @Override
    public double getSaldo() { return saldo; }

    @Override
    public boolean deposita(double valor) {
        if (valor > 0) {
            this.saldo += valor;
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "O valor do depósito deve ser positivo.", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean saca(double valor) {
        if (valor > 0) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "O valor do saque deve ser positivo.", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    @Override
    public abstract void remunera();
}
