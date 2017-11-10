package br.edu.opet.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import br.edu.opet.util.Leitor;
import redis.clients.jedis.Jedis;

public class ProjetoBancoNoSQL {

	//definindo os formatadores de data e hora
	private static DateTimeFormatter sFormatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static DateTimeFormatter sFormatadorDataeHora = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss");

	public static void main(String[] args) {
		// Obter a conexão
		Jedis jedis = new Jedis("localhost");
		System.out.println("Connecção com o servidor");
		// Testando se o servidor está execuando
		System.out.println("Servidor está executando: " + jedis.ping());
        
		//definindo a variavel jedis
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
				LocalDate tDataCadastro = LocalDate.now(); //data do cadastro é a data de agora, presente
				String tApelido = Leitor.readString("Apelido:");
				String tNome = Leitor.readString("Nome:");
				if (tNome == "") {//nome não pode ser vazio
					break opcao;
				}

				while (true) {

					if (tApelido == "") {//apelido não pode ser vazio 
						break opcao;
					}
                    //definindo a variavel tApelidoBd1 que irá ter a função de pegar o apelido que está gravado no banco de dados
					String tApelidoBd1 = tJedis.hget("Usuário:" + tApelido, "Apelido");
                    //se já estiver gravado no banco, retorna mensagem de erro 
					if (!(tApelidoBd1 == null)) {
						System.out.println("Este apelido já existe, digite novamente");
					}
                    //gravando os dados que o usuário irá inserir no banco 
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
						menu2: while (true) {//menu depois do login

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
								//definindo a variavel para o usuario digitar para quem irá ser enviada a mensagem
								String tDestinatario = Leitor.readString("Para:");
								// Destinatario não pode ser vazia
								if (tDestinatario == "") {//não pode ser vazio
									break opcao2;
								}
                                //definindo a variavel para o usuario inserir a mensagem
								String tMensagem = Leitor.readString("Mensagem:");

								// a mensagem não pode ser vazia
								if (tMensagem == "") {
									break opcao2;
								}

								// montando a chave e valor, guardando as informações junto com a data e hora
								tJedis.sadd(
										tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":Para",
										tDestinatario);//apelido unico

								tJedis.set(tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora) + ":De",
										tApelidoInformado);

								tJedis.set(tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora)
										+ ":Mensagem", tMensagem);

								// incrementando quantas mensagens foram enviadas 
								Long tSaida = tJedis.incr(tApelidoInformado + "--saida");
                                //um conjunto ordenado do remetente e quantas mensagens foram enviadas 
								tJedis.zadd(tApelidoInformado + "--saida ", tSaida, tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));

								String[] SepararTexto = tDestinatario.split(",");
								//contador para ir incrementando as mensagens recebidas 
								for (int i = 0; i < SepararTexto.length; i++) {
									
									Long tEntrada = tJedis.incr(SepararTexto[i] + "--entr  ");

									//System.out.println(SepararTexto[i] + "-entrada  " + tEntrada + ":"+ tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));
                                    
									//guardando no banco as mensagens recebidas
									tJedis.zadd(SepararTexto[i] + "--entr ", tEntrada, tApelidoInformado + ":" + tDataMensagem.format(sFormatadorDataeHora));
								}

								break opcao2;

							case 2:
								System.out.println("Visualizar mensagens recebidas");

								// zlexcount pois o count normal não está funcionando, conta a quantidade de mensagens recebidas
								//e mostra na tela
								Long contarEntrada = tJedis.zlexcount(tApelidoInformado + "--entr ", "-", "+");
								System.out.println("Você tem " + contarEntrada + " mensagens!");
                                // se o usuario for impopular e nao receber mensagem de ninguem, o programa volta pro segundo menu
								if (contarEntrada == 0) {

									break opcao2;
								} else {
									//mostra as mensagens recebidas em uma lista ordenada
									for (int i = 0; i < contarEntrada; i++) {
										System.out.println(
												(1 + i) + " " + tJedis.zrange(tApelidoInformado + "--entr ", i, i));
									}
									System.out.println();

									// variavel para ver mensagem
									Long tVisualizar = Leitor.readLong("Visualisar mensagem:");
									//verificando se a mensagem existe ou não
									if (tVisualizar == 0) {
										break opcao2;
									} else {
										//mostrando o remetente e quantas mensagens foram enviadas
										Set<String> tVerMensagem = tJedis.zrange(
												tApelidoInformado + "--entr ", tVisualizar - 1, tVisualizar - 1);
                                        // tirando o [ e o ] 
										String cortarString = tVerMensagem.toString();
										String tVerMensagem2 = cortarString.substring(1, cortarString.length() - 1);
                                        // mostrando na tela quem enviou a mensagem e a mensagem
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

											//mostrando a quantidade de respostas para a mensagem
											Long tEntradaResposta = tJedis.incr(tVerMensagem2 + "-resp  ");

											tJedis.zadd(tVerMensagem2 + "--resp  ", tEntradaResposta,
													tApelidoInformado + ":"
															+ tDataMensagem2.format(sFormatadorDataeHora));
											//definindo a variavel que vai dar get na mensagem que o enviaram
											String tDestinatario2 = tJedis.get(tVerMensagem2 + ":De");

											//guardando tudinho no banco
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

											//incrementando as saidas de mensagem
											Long tSaida2 = tJedis.incr(tApelidoInformado + "--saida");
											//guarando no redis um conjunto ordenado de quantas mensagens foram enviadas
											tJedis.zadd(tApelidoInformado + "--saida ", tSaida2, tApelidoInformado + ":"
													+ tDataMensagem2.format(sFormatadorDataeHora));
											
											//incrementando quem vai receber
											Long tEntrada2 = tJedis.incr(tDestinatario2 + "-entr ");

											//guardando um conjunto ordenado de novo das mensagens que chegaram para o usuario
											tJedis.zadd(tDestinatario2 + "--entr ", tEntrada2, tApelidoInformado
													+ ":" + tDataMensagem2.format(sFormatadorDataeHora));

										}
									}
								}

								break opcao2;

							case 3:
								System.out.println("Visualizar mensagens enviadas");

								//contando quantas mensagens foram enviadas e mostrando
								Long contarSaida = tJedis.zlexcount(tApelidoInformado + "--saida ", "-", "+");
								System.out.println("Você enviou " + contarSaida + " mensagens!");

								if (contarSaida == 0) {//ai que preguiça de comentar isso

									break opcao2;
								} else {

									
									for (int i = 0; i < contarSaida; i++) {
                                        //mostra as mensagens enviadas enquanto a condição for verdadeira
										System.out.println(
												(1 + i) + " " + jedis.zrange(tApelidoInformado + "--saida ", i, i));
									}

									opcao3: while (true) {

										System.out.println();
										Long tVerSaida = Leitor.readLong("Visualisar mensagem:");

										// n dá pro usuario digitar 0 ou outro numero menor que zero
										//na hora de visualizar a mensagem
										if (tVerSaida == 0 || tVerSaida < 0) {
											break opcao2;
										} else {
											//definindo a variavel que irá pegar da lista do banco as mensagens que o usuario enviou
											Set<String> tVerMensagem3 = tJedis.zrange(tApelidoInformado + "--saida ",
													tVerSaida - 1, tVerSaida - 1);

											//transforma em string
											String Cortar = (tVerMensagem3).toString();
											// e tira o [ e o ] pra sair da chave
											String tVerMensagem4 = Cortar.substring(1, Cortar.length() - 1);

											//mostra o destino da mensagem, pra que vai ir
											System.out.println("Para: " + tJedis.smembers(tVerMensagem4 + ":Para"));
											//mostra a mensagem enviada
											System.out.println(tJedis.get(tVerMensagem4 + ":Mensagem"));

											//conta as repostas que o usuario enviou e mostra na tela
											Long contarResposta = tJedis.zlexcount(tVerMensagem4 + "--resp ", "-",
													"+");
											System.out.println();
											System.out.println("Você possui " + contarResposta + " respostas.");

											if (contarResposta == 0) {// se for zero volta pro menu
												break opcao3;
											} else {
												System.out.println("Respostas:");

												//pegando da lista as respostas enviadas
												for (int i = 0; i < contarResposta; i++) {
													Set<String> tVerMensagem5 = tJedis
															.zrange(tVerMensagem4 + "--resp ", i, i);
													
													//transformando em string e tirando o [ e ] denovo
													String Cortar2 = (tVerMensagem5.toString());
													String tTirarChave = Cortar2.substring(1, Cortar2.length() - 1);

													// pegando do banco quem enviou a reposta e mostrando o nome de quem é
													String tNome2 = tJedis.get(tTirarChave + ":De");
													System.out.println((1 + i) + "- " + tNome2);
												}
												System.out.println();
												Long tVerResposta = Leitor.readLong("Visualizar mensagem:");
												if (tVerResposta == 0 || tVerResposta < 0) {
													break opcao2;
												} else {
													// pegando da lista
													Set<String> tVerMensagem6 = tJedis.zrange(
															tVerMensagem4 + "--resp  ", tVerResposta - 1,
															tVerResposta - 1);
													
													//transformando em string e tirando o [ e ] denovo
													String Cortar3 = (tVerMensagem6).toString();
													String tTirarChave2 = Cortar3.substring(1, Cortar3.length() - 1);

													// Mostrando a mensagem que foi enviada
													System.out.println(tJedis.get(tTirarChave2 + ":Mensagem"));

													//pede pra sair
													String opf = Leitor.readString("Digite enter para sair");
													if (opf == "")
														break opcao3;
												}
											}
										}
									}
								}
								break opcao2;
  
							//aqui nóis mostra tudo que foi cadastrado
							case 4:
								System.out.println("Visualização dos dados");
								System.out.println();
								System.out.println("Nome: " + tJedis.hget("Usuario:" + tApelidoInformado, "Nome"));
								System.out
										.println("Apelido: " + tJedis.hget("Usuario:" + tApelidoInformado, "Apelido"));
								System.out.println(
										"DataCadastro: " + tJedis.hget("Usuario:" + tApelidoInformado, "DataCadastro"));

								break opcao2;

								//sai do login
							case 5:
								break opcao;

							}
						}
					}
				}

				//sai do programa
			case 3:
				break menu1;
			}
		}

		jedis.close();

	}
}
