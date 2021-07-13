package org.dcaceres.bankingapp;
import java.util.Random;
class Card {
    private int BIN = 400000;
    private int identifier;
    private int checksum;
    private int pin;
    private int controlNumber;
    private String cardNumber;

    public Card() {
        this.identifier = new Random().nextInt(999_999_999 - 100_000_000 + 1) + 100_000_000;
        this.controlNumber = getControlNumber();
        this.checksum = controlNumber % 10 != 0 ? (10 - controlNumber % 10) : 0;
        this.pin = new Random().nextInt(9999 - 1000 + 1) + 1000;
        this.cardNumber = this.BIN + "" + this.identifier + "" + this.checksum;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
    public  void setBIN(int BIN) { this.BIN = BIN;}

    public Card(String cardNumber, String pin) {
        this.cardNumber = cardNumber;
        this.pin = Integer.parseInt(pin);
    }

    public Card(String cardNumber) {
        this.cardNumber = cardNumber;

    }

    public String getCardNumber() {
        return this.cardNumber;
    }


    public int getPin() {
        return this.pin;
    }

    private int getControlNumber() {
        int[] digitsArr = getDigits();
        doubleOddDigits(digitsArr);
        subtractNine(digitsArr);
        return addAllNumbers(digitsArr);
    }

    public boolean passLuhnAlgorithm() {
        boolean pass = false;
        String cardNumber = this.getCardNumber();
        //System.out.println("[+] Checking Luhn algo for card: " + cardNumber);
        int controlNumber = getControlNumber();
        //System.out.println("[+] Control numb is : " + controlNumber);
        int lastDigit = Integer.parseInt(cardNumber.charAt(cardNumber.length() - 1) + "");
        //System.out.println("[+] Last digit is : " + lastDigit);
        //System.out.println("[+] control numb + last digit % 10 == 0?: " + ((controlNumber + lastDigit) % 10 == 0));
        if ((controlNumber + lastDigit) % 10 == 0) {
            pass = true;
        }
        return pass;
    }

    private int addAllNumbers(int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        return sum;
    }

    private void subtractNine(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] -= arr[i] > 9 ? 9 : 0;
        }
    }

    private void doubleOddDigits(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] *= i % 2 == 0 ? 2 : 1;
        }
    }

    private int[] getDigits() {
        String strPreCardNum = this.BIN + "" + this.identifier;
        System.out.println("strPreCardNum = " + strPreCardNum);
        long longPreCardNum = Long.parseLong(strPreCardNum);
        int[] arrDig = new int[15];
        for (int i = 0; i < strPreCardNum.length(); i++) {
            if (longPreCardNum != 0) {
                long currentDig =  longPreCardNum % 10;
                arrDig[i] = (int) currentDig;
                longPreCardNum /= 10;
            } else {
                break;
            }
        }
        return  arrDig;
    }
}
