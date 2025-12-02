package banco.modelo;

import javax.swing.JOptionPane;

// Subclasse de Conta que representa uma Conta Corrente.
// Adiciona a funcionalidade do limite de cheque especial.
public class ContaCorrente extends Conta {
    private double limite; // O valor máximo permitido para o cheque especial.

    // Construtor da Conta Corrente.
    public ContaCorrente(Cliente dono, double depositoInicial, double limite) {
        super(dono, depositoInicial); // Chama o construtor da classe pai (Conta).
        this.limite = limite;
    }

    // Retorna o limite do cheque especial.
    public double getLimite() {
        return limite;
    }
    
    // Implementa a lógica de saque da Conta Corrente.
    // Permite sacar desde que o saldo final não ultrapasse o limite negativo.
    @Override
    public boolean saca(double valor) {
        // Primeiro, chamamos o saca do pai para validar se o valor é positivo.
        if (!super.saca(valor)) {
            return false;
        }

        double novoSaldo = this.saldo - valor; // Simula o saldo após o saque.

        // Checamos a regra do limite: o novo saldo deve ser maior ou igual ao limite negativo (-limite).
        if (novoSaldo >= -this.limite) { 
            this.saldo = novoSaldo; // Efetua a operação e atualiza o saldo.
            return true;
        } else {
            // Se ultrapassar o limite, mostramos a mensagem de erro.
            JOptionPane.showMessageDialog(null, "Saque não permitido. O valor ultrapassa o limite negativo de R$ " + String.format("%.2f", this.limite) + ".", "Erro de Saque", JOptionPane.ERROR_MESSAGE); 
            return false;
        }
    }

    // Aplica a remuneração (juros) da Conta Corrente, que é de 1%.
    @Override
    public void remunera() {
        this.saldo *= 1.01; // Aumenta o saldo em 1%.
    }
    
    // Retorna uma representação em String da Conta Corrente.
    @Override
    public String toString() {
        return "Conta Corrente Nº " + getNumero() + " (Dono: " + getDono().getNome() + ", Saldo: R$ " + String.format("%.2f", saldo) + ", Limite: R$ " + String.format("%.2f", limite) + ")";
    }
}