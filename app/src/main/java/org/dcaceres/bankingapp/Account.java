package org.dcaceres.bankingapp;

class Account {

    private Card card;
    private int balance;

    public Account(Card card){
        this.card = card;
        this.balance = 0;

    }
    public  Account(Card card, int balance) {
        this.card = card;
        this.balance = balance;
    }

    public Card getCard() {
        return this.card;
    }

    public int getBalance() {
        return this.balance;
    }
}