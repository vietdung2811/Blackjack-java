import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Card{
        String value;
        String type;

        Card(String value, String type){
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value + "-" + type;
        }

        public int getValue(){
            if (value.equals("A")){
                return 11; //default value of Ace is 11
            } else if (value.equals("J") || value.equals("Q") || value.equals("K")){
                return 10; //value of J, Q, K is 10
            } else {
                return Integer.parseInt(value); //value of 2-10 is the number itself
            }
        }

        public boolean isAce(){
            return value.equals("A");
        }
    }

    ArrayList<Card> deck;
    Random random = new Random();
    //First, init all the elements of the game
    //dealer and player
    Card hiddenCard;
    ArrayList<Card> dealerHand;
    ArrayList<Card> playerHand;
    int dealerSum;
    int dealerAceCount;
    int playerSum;
    int playerAceCount;

    //window, this is the graphic part
    int boardWidth = 800;
    int boardHeight = 600;

    //size of the card
    int cardWidth = 110;
    int cardHeight = 154;

    //GUI of the game
    JFrame frame = new JFrame("BlackJack");
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            try{
                //hidden card
                Image hiddenCardImage = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                g.drawImage(hiddenCardImage, 20, 20, cardWidth, cardHeight, null);
                if (!standButton.isEnabled()){
                    String cardName = hiddenCard.value +"-"+ hiddenCard.type + ".png";
                    Image cardImage = new ImageIcon(getClass().getResource("./cards/" + cardName)).getImage();
                    g.drawImage(cardImage, 20, 20, cardWidth, cardHeight, null); 
                }
                
                //dealer card
                for (int i = 0; i<dealerHand.size(); i++){
                    String cardName = dealerHand.get(i).value +"-"+ dealerHand.get(i).type + ".png";
                    Image cardImage = new ImageIcon(getClass().getResource("./cards/" + cardName)).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 20) * (i+1), 20, cardWidth, cardHeight, null); 
                }

                //player card
                for (int i = 0; i<playerHand.size(); i++){
                    String cardName = playerHand.get(i).value +"-"+ playerHand.get(i).type + ".png";
                    Image cardImage = new ImageIcon(getClass().getResource("./cards/" + cardName)).getImage();
                    g.drawImage(cardImage, 20 + (cardWidth + 20) * i, 20 + cardHeight + 20, cardWidth, cardHeight, null); 
                }

                if (!standButton.isEnabled()){
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("STAY: ");
                    System.out.println("Dealer sum: " + dealerSum);
                    System.out.println("Player sum: " + playerSum);

                    String message = "";   
                    if (playerSum>21){
                        message = "Player busts! Dealer wins!";
                    } else if (dealerSum>21){
                        message = "Dealer busts! Player wins!";
                    } else if (playerSum>dealerSum){
                        message = "Player wins!";
                    } else if (playerSum<dealerSum){
                        message = "Dealer wins!";
                    } else {
                        message = "It's a tie!";
                    }

                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.BOLD, 30));
                    g.drawString(message, 220,500);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("HIT");
    JButton standButton = new JButton("STAND");
    JButton newGameButton = new JButton("NEW GAME");

    //constructor
    BlackJack(){
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        standButton.setFocusable(false);
        buttonPanel.add(standButton);
        newGameButton.setFocusable(false);
        buttonPanel.add(newGameButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Card card = deck.remove(deck.size()-1);
                playerSum += card.getValue();
                playerAceCount += card.isAce() ? 1 : 0;
                playerHand.add(card);
                if (reducePlayerAce()>21){
                    hitButton.setEnabled(false);
                }

                standButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        hitButton.setEnabled(false);
                        standButton.setEnabled(false);
                        
                        while (dealerSum<17){
                            Card dealerCard = deck.remove(deck.size()-1);
                            dealerSum += dealerCard.getValue();
                            dealerAceCount += dealerCard.isAce() ? 1 : 0;
                            dealerHand.add(dealerCard);
                        }
                        gamePanel.repaint();
                    }
                });
                gamePanel.repaint();
            }
        });
        newGameButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                startGame();
                hitButton.setEnabled(true);
                standButton.setEnabled(true);
                gamePanel.repaint();
            }
        });
    }

    //main class that runs the game
    public void startGame(){
        buildDeck();
        shuffleDeck();
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCard = deck.remove(deck.size()-1); //remove the last card from the deck
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.value.equals("A") ? 1 : 0;

        Card card = deck.remove(deck.size()-1);   
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);

        System.out.println("DEALER:");
        System.out.println(hiddenCard);
        System.out.println(dealerHand);
        System.out.println("Dealer sum: " + dealerSum);
        System.out.println("Dealer Ace count: " + dealerAceCount);

        //player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        for (int i = 0; i<2; i++){
            Card playerCard = deck.remove(deck.size()-1);
            playerSum += playerCard.getValue();
            playerAceCount += playerCard.isAce() ? 1 : 0;
            playerHand.add(playerCard);
        }

        System.out.println("PLAYER:");
        System.out.println(playerHand);
        System.out.println("Player sum: " + playerSum);
        System.out.println("Player Ace count: " + playerAceCount);
    }

    public int reduceDealerAce(){
        while (dealerSum>21 && dealerAceCount>0){
            dealerSum -= 10;
            dealerAceCount--;
        }
        return dealerSum;
    }

    public int reducePlayerAce(){
        while (playerSum>21 && playerAceCount>0){
            playerSum -= 10;
            playerAceCount--;
        }
        return playerSum;
    }

    public void buildDeck(){
        deck = new ArrayList<Card>();
        String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        String[] types = {"H","D","C","S"};
        for (int i = 0; i<types.length; i++){
            for (int j = 0; j<values.length; j++){
                deck.add(new Card(values[j], types[i]));
            }
        }
        System.out.println("Building deck...");
        System.out.println(deck);
    }

    public void shuffleDeck(){
        for (int i =0; i<deck.size(); i++){
            int j = random.nextInt(deck.size());
            Card currCard = deck.get(i);
            Card randomCard = deck.get(j);
            deck.set(i, randomCard);
            deck.set(j, currCard);
        }

        System.out.println("Shuffling deck...");
        System.out.println(deck);
    }
}
