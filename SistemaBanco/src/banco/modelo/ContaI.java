package banco.modelo;

/**
 * Interface que define o contrato básico para qualquer tipo de conta bancária.
 */
public interface ContaI {
    /**
     * Realiza a operação de depósito na conta.
     * @param valor O valor a ser depositado (deve ser positivo).
     * @return true se o depósito foi realizado com sucesso, false caso contrário.
     */
    public boolean deposita(double valor);
    
    /**
     * Realiza a operação de saque na conta.
     * @param valor O valor a ser sacado (deve ser positivo).
     * @return true se o saque foi realizado com sucesso, false caso contrário.
     */
    public boolean saca(double valor);
    
    /**
     * Retorna o objeto Cliente dono da conta.
     * @return O Cliente.
     */
    public Cliente getDono();
    
    /**
     * Retorna o número único da conta.
     * @return O número da conta.
     */
    public int getNumero();
    
    /**
     * Retorna o saldo atual da conta.
     * @return O saldo.
     */
    public double getSaldo();
    
    /**
     * Aplica a remuneração (juros/rendimento) específica do tipo de conta.
     */
    public void remunera();
}