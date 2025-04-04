package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;
import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import java.util.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int BOARD_LIMIT = 9;
    private static Board board;

    public static void main(String[] args) {
        final Map<String, String> positions = Stream.of(args)
                .filter(arg -> arg.contains(";"))
                .collect(toMap(
                        k -> k.split(";")[0],
                        v -> v.split(";")[1]
                ));

        while (true) {
            System.out.println("Selecione uma das opções a seguir:");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo número");
            System.out.println("3 - Remover um número");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - Limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            int option = runUntilGetValidNumber(1, 8);

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opção inválida, selecione uma das opções do menu");
            }
        }
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado.");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                String positionConfig = positions.get(i + "," + j);
                if (positionConfig != null && positionConfig.contains(",")) {
                    String[] parts = positionConfig.split(",");
                    int expected = Integer.parseInt(parts[0]);
                    boolean fixed = Boolean.parseBoolean(parts[1]);
                    spaces.get(i).add(new Space(expected, fixed));
                } else {
                    spaces.get(i).add(new Space(0, false));
                }
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar.");
    }

    private static void inputNumber() {
        if (checkGameNotStarted()) return;

        System.out.println("Informe a coluna onde o número será inserido:");
        int col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha onde o número será inserido:");
        int row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o número que vai entrar na posição [%d,%d]:%n", col, row);
        int value = runUntilGetValidNumber(1, 9);

        if (!board.changeValue(col, row, value)) {
            System.out.printf("A posição [%d,%d] tem um valor fixo.%n", col, row);
        }
    }

    private static void removeNumber() {
        if (checkGameNotStarted()) return;

        System.out.println("Informe a coluna onde deseja remover o número:");
        int col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha onde deseja remover o número:");
        int row = runUntilGetValidNumber(0, 8);

        if (!board.clearValue(col, row)) {
            System.out.printf("A posição [%d,%d] tem um valor fixo.%n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (checkGameNotStarted()) return;

        Object[] args = new Object[81];
        int argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col : board.getSpaces()) {
                args[argPos++] = " " + (isNull(col.get(i).getActual()) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma:");
        System.out.printf(BOARD_TEMPLATE + "%n", args);
    }

    private static void showGameStatus() {
        if (checkGameNotStarted()) return;

        System.out.printf("O jogo atualmente está no status: %s%n", board.getStatus().getLabel());
        System.out.println(board.hasErrors() ? "O jogo contém erros." : "O jogo não contém erros.");
    }

    private static void clearGame() {
        if (checkGameNotStarted()) return;

        System.out.println("Tem certeza que deseja limpar seu jogo? (sim/não)");
        String confirm = scanner.next();
        if (confirm.equalsIgnoreCase("sim")) {
            board.reset();
            System.out.println("Jogo resetado!");
        }
    }

    private static void finishGame() {
        if (checkGameNotStarted()) return;

        if (board.gameIsFinished()) {
            System.out.println("Parabéns! Você concluiu o jogo.");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contém erros. Verifique seu tabuleiro e corrija-os.");
        } else {
            System.out.println("Você ainda precisa preencher todos os espaços.");
        }
    }

    private static boolean checkGameNotStarted() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return true;
        }
        return false;
    }

    private static int runUntilGetValidNumber(final int min, final int max) {
        while (true) {
            try {
                int current = Integer.parseInt(scanner.next());
                if (current >= min && current <= max) return current;
            } catch (NumberFormatException e) {
                System.out.printf("Entrada inválida! Informe um número entre %d e %d:%n", min, max);
            }
        }
    }
}