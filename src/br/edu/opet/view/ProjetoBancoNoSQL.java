package br.edu.opet.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


import br.edu.opet.util.Leitor;
import redis.clients.jedis.Jedis;

public class ProjetoBancoNoSQL {

	private static DateTimeFormatter sFormatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static DateTimeFormatter sFormatadorDataeHora = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

	public static void main(String[] args) {
		// Obter a conexão
		Jedis jedis = new Jedis("localhost");
		System.out.println("Connecção com o servidor");
		// Testando se o servidor está execuando
		System.out.println("Servidor está executando: " + jedis.ping());

		Jedis tJedis = new Jedis();
		menu1: while (true) {
			System.out.println("MENU");
			System.out.println("1- Cadastrar usuário");
			System.out.println("2- Enviar mensagem");
			System.out.println("4 - Sair");

			int tOpcao = Leitor.readInt("Entre com a opção desejada:");

			if (tOpcao == 4)
				break;

			opcao: switch (tOpcao) {
			case 1: // Criando o novo usuario
				LocalDate tDataCadastro = LocalDate.now();
				String tApelido = Leitor.readString("Apelido:");
				String tNome = Leitor.readString("Nome:");
				if (tNome == "") {
					break opcao;
				}

				while (true) {
					
					if (tApelido == "") {
						break opcao;
					}

					String tApelidoBd1 = tJedis.hget("Usuário:" + tApelido, "Apelido");

					if (!(tApelidoBd1 == null)) {
						System.out.println("Este apelido já existe, digite novamente");
					}

					tJedis.hset("Usuario:" + tApelido, "Nome", tNome);
					tJedis.hset("Usuario:" + tApelido, "Apelido", tApelido);
					tJedis.hset("Usuario:" + tApelido, "DataCadastro", tDataCadastro.format(sFormatador));

					break opcao;
				}
				
			

			case 2:// enviando mensagem e chorando
				while (true) {
					System.out.println();
					System.out.println("Menu de mensagens");
					String tApelido1 = Leitor.readString("Apelido:");
					if (tApelido1 == "") {
						break opcao;
					}
					else {
						
					

					String tApelidoBd = tJedis.hget("Usuario:" + tApelido1, "Apelido");
					if (!(tApelidoBd.equals(tApelido1))) {
						System.out.println("Apelido inválido");
					}

					else {
						while (true) {

							System.out.println();
							System.out.println("1 - Enviar Mensagem");
							System.out.println("2 - Visualizar mensagens");
							System.out.println("3 - Visualizar dados");
							System.out.println("5 - Encerrar sessão");
							System.out.println();

							int tOpcao2 = Leitor.readInt("Entre com a opção desejada: ");

							opcao2: switch (tOpcao2) {

							case 1:

								LocalDateTime tDataMensagem = LocalDateTime.now();

								System.out.println();
								String tPara = Leitor.readString("Para:");

								if (tPara == "") {
									break opcao2;
								}

								String tMensagem = Leitor.readString("Mensagem:");

								if (tMensagem == "") {
									break opcao2;
								}

								tJedis.sadd(tApelido1 + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":Para",
										tPara);
								tJedis.set(tApelido1 + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":De",
										tApelido1);
								tJedis.set(tApelido1 + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":Mensagem",
										tMensagem);

								Long increm = tJedis.incr(tApelido1);
								tJedis.zadd(tApelido1, increm,
										tApelido1 + ":" + tDataMensagem.format(sFormatadorDataeHora));
								String[] separar = tPara.split(",");
								for (int i = 0; i < separar.length; i++) {

									Long tEntrada = tJedis.incr(separar[i] + ":entrada");
									System.out.println(separar[i] + ":entrada " + tEntrada + " " + tApelido1 + ":"
											+ tDataMensagem.format(sFormatadorDataeHora));
									tJedis.zadd(separar[i] + ":entrada", tEntrada,
											tApelido1 + ":" + tDataMensagem.format(sFormatadorDataeHora));
								}
							
								break opcao2;
								
							case 3:
								System.out.println("Visualização dos dados");
								System.out.println();
								System.out.println("Nome: " + tJedis.hget("Usuario:" + tApelido1, "Nome" ));
								System.out.println("Apelido: " + tJedis.hget("Usuario:" + tApelido1, "Apelido" ));
								System.out.println("DataCadastro: " + tJedis.hget("Usuario:" + tApelido1, "DataCadastro" ));
							
								
								break opcao2;

							case 5:
								break menu1;

							}
						}
					}
				}
			}
		}
	}
	
			jedis.close();
		
	}
}
