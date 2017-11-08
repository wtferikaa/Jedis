package br.edu.opet.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

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
			System.out.println("2- Login");
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
					System.out.println("Usuário cadastrado com sucesso");

					break opcao;
				}

			case 2:// enviando mensagem e chorando
				while (true) {
					System.out.println();
					System.out.println("logar no sistema");
					// definindo a variavel
					String tApelidoInformado = Leitor.readString("Apelido:");
					// se for vazio, volta para o menu inicial
					if (tApelidoInformado == "") {
						break opcao;
					}
					// definindo a variavel que irá buscar o apelido que o usuário digitou no banco
					// de dados
					String tApelidoB = tJedis.hget("Usuario:" + tApelidoInformado, "Apelido");

					// se o apelido do banco estiver nulo ou o apelido que foi informado for
					// diferente do que está no banco, retorna mensagem de erro
					if ((tApelidoB == null || !tApelidoB.equals(tApelidoInformado))) {
						System.out.println("Apelido inválido");
					}

					else {
						menu2: while (true) {

							System.out.println();
							System.out.println("1 - Enviar Mensagem");
							System.out.println("2 - Visualizar mensagens recebidas");
							System.out.println("3 - Visualizar mensagens enviadas");
							System.out.println("4 - Visualizar dados do usuário");
							System.out.println("5 - Encerrar sessão");
							System.out.println();

							int tOpcao2 = Leitor.readInt("Entre com a opção desejada: ");

							opcao2: switch (tOpcao2) {

							case 1:

								LocalDateTime tDataMensagem = LocalDateTime.now();

								System.out.println();
								String tDestinatario = Leitor.readString("Para:");
								// Destinatario não pode ser vazia
								if (tDestinatario == "") {
									break opcao2;
								}

								String tMensagem = Leitor.readString("Mensagem:");

								// a mensagem não pode ser vazia
								if (tMensagem == "") {
									break opcao2;
								}

								// montando a chave e valor
								tJedis.sadd(
										tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":Para",
										tDestinatario);

								tJedis.set(tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":De",
										tApelidoInformado);

								tJedis.set(tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora)
										+ ":Mensagem", tMensagem);

								// incrementando
								Long tSaida = tJedis.incr(tApelidoInformado + "--saida");

								tJedis.zadd(tApelidoInformado + "--saida ", tSaida, tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));

								String[] SepararTexto = tDestinatario.split(",");
								for (int i = 0; i < SepararTexto.length; i++) {
									
									Long tEntrada = tJedis.incr(SepararTexto[i] + "--ent  ");

									//System.out.println(SepararTexto[i] + "-entrada  " + tEntrada + ":"+ tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));

									tJedis.zadd(SepararTexto[i] + "--ent", tEntrada, tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));
								}

								break opcao2;

							case 2:
								System.out.println("Visualizar mensagens recebidas");

								// zlexcount pois o count normal não está funcionando
								Long contarEntrada = tJedis.zlexcount(tApelidoInformado + "--ent ", "-", "+");
								System.out.println("Você tem " + contarEntrada + " mensagens!");

								if (contarEntrada == 0) {

									break opcao2;
								} else {
									for (int i = 0; i < contarEntrada; i++) {
										System.out.println(
												(1 + i) + " " + tJedis.zrange(tApelidoInformado + "--ent  ", i, i));
									}
									System.out.println();

									// variavel para ver mensagem
									Long tVisualizar = Leitor.readLong("Visualisar mensagem:");
									if (tVisualizar == 0) {
										break opcao2;
									} else {
										Set<String> tVerMensagem = tJedis.zrange(
												tApelidoInformado + "--ent  ", tVisualizar - 1, tVisualizar - 1);

										String cortarString = tVerMensagem.toString();
										String tVerMensagem2 = cortarString.substring(1, cortarString.length() - 1);

										System.out.println(tJedis.get(tVerMensagem2 + ":De") + ": "
												+ tJedis.get(tVerMensagem2 + ":Mensagem"));
										System.out.println();

										// definindo a variavel
										String tResposta = Leitor.readString("Resposta:");
										// a resposta não deve ser vazia
										if (tResposta == "") {
											break opcao2;

										} else {
											LocalDateTime tDataMensagem2 = LocalDateTime.now();

											Long tEntradaResposta = tJedis.incr(tVerMensagem2 + "-resp  ");

											tJedis.zadd(tVerMensagem2 + "--resp  ", tEntradaResposta,
													tApelidoInformado + ":"
															+ tDataMensagem2.format(sFormatadorDataeHora));
											String tDestinatario2 = tJedis.get(tVerMensagem2 + ":De");

											tJedis.sadd(
													tApelidoInformado + ":"
															+ tDataMensagem2.format(sFormatadorDataeHora) + ":Para",
													tDestinatario2);
											tJedis.set(
													tApelidoInformado + ":"
															+ tDataMensagem2.format(sFormatadorDataeHora) + ":De",
													tApelidoInformado);
											tJedis.set(
													tApelidoInformado + ":"
															+ tDataMensagem2.format(sFormatadorDataeHora) + ":Mensagem",
													tResposta);

											Long tSaida2 = tJedis.incr(tApelidoInformado + "--saida");
											tJedis.zadd(tApelidoInformado + "--saida ", tSaida2, tApelidoInformado + ":"
													+ tDataMensagem2.format(sFormatadorDataeHora));

											Long tEntrada2 = tJedis.incr(tDestinatario2 + "-ent  ");

											tJedis.zadd(tDestinatario2 + "--ent  ", tEntrada2, tApelidoInformado
													+ ":" + tDataMensagem2.format(sFormatadorDataeHora));

										}
									}
								}

								break opcao2;

							case 3:
								System.out.println("Visualizar mensagens enviadas");

								Long contarSaida = tJedis.zlexcount(tApelidoInformado + "--saida ", "-", "+");
								System.out.println("Você enviou " + contarSaida + " mensagens!");

								if (contarSaida == 0) {

									break opcao2;
								} else {

									for (int i = 0; i < contarSaida; i++) {

										System.out.println(
												(1 + i) + " " + jedis.zrange(tApelidoInformado + "--saida ", i, i));
									}

									opcao3: while (true) {

										System.out.println();
										Long tVerSaida = Leitor.readLong("Visualisar mensagem:");

										if (tVerSaida == 0 || tVerSaida < 0) {
											break opcao2;
										} else {
											Set<String> tVerMensagem3 = tJedis.zrange(tApelidoInformado + "--saida ",
													tVerSaida - 1, tVerSaida - 1);

											String Cortar = (tVerMensagem3).toString();
											String tVerMensagem4 = Cortar.substring(1, Cortar.length() - 1);

											System.out.println("Para: " + tJedis.smembers(tVerMensagem4 + ":Para"));
											System.out.println(tJedis.get(tVerMensagem4 + ":Mensagem"));

											Long contarResposta = tJedis.zlexcount(tVerMensagem4 + "--resp ", "-",
													"+");
											System.out.println();
											System.out.println("Você possui " + contarResposta + " respostas.");

											if (contarResposta == 0) {
												break opcao3;
											} else {
												System.out.println("Respostas:");

												for (int i = 0; i < contarResposta; i++) {
													Set<String> tVerMensagem5 = tJedis
															.zrange(tVerMensagem4 + "--resp ", i, i);

													String Cortar2 = (tVerMensagem5.toString());
													String tTirarChave = Cortar2.substring(1, Cortar2.length() - 1);

													String tNome2 = tJedis.get(tTirarChave + ":De");
													System.out.println((1 + i) + "- " + tNome2);
												}
												System.out.println();
												Long tVerResposta = Leitor.readLong("Visualizar mensagem:");
												if (tVerResposta == 0 || tVerResposta < 0) {
													break opcao2;
												} else {
													Set<String> tVerMensagem6 = tJedis.zrange(
															tVerMensagem4 + "--resp  ", tVerResposta - 1,
															tVerResposta - 1);

													String Cortar3 = (tVerMensagem6).toString();
													String tTirarChave2 = Cortar3.substring(1, Cortar3.length() - 1);

													System.out.println(tJedis.get(tTirarChave2 + ":Mensagem"));

													String opf = Leitor.readString("Enter para Sair!");
													if (opf == "")
														break opcao3;
												}
											}
										}
									}
								}
								break opcao2;

							case 4:
								System.out.println("Visualização dos dados");
								System.out.println();
								System.out.println("Nome: " + tJedis.hget("Usuario:" + tApelidoInformado, "Nome"));
								System.out
										.println("Apelido: " + tJedis.hget("Usuario:" + tApelidoInformado, "Apelido"));
								System.out.println(
										"DataCadastro: " + tJedis.hget("Usuario:" + tApelidoInformado, "DataCadastro"));

								break opcao2;

							case 5:
								break opcao;

							}
						}
					}
				}

			case 3:
				break menu1;
			}
		}

		jedis.close();

	}
}
