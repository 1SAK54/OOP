package ru.nsu.vorona;

import java.util.Scanner;

public class BlackjackGame {
    private final Deck deck;
    private final Player player = new Player();
    private final Dealer dealer = new Dealer();
    private int playerScore = 0;
    private int dealerScore = 0;
    private int round = 0;

    public BlackjackGame(int decks) {
        deck = new Deck(decks);
    }

    public void play() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Добро пожаловать в Блэкджек!");
        boolean cont = true;
        while (cont) {
            round++;
            System.out.println("Раунд " + round);
            playRound(sc);
            System.out.println("Счет " + playerScore + ":" + dealerScore);
            System.out.println("Играть ещё? (y/n)");
            String ans = sc.nextLine().trim().toLowerCase();
            cont = ans.equals("y") || ans.equals("д") || ans.equals("yes");
            if (deck.remaining() < 15) {
                System.out.println("Перетасовываю колоду...");
            }
            // очистим руки
            player.getHand().getCards().clear();
            dealer.getHand().getCards().clear();
        }
        sc.close();
        System.out.println("Игра окончена. Финальный счет " + playerScore + ":" + dealerScore);
    }

    private void playRound(Scanner sc) {
        // Раздать: по две карты. У дилера вторая карта — закрытая.
        player.getHand().add(deck.draw());
        dealer.getHand().add(deck.draw());
        player.getHand().add(deck.draw());
        Card hidden = deck.draw();
        dealer.setHidden(hidden);

        System.out.println("Дилер раздал карты");
        System.out.println("Ваши карты: " + player.getHand().toDisplayString(false));
        System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(true));
        // Проверка блекджека
        boolean playerBJ = player.getHand().isBlackjack();
        boolean dealerBJ = dealer.getHand().isBlackjack(); // скрытая учитывается
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

        // Ход игрока
        System.out.println("Ваш ход");
        boolean playerTurnOver = false;
        while (!playerTurnOver) {
            System.out.println("-------");
            System.out.println("Введите \"1\", чтобы взять карту, и \"0\", чтобы остановиться .");
            String in = sc.nextLine().trim();
            if (in.equals("1")) {
                Card c = deck.draw();
                player.getHand().add(c);
                System.out.println("Вы открыли карту " + c.toString() + " (" + c.baseValue() + ")");
                System.out.println("Ваши карты: " + player.getHand().toDisplayString(false));
                System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(true));
                if (player.getHand().isBust()) {
                    System.out.println("Вы перебрали! (" + player.getHand().getBestValue() + ")");
                    System.out.println("Вы проиграли раунд.");
                    dealerScore++;
                    return;
                }
            } else if (in.equals("0")) {
                playerTurnOver = true;
            } else {
                System.out.println("Неверный ввод. Введите 1 или 0.");
            }
        }

        // Ход дилера
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

        // Оба в пределах <=21 -> сравнение
        int p = player.getHand().getBestValue();
        int d = dealer.getHand().getBestValue();
        System.out.println("Результаты: игрок " + p + " vs дилер " + d);
        if (p > d) {
            System.out.println("Вы выиграли раунд!");
            playerScore++;
        } else if (p < d) {
            System.out.println("Вы проиграли раунд.");
            dealerScore++;
        } else {
            System.out.println("Ничья.");
        }
    }

    private void revealDealerHand() {
        if (dealer.hasHidden()) {
            System.out.println("Дилер открывает закрытую карту " + dealer.getHand().getCards().get(1).toString());
            dealer.revealHidden();
        }
        System.out.println("Карты дилера: " + dealer.getHand().toDisplayString(false));
    }

    public static void main(String[] args) {
        int decks = 3;
        if (args.length > 0) {
            try { decks = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        BlackjackGame game = new BlackjackGame(decks);
        game.play();
    }
}
