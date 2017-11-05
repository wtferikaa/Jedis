package br.edu.opet.view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import br.edu.opet.jedis.Conexao;
import br.edu.opet.util.Leitor;
import redis.clients.jedis.Jedis;

public class ProjetoBancoNoSQL {
	
	private static DateTimeFormatter sFormatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static DateTimeFormatter sFormatadorDataeHora = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");


	public static void main(String[] args) {
	// Obter a conexão
    Jedis tConexao = Conexao.getConexao();

	
    Jedis tJedis = new Jedis();
		menu1: while (true) {
			System.out.println("MENU");
			System.out.println("1- Cadastrar usuário");
			System.out.println("2- Enviar mensagem");
			System.out.println("4 - Sair");

			int tOpcao = Leitor.readInt("Entre com a opção desejada: ");

			if (tOpcao == 4)
				break;

			opcao: switch (tOpcao) {
			case 1: //Criando o novo usuario
				LocalDate tDataCadastro = LocalDate.now();
				
				String tNome = Leitor.readString("Nome :");
				if (tNome == "") {
					break opcao;
				}
				
				while (true) {
					String tApelido = Leitor.readString("Apelido:");
					if (tApelido == "") {
						break opcao;
					}
				
				String tApelidoArmazenado = tJedis.hget("Usuário: " + tApelido, "Apelido");
				
				if(!(tApelidoArmazenado == null)) {
					System.out.println("Este apelido já existe, digite novamente");
				}
				else {
					
				}
				
				tJedis.hset("Usuario:" + tApelido, "Nome:", tNome);
				tJedis.hset("Usuario:" + tApelido, "Apelido:", tApelido);
				tJedis.hset("Usuario:"+ tApelido, "DataCadastro", tDataCadastro.format(sFormatador));

						break opcao;
					}
				}
		}
	}
}
				
				
				
				
			