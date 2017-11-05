/*
 * ExceptionUtil.java
 *
 * Classe utilit�ria para mostrar a lista de m�todos de uma exce��o (StrackStrace)
 * na console, dando tratamento especial caso a exce��o seja do tipo SQLException.
 *
 * � 2016 - Faculdades Opet - Todos os direitos reservados.
 *
 * Hist�rico
 * 14/07/2016 � Vers�o 1.0 - Jos� Augusto � Cria��o do arquivo
 *
 */
package br.edu.opet.util;

import java.sql.DriverManager;
import java.sql.SQLException;

public class ExceptionUtil
{

    // M�todo utilit�rio que recebe qualquer exce��o e mostra o dados da
    // mesma de forma formatada.
    public static void mostrarErro(Exception pExcept, String pMsg)
    {
        // Mostrar os dados b�sicos como nome da exce��o e mensagem
        System.out.println();
        System.out.println(pMsg);
        System.out.println("Exce��o....: " + pExcept.getClass().getName());
        System.out.println("Mensagem...: " + pExcept.getMessage());

        // Caso seja uma exce��o SQLException, mosra os dados da exce��o do banco de dados
        if (pExcept instanceof SQLException)
        {
            SQLException tExcept = (SQLException) pExcept;
            System.out.println("SQLState...:" + tExcept.getSQLState());
            System.out.println("Error Code.:" + tExcept.getErrorCode());
            DriverManager.println("SQLState...:" + tExcept.getSQLState());
            DriverManager.println("Error Code.:" + tExcept.getErrorCode());
        }

        // Processa a lista de exce��es ligadas, se houver, mostrando a lista
        Throwable tCausa = pExcept.getCause();
        while (tCausa != null)
        {
            System.out.println("Causa.....: " + tCausa.getMessage());
            tCausa = tCausa.getCause();
        }

        // Mostra na console o StackTrace da exce��o
        System.out.println("Pilha de execu��o");
        pExcept.printStackTrace(System.out);
    }
}
