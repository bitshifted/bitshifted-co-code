package co.bitshifted.samples;

import java.io.BufferedReader;
import java.io.FileReader;

public class InvalidClass {

    public void Invalid_method(int x) throws Exception {
        System.out.println("This method is invalid");
        BufferedReader reader = new BufferedReader(new FileReader("foo.txt"));
        var line = reader.readLine();
        System.out.println(line);
    }
}
