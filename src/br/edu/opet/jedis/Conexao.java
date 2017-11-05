package br.edu.opet.jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import br.edu.opet.util.ExceptionUtil;
import redis.clients.jedis.Jedis;

public class Conexao
{
    // Atributos estáticos
    private static String sUrl;
    private static int    sPorta;

    // Métodos estáticos
    public static void main(String[] args)
    {
        System.out.println();
        System.out.println("Obtendo a conexão");
        Jedis tJedis = getConexao();

        System.out.println();
        System.out.println("Fazendo ping com o servidor");
        System.out.println(tJedis.ping());

        System.out.println();
        System.out.println("Fechando a conexão");
        tJedis.disconnect();
    }

    // Métodos de classe
    public static Jedis getConexao()
    {
        try
        {
            // Caso a conexão já exista, verifica se está aberta e retorna essa conexão
            if (sUrl == null)
            {
                // Carregando o arquivo de configuração do JDBC para as propriedades
                InputStream tArqEntrada = Conexao.class.getResourceAsStream("jedis.properties");

                if (tArqEntrada == null)
                    throw new IOException("Arquivo de configuração 'jedis.properties' não existe no diretório do pacote");
                Properties tPropriedades = new Properties();
                tPropriedades.load(tArqEntrada);
                tArqEntrada.close();

                // Recuperando as propriedades do arquivo e validando se estão ok
                sUrl = tPropriedades.getProperty("url");
                if (sUrl == null || sUrl.isEmpty())
                    throw new InvalidPropertiesFormatException("Propriedade 'url' não existe ou em branco no arquivo 'jedis.properties'");
                String tPortaStr = tPropriedades.getProperty("porta");
                if (tPortaStr == null || tPortaStr.isEmpty())
                    throw new InvalidPropertiesFormatException("Propriedade 'porta' não existe ou em branco no arquivo 'jedis.properties'");

                sPorta = Integer.parseInt(tPortaStr);
            }

            return new Jedis(sUrl, sPorta);
        }
        catch (NumberFormatException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Propriedade 'porta' informada não é numérica no arquivo 'jedis.properties'");
            System.exit(9);
        }
        catch (RuntimeException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Erro de Conexão");
            System.exit(9);
        }
        catch (IOException tExcept)
        {
            ExceptionUtil.mostrarErro(tExcept, "Problemas na configuração do arquivo 'jedis.properties'");
            System.exit(9);
        }

        return null;
    }
}
