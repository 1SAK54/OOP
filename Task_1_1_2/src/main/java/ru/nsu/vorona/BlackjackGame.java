package ru.nsu.vorona;

import java.util.Scanner;
import java.util.Set;

/**
 * Класс {@code BlackjackGame} реализует консольную игру "Блэкджек".
 */
public class BlackjackGame {
    private final Deck deck;
    private final Player player = new Player();
    private final Dealer dealer = new Dealer();
    private int playerScore = 0;
    private int dealerScore = 0;
    private int round = 0;
    private static final String TAKE_CARD_KEY = "1";
    private static final String STAND_KEY = "0";

    /**
     * Создаёт новую игру с указанным количеством колод.
     *
     * @param decks количество стандартных колод (52 карты каждая)
     */
    public BlackjackGame(int decks) {
        deck = new Deck(decks);
    }

    /**
     * Основной игровой цикл. Реализует последовательные раунды игры
     * с вводом пользователя из консоли.
     */
    public void play() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Добро пожаловать в Блэкджек!");
        boolean continueGame = true;

        while (continueGame) {
            round++;
            System.out.println("Раунд " + round);
            playRound(sc);
            System.out.println("Счет " + playerScore + ":" + dealerScore);

            // Проверяем, хочет ли игрок продолжать
            continueGame = askToContinue(sc);

            // Проверяем состояние колоды, если игра продолжается
            if (continueGame && deck.remaining() < 15) {
                System.out.println("Перетасовываю колоду...");
            }

            // Очистка рук перед новым раундом
            player.getHand().getCards().clear();
            dealer.getHand().getCards().clear();
        }

        sc.close();
        System.out.println("Игра окончена. Финальный счет " + playerScore + ":" + dealerScore);
    }

    private static final Set<String> YES_ANSWERS = Set.of("y", "д", "yes");

    /**
     * Запрашивает у пользователя, хочет ли он сыграть ещё один раунд.
     *
     * @param sc объект {@link Scanner} для чтения ввода
     * @return true, если пользователь выбрал "да"
     */
    private boolean askToContinue(Scanner sc) {
        System.out.println("Играть ещё? (y/n)");
        String ans = sc.nextLine().trim().toLowerCase();
        return YES_ANSWERS.contains(ans);
    }

    /**
     * Один игровой раунд: раздача карт, ход игрока, ход дилера, сравнение результатов.
     *
     * @param sc объект {@link Scanner} для чтения ввода игрока
     */
    private void playRound(Scanner sc) {
        player.getHand().add(deck.draw());
        dealer.getHand().add(deck.draw());
        player.getHand().add(deck.draw());
        Card hidden = deck.draw();
        dealer.setHidden(hidden);

        System.out.println("Дилер раздал карты");
        System.out.println("Ваши карты: " + player.getHand().toDisplayString(false));
        System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(true));

        boolean playerBJ = player.getHand().isBlackjack();
        boolean dealerBJ = dealer.getHand().isBlackjack();
        if (playerBJ || dealerBJ) {
            revealDealerHand();
            if (playerBJ && !dealerBJ) {
                System.out.println("У вас Блэкджек! Вы выиграли раунд!");
                playerScore++;
            } else if (!playerBJ && dealerBJ) {
                System.out.println("У дилера Блэкджек. Вы проиграли раунд.");
                dealerScore++;
            } else {
                System.out.println("Оба получили блэкджек. Ничья.");
            }
            return;
        }

        System.out.println("Ваш ход");
        boolean playerTurnOver = false;
        while (!playerTurnOver) {
            System.out.println("-------");
            System.out.println("Введите \"1\", чтобы взять карту, и \"0\", чтобы остановиться.");
            String in = sc.nextLine().trim();
            if (in.equals(TAKE_CARD_KEY)) {
                takeCard(player, deck, true);
                if (player.getHand().isBust()) {
                    System.out.println("Вы перебрали! (" + player.getHand().getBestValue() + ")");
                    System.out.println("Вы проиграли раунд.");
                    dealerScore++;
                    return;
                }
            } else if (in.equals(STAND_KEY)) {
                playerTurnOver = true;
            } else {
                System.out.println("Неверный ввод. Введите 1 или 0.");
            }
        }

        System.out.println("Ход дилера");
        System.out.println("-------");
        revealDealerHand();

        while (dealer.getHand().getBestValue() < 17) {
            Card c = deck.draw();
            dealer.getHand().add(c);
            System.out.println("Дилер открывает карту " + c.toString() + " (" + c.baseValue() + ")");
            System.out.println("Ваши карты: " + player.getHand().toDisplayString(false));
            System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(false));
            if (dealer.getHand().isBust()) {
                System.out.println("Дилер перебрал! Вы выиграли раунд!");
                playerScore++;
                return;
            }
        }

        int playerScoreValue = player.getHand().getBestValue();
        int dealerScoreValue = dealer.getHand().getBestValue();
        System.out.println("Результаты: игрок " + playerScoreValue + " vs дилер " + dealerScoreValue);
        if (playerScoreValue > dealerScoreValue) {
            System.out.println("Вы выиграли раунд!");
            playerScore++;
        } else if (playerScoreValue < dealerScoreValue) {
            System.out.println("Вы проиграли раунд.");
            dealerScore++;
        } else {
            System.out.println("Ничья.");
        }
    }

    /**
     * Добавляет новую карту игроку и выводит обновлённое состояние руки.
     *
     * @param player игрок, которому добавляется карта
     * @param deck колода, из которой берётся карта
     * @param hideDealerCards если true, скрывает вторую карту дилера
     */
    private void takeCard(Player player, Deck deck, boolean hideDealerCards) {
        Card c = deck.draw();
        player.getHand().add(c);
        System.out.println("Вы открыли карту " + c + " (" + c.baseValue() + ")");
        System.out.println("Ваши карты: " + this.player.getHand().toDisplayString(false));
        System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(hideDealerCards));
    }

    /**
     * Открывает закрытую карту дилера и выводит его руку в консоль.
     */
    private void revealDealerHand() {
        if (dealer.hasHidden()) {
            System.out.println("Дилер открывает закрытую карту " +
                    dealer.getHand().getCards().get(1).toString());
            dealer.revealHidden();
        }
        System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(false));
    }

    /**
     * Точка входа. Запускает игру.
     *
     * @param args первый аргумент может задавать количество колод (по умолчанию 3)
     */
    public static void main(String[] args) {
        int decks = 3;
        if (args.length > 0) {
            try {
                decks = Integer.parseInt(args[0]);
            } catch (Exception ignored) {}
        }
        BlackjackGame game = new BlackjackGame(decks);
        game.play();
    }

    /**
     * Упрощённая версия игры для тестов — имитирует поведение раунда без пользовательского ввода.
     */
    public void playRoundForTest() {
        round++;

        player.getHand().add(deck.draw());
        dealer.getHand().add(deck.draw());
        player.getHand().add(deck.draw());
        Card hidden = deck.draw();
        dealer.setHidden(hidden);

        boolean playerBJ = player.getHand().isBlackjack();
        boolean dealerBJ = dealer.getHand().isBlackjack();

        if (playerBJ && !dealerBJ) {
            playerScore++;
            return;
        } else if (!playerBJ && dealerBJ) {
            dealerScore++;
            return;
        } else if (playerBJ && dealerBJ) {
            return;
        }

        while (dealer.getHand().getBestValue() < 17) {
            dealer.getHand().add(deck.draw());
            if (dealer.getHand().isBust()) {
                playerScore++;
                return;
            }
        }

        int p = player.getHand().getBestValue();
        int d = dealer.getHand().getBestValue();
        if ((p > d && p <= 21) || d > 21) {
            playerScore++;
        } else if ((d > p && d <= 21) || p > 21) {
            dealerScore++;
        }
    }


    /**
     * Конструктор для тестов — принимает заранее подготовленную колоду.
     *
     * @param deck объект {@link Deck}, который будет использоваться в игре
     */
    BlackjackGame(Deck deck) {
        this.deck = deck;
    }

    /**
     * Возвращает текущее количество очков игрока.
     *
     * @return количество очков игрока
     */
    public int getPlayerHandValue() {
        return player.getHand().getBestValue();
    }

    /**
     * Возвращает текущее количество очков дилера.
     *
     * @return количество очков дилера
     */
    public int getDealerHandValue() {
        return dealer.getHand().getBestValue();
    }

    /**
     * Возвращает количество выигранных раундов игроком.
     *
     * @return количество побед игрока
     */
    public int getPlayerScore() {
        return playerScore;
    }

    /**
     * Возвращает количество выигранных раундов дилером.
     *
     * @return количество побед дилера
     */
    public int getDealerScore() {
        return dealerScore;
    }
}
