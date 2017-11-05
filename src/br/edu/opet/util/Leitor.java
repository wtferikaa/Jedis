/*
 * Leitor.java
 *
 * Classe utilit�ria para realizar leitura de diversos tipos de dados da console.
 *
 * � 1997 - Cay Horstmann - Todos os direitos reservados.
 *
 * Hist�rico
 * 11/03/1997 � Vers�o 1.0 - Cay Hostaman � Cria��o do arquivo
 * 10/02/2005 - Vers�o 2.0 - Jos� Augusto - Adapta��o e cia��o de m�todos novos
 * 09/01/2013 - Vers�o 3.0 - Jos� Augusto - Cria��o de leitura de enumerados
 * 28/02/2016 - Versao 3.1 - Jos� Augusto - Organiza��o do c�digo devido a indicativos do Sonar
 * 13/05/2016 - Versao 3.2 - Jos� Augusto - Cria��o de leitura para LocalDate
 *
 */
package br.edu.opet.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An easy interface to read numbers, chars and strings from standard input
 *
 * @version 1.10 10 Mar 1997
 * @author Cay Horstmann
 *
 *         Alterado e complementado por Jos� Augusto Martins Nieviadonski.
 *
 *         Permitido a c�pia e uso dessa classe desde que mantido essa mensagem de copyrigth.
 */

public class Leitor
{
    /* Atributos est�ticos */
    private static final Logger LOGGER                    = Logger.getLogger(Leitor.class.getName());
    private static final String TIPO_BYTE                 = "'byte'";
    private static final String TIPO_SHORT                = "'short'";
    private static final String TIPO_INT                  = "'int'";
    private static final String TIPO_LONG                 = "'long'";
    private static final String TIPO_FLOAT                = "'float'";
    private static final String TIPO_DOUBLE               = "'double'";
    private static final String TIPO_BOOLEAN              = "'boolean'";
    private static final String TIPO_CHAR                 = "'char'";
    private static final String TIPO_STRING               = "'String'";
    private static final String TIPO_BIGDECIMAL           = "'BigDecimal'";
    private static final String TIPO_DATE                 = "'Date'";
    private static final String FALSE_DEFAULT             = " ('false' default) : ";
    private static final String CHAR_DEFAULT              = " ('\0' default) : ";
    private static final String STRING_DEFAULT            = " (\"\" default) : ";
    private static final String ZERO_DEFAULT              = " (0 default) : ";
    private static final String DEFAULT                   = " default) : ";
    private static final String VALOR_NAO_PERTENCE        = "O valor n�o pertence a um dos valores aceitos";
    private static final String DIGITE_NOVAMENTE          = "Digite novamente...";
    private static final String VALOR_INVALIDO            = "Valor inv�lido para o tipo ";
    private static final String ENTRE_VALOR               = "Entre com um valor do tipo ";
    private static final String MASCARA_PADRAO_DATA       = "dd'/'MM'/'yyyy";
    private static final String MASCARA_PADRAO_DATA_LOCAL = "dd/MM/uuuu";

    /* Construtores da classe */
    private Leitor()
    {
        // Construtor privado para evitar instancia��o
    }

    /* M�todos de classe */
    private static void printPrompt(String pPrompt)
    {
        System.out.print(pPrompt);
        System.out.flush();
    }

    private static void printPromptLn(String pPrompt)
    {
        System.out.println(pPrompt);
        System.out.flush();
    }

    private static String readLine()
    {
        try
        {
            StringBuilder tLinha = new StringBuilder();

            while (true)
            {
                int tCh = System.in.read();
                if (tCh < 0 || (char) tCh == '\n')
                    break;
                if ((char) tCh != '\r')
                    tLinha.append((char) tCh);
            }
            return tLinha.toString();
        }
        catch (IOException tExcept)
        {
            LOGGER.log(Level.SEVERE, VALOR_INVALIDO, tExcept);
            return null;
        }
    }

    private static byte ajustaByte(long pValor)
    {
        if (pValor < Byte.MIN_VALUE)
            return Byte.MIN_VALUE;

        if (pValor > Byte.MAX_VALUE)
            return Byte.MAX_VALUE;

        return (byte) pValor;
    }

    private static short ajustaShort(long pValor)
    {
        if (pValor < Short.MIN_VALUE)
            return Short.MIN_VALUE;

        if (pValor > Short.MAX_VALUE)
            return Short.MAX_VALUE;

        return (short) pValor;
    }

    private static int ajustaInt(long pValor)
    {
        if (pValor < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;

        if (pValor > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;

        return (int) pValor;
    }

    private static float ajustaFloat(double pValor)
    {
        if (pValor < Float.MIN_VALUE)
            return Float.MIN_VALUE;

        if (pValor > Float.MAX_VALUE)
            return Float.MAX_VALUE;

        return (float) pValor;
    }

    public static byte readByte()
    {
        return readByte(ENTRE_VALOR + TIPO_BYTE + ZERO_DEFAULT, 0);
    }

    public static byte readByte(long pValor)
    {
        byte tVlr = ajustaByte(pValor);

        return readByte(ENTRE_VALOR + TIPO_BYTE + " (" + tVlr + DEFAULT, tVlr);
    }

    public static byte readByte(String pPrompt)
    {
        return readByte(pPrompt, 0);
    }

    public static byte readByte(String pPrompt, long pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaByte(pValor);
                return Byte.parseByte(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_BYTE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static byte readByte(byte[] pValores)
    {
        return readByte(ENTRE_VALOR + TIPO_BYTE + ZERO_DEFAULT, 0, pValores);
    }

    public static byte readByte(long pValor, byte[] pValores)
    {
        byte tVlr = ajustaByte(pValor);

        return readByte(ENTRE_VALOR + TIPO_BYTE + " (" + tVlr + DEFAULT, tVlr, pValores);
    }

    public static byte readByte(String pPrompt, byte[] pValores)
    {
        return readByte(pPrompt, 0, pValores);
    }

    public static byte readByte(String pPrompt, long pValor, byte[] pValores)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaByte(pValor);
                byte tValor = Byte.parseByte(tLinha);
                for (byte tValorValido : pValores)
                    if (tValorValido == tValor)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_BYTE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static short readShort()
    {
        return readShort(ENTRE_VALOR + TIPO_SHORT + ZERO_DEFAULT, 0);
    }

    public static short readShort(long pValor)
    {
        short tVlr = ajustaShort(pValor);

        return readShort(ENTRE_VALOR + TIPO_SHORT + " (" + tVlr + DEFAULT, tVlr);
    }

    public static short readShort(String pPrompt)
    {
        return readShort(pPrompt, 0);
    }

    public static short readShort(String pPrompt, long pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaShort(pValor);
                return Short.parseShort(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_SHORT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static short readShort(short[] pValores)
    {
        return readShort(ENTRE_VALOR + TIPO_SHORT + ZERO_DEFAULT, 0, pValores);
    }

    public static short readShort(long pValor, short[] pValores)
    {
        short tVlr = ajustaShort(pValor);

        return readShort(ENTRE_VALOR + TIPO_SHORT + " (" + tVlr + DEFAULT, tVlr, pValores);
    }

    public static short readShort(String pPrompt, short[] pValores)
    {
        return readShort(pPrompt, 0, pValores);
    }

    public static short readShort(String pPrompt, long pValor, short[] pValores)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaShort(pValor);
                short tValor = Short.parseShort(tLinha);
                for (short tValorValido : pValores)
                    if (tValorValido == tValor)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_SHORT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static int readInt()
    {
        return readInt(ENTRE_VALOR + TIPO_INT + ZERO_DEFAULT, 0);
    }

    public static int readInt(long pValor)
    {
        int tVlr = ajustaInt(pValor);

        return readInt(ENTRE_VALOR + TIPO_INT + " (" + tVlr + DEFAULT, tVlr);
    }

    public static int readInt(String pPrompt)
    {
        return readInt(pPrompt, 0);
    }

    public static int readInt(String pPrompt, long pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaInt(pValor);
                return Integer.parseInt(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_INT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static int readInt(int[] pValores)
    {
        return readInt(ENTRE_VALOR + TIPO_INT + ZERO_DEFAULT, 0, pValores);
    }

    public static int readInt(long pValor, int[] pValores)
    {
        int tVlr = ajustaInt(pValor);

        return readInt(ENTRE_VALOR + TIPO_INT + " (" + tVlr + DEFAULT, tVlr, pValores);
    }

    public static int readInt(String pPrompt, int[] pValores)
    {
        return readInt(pPrompt, 0, pValores);
    }

    public static int readInt(String pPrompt, long pValor, int[] pValores)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaInt(pValor);
                int tValor = Integer.parseInt(tLinha);
                for (int tValorValido : pValores)
                    if (tValorValido == tValor)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_INT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static long readLong()
    {
        return readLong(ENTRE_VALOR + TIPO_LONG + ZERO_DEFAULT, 0);
    }

    public static long readLong(long pValor)
    {
        return readLong(ENTRE_VALOR + TIPO_LONG + " (" + pValor + DEFAULT, pValor);
    }

    public static long readLong(String pPrompt)
    {
        return readLong(pPrompt, 0);
    }

    public static long readLong(String pPrompt, long pValor)
    {

        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                return Long.parseLong(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_LONG);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static long readLong(long[] pValores)
    {
        return readLong(ENTRE_VALOR + TIPO_LONG + ZERO_DEFAULT, 0, pValores);
    }

    public static long readLong(long pValor, long[] pValores)
    {
        return readLong(ENTRE_VALOR + TIPO_LONG + " (" + pValor + DEFAULT, pValor, pValores);
    }

    public static long readLong(String pPrompt, long[] pValores)
    {
        return readLong(pPrompt, 0, pValores);
    }

    public static long readLong(String pPrompt, long pValor, long[] pValores)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                long tValor = Long.parseLong(tLinha);
                for (long tValorValido : pValores)
                    if (tValorValido == tValor)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_LONG);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static float readFloat()
    {
        return readFloat(ENTRE_VALOR + TIPO_FLOAT + ZERO_DEFAULT, 0);
    }

    public static float readFloat(double pValor)
    {
        float tVlr = ajustaFloat(pValor);

        return readFloat(ENTRE_VALOR + TIPO_FLOAT + " (" + tVlr + DEFAULT, tVlr);
    }

    public static float readFloat(String pPrompt)
    {
        return readFloat(pPrompt, 0);
    }

    public static float readFloat(String pPrompt, double pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaFloat(pValor);
                return Float.parseFloat(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_FLOAT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static float readFloat(float[] pValores)
    {
        return readFloat(ENTRE_VALOR + TIPO_FLOAT + ZERO_DEFAULT, 0, pValores);
    }

    public static float readFloat(double pValor, float[] pValores)
    {
        float tVlr = ajustaFloat(pValor);

        return readFloat(ENTRE_VALOR + TIPO_FLOAT + " (" + tVlr + DEFAULT, tVlr, pValores);
    }

    public static float readFloat(String pPrompt, float[] pValores)
    {
        return readFloat(pPrompt, 0, pValores);
    }

    public static float readFloat(String pPrompt, float pValor, float[] pValores)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return ajustaFloat(pValor);
                float tValor = Float.parseFloat(tLinha);
                for (float tValorValido : pValores)
                    if (Float.compare(tValorValido, tValor) == 0)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_FLOAT);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static double readDouble()
    {
        return readDouble(ENTRE_VALOR + TIPO_DOUBLE + ZERO_DEFAULT, 0);
    }

    public static double readDouble(double pValor)
    {
        return readDouble(ENTRE_VALOR + TIPO_DOUBLE + " (" + pValor + DEFAULT, pValor);
    }

    public static double readDouble(String pPrompt)
    {
        return readDouble(pPrompt, 0);
    }

    public static double readDouble(String pPrompt, double pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                return Double.parseDouble(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_DOUBLE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static double readDouble(double[] pValores)
    {
        return readDouble(ENTRE_VALOR + TIPO_DOUBLE + ZERO_DEFAULT, 0, pValores);
    }

    public static double readDouble(double pValor, double[] pValores)
    {
        return readDouble(ENTRE_VALOR + TIPO_DOUBLE + " (" + pValor + DEFAULT, pValor, pValores);
    }

    public static double readDouble(String pPrompt, double[] pValores)
    {
        return readDouble(pPrompt, 0, pValores);
    }

    public static double readDouble(String pPrompt, double pValor, double[] pValores)
    {
        while (true)
        {
            try
            {
                double tValor = readDouble(pPrompt, pValor);
                for (double tValorValido : pValores)
                    if (Double.compare(tValorValido, tValor) == 0)
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_DOUBLE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static Number readNumber(Class<? extends Number> pTipo, Number pValor)
    {
        return readNumber(pTipo, ENTRE_VALOR + pTipo.getSimpleName() + " (" + pValor + DEFAULT, pValor);
    }

    public static Number readNumber(Class<? extends Number> pTipo, String pPrompt, Number pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                if (pTipo == Byte.class)
                    return Byte.valueOf(tLinha);
                if (pTipo == Short.class)
                    return Short.valueOf(tLinha);
                if (pTipo ==  Integer.class)
                    return Integer.valueOf(tLinha);
                if (pTipo ==  Long.class)
                    return Long.valueOf(tLinha);
                if (pTipo ==  Float.class)
                    return Float.valueOf(tLinha);
                if (pTipo == Double.class)
                    return Double.valueOf(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + pValor.getClass().getSimpleName());
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static Number readNumber(Class<? extends Number> pTipo, Number[] pValores)
    {
        return readNumber(pTipo, ENTRE_VALOR + pTipo.getSimpleName() + ZERO_DEFAULT, null, pValores);
    }

    public static Number readNumber(Class<? extends Number> pTipo, Number pValor, Number[] pValores)
    {
        return readNumber(pTipo, ENTRE_VALOR + pTipo.getSimpleName() + " (" + pValor + DEFAULT, pValor, pValores);
    }

    public static Number readNumber(Class<? extends Number> pTipo, String pPrompt, Number[] pValores)
    {
        return readNumber(pTipo, pPrompt, null, pValores);
    }

    public static Number readNumber(Class<? extends Number> pTipo, String pPrompt, Number pValor, Number[] pValores)
    {
        while (true)
        {
            try
            {
                Number tValor = readNumber(pTipo, pPrompt, pValor);
                for (Number tValorValido : pValores)
                    if (tValor.equals(tValorValido))
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + pValor.getClass().getSimpleName());
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static boolean readBoolean(String pPrompt, boolean pValor)
    {
        while (true)
        {
            printPrompt(pPrompt);
            String tLinha = readLine().trim();
            if (tLinha.isEmpty())
                return pValor;
            if ("true".equalsIgnoreCase(tLinha) || "t".equalsIgnoreCase(tLinha))
                return true;
            if ("false".equalsIgnoreCase(tLinha) || "f".equalsIgnoreCase(tLinha))
                return false;
            printPromptLn(VALOR_INVALIDO + TIPO_BOOLEAN);
            printPromptLn(DIGITE_NOVAMENTE);
        }
    }

    public static boolean readBoolean(String pPrompt)
    {
        return readBoolean(pPrompt, false);
    }

    public static boolean readBoolean(boolean pValor)
    {
        return readBoolean(ENTRE_VALOR + TIPO_BOOLEAN + " (" + pValor + DEFAULT, pValor);
    }

    public static boolean readBoolean()
    {
        return readBoolean(ENTRE_VALOR + TIPO_BOOLEAN + FALSE_DEFAULT, false);
    }

    public static char readChar()
    {
        return readChar(ENTRE_VALOR + TIPO_CHAR + CHAR_DEFAULT, '\0');
    }

    public static char readChar(char pCh)
    {
        return readChar(ENTRE_VALOR + TIPO_CHAR + " (" + pCh + DEFAULT, pCh);
    }

    public static char readChar(String pPrompt)
    {
        return readChar(pPrompt, '\0');
    }

    public static char readChar(String pPrompt, char pCh)
    {
        printPrompt(pPrompt);
        String tLinha = readLine().trim();
        if (tLinha.isEmpty())
            return pCh;
        return tLinha.charAt(0);
    }

    public static char readChar(char[] pValores)
    {
        return readChar(ENTRE_VALOR + TIPO_CHAR + CHAR_DEFAULT, '\0', pValores);
    }

    public static char readChar(char pCh, char[] pValores)
    {
        return readChar(ENTRE_VALOR + TIPO_CHAR + " (" + pCh + DEFAULT, pCh, pValores);
    }

    public static char readChar(String pPrompt, char[] pValores)
    {
        return readChar(pPrompt, '\0', pValores);
    }

    public static char readChar(String pPrompt, char pCh, char[] pValores)
    {

        while (true)
        {
            char tResult = readChar(pPrompt, pCh);

            for (char tValorValido : pValores)
                if (tValorValido == tResult)
                    return tResult;
            printPromptLn(VALOR_NAO_PERTENCE);
            printPromptLn(DIGITE_NOVAMENTE);
        }
    }

    public static String readString()
    {
        return readString(ENTRE_VALOR + TIPO_STRING + STRING_DEFAULT, "");
    }

    public static String readString(String pPrompt)
    {
        return readString(pPrompt, "");
    }

    public static String readString(String pPrompt, String pStr)
    {

        printPrompt(pPrompt);
        String tLinha = readLine();
        if (tLinha.isEmpty())
            return pStr;
        return tLinha;
    }

    public static String readString(String[] pValores)
    {
        return readString(ENTRE_VALOR + TIPO_STRING + STRING_DEFAULT, "", pValores);
    }

    public static String readString(String pPrompt, String[] pValores)
    {
        return readString(pPrompt, "", pValores);
    }

    public static String readString(String pPrompt, String pStr, String[] pValores)
    {
        while (true)
        {
            String tLinha = readString(pPrompt, pStr);
            for (String tValorValido : pValores)
                if (tValorValido.equals(tLinha))
                    return tLinha;
            printPromptLn(VALOR_NAO_PERTENCE);
            printPromptLn(DIGITE_NOVAMENTE);
        }
    }

    public static BigDecimal readBigDecimal()
    {
        return readBigDecimal(ENTRE_VALOR + TIPO_BIGDECIMAL + ZERO_DEFAULT, BigDecimal.ZERO);
    }

    public static BigDecimal readBigDecimal(BigDecimal pValor)
    {
        return readBigDecimal(ENTRE_VALOR + TIPO_BIGDECIMAL + " (" + pValor + DEFAULT, pValor);
    }

    public static BigDecimal readBigDecimal(String pPrompt)
    {
        return readBigDecimal(pPrompt, BigDecimal.ZERO);
    }

    public static BigDecimal readBigDecimal(String pPrompt, BigDecimal pValor)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                return new BigDecimal(tLinha);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_BIGDECIMAL);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static BigDecimal readBigDecimal(BigDecimal[] pValores)
    {
        return readBigDecimal(ENTRE_VALOR + TIPO_BIGDECIMAL + ZERO_DEFAULT, BigDecimal.ZERO, pValores);
    }

    public static BigDecimal readBigDecimal(String pPrompt, BigDecimal[] pValores)
    {
        return readBigDecimal(pPrompt, BigDecimal.ZERO, pValores);
    }

    public static BigDecimal readBigDecimal(String pPrompt, BigDecimal pValor, BigDecimal[] pValores)
    {
        while (true)
        {
            try
            {
                BigDecimal tValor = readBigDecimal(pPrompt, pValor);
                for (BigDecimal tValorValido : pValores)
                    if (tValorValido.equals(tValor))
                        return tValor;
                printPromptLn(VALOR_NAO_PERTENCE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
            catch (NumberFormatException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_BIGDECIMAL);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static Date readDate()
    {
        return readDate(new Date(), MASCARA_PADRAO_DATA);
    }

    public static Date readDate(Date pValor)
    {
        return readDate(pValor, MASCARA_PADRAO_DATA);
    }

    public static Date readDate(String pPrompt)
    {
        return readDate(pPrompt, new Date(), MASCARA_PADRAO_DATA);
    }

    public static Date readDate(String pPrompt, Date pValor)
    {
        return readDate(pPrompt, pValor, MASCARA_PADRAO_DATA);
    }

    public static Date readDate(Date pValor, String pFormato)
    {
        SimpleDateFormat tFormatador = new SimpleDateFormat(pFormato);
        return readDate(ENTRE_VALOR + TIPO_DATE + " (" + tFormatador.format(pValor) + DEFAULT, pValor, pFormato);
    }

    public static Date readDate(String pPrompt, String pFormato)
    {
        return readDate(pPrompt, new Date(), pFormato);
    }

    public static Date readDate(String pPrompt, Date pValor, String pFormato)
    {
        SimpleDateFormat tFormatador = new SimpleDateFormat(pFormato);
        tFormatador.setLenient(false);
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                return tFormatador.parse(tLinha);
            }
            catch (ParseException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_DATE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static LocalDate readLocalDate()
    {
        return readLocalDate(LocalDate.now(), MASCARA_PADRAO_DATA_LOCAL);
    }

    public static LocalDate readLocalDate(LocalDate pValor)
    {
        return readLocalDate(pValor, MASCARA_PADRAO_DATA_LOCAL);
    }

    public static LocalDate readLocalDate(String pPrompt)
    {
        return readLocalDate(pPrompt, LocalDate.now(), MASCARA_PADRAO_DATA_LOCAL);
    }

    public static LocalDate readLocalDate(String pPrompt, LocalDate pValor)
    {
        return readLocalDate(pPrompt, pValor, MASCARA_PADRAO_DATA_LOCAL);
    }

    public static LocalDate readLocalDate(LocalDate pValor, String pFormato)
    {
        DateTimeFormatter tFormatador = DateTimeFormatter.ofPattern(pFormato);
        return readLocalDate(ENTRE_VALOR + TIPO_DATE + " (" + tFormatador.format(pValor) + DEFAULT, pValor, pFormato);
    }

    public static LocalDate readLocalDate(String pPrompt, String pFormato)
    {
        return readLocalDate(pPrompt, LocalDate.now(), pFormato);
    }

    public static LocalDate readLocalDate(String pPrompt, LocalDate pValor, String pFormato)
    {
        DateTimeFormatter tFormatador = DateTimeFormatter.ofPattern(pFormato).withResolverStyle(ResolverStyle.STRICT);
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (tLinha.isEmpty())
                    return pValor;
                return LocalDate.parse(tLinha, tFormatador);
            }
            catch (DateTimeParseException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + TIPO_DATE);
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(String pPrompt, E pEnum, boolean pNoCaseSensitive)
    {
        while (true)
        {
            try
            {
                printPrompt(pPrompt);
                String tLinha = readLine().trim();
                if (pNoCaseSensitive)
                    tLinha = tLinha.toUpperCase();
                if (tLinha.isEmpty())
                    return pEnum;
                return Enum.valueOf(pEnum.getDeclaringClass(), tLinha);
            }
            catch (IllegalArgumentException tExcept)
            {
                printPromptLn(VALOR_INVALIDO + pEnum.getDeclaringClass().getSimpleName());
                printPromptLn(DIGITE_NOVAMENTE);
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(String pPrompt, E pEnum)
    {
        return readEnum(pPrompt, pEnum, true);
    }
}
