package banco.modelo;

import javax.swing.JOptionPane;

// Classe abstrata base que implementa as funcionalidades comuns de todas as contas bancárias.
// Implementa a interface ContaI, definindo a estrutura básica.
public abstract class Conta implements ContaI {
    
    private static int PROXIMO_NUMERO = 1000; // Contador estático para gerar o número da conta sequencialmente.
    
    private Cliente dono;
    private int numero;
    protected double saldo; // O saldo é protegido para que subclasses possam acessá-lo diretamente (ex: ContaCorrente).

    // Construtor da Conta. Inicializa o dono, o número e o saldo.
    public Conta(Cliente dono, double depositoInicial) {
        this.dono = dono;
        this.numero = PROXIMO_NUMERO++; // Atribui o número sequencial e incrementa o contador.
        this.saldo = depositoInicial; // Define o saldo inicial.
    }

    // --- Implementação dos Getters da interface ContaI ---
    public Cliente getDono() { return dono; }

    public int getNumero() { return numero; }

    public double getSaldo() { return saldo; }

    // Implementa a lógica de depósito, checando se o valor é positivo.
    @Override
    public boolean deposita(double valor) {
        // Checamos se o valor do depósito é positivo.
        if (valor > 0) {
            this.saldo += valor; // Adiciona o valor ao saldo atual.
            return true;
        } else {
            // Se for zero ou negativo, mostra um erro.
            JOptionPane.showMessageDialog(null, "O valor do depósito deve ser positivo.", "Erro de Depósito", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Lógica de validação básica de saque: checa apenas se o valor é positivo.
    // As regras de saldo/limite/montante mínimo são delegadas às subclasses.
    @Override
    public boolean saca(double valor) {
        // Verifica se o valor a ser sacado é maior que zero.
        if (valor > 0) {
            return true; // OK, permite que a subclasse continue com as validações específicas.
        } else {
            // Se for zero ou negativo, mostra um erro.
            JOptionPane.showMessageDialog(null, "O valor do saque deve ser positivo.", "Erro de Saque", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Método abstrato que obriga as classes filhas (ContaCorrente, ContaInvestimento)
    // a implementarem a lógica de remuneração de forma específica.
    @Override
    public abstract void remunera();
}