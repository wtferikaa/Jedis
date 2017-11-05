package br.edu.opet.jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import br.edu.opet.util.ExceptionUtil;
import redis.clients.jedis.Jedis;

public class Conexao
{
    // Atributos est�ticos
    private static String sUrl;
    private static int    sPorta;

    // M�todos est�ticos
    public static void main(String[] args)
    {
        System.out.println();
        System.out.println("Obtendo a conex�o");
        Jedis tJedis = getConexao();

        System.out.println();
        System.out.println("Fazendo ping com o servidor");
        System.out.println(tJedis.ping());

        System.out.println();
        System.out.println("Fechando a conex�o");
        tJedis.disconnect();
    }

    // M�todos de classe
    public static Jedis getConexao()
    {
        try
        {
            // Caso a conex�o j� exista, verifica se est� aberta e retorna essa conex�o
            if (sUrl == null)
            {
                // Carregando o arquivo de configura��o do JDBC para as propriedades
                InputStream tArqEntrada = Conexao.class.getResourceAsStream("jedis.properties");

                if (tArqEntrada == null)
                    throw new IOException("Arquivo de configura��o 'jedis.properties' n�o existe no diret�rio do pacote");
                Properties tPropriedades = new Properties();
                tPropriedades.load(tArqEntrada);
                tArqEntrada.close();

                // Recuperando as propriedades do arquivo e validando se est�o ok
                sUrl = tPropriedades.getProperty("url");
                if (sUrl == null || sUrl.isEmpty())
                    throw new InvalidPropertiesFormatException("Propriedade 'url' n�o existe ou em branco no arquivo 'jedis.properties'");
                String tPortaStr = tPropriedades.getProperty("porta");
                if (tPortaStr == null || tPortaStr.isEmpty())
                    throw new InvalidPropertiesFormatException("Propriedade 'porta' n�o existe ou em branco no arquivo 'jedis.properties'");

                sPorta = Integer.parseInt(tPortaStr);
            }

            return new Jedis(sUrl, sPorta);
        }
        catch (NumberFormatException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Propriedade 'porta' informada n�o � num�rica no arquivo 'jedis.properties'");
            System.exit(9);
        }
        catch (RuntimeException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Erro de Conex�o");
            System.exit(9);
        }
        catch (IOException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Problemas na configura��o do arquivo 'jedis.properties'");
            System.exit(9);
        }

        return null;
    }
}
