package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String mode = "enc";
        String data = null;
        String alg = "shift";

        int key = 0;

        File toRead = null;
        File toWrite = null;

        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-mode":
                    mode = args[i + 1];
                    break;
                case "-key":
                    key = Integer.parseInt(args[i + 1]);
                    break;
                case "-data":
                    data = args[i + 1];
                    break;
                case "-in":
                    toRead = new File(args[i + 1]);
                    break;
                case "-out":
                    toWrite = new File(args[i + 1]);
                    break;
                case "-alg":
                    alg = args[i + 1];
                    break;
            }
        }

        Processing processing;

        if (alg.equals("unicode")) {
            processing = new Unicode(data, mode, key, toRead, toWrite);
        } else {
            processing = new Shift(data, mode, key, toRead, toWrite);
        }

        processing.process();
    }
}

abstract class Processing {

    String result;
    String data;
    String mode;

    int key;

    File toRead;
    File toWrite;

    public Processing(String data, String mode, int key, File toRead, File toWrite) {
        this.data = data;
        this.mode = mode;
        this.key = key;
        this.toRead = toRead;
        this.toWrite = toWrite;
        this.result = "";
    }

    void process() {
        data = getInput(toRead);
        if (mode.equals("enc")) {
            encrypt(key);
        } else {
            decrypt(key);
        }
        setOutput(toWrite);

    }

    String getInput(File toRead) {

        if (toRead != null) {
            try (Scanner reader = new Scanner(toRead)) {
                data = reader.nextLine();
                return data;

            } catch (FileNotFoundException fnfe) {
                System.out.println("Error: Input file not found!");
                System.out.println(toRead.getName());
            }
        }
        return null;
    }

    abstract void encrypt(int key);

    abstract void decrypt(int key);

    static int getTemp(char c) {
        if (c >= 65 && c <= 90) {
            return 65;
        } else if (c >= 97 && c <= 122) {
            return 97;
        }
        return (int) c;
    }

    void setOutput(File toWrite) {
        if (toWrite != null) {
            if (!toWrite.isFile()) {
                try {
                    if (!toWrite.createNewFile())
                        System.out.println("Error!");
                } catch (IOException e) {
                    System.out.println("Error storing output");
                }
            }
            try (FileWriter fw = new FileWriter(toWrite)) {
                fw.write(result);
            } catch (IOException e) {
                System.out.println("Error: Output file not found!");
            }
        } else {
            System.out.println(result);
        }
    }
}

class Unicode extends Processing {
    public Unicode(String data, String mode, int key, File toRead, File toWrite) {
        super(data, mode, key, toRead, toWrite);
    }

    @Override
    void encrypt(int key) {

        for (int i = 0; i < data.length(); i++) {
            this.result = this.result.concat(Character.toString((char) (data.charAt(i) + key)));
        }

    }

    @Override
    void decrypt(int key) {
        for (int i = 0; i < data.length(); i++) {
            result = result.concat(Character.toString((char) (data.charAt(i) - key)));
        }
    }
}

class Shift extends Processing {

    public Shift(String data, String mode, int key, File toRead, File toWrite) {
        super(data, mode, key, toRead, toWrite);
    }

    @Override
    void encrypt(int key) {
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            int temp = getTemp(c);
            char r;
            if (Character.isAlphabetic(c)) {
                r = (char) ((c - temp + key) % 26 + temp);
            } else {
                r = c;
            }
            this.result = this.result.concat(Character.toString(r));
        }
    }

    @Override
    void decrypt(int key) {
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            int temp = getTemp(c);
            char r;
            if (Character.isAlphabetic(c)) {
                System.out.println();
                r = (char) ((c - key - temp) % 26 + temp);
                if (r < 97) {
                    r += 26;
                }
            } else {
                r = c;
            }
            this.result = this.result.concat(Character.toString(r));
        }
    }
}


