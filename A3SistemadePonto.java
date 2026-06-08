import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;

public class A3SistemadePonto {

    static Scanner sc = new Scanner(System.in);

    static String[] nomes = new String[0];
    static String[] rgs = new String[0];
    static int[] profissaoIndex = new int[0];
    static double[] saldos = new double[0];
    static double[] entradas = new double[0];
    static double[] saidaAlmoco = new double[0];
    static double[] retornoAlmoco = new double[0];
    static boolean[] ativos = new boolean[0];
    static boolean[] emAlmoco = new boolean[0];
    static int[] diasTrabalhados = new int[0];

    static int totalFuncionarios = 0;

    static String[] profissoes = {
            "Engenheiro Civil",
            "Mestre de Obra",
            "Pedreiro",
            "Pintor",
            "Eletricista",
            "Encanador",
            "Armador",
            "Ajudante"
    };

    static double[] diarias = {
            600, 400, 290, 290, 290, 290, 290, 145
    };

    static String[] produtos = {
            "Marmita Simples",
            "Marmita Executiva",
            "Cafe Completo",
            "Cafe Puro",
            "Passagem",
            "Refrigerante",
            "Cigarro Picado",
            "Cigarro Maco"
    };

    static double[] precos = {
            15.0, 28.5, 6.0, 2.5, 6.0, 4.0, 1.5, 10.0
    };

    public static void main(String[] args) {

        int opcao;

        do {

            menu();

            opcao = lerInt("Escolha: ");

            switch (opcao) {

                case 1:
                    registrarEntrada();
                    break;

                case 2:
                    abrirLoja();
                    break;

                case 3:
                    registrarSaida();
                    break;

                case 4:
                    registrarAlmoco();
                    break;

                case 5:
                    listarFuncionarios();
                    break;

                case 0:
                    System.out.println("Sistema encerrado!");
                    break;

                default:
                    System.out.println("Opcao invalida!");
            }

        } while (opcao != 0);
    }

    // ========================= MENU =========================

    static void menu() {

        linha();

        System.out.println("======= SISTEMA DE PONTO =======");

        linha();

        System.out.println("1 - Entrada");
        System.out.println("2 - Loja");
        System.out.println("3 - Saida");
        System.out.println("4 - Almoco");
        System.out.println("5 - Listar Funcionarios");
        System.out.println("0 - Encerrar");

        linha();
    }

    // ========================= ENTRADA =========================

    static void registrarEntrada() {

        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.print("RG: ");
        String rg = sc.nextLine();

        if (!confirmarBiometria()) {
            return;
        }

        int funcionario = buscarPorRG(rg);

        if (funcionario == -1) {

            cadastrarFuncionario(nome, rg);

            funcionario = totalFuncionarios - 1;
        }

        if (ativos[funcionario]) {

            System.out.println("Funcionario ja esta trabalhando!");
            return;
        }

        entradas[funcionario] = lerHora("Hora entrada: ");

        ativos[funcionario] = true;

        mostrarData();

        aplicarBeneficios(funcionario);

        System.out.println("Entrada registrada!");
    }

    // ========================= LOJA =========================

    static void abrirLoja() {

        int funcionario = selecionarFuncionario();

        if (funcionario == -1) {
            return;
        }

        if (!confirmarBiometria()) {
            return;
        }

        int escolha;

        do {

            linha();

            System.out.printf("Saldo: R$ %.2f\n", saldos[funcionario]);

            for (int i = 0; i < produtos.length; i++) {

                System.out.printf(
                        "%d - %s | R$ %.2f\n",
                        i + 1,
                        produtos[i],
                        precos[i]
                );
            }

            System.out.println("0 - Voltar");

            escolha = lerInt("Produto: ");

            if (escolha > 0 && escolha <= produtos.length) {

                int qtd = lerInt("Quantidade: ");

                double total = precos[escolha - 1] * qtd;

                if (saldos[funcionario] >= total) {

                    saldos[funcionario] -= total;

                    System.out.printf(
                            "Compra realizada: R$ %.2f\n",
                            total
                    );

                } else {

                    System.out.println("Saldo insuficiente!");
                }
            }

        } while (escolha != 0);
    }

    // ========================= SAIDA =========================

    static void registrarSaida() {

        int funcionario = selecionarFuncionario();

        if (funcionario == -1) {
            return;
        }

        if (!confirmarBiometria()) {
            return;
        }

        if (!ativos[funcionario]) {

            System.out.println("Funcionario nao esta ativo!");
            return;
        }

        double saida = lerHora("Hora saida: ");

        double horas = calcularHoras(funcionario, saida);

        if (horas <= 0) {

            System.out.println("Horario invalido!");
            return;
        }

        double pagamento = calcularPagamento(funcionario, horas);

        saldos[funcionario] += pagamento;

        ativos[funcionario] = false;

        diasTrabalhados[funcionario]++;

        System.out.printf("Horas trabalhadas: %.2f\n", horas);
        System.out.printf("Pagamento: R$ %.2f\n", pagamento);
        System.out.printf("Saldo atual: R$ %.2f\n", saldos[funcionario]);
    }

    // ========================= ALMOCO =========================

    static void registrarAlmoco() {

        int funcionario = selecionarFuncionario();

        if (funcionario == -1) {
            return;
        }

        if (!confirmarBiometria()) {
            return;
        }

        if (!emAlmoco[funcionario]) {

            saidaAlmoco[funcionario] =
                    lerHora("Saida almoco: ");

            emAlmoco[funcionario] = true;

            System.out.println("Almoco iniciado!");

        } else {

            retornoAlmoco[funcionario] =
                    lerHora("Retorno almoco: ");

            double tempo =
                    retornoAlmoco[funcionario]
                            - saidaAlmoco[funcionario];

            if (tempo < 1) {

                System.out.println("Minimo 1 hora!");
                return;
            }

            emAlmoco[funcionario] = false;

            System.out.println("Retorno registrado!");
        }
    }

    // ========================= LISTAR =========================

    static void listarFuncionarios() {

        if (totalFuncionarios == 0) {

            System.out.println("Nenhum funcionario!");
            return;
        }

        for (int i = 0; i < totalFuncionarios; i++) {

            linha();

            System.out.println("Nome: " + nomes[i]);

            System.out.println("RG: " + rgs[i]);

            System.out.println(
                    "Profissao: "
                            + profissoes[profissaoIndex[i]]
            );

            System.out.println(
                    "Dias trabalhados: "
                            + diasTrabalhados[i]
            );

            System.out.printf(
                    "Saldo: R$ %.2f\n",
                    saldos[i]
            );

            System.out.println(
                    "Status: "
                            + (ativos[i]
                            ? "Trabalhando"
                            : "Fora da obra")
            );
        }
    }

    // ========================= CADASTRO =========================

    static void cadastrarFuncionario(String nome, String rg) {

        nomes = Arrays.copyOf(nomes, totalFuncionarios + 1);
        rgs = Arrays.copyOf(rgs, totalFuncionarios + 1);
        profissaoIndex =
                Arrays.copyOf(
                        profissaoIndex,
                        totalFuncionarios + 1
                );

        saldos =
                Arrays.copyOf(
                        saldos,
                        totalFuncionarios + 1
                );

        entradas =
                Arrays.copyOf(
                        entradas,
                        totalFuncionarios + 1
                );

        saidaAlmoco =
                Arrays.copyOf(
                        saidaAlmoco,
                        totalFuncionarios + 1
                );

        retornoAlmoco =
                Arrays.copyOf(
                        retornoAlmoco,
                        totalFuncionarios + 1
                );

        ativos =
                Arrays.copyOf(
                        ativos,
                        totalFuncionarios + 1
                );

        emAlmoco =
                Arrays.copyOf(
                        emAlmoco,
                        totalFuncionarios + 1
                );

        diasTrabalhados =
                Arrays.copyOf(
                        diasTrabalhados,
                        totalFuncionarios + 1
                );

        nomes[totalFuncionarios] = nome;
        rgs[totalFuncionarios] = rg;

        profissaoIndex[totalFuncionarios] =
                escolherProfissao();

        totalFuncionarios++;
    }

    // ========================= CALCULOS =========================

    static double calcularHoras(int funcionario, double saida) {

        if (retornoAlmoco[funcionario] > 0) {

            double manha =
                    saidaAlmoco[funcionario]
                            - entradas[funcionario];

            double tarde =
                    saida
                            - retornoAlmoco[funcionario];

            return manha + tarde;
        }

        return saida - entradas[funcionario];
    }

    static double calcularPagamento(
            int funcionario,
            double horas
    ) {

        double valorHora =
                diarias[profissaoIndex[funcionario]] / 8;

        if (horas <= 8) {

            return valorHora * horas;
        }

        double normal = valorHora * 8;

        double extra =
                (horas - 8)
                        * (valorHora * 1.2);

        return normal + extra;
    }

    // ========================= BENEFICIOS =========================

    static void aplicarBeneficios(int funcionario) {

        LocalDate hoje = LocalDate.now();

        if (hoje.getDayOfWeek() == DayOfWeek.MONDAY) {

            saldos[funcionario] += 160;

            System.out.println(
                    "Beneficios semanais adicionados!"
            );
        }
    }

    // ========================= AUXILIARES =========================

    static void mostrarData() {

        LocalDate hoje = LocalDate.now();

        String dia =
                hoje.getDayOfWeek().getDisplayName(
                        TextStyle.FULL,
                        new Locale("pt", "BR")
                );

        System.out.println(
                "Data: "
                        + hoje.getDayOfMonth()
                        + "/"
                        + hoje.getMonthValue()
                        + "/"
                        + hoje.getYear()
        );

        System.out.println("Dia: " + dia);
    }

    static boolean confirmarBiometria() {

        System.out.print("Biometria confirmada? (s/n): ");

        String bio = sc.nextLine();

        if (!bio.equalsIgnoreCase("s")) {

            System.out.println("Biometria nao confirmada!");
            return false;
        }

        return true;
    }

    static int buscarPorRG(String rg) {

        for (int i = 0; i < totalFuncionarios; i++) {

            if (rgs[i].equals(rg)) {
                return i;
            }
        }

        return -1;
    }

    static int selecionarFuncionario() {

        if (totalFuncionarios == 0) {

            System.out.println("Nenhum funcionario!");
            return -1;
        }

        for (int i = 0; i < totalFuncionarios; i++) {

            System.out.println(i + " - " + nomes[i]);
        }

        return lerInt("Funcionario: ");
    }

    static int escolherProfissao() {

        for (int i = 0; i < profissoes.length; i++) {

            System.out.println(
                    (i + 1)
                            + " - "
                            + profissoes[i]
            );
        }

        return lerInt("Profissao: ") - 1;
    }

    static int lerInt(String msg) {

        while (true) {

            try {

                System.out.print(msg);

                return Integer.parseInt(sc.nextLine());

            } catch (Exception e) {

                System.out.println("Numero invalido!");
            }
        }
    }

    static double lerHora(String msg) {

        while (true) {

            try {

                System.out.print(msg);

                double hora =
                        Double.parseDouble(
                                sc.nextLine()
                        );

                if (hora >= 0 && hora <= 24) {

                    return hora;
                }

                System.out.println("Hora invalida!");

            } catch (Exception e) {

                System.out.println("Numero invalido!");
            }
        }
    }

    static void linha() {

        System.out.println(
                "====================================="
        );
    }
}

